## Custom classes rules
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

## Keep classes as they are for Deserialization from Firebase
-keep class com.gabr.gabc.qook.infrastructure.user.UserDto { *; }
-keep class com.gabr.gabc.qook.infrastructure.planning.DayPlanningDto { *; }
-keep class com.gabr.gabc.qook.infrastructure.planning.MealDataDto { *; }
-keep class com.gabr.gabc.qook.infrastructure.ingredient.IngredientsDto { *; }
-keep class com.gabr.gabc.qook.infrastructure.recipe.RecipeDto { *; }
-keep class com.gabr.gabc.qook.infrastructure.sharedPlanning.SharedPlanningDto { *; }
-keep class com.gabr.gabc.qook.infrastructure.tag.TagDto { *; }

## Maintain init empty constructor and fields as they are for Deserialization
-keepclassmembers class com.gabr.gabc.qook.infrastructure.user.UserDto {
    <init>();
    <fields>;
}
-keepclassmembers class com.gabr.gabc.qook.infrastructure.planning.DayPlanningDto {
    <init>();
    <fields>;
}
-keepclassmembers class com.gabr.gabc.qook.infrastructure.planning.MealDataDto {
    <init>();
    <fields>;
}
-keepclassmembers class com.gabr.gabc.qook.infrastructure.ingredient.IngredientsDto {
    <init>();
    <fields>;
}
-keepclassmembers class com.gabr.gabc.qook.infrastructure.recipe.RecipeDto {
    <init>();
    <fields>;
}
-keepclassmembers class com.gabr.gabc.qook.infrastructure.sharedPlanning.SharedPlanningDto {
    <init>();
    <fields>;
}
-keepclassmembers class com.gabr.gabc.qook.infrastructure.tag.TagDto {
    <init>();
    <fields>;
}
