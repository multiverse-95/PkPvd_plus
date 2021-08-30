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
import pkpvdplus.model.ApplicantInfoModel;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class GetAppealInfoController {

    // Функция для проверки действительности куки
    public ArrayList<ApplicantInfoModel> SearchAppealID(String cookie, String numberAppeal) throws IOException {
            ArrayList<String> Subject_and_Representive=new ArrayList<String>();
            String IdSubjectApplicant="";
            String IdRepresentative="";
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
                    JsonArray content= element.getAsJsonObject().get("content").getAsJsonArray();
                    String idAppeal=content.get(0).getAsJsonObject().get("id").getAsString();
                    String idStatement=GetStatementID(cookie, idAppeal);
                    Subject_and_Representive=GetApplicantSubjects(cookie, idAppeal, idStatement);
                    if (Subject_and_Representive.size()==1){
                        IdSubjectApplicant=Subject_and_Representive.get(0);
                        applicantInfoArr.add(GetSubjectInfo(cookie, idAppeal, IdSubjectApplicant));
                    } else if (Subject_and_Representive.size()==2){
                        IdSubjectApplicant=Subject_and_Representive.get(0);
                        IdRepresentative=Subject_and_Representive.get(1);
                        applicantInfoArr.add(GetSubjectInfo(cookie, idAppeal, IdSubjectApplicant));
                        applicantInfoArr.add(GetSubjectInfo(cookie, idAppeal, IdRepresentative));
                    }
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


        return applicantInfoArr; // Возвращаем значение куки
    }

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
                idStatement=content.get(0).getAsJsonObject().get("id").getAsString();
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

        return idStatement; // Возвращаем значение куки
    }

    public ArrayList<String> GetApplicantSubjects(String cookie, String idAppeal, String idStatement) throws IOException {
        ArrayList<String> Subject_and_Representive=new ArrayList<String>();
        String IdSubjectApplicant="";
        String IdRepresentative="";
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
                JsonArray content= element.getAsJsonObject().get("content").getAsJsonArray();
                IdSubjectApplicant=content.get(0).getAsJsonObject().get("subject").getAsString();
                Subject_and_Representive.add(IdSubjectApplicant);
                if (!content.get(0).getAsJsonObject().get("agent1").isJsonNull()) {
                    IdRepresentative=content.get(0).getAsJsonObject().get("agent1").getAsString();
                    Subject_and_Representive.add(IdRepresentative);
                }

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

        return Subject_and_Representive; // Возвращаем значение куки
    }

    public ApplicantInfoModel  GetSubjectInfo(String cookie, String idAppeal, String idSubject) throws IOException {
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
                applicantInfoModel = Parsing_result_GetSubjectInfo(result_of_req);
                return applicantInfoModel; // Возвращаем значение куки
            case 401: // Если выбраны организации
                CookieValid=false;
                cookie="";
                return null; // Возвращаем значение куки
            default:
                CookieValid=false;
                cookie="";
                return null;
        }
    }
    public ApplicantInfoModel Parsing_result_GetSubjectInfo(String json){
        // Переменные для персональных данных о заявителе
        String descriptionBaseFormat="";
        String firstName=""; String surname=""; String patronymic="";
        String subjectType="";
        String documentType=""; String documentSeries=""; String documentNum=""; String whenIssued=""; String whoIssued=""; String codeIssued="";
        String documentInfoBase=""; String documentInfoWhenWho="";
        String snils="";
        // Переменные для адреса
        String region=""; String district=""; String city=""; String street=""; String houseType=""; String house=""; String flatType=""; String flat="";
        String residenceAddress="";
        // Переменные для контактной информации
        String address=""; String phone="";
        // Категория заявителя
        String classtype="";
        // Создания экземпляра парсинга
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json); // Получение главного элемента
        // ФИО заявителя и паспортные данные
        if (!element.getAsJsonObject().get("descriptionBaseFormat").isJsonNull()) {descriptionBaseFormat = element.getAsJsonObject().get("descriptionBaseFormat").getAsString();}
        if (!element.getAsJsonObject().get("firstName").isJsonNull()) {firstName = element.getAsJsonObject().get("firstName").getAsString();}
        if (!element.getAsJsonObject().get("surname").isJsonNull()) {surname = element.getAsJsonObject().get("surname").getAsString();}
        if (!element.getAsJsonObject().get("patronymic").isJsonNull()) {patronymic = element.getAsJsonObject().get("patronymic").getAsString();}
        if (!element.getAsJsonObject().get("subjectType").isJsonNull()) {
            subjectType = element.getAsJsonObject().get("subjectType").getAsString();
            switch (subjectType){
                case ("007003001000"):
                    subjectType="Гражданин РФ";
                    break;
                default:
                    subjectType="";
                    break;
            }
        }
        if (!element.getAsJsonObject().get("documentType").isJsonNull()) {
            documentType = element.getAsJsonObject().get("documentType").getAsString();
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
        if (!element.getAsJsonObject().get("documentSeries").isJsonNull()) {documentSeries = element.getAsJsonObject().get("documentSeries").getAsString();}
        if (!element.getAsJsonObject().get("documentNum").isJsonNull()) {documentNum = element.getAsJsonObject().get("documentNum").getAsString();}
        if (!element.getAsJsonObject().get("whenIssued").isJsonNull()) {whenIssued = element.getAsJsonObject().get("whenIssued").getAsString();}
        if (!element.getAsJsonObject().get("whoIssued").isJsonNull()) {whoIssued = element.getAsJsonObject().get("whoIssued").getAsString();}
        if (!element.getAsJsonObject().get("codeIssued").isJsonNull()) {codeIssued = element.getAsJsonObject().get("codeIssued").getAsString();}
        if (!element.getAsJsonObject().get("snils").isJsonNull()) {snils = element.getAsJsonObject().get("snils").getAsString();}
        documentInfoBase= documentType+" "+documentSeries+" "+documentNum;
        documentInfoWhenWho=whenIssued+" "+whoIssued+" "+codeIssued;
        // Адрес заявителя
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

        if (!element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("phone").isJsonNull()) {
            phone=element.getAsJsonObject().get("contactInformation").getAsJsonObject().get("phone").getAsString();
        }
        if (!element.getAsJsonObject().get("classtype").isJsonNull()) {
            classtype=element.getAsJsonObject().get("classtype").getAsString();
            if (classtype.equals("Person")){classtype="Иное лицо";}
        }

        ApplicantInfoModel applicantInfoModel=new ApplicantInfoModel(descriptionBaseFormat,subjectType,documentInfoBase,
                documentInfoWhenWho,snils,"",residenceAddress,phone,classtype);
        return  applicantInfoModel;
        /*ArrayList<ApplicantInfoModel> applicantInfoArr=new ArrayList<ApplicantInfoModel>();
        applicantInfoArr.add(new ApplicantInfoModel(descriptionBaseFormat,subjectType,documentInfoBase,
                documentInfoWhenWho,snils,"",residenceAddress,phone,classtype));
        return applicantInfoArr;*/
    }


}
