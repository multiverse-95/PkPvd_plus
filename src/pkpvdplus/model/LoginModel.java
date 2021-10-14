package pkpvdplus.model;
// Модель для авторизации
public class LoginModel {
    private String login; // Логин
    private String password; // Пароль
    // Конструктор
    public LoginModel(String login, String password) {
        this.login = login;
        this.password = password;
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
}
