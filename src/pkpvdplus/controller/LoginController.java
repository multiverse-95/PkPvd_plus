package pkpvdplus.controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.concurrent.Task;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import pkpvdplus.model.LoginModel;
import pkpvdplus.model.SettingsModel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// Контроллер для авторизации в АИС
public class LoginController {

    public static class LoginTask extends Task<String> {
        private final String username;
        private final String password;
        private final boolean isCheckBoxSel;

        public LoginTask(String username, String password, boolean isCheckBoxSel) {
            this.username = username;
            this.password = password;
            this.isCheckBoxSel=isCheckBoxSel;
        }
        @Override
        protected String call() throws Exception {
            /*ArrayList<String> results=new ArrayList<String>();
            results.add("");
            results.add("");*/
            ArrayList<String> results=Autoriz(username,password);
            String cookie=results.get(0);
            String resultAutoriz=results.get(1);
            if (resultAutoriz.isEmpty()){
                System.out.println("Login correct!");
                SaveAutoriz(username, password, cookie, isCheckBoxSel);
            } else {
                System.out.println("Login is not correct!");
                cookie="";

            }
            return cookie;
        }
    }

    private static void SaveAutoriz(String login, String password, String cookie , boolean isCheckBoxSel){
        SettingsModel settingsModel=new SettingsModel(login, password, cookie,"", isCheckBoxSel);
        settingsModel.setLogin(login);
        settingsModel.setPassword(password);
        settingsModel.setCookie(cookie);
        settingsModel.setCheckBoxSel(isCheckBoxSel);
        Gson gson = new Gson();
        //StringEntity postingString = new StringEntity(gson.toJson(settingsModel), StandardCharsets.UTF_8);//gson.tojson() converts your payload to json
        System.out.println("json settings: "+ gson.toJson(settingsModel));
        File f = new File("C:\\pkpvdplus");
        try{
            if(f.mkdir()) {
                System.out.println("Directory Created");
                String content=gson.toJson(settingsModel);
                File file=new File("C:\\pkpvdplus\\settingsPVD.json");
                try {
                    FileWriter fileWriter = null;
                    fileWriter = new FileWriter(file);
                    fileWriter.write(content);
                    fileWriter.close();
                } catch (IOException ex) {
                    Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                System.out.println("Directory is not created");
                SaveSettings(login, password, isCheckBoxSel);

            }
        } catch(Exception e){
            e.printStackTrace();
        }



    }

    // Функция для начальной авторизации
    public static ArrayList<String> Autoriz(String username, String password) throws IOException {
        LoginModel loginModel = new LoginModel(username, password);

        String login_user=loginModel.getLogin();
        String password_user=loginModel.getPassword();
        System.out.println(login_user+" "+password_user);

        CookieStore httpCookieStore = new BasicCookieStore();
        HttpClient httpClient = null;
        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore);
        httpClient = builder.build();
        String postUrl       = "http://10.42.200.207/api/rs/login?returi=http%3A%2F%2F10.42.200.207%2Fhelp";// put in your url
        HttpPost post = new HttpPost(postUrl);
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("redirect", "http://10.42.200.207/help"));
        params.add(new BasicNameValuePair("username", login_user));
        params.add(new BasicNameValuePair("password", password_user));
        params.add(new BasicNameValuePair("commit", "Войти"));
        post.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

        HttpResponse response = httpClient.execute(post);
        List<Cookie> cookies = httpCookieStore.getCookies();
        String cookie=cookies.get(0).getValue();

        HttpEntity entity = response.getEntity();
        String result_of_req = EntityUtils.toString(entity);
        ArrayList<String> results=new ArrayList<String>();
        results.add(cookie);
        results.add(result_of_req);
        return results;

    }

    public static void SaveSettings(String login, String password, boolean isCheckBoxSel){
        // setlasstput info
        File fileJson = new File("C:\\pkpvdplus\\settingsPVD.json");
            JsonParser parser = new JsonParser();
            JsonElement jsontree = null;
            try {
                jsontree = parser.parse(new FileReader("C:\\pkpvdplus\\settingsPVD.json"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            JsonObject jsonObject = jsontree.getAsJsonObject();
            //String login = jsonObject.get("login").getAsString();
            //String password = jsonObject.get("password").getAsString();
            String cookie = jsonObject.get("cookie").getAsString();
            String lastPathToFile = jsonObject.get("lastPathToFile").getAsString();
            //boolean isCheckBoxSel = jsonObject.get("isCheckBoxSel").getAsBoolean();
            SettingsModel settingsModel = new SettingsModel(login, password, cookie, lastPathToFile, isCheckBoxSel);
            settingsModel.setLogin(login);
            settingsModel.setPassword(password);
            settingsModel.setCookie(cookie);
            settingsModel.setLastPathToFile(lastPathToFile);
            settingsModel.setCheckBoxSel(isCheckBoxSel);

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

    }


}