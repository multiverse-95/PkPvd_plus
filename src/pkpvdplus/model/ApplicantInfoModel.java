package pkpvdplus.model;

import javafx.beans.property.SimpleStringProperty;

public class ApplicantInfoModel {
    private SimpleStringProperty typeOfApplicant; // Тип заявителя (Физ. лицо или организация)

    // Переменные для физ. лиц и представителя заявителя
    private SimpleStringProperty applicantFIO; // ФИО заявителя (представителя)
    private SimpleStringProperty applicantSubjectType; // Тип субъекта (представителя)
    private SimpleStringProperty applicantDocumentNumberSeries; // Документ заявителя: номер и серия (представителя)
    private SimpleStringProperty applicantDocumentWhenIssuedAndWhoIssued; // Документ заявителя: когда и кем выдан (представителя)
    private SimpleStringProperty applicantSnils; // Снилс заявителя (представителя)
    private SimpleStringProperty applicantSubType; // Гражданство заявителя (представителя)
    private SimpleStringProperty applicantAddress; // Адрес заявителя (представителя)
    private SimpleStringProperty applicantPhoneNumber; // Номер телефона заявителя (представителя)
    private SimpleStringProperty applicantCategory; // Категория заявителя (представителя)
    private SimpleStringProperty representiveType; // Тип представителя
    private SimpleStringProperty confirmAuthorRepres; // Подтверждение полномочий представителя
    // Переменные для организации
    private SimpleStringProperty applicantOrg; // Заявитель (Организация)
    private SimpleStringProperty nameOrg; // Название организации
    private SimpleStringProperty ogrnOrg; // ОГРН организации
    private SimpleStringProperty innOrg; // ИНН организации
    private SimpleStringProperty kppOrg; // КПП организации
    private SimpleStringProperty addressOrg; // Адрес организации
    private SimpleStringProperty classtypeOrg; // Категория заявителя (организация)


    public ApplicantInfoModel(String typeOfApplicant, String applicantFIO, String applicantSubjectType, String applicantDocumentNumberSeries, String applicantDocumentWhenIssuedAndWhoIssued,
                              String applicantSnils, String applicantSubType, String applicantAddress, String applicantPhoneNumber, String applicantCategory,
                              String representiveType, String confirmAuthorRepres,
                              String applicantOrg, String nameOrg, String ogrnOrg, String innOrg, String kppOrg, String addressOrg, String classtypeOrg) {

        this.typeOfApplicant = new SimpleStringProperty(typeOfApplicant);

        this.applicantFIO = new SimpleStringProperty(applicantFIO);
        this.applicantSubjectType = new SimpleStringProperty(applicantSubjectType);
        this.applicantDocumentNumberSeries = new SimpleStringProperty(applicantDocumentNumberSeries);
        this.applicantDocumentWhenIssuedAndWhoIssued =new SimpleStringProperty(applicantDocumentWhenIssuedAndWhoIssued);
        this.applicantSnils = new SimpleStringProperty(applicantSnils);
        this.applicantSubType = new SimpleStringProperty(applicantSubType);
        this.applicantAddress = new SimpleStringProperty(applicantAddress);
        this.applicantPhoneNumber = new SimpleStringProperty(applicantPhoneNumber);
        this.applicantCategory = new SimpleStringProperty(applicantCategory);
        this.representiveType = new SimpleStringProperty(representiveType);
        this.confirmAuthorRepres = new SimpleStringProperty(confirmAuthorRepres);

        this.applicantOrg =new SimpleStringProperty(applicantOrg);
        this.nameOrg =new SimpleStringProperty(nameOrg);
        this.ogrnOrg =new SimpleStringProperty(ogrnOrg);
        this.innOrg =new SimpleStringProperty(innOrg);
        this.kppOrg =new SimpleStringProperty(kppOrg);
        this.addressOrg =new SimpleStringProperty(addressOrg);
        this.classtypeOrg =new SimpleStringProperty(classtypeOrg);
    }

    public String getTypeOfApplicant() {
        return typeOfApplicant.get();
    }

    public void setTypeOfApplicant(String typeOfApplicant) {
        this.typeOfApplicant = new SimpleStringProperty(typeOfApplicant);
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

    public String getRepresentiveType() {
        return representiveType.get();
    }

    public void setRepresentiveType(String representiveType) {
        this.representiveType = new SimpleStringProperty(representiveType);
    }

    public String getConfirmAuthorRepres() {
        return confirmAuthorRepres.get();
    }

    public void setConfirmAuthorRepres(String confirmAuthorRepres) {
        this.confirmAuthorRepres = new SimpleStringProperty(confirmAuthorRepres);
    }

    public String getApplicantOrg() {
         return applicantOrg.get();
     }

    public void setApplicantOrg(String applicantOrg) {
        this.applicantOrg = new SimpleStringProperty(applicantOrg);
    }
    public String getNameOrg() {
        return nameOrg.get();
    }

    public void setNameOrg(String nameOrg) {
        this.nameOrg = new SimpleStringProperty(nameOrg);
    }

    public String getOgrnOrg() {
        return ogrnOrg.get();
    }

    public void setOgrnOrg(String ogrnOrg) {
        this.ogrnOrg = new SimpleStringProperty(ogrnOrg);
    }

    public String getInnOrg() {
        return innOrg.get();
    }

    public void setInnOrg(String innOrg) {
        this.innOrg = new SimpleStringProperty(innOrg);
    }

    public String getKppOrg() {
        return kppOrg.get();
    }

    public void setKppOrg(String kppOrg) {
        this.kppOrg = new SimpleStringProperty(kppOrg);
    }

    public String getAddressOrg() {
        return addressOrg.get();
    }

    public void setAddressOrg(String addressOrg) {
        this.addressOrg = new SimpleStringProperty(addressOrg);
    }

    public String getClasstypeOrg() {
        return classtypeOrg.get();
    }

    public void setClasstypeOrg(String classtypeOrg) {
        this.classtypeOrg = new SimpleStringProperty(classtypeOrg);
    }

}
