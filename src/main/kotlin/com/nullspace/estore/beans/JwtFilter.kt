package com.nullspace.estore.beans

import com.google.firebase.auth.FirebaseAuth
import com.nullspace.estore.services.FirebaseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@DependsOn("FirebaseAppConfig")
class JwtFilter @Autowired
constructor(private val firebaseService: FirebaseService) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val firebaseLatch = CountDownLatch(1)
        if (!request.servletPath.contains("admin")) {
            filterChain.doFilter(request, response)
            return
        }

        if (!checkAuth) {
            request.setAttribute("uid", "testUID")
            request.setAttribute("role", "admin")
            filterChain.doFilter(request, response)
            return
        }

        val authHeader = request.getHeader("authorization")

        if ("OPTIONS" == request.method) {
            response.status = HttpServletResponse.SC_OK

            filterChain.doFilter(request, response)
        } else {

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw ServletException("Missing or invalid Authorization header")
            }

            val idToken = authHeader.substring(7)

            try {
                val decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken)
                val uid = decodedToken.uid
                request.setAttribute("uid", uid)

                firebaseService.addListenerForUserRole(uid) { value: String ->
                    try {
                        request.setAttribute("role", value)
                        filterChain.doFilter(request, response)
                        firebaseLatch.countDown()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                // Latch will wait for one second or until countdown amount is satisfied
                firebaseLatch.await(1L, TimeUnit.SECONDS)
                return

            } catch (e: com.google.firebase.auth.FirebaseAuthException) {
                throw ServletException("Invalid token")
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }

    companion object {

        private val checkAuth = false
    }
}