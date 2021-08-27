package pkpvdplus.model;

import javafx.beans.property.SimpleStringProperty;

public class ApplicantInfoModel {

    private SimpleStringProperty applicantFIO; // ФИО заявителя
    private SimpleStringProperty applicantSubjectType; // Тип субъекта
    private SimpleStringProperty applicantDocumentNumberSeries; // Документ заявителя: номер и серия
    private SimpleStringProperty applicantDocumentWhenIssuedAndWhoIssued; // Документ заявителя: когда и кем выдан
    private SimpleStringProperty applicantSnils; // Снилс заявителя
    private SimpleStringProperty applicantSubType; // Гражданство заявителя
    private SimpleStringProperty applicantAddress; // Адрес заявителя
    private SimpleStringProperty applicantPhoneNumber; // Номер телефона заявителя
    private SimpleStringProperty applicantCategory; // Категория заявителя

    public ApplicantInfoModel(String applicantFIO, String applicantSubjectType, String applicantDocumentNumberSeries, String applicantDocumentWhenIssuedAndWhoIssued,
                              String applicantSnils, String applicantSubType, String applicantAddress, String applicantPhoneNumber, String applicantCategory) {
        this.applicantFIO = new SimpleStringProperty(applicantFIO);
        this.applicantSubjectType = new SimpleStringProperty(applicantSubjectType);
        this.applicantDocumentNumberSeries = new SimpleStringProperty(applicantDocumentNumberSeries);
        this.applicantDocumentWhenIssuedAndWhoIssued =new SimpleStringProperty(applicantDocumentWhenIssuedAndWhoIssued);
        this.applicantSnils = new SimpleStringProperty(applicantSnils);
        this.applicantSubType = new SimpleStringProperty(applicantSubType);
        this.applicantAddress = new SimpleStringProperty(applicantAddress);
        this.applicantPhoneNumber = new SimpleStringProperty(applicantPhoneNumber);
        this.applicantCategory = new SimpleStringProperty(applicantCategory);
    }

    public String getApplicantFIO() {
        return applicantFIO.get();
    }

    public void setApplicantFIO(String applicantFIO) {
        this.applicantFIO = new SimpleStringProperty(applicantFIO);
    }

    public String getApplicantSubjectType() {
        return applicantSubjectType.get();
    }

    public void setApplicantSubjectType(String applicantSubjectType) {
        this.applicantSubjectType =new SimpleStringProperty(applicantSubjectType);
    }

    public String getApplicantDocumentNumberSeries() {
        return applicantDocumentNumberSeries.get();
    }

    public void setApplicantDocumentNumberSeries(String applicantDocumentNumberSeries) {
        this.applicantDocumentNumberSeries = new SimpleStringProperty(applicantDocumentNumberSeries);
    }

    public String getApplicantDocumentWhenIssuedAndWhoIssued() {
        return applicantDocumentWhenIssuedAndWhoIssued.get();
    }

    public void setApplicantDocumentWhenIssuedAndWhoIssued(String applicantDocumentWhenIssuedAndWhoIssued) {
        this.applicantDocumentWhenIssuedAndWhoIssued = new SimpleStringProperty(applicantDocumentWhenIssuedAndWhoIssued);
    }

    public String getApplicantSnils() {
        return applicantSnils.get();
    }

    public void setApplicantSnils(String applicantSnils) {
        this.applicantSnils = new SimpleStringProperty(applicantSnils);
    }

    public String getApplicantSubType() {
        return applicantSubType.get();
    }

    public void setApplicantSubType(String applicantSubType) {
        this.applicantSubType = new SimpleStringProperty(applicantSubType);
    }

    public String getApplicantAddress() {
        return applicantAddress.get();
    }

    public void setApplicantAddress(String applicantAddress) {
        this.applicantAddress = new SimpleStringProperty(applicantAddress);
    }

    public String getApplicantPhoneNumber() {
        return applicantPhoneNumber.get();
    }

    public void setApplicantPhoneNumber(String applicantPhoneNumber) {
        this.applicantPhoneNumber = new SimpleStringProperty(applicantPhoneNumber);
    }

    public String getApplicantCategory() {
        return applicantCategory.get();
    }

    public void setApplicantCategory(String applicantCategory) {
        this.applicantCategory = new SimpleStringProperty(applicantCategory);
    }

}
