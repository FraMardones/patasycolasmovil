package com.example.patas_y_colas.data.network

import android.content.Context
import okhttp3.* // Importamos todo OkHttp
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// --- TokenManager MODIFICADO ---
object TokenManager {
    private const val PREFS_NAME = "auth_prefs"
    // Nuevas claves para ambos tokens
    private const val KEY_ACCESS_TOKEN = "jwt_access_token"
    private const val KEY_REFRESH_TOKEN = "jwt_refresh_token"
    private const val KEY_USER_NAME = "user_name" // <-- AÑADIDO

    // Guardamos ambos tokens y el nombre (firstname es nullable)
    fun saveTokens(context: Context, accessToken: String, refreshToken: String, firstname: String?) { // <-- MODIFICADO
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .putString(KEY_USER_NAME, firstname) // <-- AÑADIDO (putString acepta null)
            .apply()
    }

    // Obtenemos el Access Token (el de corta duración)
    fun getAccessToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    // Obtenemos el Refresh Token (el de larga duración)
    fun getRefreshToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    // --- ¡NUEVA FUNCIÓN! ---
    // Obtenemos el nombre del usuario
    fun getUserName(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USER_NAME, null)
    }

    // Borramos todo (para el logout)
    fun clearTokens(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}

// --- RetrofitClient MODIFICADO ---
object RetrofitClient {
    // Asegúrate que esta sea tu URL y que TERMINE CON BARRA /
    const val BASE_URL = "https://backend-movil-1hs0.onrender.com/"

    fun getClient(context: Context): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 1. Creamos el Autenticador (el "recepcionista")
        val tokenAuthenticator = TokenAuthenticator(context)

        // 2. Creamos el Interceptor (el que añade el token a CADA llamada)
        val authInterceptor = Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            // Usamos el Access Token
            val token = TokenManager.getAccessToken(context)
            if (token != null) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }

        // 3. Construimos el cliente OkHttp
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authInterceptor)      // 1ro: Añade el token
            .authenticator(tokenAuthenticator)    // 2do: Si falla (401), activa el recepcionista
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        // 4. Construimos Retrofit
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}


// --- ¡CLASE COMPLETAMENTE NUEVA! ---
// Esta es la lógica del "recepcionista" que se activa automáticamente
// cuando el servidor devuelve un error 401 (Token Expirado).
class TokenAuthenticator(private val context: Context) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // 1. Obtenemos el Refresh Token que teníamos guardado
        val refreshToken = TokenManager.getRefreshToken(context) ?: return null // Si no hay, no podemos hacer nada

        // 2. Hacemos una llamada SÍNCRONA (bloqueante) para refrescar el token
        // NOTA: Usamos un cliente de Retrofit simple para esta llamada
        // para evitar un bucle infinito de intercepción.
        val simpleRetrofit = Retrofit.Builder()
            .baseUrl(RetrofitClient.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        try {
            val refreshResponse = simpleRetrofit.refreshToken(
                mapOf("refreshToken" to refreshToken)
            ).execute() // .execute() la hace síncrona

            if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                val newTokens = refreshResponse.body()!!

                // 3. Guardamos los nuevos tokens (y el firstname, si vino)
                TokenManager.saveTokens(
                    context,
                    newTokens.token,
                    newTokens.refreshToken,
                    newTokens.firstname // <-- Pasa el firstname (que puede ser null)
                )

                // 4. Volvemos a crear la petición original, pero con el NUEVO token
                return response.request.newBuilder()
                    .header("Authorization", "Bearer ${newTokens.token}")
                    .build()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 5. Si el Refresh Token también falló (expiró o fue inválido), cerramos sesión.
        TokenManager.clearTokens(context)
        // (Aquí deberías navegar al Login, pero desde aquí es complejo,
        // la app lo detectará en la siguiente pantalla)
        return null // No se pudo autenticar
    }
}