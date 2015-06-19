package com.onsite.chic;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Handle incoming HTTP requests and process them.
 *
 * @author Mike Virata-Stone
 */
public class Server {
    private static final int DEFAULT_PORT = 42999;
    private static final int DEFAULT_MAX_THREADS = 4;
    private static final String DEFAULT_BIND_ADDRESS = "127.0.0.1";

    private Chic chic;
    private Router router = new Router();
    private ServerSocket server;
    private int port;
    private int maxThreads;
    private String bindAddress;
    private ExecutorService executor;
    private boolean run = true;

    public Server(Chic chic) {
        this(chic, DEFAULT_PORT, DEFAULT_MAX_THREADS, DEFAULT_BIND_ADDRESS);
    }

    public Server(Chic chic, int port, int maxThreads, String bindAddress) {
        this.chic = chic;
        setPort(port);
        setMaxThreads(maxThreads);
        setBindAddress(bindAddress);
    }

    public Chic getChic() {
        return chic;
    }

    public Router getRouter() {
        return router;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setMaxThreads(int maxThreads) {
        if (maxThreads < 2) {
            throw new IllegalArgumentException("You must have at least 2 threads!");
        }

        this.maxThreads = maxThreads;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setBindAddress(String bindAddress) {
        this.bindAddress = bindAddress;
    }

    public InetAddress getBindAddress() throws IOException {
        return InetAddress.getByName(bindAddress);
    }

    public void start() throws IOException {
        if (server != null) {
            throw new IllegalStateException("Cannot start a started Server!");
        }

        setupThreadPool();
        startServer();
    }

    private void setupThreadPool() {
        this.executor = Executors.newFixedThreadPool(getMaxThreads(), new ThreadFactory() {
            private int nextNumber = 0;

            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);

                synchronized (this) {
                    thread.setName("chic-http-thread-" + (++nextNumber));
                }

                thread.setDaemon(true);
                return thread;
            }
        });
    }

    private void startServer() throws IOException {
        server = new ServerSocket(getPort(), getMaxThreads(), getBindAddress());

        executor.submit(new Runnable() {
            @Override
            public void run() {
                while (run) {
                    try {
                        final Request request = new Request(Server.this, server.accept());

                        executor.submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    request.process();
                                } catch (Exception e) {
                                    System.err.println("Error while trying to process chic request: " + e);
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        // Don't log socket exceptions when shutting down
                        if (run || !(e instanceof SocketException)) {
                            System.err.println("Error while trying to accept chic request: " + e);
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void shutdown() throws IOException {
        run = false;

        try {
            executor.shutdown();
        } finally {
            server.close();
        }
    }
}
