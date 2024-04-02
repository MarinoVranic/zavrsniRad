function hidedivRazduzenje() {
    document.getElementById("error-message").style.visibility = "hidden";
    document.location.href = '/razduzenje/all';
}
setTimeout("hidedivRazduzenje()", 1200);