$(document).ready(function() {

    // Formatter for currency
    const formatCurrency = new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD',
    });

    // Fetch shares on page load
    function loadShares() {
        $.ajax({
            url: '/user-shares',
            method: 'GET',
            success: function(data) {
                let sharesList = $('#shares-list');
                sharesList.empty();

                if(data.length === 0) {
                    sharesList.append('<tr><td colspan="6" class="text-center text-muted p-4">No assets found. Add your first stock!</td></tr>');
                    return;
                }

                data.forEach(function(share) {
                    // Logic for Profit Color
                    // Note: Ensure your backend calculates profit, OR we calculate it here if needed.
                    // Assuming share.profit is a Number.
                    let profitClass = 'text-neutral';
                    let profitSign = '';

                    if (share.profit > 0) {
                        profitClass = 'text-profit';
                        profitSign = '+';
                    } else if (share.profit < 0) {
                        profitClass = 'text-loss';
                    }

                    let row = `
                        <tr>
                            <td><span class="ticker-badge">${share.ticker}</span></td>
                            <td>${share.amount}</td>
                            <td>${new Date(share.purchaseDate).toLocaleDateString()}</td>
                            <td>${formatCurrency.format(share.price)}</td>
                            <td id="price-${share.id}">
                                <div class="spinner-border spinner-border-sm text-muted" role="status"></div>
                            </td>
                            <td class="${profitClass}">
                                ${profitSign}${formatCurrency.format(share.profit)}
                            </td>
                        </tr>
                    `;
                    sharesList.append(row);
                    loadStockPrice(share.ticker, share.id);
                });
            },
            error: function(err) {
                console.error("Failed to load shares", err);
            }
        });
    }

    // Fetch stock price
    function loadStockPrice(ticker, shareId) {
        $.ajax({
            url: `/api/stock-price?ticker=${ticker}`,
            method: 'GET',
            success: function(data) {
                if (data && data.price) {
                    $(`#price-${shareId}`).text(formatCurrency.format(data.price));
                } else {
                    $(`#price-${shareId}`).text('N/A');
                }
            },
            error: function(err) {
                $(`#price-${shareId}`).html('<span class="text-muted" style="font-size:0.8em">Err</span>');
            }
        });
    }

    loadShares();
});