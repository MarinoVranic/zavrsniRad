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

function resetPasswordDropFunc(id, iconId) {
    var x = document.getElementById(id);
    const icon = document.getElementById(iconId);
    if (x.className.indexOf("w3-show") == -1) {
        x.className += " w3-show";
        x.previousElementSibling.className += " w3-gray";
    } else {
        x.className = x.className.replace(" w3-show", "");
        x.previousElementSibling.className =
            x.previousElementSibling.className.replace(" w3-gray", "");
    }
    icon.classList.toggle('rotate');
}