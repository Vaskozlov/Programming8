package server

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import lib.IOHelper
import org.apache.logging.log4j.kotlin.Logging
import java.nio.file.Path
import kotlin.io.path.deleteIfExists
import kotlin.io.path.writeText

class AuthorizationManager(private val usersInfoDirectory: Path) : Logging {
    private val authorizedUsers: HashSet<AuthorizationInfo> = HashSet()

    init {
        val userAuthorizationFile = usersInfoDirectory.toFile()

        if (!userAuthorizationFile.exists()) {
            userAuthorizationFile.mkdirs()
        }

        require(userAuthorizationFile.isDirectory)

        userAuthorizationFile
            .walk()
            .filter { it.isFile } // shall I just write filter(File::isFile) instead?
            .map { Json.decodeFromString<AuthorizationInfo>(IOHelper.readFile(it.absolutePath)!!) }
            .forEach(authorizedUsers::add)

        logger.info("Users info loaded")
    }

    fun isAuthorized(authorizationInfo: AuthorizationInfo): Boolean {
        return authorizedUsers.contains(authorizationInfo)
    }

    fun addUser(authorizationInfo: AuthorizationInfo) {
        authorizedUsers.add(authorizationInfo)
        getAuthorizationFilePath(authorizationInfo).writeText(
            Json.encodeToString(authorizationInfo)
        )
    }

    fun removeUser(authorizationInfo: AuthorizationInfo) {
        getAuthorizationFilePath(authorizationInfo).deleteIfExists()
    }

    private fun getAuthorizationFilePath(authorizationInfo: AuthorizationInfo): Path {
        return usersInfoDirectory.resolve("${authorizationInfo.login}.json")
    }
}