# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

-keep class com.gabr.gabc.qook.domain.tag.Tag { *; }
-keep class com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanning { *; }
-keep class com.gabr.gabc.qook.domain.recipe.Recipe { *; }
-keep class com.gabr.gabc.qook.domain.planning.DayPlanning { *; }
-keep class com.gabr.gabc.qook.domain.planning.MealData { *; }
-keep class com.gabr.gabc.qook.domain.ingredients.Ingredients { *; }
-keep class com.gabr.gabc.qook.domain.user.User { *; }

-keepclassmembers class com.gabr.gabc.qook.infrastructure.user.UserDto {
    <init>();
}
-keepclassmembers class com.gabr.gabc.qook.infrastructure.planning.DayPlanningDto {
    <init>();
}
-keepclassmembers class com.gabr.gabc.qook.infrastructure.ingredient.IngredientsDto {
    <init>();
}
-keepclassmembers class com.gabr.gabc.qook.infrastructure.recipe.RecipeDto {
    <init>();
}
-keepclassmembers class com.gabr.gabc.qook.infrastructure.sharedPlanning.SharedPlanningDto {
    <init>();
}
-keepclassmembers class com.gabr.gabc.qook.infrastructure.tag.TagDto {
    <init>();
}
