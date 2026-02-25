// Save scroll position before form submission
document.querySelectorAll('form').forEach(form => {
    form.addEventListener('submit', function () {
        localStorage.setItem('scrollPosition', window.scrollY);
    });
});

// Restore scroll position after page load
window.addEventListener('load', function () {
    const scrollPosition = localStorage.getItem('scrollPosition');
    if (scrollPosition) {
        window.scrollTo(0, parseInt(scrollPosition)); // Scroll to the saved position
        localStorage.removeItem('scrollPosition'); // Remove it after use
    }
});

//budget lines
$(document).ready(function () {
    fetchBudgetLines();
    const csrfToken = $('meta[name="_csrf"]').attr('content');
    const csrfHeader = $('meta[name="_csrf_header"]').attr('content');
    // Event delegation for delete buttons
    $('#profits-table, #losses-table').on('click', '.delete-budget-line-link', function () {
        var budgetId = $(this).data('id');
        deleteBudgetItem(budgetId, csrfToken, csrfHeader);
    });
});


function fetchBudgetLines() {
    $.ajax({
        url: '/currentMonthBudgets',
        type: 'GET',
        success: function (data) {
            var profitsTableBody = $('#profits-table tbody');
            var lossesTableBody = $('#losses-table tbody');

            // Clear existing table rows
            profitsTableBody.empty();
            lossesTableBody.empty();

            data.forEach(function (budget) {
                var dateAdded = new Date(budget.date).toLocaleDateString();

                var optionsMenu = `
                    <div class="delete-budget-line">
                       <button class="delete-budget-line-link" data-id="${budget.id}">
                           <i class="fas fa-trash sidebar__menu-item-icon-delete"></i>
                       </button>
                    </div>
                `;

                var rowHtml = `
                    <tr>
                        <td>${budget.description}</td>
                        <td>$${budget.amount.toFixed(2)}</td>
                        <td>${dateAdded}</td>
                        <td>${optionsMenu}</td>
                    </tr>
                `;

                if (budget.type === 'profit') {
                    profitsTableBody.append(rowHtml);
                } else if (budget.type === 'loss') {
                    lossesTableBody.append(rowHtml);
                }
            });
        },
        error: function (error) {
            console.log("Error fetching budget lines:", error);
        }
    });
}

function deleteBudgetItem(id, csrfToken, csrfHeader) {

    console.log('Retrieved CSRF Token from Meta Tag:', csrfToken);

    if (confirm("Are you sure you want to delete this budget item?")) {
        $.ajax({
            url: `/api/deleteBudgetItem/${id}`,
            type: 'POST', // Or 'DELETE' if you switch the controller
            headers: {
                [csrfHeader]: csrfToken
            },
            success: function (response) {
                alert(response);
                fetchBudgetLines();
            },
            error: function (error) {
                console.log("Error deleting budget item:", error);
                alert("An error occurred while trying to delete the budget item.");
            }
        });
    }
}


//Adding new category

function toggleNewCategoryInput(selectElement, formType) {
    var newCategoryGroup = document.getElementById('new-category-group-' + formType);
    var newCategoryInput = document.getElementById('new-category-' + formType);

    if (selectElement.value === 'Other') {
        newCategoryGroup.style.display = 'flex';
    } else {
        newCategoryGroup.style.display = 'none';
        newCategoryInput.value = '';
    }
}

// Handle form submission for both add-profit and add-expense forms
document.getElementById('add-profit').addEventListener('submit', function(e) {
    handleCategorySubmission('profit');
});

document.getElementById('add-loss').addEventListener('submit', function(e) {
    handleCategorySubmission('loss');
});

function handleCategorySubmission(formType) {
    var categorySelect = document.getElementById(formType + '-category');
    var newCategoryInput = document.getElementById('new-category-' + formType);

    // If 'Other' is selected and the new category input is filled
    if (categorySelect.value === 'Other' && newCategoryInput.value.trim() !== '') {
        var newCategoryValue = newCategoryInput.value.trim().replace(/\s+/g, '_'); // Replace spaces with underscores

        // Create a new option element with the user's input
        var newOption = document.createElement('option');
        newOption.value = newCategoryValue;
        newOption.text = newCategoryValue;

        // Add the new option to the select dropdown
        categorySelect.add(newOption);

        // Set the new option as the selected value
        categorySelect.value = newCategoryValue;
    }
}







