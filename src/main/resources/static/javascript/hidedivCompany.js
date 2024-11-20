function hidedivCompany() {
    document.getElementById("error-message").style.visibility = "hidden";
    document.location.href = '/company/all';
}
setTimeout("hidedivCompany()", 1200);