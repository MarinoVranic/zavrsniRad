function hidedivInventura() {
    document.getElementById("error-message").style.visibility = "hidden";
    document.location.href = '/inventura/all';
}
setTimeout("hidedivInventura()", 1200);