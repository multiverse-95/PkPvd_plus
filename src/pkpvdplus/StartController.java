package pkpvdplus;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import pkpvdplus.controller.LoginController;
import pkpvdplus.model.LoginModel;
import pkpvdplus.model.SettingsModel;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.ResourceBundle;
// Стартовый контроллер
public class StartController {
    // Графические элементы
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private StackPane root;

    @FXML
    private VBox bx;

    @FXML
    private Label error_t;

    @FXML
    private TextField login_input;

    @FXML
    private PasswordField password_input;
    @FXML
    private CheckBox saveMe_ch;

    @FXML
    private Button login_button;

    @FXML
    void initialize() {
        // Поменять курсор при наведении на кнопку "Вход" или на чекбокс
        login_button.setOnMouseEntered(event_mouse -> {
            ((Node) event_mouse.getSource()).setCursor(Cursor.HAND);
        });
        saveMe_ch.setOnMouseEntered(event_mouse -> {
            ((Node) event_mouse.getSource()).setCursor(Cursor.HAND);
        });
        final String[] cookie = {""};
        setLoginPassword();

        // Запуск прогресса индикации
        ProgressIndicator pi = new ProgressIndicator();
        VBox box = new VBox(pi);
        box.setAlignment(Pos.CENTER);
        bx.setDisable(true);
        root.getChildren().add(box);

        Task CookieValidTask = new CookieValidTask(cookie[0]);
        // После выполнения потока
        CookieValidTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
        @Override
        public void handle(WorkerStateEvent event) {
            // Закрытие прогресса индикации
            box.setDisable(true);
            pi.setVisible(false);
            bx.setDisable(false);
            cookie[0] =CookieValidTask.getValue().toString();
            if(!cookie[0].isEmpty()){
                if (saveMe_ch.isSelected()){
                    Enter_PkPvd(cookie[0]);
                } else {
                    // При нажатии на кнопку "Вход"
                    login_button.setOnAction(eventLogin -> {
                        // Если чекбокс активен, запустить авторизацию со флагом True, иначе запустить авторизацию со флагом False
                        if (saveMe_ch.isSelected()) {
                            Login_PkPvd(true); // Вызов функции авторизации с флагом true
                        } else {
                            Login_PkPvd(false); // Вызов функции авторизации с флагом false
                        }
                    });
                    // При нажатии на Enter
                    login_input.setOnKeyPressed(keyEvent -> {
                        if (keyEvent.getCode() == KeyCode.ENTER)  {
                            // Если чекбокс активен, запустить авторизацию со флагом True, иначе запустить авторизацию со флагом False
                            if (saveMe_ch.isSelected()) {
                                Login_PkPvd(true); // Вызов функции авторизации с флагом true
                            } else {
                                Login_PkPvd(false); // Вызов функции авторизации с флагом false
                                //NotLogin();
                            }
                        }
                    });
                    password_input.setOnKeyPressed(keyEvent -> {
                        if (keyEvent.getCode() == KeyCode.ENTER)  {
                            // Если чекбокс активен, запустить авторизацию со флагом True, иначе запустить авторизацию со флагом False
                            if (saveMe_ch.isSelected()) {
                                Login_PkPvd(true); // Вызов функции авторизации с флагом true
                            } else {
                                Login_PkPvd(false); // Вызов функции авторизации с флагом false
                            }
                        }
                    });
                }
            } else {
                // Вызов функции автоматической авторизации
                ArrayList<LoginModel> dataLogin=AutoAutoriz();
                if (dataLogin.isEmpty()){ // Если нет данных в конф. файле
                    System.out.println("LOGIN IS EMPTY!");
                }
                // Если логин или пароль пустые
                else if (dataLogin.get(0).getLogin().isEmpty() || dataLogin.get(0).getPassword().isEmpty())
                {
                    saveMe_ch.setSelected(true); // Установить чекбокс активным
                } else if (saveMe_ch.isSelected()){ // Если данные не пустые и чекбокс активен
                    String username_text = dataLogin.get(0).getLogin(); // Считать логин с файла
                    String password_text =dataLogin.get(0).getPassword(); // Считать пароль с файла
                    login_input.setText(username_text); // Установить логин в поле
                    password_input.setText(password_text); // Установить пароль в поле
                    Login_PkPvd(true); // Запустить авторизацию с флагом true
                }
                // При нажатии на кнопку "Вход"
                login_button.setOnAction(eventLogin -> {
                    // Если чекбокс активен, запустить авторизацию со флагом True, иначе запустить авторизацию со флагом False
                    if (saveMe_ch.isSelected()) {
                        Login_PkPvd(true); // Вызов функции авторизации с флагом true
                    } else {
                        Login_PkPvd(false); // Вызов функции авторизации с флагом false
                    }
                });
                // При нажатии на Enter
                login_input.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode() == KeyCode.ENTER)  {
                        // Если чекбокс активен, запустить авторизацию со флагом True, иначе запустить авторизацию со флагом False
                        if (saveMe_ch.isSelected()) {
                            Login_PkPvd(true); // Вызов функции авторизации с флагом true
                        } else {
                            Login_PkPvd(false); // Вызов функции авторизации с флагом false
                            //NotLogin();
                        }
                    }
                });
                password_input.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode() == KeyCode.ENTER)  {
                        // Если чекбокс активен, запустить авторизацию со флагом True, иначе запустить авторизацию со флагом False
                        if (saveMe_ch.isSelected()) {
                            Login_PkPvd(true); // Вызов функции авторизации с флагом true
                        } else {
                            Login_PkPvd(false); // Вызов функции авторизации с флагом false
                        }
                    }
                });
            }

        }
        });
        Thread CookieValidThread = new Thread(CookieValidTask);
        CookieValidThread.setDaemon(true);
        CookieValidThread.start();

    }


    public void setLoginPassword(){
        File file = new File("C:\\pkpvdplus\\settingsPVD.json");
        // Если файл не существует, то ничего не делать
        if(!file.exists())
        {
            System.out.println("No file!");
        } else { // Иначе
            System.out.println("yes file!");
            // Считываем данные с файла json
            JsonParser parser = new JsonParser();
            JsonElement jsontree = null;

            try {
                jsontree = parser.parse(new BufferedReader(new InputStreamReader(new FileInputStream("C:\\pkpvdplus\\settingsPVD.json"), StandardCharsets.UTF_8)));
                //jsontree = parser.parse(new FileReader("C:\\pkpvdplus\\settingsPVD.json"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Парсим логин, пароль и флаг для активации чекбокса
            JsonObject jsonObject = jsontree.getAsJsonObject();
            String login = jsonObject.get("login").getAsString();
            String password = jsonObject.get("password").getAsString();
            boolean isCheckBoxSel=jsonObject.get("isCheckBoxSel").getAsBoolean();
            // Если флаг для чекбокса активен, то ставим активным чекбокс
            if (isCheckBoxSel) {
                saveMe_ch.setSelected(true);
                login_input.setText(login);
                password_input.setText(password);
            } else {
                saveMe_ch.setSelected(false);
                login_input.setText("");
                password_input.setText("");
            }
        }
    }

    // Функция автоматической авторизации
    public ArrayList<LoginModel> AutoAutoriz() {
        // Создание списка данных о пользователе
        ArrayList<LoginModel> dataLogin=new ArrayList<LoginModel>();
        // Путь к файлу
        File file = new File("C:\\pkpvdplus\\settingsPVD.json");
        // Если файл не существует, то ничего не делать
        if(!file.exists())
        {
            System.out.println("No file!");
        } else { // Иначе
            System.out.println("yes file!");
            // Считываем данные с файла json
            JsonParser parser = new JsonParser();
            JsonElement jsontree = null;

            try {
                jsontree = parser.parse(new BufferedReader(new InputStreamReader(new FileInputStream("C:\\pkpvdplus\\settingsPVD.json"), StandardCharsets.UTF_8)));
                //jsontree = parser.parse(new FileReader("C:\\pkpvdplus\\settingsPVD.json"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Парсим логин, пароль и флаг для активации чекбокса
            JsonObject jsonObject = jsontree.getAsJsonObject();
            String login=jsonObject.get("login").getAsString();
            String password=jsonObject.get("password").getAsString();
            boolean isCheckBoxSel=jsonObject.get("isCheckBoxSel").getAsBoolean();
            // Если флаг для чекбокса активен, то ставим активным чекбокс
            if (isCheckBoxSel) {saveMe_ch.setSelected(true);} else {saveMe_ch.setSelected(false);}
            System.out.println(login +":and: "+password);
            // Добавляем нужные данные в список
            dataLogin.add(new LoginModel(login, password));
        }
        return dataLogin;
    }


    public static String ifCookie_valid() throws IOException {

        String cookie="";
        // Путь к файлу
        File file = new File("C:\\pkpvdplus\\settingsPVD.json");
        // Если файл не существует, то ничего не делать
        if(!file.exists())
        {
            System.out.println("No file!");
        } else { // Иначе
            System.out.println("yes file!");
            // Считываем данные с файла json
            JsonParser parser = new JsonParser();
            JsonElement jsontree = null;

            try {
                jsontree = parser.parse(new BufferedReader(new InputStreamReader(new FileInputStream("C:\\pkpvdplus\\settingsPVD.json"), StandardCharsets.UTF_8)));
                //jsontree = parser.parse(new FileReader("C:\\pkpvdplus\\settingsPVD.json"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            JsonObject jsonObject = jsontree.getAsJsonObject();
            cookie=jsonObject.get("cookie").getAsString();
            System.out.println("cookie from file "+cookie);

            CookieStore httpCookieStore = new BasicCookieStore();
            HttpClient httpClient = null;
            HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore);
            httpClient = builder.build();
            String getUrl       = "http://10.42.200.207/api/rs/reports/list";// Сервер авторизации
            HttpGet httpGet = new HttpGet(getUrl);
            httpGet.setHeader("Content-type", "application/json");
            httpGet.addHeader("Cookie","JSESSIONID="+cookie);
            HttpResponse response = httpClient.execute(httpGet); // Выполняем post запрос

            HttpEntity entity = response.getEntity();
            String result_of_req = EntityUtils.toString(entity); // Получаем результат запроса

            int status_code= response.getStatusLine().getStatusCode();
            System.out.println("Status cookie autor: "+status_code);
            boolean CookieValid;
            switch (status_code){
                case 200:
                    CookieValid=true;
                    break;
                case 401: // Если выбраны организации
                   CookieValid=false;
                   cookie="";
                   break;
                default:
                    CookieValid=false;
                    cookie="";
                    break;
            }
        }

        return cookie;
    }

    // Функция авторизации
    public void Login_PkPvd(boolean isCheckBoxSel) {

        // Запуск прогресса индикации
        ProgressIndicator pi = new ProgressIndicator();
        VBox box = new VBox(pi);
        box.setAlignment(Pos.CENTER);

        bx.setDisable(true);
        root.getChildren().add(box);

        String username_text = login_input.getText(); // Считывание логина
        String password_text = password_input.getText(); // Считывание пароля
        // Инициализация потока с авторизацией
        Task LoginTask = new LoginController.LoginTask(username_text,password_text, isCheckBoxSel);

        // После выполнения потока
        LoginTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                // Закрытие прогресса индикации
                box.setDisable(true);
                pi.setVisible(false);
                bx.setDisable(false);
                System.out.println(LoginTask.getValue());
                String cookie="";
                cookie=LoginTask.getValue().toString();

                // Если авторизация прошла успешно
                if (!cookie.isEmpty()){
                    login_button.getScene().getWindow().hide(); // Скрываем окно авторизации
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/pkpvdplus/view/app.fxml"));
                    try {
                        loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Parent root = loader.getRoot();
                    // Вызываем контроллер основного окна программы
                    appController AppController = loader.getController();
                    //AppController.testDates();
                    AppController.Show_report(cookie); // Вызов функции заполнения отчёта

                    // Запускаем основное окно программы ПК ПВД Плюс
                    Stage stage = new Stage();
                    // Подтверждение выхода из приложения
                    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        public void handle(WindowEvent we) {
                            System.out.println("Stage is closing");

                            // Окошко для подтверждения выхода
                            ButtonType yes_del = new ButtonType("Да", ButtonBar.ButtonData.OK_DONE); // Создание кнопки подтвердить
                            ButtonType no_del = new ButtonType("Нет", ButtonBar.ButtonData.CANCEL_CLOSE); // Создание кнопки отменить
                            Alert alert =new Alert(Alert.AlertType.CONFIRMATION , "Test", yes_del, no_del);
                            alert.setTitle("Выход из приложения"); // Название предупреждения
                            alert.setHeaderText("Подтвердите выход из приложения!"); // Текст предупреждения
                            alert.setContentText("Вы действительно хотите выйти из приложения?");
                            // Вызов подтверждения элемента
                            alert.showAndWait().ifPresent(rs -> {
                                if (rs == yes_del){
                                    System.out.println("Exit!");
                                    Platform.exit();
                                    System.exit(0);
                                } else if(rs ==no_del){
                                    we.consume();
                                }
                            });
                        }
                    });
                    // Запуск основного окна приложения
                    stage.setTitle("ПК ПВД Плюс");
                    //stage.setResizable(false);
                    stage.setScene(new Scene(root));
                    ((Stage) stage.getScene().getWindow()).setMaximized(true);
                    stage.showAndWait();

                } else {
                    // Иначе - если авторизация не удалась, то ошибка
                    error_t.setVisible(true);
                    System.out.println("Error!");
                }
            }
        });

        // Запуск потока
        Thread loginThread = new Thread(LoginTask);
        loginThread.start();

    }

    public void Enter_PkPvd(String cookie)  {
        login_button.getScene().getWindow().hide();// Скрываем окно авторизации
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/pkpvdplus/view/app.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parent root = loader.getRoot();
        // Вызываем контроллер основного окна программы
        appController AppController = loader.getController();
        //AppController.testDates();
        AppController.Show_report(cookie); // Вызов функции заполнения отчёта

        // Запускаем основное окно программы ПК ПВД Плюс
        Stage stage = new Stage();
        // Подтверждение выхода из приложения
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.out.println("Stage is closing");

                // Окошко для подтверждения выхода
                ButtonType yes_del = new ButtonType("Да", ButtonBar.ButtonData.OK_DONE); // Создание кнопки подтвердить
                ButtonType no_del = new ButtonType("Нет", ButtonBar.ButtonData.CANCEL_CLOSE); // Создание кнопки отменить
                Alert alert =new Alert(Alert.AlertType.CONFIRMATION , "Test", yes_del, no_del);
                alert.setTitle("Выход из приложения"); // Название предупреждения
                alert.setHeaderText("Подтвердите выход из приложения!"); // Текст предупреждения
                alert.setContentText("Вы действительно хотите выйти из приложения?");
                // Вызов подтверждения элемента
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == yes_del){
                        System.out.println("Exit!");
                        Platform.exit();
                        System.exit(0);
                    } else if(rs ==no_del){
                        we.consume();
                    }
                });
            }
        });
        // Запуск основного окна приложения
        stage.setTitle("ПК ПВД Плюс");
        //stage.setResizable(false);
        stage.setScene(new Scene(root));
        ((Stage) stage.getScene().getWindow()).setMaximized(true);
        stage.showAndWait();
    }


    // Тестовая функция, если нужно проверить основную программу без авторизации
    public void NotLogin(){
        String cookie="";
        login_button.getScene().getWindow().hide();// Скрываем окно авторизации
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/pkpvdplus/view/app.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parent root = loader.getRoot();
        // Вызываем контроллер основного окна программы
        appController AppController = loader.getController();
        //AppController.testDates();
        AppController.Show_report(cookie); // Вызов функции заполнения отчёта

        // Запускаем основное окно программы ПК ПВД Плюс
        Stage stage = new Stage();

        stage.setTitle("ПК ПВД Плюс");
        //stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    public static class CookieValidTask extends Task<String> {
        private final String cookie; // Логин


        public CookieValidTask(String cookie) {
            this.cookie = cookie;

        }
        @Override
        protected String call() throws Exception {
            String check_cookie= ifCookie_valid();
            return check_cookie;
        }
    }
}
