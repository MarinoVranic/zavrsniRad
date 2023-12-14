function hidedivDobavljac() {
    document.getElementById("error-message").style.visibility = "hidden";
    document.location.href = '/dobavljac/all';
}
setTimeout("hidedivDobavljac()", 1200);