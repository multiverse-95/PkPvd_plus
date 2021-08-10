package pkpvdplus.model;

public class SettingsModel {
    private String login;
    private String password;
    private String cookie;
    private String lastPathToFile;
    private boolean isCheckBoxSel;

    public SettingsModel(String login, String password, String cookie, String lastPathToFile, boolean isCheckBoxSel) {
        this.login = login;
        this.password = password;
        this.cookie = cookie;
        this.lastPathToFile = lastPathToFile;
        this.isCheckBoxSel = isCheckBoxSel;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getLastPathToFile() {
        return lastPathToFile;
    }

    public void setLastPathToFile(String lastPathToFile) {
        this.lastPathToFile = lastPathToFile;
    }

    public boolean isCheckBoxSel() {
        return isCheckBoxSel;
    }

    public void setCheckBoxSel(boolean checkBoxSel) {
        isCheckBoxSel = checkBoxSel;
    }
}
