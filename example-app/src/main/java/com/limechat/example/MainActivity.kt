package com.limechat.example

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.limechat.sdk.LimeChatWidget
import com.limechat.sdk.models.LimeChatUser
import com.limechat.sdk.models.WidgetConfig

/**
 * Example MainActivity demonstrating LimeChat SDK integration in Kotlin
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var limeChatWidget: LimeChatWidget
    private lateinit var tokenInput: EditText
    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setupViews()
        setupLimeChat()
    }
    
    private fun setupViews() {
        limeChatWidget = findViewById(R.id.limechat_widget)
        tokenInput = findViewById(R.id.input_token)
        nameInput = findViewById(R.id.input_name)
        emailInput = findViewById(R.id.input_email)
        
        // Set default test token (replace with your actual token)
        tokenInput.setText("MEFFACy4xaovJayhLjSt836h")
        
        findViewById<Button>(R.id.btn_initialize).setOnClickListener {
            initializeLimeChat()
        }
        
        findViewById<Button>(R.id.btn_open_chat).setOnClickListener {
            limeChatWidget.openChat()
        }
        
        findViewById<Button>(R.id.btn_hide_widget).setOnClickListener {
            limeChatWidget.hide()
        }
        
        findViewById<Button>(R.id.btn_show_widget).setOnClickListener {
            limeChatWidget.show()
        }
        
        findViewById<Button>(R.id.btn_java_example).setOnClickListener {
            startActivity(Intent(this, JavaExampleActivity::class.java))
        }
    }
    
    private fun setupLimeChat() {
        // Initial configuration with minimal setup
        val config = WidgetConfig.Builder()
            .websiteToken("MEFFACy4xaovJayhLjSt836h")
            .onWidgetLoad {
                runOnUiThread {
                    Toast.makeText(this, "Widget loaded successfully!", Toast.LENGTH_SHORT).show()
                }
            }
            .onWidgetClose {
                runOnUiThread {
                    Toast.makeText(this, "Widget closed", Toast.LENGTH_SHORT).show()
                }
            }
            .onError { exception ->
                runOnUiThread {
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            }
            .onUnreadCountUpdate { count ->
                runOnUiThread {
                    Toast.makeText(this, "Unread messages: $count", Toast.LENGTH_SHORT).show()
                }
            }
            .build()
        
        limeChatWidget.configure(config)
    }
    
    private fun initializeLimeChat() {
        val token = tokenInput.text.toString()
        val name = nameInput.text.toString()
        val email = emailInput.text.toString()
        
        if (token.isEmpty()) {
            Toast.makeText(this, "Please enter a website token", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Create user object
        val user = if (name.isNotEmpty() || email.isNotEmpty()) {
            LimeChatUser(
                name = name.ifEmpty { null },
                email = email.ifEmpty { null }
            )
        } else {
            null
        }
        
        // Create configuration with all options
        val config = WidgetConfig.Builder()
            .websiteToken(token)
            .apply {
                user?.let { user(it) }
            }
            .locale("en")
            .colorScheme(LimeChatWidget.ColorScheme.LIGHT)
            .customAttributes(
                mapOf(
                    "app_version" to "1.0.0",
                    "platform" to "Android Example",
                    "test_mode" to true
                )
            )
            .onWidgetLoad {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Chat initialized!", Toast.LENGTH_SHORT).show()
                }
            }
            .onWidgetClose {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Chat closed", Toast.LENGTH_SHORT).show()
                }
            }
            .onError { exception ->
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Error: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .onUnreadCountUpdate { count ->
                runOnUiThread {
                    // Update UI with unread count
                    println("Unread messages: $count")
                }
            }
            .build()
        
        // Reconfigure the widget
        limeChatWidget.configure(config)
        
        Toast.makeText(this, "LimeChat configured successfully!", Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clean up resources
        limeChatWidget.destroy()
    }
}