package com.onsite.chic;

import java.io.IOException;
import java.net.InetSocketAddress;
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
    private Chic chic;
    private ServerSocket server;
    private int port;
    private ExecutorService executor;
    private boolean run = true;

    public Server(Chic chic) {
        this(chic, 42999);
    }

    public Server(Chic chic, int port) {
        this.chic = chic;
        this.port = port;

        this.executor = Executors.newFixedThreadPool(4, new ThreadFactory() {
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

    public void start() throws IOException {
        if (server != null) {
            throw new IllegalStateException("Cannot start a started Server!");
        }

        server = new ServerSocket();
        server.bind(new InetSocketAddress("localhost", port));

        executor.submit(new Runnable() {
            @Override
            public void run() {
                while (run) {
                    try {
                        final Request request = new Request(chic, Server.this, server.accept());

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
