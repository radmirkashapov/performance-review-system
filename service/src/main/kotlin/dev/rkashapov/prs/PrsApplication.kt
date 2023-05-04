package dev.rkashapov.prs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication(scanBasePackages = ["dev.rkashapov.*"])
@EnableScheduling
@EnableTransactionManagement
@EnableJpaRepositories
@ConfigurationPropertiesScan
class PrsApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<PrsApplication>(*args)
}

