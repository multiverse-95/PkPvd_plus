package pkpvdplus.model;

public class AppealGeneralInfoModel {
    // Переменные для главной информации об обращении
    private String statementType; // Наименование
    private String internalNum; // внутренний номер
    private String createEventDateWhen; // дата создания
    private String createEventPerformer; // кем обработан: фио
    private String packageNum; // Номер пакета
    private String numPPOZ; // Регистрационный номер ППОЗ
    private String createPPOZDate; // когда создан в ППОЗ
    private String statusNotePPOZ; // статус
    private String statusPPOZ; // обновление
    private String statusPPOZDate; // дата обновления
    private String routineExecutionDays; // Регламентный срок
    private String processingEndDate; // Окончание обработки

    // Переменные для доп. информации об обращении (принадлежит обращению)
    private String nameAdvanced; // Наименование обращения
    private String internalNumAdvanced;// Внутренний номер
    private String createEventDateWhenAdvanced; // дата создания
    private String createEventPerformerAdvanced; // кем создан:фио
    private String currentStepAdvanced; // Шаг
    private String moveStepEventDateWhenAdvanced; // Переход на шаг: дата
    private String moveStepPerformerAdvanced; // кто перешёл на шаг: фио
    private String executeEventAdvanced; // Начало выполнения шага
    private String operationCommentAdvanced; // Комментарий к текущей операции

    public AppealGeneralInfoModel(String statementType, String internalNum, String createEventDateWhen, String createEventPerformer, String packageNum, String numPPOZ,
                                  String createPPOZDate, String statusNotePPOZ, String statusPPOZ, String statusPPOZDate, String routineExecutionDays, String processingEndDate,
                                  String nameAdvanced, String internalNumAdvanced, String createEventDateWhenAdvanced, String createEventPerformerAdvanced, String currentStepAdvanced,
                                  String moveStepEventDateWhenAdvanced, String moveStepPerformerAdvanced, String executeEventAdvanced, String operationCommentAdvanced) {
        this.statementType = statementType;
        this.internalNum = internalNum;
        this.createEventDateWhen = createEventDateWhen;
        this.createEventPerformer = createEventPerformer;
        this.packageNum = packageNum;
        this.numPPOZ = numPPOZ;
        this.createPPOZDate = createPPOZDate;
        this.statusNotePPOZ = statusNotePPOZ;
        this.statusPPOZ = statusPPOZ;
        this.statusPPOZDate = statusPPOZDate;
        this.routineExecutionDays = routineExecutionDays;
        this.processingEndDate = processingEndDate;
        this.nameAdvanced = nameAdvanced;
        this.internalNumAdvanced = internalNumAdvanced;
        this.createEventDateWhenAdvanced = createEventDateWhenAdvanced;
        this.createEventPerformerAdvanced = createEventPerformerAdvanced;
        this.currentStepAdvanced = currentStepAdvanced;
        this.moveStepEventDateWhenAdvanced = moveStepEventDateWhenAdvanced;
        this.moveStepPerformerAdvanced = moveStepPerformerAdvanced;
        this.executeEventAdvanced = executeEventAdvanced;
        this.operationCommentAdvanced = operationCommentAdvanced;
    }

    public String getStatementType() {
        return statementType;
    }

    public void setStatementType(String statementType) {
        this.statementType = statementType;
    }

    public String getInternalNum() {
        return internalNum;
    }

    public void setInternalNum(String internalNum) {
        this.internalNum = internalNum;
    }

    public String getCreateEventDateWhen() {
        return createEventDateWhen;
    }

    public void setCreateEventDateWhen(String createEventDateWhen) {
        this.createEventDateWhen = createEventDateWhen;
    }

    public String getCreateEventPerformer() {
        return createEventPerformer;
    }

    public void setCreateEventPerformer(String createEventPerformer) {
        this.createEventPerformer = createEventPerformer;
    }

    public String getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(String packageNum) {
        this.packageNum = packageNum;
    }

    public String getNumPPOZ() {
        return numPPOZ;
    }

    public void setNumPPOZ(String numPPOZ) {
        this.numPPOZ = numPPOZ;
    }

    public String getCreatePPOZDate() {
        return createPPOZDate;
    }

    public void setCreatePPOZDate(String createPPOZDate) {
        this.createPPOZDate = createPPOZDate;
    }

    public String getStatusNotePPOZ() {
        return statusNotePPOZ;
    }

    public void setStatusNotePPOZ(String statusNotePPOZ) {
        this.statusNotePPOZ = statusNotePPOZ;
    }

    public String getStatusPPOZ() {
        return statusPPOZ;
    }

    public void setStatusPPOZ(String statusPPOZ) {
        this.statusPPOZ = statusPPOZ;
    }

    public String getStatusPPOZDate() {
        return statusPPOZDate;
    }

    public void setStatusPPOZDate(String statusPPOZDate) {
        this.statusPPOZDate = statusPPOZDate;
    }

    public String getRoutineExecutionDays() {
        return routineExecutionDays;
    }

    public void setRoutineExecutionDays(String routineExecutionDays) {
        this.routineExecutionDays = routineExecutionDays;
    }

    public String getProcessingEndDate() {
        return processingEndDate;
    }

    public void setProcessingEndDate(String processingEndDate) {
        this.processingEndDate = processingEndDate;
    }

    public String getNameAdvanced() {
        return nameAdvanced;
    }

    public void setNameAdvanced(String nameAdvanced) {
        this.nameAdvanced = nameAdvanced;
    }

    public String getInternalNumAdvanced() {
        return internalNumAdvanced;
    }

    public void setInternalNumAdvanced(String internalNumAdvanced) {
        this.internalNumAdvanced = internalNumAdvanced;
    }

    public String getCreateEventDateWhenAdvanced() {
        return createEventDateWhenAdvanced;
    }

    public void setCreateEventDateWhenAdvanced(String createEventDateWhenAdvanced) {
        this.createEventDateWhenAdvanced = createEventDateWhenAdvanced;
    }

    public String getCreateEventPerformerAdvanced() {
        return createEventPerformerAdvanced;
    }

    public void setCreateEventPerformerAdvanced(String createEventPerformerAdvanced) {
        this.createEventPerformerAdvanced = createEventPerformerAdvanced;
    }

    public String getCurrentStepAdvanced() {
        return currentStepAdvanced;
    }

    public void setCurrentStepAdvanced(String currentStepAdvanced) {
        this.currentStepAdvanced = currentStepAdvanced;
    }

    public String getMoveStepEventDateWhenAdvanced() {
        return moveStepEventDateWhenAdvanced;
    }

    public void setMoveStepEventDateWhenAdvanced(String moveStepEventDateWhenAdvanced) {
        this.moveStepEventDateWhenAdvanced = moveStepEventDateWhenAdvanced;
    }

    public String getMoveStepPerformerAdvanced() {
        return moveStepPerformerAdvanced;
    }

    public void setMoveStepPerformerAdvanced(String moveStepPerformerAdvanced) {
        this.moveStepPerformerAdvanced = moveStepPerformerAdvanced;
    }

    public String getExecuteEventAdvanced() {
        return executeEventAdvanced;
    }

    public void setExecuteEventAdvanced(String executeEventAdvanced) {
        this.executeEventAdvanced = executeEventAdvanced;
    }

    public String getOperationCommentAdvanced() {
        return operationCommentAdvanced;
    }

    public void setOperationCommentAdvanced(String operationCommentAdvanced) {
        this.operationCommentAdvanced = operationCommentAdvanced;
    }
}