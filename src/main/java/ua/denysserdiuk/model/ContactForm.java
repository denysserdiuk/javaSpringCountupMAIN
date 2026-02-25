package denysserdiuk.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public class ContactForm {

    @NotEmpty(message = "Name is required")
    private String name;

    @NotEmpty(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotEmpty(message = "Phone number is required")
    private String phone;

    @NotEmpty(message = "Message is required")
    private String message;


    public ContactForm() {}

    // For brevity, here's an example for one field
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
