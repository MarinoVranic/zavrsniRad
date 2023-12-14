function hidedivKorisnik() {
    document.getElementById("error-message").style.visibility = "hidden";
    document.location.href = '/korisnik/all';
}
setTimeout("hidedivKorisnik()", 1200);
