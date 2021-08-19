package pkpvdplus.model;

import javafx.beans.property.SimpleStringProperty;
// Класс для отображения данных в отчёте
public class ReportModel {
    private SimpleStringProperty period; // Период, за который сформирован отчёт
    private SimpleStringProperty nameCompany; // Наименование организации
    private SimpleStringProperty numberAppeal; // Номер обращения
    private SimpleStringProperty nameAppeal; // Наименование обращения
    private SimpleStringProperty dateCreate; // Дата создания обращения
    private SimpleStringProperty status; // Статус обращения
    private SimpleStringProperty applicant; // Заявители
    private SimpleStringProperty dateEnd; // Дата окончания обработки
    private SimpleStringProperty currentStep; // Текущий шаг



    // Конструктор
    public ReportModel(String period, String nameCompany, String numberAppeal, String nameAppeal, String dateCreate, String status, String applicant, String dateEnd, String currentStep) {
        this.period =new SimpleStringProperty(period);
        this.nameCompany = new SimpleStringProperty(nameCompany);
        this.numberAppeal = new SimpleStringProperty(numberAppeal);
        this.nameAppeal = new SimpleStringProperty(nameAppeal);
        this.dateCreate = new SimpleStringProperty(dateCreate);
        this.status = new SimpleStringProperty(status);
        this.applicant = new SimpleStringProperty(applicant);
        this.dateEnd = new SimpleStringProperty(dateEnd);
        this.currentStep = new SimpleStringProperty(currentStep);

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
    public String getNumberAppeal() {
        return numberAppeal.get();
    }
    public void setNumberAppeal(String numberAppeal) {
        this.numberAppeal = new SimpleStringProperty(numberAppeal);
    }
    public String getNameAppeal() {
        return nameAppeal.get();
    }
    public void setNameAppeal(String nameAppeal) {
        this.nameAppeal = new SimpleStringProperty(nameAppeal);
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
    public String getDateEnd() {
        return dateEnd.get();
    }
    public void setDateEnd(String dateEnd) {
        this.dateEnd = new SimpleStringProperty(dateEnd);
    }
    public String getCurrentStep() {
        return currentStep.get();
    }
    public void setCurrentStep(String currentStep) {
        this.currentStep = new SimpleStringProperty(currentStep);
    }
}
