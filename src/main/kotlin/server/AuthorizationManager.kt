package server

import lib.json.ObjectMapperWithModules
import lib.json.read
import lib.json.write
import org.apache.logging.log4j.kotlin.Logging
import java.nio.file.Path
import kotlin.io.path.deleteIfExists
import kotlin.io.path.writeText

class AuthorizationManager(
    private val usersInfoDirectory: Path,
    private val objectMapperWithModules: ObjectMapperWithModules = ObjectMapperWithModules()
) : Logging {
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
            .map { objectMapperWithModules.read<AuthorizationInfo>(it) }
            .forEach(authorizedUsers::add)

        logger.info("Users info loaded")
    }

    fun isAuthorized(authorizationInfo: AuthorizationInfo): Boolean {
        return authorizedUsers.contains(authorizationInfo)
    }

    fun addUser(authorizationInfo: AuthorizationInfo) {
        authorizedUsers.add(authorizationInfo)
        getAuthorizationFilePath(authorizationInfo).writeText(
            objectMapperWithModules.write(authorizationInfo)
        )
    }

    fun removeUser(authorizationInfo: AuthorizationInfo) {
        getAuthorizationFilePath(authorizationInfo).deleteIfExists()
    }

    private fun getAuthorizationFilePath(authorizationInfo: AuthorizationInfo): Path {
        return usersInfoDirectory.resolve("${authorizationInfo.login}.json")
    }
}