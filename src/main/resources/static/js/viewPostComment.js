function toggleEditMode() {
        console.log("button Clicked");
        var editFormContainer = document.getElementById('editFormContainer');
        var editButton = document.getElementById('editButton');

        if (editFormContainer.style.display === 'none') {
            editFormContainer.style.display = 'block';
            editButton.innerText = 'Cancel';
        }
        else {
            editFormContainer.style.display = 'none';
            editButton.innerText = 'Edit';
        }
    }
    //<script src="https://momentjs.com/downloads/moment.js"></script>
        function formatTimeAgo(lastUpdated) {
            var now = moment();
            var updated = moment(lastUpdated);
            return updated.fromNow();
        }