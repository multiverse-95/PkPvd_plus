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
    public ArrayList<ApplicantInfoModel> GetAppealInfo(String cookie, String numberAppeal) throws IOException {
            ArrayList<String> Applicant_and_Representive=new ArrayList<String>();
            String IdApplicant="";
            String IdRepresentative="";
            String representiveType="";
            String represDocumentTypeID="";
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
                    Applicant_and_Representive=GetApplicantSubjects(cookie, idAppeal, idStatement);

                    switch (Applicant_and_Representive.size()){
                        case 1:
                            IdApplicant=Applicant_and_Representive.get(0);
                            applicantInfoArr.add(GetSubjectInfo(cookie, idAppeal, IdApplicant,"",""));
                            break;
                        case 2:
                            IdApplicant=Applicant_and_Representive.get(0);
                            IdRepresentative=Applicant_and_Representive.get(1);
                            applicantInfoArr.add(GetSubjectInfo(cookie, idAppeal, IdApplicant,"",""));
                            applicantInfoArr.add(GetSubjectInfo(cookie, idAppeal, IdRepresentative, "", ""));
                            break;
                        case 3:
                            IdApplicant=Applicant_and_Representive.get(0);
                            IdRepresentative=Applicant_and_Representive.get(1);
                            representiveType=Applicant_and_Representive.get(2);
                            applicantInfoArr.add(GetSubjectInfo(cookie, idAppeal, IdApplicant,"",""));
                            applicantInfoArr.add(GetSubjectInfo(cookie, idAppeal, IdRepresentative, representiveType, ""));
                            break;
                        case 4:
                            IdApplicant=Applicant_and_Representive.get(0);
                            IdRepresentative=Applicant_and_Representive.get(1);
                            representiveType=Applicant_and_Representive.get(2);
                            represDocumentTypeID=Applicant_and_Representive.get(3);
                            applicantInfoArr.add(GetSubjectInfo(cookie, idAppeal, IdApplicant,"",""));
                            applicantInfoArr.add(GetSubjectInfo(cookie, idAppeal, IdRepresentative, representiveType, represDocumentTypeID));
                            break;
                        default:
                            return null;
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

    public ArrayList<String> GetApplicantSubjects(String cookie, String idAppeal, String idStatement) throws IOException {
        ArrayList<String> Applicant_and_Representive=new ArrayList<String>();
        String IdApplicant="";
        String IdRepresentative="";
        String representiveType="";
        String represDocumentTypeID="";
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
                IdApplicant=content.get(0).getAsJsonObject().get("subject").getAsString();
                Applicant_and_Representive.add(IdApplicant);

                if (!content.get(0).getAsJsonObject().get("agent1").isJsonNull()) {
                    IdRepresentative=content.get(0).getAsJsonObject().get("agent1").getAsString();
                    Applicant_and_Representive.add(IdRepresentative);
                    if (!content.get(0).getAsJsonObject().get("agentType1").isJsonNull()) {
                        representiveType=content.get(0).getAsJsonObject().get("agentType1").getAsString();
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
                            default:
                                representiveType="Иной представитель";
                                break;
                        }
                        Applicant_and_Representive.add(representiveType);
                    }
                    if (!content.get(0).getAsJsonObject().get("documentAgent1").isJsonNull()) {
                        represDocumentTypeID=content.get(0).getAsJsonObject().get("documentAgent1").getAsString();
                        Applicant_and_Representive.add(represDocumentTypeID);
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

        return Applicant_and_Representive; // Возвращаем значение куки
    }

    public ApplicantInfoModel  GetSubjectInfo(String cookie, String idAppeal, String idSubject, String representiveType, String represDocumentTypeID) throws IOException {
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
                if (!representiveType.equals("") && !represDocumentTypeID.equals("")){
                    ArrayList<String> infoDocTypeRepres =getRepresentiveDocInfo(cookie,idAppeal,represDocumentTypeID);
                    nameDoc=infoDocTypeRepres.get(0);
                    dateDoc=infoDocTypeRepres.get(1);
                    applicantInfoModel = Parsing_result_GetSubjectInfo(result_of_req,representiveType, nameDoc, dateDoc);
                } else {
                    applicantInfoModel = Parsing_result_GetSubjectInfo(result_of_req, "","","");
                }
                return applicantInfoModel;
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
                if (!element.getAsJsonObject().get("name").isJsonNull()) {nameDoc = element.getAsJsonObject().get("name").getAsString();}
                if (!element.getAsJsonObject().get("date").isJsonNull()) {dateDoc = element.getAsJsonObject().get("date").getAsString();}
                infoDocRepres.add(nameDoc);
                infoDocRepres.add(dateDoc);
                return infoDocRepres;
            case 401: // Запрос завершился с ошибкой
              return null;
            default:
              return null;
        }

    }
    public ApplicantInfoModel Parsing_result_GetSubjectInfo(String json, String representiveType, String nameDoc, String dateDoc){
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
        String residenceAddress="";
        // Переменные для контактной информации
        String address=""; String phone="";
        // Категория заявителя
        String classtype="";

        // Переменные для данных о заявителе (Организация)
        String descriptionBaseFormatOrg=""; String nameOrg=""; String ogrnOrg=""; String innOrg=""; String kppOrg="";
        // Переменные для адреса организации
        String regionOrg=""; String districtOrg=""; String cityOrg=""; String streetOrg="";
        String houseTypeOrg=""; String houseOrg=""; String flatTypeOrg=""; String flatOrg=""; String otherOrg="";
        // Переменные для контактной информации
        String addressOrg="";
        // Категория заявителя
        String classtypeOrg="";

        // Создания экземпляра парсинга
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json); // Получение главного элемента
        if (!element.getAsJsonObject().get("classtype").isJsonNull()) {typeOfApplicant = element.getAsJsonObject().get("classtype").getAsString();}
        if (!element.getAsJsonObject().get("subjectType").isJsonNull()) {
            subjectType = element.getAsJsonObject().get("subjectType").getAsString();
            switch (subjectType){
                case ("007003001000"):
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
        switch (typeOfApplicant){
            case "Person":
                // ФИО заявителя и паспортные данные
                if (!element.getAsJsonObject().get("descriptionBaseFormat").isJsonNull()) {descriptionBaseFormat = element.getAsJsonObject().get("descriptionBaseFormat").getAsString();}
                if (!element.getAsJsonObject().get("firstName").isJsonNull()) {firstName = element.getAsJsonObject().get("firstName").getAsString();}
                if (!element.getAsJsonObject().get("surname").isJsonNull()) {surname = element.getAsJsonObject().get("surname").getAsString();}
                if (!element.getAsJsonObject().get("patronymic").isJsonNull()) {patronymic = element.getAsJsonObject().get("patronymic").getAsString();}
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

                // Если есть представитель
                if (!representiveType.equals("") || !nameDoc.equals("") || !dateDoc.equals("")){
                    String confirmAuthorRepres=nameDoc+" от "+dateDoc;
                    ApplicantInfoModel applicantInfoModel=new ApplicantInfoModel(typeOfApplicant, descriptionBaseFormat,subjectType,documentInfoBase,
                            documentInfoWhenWho,snils,"",residenceAddress,phone,classtype,representiveType,confirmAuthorRepres,
                            "","","","","","","");
                    return  applicantInfoModel;
                } else { // Если нет представителя
                    ApplicantInfoModel applicantInfoModel=new ApplicantInfoModel(typeOfApplicant, descriptionBaseFormat,subjectType,documentInfoBase,
                            documentInfoWhenWho,snils,"",residenceAddress,phone,classtype,"","",
                            "", "","","","","","");
                    return  applicantInfoModel;
                }
            case "Organization":
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

                switch (subjectType){
                    case "Российское юридическое лицо":
                        classtypeOrg="Правообладатель или его законный представитель";
                        break;
                    case "Российские органы власти":
                        classtypeOrg="Органы местного самоуправления";
                        break;
                    default:
                        classtypeOrg="Другое";
                        break;
                }

                ApplicantInfoModel applicantInfoModel=new ApplicantInfoModel(typeOfApplicant,"",subjectType,"",
                        "","","","","","","","",
                        descriptionBaseFormatOrg, nameOrg,ogrnOrg,innOrg,kppOrg,addressOrg,classtypeOrg);
                return  applicantInfoModel;

            default:
                return null;
        }

    }

}
