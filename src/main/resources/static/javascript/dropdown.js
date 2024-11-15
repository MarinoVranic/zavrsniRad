function dropdown(id, iconId) {
    var x = document.getElementById(id);
    const icon = document.getElementById(iconId);

    if (x.className.indexOf("w3-show") == -1) {
        x.className += " w3-show";
    } else {
        x.className = x.className.replace(" w3-show", "");
    }

    icon.classList.toggle('rotate');
}