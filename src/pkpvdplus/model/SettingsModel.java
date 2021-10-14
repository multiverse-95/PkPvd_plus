package pkpvdplus.model;

// Модель для json-настроек
public class SettingsModel {
    private String login; // Логин
    private String password; // Пароль
    private String cookie; // Куки
    private String lastPathToFile; // Последний путь к файлу, где был сохранен отчёт
    private boolean isCheckBoxSel; // Флаг чекбокса
    // Конструктор
    public SettingsModel(String login, String password, String cookie, String lastPathToFile, boolean isCheckBoxSel) {
        this.login = login;
        this.password = password;
        this.cookie = cookie;
        this.lastPathToFile = lastPathToFile;
        this.isCheckBoxSel = isCheckBoxSel;
    }
    // Геттеры и сеттеры
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
