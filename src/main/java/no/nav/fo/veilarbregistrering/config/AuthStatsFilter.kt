package no.nav.fo.veilarbregistrering.config

import no.nav.common.auth.Constants
import no.nav.fo.veilarbregistrering.log.loggerFor
import org.slf4j.MDC
import org.springframework.http.HttpHeaders
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class AuthStatsFilter : Filter {

    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, chain: FilterChain) {
        val request: HttpServletRequest = servletRequest as HttpServletRequest
        val consumerId = getConsumerId(request)

        val cookieNames = request.cookies?.map { it.name } ?: emptyList()
        val sts = request.getHeader(HttpHeaders.AUTHORIZATION)?.startsWith("Bearer") ?: false
        val type = when {
            Constants.AZURE_AD_B2C_ID_TOKEN_COOKIE_NAME in cookieNames -> "AADB2C"
            Constants.AZURE_AD_ID_TOKEN_COOKIE_NAME in cookieNames -> "AAD"
            Constants.OPEN_AM_ID_TOKEN_COOKIE_NAME in cookieNames -> "OPENAM"
            sts -> "STS"
            else -> null
        }

        try {
            type?.let {
                MDC.put(TOKEN_TYPE, type)
                log.info("Authentication with: [$it] request path: [${request.servletPath}] consumer: [$consumerId]")
            }
            chain.doFilter(servletRequest, servletResponse)
        } finally {
            MDC.remove(TOKEN_TYPE)
        }
    }

    companion object {
        private const val TOKEN_TYPE = "tokenType"
        private val log = loggerFor<AuthStatsFilter>()

        private fun getConsumerId(request: HttpServletRequest) = request.getHeader("Nav-Consumer-Id")
    }
}