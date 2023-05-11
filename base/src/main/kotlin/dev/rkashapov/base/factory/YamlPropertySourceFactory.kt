package dev.rkashapov.base.factory

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.env.PropertySource
import org.springframework.core.io.support.EncodedResource
import org.springframework.core.io.support.PropertySourceFactory
import java.io.IOException
import java.util.*

class YamlPropertySourceFactory : PropertySourceFactory {

    @Throws(IOException::class)
    override fun createPropertySource(name: String?, encodedResource: EncodedResource): PropertySource<*> {
        val factory = YamlPropertiesFactoryBean()
        factory.setResources(encodedResource.resource)
        val properties: Properties = requireNotNull(factory.getObject()) { "Null received" }

        val resourceFilename = checkNotNull(encodedResource.resource.filename) { "Filename required" }

        return PropertiesPropertySource(resourceFilename, properties)
    }


}
