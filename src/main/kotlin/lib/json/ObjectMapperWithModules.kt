package lib.json

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class ObjectMapperWithModules(factory: JsonFactory? = null) {
    var objectMapper: ObjectMapper = ObjectMapper(factory)

    init {
        objectMapper.findAndRegisterModules()
        objectMapper.registerKotlinModule()
    }
}
