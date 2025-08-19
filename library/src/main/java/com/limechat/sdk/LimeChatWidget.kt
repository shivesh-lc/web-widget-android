package com.limechat.sdk

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.limechat.sdk.models.LimeChatUser
import com.limechat.sdk.models.WidgetConfig
import com.limechat.sdk.utils.LimeChatWebView
import kotlinx.coroutines.*

/**
 * Main LimeChat Widget component for native Android apps.
 * Provides a floating action button that opens a chat interface in a WebView.
 */
class LimeChatWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var floatingButton: ImageButton
    private lateinit var unreadBadge: TextView
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var webView: LimeChatWebView? = null
    
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    // Configuration
    private var websiteToken: String = ""
    private var user: LimeChatUser? = null
    private var locale: String = "en"
    private var colorScheme: ColorScheme = ColorScheme.LIGHT
    private var customAttributes: Map<String, Any> = emptyMap()
    private var baseUrl: String = "https://app.limechat.ai"
    
    // Callbacks
    private var onWidgetLoad: (() -> Unit)? = null
    private var onWidgetClose: (() -> Unit)? = null
    private var onError: ((Exception) -> Unit)? = null
    private var onUnreadCountUpdate: ((Int) -> Unit)? = null
    
    private var unreadCount: Int = 0
        set(value) {
            field = value
            updateUnreadBadge(value)
            onUnreadCountUpdate?.invoke(value)
        }
    
    init {
        setupView()
    }
    
    private fun setupView() {
        View.inflate(context, R.layout.limechat_widget_layout, this)
        
        floatingButton = findViewById(R.id.limechat_floating_button)
        unreadBadge = findViewById(R.id.limechat_unread_badge)
        
        floatingButton.setOnClickListener {
            openChatWidget()
        }
        
        // Initially hide unread badge
        unreadBadge.isVisible = false
    }
    
    /**
     * Configure the widget with your website token and optional parameters
     */
    fun configure(config: WidgetConfig) {
        this.websiteToken = config.websiteToken
        this.user = config.user
        this.locale = config.locale ?: "en"
        this.colorScheme = config.colorScheme ?: ColorScheme.LIGHT
        this.customAttributes = config.customAttributes ?: emptyMap()
        this.baseUrl = config.baseUrl ?: "https://widget.limechat.ai"
        
        // Set callbacks
        this.onWidgetLoad = config.onWidgetLoad
        this.onWidgetClose = config.onWidgetClose
        this.onError = config.onError
        this.onUnreadCountUpdate = config.onUnreadCountUpdate
        
        // Initialize WebView
        initializeWebView()
        
        // Start checking for unread messages
        startUnreadCountPolling()
    }
    
    private fun initializeWebView() {
        try {
            webView = LimeChatWebView(context).apply {
                configure(
                    websiteToken = websiteToken,
                    user = user,
                    locale = locale,
                    colorScheme = colorScheme,
                    customAttributes = customAttributes,
                    baseUrl = baseUrl
                )
                
                setOnLoadListener {
                    onWidgetLoad?.invoke()
                }
                
                setOnErrorListener { error ->
                    onError?.invoke(error)
                }
                
                setOnUnreadCountListener { count ->
                    unreadCount = count
                }
            }
        } catch (e: Exception) {
            onError?.invoke(e)
        }
    }
    
    private fun openChatWidget() {
        if (websiteToken.isEmpty()) {
            onError?.invoke(IllegalStateException("Website token not configured"))
            return
        }
        
        bottomSheetDialog = BottomSheetDialog(context, R.style.LimeChatBottomSheetDialog).apply {
            val sheetView = LayoutInflater.from(context)
                .inflate(R.layout.limechat_bottom_sheet, null)
            
            val webViewContainer = sheetView.findViewById<FrameLayout>(R.id.limechat_webview_container)
            val closeButton = sheetView.findViewById<ImageButton>(R.id.limechat_close_button)
            
            // Add WebView to container
            webView?.let { 
                (it.parent as? FrameLayout)?.removeView(it)
                val params = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                webViewContainer.removeAllViews()
                webViewContainer.addView(it, params)
                it.loadChat()
            }
            
            closeButton.setOnClickListener {
                dismiss()
                onWidgetClose?.invoke()
            }
            
            setContentView(sheetView)
            
            // Make it full screen
            behavior.peekHeight = resources.displayMetrics.heightPixels
            behavior.isDraggable = false
            @Suppress("RestrictedApi")
            run {
                behavior.skipCollapsed = true
                behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
            }
            
            show()
        }
        
        // Reset unread count when opening
        unreadCount = 0
    }
    
    private fun updateUnreadBadge(count: Int) {
        unreadBadge.apply {
            isVisible = count > 0
            text = if (count > 99) "99+" else count.toString()
        }
    }
    
    private fun startUnreadCountPolling() {
        coroutineScope.launch {
            while (isActive) {
                delay(30000) // Check every 30 seconds
                webView?.checkUnreadCount()
            }
        }
    }
    
    /**
     * Show the widget floating button
     */
    fun show() {
        visibility = View.VISIBLE
    }
    
    /**
     * Hide the widget floating button
     */
    fun hide() {
        visibility = View.GONE
    }
    
    /**
     * Open the chat programmatically
     */
    fun openChat() {
        openChatWidget()
    }
    
    /**
     * Close the chat programmatically
     */
    fun closeChat() {
        bottomSheetDialog?.dismiss()
    }
    
    /**
     * Update user information
     */
    fun updateUser(user: LimeChatUser) {
        this.user = user
        webView?.updateUser(user)
    }
    
    /**
     * Update custom attributes
     */
    fun updateCustomAttributes(attributes: Map<String, Any>) {
        this.customAttributes = attributes
        webView?.updateCustomAttributes(attributes)
    }
    
    /**
     * Clean up resources
     */
    fun destroy() {
        coroutineScope.cancel()
        webView?.destroy()
        bottomSheetDialog?.dismiss()
    }
    
    enum class ColorScheme {
        LIGHT, DARK, AUTO
    }
}