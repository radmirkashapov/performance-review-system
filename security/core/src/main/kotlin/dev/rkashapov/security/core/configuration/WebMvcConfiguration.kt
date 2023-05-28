package dev.rkashapov.security.core.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.web.servlet.config.annotation.*
import org.springframework.web.servlet.resource.PathResourceResolver


@Configuration
@EnableWebMvc
class WebMvcConfiguration : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "OPTIONS", "PUT", "PATCH", "DELETE")
    }

    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON)
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry
            .addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
            .resourceChain(true)
            .addResolver(object : PathResourceResolver() {
                override fun getResource(resourcePath: String, location: Resource): Resource {
                    val requestedResource = location.createRelative(resourcePath)

                    return if (requestedResource.exists() && requestedResource.isReadable) {
                        requestedResource
                    } else {
                        ClassPathResource("/static/index.html")
                    }
                }
            })
    }

}

