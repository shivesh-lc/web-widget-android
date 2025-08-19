package com.limechat.sdk.models

import com.limechat.sdk.LimeChatWidget

/**
 * Configuration for LimeChat Widget
 */
data class WidgetConfig(
    val websiteToken: String,
    val user: LimeChatUser? = null,
    val locale: String? = "en",
    val colorScheme: LimeChatWidget.ColorScheme? = LimeChatWidget.ColorScheme.LIGHT,
    val customAttributes: Map<String, Any>? = null,
    val baseUrl: String? = null,
    val onWidgetLoad: (() -> Unit)? = null,
    val onWidgetClose: (() -> Unit)? = null,
    val onError: ((Exception) -> Unit)? = null,
    val onUnreadCountUpdate: ((Int) -> Unit)? = null
) {
    class Builder {
        private var websiteToken: String = ""
        private var user: LimeChatUser? = null
        private var locale: String = "en"
        private var colorScheme: LimeChatWidget.ColorScheme = LimeChatWidget.ColorScheme.LIGHT
        private var customAttributes: Map<String, Any>? = null
        private var baseUrl: String? = null
        private var onWidgetLoad: (() -> Unit)? = null
        private var onWidgetClose: (() -> Unit)? = null
        private var onError: ((Exception) -> Unit)? = null
        private var onUnreadCountUpdate: ((Int) -> Unit)? = null
        
        fun websiteToken(token: String) = apply { this.websiteToken = token }
        fun user(user: LimeChatUser) = apply { this.user = user }
        fun locale(locale: String) = apply { this.locale = locale }
        fun colorScheme(scheme: LimeChatWidget.ColorScheme) = apply { this.colorScheme = scheme }
        fun customAttributes(attributes: Map<String, Any>) = apply { this.customAttributes = attributes }
        fun baseUrl(url: String) = apply { this.baseUrl = url }
        fun onWidgetLoad(callback: () -> Unit) = apply { this.onWidgetLoad = callback }
        fun onWidgetClose(callback: () -> Unit) = apply { this.onWidgetClose = callback }
        fun onError(callback: (Exception) -> Unit) = apply { this.onError = callback }
        fun onUnreadCountUpdate(callback: (Int) -> Unit) = apply { this.onUnreadCountUpdate = callback }
        
        fun build(): WidgetConfig {
            require(websiteToken.isNotEmpty()) { "Website token is required" }
            
            return WidgetConfig(
                websiteToken = websiteToken,
                user = user,
                locale = locale,
                colorScheme = colorScheme,
                customAttributes = customAttributes,
                baseUrl = baseUrl,
                onWidgetLoad = onWidgetLoad,
                onWidgetClose = onWidgetClose,
                onError = onError,
                onUnreadCountUpdate = onUnreadCountUpdate
            )
        }
    }
}