package pkpvdplus.controller;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import pkpvdplus.model.ApplicantInfoModel;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AppealInfoController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField fio_applicant_textf;

    @FXML
    void initialize() {

    }

    public static class ShowAppealInfoTask extends Task<ArrayList<ApplicantInfoModel>> {
        private final String cookie; // куки
        private final String numberAppeal; // Номер обращения

        public ShowAppealInfoTask(String cookie, String numberAppeal) {
            this.cookie = cookie;
            this.numberAppeal = numberAppeal;

        }
        @Override
        protected ArrayList<ApplicantInfoModel> call() throws Exception {
            ArrayList<ApplicantInfoModel> applicantInfoArr=new ArrayList<ApplicantInfoModel>();
            GetAppealInfoController getAppealInfoController=new GetAppealInfoController();
            try {
                applicantInfoArr= getAppealInfoController.SearchAppealID(cookie,numberAppeal);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return applicantInfoArr;
        }
    }

    public void ShowAppealInfo(String cookie, String numberAppeal){
        Task ShowAppealInfoTask = new ShowAppealInfoTask(cookie, numberAppeal);
        ShowAppealInfoTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                ArrayList<ApplicantInfoModel> applicantInfoArr=new ArrayList<ApplicantInfoModel>();
                applicantInfoArr= (ArrayList<ApplicantInfoModel>) ShowAppealInfoTask.getValue();
                String applicantFIO=applicantInfoArr.get(0).getApplicantFIO();
                fio_applicant_textf.setText(applicantFIO);
            }
        });

        // Запуск потока
        Thread ShowAppealInfoThread = new Thread(ShowAppealInfoTask);
        ShowAppealInfoThread.start();

    }
}
