package pkpvdplus.model;

import javafx.beans.property.SimpleStringProperty;

public class ReportModel {
    private SimpleStringProperty period;
    private SimpleStringProperty nameCompany;
    private SimpleStringProperty appeal;
    private SimpleStringProperty dateCreate;
    private SimpleStringProperty action;
    private SimpleStringProperty applicant;

    public ReportModel(String period, String nameCompany, String appeal, String dateCreate, String action, String applicant) {
        this.period =new SimpleStringProperty(period);
        this.nameCompany = new SimpleStringProperty(nameCompany);
        this.appeal = new SimpleStringProperty(appeal);
        this.dateCreate = new SimpleStringProperty(dateCreate);
        this.action = new SimpleStringProperty(action);
        this.applicant = new SimpleStringProperty(applicant);
    }

    // Геттеры и сеттеры
    public String getPeriod() {
        return period.get();
    }
    public void setPeriod(String period) {
        this.period = new SimpleStringProperty(period);
    }
    public String getNameCompany() {
        return nameCompany.get();
    }
    public void setNameCompany(String nameCompany) {
        this.nameCompany = new SimpleStringProperty(nameCompany);
    }
    public String getAppeal() {
        return appeal.get();
    }
    public void setAppeal(String appeal) {
        this.appeal = new SimpleStringProperty(appeal);
    }
    public String getDateCreate() {
        return dateCreate.get();
    }
    public void setDateCreate(String dateCreate) {
        this.dateCreate = new SimpleStringProperty(dateCreate);
    }
    public String getAction() {
        return action.get();
    }
    public void setAction(String action) {
        this.action = new SimpleStringProperty(action);
    }
    public String getApplicant() {
        return applicant.get();
    }
    public void setApplicant(String applicant) {
        this.applicant = new SimpleStringProperty(applicant);
    }
}
