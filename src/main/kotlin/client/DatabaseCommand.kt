package network.client

enum class DatabaseCommand {
    INFO,
    SHOW,
    ADD,
    ADD_IF_MAX,
    UPDATE,
    REMOVE_HEAD,
    REMOVE_BY_ID,
    REMOVE_ALL_BY_POSTAL_ADDRESS,
    CLEAR,
    SAVE,
    READ,
    EXIT,
    HISTORY,
    SUM_OF_ANNUAL_TURNOVER,
    MAX_BY_FULL_NAME,
    EXECUTE_SCRIPT,
    HELP;
}
