package pkpvdplus.model;

import java.util.ArrayList;

public class AllAppealInfoModel {
    private AppealGeneralInfoModel appealGeneralInfoModel;
    private ArrayList<ApplicantInfoModel> applicantInfoArr;

    public AllAppealInfoModel(AppealGeneralInfoModel appealGeneralInfoModel, ArrayList<ApplicantInfoModel> applicantInfoArr) {
        this.appealGeneralInfoModel = appealGeneralInfoModel;
        this.applicantInfoArr = applicantInfoArr;
    }

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
