function hidedivLokacija() {
    document.getElementById("error-message").style.visibility = "hidden";
    document.location.href = '/lokacija/all';
}
setTimeout("hidedivLokacija()", 1200);
