package pkpvdplus.model;

import javafx.beans.property.SimpleStringProperty;
// Класс для отображения данных в отчёте
public class ReportModel {
    private SimpleStringProperty period; // Период, за который сформирован отчёт
    private SimpleStringProperty nameCompany; // Название организации
    private SimpleStringProperty appeal; // Номер обращения
    private SimpleStringProperty dateCreate; // Дата создания обращения
    private SimpleStringProperty status; // Статус обращения
    private SimpleStringProperty applicant; // Заявители

    // Конструктор
    public ReportModel(String period, String nameCompany, String appeal, String dateCreate, String status, String applicant) {
        this.period =new SimpleStringProperty(period);
        this.nameCompany = new SimpleStringProperty(nameCompany);
        this.appeal = new SimpleStringProperty(appeal);
        this.dateCreate = new SimpleStringProperty(dateCreate);
        this.status = new SimpleStringProperty(status);
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
    public String getStatus() {
        return status.get();
    }
    public void setStatus(String status) {
        this.status = new SimpleStringProperty(status);
    }
    public String getApplicant() {
        return applicant.get();
    }
    public void setApplicant(String applicant) {
        this.applicant = new SimpleStringProperty(applicant);
    }
}
