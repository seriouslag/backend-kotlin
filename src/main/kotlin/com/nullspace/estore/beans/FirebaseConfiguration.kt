package com.nullspace.estore.beans

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.stripe.Stripe
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.io.IOException
import java.util.HashMap

@Configuration
class FirebaseConfiguration {
    @Bean
    fun FirebaseAppConfig(): FirebaseApp? {
        try {

            // Initialize the app with a custom auth variable, limiting the server's access
            val auth = HashMap<String, Any>()
            auth["uid"] = "backend-service-worker"

            val `is` = ClassPathResource("firebase.json").inputStream

            val options = FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(`is`))
                    .setDatabaseUrl("https://surruh-ed451.firebaseio.com")
                    .setDatabaseAuthVariableOverride(auth)
                    .build()

            FirebaseApp.initializeApp(options)
            println("Firebase default is init")

            Stripe.setConnectTimeout(30 * 1000)
            Stripe.setReadTimeout(80 * 1000)

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }
}