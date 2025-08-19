# Consumer ProGuard rules for LimeChat SDK
# These rules will be applied to apps that use this library

# Keep all public SDK classes and methods
-keep public class com.limechat.sdk.** { *; }

# Keep JavaScript interface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep WebView functionality
-keep class android.webkit.WebView { *; }
-keep class android.webkit.WebViewClient { *; }
-keep class android.webkit.WebChromeClient { *; }