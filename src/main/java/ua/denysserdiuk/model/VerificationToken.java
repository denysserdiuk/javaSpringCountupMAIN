package denysserdiuk.model;

import java.time.LocalDateTime;

public class VerificationToken {
    private Users user;
    private Integer code;
    private LocalDateTime expiryDate;

    public VerificationToken(Users user, int code, LocalDateTime expireDate) {
        this.user = user;
        this.code = code;
        this.expiryDate = expireDate;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
