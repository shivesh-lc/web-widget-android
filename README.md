# LimeChat Android SDK

A powerful, easy-to-integrate Android SDK that brings LimeChat's customer support capabilities directly into your native Android applications. Built with Kotlin for modern Android development with full Java compatibility.

## Features

- **Simple Integration** - Add chat support in minutes with just a few lines of code
- **Floating Action Button** - Beautiful, customizable floating chat button
- **WebView-based Chat** - Seamless web-based chat experience
- **Unread Message Badge** - Real-time unread count updates
- **Full Customization** - Configure user info, locale, theme, and custom attributes
- **Kotlin & Java Support** - Works with both Kotlin and Java projects
- **Minimal Size** - Lightweight SDK with minimal impact on app size

## Installation

### Option 1: JitPack (Recommended)

Add JitPack repository to your root `build.gradle`:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency to your app's `build.gradle`:

```gradle
dependencies {
    implementation 'com.github.limechat:limechat-android-sdk:1.0.0'
}
```

**Note:** Replace `limechat` with your actual GitHub username and `limechat-android-sdk` with your actual repository name.

### Option 2: Local AAR

1. Build the AAR file:
```bash
./gradlew :library:assembleRelease
```

2. Copy the AAR from `library/build/outputs/aar/` to your app's `libs` folder

3. Add to your app's `build.gradle`:
```gradle
dependencies {
    implementation files('libs/limechat-sdk-1.0.0.aar')
}
```

## Quick Start

### 1. Add to Layout (XML)

```xml
<com.limechat.sdk.LimeChatWidget
    android:id="@+id/limechat_widget"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom|end"
    android:layout_margin="16dp" />
```

### 2. Configure in Activity/Fragment

#### Kotlin Example

```kotlin
class MainActivity : AppCompatActivity() {
    
    private lateinit var limeChatWidget: LimeChatWidget
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        limeChatWidget = findViewById(R.id.limechat_widget)
        
        val config = WidgetConfig.Builder()
            .websiteToken("YOUR_WEBSITE_TOKEN")
            .user(LimeChatUser(
                name = "John Doe",
                email = "john@example.com"
            ))
            .onWidgetLoad {
                Log.d("LimeChat", "Widget loaded successfully")
            }
            .onError { exception ->
                Log.e("LimeChat", "Error: ${exception.message}")
            }
            .build()
        
        limeChatWidget.configure(config)
    }
}
```

#### Java Example

```java
public class MainActivity extends AppCompatActivity {
    
    private LimeChatWidget limeChatWidget;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        limeChatWidget = findViewById(R.id.limechat_widget);
        
        LimeChatUser user = new LimeChatUser(
            "John Doe",
            "john@example.com",
            "+1234567890",
            null
        );
        
        WidgetConfig config = new WidgetConfig.Builder()
            .websiteToken("YOUR_WEBSITE_TOKEN")
            .user(user)
            .onWidgetLoad(() -> {
                Log.d("LimeChat", "Widget loaded successfully");
            })
            .onError(exception -> {
                Log.e("LimeChat", "Error: " + exception.getMessage());
            })
            .build();
        
        limeChatWidget.configure(config);
    }
}
```

## Advanced Configuration

### Full Configuration Options

```kotlin
val config = WidgetConfig.Builder()
    .websiteToken("YOUR_WEBSITE_TOKEN")        // Required
    .user(LimeChatUser(                        // Optional user info
        name = "John Doe",
        email = "john@example.com",
        phoneNumber = "+1234567890",
        identifierHash = "secure_hash"
    ))
    .locale("en")                               // Language locale
    .colorScheme(ColorScheme.LIGHT)            // LIGHT, DARK, or AUTO
    .customAttributes(mapOf(                    // Custom metadata
        "subscription" to "premium",
        "user_id" to "12345",
        "app_version" to BuildConfig.VERSION_NAME
    ))
    .onWidgetLoad {                             // Success callback
        // Widget loaded successfully
    }
    .onWidgetClose {                            // Close callback
        // User closed the chat
    }
    .onError { exception ->                     // Error handling
        // Handle errors
    }
    .onUnreadCountUpdate { count ->             // Unread messages
        // Update your UI with unread count
    }
    .build()
```

### Programmatic Control

```kotlin
// Open chat programmatically
limeChatWidget.openChat()

// Close chat programmatically
limeChatWidget.closeChat()

// Show/hide the floating button
limeChatWidget.show()
limeChatWidget.hide()

// Update user information
limeChatWidget.updateUser(newUser)

// Update custom attributes
limeChatWidget.updateCustomAttributes(newAttributes)

// Clean up resources
override fun onDestroy() {
    super.onDestroy()
    limeChatWidget.destroy()
}
```

### Styling & Positioning

#### Position the Widget

```xml
<!-- Bottom Right (Default) -->
<com.limechat.sdk.LimeChatWidget
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom|end"
    android:layout_margin="16dp" />

<!-- Bottom Left -->
<com.limechat.sdk.LimeChatWidget
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom|start"
    android:layout_margin="16dp" />
```

#### Custom Colors

Override these colors in your app's `colors.xml`:

```xml
<resources>
    <color name="limechat_primary">#YourColor</color>
    <color name="limechat_primary_dark">#YourDarkColor</color>
    <color name="limechat_badge_color">#YourBadgeColor</color>
</resources>
```

## API Reference

### WidgetConfig.Builder

| Method | Description |
|--------|-------------|
| `websiteToken(String)` | **Required.** Your LimeChat website token |
| `user(LimeChatUser)` | User information for personalization |
| `locale(String)` | Language locale (e.g., "en", "es", "fr") |
| `colorScheme(ColorScheme)` | Theme: LIGHT, DARK, or AUTO |
| `customAttributes(Map)` | Custom metadata for the session |
| `onWidgetLoad(() -> Unit)` | Callback when widget loads |
| `onWidgetClose(() -> Unit)` | Callback when widget closes |
| `onError((Exception) -> Unit)` | Error handler |
| `onUnreadCountUpdate((Int) -> Unit)` | Unread message count updates |

### LimeChatUser

| Property | Type | Description |
|----------|------|-------------|
| `name` | String? | User's display name |
| `email` | String? | User's email address |
| `phoneNumber` | String? | User's phone number |
| `identifierHash` | String? | Secure user identifier |

### LimeChatWidget Methods

| Method | Description |
|--------|-------------|
| `configure(WidgetConfig)` | Initialize the widget with configuration |
| `openChat()` | Open the chat interface programmatically |
| `closeChat()` | Close the chat interface |
| `show()` | Show the floating button |
| `hide()` | Hide the floating button |
| `updateUser(LimeChatUser)` | Update user information |
| `updateCustomAttributes(Map)` | Update custom attributes |
| `destroy()` | Clean up resources |

## ProGuard Rules

If you're using ProGuard/R8, add these rules to your `proguard-rules.pro`:

```pro
-keep class com.limechat.sdk.** { *; }
-keep interface com.limechat.sdk.** { *; }

# WebView
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
```

## Permissions

The SDK automatically includes these permissions in its manifest:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Requirements

- **Java 17+** (for development and building)
- Android API level 21+ (Android 5.0 Lollipop)
- Android Studio Arctic Fox or newer (recommended)
- Kotlin 1.9.0+ (for Kotlin projects)
- AndroidX libraries

### Development Setup
```bash
# Install Java (required for building)
brew install openjdk@17

# Set JAVA_HOME (add to ~/.zshrc or ~/.bash_profile)
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
export PATH=$JAVA_HOME/bin:$PATH
```

## Migration from React Native SDK

If you're migrating from the React Native SDK, the Android SDK provides similar functionality:

| React Native | Android Native |
|--------------|----------------|
| `websiteToken` | `websiteToken()` |
| `user` prop | `user()` builder method |
| `locale` | `locale()` |
| `colorScheme` | `colorScheme()` |
| `customAttributes` | `customAttributes()` |
| `onWidgetLoad` | `onWidgetLoad()` |
| `onWidgetClose` | `onWidgetClose()` |
| `onError` | `onError()` |

## Example App

Check out the `example-app` directory for a complete implementation example with both Kotlin and Java activities.

To run the example:

1. Clone the repository
2. Open the project in Android Studio
3. Replace `YOUR_WEBSITE_TOKEN` with your actual token
4. Run the `example-app` module

## Publishing to JitPack

To publish your SDK to JitPack:

1. Push your code to GitHub
2. Create a release/tag (e.g., `v1.0.0`)
3. Go to https://jitpack.io/#YourUsername/YourRepo
4. Click "Get it" to build

Users can then add your SDK using:

```gradle
implementation 'com.github.YourUsername:YourRepo:1.0.0'
```

## Support

- Documentation: [LimeChat Docs](https://docs.limechat.ai)
- Email: support@limechat.ai
- Issues: [GitHub Issues](https://github.com/limechat/android-sdk/issues)

## License

MIT License - see LICENSE file for details