package ui.locale

class GuiResourcesEn : IGuiResources {
    override fun uiWelcome(): String {
        return "Welcome to collection manager"
    }

    override fun uiAddress(): String {
        return "address"
    }

    override fun uiPort(): String {
        return "port"
    }

    override fun uiLogin(): String {
        return "login"
    }

    override fun uiPassword(): String {
        return "password"
    }

    override fun uiLoginButton(): String {
        return "login"
    }

    override fun uiUnableToLogin(): String {
        return "Unable to login"
    }

    override fun uiUnableToConnect(): String {
        return "Unable to connect to the server"
    }

    override fun uiFilter(): String {
        return "Filter"
    }

    override fun uiFilterColumn(): String {
        return "Column"
    }

    override fun uiID(): String {
        return "ID"
    }

    override fun uiName(): String {
        return "Name"
    }

    override fun uiCoordinateX(): String {
        return "Coordinate x"
    }

    override fun uiCoordinateY(): String {
        return "Coordinate y"
    }

    override fun uiCreationDate(): String {
        return "Creation date"
    }

    override fun uiFullName(): String {
        return "Full name"
    }

    override fun uiAnnualTurnover(): String {
        return "Annual turnover"
    }

    override fun uiEmployeesCount(): String {
        return "Employees count"
    }

    override fun uiType(): String {
        return "Type"
    }

    override fun uiZipCode(): String {
        return "Zip code"
    }

    override fun uiLocationX(): String {
        return "Location x"
    }

    override fun uiLocationY(): String {
        return "Location y"
    }

    override fun uiLocationZ(): String {
        return "Location z"
    }

    override fun uiLocationName(): String {
        return "Location name"
    }

    override fun uiCurrentUser(): String {
        return "You are logging as"
    }

    override fun uiYourIdIs(): String {
        return "your ID"
    }

    override fun uiUnselectOrganization(): String {
        return "Unselect organization"
    }

    override fun uiAddOrganization(): String {
        return "Add organization"
    }

    override fun uiClearOrganizations(): String {
        return "Clear"
    }

    override fun uiValidateInvalidName(): String {
        return "Name cannot be empty"
    }

    override fun uiValidateInvalidCoordinateX(): String {
        return "Coordinate x must be a number"
    }

    override fun uiValidateInvalidCoordinateY(): String {
        return "Coordinate y must be a number in range -464 < y < 464"
    }

    override fun uiValidateInvalidAnnualTurnover(): String {
        return "Annual turnover must be a non-negative number"
    }

    override fun uiValidateInvalidFullName(): String {
        return "Full name cannot be empty"
    }

    override fun uiValidateInvalidEmployeesCount(): String {
        return "Employee count must be a positive number"
    }

    override fun uiValidateInvalidZipCode(): String {
        return "Zip code must contain at least 3 digits"
    }

    override fun uiValidateInvalidLocationX(): String {
        return "Location x must be a number"
    }

    override fun uiValidateInvalidLocationY(): String {
        return "Location y must be a number"
    }

    override fun uiValidateInvalidLocationZ(): String {
        return "Location z must be a number"
    }

    override fun uiValidateNullLocationX(): String {
        return "Location x cannot be null if the address is set"
    }

    override fun uiValidateNullLocationY(): String {
        return "Location y cannot be null if the address is set"
    }

    override fun uiValidateNullLocationZ(): String {
        return "Location z cannot be null if the address is set"
    }

    override fun uiClearAddress(): String {
        return "Clear address"
    }

    override fun uiAddOrganizationIfMax(): String {
        return "Add if max"
    }

    override fun uiRemoveAllByAddress(): String {
        return "Remove all by address"
    }

    override fun uiTableHeaderId(): String {
        return "ID"
    }

    override fun uiTableHeaderName(): String {
        return "Name"
    }

    override fun uiTableHeaderCoordinateX(): String {
        return "Coordinate x"
    }

    override fun uiTableHeaderCoordinateY(): String {
        return "Coordinate y"
    }

    override fun uiTableHeaderCreationDate(): String {
        return "Creation date"
    }

    override fun uiTableHeaderAnnualTurnover(): String {
        return "Annual turnover"
    }

    override fun uiTableHeaderFullName(): String {
        return "Full name"
    }

    override fun uiTableHeaderEmployeesCount(): String {
        return "Employees count"
    }

    override fun uiTableHeaderType(): String {
        return "Type"
    }

    override fun uiTableHeaderZipCode(): String {
        return "Zip code"
    }

    override fun uiTableHeaderLocationX(): String {
        return "Location x"
    }

    override fun uiTableHeaderLocationY(): String {
        return "Location y"
    }

    override fun uiTableHeaderLocationZ(): String {
        return "Location z"
    }

    override fun uiTableHeaderLocationName(): String {
        return "Location name"
    }

    override fun uiTableHeaderUser(): String {
        return "User"
    }

    override fun uiTableHeaderCreatorId(): String {
        return "Creator ID"
    }

    override fun uiChooseLanguage(): String {
        return "Select language"
    }

    override fun uiTypeCommercial(): String {
        return "Commercial"
    }
    
    override fun uiCreatorLogin(): String {
        return "Creator login"
    }
    
    override fun uiTypePublic(): String {
        return "Public"
    }

    override fun uiTypeOpenJointStockCompany(): String {
        return "Open joint-stock company"
    }

    override fun uiTypePrivateLimitedCompany(): String {
        return "Private limited company"
    }

    override fun uiTypeNull(): String {
        return "Null"
    }
    
    override fun uiExecuteScript(): String{
        return "Execute script"
    }
}