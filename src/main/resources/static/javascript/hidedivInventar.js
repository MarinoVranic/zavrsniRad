function hidedivInventar() {
    document.getElementById("error-message").style.visibility = "hidden";
    document.location.href = '/inventar/all';
}
setTimeout("hidedivInventar()", 1200);