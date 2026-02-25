$(document).ready(function() {
    // Set current month and year in the form inputs
    let currentDate = new Date();
    let currentMonth = currentDate.getMonth() + 1;
    let currentYear = currentDate.getFullYear();
    $('#month-select').val(currentMonth);
    $('#year-input').val(currentYear);

    // Load budget lines for the current month and year on page load
    loadBudgetLines(currentMonth, currentYear);

    // Handle form submission
    $('#budget-form').on('submit', function(e) {
        e.preventDefault();
        let month = $('#month-select').val();
        let year = $('#year-input').val();
        loadBudgetLines(month, year);
    });

    // Function to load budget lines based on month and year
    function loadBudgetLines(month, year) {
        $.ajax({
            url: '/api/GetCustomMonthYearBudget',
            method: 'GET',
            data: { month: month, year: year },
            success: function(data) {
                let budgetList = $('#budget-list');
                budgetList.empty(); // Clear existing data
                data.forEach(function(budget) {
                    let row = `
                        <tr>
                            <td>${budget.description}</td>
                            <td>${budget.amount}</td>
                            <td>${budget.category}</td>
                            <td>${budget.date}</td>
                        </tr>
                    `;
                    budgetList.append(row);
                });
            },
            error: function(xhr, status, error) {
                console.error("Error fetching budget lines:", error);
                // Optionally, display an error message to the user
            }
        });
    }
});
