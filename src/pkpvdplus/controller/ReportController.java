package pkpvdplus.controller;

import com.google.gson.*;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
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
import org.apache.http.client.methods.HttpGet;
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
import pkpvdplus.model.MFCsInfoModel;
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
            String typeListOrdersPeriod="Список заявлений.jrd"; // Отчёт по заявлениям
            String typeAppealPeriod="Список обращений.jrd"; // Отчёт по обращениям
            // Получение отчёта по заявлениям с сервера
            String csvListOrdersReport=getReport(dateStartLong,dateFinishLong,cookies, typeListOrdersPeriod);
            // Получение отчёта по обращениям с сервера
            String csvAppealReport=getReport(dateStartLong,dateFinishLong,cookies, typeAppealPeriod);
            //String csvReportMain="";
            // Обработка отчётов и получение одного общего отчёта
            ArrayList<ReportModel> reportListFinal= parsingReport(csvListOrdersReport, csvAppealReport);
            return reportListFinal; // Возвращаем итоговый отчёт
        }
    }

    // Класс для потока получения отчёта
    public static class ReportOrgTask extends Task<ArrayList<ReportModel>> {
        private final String cookies; // Куки
        private final String search_text;
        private final LocalDate dateStart; // Дата начала в юникс
        private final LocalDate dateFinish; // Дата окончания в юникс
        private final String typeGetDoc;

        public ReportOrgTask(String cookies, String search_text, LocalDate dateStart, LocalDate dateFinish, String typeGetDoc) {
            this.cookies = cookies;
            this.search_text = search_text;
            this.dateStart = dateStart;
            this.dateFinish = dateFinish;
            this.typeGetDoc=typeGetDoc;
        }
        @Override
        protected ArrayList<ReportModel> call() throws Exception {
            ArrayList<MFCsInfoModel> MFCsList;
            System.out.println("start date: "+dateStart+" finish date: "+dateFinish);
            String dateStartS = convertTimeOrgInput(String.valueOf(dateStart));
            String dateFinishS = convertTimeOrgInput(String.valueOf(dateFinish));
            ArrayList<ReportModel> reportListOrg=new ArrayList<ReportModel>();
            // Получение отчёта по заявлениям с сервера
            switch (typeGetDoc){
                case "ALL":
                    System.out.println("Chosen ALL DOC");
                    String jsonOrgALL= getReportOrgTypeDocALL(cookies, dateStartS, dateFinishS);
                    MFCsList=getMFCs(cookies);
                    reportListOrg= parsingReportOrg(jsonOrgALL, MFCsList);
                    break;
                case "MFC":
                    System.out.println("Chosen MFC DOC");
                    String jsonOrgMFC= getReportOrgTypeDocMFC(cookies, dateStartS, dateFinishS);
                    MFCsList=getMFCs(cookies);
                    reportListOrg= parsingReportOrg(jsonOrgMFC, MFCsList);
                    break;
                case "EMAIL":
                    System.out.println("Chosen EMAIL DOC");
                    String jsonOrgEMAIL= getReportOrgTypeDocEMAIL(cookies, dateStartS, dateFinishS);
                    MFCsList=getMFCs(cookies);
                    reportListOrg= parsingReportOrg(jsonOrgEMAIL, MFCsList);
                    break;
            }

            return reportListOrg; // Возвращаем итоговый отчёт
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


    // Функция для конвертирования времени в нужный формат (Для отчёта по юридическим лицам)
    public static String convertTimeOrgInput(String dateInputOrg) throws ParseException {
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        Date dateServerD = oldDateFormat.parse(dateInputOrg);
        String resultDateCorrect = newDateFormat.format(dateServerD);
        return resultDateCorrect;

    }

    // Функция для конвертирования времени в нужный формат (Для отчёта по юридическим лицам)
    public static String convertTimeOrg(String dateServerPVD) throws ParseException {
        SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
        SimpleDateFormat newDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        Date dateServerD = oldDateFormat.parse(dateServerPVD);
        String resultDateCorrect = newDateFormat.format(dateServerD);
        return resultDateCorrect;

    }

    // Функция для получения отчёта с сервера (Все заявители)
    public static String getReport(long dateStart, long dateFinish,String cookie, String typeReport) throws IOException {
        Payload_report payload_report = new Payload_report();
        CookieStore httpCookieStore = new BasicCookieStore();
        //payload_user.file ="Список заявлений.jrd";
        // Заполнение json параметрами
        payload_report.file = typeReport;
        payload_report.output="csv";

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
        // Если тип отчёта Список обращений.jrd, то добавляем ещё одни параметры для запроса
        if (typeReport.equals("Список обращений.jrd")){
            Params params3=new Params();
            params3.name="num";
            params3.label="Код организации";
            params3.type="STRING";
            params3.required=false;
            params3.value="";
            params.add(params3);
        }

        payload_report.params=params; // Заполняем все параметры

        String postUrl       = "http://10.42.200.207/api/rs/reports/execute";// Ссылка на сервер
        Gson gson          = new Gson();
        HttpClient httpClient = null;
        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore);
        httpClient = builder.build();
        HttpPost post          = new HttpPost(postUrl);
        StringEntity postingString = new StringEntity(gson.toJson(payload_report), StandardCharsets.UTF_8);// Конвертирование json в строку
        System.out.println(gson.toJson(payload_report));
        post.setEntity(postingString); // Установка json для запроса
        post.setHeader("Content-type", "application/json");
        post.addHeader("Cookie","JSESSIONID="+cookie);
        HttpResponse response = httpClient.execute(post); // Выполнение post запроса на сервер
        System.out.println(response.getStatusLine());
        HttpEntity entity = response.getEntity(); // Получение результата от сервера

        String reportCsv=EntityUtils.toString(entity);
        return reportCsv; // Возвращаем результат в формате csv
    }

    // Функция для получения отчёта с сервера (Только юридические лица)
    public static String getReportOrgTypeDocALL(String cookie, String dateStart, String dateFinish) throws IOException {
        CookieStore httpCookieStore = new BasicCookieStore();
        // Заполнение json параметрами
        String JsonToServer="{\"usedFields\":[\"subjects.subjectType\",\"createEvent.dateWhen\",\"name\"],\"rules\":[{\"rules\":[{\"id\":\"8ab9b998-0123-4456-b89a-b17c0ccf7e9f\"," +
                "\"field\":\"subjects.subjectType\",\"type\":\"cls\",\"input\":\"cls\",\"operator\":\"equal\",\"values\":[{\"type\":\"cls\",\"value\":\"007002001000\"}]}," +
                "{\"id\":\"a8aa9ba9-cdef-4012-b456-717bc9b25ac4\",\"field\":\"createEvent.dateWhen\",\"type\":\"datetime\",\"input\":\"datetime\",\"operator\":\"between\"," +
                "\"values\":[{\"type\":\"datetime\",\"value\":\""+dateStart+"\"},{\"type\":\"datetime\",\"value\":\""+dateFinish+"\"}]}," +
                "{\"rules\":[{\"id\":\"9b9a8ba9-89ab-4cde-b012-317bc9b49af7\",\"field\":\"name\",\"type\":\"text\",\"input\":\"text\",\"operator\":\"contains\"," +
                "\"values\":[{\"type\":\"text\",\"value\":\"Предоставление сведений об объекте недвижимости\"}]},{\"id\":\"b98b8a9b-cdef-4012-b456-717c12247e04\"," +
                "\"field\":\"name\",\"type\":\"text\",\"input\":\"text\",\"operator\":\"contains\",\"values\":[{\"type\":\"text\"," +
                "\"value\":\"Предоставление сведений о правообладателе\"}]}],\"condition\":\"OR\"}],\"condition\":\"AND\"},{\"rules\":[{\"id\":\"a8a88889-4567-489a-bcde-f17c0cd528f7\"," +
                "\"field\":\"subjects.subjectType\",\"type\":\"cls\",\"input\":\"cls\",\"operator\":\"equal\",\"values\":[{\"type\":\"cls\",\"value\":\"007002004000\"}]}," +
                "{\"id\":\"989a9b88-0123-4456-b89a-b17c0cd56cd9\",\"field\":\"createEvent.dateWhen\",\"type\":\"datetime\",\"input\":\"datetime\",\"operator\":\"between\"," +
                "\"values\":[{\"type\":\"datetime\",\"value\":\""+dateStart+"\"},{\"type\":\"datetime\",\"value\":\""+dateFinish+"\"}]}," +
                "{\"rules\":[{\"id\":\"99b89998-cdef-4012-b456-717c0cd5f703\",\"field\":\"name\",\"type\":\"text\",\"input\":\"text\",\"operator\":\"contains\"," +
                "\"values\":[{\"type\":\"text\",\"value\":\"Предоставление сведений об объекте недвижимости\"}]},{\"id\":\"9aa9aa9a-4567-489a-bcde-f17c12254bb8\"," +
                "\"field\":\"name\",\"type\":\"text\",\"input\":\"text\",\"operator\":\"contains\",\"values\":[{\"type\":\"text\"," +
                "\"value\":\"Предоставление сведений о правообладателе\"}]}],\"condition\":\"OR\"}],\"condition\":\"AND\"}],\"condition\":\"OR\",\"not\":false}";
        String postUrl       = "http://10.42.200.207:9188/query/execute/appeal";// Ссылка на сервер
        HttpClient httpClient = null;
        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore);
        httpClient = builder.build();
        HttpPost post          = new HttpPost(postUrl);
        StringEntity postingString = new StringEntity(JsonToServer, StandardCharsets.UTF_8);// Конвертирование json в строку

        post.setEntity(postingString); // Установка json для запроса
        post.setHeader("Content-type", "application/json");
        post.addHeader("Cookie","JSESSIONID="+cookie);
        post.addHeader("Content-Search","a2VtLXZ2dnxSU19BRE1JTjtSU19TQ0FOO1JTX0RFTElWRVJZO1JTX1JFQ0VQVElPTjtSU19ESVNQO1JTX01BTjs=");
        HttpResponse response = httpClient.execute(post); // Выполнение post запроса на сервер
        System.out.println(response.getStatusLine());
        HttpEntity entity = response.getEntity(); // Получение результата от сервера

        String jsonResult=EntityUtils.toString(entity);

        //System.out.println("Json ORG! - \n"+jsonResult);
        return jsonResult; // Возвращаем результат в формате csv
    }

    // Функция для получения отчёта с сервера (Только юридические лица)
    public static String getReportOrgTypeDocMFC(String cookie, String dateStart, String dateFinish) throws IOException {
        CookieStore httpCookieStore = new BasicCookieStore();
        // Заполнение json параметрами
        String JsonToServer="{\"usedFields\":[\"subjects.subjectType\",\"createEvent.dateWhen\",\"statements.givenRequestDocumentType.typeOutputDoc\",\"name\"]," +
                "\"rules\":[{\"rules\":[{\"id\":\"8ab9b998-0123-4456-b89a-b17c0ccf7e9f\",\"field\":\"subjects.subjectType\",\"type\":\"cls\",\"input\":\"cls\"," +
                "\"operator\":\"equal\",\"values\":[{\"type\":\"cls\",\"value\":\"007002001000\"}]},{\"id\":\"a8aa9ba9-cdef-4012-b456-717bc9b25ac4\"," +
                "\"field\":\"createEvent.dateWhen\",\"type\":\"datetime\",\"input\":\"datetime\",\"operator\":\"between\",\"values\":[{\"type\":\"datetime\"," +
                "\"value\":\""+dateStart+"\"},{\"type\":\"datetime\",\"value\":\""+dateFinish+"\"}]},{\"id\":\"aa8a9a8b-0123-4456-b89a-b17c26474d50\"," +
                "\"field\":\"statements.givenRequestDocumentType.typeOutputDoc\",\"type\":\"cls\",\"input\":\"cls\",\"operator\":\"equal\",\"values\":[{\"type\":\"cls\"," +
                "\"value\":\"785007000000\"}]},{\"rules\":[{\"id\":\"9b9a8ba9-89ab-4cde-b012-317bc9b49af7\",\"field\":\"name\",\"type\":\"text\",\"input\":\"text\"," +
                "\"operator\":\"contains\",\"values\":[{\"type\":\"text\",\"value\":\"Предоставление сведений об объекте недвижимости\"}]}," +
                "{\"id\":\"b98b8a9b-cdef-4012-b456-717c12247e04\",\"field\":\"name\",\"type\":\"text\",\"input\":\"text\",\"operator\":\"contains\"," +
                "\"values\":[{\"type\":\"text\",\"value\":\"Предоставление сведений о правообладателе\"}]}],\"condition\":\"OR\"}],\"condition\":\"AND\"}," +
                "{\"rules\":[{\"id\":\"a8a88889-4567-489a-bcde-f17c0cd528f7\",\"field\":\"subjects.subjectType\",\"type\":\"cls\",\"input\":\"cls\"," +
                "\"operator\":\"equal\",\"values\":[{\"type\":\"cls\",\"value\":\"007002004000\"}]},{\"id\":\"989a9b88-0123-4456-b89a-b17c0cd56cd9\"," +
                "\"field\":\"createEvent.dateWhen\",\"type\":\"datetime\",\"input\":\"datetime\",\"operator\":\"between\",\"values\":[{\"type\":\"datetime\"," +
                "\"value\":\""+dateStart+"\"},{\"type\":\"datetime\",\"value\":\""+dateFinish+"\"}]},{\"id\":\"989ab98b-cdef-4012-b456-717c26477219\"," +
                "\"field\":\"statements.givenRequestDocumentType.typeOutputDoc\",\"type\":\"cls\",\"input\":\"cls\",\"operator\":\"equal\"," +
                "\"values\":[{\"type\":\"cls\",\"value\":\"785007000000\"}]},{\"rules\":[{\"id\":\"99b89998-cdef-4012-b456-717c0cd5f703\",\"field\":\"name\"," +
                "\"type\":\"text\",\"input\":\"text\",\"operator\":\"contains\",\"values\":[{\"type\":\"text\",\"value\":\"Предоставление сведений об объекте недвижимости\"}]}," +
                "{\"id\":\"9aa9aa9a-4567-489a-bcde-f17c12254bb8\",\"field\":\"name\",\"type\":\"text\",\"input\":\"text\",\"operator\":\"contains\"," +
                "\"values\":[{\"type\":\"text\",\"value\":\"Предоставление сведений о правообладателе\"}]}],\"condition\":\"OR\"}],\"condition\":\"AND\"}]," +
                "\"condition\":\"OR\",\"not\":false}";
        String postUrl       = "http://10.42.200.207:9188/query/execute/appeal";// Ссылка на сервер
        HttpClient httpClient = null;
        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore);
        httpClient = builder.build();
        HttpPost post          = new HttpPost(postUrl);
        StringEntity postingString = new StringEntity(JsonToServer, StandardCharsets.UTF_8);// Конвертирование json в строку

        post.setEntity(postingString); // Установка json для запроса
        post.setHeader("Content-type", "application/json");
        post.addHeader("Cookie","JSESSIONID="+cookie);
        post.addHeader("Content-Search","a2VtLXZ2dnxSU19BRE1JTjtSU19TQ0FOO1JTX0RFTElWRVJZO1JTX1JFQ0VQVElPTjtSU19ESVNQO1JTX01BTjs=");
        HttpResponse response = httpClient.execute(post); // Выполнение post запроса на сервер
        System.out.println(response.getStatusLine());
        HttpEntity entity = response.getEntity(); // Получение результата от сервера

        String jsonResult=EntityUtils.toString(entity);

        //System.out.println("Json ORG! - \n"+jsonResult);
        return jsonResult; // Возвращаем результат в формате csv
    }

    // Функция для получения отчёта с сервера (Только юридические лица)
    public static String getReportOrgTypeDocEMAIL(String cookie, String dateStart, String dateFinish) throws IOException {
        CookieStore httpCookieStore = new BasicCookieStore();
        // Заполнение json параметрами
        String JsonToServer="{\"usedFields\":[\"subjects.subjectType\",\"createEvent.dateWhen\",\"statements.givenRequestDocumentType.typeOutputDoc\",\"name\"]," +
                "\"rules\":[{\"rules\":[{\"id\":\"8ab9b998-0123-4456-b89a-b17c0ccf7e9f\",\"field\":\"subjects.subjectType\",\"type\":\"cls\",\"input\":\"cls\"," +
                "\"operator\":\"equal\",\"values\":[{\"type\":\"cls\",\"value\":\"007002001000\"}]},{\"id\":\"a8aa9ba9-cdef-4012-b456-717bc9b25ac4\"," +
                "\"field\":\"createEvent.dateWhen\",\"type\":\"datetime\",\"input\":\"datetime\",\"operator\":\"between\",\"values\":[{\"type\":\"datetime\"," +
                "\"value\":\""+dateStart+"\"},{\"type\":\"datetime\",\"value\":\""+dateFinish+"\"}]},{\"id\":\"aa8a9a8b-0123-4456-b89a-b17c26474d50\"," +
                "\"field\":\"statements.givenRequestDocumentType.typeOutputDoc\",\"type\":\"cls\",\"input\":\"cls\",\"operator\":\"equal\",\"values\":[{\"type\":\"cls\"," +
                "\"value\":\"785003000000\"}]},{\"rules\":[{\"id\":\"9b9a8ba9-89ab-4cde-b012-317bc9b49af7\",\"field\":\"name\",\"type\":\"text\",\"input\":\"text\"," +
                "\"operator\":\"contains\",\"values\":[{\"type\":\"text\",\"value\":\"Предоставление сведений об объекте недвижимости\"}]}," +
                "{\"id\":\"b98b8a9b-cdef-4012-b456-717c12247e04\",\"field\":\"name\",\"type\":\"text\",\"input\":\"text\",\"operator\":\"contains\"," +
                "\"values\":[{\"type\":\"text\",\"value\":\"Предоставление сведений о правообладателе\"}]}],\"condition\":\"OR\"}],\"condition\":\"AND\"}," +
                "{\"rules\":[{\"id\":\"a8a88889-4567-489a-bcde-f17c0cd528f7\",\"field\":\"subjects.subjectType\",\"type\":\"cls\",\"input\":\"cls\"," +
                "\"operator\":\"equal\",\"values\":[{\"type\":\"cls\",\"value\":\"007002004000\"}]},{\"id\":\"989a9b88-0123-4456-b89a-b17c0cd56cd9\"," +
                "\"field\":\"createEvent.dateWhen\",\"type\":\"datetime\",\"input\":\"datetime\",\"operator\":\"between\",\"values\":[{\"type\":\"datetime\"," +
                "\"value\":\""+dateStart+"\"},{\"type\":\"datetime\",\"value\":\""+dateFinish+"\"}]},{\"id\":\"989ab98b-cdef-4012-b456-717c26477219\"," +
                "\"field\":\"statements.givenRequestDocumentType.typeOutputDoc\",\"type\":\"cls\",\"input\":\"cls\",\"operator\":\"equal\",\"values\":[{\"type\":\"cls\"," +
                "\"value\":\"785003000000\"}]},{\"rules\":[{\"id\":\"99b89998-cdef-4012-b456-717c0cd5f703\",\"field\":\"name\",\"type\":\"text\",\"input\":\"text\"," +
                "\"operator\":\"contains\",\"values\":[{\"type\":\"text\",\"value\":\"Предоставление сведений об объекте недвижимости\"}]}," +
                "{\"id\":\"9aa9aa9a-4567-489a-bcde-f17c12254bb8\",\"field\":\"name\",\"type\":\"text\",\"input\":\"text\",\"operator\":\"contains\"," +
                "\"values\":[{\"type\":\"text\",\"value\":\"Предоставление сведений о правообладателе\"}]}],\"condition\":\"OR\"}],\"condition\":\"AND\"}]," +
                "\"condition\":\"OR\",\"not\":false}";
        String postUrl       = "http://10.42.200.207:9188/query/execute/appeal";// Ссылка на сервер
        HttpClient httpClient = null;
        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore);
        httpClient = builder.build();
        HttpPost post          = new HttpPost(postUrl);
        StringEntity postingString = new StringEntity(JsonToServer, StandardCharsets.UTF_8);// Конвертирование json в строку

        post.setEntity(postingString); // Установка json для запроса
        post.setHeader("Content-type", "application/json");
        post.addHeader("Cookie","JSESSIONID="+cookie);
        post.addHeader("Content-Search","a2VtLXZ2dnxSU19BRE1JTjtSU19TQ0FOO1JTX0RFTElWRVJZO1JTX1JFQ0VQVElPTjtSU19ESVNQO1JTX01BTjs=");
        HttpResponse response = httpClient.execute(post); // Выполнение post запроса на сервер
        System.out.println(response.getStatusLine());
        HttpEntity entity = response.getEntity(); // Получение результата от сервера

        String jsonResult=EntityUtils.toString(entity);

        //System.out.println("Json ORG! - \n"+jsonResult);
        return jsonResult; // Возвращаем результат в формате csv
    }

    public static ArrayList<MFCsInfoModel> getMFCs(String cookie) throws IOException {
        CookieStore httpCookieStore = new BasicCookieStore();
        HttpClient httpClient = null;
        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore);
        httpClient = builder.build();
        String getUrl       = "http://10.42.200.207/api/rs/adm/organization";// Сервер авторизации
        HttpGet httpGet = new HttpGet(getUrl);
        httpGet.setHeader("Content-type", "application/json");
        httpGet.addHeader("Cookie","JSESSIONID="+cookie);
        HttpResponse response = httpClient.execute(httpGet); // Выполняем get запрос для проверки действительности куки

        HttpEntity entity = response.getEntity();
        String result_of_req = EntityUtils.toString(entity); // Получаем результат запроса

        int status_code= response.getStatusLine().getStatusCode(); // Получаем код ответа от сервера
        System.out.println("Status GetMFCs: "+status_code);
        if (status_code==200){
            ArrayList<MFCsInfoModel> MFCsList=new ArrayList<MFCsInfoModel>();

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result_of_req); // Получение главного элемента
            JsonArray content= element.getAsJsonArray();
            for (int i=0; i<content.size(); i++){
                MFCsList.add(new MFCsInfoModel(content.get(i).getAsJsonObject().get("code").getAsString(),content.get(i).getAsJsonObject().get("name").getAsString()));
            }
            return MFCsList;

        } else {
            return null;
        }


    }

    // Функция для обработки отчётов
    public static ArrayList<ReportModel> parsingReport(String csvListOrdersReport, String csvAppealReport) throws IOException, CsvValidationException {
        ArrayList<ReportModel> reportListFinal; // Итоговый список (Фильтрованный список по заявлениям, документам и обращениям)
        switch (csvListOrdersReport){
            case "":
                reportListFinal=null;
                return reportListFinal;
            default:
                reportListFinal=parsingCsvData(csvListOrdersReport, csvAppealReport);
                return reportListFinal;

        }
    }

    public static ArrayList<ReportModel> parsingCsvData(String csvListOrdersReport, String csvAppealReport) throws IOException, CsvValidationException{
        ArrayList<ReportModel> reportListOrders=new ArrayList<ReportModel>(); // Список по заявлениям
        ArrayList<ReportModel> reportListAppeal=new ArrayList<ReportModel>(); // Список по обращениям
        ArrayList<ReportModel> reportListFinal=new ArrayList<ReportModel>(); // Итоговый список (Фильтрованный список по заявлениям, документам и обращениям)

        // Обрабатываем csv результат для отчёта по заявлениям
        try (CSVReader reader = new CSVReaderBuilder(new StringReader(csvListOrdersReport))
                .withSkipLines(1)           // Пропускаем первую строку
                .build()) {
            String[] lineInArray;
            // Идём по csv
            while ((lineInArray = reader.readNext()) != null) {
                // Получаем период с csv
                reportListFinal.add(new ReportModel(lineInArray[0], "", "", "", "", "","","",""));
                break; // После получения периода, прерываем чтение данных
            }
            // Вновь читаем csv
            while ((lineInArray = reader.readNext()) != null) {
                // Берём только данные по ЕГРН
                if (lineInArray[11].equals("Предоставление сведений, содержащихся в ЕГРН, об объектах недвижимости и (или) их правообладателях")){
                    // Записываем нужную информацию в список (Организация, номера обращений, даты создания, заявители)
                    reportListOrders.add(new ReportModel("", lineInArray[1], lineInArray[2],lineInArray[11], lineInArray[3], "",lineInArray[12],"",""));
                }
            }
        }

        // Идём по отчёту обращений в csv
        try (CSVReader reader = new CSVReaderBuilder(new StringReader(csvAppealReport))
                .withSkipLines(3)           // Пропускаем первые три строки
                .build()) {
            String[] lineInArray;
            while ((lineInArray = reader.readNext()) != null) {
                // Записываем нужные данные в список отчёта по обращениям
                reportListAppeal.add(new ReportModel("", "", lineInArray[6],lineInArray[5], "",lineInArray[8],"", lineInArray[11],lineInArray[13]));
            }
        }

        System.out.println("Size Orders List: "+reportListOrders.size()+" Size appeal list: "+reportListAppeal.size());
        if (reportListAppeal.size() == 0) {
            for (int i=0; i<reportListOrders.size(); i++){
                reportListFinal.add(new ReportModel("",reportListOrders.get(i).getNameCompany(), reportListOrders.get(i).getNumberAppeal(),
                        reportListOrders.get(i).getNameAppeal(),reportListOrders.get(i).getDateCreate(),
                        reportListOrders.get(i).getStatus(),reportListOrders.get(i).getApplicant(),
                        reportListOrders.get(i).getDateEnd(), reportListOrders.get(i).getCurrentStep()));
            }

        } else {
            // Обрабатываем отфильтрованный отчёт с отчётом по обращениям
            if(reportListAppeal.size()>reportListOrders.size()){ // Проверяем, чтобы отчёт по обращениям был больше отчёта фильтрованного
                for (int i=0; i<reportListOrders.size(); i++){ // Идём по циклу фильтрованного отчёта
                    String FilterNumberAppeal = reportListOrders.get(i).getNumberAppeal().toLowerCase(); // Получаем номер обращения
                    for (int j=0; j<reportListAppeal.size(); j++){ // Идём по циклу отчёта по обращениям
                        String ListNumberAppeal = reportListAppeal.get(j).getNumberAppeal().toLowerCase(); // Получаем номер обращения
                        if (FilterNumberAppeal.contains(ListNumberAppeal)){ // Сравниваем номер обращения с фильтрованного отчёта и отчёта по обращениям
                            // Если совпадают, то добавить в финальный отчёт информацию: Организация, номер обращения и т.д.
                            reportListFinal.add(new ReportModel("",reportListOrders.get(i).getNameCompany(), reportListOrders.get(i).getNumberAppeal(),
                                    reportListAppeal.get(j).getNameAppeal(),reportListOrders.get(i).getDateCreate(),
                                    reportListAppeal.get(j).getStatus(),reportListOrders.get(i).getApplicant(),
                                    reportListAppeal.get(j).getDateEnd(), reportListAppeal.get(j).getCurrentStep()));
                        }
                    }
                }
            } else {
                reportListFinal=null;
            }
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
    // Функция для проверки на наличие дупликатов в списке
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

    public static ArrayList<ReportModel> parsingReportOrg(String json, ArrayList<MFCsInfoModel> MFCsList) throws ParseException {
        ArrayList<ReportModel> reportOrgList=new ArrayList<ReportModel>();

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json); // Получение главного элемента
        JsonArray content= element.getAsJsonObject().get("list").getAsJsonObject().get("content").getAsJsonArray();

        for (int i=0; i<content.size(); i++){
            String codeOrg=""; String orgName=""; String internalNum=""; String name=""; String createDate="";
            String statusNotePPOZ = ""; String textApplicants=""; String processingEndDate=""; String currentStep="";

            if (!content.get(i).getAsJsonObject().get("codeOrg").isJsonNull()){
                codeOrg = content.get(i).getAsJsonObject().get("codeOrg").getAsString();
                for (MFCsInfoModel mfCsInfoModel : MFCsList) {
                    if (codeOrg.equals(mfCsInfoModel.getCode())) {
                        orgName = mfCsInfoModel.getName();
                        break;
                    }
                }
            }
            if (!content.get(i).getAsJsonObject().get("internalNum").isJsonNull()){
                internalNum = content.get(i).getAsJsonObject().get("internalNum").getAsString();
            }
            if (!content.get(i).getAsJsonObject().get("name").isJsonNull()){
                name = content.get(i).getAsJsonObject().get("name").getAsString();
            }
            if (!content.get(i).getAsJsonObject().get("createDate").isJsonNull()){
                createDate = content.get(i).getAsJsonObject().get("createDate").getAsString();
                createDate=convertTimeOrg(createDate);
            }
            if (!content.get(i).getAsJsonObject().get("statusNotePPOZ").isJsonNull()){
                statusNotePPOZ = content.get(i).getAsJsonObject().get("statusNotePPOZ").getAsString();
            }
            if (!content.get(i).getAsJsonObject().get("textApplicants").isJsonNull()){
                textApplicants = content.get(i).getAsJsonObject().get("textApplicants").getAsString();
            }
            if (!content.get(i).getAsJsonObject().get("processingEndDate").isJsonNull()){
                processingEndDate = content.get(i).getAsJsonObject().get("processingEndDate").getAsString();
                processingEndDate =convertTimeOrg(processingEndDate);
            }
            if (!content.get(i).getAsJsonObject().get("currentStep").isJsonNull()){
                currentStep = content.get(i).getAsJsonObject().get("currentStep").getAsString();

                switch (currentStep){
                    case "PROCESS_END_13":
                        currentStep="Обработка завершена";
                        break;
                    case "WAIT_OUT_11":
                        currentStep="Ожидается выдача";
                        break;
                    case "ATTACH_IMAGE":
                        currentStep="Присоединение образов";
                        break;
                    case "PKG_IMG_WAIT_PPOZ_43":
                        currentStep="Отправлено в ПКУРП";
                        break;
                    case "CANCELED_114":
                        currentStep="Аннулировано";
                        break;
                    case "CREATE":
                        currentStep="Приём обращения";
                        break;
                    default:
                        currentStep="Неизвестно";
                        break;
                }
            }

            reportOrgList.add(new ReportModel("", orgName, internalNum, name, createDate, statusNotePPOZ, textApplicants, processingEndDate, currentStep));
        }


        return reportOrgList;
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
        cell.setCellValue("Наименование МФЦ");
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
        cell.setCellValue("Наименование МФЦ");
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

    // Класс для для payload report
    static class Payload_report
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