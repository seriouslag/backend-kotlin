package com.nullspace.estore.beans

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.web.filter.AbstractRequestLoggingFilter
import javax.servlet.Filter
import javax.servlet.http.HttpServletRequest

@Configuration
class LoggingBean @Autowired
constructor(environment: Environment) {
    private var isProd: Boolean = environment.activeProfiles.any { profile -> profile.contains("prod") }

    @Bean
    fun loggingFilter(): Filter {

        return object : AbstractRequestLoggingFilter() {
            private val log = LoggerFactory
                    .getLogger(LoggingBean::class.java)

            init {
                isIncludeClientInfo = true
                isIncludeQueryString = true
                isIncludePayload = true
            }

            override fun beforeRequest(request: HttpServletRequest, message: String) {
                // not needed
            }

            override fun afterRequest(request: HttpServletRequest, message: String) {
                if (isProd) {
                    log.info(message)
                }
            }
        }
    }

}