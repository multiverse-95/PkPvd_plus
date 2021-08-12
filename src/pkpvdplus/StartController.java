package pkpvdplus;

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
import pkpvdplus.controller.LoginController;
import pkpvdplus.model.LoginModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class StartController {
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
        login_button.setOnMouseEntered(event_mouse -> {
            ((Node) event_mouse.getSource()).setCursor(Cursor.HAND);
        });
        saveMe_ch.setOnMouseEntered(event_mouse -> {
            ((Node) event_mouse.getSource()).setCursor(Cursor.HAND);
        });

        ArrayList<LoginModel> dataLogin=AutoAutoriz();
            if (dataLogin.isEmpty()){
                System.out.println("LOGIN IS EMPTY!");
            }
            else if (dataLogin.get(0).getLogin().isEmpty() || dataLogin.get(0).getPassword().isEmpty())
            {
                saveMe_ch.setSelected(true);
            } else if (saveMe_ch.isSelected()){
                String username_text = dataLogin.get(0).getLogin();
                String password_text =dataLogin.get(0).getPassword();
                login_input.setText(username_text);
                password_input.setText(password_text);
                Login_PkPvd(true);
            }

        // При нажатии на кнопку "Вход"
        login_button.setOnAction(event -> {
            if (saveMe_ch.isSelected()) {
                Login_PkPvd(true); // Вызов функции авторизации
            } else {
                Login_PkPvd(false); // Вызов функции авторизации
            }
        });
        // При нажатии на Enter
        login_input.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER)  {
                if (saveMe_ch.isSelected()) {
                    Login_PkPvd(true); // Вызов функции авторизации
                } else {
                    Login_PkPvd(false); // Вызов функции авторизации
                    //NotLogin();
                }
            }
        });
        password_input.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER)  {
                if (saveMe_ch.isSelected()) {
                    Login_PkPvd(true); // Вызов функции авторизации
                } else {
                    Login_PkPvd(false); // Вызов функции авторизации
                }
            }
        });

    }

    public ArrayList<LoginModel> AutoAutoriz() {
        ArrayList<LoginModel> dataLogin=new ArrayList<LoginModel>();
        File file = new File("C:\\pkpvdplus\\settingsPVD.json");

        if(!file.exists())
        {
            System.out.println("No file!");
        } else {
            System.out.println("yes file!");

            JsonParser parser = new JsonParser();
            JsonElement jsontree = null;
            try {
                jsontree = parser.parse(new FileReader("C:\\pkpvdplus\\settingsPVD.json"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            JsonObject jsonObject = jsontree.getAsJsonObject();
            String login=jsonObject.get("login").getAsString();
            String password=jsonObject.get("password").getAsString();
            boolean isCheckBoxSel=jsonObject.get("isCheckBoxSel").getAsBoolean();
            if (isCheckBoxSel) {saveMe_ch.setSelected(true);} else {saveMe_ch.setSelected(false);}
            System.out.println(login +":and: "+password);
            dataLogin.add(new LoginModel(login, password));
        }
        return dataLogin;
    }

    public void Login_PkPvd(boolean isCheckBoxSel) {

        // Запуск прогресса индикации
        ProgressIndicator pi = new ProgressIndicator();
        VBox box = new VBox(pi);
        box.setAlignment(Pos.CENTER);
        // Grey Background
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
}
