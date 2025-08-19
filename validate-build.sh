#!/bin/bash

echo "🔍 Validating LimeChat Android SDK build..."

# Check for common issues
echo "📁 Checking directory structure..."

# Check if required files exist
REQUIRED_FILES=(
    "library/src/main/AndroidManifest.xml"
    "library/src/main/java/com/limechat/sdk/LimeChatWidget.kt"
    "library/src/main/java/com/limechat/sdk/models/LimeChatUser.kt"
    "library/src/main/java/com/limechat/sdk/models/WidgetConfig.kt"
    "library/src/main/java/com/limechat/sdk/utils/LimeChatWebView.kt"
    "library/src/main/res/layout/limechat_widget_layout.xml"
    "library/src/main/res/layout/limechat_bottom_sheet.xml"
    "library/src/main/res/values/colors.xml"
    "library/src/main/res/values/strings.xml"
    "library/src/main/res/values/styles.xml"
    "example-app/src/main/AndroidManifest.xml"
    "example-app/src/main/java/com/limechat/example/MainActivity.kt"
    "example-app/src/main/res/layout/activity_main.xml"
    "gradle/wrapper/gradle-wrapper.jar"
    "gradle/wrapper/gradle-wrapper.properties"
)

MISSING_FILES=()
for file in "${REQUIRED_FILES[@]}"; do
    if [ ! -f "$file" ]; then
        MISSING_FILES+=("$file")
    fi
done

if [ ${#MISSING_FILES[@]} -gt 0 ]; then
    echo "❌ Missing files:"
    for file in "${MISSING_FILES[@]}"; do
        echo "   - $file"
    done
else
    echo "✅ All required files present"
fi

echo ""
echo "🔧 Running build validation..."

# Try to build the library
echo "📦 Building library..."
./gradlew :library:assembleDebug --no-daemon --quiet

if [ $? -eq 0 ]; then
    echo "✅ Library build successful"
else
    echo "❌ Library build failed"
    echo "   Run './gradlew :library:assembleDebug' for detailed error messages"
fi

echo ""
echo "📱 Building example app..."
./gradlew :example-app:assembleDebug --no-daemon --quiet

if [ $? -eq 0 ]; then
    echo "✅ Example app build successful"
    echo "✅ SDK is ready for testing!"
    echo ""
    echo "🚀 Next steps:"
    echo "   1. Open in Android Studio: open -a 'Android Studio' ."
    echo "   2. Update token in MainActivity.kt"
    echo "   3. Run the example app"
else
    echo "❌ Example app build failed"
    echo "   Run './gradlew :example-app:assembleDebug' for detailed error messages"
fi