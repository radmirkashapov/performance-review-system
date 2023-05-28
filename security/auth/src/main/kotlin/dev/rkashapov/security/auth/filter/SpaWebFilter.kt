package dev.rkashapov.security.auth.filter

import dev.rkashapov.security.auth.configuration.WebSecurityConfiguration
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException


@Component
class SpaWebFilter : OncePerRequestFilter() {

    private val regexp = "/(.*)".toRegex()

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI
        if (!path.startsWith("/api") &&
            !path.startsWith(WebSecurityConfiguration.DEFAULT_LOGIN_URL) &&
            !path.contains(".") &&
            path.matches(regexp)
        ) {
            request.getRequestDispatcher("/index.html").forward(request, response)
            return
        }
        filterChain.doFilter(request, response)
    }

}
