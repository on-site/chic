document.addEventListener("DOMContentLoaded", function() {
    var elements = document.getElementsByClassName("sorted");

    for (var i = 0; i < elements.length; i++) {
        new Tablesort(elements[i]);
    }
});
