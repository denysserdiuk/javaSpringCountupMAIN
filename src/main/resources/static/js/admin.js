document.addEventListener('DOMContentLoaded', function() {

    const deleteForm = document.getElementById('delete-user-form');

    // 1. Get CSRF Token from Meta Tags (Spring Security requirement)
    const csrfMeta = document.querySelector('meta[name="_csrf"]');
    const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

    const csrfToken = csrfMeta ? csrfMeta.getAttribute('content') : '';
    const csrfHeader = csrfHeaderMeta ? csrfHeaderMeta.getAttribute('content') : 'X-CSRF-TOKEN';

    if (deleteForm) {
        deleteForm.addEventListener('submit', function(e) {
            e.preventDefault(); // STOP the page from reloading

            // 2. Get the email value
            const emailInput = document.getElementById('userEmail');
            const email = emailInput.value.trim();

            if (!email) {
                showNotification('Please enter a valid email address', 'error');
                return;
            }

            // 3. Visual feedback (Spin the button)
            const btn = deleteForm.querySelector('button');
            const originalBtnText = btn.innerHTML;
            btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Processing...';
            btn.disabled = true;

            // 4. Send JSON request
            fetch('/admin/deleteUser', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                },
                // Send 'email' to match AdminUserActionDto
                body: JSON.stringify({ email: email })
            })
                .then(response => {
                    if (response.ok) {
                        // Success: 200 OK
                        return response.text().then(msg => {
                            showNotification(msg, 'success');
                            emailInput.value = ''; // Clear input on success
                        });
                    } else {
                        // Error: 404 Not Found, etc.
                        return response.text().then(msg => {
                            showNotification(msg || 'Error deleting user', 'error');
                        });
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showNotification('Connection error occurred. Check console.', 'error');
                })
                .finally(() => {
                    // 5. Reset button state
                    btn.innerHTML = originalBtnText;
                    btn.disabled = false;
                });
        });
    }
});

// --- HELPER: Notification Banner Logic ---
function showNotification(message, type) {
    const notification = document.getElementById('notification-area');
    const msgSpan = document.getElementById('notification-message');
    const icon = document.getElementById('notification-icon');

    if (!notification) return;

    // Set Message
    msgSpan.textContent = message;

    // Reset Classes (remove old success/error states)
    notification.className = '';

    // Apply styling based on type
    if (type === 'success') {
        notification.classList.add('notification-success');
        icon.className = 'fas fa-check-circle';
    } else {
        notification.classList.add('notification-error');
        icon.className = 'fas fa-times-circle';
    }

    // Animation: Slide Down
    // Use a tiny timeout to ensure the browser registers the class change before animating
    setTimeout(() => {
        notification.style.top = "20px";
        notification.style.opacity = "1";
    }, 10);

    // Animation: Slide Up (Hide) after 4 seconds
    setTimeout(() => {
        notification.style.top = "-100px";
        notification.style.opacity = "0";
    }, 4000);
}