package pkpvdplus.model;

public class ApplicantInfoModel {
    private String typeOfApplicant; // Тип заявителя (Физ. лицо или организация)

    // Переменные для физ. лиц и представителя заявителя
    private String applicantFIO; // ФИО заявителя (представителя)
    private String applicantSubjectType; // Тип субъекта (представителя)
    private String applicantDocumentNumberSeries; // Документ заявителя: номер и серия (представителя)
    private String applicantDocumentWhenIssuedAndWhoIssued; // Документ заявителя: когда и кем выдан (представителя)
    private String applicantSnils; // Снилс заявителя (представителя)
    private String applicantSubType; // Гражданство заявителя (представителя)
    private String applicantAddress; // Адрес заявителя (представителя)
    private String applicantPhoneNumber; // Номер телефона заявителя (представителя)
    private String applicantCategory; // Категория заявителя (представителя)
    private String representiveType; // Тип представителя
    private String confirmAuthorRepres; // Подтверждение полномочий представителя
    // Переменные для организации
    private String applicantOrg; // Заявитель (Организация)
    private String nameOrg; // Название организации
    private String ogrnOrg; // ОГРН организации
    private String innOrg; // ИНН организации
    private String kppOrg; // КПП организации
    private String addressOrg; // Адрес организации
    private String classtypeOrg; // Категория заявителя (организация)

    public ApplicantInfoModel(String typeOfApplicant, String applicantFIO, String applicantSubjectType, String applicantDocumentNumberSeries,
                              String applicantDocumentWhenIssuedAndWhoIssued, String applicantSnils, String applicantSubType, String applicantAddress,
                              String applicantPhoneNumber, String applicantCategory, String representiveType, String confirmAuthorRepres, String applicantOrg,
                              String nameOrg, String ogrnOrg, String innOrg, String kppOrg, String addressOrg, String classtypeOrg) {
        this.typeOfApplicant = typeOfApplicant;
        this.applicantFIO = applicantFIO;
        this.applicantSubjectType = applicantSubjectType;
        this.applicantDocumentNumberSeries = applicantDocumentNumberSeries;
        this.applicantDocumentWhenIssuedAndWhoIssued = applicantDocumentWhenIssuedAndWhoIssued;
        this.applicantSnils = applicantSnils;
        this.applicantSubType = applicantSubType;
        this.applicantAddress = applicantAddress;
        this.applicantPhoneNumber = applicantPhoneNumber;
        this.applicantCategory = applicantCategory;
        this.representiveType = representiveType;
        this.confirmAuthorRepres = confirmAuthorRepres;
        this.applicantOrg = applicantOrg;
        this.nameOrg = nameOrg;
        this.ogrnOrg = ogrnOrg;
        this.innOrg = innOrg;
        this.kppOrg = kppOrg;
        this.addressOrg = addressOrg;
        this.classtypeOrg = classtypeOrg;
    }

    public String getTypeOfApplicant() {
        return typeOfApplicant;
    }

    public void setTypeOfApplicant(String typeOfApplicant) {
        this.typeOfApplicant = typeOfApplicant;
    }

    public String getApplicantFIO() {
        return applicantFIO;
    }

    public void setApplicantFIO(String applicantFIO) {
        this.applicantFIO = applicantFIO;
    }

    public String getApplicantSubjectType() {
        return applicantSubjectType;
    }

    public void setApplicantSubjectType(String applicantSubjectType) {
        this.applicantSubjectType = applicantSubjectType;
    }

    public String getApplicantDocumentNumberSeries() {
        return applicantDocumentNumberSeries;
    }

    public void setApplicantDocumentNumberSeries(String applicantDocumentNumberSeries) {
        this.applicantDocumentNumberSeries = applicantDocumentNumberSeries;
    }

    public String getApplicantDocumentWhenIssuedAndWhoIssued() {
        return applicantDocumentWhenIssuedAndWhoIssued;
    }

    public void setApplicantDocumentWhenIssuedAndWhoIssued(String applicantDocumentWhenIssuedAndWhoIssued) {
        this.applicantDocumentWhenIssuedAndWhoIssued = applicantDocumentWhenIssuedAndWhoIssued;
    }

    public String getApplicantSnils() {
        return applicantSnils;
    }

    public void setApplicantSnils(String applicantSnils) {
        this.applicantSnils = applicantSnils;
    }

    public String getApplicantSubType() {
        return applicantSubType;
    }

    public void setApplicantSubType(String applicantSubType) {
        this.applicantSubType = applicantSubType;
    }

    public String getApplicantAddress() {
        return applicantAddress;
    }

    public void setApplicantAddress(String applicantAddress) {
        this.applicantAddress = applicantAddress;
    }

    public String getApplicantPhoneNumber() {
        return applicantPhoneNumber;
    }

    public void setApplicantPhoneNumber(String applicantPhoneNumber) {
        this.applicantPhoneNumber = applicantPhoneNumber;
    }

    public String getApplicantCategory() {
        return applicantCategory;
    }

    public void setApplicantCategory(String applicantCategory) {
        this.applicantCategory = applicantCategory;
    }

    public String getRepresentiveType() {
        return representiveType;
    }

    public void setRepresentiveType(String representiveType) {
        this.representiveType = representiveType;
    }

    public String getConfirmAuthorRepres() {
        return confirmAuthorRepres;
    }

    public void setConfirmAuthorRepres(String confirmAuthorRepres) {
        this.confirmAuthorRepres = confirmAuthorRepres;
    }

    public String getApplicantOrg() {
        return applicantOrg;
    }

    public void setApplicantOrg(String applicantOrg) {
        this.applicantOrg = applicantOrg;
    }

    public String getNameOrg() {
        return nameOrg;
    }

    public void setNameOrg(String nameOrg) {
        this.nameOrg = nameOrg;
    }

    public String getOgrnOrg() {
        return ogrnOrg;
    }

    public void setOgrnOrg(String ogrnOrg) {
        this.ogrnOrg = ogrnOrg;
    }

    public String getInnOrg() {
        return innOrg;
    }

    public void setInnOrg(String innOrg) {
        this.innOrg = innOrg;
    }

    public String getKppOrg() {
        return kppOrg;
    }

    public void setKppOrg(String kppOrg) {
        this.kppOrg = kppOrg;
    }

    public String getAddressOrg() {
        return addressOrg;
    }

    public void setAddressOrg(String addressOrg) {
        this.addressOrg = addressOrg;
    }

    public String getClasstypeOrg() {
        return classtypeOrg;
    }

    public void setClasstypeOrg(String classtypeOrg) {
        this.classtypeOrg = classtypeOrg;
    }
}
