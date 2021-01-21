package io.github.ricardormdev.clockifyplugin.api.websocket.authentication

data class User(val email: String, val password: String)

data class UserLogin(val id: String, val email: String, val name: String, val token: String,
                     val refreshToken: String, val membership: Array<Membership>)

data class Membership(val userId: String, val targetId: String, val membershipType: String,
                      val membershipStatus: String)

data class Token(val name: String, val exp: Int, val email: String)