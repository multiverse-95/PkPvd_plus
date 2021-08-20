package pkpvdplus.controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pkpvdplus.appController;
import pkpvdplus.model.ReportModel;
import pkpvdplus.model.SettingsModel;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Контроллер для обработки отчётов
public class ReportController {
    // Класс для потока получения отчёта
    public static class ReportTask extends Task<ArrayList<ReportModel>> {
        private final String cookies; // Куки
        private final String search_text;
        private final LocalDate dateStart; // Дата начала в юникс
        private final LocalDate dateFinish; // Дата окончания в юникс

        public ReportTask(String cookies, String search_text, LocalDate dateStart, LocalDate dateFinish) {
            this.cookies = cookies;
            this.search_text = search_text;
            this.dateStart = dateStart;
            this.dateFinish = dateFinish;
        }
        @Override
        protected ArrayList<ReportModel> call() throws Exception {
            System.out.println("start date: "+dateStart+" finish date: "+dateFinish);
            List<Long> timelist=convertTime(dateStart, dateFinish);// Конвертируем время в UNIX формат
            long dateStartLong=timelist.get(0); // Получение начальной даты
            long dateFinishLong=timelist.get(1); // Получение конечной даты
            System.out.println("start: "+dateStartLong+" finish: "+dateFinishLong);
            String typeListOrdersPeriod="Список заявлений (PRG).jrd"; // Отчёт по заявлениям
            String typeDocPeriod="Список поступивших выдаваемых документов за период.jrd"; // Отчёт по документам
            String typeAppealPeriod="Список обращений.jrd"; // Отчёт по обращениям
            // Получение отчёта по заявлениям с сервера
            String csvListOrdersReport=getReport(dateStartLong,dateFinishLong,cookies, typeListOrdersPeriod);
            // Получение отчёта по документам с сервера
            String csvDocReport=getReport(dateStartLong,dateFinishLong,cookies, typeDocPeriod);
            // Получение отчёта по обращениям с сервера
            String csvAppealReport=getReport(dateStartLong,dateFinishLong,cookies, typeAppealPeriod);
            //String csvReportMain="";
            // Обработка отчётов и получение одного общего отчёта
            ArrayList<ReportModel> reportListFinal= parsingReport(csvListOrdersReport, csvDocReport, csvAppealReport);
            return reportListFinal; // Возвращаем итоговый отчёт
        }
    }

    // Класс для потока скачивания отчёта
    public static class DownloadTaskExcel extends Task<ArrayList<String>> {
        private final ArrayList<ReportModel> dataReportList;
        private File file;

        public DownloadTaskExcel(ArrayList<ReportModel> dataReportList, File file) {
            this.dataReportList = dataReportList;
            this.file=file;

        }
        @Override
        protected ArrayList<String> call() throws Exception {
            // Получение файла Excel
            ArrayList<String> pathFileAndDir=SaveFileExcel(dataReportList, file);
            String success_download="SUCCESS REPORT!!";
            return pathFileAndDir; // Возвращаем путь к файлу
        }
    }
    // Класс для потока скачивания отчёта (Старый формат)
    public static class DownloadTaskExcelOld extends Task<ArrayList<String>> {
        private final ArrayList<ReportModel> dataReportList;
        private File file;

        public DownloadTaskExcelOld(ArrayList<ReportModel> dataReportList, File file) {
            this.dataReportList = dataReportList;
            this.file=file;

        }
        @Override
        protected ArrayList<String> call() throws Exception {
            ArrayList<String> pathFileAndDir=SaveFileExcelOldFormat(dataReportList, file);
            String success_download="SUCCESS REPORT!!";
            return pathFileAndDir; // Возвращаем путь к файлу
        }
    }

    // Функция для конвертирования времени в UNIX формат
    public static List<Long> convertTime(LocalDate dateStart, LocalDate dateFinish) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); // Получение времени по шаблону
        Date date1 = format.parse(String.valueOf(dateStart));
        Date date2 = format.parse(String.valueOf(dateFinish));
        long timestamp1 = date1.getTime(); // Конвертирование даты начала в UNIX формат
        long timestamp2 =date2.getTime(); // Конвертирование даты окончания в UNIX формат
        List<Long> timelist=new ArrayList<Long>(); // Добавление даты начала и даты окончания в список
        timelist.add(timestamp1);
        timelist.add(timestamp2);
        return timelist; // Возвращаем список с датами
    }

    // Функция для получения отчёта с сервера
    public static String getReport(long dateStart, long dateFinish,String cookie, String typeReport) throws IOException {
        Payload_user payload_user = new Payload_user();
        CookieStore httpCookieStore = new BasicCookieStore();
        //payload_user.file ="Список заявлений.jrd";
        // Заполнение json параметрами
        payload_user.file = typeReport;
        payload_user.output="csv";

        ArrayList<Params> params=new ArrayList<Params>();
        Params params1=new Params();
        params1.name="start";
        params1.label="Начало периода";
        params1.type="DATE";
        params1.required=true;
        params1.value=dateStart;
        params.add(params1);

        Params params2=new Params();
        params2.name="end";
        params2.label="Конец периода";
        params2.type="DATE";
        params2.required=true;
        params2.value=dateFinish;
        params.add(params2);

        if (typeReport.equals("Список обращений.jrd")){
            Params params3=new Params();
            params3.name="num";
            params3.label="Код организации";
            params3.type="STRING";
            params3.required=false;
            params3.value="";
            params.add(params3);
        }

        payload_user.params=params;

        String postUrl       = "http://10.42.200.207/api/rs/reports/execute";// Ссылка на сервер
        Gson gson          = new Gson();
        HttpClient httpClient = null;
        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore);
        httpClient = builder.build();
        HttpPost post          = new HttpPost(postUrl);
        StringEntity postingString = new StringEntity(gson.toJson(payload_user), StandardCharsets.UTF_8);// Конвертирование json в строку
        System.out.println(gson.toJson(payload_user));
        post.setEntity(postingString); // Установка json для запроса
        post.setHeader("Content-type", "application/json");
        post.addHeader("Cookie","JSESSIONID="+cookie);
        HttpResponse response = httpClient.execute(post); // Выполнение post запроса на сервер
        System.out.println(response.getStatusLine());
        HttpEntity entity = response.getEntity(); // Получение результата от сервера

        String reportCsv=EntityUtils.toString(entity);
        return reportCsv; // Возвращаем результат в формате csv
    }

    // Функция для обработки отчётов
    public static ArrayList<ReportModel> parsingReport(String csvListOrdersReport, String csvDocReport, String csvAppealReport) throws IOException, CsvValidationException {
        ArrayList<ReportModel> reportListOrders=new ArrayList<ReportModel>(); // Список по заявлениям
        ArrayList<ReportModel> reportListDoc=new ArrayList<ReportModel>(); // Список по документам
        ArrayList<ReportModel> reportListOrdersDoc=new ArrayList<ReportModel>(); // Фильтрованный список по заявлениям и документам
        ArrayList<ReportModel> reportListAppeal=new ArrayList<ReportModel>(); // Список по обращениям
        ArrayList<ReportModel> reportListFinal=new ArrayList<ReportModel>(); // Итоговый список (Фильтрованный список по заявлениям, документам и обращениям)
        //try (CSVReader reader = new CSVReaderBuilder(new FileReader("D:\\recovery\\pk_pvd\\reportPkPvd.csv"))

        // Обрабатываем csv результат для отчёта по заявлениям
        try (CSVReader reader = new CSVReaderBuilder(new StringReader(csvListOrdersReport))
                .withSkipLines(1)           // Пропускаем первую строку
                .build()) {
            String[] lineInArray;
            // Идём по csv
            while ((lineInArray = reader.readNext()) != null) {
                //System.out.println(lineInArray[0]);
                // Получаем период с csv
                //reportListOrders.add(new ReportModel(lineInArray[0], "", "","", "", "","","",""));
                reportListFinal.add(new ReportModel(lineInArray[0], "", "", "", "", "","","",""));
                break; // После получения периода, прерываем чтение данных
                //System.out.println(lineInArray[1] + ","+ lineInArray[2]+","+ lineInArray[3]+","+ lineInArray[12]);
            }
            // Вновь читаем csv
            while ((lineInArray = reader.readNext()) != null) {
                // Берём только данные по ЕГРН
                if (lineInArray[11].equals("Предоставление сведений, содержащихся в ЕГРН, об объектах недвижимости и (или) их правообладателях")){
                    // Записываем нужную информацию в список (Организация, номера обращений, даты создания, статус, заявители)
                    reportListOrders.add(new ReportModel("", lineInArray[1], lineInArray[2],"", lineInArray[3], lineInArray[11],lineInArray[12],"",""));
                }
                //System.out.println(lineInArray[1] + ","+ lineInArray[2]+","+ lineInArray[3]+","+ lineInArray[12]);
            }
        }
        // Идём по отчёту документов в csv
        try (CSVReader reader = new CSVReaderBuilder(new StringReader(csvDocReport))
                .withSkipLines(3)           // Пропускаем первые три строки
                .build()) {
            String[] lineInArray;
            while ((lineInArray = reader.readNext()) != null) {
                // Записываем нужные данные в список дополнительного отчёта
                reportListDoc.add(new ReportModel("", lineInArray[1], lineInArray[2],lineInArray[3], lineInArray[5], lineInArray[6],"","",""));
                //System.out.println(lineInArray[1] + ","+ lineInArray[2]+","+ lineInArray[3]+","+ lineInArray[12]);
            }
        }

        // Идём по отчёту обращений в csv
        try (CSVReader reader = new CSVReaderBuilder(new StringReader(csvAppealReport))
                .withSkipLines(3)           // Пропускаем первые три строки
                .build()) {
            String[] lineInArray;
            while ((lineInArray = reader.readNext()) != null) {
                // Записываем нужные данные в список отчёта по обращениям
                reportListAppeal.add(new ReportModel("", "", lineInArray[6],"", "","","", lineInArray[11],lineInArray[13]));
                //System.out.println(lineInArray[1] + ","+ lineInArray[2]+","+ lineInArray[3]+","+ lineInArray[12]);
            }
        }

        System.out.println("Size orders List: "+reportListOrders.size()+" Size doc list: "+reportListDoc.size());
        // Обрабатываем отчёт по заявлениям и отчёт по документам
        if(reportListDoc.size()>reportListOrders.size()){ // Проверяем, чтобы отчёт по документам был больше отчёта по заявлениям
            for (int i=0; i<reportListOrders.size(); i++){ // Идём по циклу отчёта по заявлениям
                String FilterAppeal = reportListOrders.get(i).getNumberAppeal().toLowerCase(); // Получаем номер обращения
                for (int j=0; j<reportListDoc.size(); j++){ // Идём по циклу отчёта по документам
                    String ListDocAppeal = reportListDoc.get(j).getNumberAppeal().toLowerCase(); // Получаем номер обращения
                    if (FilterAppeal.contains(ListDocAppeal)){ // Сравниваем номер обращения с отчёта по заявлениям и отчёта по документам
                        // Если совпадают, то добавить в фильтрованный отчёт информацию: Организация, номер обращения и т.д.
                        reportListOrdersDoc.add(new ReportModel("",reportListOrders.get(i).getNameCompany(), reportListDoc.get(j).getNumberAppeal(),
                                reportListDoc.get(j).getNameAppeal(),reportListOrders.get(i).getDateCreate(),
                                reportListDoc.get(j).getStatus(),reportListOrders.get(i).getApplicant(), "",""));
                    }
                }
            }
        } else {
            reportListOrdersDoc=null;
        }

        System.out.println("Size OrdersDoc List: "+reportListOrdersDoc.size()+" Size appeal list: "+reportListAppeal.size());
        // Обрабатываем отфильтрованный отчёт с отчётом по обращениям
        if(reportListAppeal.size()>reportListOrdersDoc.size()){ // Проверяем, чтобы отчёт по обращениям был больше отчёта фильтрованного
            for (int i=0; i<reportListOrdersDoc.size(); i++){ // Идём по циклу фильтрованного отчёта
                String FilterNumberAppeal = reportListOrdersDoc.get(i).getNumberAppeal().toLowerCase(); // Получаем номер обращения
                for (int j=0; j<reportListAppeal.size(); j++){ // Идём по циклу отчёта по обращениям
                    String ListNumberAppeal = reportListAppeal.get(j).getNumberAppeal().toLowerCase(); // Получаем номер обращения
                    if (FilterNumberAppeal.contains(ListNumberAppeal)){ // Сравниваем номер обращения с фильтрованного отчёта и отчёта по обращениям
                        // Если совпадают, то добавить в финальный отчёт информацию: Организация, номер обращения и т.д.
                        reportListFinal.add(new ReportModel("",reportListOrdersDoc.get(i).getNameCompany(), reportListOrdersDoc.get(i).getNumberAppeal(),
                                reportListOrdersDoc.get(i).getNameAppeal(),reportListOrdersDoc.get(i).getDateCreate(),
                                reportListOrdersDoc.get(i).getStatus(),reportListOrdersDoc.get(i).getApplicant(),
                                reportListAppeal.get(j).getDateEnd(), reportListAppeal.get(j).getCurrentStep()));
                    }
                }
            }
        } else {
            reportListFinal=null;
        }

        System.out.println("Size Final List: "+reportListFinal.size());

        /*ArrayList<String> columnOrder=new ArrayList<String>();
        for (int i=0; i<reportListFinal.size(); i++){
            columnOrder.add(reportListFinal.get(i).getNumberAppeal());
        }

        Set<String> uniqueReport = new HashSet<String>(columnOrder);
        System.out.println("Unique report count: " + uniqueReport.size());

        System.out.println("\nHere are the duplicate elements from list : " + findDuplicates(columnOrder));*/


        return reportListFinal; // Возвращаем итоговый отчёт
    }

    public static Set<String> findDuplicates(List<String> listContainingDuplicates) {

        final Set<String> setToReturn = new HashSet<String>();
        final Set<String> set1 = new HashSet<String>();

        for (String yourInt : listContainingDuplicates) {
            if (!set1.add(yourInt)) {
                setToReturn.add(yourInt);
            }
        }
        return setToReturn;
    }

    // Функция для загрузки отчета
    public void Download_report(ArrayList<ReportModel> dataReportList, LocalDate dateStart, LocalDate datefinish) {

        //System.out.println(text_test);
        // Создаем экземпляр класса FileChooser
        FileChooser fileChooser = new FileChooser();

        String lastPathDirectory=getLastDirectory();
        if (!lastPathDirectory.equals("")){
            fileChooser.setInitialDirectory(new File(lastPathDirectory));
        }
        // Устанавливаем список расширений для файла
        //fileChooser.setInitialFileName("report_pkpvd");// Устанавливаем название для файла
        fileChooser.setInitialFileName("report_pkpvd_"+dateStart.getDayOfMonth()+"."+dateStart.getMonthValue()+"."+dateStart.getYear()+"-"+
                datefinish.getDayOfMonth()+"."+datefinish.getMonthValue()+"."+datefinish.getYear());// Устанавливаем название для файла
        // Список расширений для Excel
        FileChooser.ExtensionFilter extFilterExcel = new FileChooser.ExtensionFilter("Excel file (*.xlsx)", "*.xlsx");
        // Список расширений для Excel (старый формат)
        FileChooser.ExtensionFilter extFilterExcelOld = new FileChooser.ExtensionFilter("Excel file (old format) (*.xls)", "*.xls");

        // Добавляем список расширений
        fileChooser.getExtensionFilters().add(extFilterExcel);
        fileChooser.getExtensionFilters().add(extFilterExcelOld);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/pkpvdplus/view/app.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parent root = loader.getRoot();
        appController AppController = loader.getController();
        Stage stage = new Stage();
        // Показываем диалоговое окно для сохранения файла
        File file = fileChooser.showSaveDialog(stage);
        // Если не нажать кнопка "Отмена"
        if (fileChooser.getSelectedExtensionFilter()!=null){
            // Если выбрано расширение для Excel
            if (fileChooser.getSelectedExtensionFilter().getExtensions().toString().equals("[*.xlsx]")){
                System.out.println("SELECTED XLSX");
                // Если файл не пустой
                if(file != null){
                    // Сохраняем файл

                    // Окно, которое уведомляет о загрузке файла
                    ButtonType ok_but = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE); // Создание кнопки "Открыть отчёт"
                    ButtonType cancel_but = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE); // Создание кнопки "Открыть папку с отчётом"
                    Alert alert =new Alert(Alert.AlertType.INFORMATION , "Test", ok_but, cancel_but);
                    alert.setTitle("Загрузка отчёта...");
                    alert.setHeaderText("Идёт загрузка отчёта, подождите...");
                    alert.setContentText("После загрузки появится уведомление!");
                    alert.show();
                    // Скрываем кнопки в окне, чтобы пользователь случайно не нажал их
                    Button okButton =( Button ) alert.getDialogPane().lookupButton( ok_but );
                    Button cancelButton = ( Button ) alert.getDialogPane().lookupButton( cancel_but );
                    okButton.setVisible(false);
                    cancelButton.setVisible(false);

                    Task DownloadTaskExcel =  new DownloadTaskExcel (dataReportList, file);

                    //  После выполнения потока
                    DownloadTaskExcel.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent event) {

                            cancelButton.fire(); // Закрываем окно с загрузкой
                            // Получение директорий, вызов функции скачивания отчёта
                            ArrayList<String> pathFileAndDir= (ArrayList<String>) DownloadTaskExcel.getValue();
                            // Получаем путь для файла
                            String pathToFile=pathFileAndDir.get(0);
                            String absolutePathToFile=pathFileAndDir.get(1);

                            System.out.println(pathToFile +" "+absolutePathToFile);
                            // Отображаем окно с выбором: Открыть отчёт или открыть папку с отчётом
                            ButtonType openReport = new ButtonType("Открыть отчёт", ButtonBar.ButtonData.OK_DONE); // Создание кнопки "Открыть отчёт"
                            ButtonType openDir = new ButtonType("Открыть папку с отчётом", ButtonBar.ButtonData.CANCEL_CLOSE); // Создание кнопки "Открыть папку с отчётом"
                            Alert alert =new Alert(Alert.AlertType.INFORMATION , "Test", openReport, openDir);
                            alert.setTitle("Загрузка завершена!"); // Название предупреждения
                            alert.setHeaderText("Отчёт загружен!"); // Текст предупреждения
                            alert.setContentText("Отчёт доступен в папке: "+pathToFile);
                            // Вызов подтверждения элемента
                            alert.showAndWait().ifPresent(rs -> {
                                if (rs == openReport){ // Если выбрали открыть отчёт
                                    try {
                                        Desktop.getDesktop().open(new File(absolutePathToFile));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else if (rs==openDir){ // Если выбрали открыт папку
                                    try {
                                        Desktop.getDesktop().open(new File(pathToFile));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });

                    // Запуск потока
                    Thread DownloadThread = new Thread(DownloadTaskExcel);
                    DownloadThread.setDaemon(true);
                    DownloadThread.start();

                }
            } // Иначе если выбрано расширение для старого формата Excel
            else if (fileChooser.getSelectedExtensionFilter().getExtensions().toString().equals("[*.xls]")){
                System.out.println("SELECTED XLS");
                // Если файл не пустой
                if(file != null){
                    // Сохраняем файл

                    // Окно, которое уведомляет о загрузке файла
                    ButtonType ok_but = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE); // Создание кнопки "Открыть отчёт"
                    ButtonType cancel_but = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE); // Создание кнопки "Открыть папку с отчётом"
                    Alert alert =new Alert(Alert.AlertType.INFORMATION , "Test", ok_but, cancel_but);
                    alert.setTitle("Загрузка отчёта...");
                    alert.setHeaderText("Идёт загрузка отчёта, подождите...");
                    alert.setContentText("После загрузки появится уведомление!");
                    alert.show();
                    Button okButton =( Button ) alert.getDialogPane().lookupButton( ok_but );
                    Button cancelButton = ( Button ) alert.getDialogPane().lookupButton( cancel_but );
                    // Скрываем кнопки в окне, чтобы пользователь случайно не нажал их
                    okButton.setVisible(false);
                    cancelButton.setVisible(false);

                    Task DownloadTaskExcelOld =  new DownloadTaskExcelOld (dataReportList, file);
                    //  После выполнения потока
                    DownloadTaskExcelOld.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent event) {
                            cancelButton.fire(); // Закрываем окно с загрузкой
                            // Получение директорий, вызов функции скачивания отчёта
                            ArrayList<String> pathFileAndDir= (ArrayList<String>) DownloadTaskExcelOld.getValue();
                            // Получаем путь для файла
                            String pathToFile=pathFileAndDir.get(0);
                            String absolutePathToFile=pathFileAndDir.get(1);

                            System.out.println(pathToFile +" "+absolutePathToFile);
                            // Отображаем окно с выбором: Открыть отчёт или открыть папку с отчётом
                            ButtonType openReport = new ButtonType("Открыть отчёт", ButtonBar.ButtonData.OK_DONE); // Создание кнопки "Открыть отчёт"
                            ButtonType openDir = new ButtonType("Открыть папку с отчётом", ButtonBar.ButtonData.CANCEL_CLOSE); // Создание кнопки "Открыть папку с отчётом"
                            Alert alert =new Alert(Alert.AlertType.INFORMATION , "Test", openReport, openDir);
                            alert.setTitle("Загрузка завершена!"); // Название предупреждения
                            alert.setHeaderText("Отчёт загружен!"); // Текст предупреждения
                            alert.setContentText("Отчёт доступен в папке: "+pathToFile);
                            // Вызов подтверждения элемента
                            alert.showAndWait().ifPresent(rs -> {
                                if (rs == openReport){ // Если выбрали открыть отчёт
                                    try {
                                        Desktop.getDesktop().open(new File(absolutePathToFile));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else if (rs==openDir){ // Если выбрали открыть папку с отчётом
                                    try {
                                        Desktop.getDesktop().open(new File(pathToFile));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
                    });

                    // Запуск потока
                    Thread DownloadThread = new Thread(DownloadTaskExcelOld);
                    DownloadThread.setDaemon(true);
                    DownloadThread.start();
                }
            }
        }
    }

    // Функция для установки стилей Excel старый формат
    private static HSSFCellStyle createStyleForTitleOld(HSSFWorkbook workbook) {
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    // Функция сохранения файла в Excel старый формат
    public static ArrayList<String> SaveFileExcelOldFormat(ArrayList<ReportModel> dataReportList, File file) throws IOException {
        // Создание книги Excel
        HSSFWorkbook workbook = new HSSFWorkbook();
        // Создание листа
        HSSFSheet sheet = workbook.createSheet("Отчёт по заявлениям");

        //List<Employee> list = EmployeeDAO.listEmployees();

        int rownum = 0;
        Cell cell;
        Row row;
        // Установка стилей для книги
        HSSFCellStyle style = createStyleForTitleOld(workbook);

        row = sheet.createRow(rownum);

        // Создание столбца "Название организации"
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Наименование организации");
        cell.setCellStyle(style);
        // Создание столбца "Номер обращения"
        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Номер обращения");
        cell.setCellStyle(style);
        // Создание столбца "Наименование обращения"
        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Наименование обращения");
        cell.setCellStyle(style);
        // Создание столбца "Дата создания"
        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Дата создания");
        cell.setCellStyle(style);
        // Создание столбца "Дата окончания"
        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Дата окончания");
        cell.setCellStyle(style);
        // Создание столбца "Статус"
        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("Статус");
        cell.setCellStyle(style);
        // Создание столбца "Текущий шаг"
        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("Текущий шаг");
        cell.setCellStyle(style);
        // Создание столбца "Заявители"
        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue("Заявители");
        cell.setCellStyle(style);


        // Перебор по данным
        for (ReportModel reportModel: dataReportList) {
            //System.out.println(mfc_model.getIdMfc() +"\t" +mfc_model.getNameMfc());
            rownum++;
            row = sheet.createRow(rownum);

            // Запись данных в отчёт
            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(reportModel.getNameCompany());

            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(reportModel.getNumberAppeal());

            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue(reportModel.getNameAppeal());

            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue(reportModel.getDateCreate());

            cell = row.createCell(4, CellType.STRING);
            cell.setCellValue(reportModel.getDateEnd());

            cell = row.createCell(5, CellType.STRING);
            cell.setCellValue(reportModel.getStatus());

            cell = row.createCell(6, CellType.STRING);
            cell.setCellValue(reportModel.getCurrentStep());

            cell = row.createCell(7, CellType.STRING);
            cell.setCellValue(reportModel.getApplicant());

        }
        // Выравнивание столбцов по ширине
        autoSizeColumns(workbook);
        // Создания потока сохранения файла
        FileOutputStream outFile = new FileOutputStream(file);
        // Запись файла
        workbook.write(outFile);
        // Закрытие потока записи
        outFile.close();
        System.out.println("Created file: " + file.getParent());
        // Сохраняем путь, где был сохранён отчёт
        SaveLastPathInfo(file.getParent());
        // Добавляем данные о пути файла в список
        ArrayList<String> pathFileAndDir=new ArrayList<String>();
        pathFileAndDir.add(file.getParent());
        pathFileAndDir.add(file.getAbsolutePath());

        return pathFileAndDir; // Возвращаем путь файл и абсолютный путь
    }

    // Функция для установки стилей Excel
    private static XSSFCellStyle createStyleForTitleNew(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    // Функция сохранения файла в Excel
    public static ArrayList<String> SaveFileExcel(ArrayList<ReportModel> dataReportList, File file) throws IOException {
        // Создание книги Excel
        XSSFWorkbook workbook = new XSSFWorkbook();
        // Создание листа
        XSSFSheet sheet = workbook.createSheet("Отчёт по заявлениям");

        int rownum = 0;
        Cell cell;
        Row row;
        // Установка стилей
        XSSFCellStyle style = createStyleForTitleNew(workbook);

        row = sheet.createRow(rownum);

        // Создание столбца "Название организации"
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Наименование организации");
        cell.setCellStyle(style);
        // Создание столбца "Номер обращения"
        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Номер обращения");
        cell.setCellStyle(style);
        // Создание столбца "Наименование обращения"
        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Наименование обращения");
        cell.setCellStyle(style);
        // Создание столбца "Дата создания"
        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Дата создания");
        cell.setCellStyle(style);
        // Создание столбца "Дата окончания"
        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Дата окончания");
        cell.setCellStyle(style);
        // Создание столбца "Статус"
        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("Статус");
        cell.setCellStyle(style);
        // Создание столбца "Текущий шаг"
        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("Текущий шаг");
        cell.setCellStyle(style);
        // Создание столбца "Заявители"
        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue("Заявители");
        cell.setCellStyle(style);




        // Перебор по данным
        for (ReportModel reportModel : dataReportList) {
            //System.out.println(mfc_model.getIdMfc() +"\t" +mfc_model.getNameMfc());
            rownum++;
            row = sheet.createRow(rownum);

            // Запись данных в отчёт
            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(reportModel.getNameCompany());

            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(reportModel.getNumberAppeal());

            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue(reportModel.getNameAppeal());

            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue(reportModel.getDateCreate());

            cell = row.createCell(4, CellType.STRING);
            cell.setCellValue(reportModel.getDateEnd());

            cell = row.createCell(5, CellType.STRING);
            cell.setCellValue(reportModel.getStatus());

            cell = row.createCell(6, CellType.STRING);
            cell.setCellValue(reportModel.getCurrentStep());

            cell = row.createCell(7, CellType.STRING);
            cell.setCellValue(reportModel.getApplicant());

        }
        // Выравнивание столбцов по ширине
        autoSizeColumns(workbook);
        // Создания потока сохранения файла
        FileOutputStream outFile = new FileOutputStream(file);
        // Запись файла
        workbook.write(outFile);
        // Закрытие потока записи
        outFile.close();


        System.out.println("Created file: " + file.getParent());
        // Сохранение последнего пути файла
        SaveLastPathInfo(file.getParent());

        ArrayList<String> pathFileAndDir=new ArrayList<String>();
        pathFileAndDir.add(file.getParent());
        pathFileAndDir.add(file.getAbsolutePath());

        return pathFileAndDir; // Возвращение пути и абсолютного пути файла

    }

    // Функция для автоматического выравнивания столбцов по ширине содержимого
    public static void autoSizeColumns(Workbook workbook) {
        int numberOfSheets = workbook.getNumberOfSheets(); // Получаем кол-во листов
        for (int i = 0; i < numberOfSheets; i++) { // Идём по кол-ву листов
            Sheet sheet = workbook.getSheetAt(i); // Получаем лист
            if (sheet.getPhysicalNumberOfRows() > 0) { // Если столбцов больше 0
                Row row = sheet.getRow(sheet.getFirstRowNum()); // Получить первую строку
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) { // Идём по строкам и выравниваем по ширине
                    Cell cell = cellIterator.next();
                    int columnIndex = cell.getColumnIndex();
                    sheet.autoSizeColumn(columnIndex);
                }
            }
        }
    }

    // Функция для сохранения последнего пути файла
    public static void SaveLastPathInfo(String lastPathToFile) throws IOException {
        // Путь к файлу
        File fileJson = new File("C:\\pkpvdplus\\settingsPVD.json");
        // Проверяем, существует ли файл
        if(!fileJson.exists())
        {
            System.out.println("No file!");
        } else {
            System.out.println("yes file!");
            // Читаем в кодировке UTF-8
            JsonParser parser = new JsonParser();
            JsonElement jsontree = null;
            try {
                jsontree = parser.parse(new BufferedReader(new InputStreamReader(new FileInputStream("C:\\pkpvdplus\\settingsPVD.json"), StandardCharsets.UTF_8)));
                //jsontree = parser.parse(new FileReader("C:\\pkpvdplus\\settingsPVD.json"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // Парсим нужные данные
            JsonObject jsonObject = jsontree.getAsJsonObject();
            String login=jsonObject.get("login").getAsString();
            String password=jsonObject.get("password").getAsString();
            String cookie=jsonObject.get("cookie").getAsString();
            boolean isCheckBoxSel=jsonObject.get("isCheckBoxSel").getAsBoolean();
            SettingsModel settingsModel=new SettingsModel(login, password, cookie, lastPathToFile, isCheckBoxSel);
            settingsModel.setLogin(login);
            settingsModel.setPassword(password);
            settingsModel.setCookie(cookie);
            settingsModel.setLastPathToFile(lastPathToFile);
            settingsModel.setCheckBoxSel(isCheckBoxSel);
            // Сохраняем путь к файлу
            Gson gson = new Gson();
            File f = new File("C:\\pkpvdplus");
            try{
                if(f.mkdir()) {
                    System.out.println("Directory Created");
                } else {
                    System.out.println("Directory is not created");
                }
            } catch(Exception e){
                e.printStackTrace();
            }

            String content=gson.toJson(settingsModel);
            // Записываем в кодировке UTF-8
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileJson), StandardCharsets.UTF_8));
            out.write(content);
            out.close();
            /*try {
                FileWriter fileWriter = null;
                fileWriter = new FileWriter(fileJson);
                fileWriter.write(content);
                fileWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        }
    }

    // Функция для получения последней директории, где был сохранён отчёт
    public String getLastDirectory(){
        String lastPathToFile="";
        // Читаем файл
        File fileJson = new File("C:\\pkpvdplus\\settingsPVD.json");

        if(!fileJson.exists())
        {
            System.out.println("No file!");
        } else {
            System.out.println("yes file!");
            // Парсим нужные данные
            JsonParser parser = new JsonParser();
            JsonElement jsontree = null;
            try {
                jsontree = parser.parse(new BufferedReader(new InputStreamReader(new FileInputStream("C:\\pkpvdplus\\settingsPVD.json"), StandardCharsets.UTF_8)));
                //jsontree = parser.parse(new FileReader("C:\\pkpvdplus\\settingsPVD.json"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // Получаем путь к файлу
            JsonObject jsonObject = jsontree.getAsJsonObject();
            lastPathToFile = jsonObject.get("lastPathToFile").getAsString();
        }
        return lastPathToFile;
    }

    // Класс для для payload
    static class Payload_user
    {
        public String file;
        public String output;
        public ArrayList<Params> params;
    }

    static class Params
    {
        public String name;
        public String label;
        public String type;
        public boolean required;
        public Object value;
    }


}