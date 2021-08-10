package pkpvdplus;

import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import pkpvdplus.controller.LoginController;
import pkpvdplus.controller.ReportController;
import pkpvdplus.model.ReportModel;
import pkpvdplus.model.SettingsModel;

public class appController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private MenuBar app_menuBar;

    @FXML
    private MenuItem menu_item_change_user;

    @FXML
    private StackPane root_report;

    @FXML
    private DatePicker date_start_d;

    @FXML
    private DatePicker date_finish_d;

    @FXML
    private Button generate_report_b;

    @FXML
    private Button download_report_b;

    @FXML
    private VBox vbox_rep_main;

    @FXML
    private HBox vbox_filter;

    @FXML
    private ChoiceBox<String> choiceFilter_box;

    @FXML
    private TextField search_t;

    @FXML
    private Button show_rep_b;

    @FXML
    private Label period_label;

    @FXML
    private TableView<ReportModel> data_rep_table;

    @FXML
    private TableColumn<ReportModel, String> name_company_col;

    @FXML
    private TableColumn<ReportModel, String> appeal_col;

    @FXML
    private TableColumn<ReportModel, String> date_create_col;

    @FXML
    private TableColumn<ReportModel, String> action_col;

    @FXML
    private TableColumn<ReportModel, String> applicant_col;

    @FXML
    void initialize() {

    }

    public void SetResult(ArrayList<ReportModel> reportFind_arr){
        if (reportFind_arr.isEmpty()){
            Label label_search=new Label();
            label_search.setText("Нет данных по указанному фильтру.");
            label_search.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
            data_rep_table.setPlaceholder(label_search);
        }
        ObservableList<ReportModel> dataReport = FXCollections.observableArrayList(reportFind_arr);
        // Получение списка всех ведомств и заполнение в текстовое поле
        // Заполнение данными таблицы
        name_company_col.setCellValueFactory(new PropertyValueFactory<>("nameCompany"));
        name_company_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

        appeal_col.setCellValueFactory(new PropertyValueFactory<>("appeal"));
        appeal_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

        date_create_col.setCellValueFactory(new PropertyValueFactory<>("dateCreate"));
        date_create_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

        action_col.setCellValueFactory(new PropertyValueFactory<>("action"));
        action_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

        applicant_col.setCellValueFactory(new PropertyValueFactory<>("applicant"));
        applicant_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

        data_rep_table.setItems(dataReport);
        // Вызов события для кнопки скачивания отчета по ведомствам

        download_report_b.setDisable(false);
        ArrayList<ReportModel> finalReportFind_arr = reportFind_arr;
        download_report_b.setOnAction(event1 -> {
            //Download_Report_Task(period_report_label, parsed_result_arr);
            ReportController reportController_new=new ReportController();
            reportController_new.Download_report(finalReportFind_arr);
        });
    }

    public void SearchResult(ArrayList<ReportModel> dataReportList, String typeFilter){
            String FilterText=search_t.getText();
            ReportController reportController=new ReportController();

        ArrayList<ReportModel> reportFind_arr = new ArrayList<ReportModel>();
            switch (typeFilter){
                case "Заявитель":
                    reportFind_arr=reportController.FilterApplicants(FilterText, dataReportList);

                    /*Task FilterApplicantsTask = new ReportController.FilterApplicantsTask(FilterText, dataReportList);
                    //  После выполнения потока
                    FilterApplicantsTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent event) {
                            ArrayList<ReportModel> reportFind_arr =(ArrayList<ReportModel>) FilterApplicantsTask.getValue();
                            SetResult(reportFind_arr);
                        }
                    });
                    // Запуск потока
                    Thread FilterApplicantsThread = new Thread(FilterApplicantsTask);
                    FilterApplicantsThread.setDaemon(true);
                    FilterApplicantsThread.start();*/

                    break;
                case "Организация":
                    reportFind_arr =reportController.FilterNameCompany(FilterText, dataReportList);

                    /*Task FilterNameCompanyTask = new ReportController.FilterNameCompanyTask(FilterText, dataReportList);
                    //  После выполнения потока
                    FilterNameCompanyTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent event) {
                            ArrayList<ReportModel> reportFind_arr =(ArrayList<ReportModel>) FilterNameCompanyTask.getValue();
                            SetResult(reportFind_arr);
                        }
                    });
                    // Запуск потока
                    Thread FilterNameCompanyThread = new Thread(FilterNameCompanyTask);
                    FilterNameCompanyThread.setDaemon(true);
                    FilterNameCompanyThread.start();*/
                    break;
                case "Обращения":
                    reportFind_arr =reportController.FilterAppeal(FilterText, dataReportList);

                    /*Task FilterAppealTask = new ReportController.FilterAppealTask(FilterText, dataReportList);
                    //  После выполнения потока
                    FilterAppealTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent event) {
                            ArrayList<ReportModel> reportFind_arr =(ArrayList<ReportModel>) FilterAppealTask.getValue();
                            SetResult(reportFind_arr);
                        }
                    });
                    // Запуск потока
                    Thread FilterAppealThread = new Thread(FilterAppealTask);
                    FilterAppealThread.setDaemon(true);
                    FilterAppealThread.start();*/
                    break;
                default:
                    System.out.println("none");
                    break;
            }
        SetResult(reportFind_arr);

    }

    public void SetFilter(ArrayList<ReportModel> dataReportList){
        System.out.println("Заявитель");
        search_t.textProperty().addListener((observable, oldValue, newValue) -> {
            SearchResult(dataReportList,"Заявитель");
        });
        choiceFilter_box.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                //String FilterText=search_t.getText();
                String selectedItemFilter=choiceFilter_box.getItems().get((Integer) number2);
                switch (selectedItemFilter){
                    case "Фильтр по заявителям":
                        System.out.println("Заявитель");
                        search_t.setPromptText("Введите ФИО заявителя");
                        SearchResult(dataReportList,"Заявитель");
                        search_t.textProperty().addListener((observable, oldValue, newValue) -> {
                            SearchResult(dataReportList,"Заявитель");
                        });
                        show_rep_b.setOnAction(event -> {
                            SearchResult(dataReportList,"Заявитель");
                        });
                        break;
                    case "Фильтр по организациям":
                        System.out.println("Организация");
                        search_t.setPromptText("Введите название организации");
                        SearchResult(dataReportList,"Организация");
                        search_t.textProperty().addListener((observable, oldValue, newValue) -> {
                            SearchResult(dataReportList,"Организация");
                        });
                        show_rep_b.setOnAction(event -> {
                            SearchResult(dataReportList,"Организация");
                        });
                        break;
                    case "Фильтр по обращениям":
                        System.out.println("Обращения");
                        search_t.setPromptText("Введите номер обращения");
                        SearchResult(dataReportList,"Обращения");
                        search_t.textProperty().addListener((observable, oldValue, newValue) -> {
                            SearchResult(dataReportList,"Обращения");
                        });
                        show_rep_b.setOnAction(event -> {
                            SearchResult(dataReportList,"Обращения");
                        });
                        break;
                    default:
                        System.out.println("Nooone!");
                        break;
                }
            }
        });
    }

    public void Show_report(String cookie){
        generate_report_b.setOnMouseEntered(event_mouse -> {
            ((Node) event_mouse.getSource()).setCursor(Cursor.HAND);
        });
        download_report_b.setOnMouseEntered(event_mouse -> {
            ((Node) event_mouse.getSource()).setCursor(Cursor.HAND);
        });
        show_rep_b.setOnMouseEntered(event_mouse -> {
            ((Node) event_mouse.getSource()).setCursor(Cursor.HAND);
        });
        choiceFilter_box.setOnMouseEntered(event_mouse -> {
            ((Node) event_mouse.getSource()).setCursor(Cursor.HAND);
        });


        choiceFilter_box.setItems(FXCollections.observableArrayList(
                "Фильтр по заявителям","Фильтр по организациям", "Фильтр по обращениям"));
        choiceFilter_box.getSelectionModel().selectFirst();
        search_t.setText("");
        search_t.setPromptText("Введите ФИО заявителя");

        menu_item_change_user.setOnAction(event -> {
            File fileJson = new File("C:\\pkpvdplus\\settingsPVD.json");
            /*if(file.delete()){
                System.out.println("C:\\pkpvdplus\\settingsPVD.json файл удален");
            }else System.out.println("Файла C:\\pkpvdplus\\settingsPVD.json не обнаружено");*/

            JsonParser parser = new JsonParser();
            JsonElement jsontree = null;
            try {
                jsontree = parser.parse(new FileReader("C:\\pkpvdplus\\settingsPVD.json"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            JsonObject jsonObject = jsontree.getAsJsonObject();
            String lastPathToFile = jsonObject.get("lastPathToFile").getAsString();
            SettingsModel settingsModel = new SettingsModel("", "", "", lastPathToFile, false);
            settingsModel.setLogin("");
            settingsModel.setPassword("");
            settingsModel.setCookie("");
            settingsModel.setLastPathToFile(lastPathToFile);
            settingsModel.setCheckBoxSel(false);

            Gson gson = new Gson();
            String content = gson.toJson(settingsModel);

            try {
                FileWriter fileWriter = null;
                fileWriter = new FileWriter(fileJson);
                fileWriter.write(content);
                fileWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }


            app_menuBar.getScene().getWindow().hide();// Скрываем окно авторизации

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/pkpvdplus/view/login.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setTitle("ПК ПВД Плюс");
            stage.setResizable(false);
            stage.setScene(new Scene(root, 400, 200));
            stage.show();
        });

        Label label_onStart=new Label();
        label_onStart.setText("Выберите параметры для отчёта.");
        label_onStart.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
        data_rep_table.setPlaceholder(label_onStart);

        generate_report_b.setOnAction(event -> {
            choiceFilter_box.getSelectionModel().selectFirst();
            search_t.setText("");
            search_t.setPromptText("Введите ФИО заявителя");

            LocalDate dateStart=date_start_d.getValue();
            LocalDate dateFinish=date_finish_d.getValue();

            if (dateStart==null || dateFinish==null){
                System.out.println("Date is not correct!");
                Alert alert =new Alert(Alert.AlertType.WARNING , "Test");
                alert.setTitle("Вы не ввели дату!");
                alert.setHeaderText("Необходимо ввести дату!");
                alert.setContentText("Введите начало и конец периода.");
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK){}
                });
            } else {
                //CheckDates
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date1 = null;
                Date date2 =null;
                try {
                    date1 = format.parse(String.valueOf(dateStart));
                    date2= format.parse(String.valueOf(dateFinish));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long dateStartLong = date1.getTime();
                long dateFinishLong =date2.getTime();
                long diffDate=dateFinishLong-dateStartLong;

                if (diffDate<=0){
                    System.out.println("Date is not correct!");
                    Alert alert =new Alert(Alert.AlertType.ERROR , "Test");
                    alert.setTitle("Вы ввели дату некорректно!");
                    alert.setHeaderText("Проверьте правильность ввода даты!");
                    alert.setContentText("Возможно вы перепутали начало и конец периода.");
                    alert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK){}
                    });
                } else {
                    data_rep_table.setItems(null);
                    ProgressIndicator pi = new ProgressIndicator(); // Запуск прогресс индикатора
                    VBox box = new VBox(pi);
                    box.setAlignment(Pos.CENTER);
                    data_rep_table.setDisable(true);

                    Label label_load=new Label();
                    label_load.setText("Загрузка данных...");
                    label_load.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 18));
                    data_rep_table.setPlaceholder(label_load);
                    vbox_rep_main.setDisable(true);
                    root_report.getChildren().add(box);

                    Task ReportTask = new ReportController.ReportTask(cookie, "",dateStart,dateFinish);

                    //  После выполнения потока
                    ReportTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent event) {
                            box.setDisable(true);
                            pi.setVisible(false);
                            vbox_rep_main.setDisable(false);
                            data_rep_table.setDisable(false);
                            vbox_filter.setDisable(false);
                            // Получение распарсенных данных по ведомствам МфЦ

                            // Получение данных с распарсенного поля
                            ArrayList<ReportModel> parsed_result_arr= (ArrayList<ReportModel>) ReportTask.getValue();

                            System.out.println(parsed_result_arr.get(0).getPeriod());
                            String period_report_label="Отчёт "+parsed_result_arr.get(0).getPeriod();
                            period_label.setText(period_report_label);
                            parsed_result_arr.remove(0);

                            ObservableList<ReportModel> dataReport = FXCollections.observableArrayList(parsed_result_arr);
                            // Получение списка всех ведомств и заполнение в текстовое поле
                            // Заполнение данными таблицы
                            name_company_col.setCellValueFactory(new PropertyValueFactory<>("nameCompany"));
                            name_company_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                            appeal_col.setCellValueFactory(new PropertyValueFactory<>("appeal"));
                            appeal_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                            date_create_col.setCellValueFactory(new PropertyValueFactory<>("dateCreate"));
                            date_create_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                            action_col.setCellValueFactory(new PropertyValueFactory<>("action"));
                            action_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                            applicant_col.setCellValueFactory(new PropertyValueFactory<>("applicant"));
                            applicant_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                            data_rep_table.setItems(dataReport);
                            // Вызов события для кнопки скачивания отчета по ведомствам

                            download_report_b.setDisable(false);
                            download_report_b.setOnAction(event1 -> {
                                //Download_Report_Task(period_report_label, parsed_result_arr);
                                ReportController reportController=new ReportController();
                                reportController.Download_report(parsed_result_arr);
                            });

                            SetFilter(parsed_result_arr);
                        }
                    });

                    // Запуск потока
                    Thread reportThread = new Thread(ReportTask);
                    reportThread.setDaemon(true);
                    reportThread.start();
                }
            }
        });

    }

    /*public void Download_Report_Task(String dateReport, ArrayList<ReportModel> parsed_result_arr){
        Task DownloadTask = new ReportController.DownloadTask (dateReport, parsed_result_arr);

        //  После выполнения потока
        DownloadTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println(DownloadTask.getValue());
            }
        });

        // Запуск потока
        Thread DownloadThread = new Thread(DownloadTask);
        DownloadThread.setDaemon(true);
        DownloadThread.start();
    }*/
}

