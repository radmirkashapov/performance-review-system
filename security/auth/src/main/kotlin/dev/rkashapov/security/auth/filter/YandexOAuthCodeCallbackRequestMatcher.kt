package dev.rkashapov.security.auth.filter

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpMethod
import org.springframework.security.web.util.matcher.RequestMatcher


class YandexOAuthCodeCallbackRequestMatcher(
    val requestUri: String,
    val httpMethod: HttpMethod
) : RequestMatcher {
    override fun matches(request: HttpServletRequest): Boolean {
        val uri = request.requestURI
        return uri.contentEquals(requestUri, ignoreCase = true)
                && request.method.contentEquals(httpMethod.name(), ignoreCase = true)
    }
}
