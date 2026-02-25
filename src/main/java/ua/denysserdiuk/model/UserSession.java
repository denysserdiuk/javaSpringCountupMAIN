package denysserdiuk.model;

import denysserdiuk.enums.SessionStage;

public class UserSession {
    private boolean authenticated = false;
    private SessionStage stage = SessionStage.NONE;
    private Users user;
    private String username;
    private Budget budget;

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public SessionStage getStage() {
        return stage;
    }

    public void setStage(SessionStage stage) {
        this.stage = stage;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void reset() {
        this.authenticated = false;
        this.stage = SessionStage.NONE;
        this.user = null;
        this.username = null;
        this.budget = null;
    }

    public void resetBudget() {
        this.budget = null;
        this.stage = SessionStage.NONE;
    }
}
