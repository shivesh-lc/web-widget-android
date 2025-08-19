package com.limechat.example;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.limechat.sdk.LimeChatWidget;
import com.limechat.sdk.models.LimeChatUser;
import com.limechat.sdk.models.WidgetConfig;
import java.util.HashMap;
import java.util.Map;

/**
 * Example showing LimeChat SDK integration in Java
 */
public class JavaExampleActivity extends AppCompatActivity {
    
    private LimeChatWidget limeChatWidget;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_example);
        
        setupLimeChat();
    }
    
    private void setupLimeChat() {
        limeChatWidget = findViewById(R.id.limechat_widget_java);
        
        // Create user
        LimeChatUser user = new LimeChatUser(
            "John Doe",
            "john@example.com",
            "+1234567890",
            null
        );
        
        // Create custom attributes
        Map<String, Object> customAttributes = new HashMap<>();
        customAttributes.put("subscription", "premium");
        customAttributes.put("user_id", "12345");
        customAttributes.put("language", "en");
        
        // Build configuration
        WidgetConfig config = new WidgetConfig.Builder()
            .websiteToken("MEFFACy4xaovJayhLjSt836h")
            .user(user)
            .locale("en")
            .colorScheme(LimeChatWidget.ColorScheme.LIGHT)
            .customAttributes(customAttributes)
            .onWidgetLoad(() -> {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Widget loaded!", Toast.LENGTH_SHORT).show();
                });
                return null;
            })
            .onWidgetClose(() -> {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Widget closed", Toast.LENGTH_SHORT).show();
                });
                return null;
            })
            .onError(exception -> {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error: " + exception.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
                return null;
            })
            .onUnreadCountUpdate(count -> {
                runOnUiThread(() -> {
                    // Update your UI with unread count
                    System.out.println("Unread messages: " + count);
                });
                return null;
            })
            .build();
        
        // Configure the widget
        limeChatWidget.configure(config);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (limeChatWidget != null) {
            limeChatWidget.destroy();
        }
    }
}