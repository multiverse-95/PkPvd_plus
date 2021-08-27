package pkpvdplus.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
            //System.out.println("Status cookie autor: "+status_code);
            boolean CookieValid;
            // Если код ответа 200, значит куки действителен, если 401 или другой, то недействителен
            switch (status_code){
                case 200:
                    CookieValid=true;
                    String idAppeal=Parsing_result_searchAppealID(result_of_req);
                    String idStatement=GetStatementID(cookie, idAppeal);
                    String SubjectApplicant=GetApplicantSubject(cookie, idAppeal, idStatement);
                    applicantInfoArr=GetSubjectInfo(cookie, idAppeal, SubjectApplicant);
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

    public String Parsing_result_searchAppealID(String json){
        // Создания экземпляра парсинга
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json); // Получение главного элемента

        JsonArray content= element.getAsJsonObject().get("content").getAsJsonArray();
        String idAppeal=content.get(0).getAsJsonObject().get("id").getAsString();

        System.out.println("ID of appeal: "+idAppeal);
        return idAppeal;
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
        //System.out.println("Status cookie autor: "+status_code);
        boolean CookieValid;
        // Если код ответа 200, значит куки действителен, если 401 или другой, то недействителен
        switch (status_code){
            case 200:
                CookieValid=true;
                idStatement=Parsing_result_getStatementID(result_of_req);
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
    public String Parsing_result_getStatementID(String json){
        // Создания экземпляра парсинга
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json); // Получение главного элемента

        JsonArray content= element.getAsJsonObject().get("content").getAsJsonArray();
        String idStatement=content.get(0).getAsJsonObject().get("id").getAsString();

        System.out.println("ID of statement: "+idStatement);
        return idStatement;
    }


    public String GetApplicantSubject(String cookie, String idAppeal, String idStatement) throws IOException {
        String SubjectApplicant="";
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
        //System.out.println("Status cookie autor: "+status_code);
        boolean CookieValid;
        // Если код ответа 200, значит куки действителен, если 401 или другой, то недействителен
        switch (status_code){
            case 200:
                CookieValid=true;
                SubjectApplicant=Parsing_result_getApplicant_Subject(result_of_req);
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

        return SubjectApplicant; // Возвращаем значение куки
    }
    public String Parsing_result_getApplicant_Subject(String json){
        // Создания экземпляра парсинга
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json); // Получение главного элемента

        JsonArray content= element.getAsJsonObject().get("content").getAsJsonArray();
        String idStatement=content.get(0).getAsJsonObject().get("subject").getAsString();

        System.out.println("Subject of applicant: "+idStatement);
        return idStatement;
    }

    public ArrayList<ApplicantInfoModel> GetSubjectInfo(String cookie, String idAppeal, String idSubject) throws IOException {
        ArrayList<ApplicantInfoModel> applicantInfoArr=new ArrayList<ApplicantInfoModel>();
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
        //System.out.println("Status cookie autor: "+status_code);
        boolean CookieValid;
        // Если код ответа 200, значит куки действителен, если 401 или другой, то недействителен
        switch (status_code){
            case 200:
                CookieValid=true;
                applicantInfoArr=Parsing_result_GetSubjectInfo(result_of_req);
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
    public ArrayList<ApplicantInfoModel> Parsing_result_GetSubjectInfo(String json){
        // Создания экземпляра парсинга
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json); // Получение главного элемента

        String descriptionBaseFormat=element.getAsJsonObject().get("descriptionBaseFormat").getAsString();
        String firstName=element.getAsJsonObject().get("firstName").getAsString();
        String surname=element.getAsJsonObject().get("surname").getAsString();
        String patronymic=element.getAsJsonObject().get("patronymic").getAsString();

        String fio= surname+" "+firstName+" "+patronymic;
        System.out.println("Applicant FIO: "+descriptionBaseFormat+"\n");
        ArrayList<ApplicantInfoModel> applicantInfoArr=new ArrayList<ApplicantInfoModel>();
        applicantInfoArr.add(new ApplicantInfoModel(descriptionBaseFormat,"","","","",
                "","","",""));
        return applicantInfoArr;
    }


}
