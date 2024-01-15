function validateForm() {
    var selectedOption = document.getElementById("idInventure").value;
    if (selectedOption === "") {
        alert("Odaberite godinu inventure!");
        return false;
    }
    return true;
}

function generateReport() {
    var selectedOption1 = document.getElementById("idInventure").value;
    var selectedOption2 = document.getElementById("tipInventara").value;
    if (selectedOption1 !== "" && selectedOption2 !== "") {
        var form = document.getElementById("form2");
        form.action = "/provodenjeInventure/generatePDF";
        form.submit();
    }

}
function generateCSV() {
    var selectedOption = document.getElementById("idInventure").value;
    if (selectedOption !== "") {
        var form = document.getElementById("form2");
        form.action = "/provodenjeInventure/generateCSV";
        form.submit();
    }
}