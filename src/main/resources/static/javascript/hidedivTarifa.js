function hidedivTarifa() {
    document.getElementById("error-message").style.visibility = "hidden";
    document.location.href = '/mobilnaTarifa/all';
}
setTimeout("hidedivTarifa()", 1200);