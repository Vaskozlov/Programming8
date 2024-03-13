package lib.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

inline fun <reified T> ObjectMapperWithModules.read(json: String): T = objectMapper.readValue(json)
inline fun <reified T> ObjectMapperWithModules.read(json: JsonNode): T = read(json.toString())
inline fun <reified T> ObjectMapperWithModules.read(file: File): T = objectMapper.readValue(file)
inline fun <reified T> ObjectMapperWithModules.write(obj: T): String = objectMapper.writeValueAsString(obj)
inline fun <reified T> ObjectMapperWithModules.prettyWrite(obj: T): String =
    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj)