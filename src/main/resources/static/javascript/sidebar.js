function w3_open() {
    document.getElementById("mySidebar").style.display = "block";
    document.getElementById("myOverlay").style.display = "block";
    document.addEventListener("click", closeSidebarOutside);
}

function w3_close() {
    document.getElementById("mySidebar").style.display = "none";
    document.getElementById("myOverlay").style.display = "none";
    document.removeEventListener("click", closeSidebarOutside);
}

function closeSidebarOutside(event) {
    var sidebar = document.getElementById("mySidebar");
    if (!sidebar.contains(event.target) && event.target !== document.getElementById("openNav")) {
        w3_close();
    }
}