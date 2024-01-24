function hidedivUser() {
    document.getElementById("error-message").style.visibility = "hidden";
    document.location.href = '/appUsers/all';
}
setTimeout("hidedivUser()", 1200);
