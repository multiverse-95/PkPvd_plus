package pkpvdplus.controller;

import com.google.gson.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import pkpvdplus.model.AllAppealInfoModel;
import pkpvdplus.model.AppealGeneralInfoModel;
import pkpvdplus.model.ApplicantInfoModel;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
// Класс, который отвечает за получение информации по обращению
public class GetAppealInfoController {

    // Функция для получения информации об обращении
    public AllAppealInfoModel GetAppealInfo(String cookie, String numberAppeal) throws IOException {
            AllAppealInfoModel allAppealInfoModel;
            AppealGeneralInfoModel appealGeneralInfoModel=null;
            ArrayList<String> Applicant_and_Representive=new ArrayList<String>();
            String IdApplicant=""; // ID заявителя
            String applicantTypeRequest=""; // Категория заявителя
            String IdRepresentative=""; // ID представителя
            String representiveType=""; // Тип представителя
            String represDocumentTypeID=""; // ID документа, подтверждающего полномочия представителя
            ArrayList<ApplicantInfoModel> applicantInfoArr=new ArrayList<ApplicantInfoModel>();

            CookieStore httpCookieStore = new BasicCookieStore();
            HttpClient httpClient = null;
            HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore); //MFC-0561/2021-241209
            httpClient = builder.build();
            String getUrl       = "http://10.42.200.207/api/rs/appeal/search2?page=0&size=5&sort=createDate,desc&startWith=false&internalNum="
                    + URLEncoder.encode(numberAppeal, String.valueOf(StandardCharsets.UTF_8))+"&packageNum" +
                    "=&statusNotePPOZ=&currentStep=&createDateFrom=&createDateTill=&createWho=&moveStepDate=&kudNum=&routineExecutionDays=&processingEndDateFrom=&processingEndDateTill" +
                    "=&typeGosUslug=&cn=&textApplicants=";// Сервер авторизации
            HttpGet httpGet = new HttpGet(getUrl);
            httpGet.setHeader("Content-type", "application/json");
            httpGet.addHeader("Cookie","JSESSIONID="+cookie);
            HttpResponse response = httpClient.execute(httpGet); // Выполняем get запрос для проверки действительности куки

            HttpEntity entity = response.getEntity();
            String result_of_req = EntityUtils.toString(entity); // Получаем результат запроса

            int status_code= response.getStatusLine().getStatusCode(); // Получаем код ответа от сервера
            System.out.println("Status SearchAppealID: "+status_code);
            boolean CookieValid;
            // Если код ответа 200, значит куки действителен, если 401 или другой, то недействителен
            switch (status_code){
                case 200:
                    CookieValid=true;
                    //String idAppeal=Parsing_result_searchAppealID(result_of_req);
                    // Создания экземпляра парсинга
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(result_of_req); // Получение главного элемента
                    JsonArray content= element.getAsJsonObject().get("content").getAsJsonArray(); // Получаем контент
                    String idAppeal=content.get(0).getAsJsonObject().get("id").getAsString(); // Получаем id обращения
                    String idStatement=GetStatementID(cookie, idAppeal); // Получаем id statement
                    appealGeneralInfoModel= GetAppealGeneralInformation(cookie, idAppeal, idStatement); // Получаем общую информацию об обращении
                    Applicant_and_Representive=GetApplicantSubjects(cookie, idAppeal, idStatement); // Получаем id заявителя и представителя
                    // Смотрим, есть ли представитель у заявителя
                    switch (Applicant_and_Representive.size()){ // смотрим по размеру пришедшего списка
                        case 1: // если размер 1, то у нас только заявитель
                            IdApplicant=Applicant_and_Representive.get(0); // Получаем id заявителя
                            // Получаем подробную информацию о заявителе и добавляем в список
                            applicantInfoArr.add(GetSubjectInfo(cookie, idAppeal, IdApplicant, "","",""));
                            break;
                        case 2: // если размер 2, то у заявителя есть тип запроса
                            IdApplicant=Applicant_and_Representive.get(0); // Получаем id заявителя
                            applicantTypeRequest=Applicant_and_Representive.get(1); // Получаем тип запроса
                            // Получаем подробную информацию о заявителе и добавляем в список
                            applicantInfoArr.add(GetSubjectInfo(cookie, idAppeal, IdApplicant,applicantTypeRequest,"",""));
                            break;
                        case 3: // если размер 3, то у заявителя есть представитель
                            IdApplicant=Applicant_and_Representive.get(0); // Получаем id заявителя
                            applicantTypeRequest=Applicant_and_Representive.get(1); // Получаем тип запроса
                            IdRepresentative=Applicant_and_Representive.get(2); // Получаем id представителя
                            // Получаем подробную информацию о заявителе и добавляем в список
                            applicantInfoArr.add(GetSubjectInfo(cookie, idAppeal, IdApplicant,applicantTypeRequest,"",""));
                            // Получаем подробную информацию о представителе и добавляем в список
                            applicantInfoArr.add(GetSubjectInfo(cookie, idAppeal, IdRepresentative,"","",""));
                            break;
                        case 4: // если размер 4, то у заявителя есть представитель. Также у представителя указан тип представителя
                            IdApplicant=Applicant_and_Representive.get(0); // Получаем id заявителя
                            applicantTypeRequest=Applicant_and_Representive.get(1); // Получаем тип запроса
                            IdRepresentative=Applicant_and_Representive.get(2); // Получаем id представителя
                            representiveType=Applicant_and_Representive.get(3); // Получаем тип представителя
                            // Получаем подробную информацию о заявителе и добавляем в список
                            applicantInfoArr.add(GetSubjectInfo(cookie, idAppeal, IdApplicant,applicantTypeRequest,"",""));
                            // Получаем подробную информацию о представителе и добавляем в список
                            applicantInfoArr.add(GetSubjectInfo(cookie, idAppeal, IdRepresentative,"", representiveType,""));
                            break;
                        case 5: // если размер 5, то у заявителя есть представитель. Также у представителя указан тип представителя и id документа, подтверждающего полномочия
                            IdApplicant=Applicant_and_Representive.get(0); // Получаем id заявителя
                            applicantTypeRequest=Applicant_and_Representive.get(1); // Получаем тип запроса
                            IdRepresentative=Applicant_and_Representive.get(2); // Получаем id представителя
                            representiveType=Applicant_and_Representive.get(3); // Получаем тип представителя
                            represDocumentTypeID=Applicant_and_Representive.get(4); // Получаем id документа, подтверждающего полномочия
                            // Получаем подробную информацию о заявителе и добавляем в список
                            applicantInfoArr.add(GetSubjectInfo(cookie, idAppeal, IdApplicant,applicantTypeRequest,"",""));
                            // Получаем подробную информацию о представителе и добавляем в список
                            applicantInfoArr.add(GetSubjectInfo(cookie, idAppeal, IdRepresentative,"",representiveType,represDocumentTypeID));
                            break;
                        default:
                            break;
                    }
                    break;
                case 401: // Запрос завершился с ошибкой
                    CookieValid=false;
                    cookie="";
                    break;
                default:
                    CookieValid=false;
                    cookie="";
                    break;
            }
            // Добавляем в итоговый список общую информацию об обращении и информацию по заявителям и представителям
            allAppealInfoModel=new AllAppealInfoModel(appealGeneralInfoModel, applicantInfoArr);
            return allAppealInfoModel; // Возвращаем итоговый отчёт
    }
    // Получаем statement ID
    public String GetStatementID(String cookie, String idAppeal) throws IOException {
        String idStatement="";
        CookieStore httpCookieStore = new BasicCookieStore();
        HttpClient httpClient = null;
        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore);
        httpClient = builder.build();
        String getUrl       = "http://10.42.200.207/api/rs/appeal/"+idAppeal+"/statement?page=0&size=5";// Сервер авторизации
        HttpGet httpGet = new HttpGet(getUrl);
        httpGet.setHeader("Content-type", "application/json");
        httpGet.addHeader("Cookie","JSESSIONID="+cookie);
        HttpResponse response = httpClient.execute(httpGet); // Выполняем get запрос для проверки действительности куки

        HttpEntity entity = response.getEntity();
        String result_of_req = EntityUtils.toString(entity); // Получаем результат запроса

        int status_code= response.getStatusLine().getStatusCode(); // Получаем код ответа от сервера
        System.out.println("Status GetStatementID: "+status_code);
        boolean CookieValid;
        // Если код ответа 200, значит куки действителен, если 401 или другой, то недействителен
        switch (status_code){
            case 200:
                CookieValid=true;
                //idStatement=Parsing_result_getStatementID(result_of_req);
                // Создания экземпляра парсинга
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(result_of_req); // Получение главного элемента
                JsonArray content= element.getAsJsonObject().get("content").getAsJsonArray();
                idStatement=content.get(0).getAsJsonObject().get("id").getAsString(); // Парсим id Statement
                break;
            case 401: // Запрос завершился с ошибкой
                CookieValid=false;
                cookie="";
                break;
            default:
                CookieValid=false;
                cookie="";
                break;
        }

        return idStatement; // Возвращаем значение куки
    }
    // Получаем общую информацию по обращению
    public AppealGeneralInfoModel GetAppealGeneralInformation(String cookie, String idAppeal, String idStatement ) throws IOException {
        AppealGeneralInfoModel appealGeneralInfoModel;
        // Запрос на получение основной информации об обращении
        CookieStore httpCookieStore = new BasicCookieStore();
        HttpClient httpClient = null;
        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore);
        httpClient = builder.build();
        String getUrlMainInfo       = "http://10.42.200.207/api/rs/appeal/"+idAppeal+"/statement/"+idStatement;// Сервер авторизации
        HttpGet httpGet = new HttpGet(getUrlMainInfo);
        httpGet.setHeader("Content-type", "application/json");
        httpGet.addHeader("Cookie","JSESSIONID="+cookie);
        HttpResponse response = httpClient.execute(httpGet); // Выполняем get запрос для проверки действительности куки

        HttpEntity entity = response.getEntity();
        String result_of_req_MainInfo = EntityUtils.toString(entity); // Получаем результат запроса
        int status_code= response.getStatusLine().getStatusCode(); // Получаем код ответа от сервера

        // Запрос на получение доп. информации об обращении
        CookieStore httpCookieStoreAdvanced = new BasicCookieStore();
        HttpClient httpClientAdvanced = null;
        HttpClientBuilder builderAdvanced = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStoreAdvanced);
        httpClientAdvanced = builderAdvanced.build();

        String getUrlAdvancedInfo       = "http://10.42.200.207/api/rs/appeal/"+idAppeal;// Сервер авторизации
        HttpGet httpGetAdvanced = new HttpGet(getUrlAdvancedInfo);
        httpGetAdvanced.setHeader("Content-type", "application/json");
        httpGetAdvanced.addHeader("Cookie","JSESSIONID="+cookie);
        HttpResponse responseAdvanced = httpClientAdvanced.execute(httpGetAdvanced); // Выполняем get запрос для проверки действительности куки

        HttpEntity entityAdvanced = responseAdvanced.getEntity();
        String result_of_req_AdvancedInfo = EntityUtils.toString(entityAdvanced); // Получаем результат запроса
        int status_code_Advanced= responseAdvanced.getStatusLine().getStatusCode(); // Получаем код ответа от сервера

        System.out.println("Status GetAppealMainInformation: "+status_code);
        System.out.println("Status GetAppealAdvancedInformation: "+status_code_Advanced);
        boolean CookieValid;
        // Если код ответа 200, значит куки действителен, если 401 или другой, то недействителен
        switch (status_code){
            case 200:
                CookieValid=true;
                if (status_code_Advanced==200){
                    // Парсим всю информацию об обращении
                    appealGeneralInfoModel= Parsing_result_GetAppealGeneralInformation(result_of_req_MainInfo, result_of_req_AdvancedInfo);
                    return appealGeneralInfoModel; // Возвращаем итоговый результат
                } else {
                    return null;
                }
            case 401: // Запрос завершился с ошибкой
                CookieValid=false;
                cookie="";
                break;
            default:
                CookieValid=false;
                cookie="";
                break;
        }

        return null; // Возвращаем значение куки
    }
    // Функция для конвертирования даты из UNIX формата
    public String convertTimeFromUnix(String timeUnix, String timeZone, String dateFormat){

        long unixSeconds = Long.parseLong(timeUnix);

        Date date = new java.util.Date(unixSeconds);
        // Формат даты
        SimpleDateFormat sdf = new java.text.SimpleDateFormat(dateFormat);
        // Устанавливаем часовой пояс
        sdf.setTimeZone(java.util.TimeZone.getTimeZone(timeZone));
        String formattedDate = sdf.format(date);
        System.out.println("FORMAT DATE FROM UNIX: "+ formattedDate);
        return formattedDate; // Возвращаем итоговый результат
    }
    // Конвертируем дату в нужный формат
    public String convertDateToCorrect(String dateFromSever){
        Date date=null;
        // Из формата yyyy-MM-dd нужно переконвертировать в формат dd.MM.yyyy
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateFromSever);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy");
        // Устанавливаем часовой пояс
        String formattedDate = sdf.format(date);
        return  formattedDate; // Возвращаем итоговую дату
    }
    // Функция для парсинга общей информации по обращению
    public AppealGeneralInfoModel Parsing_result_GetAppealGeneralInformation(String jsonMain, String jsonAdvanced){
        // Переменные для главной информации об обращении
        // Наименование, внутренний номер, дата создания, кем обработан: фио
        String statementType=""; String internalNum=""; String createEventDateWhen=""; String createEventPerformer="";
        String createEventSurName="";  String createEventFirstName=""; String createEventPatronymic="";
        // Номер пакета
        String packageNum="";
        // Регистрационный номер ППОЗ, когда создан в ППОЗ, статус, обновление, дата обновления
        String numPPOZ=""; String createPPOZDate=""; String statusNotePPOZ=""; String statusPPOZ=""; String statusPPOZDate="";
        // Регламентный срок, Окончание обработки
        String routineExecutionDays=""; String processingEndDate="";

        // Переменные для доп. информации об обращении (принадлежит обращению)
        // Наименование обращения
        String nameAdvanced="";
        // Внутренний номер, дата создания, кем создан:фио
        String internalNumAdvanced=""; String createEventDateWhenAdvanced=""; String createEventPerformerAdvanced="";
        String createEventSurNameAdvanced="";  String createEventFirstNameAdvanced=""; String createEventPatronymicAdvanced="";
        // Шаг
        String currentStepAdvanced="";
        // Переход на шаг: дата, кто перешёл на шаг: фио
        String moveStepEventDateWhenAdvanced=""; String moveStepPerformerAdvanced="";
        String moveStepSurNameAdvanced="";  String moveStepFirstNameAdvanced=""; String moveStepPatronymicAdvanced="";
        // Начало выполнения шага
        String executeEventDateWhenAdvanced=""; String executeEventPerformerAdvanced="";
        String executeEventSurNameAdvanced="";  String executeEventFirstNameAdvanced=""; String executeEventPatronymicAdvanced="";
        String executeEventAdvanced="";
        // Комментарий к текущей операции
        String operationCommentAdvanced="";

        // Переменные для отображения информации о способе получения и представления документов
        boolean pres_on_MFC=false; // Представление документов В МФЦ
        boolean pres_mail=false; // Представление документов почтой
        boolean pres_indiv=false; // Представление документов индивидуально
        boolean output_doc_MFC=false; // Получение документов в МФЦ
        boolean output_doc_mail=false; // Получение документов почтой
        boolean output_doc_email=false; // Получение документов на электронную почту
        String email_output="";

        // Парсинг главной информации об обращении
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(jsonMain); // Получение главного элемента
        // Тип statementType
        if (!element.getAsJsonObject().get("statementType").isJsonNull()) { statementType=element.getAsJsonObject().get("statementType").getAsString(); }
        // Внутренний номер
        if (!element.getAsJsonObject().get("internalNum").isJsonNull()) { internalNum=element.getAsJsonObject().get("internalNum").getAsString(); }
        // Дата создания обращения
        if (!element.getAsJsonObject().get("createEvent").getAsJsonObject().get("dateWhen").isJsonNull()) {
            createEventDateWhen=element.getAsJsonObject().get("createEvent").getAsJsonObject().get("dateWhen").getAsString();
            // Дату конвертируем в нужный формат
            createEventDateWhen=convertTimeFromUnix(createEventDateWhen, "GMT+7", "dd.MM.yyyy HH:mm");
        }
        // Информация об операторе, кто создал заявленияя
        if (!element.getAsJsonObject().get("createEvent").getAsJsonObject().get("performer").isJsonNull()) {
            // Парсим фамилию, имя и отчество
            createEventSurName=element.getAsJsonObject().get("createEvent").getAsJsonObject().get("performer").getAsJsonObject().get("surName").getAsString();
            createEventFirstName=element.getAsJsonObject().get("createEvent").getAsJsonObject().get("performer").getAsJsonObject().get("firstName").getAsString();
            createEventPatronymic=element.getAsJsonObject().get("createEvent").getAsJsonObject().get("performer").getAsJsonObject().get("patronymic").getAsString();
            createEventPerformer=createEventSurName+" "+createEventFirstName+" "+createEventPatronymic;
        }
        // Имя пакета
        if (!element.getAsJsonObject().get("packageNum").isJsonNull()) { packageNum=element.getAsJsonObject().get("packageNum").getAsString(); }
        // Номер ППОЗ
        if (!element.getAsJsonObject().get("numPPOZ").isJsonNull()) { numPPOZ=element.getAsJsonObject().get("numPPOZ").getAsString(); }
        // Дата создания ППОЗ
        if (!element.getAsJsonObject().get("createPPOZDate").isJsonNull()) {
            createPPOZDate=element.getAsJsonObject().get("createPPOZDate").getAsString();
            createPPOZDate=convertTimeFromUnix(createPPOZDate, "GMT+3", "dd.MM.yyyy HH:mm")+" МСК";
        }
        // Статус ППОЗ
        if (!element.getAsJsonObject().get("statusNotePPOZ").isJsonNull()) { statusNotePPOZ=element.getAsJsonObject().get("statusNotePPOZ").getAsString(); }
        if (!element.getAsJsonObject().get("statusPPOZ").isJsonNull()) {
            statusPPOZ=element.getAsJsonObject().get("statusPPOZ").getAsString();
            switch (statusPPOZ){
                case "returned":
                    statusPPOZ="обновлен";
                    break;
                case "processed":
                    statusPPOZ="обновлен";
                    break;
                case "awaitingPayment":
                    statusPPOZ="обновлен";
                    break;
                default:
                    statusPPOZ="неизвестно";
                    break;
            }
        }
        // Дата обновления ППОЗ
        if (!element.getAsJsonObject().get("statusPPOZDate").isJsonNull()) {
            statusPPOZDate=element.getAsJsonObject().get("statusPPOZDate").getAsString();
            statusPPOZDate =convertTimeFromUnix(statusPPOZDate, "GMT+3","dd.MM.yyyy HH:mm")+" МСК";
        }
        // Регламентный срок
        if (!element.getAsJsonObject().get("routineExecutionDays").isJsonNull()) { routineExecutionDays=element.getAsJsonObject().get("routineExecutionDays").getAsString(); }
        // Дата окончания обработки
        if (!element.getAsJsonObject().get("processingEndDate").isJsonNull()) {
            processingEndDate=element.getAsJsonObject().get("processingEndDate").getAsString();
            processingEndDate= convertTimeFromUnix(processingEndDate, "GMT+7","dd.MM.yyyy");
        }

        // Парсинг дополнительной информации об обращении
        JsonParser parserAdvanced = new JsonParser();
        JsonElement elementAdvanced = parserAdvanced.parse(jsonAdvanced); // Получение главного элемента
        // Наименование обращения
        if (!elementAdvanced.getAsJsonObject().get("name").isJsonNull()) { nameAdvanced=elementAdvanced.getAsJsonObject().get("name").getAsString(); }
        // Внутренний номер обращения
        if (!elementAdvanced.getAsJsonObject().get("internalNum").isJsonNull()) { internalNumAdvanced=elementAdvanced.getAsJsonObject().get("internalNum").getAsString(); }
        // Дата создания обращения
        if (!elementAdvanced.getAsJsonObject().get("createEvent").getAsJsonObject().get("dateWhen").isJsonNull()) {
            createEventDateWhenAdvanced=elementAdvanced.getAsJsonObject().get("createEvent").getAsJsonObject().get("dateWhen").getAsString();
            // Преобразуем дату в нужный формат
            createEventDateWhenAdvanced= convertTimeFromUnix(createEventDateWhenAdvanced, "GMT+7", "dd.MM.yyyy HH:mm");
        }
        // Информация об операторе, кто создал обращение
        if (!elementAdvanced.getAsJsonObject().get("createEvent").getAsJsonObject().get("performer").isJsonNull()) {
            // Получаем фамилию, имя и отчество
            createEventSurNameAdvanced=elementAdvanced.getAsJsonObject().get("createEvent").getAsJsonObject().get("performer").getAsJsonObject().get("surName").getAsString();
            createEventFirstNameAdvanced=elementAdvanced.getAsJsonObject().get("createEvent").getAsJsonObject().get("performer").getAsJsonObject().get("firstName").getAsString();
            createEventPatronymicAdvanced=elementAdvanced.getAsJsonObject().get("createEvent").getAsJsonObject().get("performer").getAsJsonObject().get("patronymic").getAsString();
            createEventPerformerAdvanced=createEventSurNameAdvanced+" "+createEventFirstNameAdvanced+" "+createEventPatronymicAdvanced;
        }
        // Текущий шаг
        if (!elementAdvanced.getAsJsonObject().get("currentStep").isJsonNull()) {
            currentStepAdvanced=elementAdvanced.getAsJsonObject().get("currentStep").getAsString();
            switch (currentStepAdvanced){
                case "PROCESS_END_13":
                    currentStepAdvanced="Обработка завершена";
                    break;
                case "WAIT_OUT_11":
                    currentStepAdvanced="Ожидается выдача";
                    break;
                case "PKG_IMG_WAIT_PPOZ_43":
                    currentStepAdvanced="Обработка документов в ППОЗ";
                    break;
                case "CANCELED_114":
                    currentStepAdvanced="Аннулировано";
                    break;
                case "CREATE":
                    currentStepAdvanced="Приём обращения";
                    break;
                case "ATTACH_IMAGE":
                    currentStepAdvanced="Присоединение образов";
                    break;
                default:
                    currentStepAdvanced="Неизвестно";
                    break;
            }
        }
        // Следующий шаг
        if (!elementAdvanced.getAsJsonObject().get("moveStepEvent").getAsJsonObject().get("dateWhen").isJsonNull()) {
            moveStepEventDateWhenAdvanced=elementAdvanced.getAsJsonObject().get("moveStepEvent").getAsJsonObject().get("dateWhen").getAsString();
            moveStepEventDateWhenAdvanced= convertTimeFromUnix(moveStepEventDateWhenAdvanced, "GMT+7", "dd.MM.yyyy HH:mm");
        }
        // Информация об операторе, кто перевел на следущий шаг
        if (!elementAdvanced.getAsJsonObject().get("moveStepEvent").getAsJsonObject().get("performer").isJsonNull()) {
            if(!elementAdvanced.getAsJsonObject().get("moveStepEvent").getAsJsonObject().get("performer").getAsJsonObject().get("surName").isJsonNull()){
                moveStepSurNameAdvanced=elementAdvanced.getAsJsonObject().get("moveStepEvent").getAsJsonObject().get("performer").getAsJsonObject().get("surName").getAsString();
            }
            if(!elementAdvanced.getAsJsonObject().get("moveStepEvent").getAsJsonObject().get("performer").getAsJsonObject().get("firstName").isJsonNull()){
                moveStepFirstNameAdvanced=elementAdvanced.getAsJsonObject().get("moveStepEvent").getAsJsonObject().get("performer").getAsJsonObject().get("firstName").getAsString();
            }
            if(!elementAdvanced.getAsJsonObject().get("moveStepEvent").getAsJsonObject().get("performer").getAsJsonObject().get("patronymic").isJsonNull()){
                moveStepPatronymicAdvanced=elementAdvanced.getAsJsonObject().get("moveStepEvent").getAsJsonObject().get("performer").getAsJsonObject().get("patronymic").getAsString();
            }
            if (moveStepSurNameAdvanced.equals("") && moveStepFirstNameAdvanced.equals("") && moveStepPatronymicAdvanced.equals("")){
                moveStepPerformerAdvanced="";
            } else {
                moveStepPerformerAdvanced=moveStepSurNameAdvanced+" "+moveStepFirstNameAdvanced+" "+moveStepPatronymicAdvanced;
            }
        }
        // Время исполнения
        if (!elementAdvanced.getAsJsonObject().get("executeEvent").isJsonNull()) {

            executeEventDateWhenAdvanced=elementAdvanced.getAsJsonObject().get("executeEvent").getAsJsonObject().get("dateWhen").getAsString();
            // Переводим время в нужный формат
            executeEventDateWhenAdvanced= convertTimeFromUnix(executeEventDateWhenAdvanced, "GMT+7", "dd.MM.yyyy HH:mm");
            // Получам информацию об операторе
            if(!elementAdvanced.getAsJsonObject().get("executeEvent").getAsJsonObject().get("performer").getAsJsonObject().get("surName").isJsonNull()){
                executeEventSurNameAdvanced=elementAdvanced.getAsJsonObject().get("executeEvent").getAsJsonObject().get("performer").getAsJsonObject().get("surName").getAsString();
            }
            if(!elementAdvanced.getAsJsonObject().get("executeEvent").getAsJsonObject().get("performer").getAsJsonObject().get("firstName").isJsonNull()){
                executeEventFirstNameAdvanced=elementAdvanced.getAsJsonObject().get("executeEvent").getAsJsonObject().get("performer").getAsJsonObject().get("firstName").getAsString();
            }
            if(!elementAdvanced.getAsJsonObject().get("executeEvent").getAsJsonObject().get("performer").getAsJsonObject().get("patronymic").isJsonNull()){
                executeEventPatronymicAdvanced=elementAdvanced.getAsJsonObject().get("executeEvent").getAsJsonObject().get("performer").getAsJsonObject().get("patronymic").getAsString();
            }
            if (executeEventSurNameAdvanced.equals("") && executeEventFirstNameAdvanced.equals("") && executeEventPatronymicAdvanced.equals("")){
                executeEventAdvanced="";
            } else {
                executeEventAdvanced=executeEventDateWhenAdvanced+" "+ executeEventSurNameAdvanced+" "+executeEventFirstNameAdvanced+" "+executeEventPatronymicAdvanced;
            }


        }
        // Комментарий об обращении
        if (!elementAdvanced.getAsJsonObject().get("operationComment").isJsonNull()) { operationCommentAdvanced=elementAdvanced.getAsJsonObject().get("operationComment").getAsString(); }

        // Парсинг информации по получению и представлению документов
        // Если данные есть
        if (!element.getAsJsonObject().get("givenRequestDocumentType").isJsonNull()) {
            if (!element.getAsJsonObject().get("givenRequestDocumentType").getAsJsonObject().get("typePresentation").isJsonNull()){
                // Парсим тип получения документов
                String typePresentation= element.getAsJsonObject().get("givenRequestDocumentType").getAsJsonObject().get("typePresentation").getAsString();
                String typeOutputDoc = element.getAsJsonObject().get("givenRequestDocumentType").getAsJsonObject().get("typeOutputDoc").getAsString();
                // Определяем по коду тип представления документов
                switch (typePresentation){
                    case "787003000000":
                        pres_on_MFC=true;
                        break;
                    case "787001000000":
                        pres_mail=true;
                        break;
                    default:
                        pres_indiv=true;
                        break;
                }
                // Определяем по коду тип получения документов
                switch (typeOutputDoc){
                    case "785007000000":
                        output_doc_MFC=true;
                        break;
                    case "785003000000":
                        output_doc_email=true;
                        // если тип получения документов эл почтой, то вывести адрес почты
                        if (!element.getAsJsonObject().get("givenRequestDocumentType").getAsJsonObject().get("email").isJsonNull()){
                            email_output=element.getAsJsonObject().get("givenRequestDocumentType").getAsJsonObject().get("email").getAsString();
                        }
                        break;
                    default:
                        output_doc_mail=true;
                        break;
                }
            }

        }

        // Добавление в модель по обращению всех переменных
        AppealGeneralInfoModel appealGeneralInfoModel = new AppealGeneralInfoModel(statementType,internalNum,createEventDateWhen,createEventPerformer,packageNum,
                numPPOZ, createPPOZDate, statusNotePPOZ, statusPPOZ, statusPPOZDate, routineExecutionDays,processingEndDate,
                nameAdvanced, internalNumAdvanced, createEventDateWhenAdvanced, createEventPerformerAdvanced, currentStepAdvanced, moveStepEventDateWhenAdvanced,
                moveStepPerformerAdvanced, executeEventAdvanced, operationCommentAdvanced, pres_on_MFC, pres_mail, pres_indiv, output_doc_MFC, output_doc_mail,
                output_doc_email, email_output);
        return appealGeneralInfoModel; // Возвращаем итоговый результат по обращению

    }

    // Получаем информацию о субъектах (заявителях)
    public ArrayList<String> GetApplicantSubjects(String cookie, String idAppeal, String idStatement) throws IOException {
        ArrayList<String> Applicant_and_Representive=new ArrayList<String>();
        String IdApplicant=""; // id заявителя
        String applicantTypeRequest=""; // Категория заявителя
        String IdRepresentative=""; // id представителя
        String representiveType=""; // Тип представителя
        String represDocumentTypeID=""; // Тип документа, подтверждающего полномочия представителя
        CookieStore httpCookieStore = new BasicCookieStore();
        HttpClient httpClient = null;
        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore);
        httpClient = builder.build();
        String getUrl       = "http://10.42.200.207/api/rs/appeal/"+idAppeal+"/statement/"+idStatement+"/applicants?all=0&page=0&size=10&sort=createDate,desc";// Сервер авторизации
        HttpGet httpGet = new HttpGet(getUrl);
        httpGet.setHeader("Content-type", "application/json");
        httpGet.addHeader("Cookie","JSESSIONID="+cookie);
        HttpResponse response = httpClient.execute(httpGet); // Выполняем get запрос для проверки действительности куки

        HttpEntity entity = response.getEntity();
        String result_of_req = EntityUtils.toString(entity); // Получаем результат запроса

        int status_code= response.getStatusLine().getStatusCode(); // Получаем код ответа от сервера
        System.out.println("Status GetApplicantSubject: "+status_code);
        boolean CookieValid;
        // Если код ответа 200, значит куки действителен, если 401 или другой, то недействителен
        switch (status_code){
            case 200:
                CookieValid=true;
                //SubjectApplicant=Parsing_result_getApplicant_Subject(result_of_req);
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(result_of_req); // Получение главного элемента
                if (element.getAsJsonObject().get("content").getAsJsonArray().size()==0){
                    break;
                }
                JsonArray content= element.getAsJsonObject().get("content").getAsJsonArray();
                IdApplicant=content.get(0).getAsJsonObject().get("subject").getAsString(); // id заявителя
                Applicant_and_Representive.add(IdApplicant);
                applicantTypeRequest=content.get(0).getAsJsonObject().get("applicantTypeRequest").getAsString(); // тип запроса
                Applicant_and_Representive.add(applicantTypeRequest); // Добавляем в список

                if (!content.get(0).getAsJsonObject().get("agent1").isJsonNull()) { // Если есть представитель
                    IdRepresentative=content.get(0).getAsJsonObject().get("agent1").getAsString(); // id представителя
                    Applicant_and_Representive.add(IdRepresentative); // Добавляем в список
                    if (!content.get(0).getAsJsonObject().get("agentType1").isJsonNull()) { // Тип представителя
                        representiveType=content.get(0).getAsJsonObject().get("agentType1").getAsString();
                        // Определяем тип представителя по коду
                        switch (representiveType){
                            case "356004000000":
                                representiveType= "Доверенное лицо";
                                break;
                            case "356001000000":
                                representiveType= "Опекун";
                                break;
                            case "356003000000":
                                representiveType= "Законный представитель";
                                break;
                            case "356005000000":
                                representiveType="Уполномоченное лицо";
                                break;
                            default:
                                representiveType="Иной представитель";
                                break;
                        }
                        Applicant_and_Representive.add(representiveType); // Добавляем в список
                    }
                    // Если есть документ, подтверждающий полномочия представителя
                    if (!content.get(0).getAsJsonObject().get("documentAgent1").isJsonNull()) {
                        represDocumentTypeID=content.get(0).getAsJsonObject().get("documentAgent1").getAsString(); // документ, подтверждающий полномочия представителя
                        Applicant_and_Representive.add(represDocumentTypeID); // Добавляем в список
                    }
                }
                break;
            case 401: // Запрос завершился с ошибкой
                CookieValid=false;
                cookie="";
                break;
            default:
                CookieValid=false;
                cookie="";
                break;
        }

        return Applicant_and_Representive; // Возвращаем итоговый список
    }
    // Функция для получения информации о субъекте
    public ApplicantInfoModel  GetSubjectInfo(String cookie, String idAppeal, String idSubject, String applicantTypeRequest, String representiveType, String represDocumentTypeID) throws IOException {
        String nameDoc="";
        String dateDoc="";
        ApplicantInfoModel applicantInfoModel;
        CookieStore httpCookieStore = new BasicCookieStore();
        HttpClient httpClient = null;
        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore);
        httpClient = builder.build();
        String getUrl       = "http://10.42.200.207/api/rs/appeal/"+idAppeal+"/subject/"+idSubject;// Сервер авторизации
        HttpGet httpGet = new HttpGet(getUrl);
        httpGet.setHeader("Content-type", "application/json");
        httpGet.addHeader("Cookie","JSESSIONID="+cookie);
        HttpResponse response = httpClient.execute(httpGet); // Выполняем get запрос для проверки действительности куки

        HttpEntity entity = response.getEntity();
        String result_of_req = EntityUtils.toString(entity); // Получаем результат запроса

        int status_code= response.getStatusLine().getStatusCode(); // Получаем код ответа от сервера
        System.out.println("Status GetSubjectInfo: "+status_code);
        boolean CookieValid;
        // Если код ответа 200, значит куки действителен, если 401 или другой, то недействителен
        switch (status_code){
            case 200:
                CookieValid=true;
                // Если есть представитель
                if (!representiveType.equals("") && !represDocumentTypeID.equals("")){
                    // Получаем информацию о документе
                    ArrayList<String> infoDocTypeRepres =getRepresentiveDocInfo(cookie,idAppeal,represDocumentTypeID);
                    nameDoc=infoDocTypeRepres.get(0); // Имя документа
                    dateDoc=infoDocTypeRepres.get(1); // Дата документа
                    // Парсим данные о представителе
                    applicantInfoModel = Parsing_result_GetSubjectInfo(result_of_req,"",representiveType, nameDoc, dateDoc);
                } else {
                    // Если нет представителя, то парсим данные о заявителе
                    applicantInfoModel = Parsing_result_GetSubjectInfo(result_of_req,applicantTypeRequest, "","","");
                }
                return applicantInfoModel; // Возвращаем итоговый результат
            case 401: // Запрос завершился с ошибкой
                CookieValid=false;
                cookie="";
                return null; // Возвращаем значение куки
            default:
                CookieValid=false;
                cookie="";
                return null;
        }

    }
    // Функция для получения информации о документе, подтверждающего полномочия представителя
    public ArrayList<String> getRepresentiveDocInfo(String cookie, String idAppeal, String idDocRepres) throws IOException {
        String nameDoc="";
        String dateDoc="";
        ArrayList<String> infoDocRepres=new ArrayList<String>();
        CookieStore httpCookieStore = new BasicCookieStore();
        HttpClient httpClient = null;
        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore);
        httpClient = builder.build();
        String getUrl       = "http://10.42.200.207/api/rs/appeal/"+idAppeal+"/document/"+idDocRepres;
        HttpGet httpGet = new HttpGet(getUrl);
        httpGet.setHeader("Content-type", "application/json");
        httpGet.addHeader("Cookie","JSESSIONID="+cookie);
        HttpResponse response = httpClient.execute(httpGet); // Выполняем get запрос для проверки действительности куки

        HttpEntity entity = response.getEntity();
        String result_of_req = EntityUtils.toString(entity); // Получаем результат запроса

        int status_code= response.getStatusLine().getStatusCode(); // Получаем код ответа от сервера
        System.out.println("Status GetDocRepresInfo: "+status_code);
        boolean CookieValid;
        // Если код ответа 200, значит куки действителен, если 401 или другой, то недействителен
        switch (status_code){
            case 200:
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(result_of_req); // Получение главного элемента
                // Парсим данные о документе
                if (!element.getAsJsonObject().get("name").isJsonNull()) {nameDoc = element.getAsJsonObject().get("name").getAsString();}
                if (!element.getAsJsonObject().get("date").isJsonNull()) {dateDoc = element.getAsJsonObject().get("date").getAsString();}
                dateDoc=convertDateToCorrect(dateDoc); // Конвертируем дату в нужный формат
                // Добавляем информацию в список
                infoDocRepres.add(nameDoc);
                infoDocRepres.add(dateDoc);
                return infoDocRepres; // Возвращаем итоговую информацию
            case 401: // Запрос завершился с ошибкой
              return null;
            default:
              return null;
        }

    }

    // Парсим информацию о конкретном субъекте (Заявителе или представителе)
    public ApplicantInfoModel Parsing_result_GetSubjectInfo(String json, String applicantTypeRequest, String representiveType, String nameDoc, String dateDoc){
        // Тип заявителя (Физ лицо или организация)
        String typeOfApplicant="";
        // Тип субъекта (Гражданин РФ или юр лицо)
        String subjectType="";

        // Переменные для персональных данных о заявителе (Физ.лицо)
        String descriptionBaseFormat="";
        String firstName=""; String surname=""; String patronymic="";
        String documentType=""; String documentSeries=""; String documentNum=""; String whenIssued=""; String whoIssued=""; String codeIssued="";
        String documentInfoBase=""; String documentInfoWhenWho="";
        String snils="";
        // Переменные для адреса
        String region=""; String district=""; String city=""; String street=""; String houseType=""; String house=""; String flatType=""; String flat="";
        String residenceAddress=""; // Адрес места жительства
        // Переменные для контактной информации
        String registrAddress=""; String phone=""; // Адрес по прописке, телефон
        // Категория заявителя
        String applicantCategory="";

        // Переменные для данных о заявителе (Организация)
        String descriptionBaseFormatOrg=""; String nameOrg=""; String ogrnOrg=""; String innOrg=""; String kppOrg="";
        // Переменные для адреса организации
        String regionOrg=""; String districtOrg=""; String cityOrg=""; String streetOrg="";
        String houseTypeOrg=""; String houseOrg=""; String flatTypeOrg=""; String flatOrg=""; String otherOrg="";
        // Переменные для контактной информации
        String addressOrg="";
        // Категория заявителя
        String categoryOrg="";

        // Создания экземпляра парсинга
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json); // Получение главного элемента
        // Получаем тип субъекта
        if (!element.getAsJsonObject().get("classtype").isJsonNull()) {typeOfApplicant = element.getAsJsonObject().get("classtype").getAsString();}
        if (!element.getAsJsonObject().get("subjectType").isJsonNull()) {
            subjectType = element.getAsJsonObject().get("subjectType").getAsString();
            // Определяем по коду субъекта тип субъекта
            switch (subjectType){
                case "007003001000":
                    subjectType="Гражданин РФ";
                    break;
                case "007002001000":
                    subjectType="Российское юридическое лицо";
                    break;
                case "007002004000":
                    subjectType="Российские органы власти";
                    break;
                default:
                    subjectType="Иной субъект";
                    break;
            }
        }
        // Определяем по коду типа запроса, тип запроса
        switch (applicantTypeRequest){
            case "357039000000":
                applicantCategory="Правообладатель или его законный представитель";
                break;
            case "357018000000":
                applicantCategory="Федеральные органы исполнительной власти и их территориальные органы";
                break;
            case "357006004000":
                applicantCategory="Органы внутренних дел, имеющие в производстве дела, связанные с объектами недвижимого имущества и (или) их правообладателями";
                break;
            case "357014000000":
                applicantCategory="Органы местного самоуправления";
                break;
            case "357006005000":
                applicantCategory="Органы Федеральной службы безопасности Российской Федерации, имеющие в производстве дела, связанные с объектами недвижимого имущества и (или) их правообладателями";
                break;
            case "357023000000":
                applicantCategory="Иные определенные федеральным законом органы и организации, имеющие право на бесплатное получение информации";
                break;
            case "357002000000":
                applicantCategory="Лицо, имеющее право на наследование имущества правообладателя по завещанию или по закону";
                break;
            case "357099000000":
                applicantCategory="Иное лицо";
                break;
            default:
                applicantCategory="Неизвестно";
                break;
        }
        // Смотрим на тип заявителя
        switch (typeOfApplicant){
            case "Person": // Если физ лицо
                // ФИО заявителя и паспортные данные
                if (!element.getAsJsonObject().get("descriptionBaseFormat").isJsonNull()) {descriptionBaseFormat = element.getAsJsonObject().get("descriptionBaseFormat").getAsString();}
                if (!element.getAsJsonObject().get("firstName").isJsonNull()) {firstName = element.getAsJsonObject().get("firstName").getAsString();}
                if (!element.getAsJsonObject().get("surname").isJsonNull()) {surname = element.getAsJsonObject().get("surname").getAsString();}
                if (!element.getAsJsonObject().get("patronymic").isJsonNull()) {patronymic = element.getAsJsonObject().get("patronymic").getAsString();}
                if (!element.getAsJsonObject().get("documentType").isJsonNull()) {
                    documentType = element.getAsJsonObject().get("documentType").getAsString();
                    // Определяем по коду документа тип документа
                    switch (documentType){
                        case ("008001001000"):
                            documentType="Паспорт гражданина Российской Федерации";
                            break;
                        case ("008001011000"):
                            documentType="Свидетельство о рождении";
                            break;
                        default:
                            documentType="";
                            break;
                    }
                }
                // Парсим данные о документе
                if (!element.getAsJsonObject().get("documentSeries").isJsonNull()) {documentSeries = element.getAsJsonObject().get("documentSeries").getAsString();}
                if (!element.getAsJsonObject().get("documentNum").isJsonNull()) {documentNum = element.getAsJsonObject().get("documentNum").getAsString();}
                if (!element.getAsJsonObject().get("whenIssued").isJsonNull()) {whenIssued = element.getAsJsonObject().get("whenIssued").getAsString();}
                if (!element.getAsJsonObject().get("whoIssued").isJsonNull()) {whoIssued = element.getAsJsonObject().get("whoIssued").getAsString();}
                if (!element.getAsJsonObject().get("codeIssued").isJsonNull()) {codeIssued = element.getAsJsonObject().get("codeIssued").getAsString();}
                if (!element.getAsJsonObject().get("snils").isJsonNull()) {snils = element.getAsJsonObject().get("snils").getAsString();}
                documentInfoBase= documentType+" "+documentSeries+" "+documentNum;
                whenIssued=convertDateToCorrect(whenIssued);
                documentInfoWhenWho=whenIssued+" "+whoIssued+" "+codeIssued;

                // Адрес заявителя по месту жительства
                if (!element.getAsJsonObject().get("address").getAsJsonObject().get("region").getAsJsonObject().get("name").isJsonNull() ||
                        !element.getAsJsonObject().get("address").getAsJsonObject().get("region").getAsJsonObject().get("type").isJsonNull()) {
                    region=element.getAsJsonObject().get("address").getAsJsonObject().get("region").getAsJsonObject().get("name").getAsString()+" "+
                            element.getAsJsonObject().get("address").getAsJsonObject().get("region").getAsJsonObject().get("type").getAsString();
                }
                if (!element.getAsJsonObject().get("address").getAsJsonObject().get("district").getAsJsonObject().get("name").isJsonNull() ||
                        !element.getAsJsonObject().get("address").getAsJsonObject().get("district").getAsJsonObject().get("type").isJsonNull()) {
                    district=element.getAsJsonObject().get("address").getAsJsonObject().get("district").getAsJsonObject().get("name").getAsString()+" "+
                            element.getAsJsonObject().get("address").getAsJsonObject().get("district").getAsJsonObject().get("type").getAsString();
                }
                if (!element.getAsJsonObject().get("address").getAsJsonObject().get("city").getAsJsonObject().get("name").isJsonNull() ||
                        !element.getAsJsonObject().get("address").getAsJsonObject().get("city").getAsJsonObject().get("type").isJsonNull()) {
                    city=element.getAsJsonObject().get("address").getAsJsonObject().get("city").getAsJsonObject().get("name").getAsString()+" "+
                            element.getAsJsonObject().get("address").getAsJsonObject().get("city").getAsJsonObject().get("type").getAsString();
                }
                if (!element.getAsJsonObject().get("address").getAsJsonObject().get("street").getAsJsonObject().get("name").isJsonNull() ||
                        !element.getAsJsonObject().get("address").getAsJsonObject().get("street").getAsJsonObject().get("type").isJsonNull()) {
                    street=element.getAsJsonObject().get("address").getAsJsonObject().get("street").getAsJsonObject().get("name").getAsString()+" "+
                            element.getAsJsonObject().get("address").getAsJsonObject().get("street").getAsJsonObject().get("type").getAsString();
                }

                if (!element.getAsJsonObject().get("address").getAsJsonObject().get("houseType").isJsonNull()) {
                    houseType=element.getAsJsonObject().get("address").getAsJsonObject().get("houseType").getAsString();
                }
                if (!element.getAsJsonObject().get("address").getAsJsonObject().get("house").isJsonNull()) {
                    house=element.getAsJsonObject().get("address").getAsJsonObject().get("house").getAsString();
                }

                if (!element.getAsJsonObject().get("address").getAsJsonObject().get("flatType").isJsonNull()) {
                    flatType=element.getAsJsonObject().get("address").getAsJsonObject().get("flatType").getAsString();
                }
                if (!element.getAsJsonObject().get("address").getAsJsonObject().get("flat").isJsonNull()) {
                    flat=element.getAsJsonObject().get("address").getAsJsonObject().get("flat").getAsString();
                }

                if (flatType.equals("") || flat.equals("")){
                    residenceAddress= region+", "+district+", "+city+", "+street+", "+houseType+". "+house;
                } else {
                    residenceAddress= region+", "+district+", "+city+", "+street+", "+houseType+". "+house+", "+flatType+". "+flat;
                }

                // Адрес заявителя по прописке
                if (!element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("region").getAsJsonObject().get("name").isJsonNull() ||
                        !element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("region").getAsJsonObject().get("type").isJsonNull()) {
                    region=element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("region").getAsJsonObject().get("name").getAsString()+" "+
                            element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("region").getAsJsonObject().get("type").getAsString();
                } else {region="";}
                if (!element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("district").getAsJsonObject().get("name").isJsonNull() ||
                        !element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("district").getAsJsonObject().get("type").isJsonNull()) {
                    district=element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("district").getAsJsonObject().get("name").getAsString()+" "+
                            element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("district").getAsJsonObject().get("type").getAsString();
                } else {district="";}
                if (!element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("city").getAsJsonObject().get("name").isJsonNull() ||
                        !element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("city").getAsJsonObject().get("type").isJsonNull()) {
                    city=element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("city").getAsJsonObject().get("name").getAsString()+" "+
                            element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("city").getAsJsonObject().get("type").getAsString();
                } else {city="";}
                if (!element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("street").getAsJsonObject().get("name").isJsonNull() ||
                        !element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("street").getAsJsonObject().get("type").isJsonNull()) {
                    street=element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("street").getAsJsonObject().get("name").getAsString()+" "+
                            element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("street").getAsJsonObject().get("type").getAsString();
                } else {street="";}

                if (!element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("houseType").isJsonNull()) {
                    houseType=element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("houseType").getAsString();
                } else {houseType="";}
                if (!element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("house").isJsonNull()) {
                    house=element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("house").getAsString();
                } else {house="";}

                if (!element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("flatType").isJsonNull()) {
                    flatType=element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("flatType").getAsString();
                } else {flatType="";}
                if (!element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("flat").isJsonNull()) {
                    flat=element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("flat").getAsString();
                } else {flat="";}

                if (flatType.equals("") || flat.equals("")){
                    registrAddress= region+", "+district+", "+city+", "+street+", "+houseType+". "+house;
                } else {
                    registrAddress= region+", "+district+", "+city+", "+street+", "+houseType+". "+house+", "+flatType+". "+flat;
                }
                // Телефон заявителя
                if (!element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("phone").isJsonNull()) {
                    phone=element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("phone").getAsString();
                }

                // Если есть представитель
                if (!representiveType.equals("") || !nameDoc.equals("") || !dateDoc.equals("")){
                    String confirmAuthorRepres=nameDoc+" от "+dateDoc;
                    ApplicantInfoModel applicantInfoModel=new ApplicantInfoModel(typeOfApplicant, descriptionBaseFormat,subjectType,documentInfoBase,
                            documentInfoWhenWho,snils,"",residenceAddress,registrAddress, phone,applicantCategory,representiveType,confirmAuthorRepres,
                            "","","","","","","");
                    return  applicantInfoModel;
                } else { // Если нет представителя
                    ApplicantInfoModel applicantInfoModel=new ApplicantInfoModel(typeOfApplicant, descriptionBaseFormat,subjectType,documentInfoBase,
                            documentInfoWhenWho,snils,"",residenceAddress, registrAddress, phone,applicantCategory,"","",
                            "", "","","","","","");
                    return  applicantInfoModel;
                }
            case "Organization": // Тип заявителя: организация
                // Основная информация
                if (!element.getAsJsonObject().get("descriptionBaseFormat").isJsonNull()) {descriptionBaseFormatOrg = element.getAsJsonObject().get("descriptionBaseFormat").getAsString();}
                if (!element.getAsJsonObject().get("name").isJsonNull()) {nameOrg = element.getAsJsonObject().get("name").getAsString();}
                if (!element.getAsJsonObject().get("ogrn").isJsonNull()) {ogrnOrg = element.getAsJsonObject().get("ogrn").getAsString();}
                if (!element.getAsJsonObject().get("inn").isJsonNull()) {innOrg = element.getAsJsonObject().get("inn").getAsString();}
                if (!element.getAsJsonObject().get("kpp").isJsonNull()) {kppOrg = element.getAsJsonObject().get("kpp").getAsString();}

                // Адрес организации
                if (!element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("region").getAsJsonObject().get("name").isJsonNull() ||
                        !element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("region").getAsJsonObject().get("type").isJsonNull()) {
                    regionOrg=element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("region").getAsJsonObject().get("name").getAsString()+" "+
                            element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("region").getAsJsonObject().get("type").getAsString();
                }
                if (!element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("district").getAsJsonObject().get("name").isJsonNull() ||
                        !element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("district").getAsJsonObject().get("type").isJsonNull()) {
                    districtOrg=element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("district").getAsJsonObject().get("name").getAsString()+" "+
                            element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("district").getAsJsonObject().get("type").getAsString();
                }
                if (!element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("city").getAsJsonObject().get("name").isJsonNull() ||
                        !element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("city").getAsJsonObject().get("type").isJsonNull()) {
                    cityOrg=element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("city").getAsJsonObject().get("name").getAsString()+" "+
                            element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("city").getAsJsonObject().get("type").getAsString();
                }
                if (!element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("street").getAsJsonObject().get("name").isJsonNull() ||
                        !element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("street").getAsJsonObject().get("type").isJsonNull()) {
                    streetOrg=element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("street").getAsJsonObject().get("name").getAsString()+" "+
                            element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("street").getAsJsonObject().get("type").getAsString();
                }
                // Контактная информация
                if (!element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("houseType").isJsonNull()) {
                    houseTypeOrg=element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("houseType").getAsString();
                }
                if (!element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("house").isJsonNull()) {
                    houseOrg=element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("house").getAsString();
                }

                if (!element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("flatType").isJsonNull()) {
                    flatTypeOrg=element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("flatType").getAsString();
                }
                if (!element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("flat").isJsonNull()) {
                    flatOrg=element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("flat").getAsString();
                }
                if (!element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("other").isJsonNull()) {
                    otherOrg=element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("address").getAsJsonObject().get("other").getAsString();
                }

                if (flatTypeOrg.equals("") || flatOrg.equals("")){
                    addressOrg= regionOrg+", "+districtOrg+", "+cityOrg+", "+streetOrg+", "+houseTypeOrg+". "+houseOrg+", "+otherOrg;
                } else {
                    addressOrg= regionOrg+", "+districtOrg+", "+cityOrg+", "+streetOrg+", "+houseTypeOrg+". "+houseOrg+", "+flatTypeOrg+". "+flatOrg;
                }

                categoryOrg=applicantCategory;
                // Добавляем информацию в итоговую модель данных
                ApplicantInfoModel applicantInfoModel=new ApplicantInfoModel(typeOfApplicant,"",subjectType,"",
                        "","","","","","",
                        "","","",
                        descriptionBaseFormatOrg, nameOrg,ogrnOrg,innOrg,kppOrg,addressOrg,categoryOrg);
                return  applicantInfoModel; // Возвращаем модель данных

            default:
                return null;
        }

    }



}
