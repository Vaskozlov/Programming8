package commands.client.core

abstract class Command {
    suspend fun execute(argument: Any? = null): Result<Any?> {
        return try {
            executeImplementation(argument)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    protected abstract suspend fun executeImplementation(argument: Any?): Result<Any?>
}
