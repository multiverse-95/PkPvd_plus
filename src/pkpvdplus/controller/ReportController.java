package pkpvdplus.controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportController {

    public static class ReportTask extends Task<ArrayList<ReportModel>> {
        private final String cookies;
        private final String search_text;
        private final LocalDate dateStart;
        private final LocalDate dateFinish;

        public ReportTask(String cookies, String search_text, LocalDate dateStart, LocalDate dateFinish) {
            this.cookies = cookies;
            this.search_text = search_text;
            this.dateStart = dateStart;
            this.dateFinish = dateFinish;
        }
        @Override
        protected ArrayList<ReportModel> call() throws Exception {
            System.out.println("start date: "+dateStart+" finish date: "+dateFinish);
            List<Long> timelist=convertTime(dateStart, dateFinish);
            long dateStartLong=timelist.get(0);
            long dateFinishLong=timelist.get(1);
            System.out.println("start: "+dateStartLong+" finish: "+dateFinishLong);
            String csvReport=getReport(dateStartLong,dateFinishLong,cookies);
            //tring csvReport="";
            ArrayList<ReportModel> reportList= parsingReport(csvReport);
            return reportList;
        }
    }

    public static class DownloadTaskExcel extends Task<ArrayList<String>> {
        private final ArrayList<ReportModel> dataReportList;
        private File file;

        public DownloadTaskExcel(ArrayList<ReportModel> dataReportList, File file) {
            this.dataReportList = dataReportList;
            this.file=file;

        }
        @Override
        protected ArrayList<String> call() throws Exception {
            ArrayList<String> pathFileAndDir=SaveFileExcel(dataReportList, file);
            String success_download="SUCCESS REPORT!!";
            return pathFileAndDir;
        }
    }

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
            return pathFileAndDir;
        }
    }

    /*public static class FilterApplicantsTask extends Task<ArrayList<ReportModel>> {
        private final String FilterApplicant;
        private final ArrayList<ReportModel> dataReportList;

        public FilterApplicantsTask(String FilterApplicant, ArrayList<ReportModel> dataReportList) {
            this.FilterApplicant=FilterApplicant;
            this.dataReportList = dataReportList;

        }
        @Override
        protected ArrayList<ReportModel> call() throws Exception {
            ArrayList<ReportModel> dataReportList_findArr=FilterApplicants(FilterApplicant, dataReportList);
            return dataReportList_findArr;
        }
    }

    public static class FilterNameCompanyTask extends Task<ArrayList<ReportModel>> {
        private final String FilterNameCompany;
        private final ArrayList<ReportModel> dataReportList;

        public FilterNameCompanyTask(String FilterNameCompany, ArrayList<ReportModel> dataReportList) {
            this.FilterNameCompany=FilterNameCompany;
            this.dataReportList = dataReportList;

        }
        @Override
        protected ArrayList<ReportModel> call() throws Exception {
            ArrayList<ReportModel> dataReportList_findArr=FilterNameCompany(FilterNameCompany, dataReportList);
            return dataReportList_findArr;
        }
    }

    public static class FilterAppealTask extends Task<ArrayList<ReportModel>> {
        private final String FilterAppeal;
        private final ArrayList<ReportModel> dataReportList;

        public FilterAppealTask(String FilterAppeal, ArrayList<ReportModel> dataReportList) {
            this.FilterAppeal=FilterAppeal;
            this.dataReportList = dataReportList;

        }
        @Override
        protected ArrayList<ReportModel> call() throws Exception {
            ArrayList<ReportModel> dataReportList_findArr=FilterAppeal(FilterAppeal, dataReportList);
            return dataReportList_findArr;
        }
    }*/


    public static List<Long> convertTime(LocalDate dateStart, LocalDate dateFinish) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = format.parse(String.valueOf(dateStart));
        Date date2 = format.parse(String.valueOf(dateFinish));
        long timestamp1 = date1.getTime();
        long timestamp2 =date2.getTime();
        List<Long> timelist=new ArrayList<Long>();
        timelist.add(timestamp1);
        timelist.add(timestamp2);
        return timelist;
    }


    public static String getReport(long dateStart, long dateFinish,String cookie) throws IOException {
//        dateStart=Long.parseLong("1625072400000");
//        dateFinish=Long.parseLong("1625245200000");
        Payload_user payload_user = new Payload_user();
        CookieStore httpCookieStore = new BasicCookieStore();
        payload_user.file ="Список заявлений.jrd";
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


        payload_user.params=params;

        String postUrl       = "http://10.42.200.207/api/rs/reports/execute";// put in your url
        Gson gson          = new Gson();
        HttpClient httpClient = null;
        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore);
        httpClient = builder.build();
        HttpPost post          = new HttpPost(postUrl);
        StringEntity postingString = new StringEntity(gson.toJson(payload_user), StandardCharsets.UTF_8);//gson.tojson() converts your payload to json
        System.out.println(gson.toJson(payload_user));
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        post.addHeader("Cookie","JSESSIONID="+cookie);
        HttpResponse response = httpClient.execute(post);
        System.out.println(response.getStatusLine());
        HttpEntity entity = response.getEntity();

        String reportCsv=EntityUtils.toString(entity);
        return reportCsv;
    }

    public static ArrayList<ReportModel> parsingReport(String csvReport) throws IOException, CsvValidationException {
        ArrayList<ReportModel> reportList=new ArrayList<ReportModel>();
        //try (CSVReader reader = new CSVReaderBuilder(new FileReader("D:\\recovery\\pk_pvd\\reportPkPvd.csv"))
        try (CSVReader reader = new CSVReaderBuilder(new StringReader(csvReport))
                .withSkipLines(1)           // skip the first line, header info
                .build()) {
            String[] lineInArray;
            while ((lineInArray = reader.readNext()) != null) {
                //System.out.println(lineInArray[0]);
                reportList.add(new ReportModel(lineInArray[0], "", "", "", "",""));
                break;
                //System.out.println(lineInArray[1] + ","+ lineInArray[2]+","+ lineInArray[3]+","+ lineInArray[12]);
            }

            while ((lineInArray = reader.readNext()) != null) {
                if (lineInArray[11].equals("Предоставление сведений, содержащихся в ЕГРН, об объектах недвижимости и (или) их правообладателях")){
                    reportList.add(new ReportModel("", lineInArray[1], lineInArray[2], lineInArray[3], lineInArray[11],lineInArray[12]));
                }
                //System.out.println(lineInArray[1] + ","+ lineInArray[2]+","+ lineInArray[3]+","+ lineInArray[12]);
            }
        }
        return reportList;
    }

    public ArrayList<ReportModel> FilterApplicants(String FilterApplicant, ArrayList<ReportModel> dataReportList){
        // Поиск через регулярные выражения
        Pattern pattern = Pattern.compile(".*" + FilterApplicant.toLowerCase() + ".*");

        ArrayList<ReportModel> dataReportList_findArr = new ArrayList<ReportModel>();
        // Идем по циклу данных, что есть в таблице, применяем паттерн. Совпадения записываем в новый список
        for (int i = 0; i < dataReportList.size(); i++) {
            Matcher matcher = pattern.matcher(dataReportList.get(i).getApplicant().toLowerCase());
            if (matcher.find()) {
                dataReportList_findArr.add(new ReportModel(
                        dataReportList.get(i).getPeriod(),dataReportList.get(i).getNameCompany() ,dataReportList.get(i).getAppeal(),
                        dataReportList.get(i).getDateCreate(), dataReportList.get(i).getAction(), dataReportList.get(i).getApplicant()));
            } else {
                //System.out.println("Search Failed!");
            }
        }
        // Возвращаем список найденных переменных по запросу
        return dataReportList_findArr;
    }

    public ArrayList<ReportModel> FilterNameCompany(String FilterNameCompany, ArrayList<ReportModel> dataReportList){
        // Поиск через регулярные выражения
        Pattern pattern = Pattern.compile(".*" + FilterNameCompany.toLowerCase() + ".*");

        ArrayList<ReportModel> dataReportList_findArr = new ArrayList<ReportModel>();
        // Идем по циклу данных, что есть в таблице, применяем паттерн. Совпадения записываем в новый список
        for (int i = 0; i < dataReportList.size(); i++) {
            Matcher matcher = pattern.matcher(dataReportList.get(i).getNameCompany().toLowerCase());
            if (matcher.find()) {
                dataReportList_findArr.add(new ReportModel(
                        dataReportList.get(i).getPeriod(),dataReportList.get(i).getNameCompany() ,dataReportList.get(i).getAppeal(),
                        dataReportList.get(i).getDateCreate(), dataReportList.get(i).getAction(), dataReportList.get(i).getApplicant()));
            } else {
                //System.out.println("Search Failed!");
            }
        }
        // Возвращаем список найденных переменных по запросу
        return dataReportList_findArr;
    }

    public ArrayList<ReportModel> FilterAppeal(String FilterAppeal, ArrayList<ReportModel> dataReportList){
        // Поиск через регулярные выражения
        Pattern pattern = Pattern.compile(".*" + FilterAppeal.toLowerCase() + ".*");

        ArrayList<ReportModel> dataReportList_findArr = new ArrayList<ReportModel>();
        // Идем по циклу данных, что есть в таблице, применяем паттерн. Совпадения записываем в новый список
        for (int i = 0; i < dataReportList.size(); i++) {
            Matcher matcher = pattern.matcher(dataReportList.get(i).getAppeal().toLowerCase());
            if (matcher.find()) {
                dataReportList_findArr.add(new ReportModel(
                        dataReportList.get(i).getPeriod(),dataReportList.get(i).getNameCompany() ,dataReportList.get(i).getAppeal(),
                        dataReportList.get(i).getDateCreate(), dataReportList.get(i).getAction(), dataReportList.get(i).getApplicant()));
            } else {
                //System.out.println("Search Failed!");
            }
        }
        // Возвращаем список найденных переменных по запросу
        return dataReportList_findArr;
    }



    // Функция для загрузки отчета по ведомствам
    public void Download_report(ArrayList<ReportModel> dataReportList) {

        //System.out.println(text_test);
        // Создаем экземпляр класса FileChooser
        FileChooser fileChooser = new FileChooser();

        String lastPathDirectory=getLastDirectory();
        if (!lastPathDirectory.equals("")){
            fileChooser.setInitialDirectory(new File(lastPathDirectory));
        }
        // Устанавливаем список расширений для файла
        fileChooser.setInitialFileName("report_pkpvd");// Устанавливаем название для файла
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
                    //SaveFileExcel(dataReportList, file);
                    Task DownloadTaskExcel =  new DownloadTaskExcel (dataReportList, file);

                    //  После выполнения потока
                    DownloadTaskExcel.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent event) {
                            //System.out.println(DownloadTaskExcel.getValue());
                            ArrayList<String> pathFileAndDir= (ArrayList<String>) DownloadTaskExcel.getValue();

                            String pathToFile=pathFileAndDir.get(0);
                            String absolutePathToFile=pathFileAndDir.get(1);

                            System.out.println(pathToFile +" "+absolutePathToFile);

                            ButtonType openReport = new ButtonType("Открыть отчёт", ButtonBar.ButtonData.OK_DONE); // Создание кнопки подтвердить
                            ButtonType openDir = new ButtonType("Открыть папку с отчётом", ButtonBar.ButtonData.CANCEL_CLOSE); // Создание кнопки отменить
                            Alert alert =new Alert(Alert.AlertType.INFORMATION , "Test", openReport, openDir);
                            alert.setTitle("Загрузка завершена!"); // Название предупреждения
                            alert.setHeaderText("Отчёт загружен!"); // Текст предупреждения
                            alert.setContentText("Отчёт доступен в папке: "+pathToFile);
                            // Вызов подтверждения элемента
                            alert.showAndWait().ifPresent(rs -> {
                                if (rs == openReport){
                                    Desktop desktop = Desktop.getDesktop();
                                    File fileOpen= new File (absolutePathToFile);
                                    try {
                                        //desktop.open(file);
                                        desktop.open(fileOpen);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else if (rs==openDir){
                                    try {
                                        Process p = new ProcessBuilder("explorer.exe", "/select,"+absolutePathToFile).start();
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
                    //SaveFileExcelOldFormat(dataReportList, file);
                    Task DownloadTaskExcelOld =  new DownloadTaskExcelOld (dataReportList, file);
                    //  После выполнения потока
                    DownloadTaskExcelOld.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent event) {
                            System.out.println(DownloadTaskExcelOld.getValue());

                            ArrayList<String> pathFileAndDir= (ArrayList<String>) DownloadTaskExcelOld.getValue();

                            String pathToFile=pathFileAndDir.get(0);
                            String absolutePathToFile=pathFileAndDir.get(1);

                            System.out.println(pathToFile +" "+absolutePathToFile);

                            ButtonType openReport = new ButtonType("Открыть отчёт", ButtonBar.ButtonData.OK_DONE); // Создание кнопки подтвердить
                            ButtonType openDir = new ButtonType("Открыть папку с отчётом", ButtonBar.ButtonData.CANCEL_CLOSE); // Создание кнопки отменить
                            Alert alert =new Alert(Alert.AlertType.INFORMATION , "Test", openReport, openDir);
                            alert.setTitle("Загрузка завершена!"); // Название предупреждения
                            alert.setHeaderText("Отчёт загружен!"); // Текст предупреждения
                            alert.setContentText("Отчёт доступен в папке: "+pathToFile);
                            // Вызов подтверждения элемента
                            alert.showAndWait().ifPresent(rs -> {
                                if (rs == openReport){
                                    Desktop desktop = Desktop.getDesktop();
                                    File fileOpen= new File (absolutePathToFile);
                                    try {
                                        //desktop.open(file);
                                        desktop.open(fileOpen);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else if (rs==openDir){
                                    try {
                                        Process p = new ProcessBuilder("explorer.exe", "/select,"+absolutePathToFile).start();
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

        // Создание столбца IdDepartm
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Название организации");
        cell.setCellStyle(style);
        // Создание столбца названия ведомства
        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Обращение");
        cell.setCellStyle(style);

        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Дата создания");
        cell.setCellStyle(style);

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Действие");
        cell.setCellStyle(style);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Заявители");
        cell.setCellStyle(style);


        // Перебор по данным
        for (ReportModel reportModel: dataReportList) {
            //System.out.println(mfc_model.getIdMfc() +"\t" +mfc_model.getNameMfc());
            rownum++;
            row = sheet.createRow(rownum);

            // Запись id ведомства
            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(reportModel.getNameCompany());
            // Запись Названия ведомства
            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(reportModel.getAppeal());

            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue(reportModel.getDateCreate());

            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue(reportModel.getAction());

            cell = row.createCell(4, CellType.STRING);
            cell.setCellValue(reportModel.getApplicant());

        }
        autoSizeColumns(workbook);
        // Создания потока сохранения файла
        FileOutputStream outFile = new FileOutputStream(file);
        // Запись файла
        workbook.write(outFile);
        // Закрытие потока записи
        outFile.close();
        System.out.println("Created file: " + file.getParent());

        SaveLastPathInfo(file.getParent());

        ArrayList<String> pathFileAndDir=new ArrayList<String>();
        pathFileAndDir.add(file.getParent());
        pathFileAndDir.add(file.getAbsolutePath());

        return pathFileAndDir;


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
        sheet.autoSizeColumn(5);
        //List<Employee> list = EmployeeDAO.listEmployees();

        int rownum = 0;
        Cell cell;
        Row row;
        // Установка стилей
        XSSFCellStyle style = createStyleForTitleNew(workbook);

        row = sheet.createRow(rownum);

        // Создание столбца IdDepartm
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Название организации");
        cell.setCellStyle(style);
        // Создание столбца названия ведомства
        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Обращение");
        cell.setCellStyle(style);

        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Дата создания");
        cell.setCellStyle(style);

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Действие");
        cell.setCellStyle(style);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Заявители");
        cell.setCellStyle(style);


        // Перебор по данным
        for (ReportModel reportModel : dataReportList) {
            //System.out.println(mfc_model.getIdMfc() +"\t" +mfc_model.getNameMfc());
            rownum++;
            row = sheet.createRow(rownum);

            // Запись id Ведомства
            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(reportModel.getNameCompany());
            // Запись Названия Ведомства
            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(reportModel.getAppeal());

            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue(reportModel.getDateCreate());

            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue(reportModel.getAction());

            cell = row.createCell(4, CellType.STRING);
            cell.setCellValue(reportModel.getApplicant());

        }
        autoSizeColumns(workbook);
        // Создания потока сохранения файла
        FileOutputStream outFile = new FileOutputStream(file);
        // Запись файла
        workbook.write(outFile);
        // Закрытие потока записи
        outFile.close();


        System.out.println("Created file: " + file.getParent());
        SaveLastPathInfo(file.getParent());

        ArrayList<String> pathFileAndDir=new ArrayList<String>();
        pathFileAndDir.add(file.getParent());
        pathFileAndDir.add(file.getAbsolutePath());

        return pathFileAndDir;

    }

    public static void autoSizeColumns(Workbook workbook) {
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet.getPhysicalNumberOfRows() > 0) {
                Row row = sheet.getRow(sheet.getFirstRowNum());
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    int columnIndex = cell.getColumnIndex();
                    sheet.autoSizeColumn(columnIndex);
                }
            }
        }
    }

    public static void SaveLastPathInfo(String lastPathToFile){
        // setlasstput info
        File fileJson = new File("C:\\pkpvdplus\\settingsPVD.json");

        if(!fileJson.exists())
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
            String cookie=jsonObject.get("cookie").getAsString();
            boolean isCheckBoxSel=jsonObject.get("isCheckBoxSel").getAsBoolean();
            SettingsModel settingsModel=new SettingsModel(login, password, cookie, lastPathToFile, isCheckBoxSel);
            settingsModel.setLogin(login);
            settingsModel.setPassword(password);
            settingsModel.setCookie(cookie);
            settingsModel.setLastPathToFile(lastPathToFile);
            settingsModel.setCheckBoxSel(isCheckBoxSel);

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

            try {
                FileWriter fileWriter = null;
                fileWriter = new FileWriter(fileJson);
                fileWriter.write(content);
                fileWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String getLastDirectory(){
        String lastPathToFile="";
        File fileJson = new File("C:\\pkpvdplus\\settingsPVD.json");

        if(!fileJson.exists())
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
            lastPathToFile = jsonObject.get("lastPathToFile").getAsString();
        }
        return lastPathToFile;
    }


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
        public long value;
    }

}
