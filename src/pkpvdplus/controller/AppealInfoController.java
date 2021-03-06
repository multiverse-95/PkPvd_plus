package pkpvdplus.controller;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import pkpvdplus.model.AllAppealInfoModel;
import pkpvdplus.model.AppealGeneralInfoModel;
import pkpvdplus.model.ApplicantInfoModel;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
// Класс отвечает за отображение информации об обращении
public class AppealInfoController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private StackPane root;

    @FXML
    private VBox vbox_main;

    @FXML
    private Accordion appeal_info_accord;
    // Вкладка об общей информации про обращение
    @FXML
    private TitledPane main_info_appeal_info_pane;

    @FXML
    private TextField statementType_appeal_textf;

    @FXML
    private TextField internalNum_appeal_textf;

    @FXML
    private TextField packageNum_appeal_textf;

    @FXML
    private TextField numPPOZ_appeal_textf;

    @FXML
    private TextField statusNotePPOZ_appeal_textf;

    @FXML
    private TextField routineExecutionDays_appeal_textf;

    @FXML
    private TextField processingEndDate_appeal_textf;

    @FXML
    private TextField nameAdvanced_appeal_textf;

    @FXML
    private TextField internalNumAdvanced_appeal_textf;

    @FXML
    private TextField currentStepAdvanced_appeal_textf;

    @FXML
    private TextField moveStepEventDateWhenAdvanced_appeal_texf;

    @FXML
    private TextField executeEventAdvanced_appeal_textf;

    @FXML
    private TextField operationCommentAdvanced_appeal_textf;

    // Вкладка о заявителях
    @FXML
    private TitledPane applicant_appeal_info_pane;

    @FXML
    private TabPane applicants_tabPane;

    @FXML
    private Tab applicant_tab;

    @FXML
    private Tab organiz_tab;

    @FXML
    private Tab representive_tab;

    // Граф. элементы для заявителя (физ.лицо)
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

    // Графические элементы для заявителя (организация)

    @FXML
    private TextField applicant_organiz_textf;

    @FXML
    private TextField type_sub_applicant_organiz_textf;

    @FXML
    private TextField name_organiz_textf;

    @FXML
    private TextField ogrn_organiz_textf;

    @FXML
    private TextField inn_organiz_textf;

    @FXML
    private TextField kpp_organiz_textf;

    @FXML
    private TextField address_organiz_textf;

    @FXML
    private TextField category_applicant_organiz_textf;

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

    // Вкладка: способ представления и получения документов

    @FXML
    private AnchorPane anchor_getDoc;

    @FXML
    private TitledPane given_present_doc_pane;

    @FXML
    private RadioButton pres_on_MFC_rb;

    @FXML
    private RadioButton pres_mail_rb;

    @FXML
    private RadioButton pres_indiv_rb;

    @FXML
    private RadioButton output_doc_MFC_rb;

    @FXML
    private RadioButton output_doc_mail_rb;

    @FXML
    private HBox hbox_getDocMail;

    @FXML
    private RadioButton output_doc_email_rb;

    @FXML
    private TextField email_adress_getDoc_textf;

    // Вкладка об объектах
    @FXML
    private TitledPane object_info_appeal_pane;



    @FXML
    void initialize() {
        appeal_info_accord.getPanes().remove(object_info_appeal_pane);
    }
    // Класс для потока получения информации об обращении
    public static class ShowAppealInfoTask extends Task<AllAppealInfoModel> {
        private final String cookie; // куки
        private final String numberAppeal; // Номер обращения
        // Конструктор
        public ShowAppealInfoTask(String cookie, String numberAppeal) {
            this.cookie = cookie;
            this.numberAppeal = numberAppeal;

        }
        @Override
        protected AllAppealInfoModel call() throws Exception {

            AllAppealInfoModel allAppealInfoModel=null;
            GetAppealInfoController getAppealInfoController=new GetAppealInfoController();
            // Получаем информации об обращении
            try {
                allAppealInfoModel= getAppealInfoController.GetAppealInfo(cookie,numberAppeal);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return allAppealInfoModel; // Возвращаем список с информацией
        }
    }
    // Функция для отображения информации по обращению
    public void ShowAppealInfo(String cookie, String numberAppeal){
        // Запуск прогресса индикации
        ProgressIndicator pi = new ProgressIndicator();
        VBox box = new VBox(pi);
        box.setAlignment(Pos.CENTER);

        vbox_main.setDisable(true);
        root.getChildren().add(box);
        // Поток для получения информации по обращению
        Task ShowAppealInfoTask = new ShowAppealInfoTask(cookie, numberAppeal);
        ShowAppealInfoTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                AllAppealInfoModel allAppealInfoModel;
                AppealGeneralInfoModel appealGeneralInfoModel;
                ArrayList<ApplicantInfoModel> applicantInfoArr;
                // Получаем информацию об обращении
                allAppealInfoModel= (AllAppealInfoModel) ShowAppealInfoTask.getValue();
                appealGeneralInfoModel=allAppealInfoModel.getAppealGeneralInfoModel();
                applicantInfoArr=allAppealInfoModel.getApplicantInfoArr();

                setTextAppealGeneralInfo(appealGeneralInfoModel); // Устанавливаем общую информацию по обращению
                setPresentOutputDocInfo(appealGeneralInfoModel); // Устанавливаем тип получения документов
                // Если есть данные
                if (applicantInfoArr.size()>0){
                    switch (applicantInfoArr.get(0).getTypeOfApplicant()){ // Получить тип заявителя
                        case "Person": // Если физ лицо
                            switch (applicantInfoArr.size()){ // Если есть заявитель и представитель
                                case 1: // Если только заявитель
                                    setTextApplicant(applicantInfoArr); // Установить данные о заявителе
                                    applicants_tabPane.getTabs().remove(organiz_tab); // Удалить вкладку об организации
                                    applicants_tabPane.getTabs().remove(representive_tab); // Удалить вкладку о представителе
                                    break;
                                case 2: // Если есть заявитель и представитель
                                    setTextApplicant(applicantInfoArr); // Установить данные о заявителе
                                    setTextRepresentive(applicantInfoArr); // Установить данные о представителе
                                    applicants_tabPane.getTabs().remove(organiz_tab); // Удалить вкладку об организации
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case "Organization": // Если организации
                            if (applicantInfoArr.size()==1) { // Если только заявитель
                                setTextApplicantOrganization(applicantInfoArr); // Установить данные об организации
                                applicants_tabPane.getTabs().remove(representive_tab); // Удалить вкладку о представителе
                            }
                            if (applicantInfoArr.size()==2) { // Если заявитель и представитель
                                setTextApplicantOrganization(applicantInfoArr); // Установить данные о заявителе
                                setTextRepresentive(applicantInfoArr); // Установить данные о представителе
                            }
                            applicants_tabPane.getTabs().remove(applicant_tab); // Удалить вкладку о физическом лице
                            break;
                        default:
                            break;

                    }
                } else { // Если нет данных о заявителях
                    // Заблокировать вкладки о заявителях и способах получения документов
                    applicant_appeal_info_pane.setDisable(true);
                    given_present_doc_pane.setDisable(true);
                }

                // Закрытие прогресса индикации
                box.setDisable(true);
                pi.setVisible(false);
                vbox_main.setDisable(false);
                appeal_info_accord.setExpandedPane(main_info_appeal_info_pane);
            }
        });

        // Запуск потока
        Thread ShowAppealInfoThread = new Thread(ShowAppealInfoTask);
        ShowAppealInfoThread.start();

    }
    // Функция для отображения данных об обращении
    public void setTextAppealGeneralInfo(AppealGeneralInfoModel appealGeneralInfoModel){
        // Получаем данные с модели и вставляем в текстовые поля
        String statementType_appeal=appealGeneralInfoModel.getStatementType();
        statementType_appeal_textf.setText(statementType_appeal);
        String internalNum_appeal =appealGeneralInfoModel.getInternalNum()+"; Создан: " +appealGeneralInfoModel.getCreateEventDateWhen()+" "+appealGeneralInfoModel.getCreateEventPerformer();
        internalNum_appeal_textf.setText(internalNum_appeal);
        String packageNum_appeal= appealGeneralInfoModel.getPackageNum();
        packageNum_appeal_textf.setText(packageNum_appeal);
        String numPPOZ_appeal=appealGeneralInfoModel.getNumPPOZ()+"; создан: "+appealGeneralInfoModel.getCreatePPOZDate();
        numPPOZ_appeal_textf.setText(numPPOZ_appeal);
        String statusNotePPOZ_appeal =appealGeneralInfoModel.getStatusNotePPOZ() +"; "+ appealGeneralInfoModel.getStatusPPOZ()+": "+appealGeneralInfoModel.getStatusPPOZDate();
        statusNotePPOZ_appeal_textf.setText(statusNotePPOZ_appeal);
        String routineExecutionDays_appeal=appealGeneralInfoModel.getRoutineExecutionDays();
        routineExecutionDays_appeal_textf.setText(routineExecutionDays_appeal);
        String processingEndDate_appeal=appealGeneralInfoModel.getProcessingEndDate();
        processingEndDate_appeal_textf.setText(processingEndDate_appeal);

        String nameAdvanced_appeal=appealGeneralInfoModel.getNameAdvanced();
        nameAdvanced_appeal_textf.setText(nameAdvanced_appeal);
        String internalNumAdvanced_appeal=appealGeneralInfoModel.getInternalNumAdvanced()+"; Создан: "+appealGeneralInfoModel.getCreateEventDateWhenAdvanced()+" "+
                appealGeneralInfoModel.getCreateEventPerformerAdvanced();
        internalNumAdvanced_appeal_textf.setText(internalNumAdvanced_appeal);
        String currentStepAdvanced_appeal=appealGeneralInfoModel.getCurrentStepAdvanced();
        currentStepAdvanced_appeal_textf.setText(currentStepAdvanced_appeal);
        String moveStepEventDateWhenAdvanced_appeal = appealGeneralInfoModel.getMoveStepEventDateWhenAdvanced()+ " "+appealGeneralInfoModel.getMoveStepPerformerAdvanced();
        moveStepEventDateWhenAdvanced_appeal_texf.setText(moveStepEventDateWhenAdvanced_appeal);
        String executeEventAdvanced_appeal = appealGeneralInfoModel.getExecuteEventAdvanced();
        executeEventAdvanced_appeal_textf.setText(executeEventAdvanced_appeal);
        String operationCommentAdvanced_appeal = appealGeneralInfoModel.getOperationCommentAdvanced();
        if (operationCommentAdvanced_appeal.equals("")){
            operationCommentAdvanced_appeal_textf.setText("Комментарий отсутствует");
        } else {
            operationCommentAdvanced_appeal_textf.setText(operationCommentAdvanced_appeal);
        }

    }
    // Функция для отображения данных о способе получения документов
    public void setPresentOutputDocInfo(AppealGeneralInfoModel appealGeneralInfoModel){
        // Получаем данные с модели
        // Устанавливаем нужные переключатели
        // Если тип представления документов в МФЦ
        if (appealGeneralInfoModel.isPres_on_MFC()){
            // Устанавливаем переключатель для мфц
            pres_on_MFC_rb.setSelected(true);
            pres_on_MFC_rb.setOnAction(event -> {
                pres_on_MFC_rb.setSelected(true);
            });
            pres_mail_rb.setDisable(true);
            pres_indiv_rb.setDisable(true);
        }
        // Если тип представления документов почтой
        if (appealGeneralInfoModel.isPres_mail()) {
            // Устанавливаем тип документов почтой
            pres_mail_rb.setSelected(true);
            pres_mail_rb.setOnAction(event -> {
                pres_mail_rb.setSelected(true);
            });
            pres_on_MFC_rb.setDisable(true);
            pres_indiv_rb.setDisable(true);
        }
        // Если тип представления документов индивидуальный
        if (appealGeneralInfoModel.isPres_indiv()) {
            // Устанавливаем тип документов индивидульный
            pres_indiv_rb.setSelected(true);
            pres_indiv_rb.setOnAction(event -> {
                pres_indiv_rb.setSelected(true);
            });
            pres_on_MFC_rb.setDisable(true);
            pres_mail_rb.setDisable(true);
        }
        // Если способ получения документов в мфц
        if (appealGeneralInfoModel.isOutput_doc_MFC()) {
            // Устанавливаем переключатель для мфц
            output_doc_MFC_rb.setSelected(true);
            hbox_getDocMail.getChildren().clear();
            output_doc_MFC_rb.setOnAction(event -> {
                output_doc_MFC_rb.setSelected(true);
            });
            output_doc_mail_rb.setDisable(true);
            output_doc_email_rb.setDisable(true);
        }
        // Если способ получения почтой
        if (appealGeneralInfoModel.isOutput_doc_mail()){
            // Устанавливаем переключатель для почтый
            output_doc_mail_rb.setSelected(true);
            output_doc_mail_rb.setOnAction(event -> {
                output_doc_mail_rb.setSelected(true);
            });
            output_doc_MFC_rb.setDisable(true);
            output_doc_email_rb.setDisable(true);
        }
        // Если способ получения эл почтой
        if (appealGeneralInfoModel.isOutput_doc_email()){
            // Устанавливаем переключатель эл почтой
            output_doc_email_rb.setSelected(true);
            // Устанавливаем наименование эл почты
            email_adress_getDoc_textf.setText(appealGeneralInfoModel.getOutput_email());
            output_doc_email_rb.setOnAction(event -> {
                output_doc_email_rb.setSelected(true);
            });
            output_doc_MFC_rb.setDisable(true);
            output_doc_mail_rb.setDisable(true);
        }

    }
    // Функция для отображения информации о заявителе
    public void setTextApplicant(ArrayList<ApplicantInfoModel> applicantInfoArr){
        // Получаем данные со списка и устанавливаем в текстовые поля
        // Получаем данные о заявителе
        System.out.println("FIO APPl from arrr "+applicantInfoArr.get(0).getApplicantFIO());
        String applicantFIO=applicantInfoArr.get(0).getApplicantFIO();
        String applicant_subjectType=applicantInfoArr.get(0).getApplicantSubjectType();
        String document_applicant_num_ser =applicantInfoArr.get(0).getApplicantDocumentNumberSeries();
        String document_who_when_iss_appl=applicantInfoArr.get(0).getApplicantDocumentWhenIssuedAndWhoIssued();
        String snils_applicant = applicantInfoArr.get(0).getApplicantSnils();
        String residenceAddress=applicantInfoArr.get(0).getApplicantResidenceAddress();
        String registrAddress=applicantInfoArr.get(0).getApplicantRegistrAddress();
        String phoneNumber=applicantInfoArr.get(0).getApplicantPhoneNumber();
        String categoryApplic=applicantInfoArr.get(0).getApplicantCategory();
        // Устанавливаем данные для заявителя
        fio_applicant_textf.setText(applicantFIO);
        type_sub_applicant_textf.setText(applicant_subjectType);
        doc_num_ser_applicant_textf.setText(document_applicant_num_ser);
        whowheniss_applicant_textf.setText(document_who_when_iss_appl);
        snils_applicant_textf.setText(snils_applicant);
        res_address_applicant_textf.setText(residenceAddress);
        address_applicant_textf.setText(registrAddress);
        phone_applicant_textf.setText(phoneNumber);
        category_applicant_textf.setText(categoryApplic);
    }
    // Функция для отображения информации об организации
    public void setTextApplicantOrganization(ArrayList<ApplicantInfoModel> applicantInfoArr){
        // Получаем информацию со списка
        // Получаем данные об организации
        String applicant_organiz=applicantInfoArr.get(0).getApplicantOrg();
        String type_sub_applicant_organiz=applicantInfoArr.get(0).getApplicantSubjectType();
        String name_organiz=applicantInfoArr.get(0).getNameOrg();
        String ogrn_organiz=applicantInfoArr.get(0).getOgrnOrg();
        String inn_organiz=applicantInfoArr.get(0).getInnOrg();
        String kpp_organiz=applicantInfoArr.get(0).getKppOrg();
        String address_organiz=applicantInfoArr.get(0).getAddressOrg();
        String category_applicant_organiz=applicantInfoArr.get(0).getCategoryOrg();
        // Устанавливаем в текстовые поля
        applicant_organiz_textf.setText(applicant_organiz);
        type_sub_applicant_organiz_textf.setText(type_sub_applicant_organiz);
        name_organiz_textf.setText(name_organiz);
        ogrn_organiz_textf.setText(ogrn_organiz);
        inn_organiz_textf.setText(inn_organiz);
        kpp_organiz_textf.setText(kpp_organiz);
        address_organiz_textf.setText(address_organiz);
        category_applicant_organiz_textf.setText(category_applicant_organiz);

    }
    // Функция для отображения данных о представителе
    public void setTextRepresentive(ArrayList<ApplicantInfoModel> applicantInfoArr){
        // Получаем данные со списка
        // Получаем данные о представителе
        System.out.println("FIO REPRES from arrr "+applicantInfoArr.get(1).getApplicantFIO());
        String representiveFIO=applicantInfoArr.get(1).getApplicantFIO();
        String representive_subjectType=applicantInfoArr.get(1).getApplicantSubjectType();
        String document_representive_num_ser =applicantInfoArr.get(1).getApplicantDocumentNumberSeries();
        String document_who_when_iss_repres=applicantInfoArr.get(1).getApplicantDocumentWhenIssuedAndWhoIssued();
        String snils_repres = applicantInfoArr.get(1).getApplicantSnils();
        String residenceAddressRepres=applicantInfoArr.get(1).getApplicantResidenceAddress();
        String registrAddressRepres=applicantInfoArr.get(1).getApplicantRegistrAddress();
        String phoneNumberRepres=applicantInfoArr.get(1).getApplicantPhoneNumber();
        String categoryRepres=applicantInfoArr.get(1).getRepresentiveType();
        String confirmAuthorRepres=applicantInfoArr.get(1).getConfirmAuthorRepres();
        // Устанавливаем данные о представителе в текстовые поля
        fio_applicant_repres_textf.setText(representiveFIO);
        type_sub_applicant_repres_textf.setText(representive_subjectType);
        doc_num_ser_applicant_repres_textf.setText(document_representive_num_ser);
        whowheniss_applicant_repres_textf.setText(document_who_when_iss_repres);
        snils_applicant_repres_textf.setText(snils_repres);
        //region_applicant_repres_textf.setText();
        res_address_applicant_repres_textf.setText(residenceAddressRepres);
        address_applicant_repres_textf.setText(registrAddressRepres);
        phone_applicant_repres_textf.setText(phoneNumberRepres);
        category_applicant_repres_textf.setText(categoryRepres);
        //position_applicant_repres_textf.setText();
        confirm_author_applicant_repr_textarea.setText(confirmAuthorRepres);
    }
}
