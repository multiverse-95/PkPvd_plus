package pkpvdplus.controller;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.apache.http.util.TextUtils;
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
    private StackPane root;

    @FXML
    private Accordion appeal_info_accord;

    @FXML
    private TitledPane main_info_appeal_info_pane;

    @FXML
    private TitledPane applicant_appeal_info_pane;

    @FXML
    private TitledPane object_info_appeal_pane;

    // Граф. элементы для заявителя
    @FXML
    private TextField fio_applicant_textf;

    @FXML
    private TextField type_sub_applicant_textf;

    @FXML
    private TextField doc_num_ser_applicant_textf;

    @FXML
    private Label whenwho_label;

    @FXML
    private TextField whowheniss_applicant_textf;

    @FXML
    private TextField snils_applicant_textf;

    @FXML
    private TextField region_applicant_textf;

    @FXML
    private TextField res_address_applicant_textf;

    @FXML
    private TextField address_applicant_textf;

    @FXML
    private TextField phone_applicant_textf;

    @FXML
    private TextField category_applicant_textf;

    // Граф. элементы для представителя заявителя
    @FXML
    private TextField fio_applicant_repres_textf;

    @FXML
    private TextField type_sub_applicant_repres_textf;

    @FXML
    private TextField doc_num_ser_applicant_repres_textf;

    @FXML
    private TextField whowheniss_applicant_repres_textf;

    @FXML
    private TextField snils_applicant_repres_textf;

    @FXML
    private TextField region_applicant_repres_textf;

    @FXML
    private TextField res_address_applicant_repres_textf;

    @FXML
    private TextField address_applicant_repres_textf;

    @FXML
    private TextField phone_applicant_repres_textf;

    @FXML
    private TextField category_applicant_repres_textf;

    @FXML
    private TextField position_applicant_repres_textf;

    @FXML
    private TextArea confirm_author_applicant_repr_textarea;

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
                System.out.println("FIO APPl from arrr "+applicantInfoArr.get(0).getApplicantFIO());

                String applicantFIO=applicantInfoArr.get(0).getApplicantFIO();
                fio_applicant_textf.setText(applicantFIO);
                String applicant_subjectType=applicantInfoArr.get(0).getApplicantSubjectType();
                type_sub_applicant_textf.setText(applicant_subjectType);

                String document_applicant_num_ser =applicantInfoArr.get(0).getApplicantDocumentNumberSeries();
                doc_num_ser_applicant_textf.setText(document_applicant_num_ser);
                String document_who_when_iss_appl=applicantInfoArr.get(0).getApplicantDocumentWhenIssuedAndWhoIssued();
                whowheniss_applicant_textf.setText(document_who_when_iss_appl);
                String snils_applicant = applicantInfoArr.get(0).getApplicantSnils();
                snils_applicant_textf.setText(snils_applicant);

                String residenceAddress=applicantInfoArr.get(0).getApplicantAddress();
                res_address_applicant_textf.setText(residenceAddress);
                address_applicant_textf.setText(residenceAddress);

                String phoneNumber=applicantInfoArr.get(0).getApplicantPhoneNumber();
                phone_applicant_textf.setText(phoneNumber);
                String categoryApplic=applicantInfoArr.get(0).getApplicantCategory();
                category_applicant_textf.setText(categoryApplic);

                appeal_info_accord.setExpandedPane(main_info_appeal_info_pane);
                //whowheniss_applicant_textf.setPrefWidth();
                //whowheniss_applicant_textf.prefColumnCountProperty().bind(whowheniss_applicant_textf.textProperty().length());
                //whenwho_label.setPrefWidth(170);
            }
        });

        // Запуск потока
        Thread ShowAppealInfoThread = new Thread(ShowAppealInfoTask);
        ShowAppealInfoThread.start();

    }
}
