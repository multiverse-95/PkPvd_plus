package pkpvdplus.model;
// Модель информации об мфц
public class MFCsInfoModel {
    private String code; // Код мфц
    private String name; // Наименование мфц
    // Конструктор
    public MFCsInfoModel(String code, String name) {
        this.code = code;
        this.name = name;
    }
    // Геттеры и сеттеры
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
