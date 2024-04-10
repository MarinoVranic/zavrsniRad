function hidedivFile() {
    document.getElementById("error-message").style.visibility = "hidden";
    document.location.href = '/file/all';
}
setTimeout("hidedivFile()", 1200);