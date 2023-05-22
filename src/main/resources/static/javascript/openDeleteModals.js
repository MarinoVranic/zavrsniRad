function openDeleteModal(button) {
    var id = button.getAttribute("data-id");
    var naziv = button.getAttribute("data-naziv");
    console.log(id);
    console.log(naziv);
    // Set the ID value in the modal form
    document.getElementById("deleteId").value = id;
    document.getElementById("nameEdit").value = naziv;

    // Open the modal
    document.getElementById('deleteModal').style.display = 'block';
}