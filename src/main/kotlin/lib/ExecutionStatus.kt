package lib

enum class ExecutionStatus(val value: Boolean) {
    SUCCESS(true),
    FAILURE(false);

    companion object {
        fun getByValue(value: Boolean) = entries.first { it.value == value }
    }
}
