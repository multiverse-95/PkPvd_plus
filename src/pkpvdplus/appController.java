package pkpvdplus;

import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
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
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import javafx.scene.text.Text;
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
    private TableColumn<ReportModel, String> status_col;

    @FXML
    private TableColumn<ReportModel, String> applicant_col;

    @FXML
    void initialize() {

    }

    public void FilterApplicants(ArrayList<ReportModel> dataReportList, LocalDate dateStart, LocalDate dateFinish){
        ObservableList<ReportModel> data =FXCollections.observableArrayList(dataReportList);
        FilteredList<ReportModel> filteredData = new FilteredList<>(data, e -> true);
        search_t.setOnKeyPressed(event -> {
            search_t.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate((Predicate<? super ReportModel>) report_model->{
                    if (newValue ==null || newValue.isEmpty()){
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if (report_model.getApplicant().toLowerCase().contains(lowerCaseFilter)){
                        return true;
                    }
                    return false;
                });
            });
            SortedList<ReportModel> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(data_rep_table.comparatorProperty());
            data_rep_table.setItems(sortedData);
            if (sortedData.isEmpty()){
                Label label_search=new Label();
                label_search.setText("Нет данных по указанному фильтру.");
                label_search.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
                data_rep_table.setPlaceholder(label_search);
            } else {
                download_report_b.setOnAction(event1 -> {
                    ArrayList<ReportModel> sortedFinal=new ArrayList<>(sortedData);
                    ReportController reportController_new=new ReportController();
                    reportController_new.Download_report(sortedFinal, dateStart, dateFinish);
                });
            }
        });
    }

    public void FilterNameCompany(ArrayList<ReportModel> dataReportList, LocalDate dateStart, LocalDate dateFinish){
        ObservableList<ReportModel> data =FXCollections.observableArrayList(dataReportList);
        FilteredList<ReportModel> filteredData = new FilteredList<>(data, e -> true);
        search_t.setOnKeyPressed(event -> {
            search_t.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate((Predicate<? super ReportModel>) report_model->{
                    if (newValue ==null || newValue.isEmpty()){
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if (report_model.getNameCompany().toLowerCase().contains(lowerCaseFilter)){
                        return true;
                    }
                    return false;
                });
            });
            SortedList<ReportModel> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(data_rep_table.comparatorProperty());
            data_rep_table.setItems(sortedData);
            if (sortedData.isEmpty()){
                Label label_search=new Label();
                label_search.setText("Нет данных по указанному фильтру.");
                label_search.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
                data_rep_table.setPlaceholder(label_search);
            } else {
                download_report_b.setOnAction(event1 -> {
                    ArrayList<ReportModel> sortedFinal=new ArrayList<>(sortedData);
                    ReportController reportController_new=new ReportController();
                    reportController_new.Download_report(sortedFinal, dateStart, dateFinish);
                });
            }
        });
    }

    public void FilterAppeal(ArrayList<ReportModel> dataReportList, LocalDate dateStart, LocalDate dateFinish){
        ObservableList<ReportModel> data =FXCollections.observableArrayList(dataReportList);
        FilteredList<ReportModel> filteredData = new FilteredList<>(data, e -> true);
        search_t.setOnKeyPressed(event -> {
            search_t.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate((Predicate<? super ReportModel>) report_model->{
                    if (newValue ==null || newValue.isEmpty()){
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if (report_model.getAppeal().toLowerCase().contains(lowerCaseFilter)){
                        return true;
                    }
                    return false;
                });
            });
            SortedList<ReportModel> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(data_rep_table.comparatorProperty());
            data_rep_table.setItems(sortedData);
            if (sortedData.isEmpty()){
                Label label_search=new Label();
                label_search.setText("Нет данных по указанному фильтру.");
                label_search.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
                data_rep_table.setPlaceholder(label_search);
            } else {
                download_report_b.setOnAction(event1 -> {
                    ArrayList<ReportModel> sortedFinal=new ArrayList<>(sortedData);
                    ReportController reportController_new=new ReportController();
                    reportController_new.Download_report(sortedFinal, dateStart, dateFinish);
                });
            }
        });
    }

    public void SetFilter(ArrayList<ReportModel> dataReportList, LocalDate dateStart, LocalDate dateFinish){
        System.out.println("Заявитель");
        FilterApplicants(dataReportList, dateStart, dateFinish);
        choiceFilter_box.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                String selectedItemFilter=choiceFilter_box.getItems().get((Integer) number2);
                switch (selectedItemFilter){
                    case "Фильтр по заявителям":
                        System.out.println("Заявитель");
                        search_t.setPromptText("Введите ФИО заявителя");
                        search_t.setText("");
                        FilterApplicants(dataReportList, dateStart, dateFinish);
                        show_rep_b.setOnAction(event -> {});
                        break;
                    case "Фильтр по организациям":
                        System.out.println("Организация");
                        search_t.setPromptText("Введите название организации");
                        search_t.setText("");
                        FilterNameCompany(dataReportList, dateStart, dateFinish);
                        show_rep_b.setOnAction(event -> {});
                        break;
                    case "Фильтр по обращениям":
                        System.out.println("Обращения");
                        search_t.setPromptText("Введите номер обращения");
                        search_t.setText("");
                        FilterAppeal(dataReportList, dateStart, dateFinish);
                        show_rep_b.setOnAction(event -> {});
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
            stage.setScene(new Scene(root, 400, 220));
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

                            status_col.setCellValueFactory(new PropertyValueFactory<>("status"));
                            status_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                            applicant_col.setCellValueFactory(new PropertyValueFactory<>("applicant"));
                            applicant_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                            data_rep_table.setItems(dataReport);
                            // Вызов события для кнопки скачивания отчета по ведомствам

                            download_report_b.setDisable(false);
                            download_report_b.setOnAction(event1 -> {
                                //Download_Report_Task(period_report_label, parsed_result_arr);
                                ReportController reportController=new ReportController();
                                reportController.Download_report(parsed_result_arr ,dateStart, dateFinish);
                            });

                            SetFilter(parsed_result_arr, dateStart, dateFinish);
                            autoResizeColumns(data_rep_table);
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

    public static void autoResizeColumns( TableView<ReportModel> table )
    {
        //Set the right policy
        table.setColumnResizePolicy( TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.getColumns().stream().forEach( (column) ->
        {
            //Minimal width = columnheader
            Text t = new Text( column.getText() );
            double max = t.getLayoutBounds().getWidth();
            for ( int i = 0; i < table.getItems().size(); i++ )
            {
                //cell must not be empty
                if ( column.getCellData( i ) != null )
                {
                    t = new Text( column.getCellData( i ).toString() );
                    double calcwidth = t.getLayoutBounds().getWidth();
                    //remember new max-width
                    if ( calcwidth > max )
                    {
                        max = calcwidth;
                    }
                }
            }
            //set the new max-widht with some extra space
            column.setPrefWidth( max + 10.0d );
        } );
    }
}

