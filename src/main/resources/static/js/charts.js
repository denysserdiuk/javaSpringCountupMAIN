// Global Defaults for a consistent "Enterprise" look
Chart.defaults.font.family = "'Inter', sans-serif";
Chart.defaults.color = '#64748b'; // Muted text color
Chart.defaults.scale.grid.color = '#f1f5f9'; // Very subtle grid lines

// 1. Earnings Overview Chart (Line)
$(document).ready(function () {
    $.ajax({
        url: '/api/userBalance',
        type: 'GET',
        success: function (data) {
            const canvas = document.getElementById('earningsChart');
            const earningsCtx = canvas.getContext('2d');

            // --- THEME COLORS (Matches global.css) ---
            const primaryColor = '#10b981'; // Emerald Green
            const gradientStart = 'rgba(16, 185, 129, 0.2)'; // Emerald with opacity
            const gradientEnd = 'rgba(16, 185, 129, 0.0)';   // Transparent

            // Create gradient for the fill
            const gradientFill = earningsCtx.createLinearGradient(0, 0, 0, 300);
            gradientFill.addColorStop(0, gradientStart);
            gradientFill.addColorStop(1, gradientEnd);

            new Chart(earningsCtx, {
                type: 'line',
                data: {
                    labels: Object.keys(data),
                    datasets: [{
                        label: 'Balance',
                        data: Object.values(data),
                        borderColor: primaryColor,
                        backgroundColor: gradientFill,
                        borderWidth: 2,
                        pointBackgroundColor: '#ffffff',
                        pointBorderColor: primaryColor,
                        pointBorderWidth: 2,
                        pointRadius: 4,
                        pointHoverRadius: 6,
                        tension: 0.4, // Smooth curves
                        fill: true
                    }]
                },
                options: {
                    maintainAspectRatio: false, // CRITICAL: Fits to the CSS height
                    responsive: true,
                    interaction: {
                        mode: 'index',
                        intersect: false,
                    },
                    scales: {
                        x: {
                            grid: { display: false },
                            ticks: { font: { size: 11 } }
                        },
                        y: {
                            beginAtZero: true,
                            ticks: {
                                callback: function (value) { return '$' + value.toLocaleString(); },
                                font: { size: 11 }
                            },
                            border: { display: false } // Hides the Y-axis vertical line
                        }
                    },
                    plugins: {
                        legend: { display: false }, // Hide legend for cleaner look
                        tooltip: {
                            backgroundColor: '#1e293b', // Dark Slate tooltip
                            titleColor: '#fff',
                            bodyColor: '#cbd5e1',
                            padding: 10,
                            cornerRadius: 8,
                            displayColors: false, // Remove the color box in tooltip
                            callbacks: {
                                label: function (context) {
                                    return 'Balance: $' + context.parsed.y.toLocaleString();
                                }
                            }
                        }
                    }
                }
            });
        },
        error: function (error) {
            console.log("Error fetching user balance data:", error);
        }
    });
});

// 2. Donut Chart (Expenses)
$(document).ready(function () {
    $.ajax({
        url: 'api/CurrentMonthLossesByCategory',
        type: 'GET',
        success: function (data) {
            var categories = Object.keys(data);
            var percentages = Object.values(data);

            // --- MODERN PALETTE ---
            var colorPalette = [
                '#3b82f6', // Blue
                '#ef4444', // Red
                '#f59e0b', // Amber
                '#10b981', // Emerald
                '#8b5cf6', // Purple
                '#64748b'  // Slate
            ];

            // Extend palette if needed
            while (colorPalette.length < categories.length) {
                colorPalette = colorPalette.concat(colorPalette);
            }

            const revenueCtx = document.getElementById('revenueChart').getContext('2d');
            new Chart(revenueCtx, {
                type: 'doughnut',
                data: {
                    labels: categories,
                    datasets: [{
                        data: percentages,
                        backgroundColor: colorPalette,
                        borderWidth: 0, // No border looks cleaner
                        hoverOffset: 4
                    }]
                },
                options: {
                    maintainAspectRatio: false, // CRITICAL
                    responsive: true,
                    cutout: '75%', // Thinner ring = More modern
                    plugins: {
                        legend: {
                            position: 'right',
                            labels: {
                                usePointStyle: true,
                                pointStyle: 'circle',
                                padding: 20,
                                font: { size: 12 }
                            }
                        },
                        tooltip: {
                            backgroundColor: '#1e293b',
                            callbacks: {
                                label: function (tooltipItem) {
                                    let value = tooltipItem.raw;
                                    return tooltipItem.label + ': ' + Math.round(value) + '%';
                                }
                            }
                        }
                    }
                }
            });
        },
        error: function (error) {
            console.log("Error fetching loss category data:", error);
        }
    });
});