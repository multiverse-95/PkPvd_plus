package pkpvdplus.model;

import java.util.ArrayList;
// Модель для отображения информации об обращении и информации о заявителях
public class AllAppealInfoModel {
    private AppealGeneralInfoModel appealGeneralInfoModel; // Информация об обращении
    private ArrayList<ApplicantInfoModel> applicantInfoArr; // Информация о заявителях
    // Конструктор
    public AllAppealInfoModel(AppealGeneralInfoModel appealGeneralInfoModel, ArrayList<ApplicantInfoModel> applicantInfoArr) {
        this.appealGeneralInfoModel = appealGeneralInfoModel;
        this.applicantInfoArr = applicantInfoArr;
    }
    // Геттеры и сеттеры
    public AppealGeneralInfoModel getAppealGeneralInfoModel() {
        return appealGeneralInfoModel;
    }

    public void setAppealGeneralInfoModel(AppealGeneralInfoModel appealGeneralInfoModel) {
        this.appealGeneralInfoModel = appealGeneralInfoModel;
    }

    public ArrayList<ApplicantInfoModel> getApplicantInfoArr() {
        return applicantInfoArr;
    }

    public void setApplicantInfoArr(ArrayList<ApplicantInfoModel> applicantInfoArr) {
        this.applicantInfoArr = applicantInfoArr;
    }
}
