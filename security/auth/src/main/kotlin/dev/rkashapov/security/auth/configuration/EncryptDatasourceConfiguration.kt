package dev.rkashapov.security.auth.configuration

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class EncryptDatasourceConfiguration(
    private val encryptProperties: EncryptProperties
) {

    @Bean
    fun dataSourceProperties(): DataSourceProperties = DataSourceProperties()

    @Bean
    fun getDataSource(): DataSource =
        dataSourceProperties()
            .initializeDataSourceBuilder()
            .type(HikariDataSource::class.java)
            .build().apply {
                connectionInitSql = encryptProperties.getSql()
            }


}
