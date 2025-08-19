# Testing Guide for LimeChat Android SDK

## Prerequisites

1. **Android Studio** installed (Arctic Fox or newer)
2. **Android device or emulator** (API 21+)
3. **Your LimeChat website token**

## Quick Start Testing

### 1. Open in Android Studio

```bash
cd /Users/shiveshtiwari/Documents/limechat/FE/limechat-android-sdk
open -a "Android Studio" .
```

### 2. Configure Your Token

Edit `example-app/src/main/java/com/limechat/example/MainActivity.kt`:

```kotlin
// Replace this with your actual token
tokenInput.setText("YOUR_ACTUAL_WEBSITE_TOKEN")
```

### 3. Run the Example App

1. In Android Studio, select `example-app` from the run configuration
2. Connect your Android device or start an emulator
3. Click the Run button (green play icon)

## Testing During Development

### Live Reload Development

1. Make changes to the library code in `library/src/`
2. Click "Sync Now" in Android Studio
3. Re-run the example app to see changes

### Testing Specific Features

#### Test Widget Initialization
```kotlin
// In MainActivity.kt, test different configurations
val config = WidgetConfig.Builder()
    .websiteToken("YOUR_TOKEN")
    .locale("es")  // Test different locales
    .colorScheme(LimeChatWidget.ColorScheme.DARK)  // Test themes
    .build()
```

#### Test User Authentication
```kotlin
// Test with different user types
val userWithEmail = LimeChatUser(
    name = "Test User",
    email = "test@example.com"
)

val anonymousUser = null  // Test anonymous chat

val userWithPhone = LimeChatUser(
    phoneNumber = "+1234567890"
)
```

#### Test Custom Attributes
```kotlin
// Test metadata passing
.customAttributes(mapOf(
    "debug_mode" to true,
    "test_timestamp" to System.currentTimeMillis(),
    "app_version" to "test-1.0"
))
```

## Testing in Your Own App

### Option 1: Local Module Dependency

In your app's `settings.gradle`:
```gradle
include ':limechat-sdk'
project(':limechat-sdk').projectDir = new File('/path/to/limechat-android-sdk/library')
```

In your app's `build.gradle`:
```gradle
dependencies {
    implementation project(':limechat-sdk')
}
```

### Option 2: Local AAR File

1. Build the AAR:
```bash
cd /Users/shiveshtiwari/Documents/limechat/FE/limechat-android-sdk
./gradlew :library:assembleRelease
```

2. Copy AAR to your app:
```bash
cp library/build/outputs/aar/library-release.aar /path/to/your/app/libs/
```

3. In your app's `build.gradle`:
```gradle
dependencies {
    implementation files('libs/library-release.aar')
    
    // Also add SDK dependencies
    implementation 'androidx.webkit:webkit:1.8.0'
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
}
```

### Option 3: Local Maven Repository

1. Publish to local Maven:
```bash
cd /Users/shiveshtiwari/Documents/limechat/FE/limechat-android-sdk
./gradlew :library:publishToMavenLocal
```

2. In your app's `build.gradle`:
```gradle
repositories {
    mavenLocal()
}

dependencies {
    implementation 'com.limechat:android-sdk:1.0.0'
}
```

## Debugging Tips

### 1. Enable WebView Debugging

In `LimeChatWebView.kt`, add:
```kotlin
if (BuildConfig.DEBUG) {
    WebView.setWebContentsDebuggingEnabled(true)
}
```

Then use Chrome DevTools:
1. Open Chrome and go to `chrome://inspect`
2. Find your app's WebView
3. Click "inspect" to debug

### 2. Add Logging

```kotlin
// In your test app
val config = WidgetConfig.Builder()
    .websiteToken("YOUR_TOKEN")
    .onError { exception ->
        Log.e("LimeChat", "Error occurred", exception)
        exception.printStackTrace()
    }
    .build()
```

### 3. Test Network Conditions

Use Android Studio's network profiler or emulator settings to test:
- Slow connections
- No internet
- Connection drops

### 4. Test Different Devices

Test on various configurations:
- Different Android versions (5.0, 8.0, 11, 13, 14)
- Different screen sizes
- Different orientations

## Common Issues & Solutions

### Widget Not Appearing
- Check the token is correct
- Verify internet permissions in manifest
- Check if the parent layout allows the widget positioning

### WebView Not Loading
- Ensure WebView is enabled on the device
- Check ProGuard rules if using minification
- Verify the base URL is correct

### Crashes on Initialization
- Check all required dependencies are included
- Verify minimum SDK version (21)
- Look for missing resources or layouts

## Test Checklist

- [ ] Widget appears correctly
- [ ] Chat opens when button is tapped
- [ ] Chat closes properly
- [ ] User information is passed correctly
- [ ] Custom attributes work
- [ ] Unread badge shows/updates
- [ ] Different color schemes work
- [ ] Different locales load correctly
- [ ] Error callbacks trigger appropriately
- [ ] Memory is cleaned up on destroy
- [ ] Works on different Android versions
- [ ] Works in both portrait and landscape
- [ ] Works with ProGuard/R8 enabled

## Performance Testing

Monitor these metrics in Android Studio Profiler:
- Memory usage before/after opening chat
- CPU usage during WebView operations
- Network requests and response times
- App size increase with SDK

## Automated Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumented tests:
```bash
./gradlew connectedAndroidTest
```