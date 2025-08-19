package com.limechat.sdk.models

/**
 * User information for personalized chat experience
 */
data class LimeChatUser(
    val name: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val identifierHash: String? = null
) {
    fun toJson(): Map<String, String> {
        val json = mutableMapOf<String, String>()
        name?.let { json["name"] = it }
        email?.let { json["email"] = it }
        phoneNumber?.let { json["phone_number"] = it }
        identifierHash?.let { json["identifier_hash"] = it }
        return json
    }
}