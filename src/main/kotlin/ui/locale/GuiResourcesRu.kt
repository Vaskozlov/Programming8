package ui.locale

class GuiResourcesRu : IGuiResources {
    override fun uiWelcome(): String {
        return "Добро пожаловать в коллекцию организацией"
    }

    override fun uiAddress(): String {
        return "Адрес"
    }

    override fun uiPort(): String {
        return "Порт"
    }

    override fun uiLogin(): String {
        return "Логин"
    }

    override fun uiPassword(): String {
        return "Пароль"
    }

    override fun uiLoginButton(): String {
        return "Войти"
    }

    override fun uiUnableToLogin(): String {
        return "Не удалось войти"
    }

    override fun uiUnableToConnect(): String {
        return "Не удалось подключиться к серверу"
    }

    override fun uiFilter(): String {
        return "Фильтр"
    }

    override fun uiFilterColumn(): String {
        return "Столбец"
    }

    override fun uiID(): String {
        return "Ключ"
    }

    override fun uiName(): String {
        return "Название"
    }

    override fun uiCoordinateX(): String {
        return "Координата x"
    }

    override fun uiCoordinateY(): String {
        return "Координата y"
    }

    override fun uiCreationDate(): String {
        return "Дата создания"
    }

    override fun uiFullName(): String {
        return "Полное название"
    }

    override fun uiAnnualTurnover(): String {
        return "Годовой оборот"
    }

    override fun uiEmployeesCount(): String {
        return "Количество сотрудников"
    }

    override fun uiType(): String {
        return "Тип"
    }

    override fun uiZipCode(): String {
        return "Почтовый индекс"
    }

    override fun uiLocationX(): String {
        return "Позиция x"
    }

    override fun uiLocationY(): String {
        return "Позиция y"
    }

    override fun uiLocationZ(): String {
        return "Позиция z"
    }

    override fun uiLocationName(): String {
        return "Город"
    }

    override fun uiCurrentUser(): String {
        return "Вы вошли под пользователем"
    }

    override fun uiYourIdIs(): String {
        return "ваш ID"
    }

    override fun uiUnselectOrganization(): String {
        return "Отменить выбор организации"
    }

    override fun uiAddOrganization(): String {
        return "Добавить организацию"
    }

    override fun uiClearOrganizations(): String {
        return "Отчистить"
    }

    override fun uiValidateInvalidName(): String {
        return "Имя не может быть пустым"
    }

    override fun uiValidateInvalidCoordinateX(): String {
        return "Координата x должна быть числом"
    }

    override fun uiValidateInvalidCoordinateY(): String {
        return "Координата y должна быть числом"
    }

    override fun uiValidateInvalidAnnualTurnover(): String {
        return "Годовой оборот должен быть неотрицательным числом"
    }

    override fun uiValidateInvalidFullName(): String {
        return "Полное название не может быть пустым"
    }

    override fun uiValidateInvalidEmployeesCount(): String {
        return "Количество сотрудников должно быть положительным числом"
    }

    override fun uiValidateInvalidZipCode(): String {
        return "Почтовый индекс должен состоять из 3 и более цифр"
    }

    override fun uiValidateInvalidLocationX(): String {
        return "Позиция x должна быть числом"
    }

    override fun uiValidateInvalidLocationY(): String {
        return "Позиция y должна быть числом"
    }

    override fun uiValidateInvalidLocationZ(): String {
        return "Позиция z должна быть числом"
    }

    override fun uiValidateNullLocationX(): String {
        return "Позиция x может быть неопределенной, при выставленном адресе"
    }

    override fun uiValidateNullLocationY(): String {
        return "Позиция y может быть неопределенной, при выставленном адресе"
    }

    override fun uiValidateNullLocationZ(): String {
        return "Позиция z может быть неопределенной, при выставленном адресе"
    }

    override fun uiClearAddress(): String {
        return "Отчистить адрес"
    }

    override fun uiAddOrganizationIfMax(): String {
        return "Добавить если максимально"
    }

    override fun uiRemoveAllByAddress(): String {
        return "Удалить все по адресу"
    }

    override fun uiTableHeaderId(): String {
        return "Ключ"
    }

    override fun uiTableHeaderName(): String {
        return "Название"
    }

    override fun uiTableHeaderCoordinateX(): String {
        return "Координата x"
    }

    override fun uiTableHeaderCoordinateY(): String {
        return "Координата y"
    }

    override fun uiTableHeaderCreationDate(): String {
        return "Дата создания"
    }

    override fun uiTableHeaderAnnualTurnover(): String {
        return "Годовой оборот"
    }

    override fun uiTableHeaderFullName(): String {
        return "Полное название"
    }

    override fun uiTableHeaderEmployeesCount(): String {
        return "Количество сотрудников"
    }

    override fun uiTableHeaderType(): String {
        return "Тип"
    }

    override fun uiTableHeaderZipCode(): String {
        return "Почтовый индекс"
    }

    override fun uiTableHeaderLocationX(): String {
        return "Позиция x"
    }

    override fun uiTableHeaderLocationY(): String {
        return "Позиция y"
    }

    override fun uiTableHeaderLocationZ(): String {
        return "Позиция z"
    }

    override fun uiTableHeaderLocationName(): String {
        return "Город"
    }

    override fun uiTableHeaderUser(): String {
        return "Пользователь"
    }

    override fun uiTableHeaderCreatorId(): String {
        return "ID создателя"
    }

    override fun uiChooseLanguage(): String {
        return "Выберите язык"
    }

    override fun uiTypeCommercial(): String {
        return "Коммерческая"
    }

    override fun uiTypePublic(): String {
        return "Публичная"
    }
    
    override fun uiCreatorLogin(): String {
        return "Логин создателя"
    }

    override fun uiTypeOpenJointStockCompany(): String {
        return "Открытое акционерное общество"
    }

    override fun uiTypePrivateLimitedCompany(): String {
        return "Закрытое акционерное общество"
    }

    override fun uiTypeNull(): String {
        return "Неопределенный"
    }
    
    override fun uiExecuteScript(): String{
        return "Выполнить скрипт"
    }
}