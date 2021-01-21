package io.github.ricardormdev.clockifyplugin.api.websocket.authentication

import com.auth0.jwt.JWT
import io.github.ricardormdev.clockifyplugin.settings.settingsState
import kong.unirest.Unirest

class AuthenticateTokens(private val loginUser: User) {

    private val mainEndPoint = "https://global.api.clockify.me"

    private var token: String = settingsState.token.value
    private var refreshToken: String = settingsState.refreshToken.value

    fun retrieveFullUser() : UserLogin? {
        var user: UserLogin? = null

        if(refreshToken.isTokenValid()) {
            Unirest.post("$mainEndPoint/auth/token/refresh")
                .header("Content-Type", "application/json")
                .accept("application/json")
                .body("""{"refreshToken":"$refreshToken"}""")
                .asObject(UserLogin::class.java).ifSuccess {
                    if(it.status != 200) {
                        user = null
                    } else it.body.let { login ->
                        token = login.token
                        refreshToken = login.refreshToken
                        saveTokens()
                        user = login
                    }
                }

            return user
        }

        Unirest.post("$mainEndPoint/auth/token")
            .header("Content-Type", "application/json")
            .accept("application/json")
            .body(loginUser)
            .asObject(UserLogin::class.java).ifSuccess {
                if(it.status != 200) {
                    user = null
                } else it.body.let { userLogin ->
                    token = userLogin.token
                    refreshToken = userLogin.refreshToken
                    saveTokens()
                    user = userLogin
                }
            }

        return user
    }

    fun retrieveToken() : String {
        if(token.isTokenValid())
            return token
        return retrieveFullUser()?.token ?: ""
    }

    private fun saveTokens() {
        settingsState.token.value = token
        settingsState.refreshToken.value = refreshToken
    }

    private fun String.isTokenValid() : Boolean{
        if(this.isBlank())
            return false

        val verifier = JWT.decode(this)
        return verifier.expiresAt.time > System.currentTimeMillis()
    }

}