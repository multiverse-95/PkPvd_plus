package pkpvdplus;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import pkpvdplus.controller.AppealInfoController;
import pkpvdplus.controller.GetAppealInfoController;
import pkpvdplus.controller.LoginController;
import pkpvdplus.controller.ReportController;
import pkpvdplus.model.ReportModel;
import pkpvdplus.model.SettingsModel;
// Главный контроллер программы
public class appController {
    // Графические элементы
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private MenuBar app_menuBar;

    @FXML
    private MenuItem menu_item_change_user;

    @FXML
    private ChoiceBox<String> type_getDoc_box;

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
    private TableColumn<ReportModel, String> number_appeal_col;

    @FXML
    private TableColumn<ReportModel, String> name_appeal_col;

    @FXML
    private TableColumn<ReportModel, String> date_create_col;

    @FXML
    private TableColumn<ReportModel, String> date_end_col;

    @FXML
    private TableColumn<ReportModel, String> status_col;

    @FXML
    private TableColumn<ReportModel, String> cur_step_col;

    @FXML
    private TableColumn<ReportModel, String> applicant_col;

    @FXML
    private StackPane root_org_report;

    @FXML
    private VBox vbox_rep_org_main;

    @FXML
    private DatePicker date_start_org_d;

    @FXML
    private DatePicker date_finish_org_d;

    @FXML
    private Button generate_report_org_b;

    @FXML
    private Button download_report_org_b;

    @FXML
    private HBox vbox_org_filter;

    @FXML
    private ChoiceBox<String> choiceFilter_org_box;

    @FXML
    private TextField search_org_t;

    @FXML
    private Button show_rep_org_b;

    @FXML
    private Label period_org_label;

    @FXML
    private TableView<ReportModel> data_rep_org_table;

    @FXML
    private TableColumn<ReportModel, String> name_company_org_col;

    @FXML
    private TableColumn<ReportModel, String> number_appeal_org_col;

    @FXML
    private TableColumn<ReportModel, String> name_appeal_org_col;

    @FXML
    private TableColumn<ReportModel, String> date_create_org_col;

    @FXML
    private TableColumn<ReportModel, String> date_end_org_col;

    @FXML
    private TableColumn<ReportModel, String> status_org_col;

    @FXML
    private TableColumn<ReportModel, String> cur_step_org_col;

    @FXML
    private TableColumn<ReportModel, String> applicant_org_col;

    @FXML
    void initialize() {
        date_start_d.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
        date_finish_d.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
        date_start_org_d.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
        date_finish_org_d.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
    }

    public void Show_Appeal_Info(String cookie, String numberAppeal){
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/pkpvdplus/view/appeal_info.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            AppealInfoController appealInfoController = loader.getController();
            //AppController.testDates();
            appealInfoController.ShowAppealInfo(cookie, numberAppeal); // Вызов функции заполнения отчёта
            Stage stage = new Stage();
            stage.setTitle("Информация об обращении");
            //stage.setResizable(false);
            stage.setScene(new Scene(root, 800, 900));
            stage.showAndWait();

       // });
    }

    // Фильтр для обработки заявителей
    public void FilterApplicants(ArrayList<ReportModel> dataReportList, LocalDate dateStart, LocalDate dateFinish){
        ObservableList<ReportModel> data =FXCollections.observableArrayList(dataReportList);
        FilteredList<ReportModel> filteredData = new FilteredList<>(data, e -> true);
        // При вводе в поле
        search_t.setOnKeyPressed(event -> {
            search_t.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate((Predicate<? super ReportModel>) report_model->{
                    if (newValue ==null || newValue.isEmpty()){ // Если значение пустое или null
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase(); // Получить значение с поля и привести к нижнему регистру
                    // Если в списке отчёта есть данные, которые соответствуют фильтру
                    if (report_model.getApplicant().toLowerCase().contains(lowerCaseFilter)){
                        return true;
                    }
                    return false;
                });
            });
            SortedList<ReportModel> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(data_rep_table.comparatorProperty());
            data_rep_table.setItems(sortedData); // Установить отсортированные данные для таблицы
            // Если нет данные в отсортированных даннных
            if (sortedData.isEmpty()){
                // Установить поле для таблицы
                Label label_search=new Label();
                label_search.setText("Нет данных по указанному фильтру.");
                label_search.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
                data_rep_table.setPlaceholder(label_search);
            } else {
                // Иначе назначить для кнопки "Скачать отчёт" событие на скачивание отчёта
                download_report_b.setOnAction(event1 -> {
                    ArrayList<ReportModel> sortedFinal=new ArrayList<>(sortedData);
                    ReportController reportController_new=new ReportController();
                    reportController_new.Download_report(sortedFinal, dateStart, dateFinish); // Вызов функции на скачивание отчёта
                });
            }
        });
    }

    // Фильтр для обработки заявителей (Для организаций)
    public void FilterApplicantsOrg(ArrayList<ReportModel> dataReportList, LocalDate dateStart, LocalDate dateFinish){
        ObservableList<ReportModel> data =FXCollections.observableArrayList(dataReportList);
        FilteredList<ReportModel> filteredData = new FilteredList<>(data, e -> true);
        // При вводе в поле
        search_org_t.setOnKeyPressed(event -> {
            search_org_t.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate((Predicate<? super ReportModel>) report_model->{
                    if (newValue ==null || newValue.isEmpty()){ // Если значение пустое или null
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase(); // Получить значение с поля и привести к нижнему регистру
                    // Если в списке отчёта есть данные, которые соответствуют фильтру
                    if (report_model.getApplicant().toLowerCase().contains(lowerCaseFilter)){
                        return true;
                    }
                    return false;
                });
            });
            SortedList<ReportModel> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(data_rep_org_table.comparatorProperty());
            data_rep_org_table.setItems(sortedData); // Установить отсортированные данные для таблицы
            // Если нет данные в отсортированных даннных
            if (sortedData.isEmpty()){
                // Установить поле для таблицы
                Label label_search=new Label();
                label_search.setText("Нет данных по указанному фильтру.");
                label_search.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
                data_rep_org_table.setPlaceholder(label_search);
            } else {
                // Иначе назначить для кнопки "Скачать отчёт" событие на скачивание отчёта
                download_report_org_b.setOnAction(event1 -> {
                    ArrayList<ReportModel> sortedFinal=new ArrayList<>(sortedData);
                    ReportController reportController_new=new ReportController();
                    reportController_new.Download_report(sortedFinal, dateStart, dateFinish); // Вызов функции на скачивание отчёта
                });
            }
        });
    }

    // Фильтр для обработки наименований МФЦ
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
                    // Сравниваем с фильтром название организации
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

    // Фильтр для обработки наименований МФЦ (Для организаций)
    public void FilterNameCompanyOrg(ArrayList<ReportModel> dataReportList, LocalDate dateStart, LocalDate dateFinish){
        ObservableList<ReportModel> data =FXCollections.observableArrayList(dataReportList);
        FilteredList<ReportModel> filteredData = new FilteredList<>(data, e -> true);
        search_org_t.setOnKeyPressed(event -> {
            search_org_t.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate((Predicate<? super ReportModel>) report_model->{
                    if (newValue ==null || newValue.isEmpty()){
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    // Сравниваем с фильтром название организации
                    if (report_model.getNameCompany().toLowerCase().contains(lowerCaseFilter)){
                        return true;
                    }
                    return false;
                });
            });
            SortedList<ReportModel> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(data_rep_org_table.comparatorProperty());
            data_rep_org_table.setItems(sortedData);
            if (sortedData.isEmpty()){
                Label label_search=new Label();
                label_search.setText("Нет данных по указанному фильтру.");
                label_search.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
                data_rep_org_table.setPlaceholder(label_search);
            } else {
                download_report_org_b.setOnAction(event1 -> {
                    ArrayList<ReportModel> sortedFinal=new ArrayList<>(sortedData);
                    ReportController reportController_new=new ReportController();
                    reportController_new.Download_report(sortedFinal, dateStart, dateFinish);
                });
            }
        });
    }


    // Фильтр для обработки обращений
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
                    // Сравниваем обращения с фильтром
                    if (report_model.getNumberAppeal().toLowerCase().contains(lowerCaseFilter)){
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

    // Фильтр для обработки обращений (Для организаций)
    public void FilterAppealOrg(ArrayList<ReportModel> dataReportList, LocalDate dateStart, LocalDate dateFinish){
        ObservableList<ReportModel> data =FXCollections.observableArrayList(dataReportList);
        FilteredList<ReportModel> filteredData = new FilteredList<>(data, e -> true);
        search_org_t.setOnKeyPressed(event -> {
            search_org_t.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate((Predicate<? super ReportModel>) report_model->{
                    if (newValue ==null || newValue.isEmpty()){
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    // Сравниваем обращения с фильтром
                    if (report_model.getNumberAppeal().toLowerCase().contains(lowerCaseFilter)){
                        return true;
                    }
                    return false;
                });
            });
            SortedList<ReportModel> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(data_rep_org_table.comparatorProperty());
            data_rep_org_table.setItems(sortedData);
            if (sortedData.isEmpty()){
                Label label_search=new Label();
                label_search.setText("Нет данных по указанному фильтру.");
                label_search.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
                data_rep_org_table.setPlaceholder(label_search);
            } else {
                download_report_org_b.setOnAction(event1 -> {
                    ArrayList<ReportModel> sortedFinal=new ArrayList<>(sortedData);
                    ReportController reportController_new=new ReportController();
                    reportController_new.Download_report(sortedFinal, dateStart, dateFinish);
                });
            }
        });
    }

    public void FilterByButton(String typeFilter, ArrayList<ReportModel> dataReportList, LocalDate dateStart, LocalDate dateFinish){
        String search_text=search_t.getText().toLowerCase();
        // Список найденных ведомств
        ArrayList<ReportModel> datFind_modelArr = new ArrayList<ReportModel>();
        // Идем по циклу ведомства и если есть совпадение, то записываем в список найденных ведомств
        switch (typeFilter){
                case "Фильтр по заявителям":
                    for (int i=0; i<dataReportList.size(); i++){
                        if (dataReportList.get(i).getApplicant().toLowerCase().contains(search_text)){
                            datFind_modelArr.add((new ReportModel("", dataReportList.get(i).getNameCompany(), dataReportList.get(i).getNumberAppeal(),
                                    dataReportList.get(i).getNameAppeal(), dataReportList.get(i).getDateCreate(), dataReportList.get(i).getStatus(),
                                    dataReportList.get(i).getApplicant(), dataReportList.get(i).getDateEnd(), dataReportList.get(i).getCurrentStep())));
                        }
                    }
                    break;
                case "Фильтр по МФЦ":
                    for (int i=0; i<dataReportList.size(); i++){
                        if (dataReportList.get(i).getNameCompany().toLowerCase().contains(search_text)){
                            datFind_modelArr.add((new ReportModel("", dataReportList.get(i).getNameCompany(), dataReportList.get(i).getNumberAppeal(),
                                    dataReportList.get(i).getNameAppeal(), dataReportList.get(i).getDateCreate(), dataReportList.get(i).getStatus(),
                                    dataReportList.get(i).getApplicant(), dataReportList.get(i).getDateEnd(), dataReportList.get(i).getCurrentStep())));
                        }
                    }
                    break;
                case "Фильтр по обращениям":
                    for (int i=0; i<dataReportList.size(); i++){
                            if (dataReportList.get(i).getNumberAppeal().toLowerCase().contains(search_text)){
                                datFind_modelArr.add((new ReportModel("", dataReportList.get(i).getNameCompany(), dataReportList.get(i).getNumberAppeal(),
                                        dataReportList.get(i).getNameAppeal(), dataReportList.get(i).getDateCreate(), dataReportList.get(i).getStatus(),
                                        dataReportList.get(i).getApplicant(), dataReportList.get(i).getDateEnd(), dataReportList.get(i).getCurrentStep())));
                            }
                        }
                    break;
        default:
        datFind_modelArr=null;
        break;
        }

        ObservableList<ReportModel> data =FXCollections.observableArrayList(datFind_modelArr);
        data_rep_table.setItems(data);
        if (datFind_modelArr.isEmpty()){
            Label label_search=new Label();
            label_search.setText("Нет данных по указанному фильтру.");
            label_search.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
            data_rep_table.setPlaceholder(label_search);
        } else {
            ArrayList<ReportModel> finalDatFind_modelArr = datFind_modelArr;
            download_report_b.setOnAction(event1 -> {
                ReportController reportController_new=new ReportController();
                reportController_new.Download_report(finalDatFind_modelArr, dateStart, dateFinish);
            });
        }

    }

    public void FilterByButtonOrg(String typeFilter, ArrayList<ReportModel> dataReportList, LocalDate dateStart, LocalDate dateFinish){
        String search_text=search_org_t.getText().toLowerCase();
        // Список найденных ведомств
        ArrayList<ReportModel> datFind_modelArr = new ArrayList<ReportModel>();
        // Идем по циклу ведомства и если есть совпадение, то записываем в список найденных ведомств
        switch (typeFilter){
            case "Фильтр по заявителям":
                for (int i=0; i<dataReportList.size(); i++){
                    if (dataReportList.get(i).getApplicant().toLowerCase().contains(search_text)){
                        datFind_modelArr.add((new ReportModel("", dataReportList.get(i).getNameCompany(), dataReportList.get(i).getNumberAppeal(),
                                dataReportList.get(i).getNameAppeal(), dataReportList.get(i).getDateCreate(), dataReportList.get(i).getStatus(),
                                dataReportList.get(i).getApplicant(), dataReportList.get(i).getDateEnd(), dataReportList.get(i).getCurrentStep())));
                    }
                }
                break;
            case "Фильтр по МФЦ":
                for (int i=0; i<dataReportList.size(); i++){
                    if (dataReportList.get(i).getNameCompany().toLowerCase().contains(search_text)){
                        datFind_modelArr.add((new ReportModel("", dataReportList.get(i).getNameCompany(), dataReportList.get(i).getNumberAppeal(),
                                dataReportList.get(i).getNameAppeal(), dataReportList.get(i).getDateCreate(), dataReportList.get(i).getStatus(),
                                dataReportList.get(i).getApplicant(), dataReportList.get(i).getDateEnd(), dataReportList.get(i).getCurrentStep())));
                    }
                }
                break;
            case "Фильтр по обращениям":
                for (int i=0; i<dataReportList.size(); i++){
                    if (dataReportList.get(i).getNumberAppeal().toLowerCase().contains(search_text)){
                        datFind_modelArr.add((new ReportModel("", dataReportList.get(i).getNameCompany(), dataReportList.get(i).getNumberAppeal(),
                                dataReportList.get(i).getNameAppeal(), dataReportList.get(i).getDateCreate(), dataReportList.get(i).getStatus(),
                                dataReportList.get(i).getApplicant(), dataReportList.get(i).getDateEnd(), dataReportList.get(i).getCurrentStep())));
                    }
                }
                break;
            default:
                datFind_modelArr=null;
                break;
        }

        ObservableList<ReportModel> data =FXCollections.observableArrayList(datFind_modelArr);
        data_rep_org_table.setItems(data);
        if (datFind_modelArr.isEmpty()){
            Label label_search=new Label();
            label_search.setText("Нет данных по указанному фильтру.");
            label_search.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
            data_rep_org_table.setPlaceholder(label_search);
        } else {
            ArrayList<ReportModel> finalDatFind_modelArr = datFind_modelArr;
            download_report_org_b.setOnAction(event1 -> {
                ReportController reportController_new=new ReportController();
                reportController_new.Download_report(finalDatFind_modelArr, dateStart, dateFinish);
            });
        }

    }

    public void setTableByDefault(ArrayList<ReportModel> dataReportList, LocalDate dateStart, LocalDate dateFinish){
        ObservableList<ReportModel> dataTable=FXCollections.observableArrayList(dataReportList);
        data_rep_table.setItems(dataTable);

        if (dataReportList.isEmpty()){
            Label label_search=new Label();
            label_search.setText("Нет данных по указанному фильтру.");
            label_search.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
            data_rep_table.setPlaceholder(label_search);
        } else {
            download_report_b.setOnAction(event1 -> {
                ReportController reportController_new=new ReportController();
                reportController_new.Download_report(dataReportList, dateStart, dateFinish);
            });
        }
    }

    public void setTableByDefaultOrg(ArrayList<ReportModel> dataReportList, LocalDate dateStart, LocalDate dateFinish){
        ObservableList<ReportModel> dataTable=FXCollections.observableArrayList(dataReportList);
        data_rep_org_table.setItems(dataTable);

        if (dataReportList.isEmpty()){
            Label label_search=new Label();
            label_search.setText("Нет данных по указанному фильтру.");
            label_search.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
            data_rep_org_table.setPlaceholder(label_search);
        } else {
            download_report_org_b.setOnAction(event1 -> {
                ReportController reportController_new=new ReportController();
                reportController_new.Download_report(dataReportList, dateStart, dateFinish);
            });
        }
    }

    // Функция для установки фильтра
    public void SetFilter(ArrayList<ReportModel> dataReportList, LocalDate dateStart, LocalDate dateFinish){
        System.out.println("Заявитель");
        // Ставим фильтр для заявителей по-умолчанию
        FilterApplicants(dataReportList, dateStart, dateFinish);
        show_rep_b.setOnAction(event -> {FilterByButton("Фильтр по заявителям",dataReportList,dateStart,dateFinish);});
        createContextMenuFilterField("Фильтр по заявителям", dataReportList,dateStart,dateFinish);
        // Событие для переключателя фильтров
        choiceFilter_box.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                String selectedItemFilter=choiceFilter_box.getItems().get((Integer) number2);
                switch (selectedItemFilter){
                    case "Фильтр по заявителям": // Если выбраны заявители
                        setTableByDefault(dataReportList, dateStart, dateFinish);

                        System.out.println("Заявитель");
                        search_t.setPromptText("Введите ФИО заявителя");
                        search_t.setText("");
                        FilterApplicants(dataReportList, dateStart, dateFinish); // Запустить фильтр для заявителей
                        show_rep_b.setOnAction(event -> {FilterByButton("Фильтр по заявителям",dataReportList,dateStart,dateFinish);});
                        createContextMenuFilterField("Фильтр по заявителям", dataReportList,dateStart,dateFinish);
                        break;
                    case "Фильтр по МФЦ": // Если выбраны организации
                        setTableByDefault(dataReportList, dateStart, dateFinish);

                        System.out.println("Организация");
                        search_t.setPromptText("Введите название МФЦ");
                        search_t.setText("");
                        FilterNameCompany(dataReportList, dateStart, dateFinish); // Запустить фильтр для организаций
                        show_rep_b.setOnAction(event -> {FilterByButton("Фильтр по МФЦ",dataReportList,dateStart,dateFinish);});
                        createContextMenuFilterField("Фильтр по МФЦ", dataReportList,dateStart,dateFinish);
                        break;
                    case "Фильтр по обращениям": // Если выбраны обращения
                        setTableByDefault(dataReportList, dateStart, dateFinish);

                        System.out.println("Обращения");
                        search_t.setPromptText("Введите номер обращения");
                        search_t.setText("");
                        FilterAppeal(dataReportList, dateStart, dateFinish); // Запустить фильтр для обращений
                        show_rep_b.setOnAction(event -> {FilterByButton("Фильтр по обращениям",dataReportList,dateStart,dateFinish);});
                        createContextMenuFilterField("Фильтр по обращениям", dataReportList,dateStart,dateFinish);
                        break;
                    default:
                        System.out.println("Nooone!");
                        break;
                }
            }
        });
    }

    // Функция для установки фильтра (Для организаций)
    public void SetFilterOrg(ArrayList<ReportModel> dataReportList, LocalDate dateStart, LocalDate dateFinish){
        System.out.println("Заявитель");
        // Ставим фильтр для заявителей по-умолчанию
        FilterApplicantsOrg(dataReportList, dateStart, dateFinish);
        show_rep_org_b.setOnAction(event -> {FilterByButtonOrg("Фильтр по заявителям",dataReportList,dateStart,dateFinish);});
        createContextMenuFilterFieldOrg("Фильтр по заявителям", dataReportList,dateStart,dateFinish);
        // Событие для переключателя фильтров
        choiceFilter_org_box.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                String selectedItemFilter=choiceFilter_org_box.getItems().get((Integer) number2);
                switch (selectedItemFilter){
                    case "Фильтр по заявителям": // Если выбраны заявители
                        setTableByDefaultOrg(dataReportList, dateStart, dateFinish);

                        System.out.println("Заявитель");
                        search_org_t.setPromptText("Введите ФИО заявителя");
                        search_org_t.setText("");
                        FilterApplicantsOrg(dataReportList, dateStart, dateFinish); // Запустить фильтр для заявителей
                        show_rep_org_b.setOnAction(event -> {FilterByButtonOrg("Фильтр по заявителям",dataReportList,dateStart,dateFinish);});
                        createContextMenuFilterFieldOrg("Фильтр по заявителям", dataReportList,dateStart,dateFinish);
                        break;
                    case "Фильтр по МФЦ": // Если выбраны организации
                        setTableByDefaultOrg(dataReportList, dateStart, dateFinish);

                        System.out.println("Организация");
                        search_org_t.setPromptText("Введите название МФЦ");
                        search_org_t.setText("");
                        FilterNameCompanyOrg(dataReportList, dateStart, dateFinish); // Запустить фильтр для организаций
                        show_rep_org_b.setOnAction(event -> {FilterByButtonOrg("Фильтр по МФЦ",dataReportList,dateStart,dateFinish);});
                        createContextMenuFilterFieldOrg("Фильтр по МФЦ", dataReportList,dateStart,dateFinish);
                        break;
                    case "Фильтр по обращениям": // Если выбраны обращения
                        setTableByDefaultOrg(dataReportList, dateStart, dateFinish);

                        System.out.println("Обращения");
                        search_org_t.setPromptText("Введите номер обращения");
                        search_org_t.setText("");
                        FilterAppealOrg(dataReportList, dateStart, dateFinish); // Запустить фильтр для обращений
                        show_rep_org_b.setOnAction(event -> {FilterByButtonOrg("Фильтр по обращениям",dataReportList,dateStart,dateFinish);});
                        createContextMenuFilterFieldOrg("Фильтр по обращениям", dataReportList,dateStart,dateFinish);
                        break;
                    default:
                        System.out.println("Nooone!");
                        break;
                }
            }
        });
    }

    // Функция для установки способа получения документов
    public void TypeGetDocGenerateReport(String cookie){
        // По-умолчанию
        generate_report_org_b.setOnAction(event -> {
            GenerateReportOrg(cookie, "ALL");
        });
        // Событие для переключателя фильтров
        type_getDoc_box.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                String selectedItemFilter=type_getDoc_box.getItems().get((Integer) number2);
                switch (selectedItemFilter){
                    case "Все": // Если выбраны заявители
                        System.out.println("ALL");
                        generate_report_org_b.setOnAction(event -> {
                            GenerateReportOrg(cookie, "ALL");
                        });
                        break;
                    case "В МФЦ": // Если выбраны организации
                        System.out.println("MFC");
                        generate_report_org_b.setOnAction(event -> {
                            GenerateReportOrg(cookie, "MFC");
                        });
                        break;
                    case "По электронной почте": // Если выбраны обращения
                        System.out.println("EMAIL");
                        generate_report_org_b.setOnAction(event -> {
                            GenerateReportOrg(cookie, "EMAIL");
                        });
                        break;
                }
            }
        });
    }

    // Функция для отображения отчёта
    public void Show_report(String cookie){
        // Установить курсор при наведении на кнопки
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

        // Установить фильтры
        choiceFilter_box.setItems(FXCollections.observableArrayList(
                "Фильтр по заявителям","Фильтр по МФЦ", "Фильтр по обращениям"));
        choiceFilter_box.getSelectionModel().selectFirst();
        search_t.setText("");
        search_t.setPromptText("Введите ФИО заявителя");
        // Установить действие для кнопки "Сменить пользователя"
        menu_item_change_user.setOnAction(event -> {
            // Считать данные с файла
            File fileJson = new File("C:\\pkpvdplus\\settingsPVD.json");

            JsonParser parser = new JsonParser();
            JsonElement jsontree = null;
            try {
                jsontree = parser.parse(new BufferedReader(new InputStreamReader(new FileInputStream("C:\\pkpvdplus\\settingsPVD.json"), StandardCharsets.UTF_8)));
               // jsontree = parser.parse(new FileReader("C:\\pkpvdplus\\settingsPVD.json"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // Сохранить путь для сохранения отчёта, остальное удалить
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
                Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileJson), StandardCharsets.UTF_8));
                out.write(content);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            app_menuBar.getScene().getWindow().hide();// Скрываем окно программы
            // Запускаем окно авторизации
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
        // Установить текст для поля таблицы
        Label label_onStart=new Label();
        label_onStart.setText("Выберите параметры для отчёта.");
        label_onStart.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
        data_rep_table.setPlaceholder(label_onStart);
        // Установить событие на кнопку "Сформировать отчёт"
        generate_report_b.setOnAction(event -> {
            // Выбрать для переключателя фильтров первый элемент
            choiceFilter_box.getSelectionModel().selectFirst();
            search_t.setText("");
            search_t.setPromptText("Введите ФИО заявителя");
            // Получить начальную дату и конечную дату
            LocalDate dateStart=date_start_d.getValue();
            LocalDate dateFinish=date_finish_d.getValue();
            // Если начальная дата или конечная дата пустые
            if (dateStart==null || dateFinish==null){
                // Вывести предупреждение
                System.out.println("Date is not correct!");
                Alert alert =new Alert(Alert.AlertType.WARNING , "Test");
                alert.setTitle("Вы не ввели дату!");
                alert.setHeaderText("Необходимо ввести дату!");
                alert.setContentText("Введите начало и конец периода.");
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK){}
                });
            } else { // Иначе
                // Получить начальную и конечную дату в Unix формате
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date1 = null;
                Date date2 =null;
                try {
                    date1 = format.parse(String.valueOf(dateStart));
                    date2= format.parse(String.valueOf(dateFinish));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String DateNow_String = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

                Date dateNow_date=null;
                try {
                    dateNow_date = format.parse(DateNow_String);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long dateNowLong=dateNow_date.getTime();
                long dateStartLong = date1.getTime(); // Дата начала в UNIX
                long dateFinishLong =date2.getTime(); // Дата окончания в UNIX
                long diffNow_difStart=dateNowLong-dateStartLong;
                long diffNow_difFinish=dateNowLong-dateFinishLong;
                long diffDate=dateFinishLong-dateStartLong; // Разность между конечной датой и начальной датой
                // Если разность 0 или меньше
                if (diffDate<=0 || diffNow_difStart<=0 || diffNow_difFinish<=0){
                    // Вывести предупреждение
                    System.out.println("Date is not correct!");
                    Alert alert =new Alert(Alert.AlertType.ERROR , "Test");
                    alert.setTitle("Вы ввели дату некорректно!");
                    alert.setHeaderText("Проверьте правильность ввода даты!");
                    alert.setContentText("Возможно вы перепутали начало и конец периода. Также отчёт нельзя взять за текущий день.");
                    alert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK){}
                    });
                } else { // Иначе, если дата в порядке
                    data_rep_table.setItems(null); // Обнулить данные в таблице
                    ProgressIndicator pi = new ProgressIndicator(); // Запуск прогресс индикатора
                    VBox box = new VBox(pi);
                    box.setAlignment(Pos.CENTER);
                    data_rep_table.setDisable(true);
                    // Установить текст для таблицы
                    Label label_load=new Label();
                    label_load.setText("Загрузка данных...");
                    label_load.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 18));
                    data_rep_table.setPlaceholder(label_load);
                    vbox_rep_main.setDisable(true);
                    root_report.getChildren().add(box);
                    // Запустить поток для получения отчёта с сервера
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

                            // Получение данных с распарсенного поля
                            ArrayList<ReportModel> parsed_result_arr= (ArrayList<ReportModel>) ReportTask.getValue();
                            if (parsed_result_arr==null){
                                vbox_filter.setDisable(true);
                                download_report_b.setDisable(true);

                                Label label_load=new Label();
                                label_load.setText("Выберите параметры для отчёта.");
                                label_load.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
                                data_rep_table.setPlaceholder(label_load);
                                period_label.setText("Отчёт не существует.");
                                Alert alert =new Alert(Alert.AlertType.ERROR , "Test");
                                alert.setTitle("Отчёт не сформирован!");
                                alert.setHeaderText("За данный период отчёт не был сформирован на сервере!");
                                alert.setContentText("Выберите другой период!");
                                alert.showAndWait().ifPresent(rs -> {if (rs == ButtonType.OK){}});
                            } else {
                                System.out.println(parsed_result_arr.get(0).getPeriod());
                                // Получить период
                                SimpleDateFormat formatStringDate = new SimpleDateFormat("yyyy-MM-dd");
                                Date dateStartOrg = null;
                                Date dateFinishOrg =null;
                                try {
                                    dateStartOrg = formatStringDate.parse(String.valueOf(dateStart));
                                    dateFinishOrg= formatStringDate.parse(String.valueOf(dateFinish));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                String DateStartOrgString = new SimpleDateFormat("dd.MM.yyyy").format(dateStartOrg);
                                String DateFinishOrgString = new SimpleDateFormat("dd.MM.yyyy").format(dateFinishOrg);
                                String period_report_label="Отчёт за период с "+DateStartOrgString+" по "+DateFinishOrgString;

                                period_label.setText(period_report_label);
                                //parsed_result_arr.remove(0); // Удалить период со списка

                                ObservableList<ReportModel> dataReport = FXCollections.observableArrayList(parsed_result_arr);
                                // Заполнение данными таблицы
                                name_company_col.setCellValueFactory(new PropertyValueFactory<>("nameCompany"));
                                name_company_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                                number_appeal_col.setCellValueFactory(new PropertyValueFactory<>("numberAppeal"));
                                number_appeal_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                                name_appeal_col.setCellValueFactory(new PropertyValueFactory<>("nameAppeal"));
                                name_appeal_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                                date_create_col.setCellValueFactory(new PropertyValueFactory<>("dateCreate"));
                                date_create_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                                date_end_col.setCellValueFactory(new PropertyValueFactory<>("dateEnd"));
                                date_end_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                                status_col.setCellValueFactory(new PropertyValueFactory<>("status"));
                                status_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                                cur_step_col.setCellValueFactory(new PropertyValueFactory<>("currentStep"));
                                cur_step_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                                applicant_col.setCellValueFactory(new PropertyValueFactory<>("applicant"));
                                applicant_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                                data_rep_table.setItems(dataReport);
                                download_report_b.setDisable(false);
                                // Вызов события для кнопки скачивания отчета
                                download_report_b.setOnAction(event1 -> {
                                    ReportController reportController=new ReportController();
                                    reportController.Download_report(parsed_result_arr ,dateStart, dateFinish); // Скачивание отчётаа
                                });

                                SetFilter(parsed_result_arr, dateStart, dateFinish); // Установить фильтры
                                autoResizeColumns(data_rep_table); // Выровнять колонки в таблице
                                createContextMenuTable(cookie);

                            }
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

    public void Show_report_org(String cookie){
        // Установить курсор при наведении на кнопки
        generate_report_org_b.setOnMouseEntered(event_mouse -> {
            ((Node) event_mouse.getSource()).setCursor(Cursor.HAND);
        });
        download_report_org_b.setOnMouseEntered(event_mouse -> {
            ((Node) event_mouse.getSource()).setCursor(Cursor.HAND);
        });
        show_rep_org_b.setOnMouseEntered(event_mouse -> {
            ((Node) event_mouse.getSource()).setCursor(Cursor.HAND);
        });
        type_getDoc_box.setOnMouseEntered(event_mouse -> {
            ((Node) event_mouse.getSource()).setCursor(Cursor.HAND);
        });
        choiceFilter_org_box.setOnMouseEntered(event_mouse -> {
            ((Node) event_mouse.getSource()).setCursor(Cursor.HAND);
        });

        // Установить фильтры
        choiceFilter_org_box.setItems(FXCollections.observableArrayList(
                "Фильтр по заявителям","Фильтр по МФЦ", "Фильтр по обращениям"));
        choiceFilter_org_box.getSelectionModel().selectFirst();
        search_org_t.setText("");
        search_org_t.setPromptText("Введите ФИО заявителя");

        // Установить текст для поля таблицы
        Label label_onStart=new Label();
        label_onStart.setText("Выберите параметры для отчёта.");
        label_onStart.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
        data_rep_org_table.setPlaceholder(label_onStart);

        // Установить выбор получения документов
        type_getDoc_box.setItems(FXCollections.observableArrayList(
                "Все","В МФЦ", "По электронной почте"));
        type_getDoc_box.getSelectionModel().selectFirst();
        // Определить тип получения документа и на основании этого сформировать отчёт
        TypeGetDocGenerateReport(cookie);

    }

    public void GenerateReportOrg(String cookie, String typeGetDoc){
        // Выбрать для переключателя фильтров первый элемент
        choiceFilter_org_box.getSelectionModel().selectFirst();
        search_org_t.setText("");
        search_org_t.setPromptText("Введите ФИО заявителя");
        // Получить начальную дату и конечную дату
        LocalDate dateStart=date_start_org_d.getValue();
        LocalDate dateFinish=date_finish_org_d.getValue();
        // Если начальная дата или конечная дата пустые
        if (dateStart==null || dateFinish==null){
            // Вывести предупреждение
            System.out.println("Date is not correct!");
            Alert alert =new Alert(Alert.AlertType.WARNING , "Test");
            alert.setTitle("Вы не ввели дату!");
            alert.setHeaderText("Необходимо ввести дату!");
            alert.setContentText("Введите начало и конец периода.");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK){}
            });
        } else { // Иначе
            // Получить начальную и конечную дату в Unix формате
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = null;
            Date date2 =null;
            try {
                date1 = format.parse(String.valueOf(dateStart));
                date2= format.parse(String.valueOf(dateFinish));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String DateNow_String = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

            Date dateNow_date=null;
            try {
                dateNow_date = format.parse(DateNow_String);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long dateNowLong=dateNow_date.getTime();
            long dateStartLong = date1.getTime(); // Дата начала в UNIX
            long dateFinishLong =date2.getTime(); // Дата окончания в UNIX
            long diffNow_difStart=dateNowLong-dateStartLong;
            long diffNow_difFinish=dateNowLong-dateFinishLong;
            long diffDate=dateFinishLong-dateStartLong; // Разность между конечной датой и начальной датой
            // Если разность 0 или меньше
            if (diffDate<=0 || diffNow_difStart<=0 || diffNow_difFinish<=0){
                // Вывести предупреждение
                System.out.println("Date is not correct!");
                Alert alert =new Alert(Alert.AlertType.ERROR , "Test");
                alert.setTitle("Вы ввели дату некорректно!");
                alert.setHeaderText("Проверьте правильность ввода даты!");
                alert.setContentText("Возможно вы перепутали начало и конец периода. Также отчёт нельзя взять за текущий день.");
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK){}
                });
            } else { // Иначе, если дата в порядке
                data_rep_org_table.setItems(null); // Обнулить данные в таблице
                ProgressIndicator pi = new ProgressIndicator(); // Запуск прогресс индикатора
                VBox box = new VBox(pi);
                box.setAlignment(Pos.CENTER);
                data_rep_org_table.setDisable(true);
                // Установить текст для таблицы
                Label label_load=new Label();
                label_load.setText("Загрузка данных...");
                label_load.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 18));
                data_rep_org_table.setPlaceholder(label_load);
                vbox_rep_org_main.setDisable(true);
                root_org_report.getChildren().add(box);
                // Запустить поток для получения отчёта с сервера
                Task ReportOrgTask = new ReportController.ReportOrgTask(cookie, "",dateStart,dateFinish, typeGetDoc);

                //  После выполнения потока
                ReportOrgTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        box.setDisable(true);
                        pi.setVisible(false);
                        vbox_rep_org_main.setDisable(false);
                        data_rep_org_table.setDisable(false);
                        vbox_org_filter.setDisable(false);

                        // Получение данных с распарсенного поля
                        ArrayList<ReportModel> parsed_result_arr= (ArrayList<ReportModel>) ReportOrgTask.getValue();
                        if (parsed_result_arr==null){
                            vbox_org_filter.setDisable(true);
                            download_report_org_b.setDisable(true);

                            Label label_load=new Label();
                            label_load.setText("Выберите параметры для отчёта.");
                            label_load.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
                            data_rep_org_table.setPlaceholder(label_load);
                            period_org_label.setText("Отчёт не существует.");
                            Alert alert =new Alert(Alert.AlertType.ERROR , "Test");
                            alert.setTitle("Отчёт не сформирован!");
                            alert.setHeaderText("За данный период отчёт не сформирован на сервере!");
                            alert.setContentText("Выберите другой период! Или измените другие параметры запроса.");
                            alert.showAndWait().ifPresent(rs -> {if (rs == ButtonType.OK){}});
                        } else {
                            System.out.println(parsed_result_arr.get(0).getPeriod());
                            // Получить период
                            SimpleDateFormat formatStringDate = new SimpleDateFormat("yyyy-MM-dd");
                            Date dateStartOrg = null;
                            Date dateFinishOrg =null;
                            try {
                                dateStartOrg = formatStringDate.parse(String.valueOf(dateStart));
                                dateFinishOrg= formatStringDate.parse(String.valueOf(dateFinish));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String DateStartOrgString = new SimpleDateFormat("dd.MM.yyyy").format(dateStartOrg);
                            String DateFinishOrgString = new SimpleDateFormat("dd.MM.yyyy").format(dateFinishOrg);
                            String period_report_label="Отчёт за период с "+DateStartOrgString+" по "+DateFinishOrgString;
                            period_org_label.setText(period_report_label);

                            ObservableList<ReportModel> dataReport = FXCollections.observableArrayList(parsed_result_arr);
                            // Заполнение данными таблицы
                            name_company_org_col.setCellValueFactory(new PropertyValueFactory<>("nameCompany"));
                            name_company_org_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                            number_appeal_org_col.setCellValueFactory(new PropertyValueFactory<>("numberAppeal"));
                            number_appeal_org_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                            name_appeal_org_col.setCellValueFactory(new PropertyValueFactory<>("nameAppeal"));
                            name_appeal_org_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                            date_create_org_col.setCellValueFactory(new PropertyValueFactory<>("dateCreate"));
                            date_create_org_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                            date_end_org_col.setCellValueFactory(new PropertyValueFactory<>("dateEnd"));
                            date_end_org_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                            status_org_col.setCellValueFactory(new PropertyValueFactory<>("status"));
                            status_org_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                            cur_step_org_col.setCellValueFactory(new PropertyValueFactory<>("currentStep"));
                            cur_step_org_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                            applicant_org_col.setCellValueFactory(new PropertyValueFactory<>("applicant"));
                            applicant_org_col.setCellFactory(TextFieldTableCell.<ReportModel>forTableColumn());

                            data_rep_org_table.setItems(dataReport);
                            download_report_org_b.setDisable(false);
                            // Вызов события для кнопки скачивания отчета
                            download_report_org_b.setOnAction(event1 -> {
                                ReportController reportController=new ReportController();
                                reportController.Download_report(parsed_result_arr ,dateStart, dateFinish); // Скачивание отчётаа
                            });

                            SetFilterOrg(parsed_result_arr, dateStart, dateFinish); // Установить фильтры
                            autoResizeColumns(data_rep_org_table); // Выровнять колонки в таблице
                            createContextMenuTableOrg(cookie);
                            System.out.println("Result of org: " +parsed_result_arr.size());
                            if (parsed_result_arr.size()>=200) ShowWarningIfOutRangeOrg();


                        }
                    }
                });

                // Запуск потока
                Thread reportOrgThread = new Thread(ReportOrgTask);
                reportOrgThread.setDaemon(true);
                reportOrgThread.start();
            }
        }

    }

    public void ShowWarningIfOutRangeOrg(){
        // Вывести предупреждение
        Alert alert =new Alert(Alert.AlertType.WARNING , "Test");
        alert.setTitle("Превышено ограничение сервера!");
        alert.setHeaderText("Количество обращений больше 200!");
        alert.setContentText("Выберите период поменьше.");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK){}
        });
    }

    public void createContextMenuFilterField(String typeFilter, ArrayList<ReportModel> dataReportList, LocalDate dateStart, LocalDate dateFinish){
        search_t.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
        // Create ContextMenu
        ContextMenu contextMenu = new ContextMenu();

        MenuItem copyTextSearch = new MenuItem("Скопировать");
        copyTextSearch.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                String copyTextSearchBuffer=search_t.getText();
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(copyTextSearchBuffer);
                clipboard.setContent(content);
            }
        });
        MenuItem pasteTextSearch = new MenuItem("Вставить");
        pasteTextSearch.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                String clipboardText = clipboard.getString();
                search_t.setText(clipboardText);
                FilterByButton(typeFilter, dataReportList, dateStart, dateFinish);
            }
        });
        MenuItem clearSearchText = new MenuItem("Очистить поле");
        clearSearchText.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                search_t.setText("");
                setTableByDefault(dataReportList, dateStart, dateFinish);
            }
        });

        // When user right-click on Circle
        /*search_t.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

            @Override
            public void handle(ContextMenuEvent event) {

                contextMenu.show(search_t, event.getScreenX(), event.getScreenY());
            }
        });*/

        // Add MenuItem to ContextMenu
        contextMenu.getItems().addAll(copyTextSearch, pasteTextSearch, clearSearchText);
        search_t.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(search_t, e.getScreenX(), e.getScreenY());
            } else {
                contextMenu.hide();
            }
        });
    }

    public void createContextMenuTable(String cookie){
        // Create ContextMenu
        ContextMenu contextMenu = new ContextMenu();

        MenuItem openAppealInfo = new MenuItem("Открыть обращение");
        openAppealInfo.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("Number Appeal:");
                ReportModel reportModel = data_rep_table.getSelectionModel().getSelectedItem(); // Получить выделенный элемент
                System.out.println("Number appeal selected item: "+reportModel.getNumberAppeal());
                String numberAppeal=reportModel.getNumberAppeal();
                Show_Appeal_Info(cookie, numberAppeal);
            }
        });
        MenuItem copyMfcOrg = new MenuItem("Скопировать название МФЦ");
        copyMfcOrg.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                ReportModel reportModel = data_rep_table.getSelectionModel().getSelectedItem(); // Получить выделенный элемент
                String copyMfcBuffer=reportModel.getNameCompany();
                System.out.println("Companyselected item: "+copyMfcBuffer);
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(copyMfcBuffer);
                clipboard.setContent(content);
            }
        });
        MenuItem copyNumberAppeal = new MenuItem("Скопировать номер обращения");
        copyNumberAppeal.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                ReportModel reportModel = data_rep_table.getSelectionModel().getSelectedItem(); // Получить выделенный элемент
                String copyNumberAppealBuffer=reportModel.getNumberAppeal();
                System.out.println("Number appeal selected item: "+copyNumberAppealBuffer);
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(copyNumberAppealBuffer);
                clipboard.setContent(content);
            }
        });
        MenuItem copyApplicant = new MenuItem("Скопировать заявителя (заявителей)");
        copyApplicant.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                ReportModel reportModel = data_rep_table.getSelectionModel().getSelectedItem(); // Получить выделенный элемент
                String copyApplicantBuffer=reportModel.getApplicant();
                System.out.println("Applicant appeal selected item: "+copyApplicantBuffer);
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(copyApplicantBuffer);
                clipboard.setContent(content);
            }
        });

       /*
        // When user right-click on Circle
        data_rep_table.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

            @Override
            public void handle(ContextMenuEvent event) {

                contextMenu.show(data_rep_table, event.getScreenX(), event.getScreenY());
            }
        });*/

        // Add MenuItem to ContextMenu
        contextMenu.getItems().addAll(openAppealInfo,copyMfcOrg, copyNumberAppeal, copyApplicant);
        data_rep_table.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(data_rep_table, e.getScreenX(), e.getScreenY());
            } else {
                contextMenu.hide();
            }
        });
    }

    public void createContextMenuFilterFieldOrg(String typeFilter, ArrayList<ReportModel> dataReportList, LocalDate dateStart, LocalDate dateFinish){
        search_org_t.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
        // Create ContextMenu
        ContextMenu contextMenu = new ContextMenu();

        MenuItem copyTextSearch = new MenuItem("Скопировать");
        copyTextSearch.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                String copyTextSearchBuffer=search_org_t.getText();
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(copyTextSearchBuffer);
                clipboard.setContent(content);
            }
        });
        MenuItem pasteTextSearch = new MenuItem("Вставить");
        pasteTextSearch.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                String clipboardText = clipboard.getString();
                search_org_t.setText(clipboardText);
                FilterByButtonOrg(typeFilter, dataReportList, dateStart, dateFinish);
            }
        });
        MenuItem clearSearchText = new MenuItem("Очистить поле");
        clearSearchText.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                search_org_t.setText("");
                setTableByDefaultOrg(dataReportList, dateStart, dateFinish);
            }
        });


        // Add MenuItem to ContextMenu
        contextMenu.getItems().addAll(copyTextSearch, pasteTextSearch, clearSearchText);
        search_org_t.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(search_t, e.getScreenX(), e.getScreenY());
            } else {
                contextMenu.hide();
            }
        });
    }

    public void createContextMenuTableOrg(String cookie){
        // Create ContextMenu
        ContextMenu contextMenu = new ContextMenu();

        MenuItem openAppealInfo = new MenuItem("Открыть обращение");
        openAppealInfo.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("Number Appeal:");
                ReportModel reportModel = data_rep_org_table.getSelectionModel().getSelectedItem(); // Получить выделенный элемент
                System.out.println("Number appeal selected item: "+reportModel.getNumberAppeal());
                String numberAppeal=reportModel.getNumberAppeal();
                Show_Appeal_Info(cookie, numberAppeal);
            }
        });
        MenuItem copyMfcOrg = new MenuItem("Скопировать название МФЦ");
        copyMfcOrg.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                ReportModel reportModel = data_rep_org_table.getSelectionModel().getSelectedItem(); // Получить выделенный элемент
                String copyMfcBuffer=reportModel.getNameCompany();
                System.out.println("Companyselected item: "+copyMfcBuffer);
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(copyMfcBuffer);
                clipboard.setContent(content);
            }
        });
        MenuItem copyNumberAppeal = new MenuItem("Скопировать номер обращения");
        copyNumberAppeal.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                ReportModel reportModel = data_rep_org_table.getSelectionModel().getSelectedItem(); // Получить выделенный элемент
                String copyNumberAppealBuffer=reportModel.getNumberAppeal();
                System.out.println("Number appeal selected item: "+copyNumberAppealBuffer);
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(copyNumberAppealBuffer);
                clipboard.setContent(content);
            }
        });
        MenuItem copyApplicant = new MenuItem("Скопировать заявителя (заявителей)");
        copyApplicant.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                ReportModel reportModel = data_rep_org_table.getSelectionModel().getSelectedItem(); // Получить выделенный элемент
                String copyApplicantBuffer=reportModel.getApplicant();
                System.out.println("Applicant appeal selected item: "+copyApplicantBuffer);
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(copyApplicantBuffer);
                clipboard.setContent(content);
            }
        });

        // Add MenuItem to ContextMenu
        contextMenu.getItems().addAll(openAppealInfo,copyMfcOrg, copyNumberAppeal, copyApplicant);
        data_rep_org_table.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(data_rep_org_table, e.getScreenX(), e.getScreenY());
            } else {
                contextMenu.hide();
            }
        });
    }

    // Функция для выравнивания колонок в таблице по ширине текста
    public static void autoResizeColumns( TableView<ReportModel> table )
    {
        table.setColumnResizePolicy( TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.getColumns().stream().forEach( (column) ->
        {
            // Получение минимальной ширины
            Text t = new Text( column.getText() );
            double max = t.getLayoutBounds().getWidth();
            for ( int i = 0; i < table.getItems().size(); i++ )
            {
                // Столбцы не должны быть пустыми
                if ( column.getCellData( i ) != null )
                {
                    t = new Text( column.getCellData( i ).toString() ); // Получить текст со столбца
                    double calcwidth = t.getLayoutBounds().getWidth(); // Получить ширину текста
                    // Запомнить новую макс ширину
                    if ( calcwidth > max )
                    {
                        max = calcwidth;
                    }
                }
            }
            // Добавить к максимальной ширине немного пространства
            column.setPrefWidth( max + 12.0d );
        } );
    }
}

