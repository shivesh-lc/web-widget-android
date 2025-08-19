package com.limechat.sdk.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.webkit.*
import com.google.gson.Gson
import com.limechat.sdk.LimeChatWidget
import com.limechat.sdk.models.LimeChatUser

/**
 * Custom WebView for LimeChat widget
 */
@SuppressLint("SetJavaScriptEnabled")
class LimeChatWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {
    
    private val gson = Gson()
    private var websiteToken: String = ""
    private var user: LimeChatUser? = null
    private var locale: String = "en"
    private var colorScheme: LimeChatWidget.ColorScheme = LimeChatWidget.ColorScheme.LIGHT
    private var customAttributes: Map<String, Any> = emptyMap()
    private var baseUrl: String = "https://widget.limechat.ai"
    
    private var onLoadListener: (() -> Unit)? = null
    private var onErrorListener: ((Exception) -> Unit)? = null
    private var onUnreadCountListener: ((Int) -> Unit)? = null
    
    init {
        setupWebView()
    }
    
    private fun setupWebView() {
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = false
            allowContentAccess = false
            cacheMode = WebSettings.LOAD_DEFAULT
            setGeolocationEnabled(false)
            setSupportZoom(false)
            builtInZoomControls = false
            displayZoomControls = false
        }
        
        // Set transparent background
        setBackgroundColor(Color.TRANSPARENT)
        
        // Add JavaScript interface for communication
        addJavascriptInterface(JavaScriptInterface(), "LimeChatAndroid")
        
        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                injectConfiguration()
                onLoadListener?.invoke()
            }
            
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                onErrorListener?.invoke(
                    Exception("WebView error: ${error?.description}")
                )
            }
            
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url?.toString() ?: return false
                
                // Handle external links
                if (!url.startsWith(baseUrl)) {
                    // Open in external browser
                    return true
                }
                
                return false
            }
        }
        
        webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(message: ConsoleMessage?): Boolean {
                // Log JavaScript console messages for debugging
                return super.onConsoleMessage(message)
            }

            // Suppress JavaScript alert/confirm/prompt dialogs to avoid UX interruptions
            override fun onJsAlert(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                result?.confirm()
                return true
            }

            override fun onJsConfirm(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                result?.cancel()
                return true
            }

            override fun onJsPrompt(
                view: WebView?,
                url: String?,
                message: String?,
                defaultValue: String?,
                result: JsPromptResult?
            ): Boolean {
                result?.cancel()
                return true
            }
        }
    }
    
    fun configure(
        websiteToken: String,
        user: LimeChatUser?,
        locale: String,
        colorScheme: LimeChatWidget.ColorScheme,
        customAttributes: Map<String, Any>,
        baseUrl: String
    ) {
        this.websiteToken = websiteToken
        this.user = user
        this.locale = locale
        this.colorScheme = colorScheme
        this.customAttributes = customAttributes
        this.baseUrl = baseUrl
    }
    
    fun loadChat() {
        val url = buildWidgetUrl()
        loadUrl(url)
    }
    
    private fun buildWidgetUrl(): String {
        val builder = Uri.parse(baseUrl).buildUpon()
        
        // Add website token
        builder.appendQueryParameter("websiteToken", websiteToken)
        
        // Add locale
        builder.appendQueryParameter("locale", locale)
        
        // Add color scheme
        builder.appendQueryParameter("colorScheme", colorScheme.name.lowercase())
        
        // Add platform identifier
        builder.appendQueryParameter("platform", "android")
        builder.appendQueryParameter("sdk_version", "1.0.0")
        
        return builder.build().toString()
    }
    
    private fun injectConfiguration() {
        val config = mutableMapOf<String, Any>()
        
        user?.let {
            config["user"] = it.toJson()
        }
        
        if (customAttributes.isNotEmpty()) {
            config["customAttributes"] = customAttributes
        }
        
        val configJson = gson.toJson(config)
        val javascript = """
            window.limechatConfig = $configJson;
            if (window.initializeLimeChat) {
                window.initializeLimeChat(window.limechatConfig);
            }
        """.trimIndent()
        
        evaluateJavascript(javascript, null)
    }
    
    fun updateUser(user: LimeChatUser) {
        this.user = user
        val userJson = gson.toJson(user.toJson())
        evaluateJavascript(
            "window.updateLimeChatUser && window.updateLimeChatUser($userJson)",
            null
        )
    }
    
    fun updateCustomAttributes(attributes: Map<String, Any>) {
        this.customAttributes = attributes
        val attributesJson = gson.toJson(attributes)
        evaluateJavascript(
            "window.updateLimeChatAttributes && window.updateLimeChatAttributes($attributesJson)",
            null
        )
    }
    
    fun checkUnreadCount() {
        evaluateJavascript(
            "window.getLimeChatUnreadCount && window.getLimeChatUnreadCount()",
            null
        )
    }
    
    fun setOnLoadListener(listener: () -> Unit) {
        onLoadListener = listener
    }
    
    fun setOnErrorListener(listener: (Exception) -> Unit) {
        onErrorListener = listener
    }
    
    fun setOnUnreadCountListener(listener: (Int) -> Unit) {
        onUnreadCountListener = listener
    }
    
    /**
     * JavaScript interface for communication between WebView and native code
     */
    inner class JavaScriptInterface {
        @JavascriptInterface
        fun onUnreadCountUpdate(count: Int) {
            post {
                onUnreadCountListener?.invoke(count)
            }
        }
        
        @JavascriptInterface
        fun onError(message: String) {
            post {
                onErrorListener?.invoke(Exception(message))
            }
        }
        
        @JavascriptInterface
        fun log(message: String) {
            // For debugging JavaScript logs
        }
    }
}