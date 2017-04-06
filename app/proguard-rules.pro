# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android SDK\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keepattributes *Annotation*
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-keep class jp.wasabeef.** { *; }

-keepclasseswithmembers class * {
    @jp.wasabeef.* <methods>;
}
-dontwarn okio.**
-keep class okio.** { *; }
-keep interface okio.** { *; }
-keep class okio.** { *; }
# support-v4
#https://stackoverflow.com/questions/18978706/obfuscate-android-support-v7-widget-gridlayout-issue
-dontwarn android.support.v4.**
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v4.** { *; }


# support design
#@link http://stackoverflow.com/a/31028536
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

# support-v7
-dontwarn android.support.v7.**
-keep class android.support.v7.internal.** { *; }
-keep interface android.support.v7.internal.** { *; }
-keep class android.support.v7.** { *; }

# support-v7
-dontwarn com.google.gson.**
-keep interface com.google.gson.** { *; }
-keep class com.google.gson.** { *; }

# support-v7
-dontwarn io.smooch.core.**
-keep interface io.smooch.core.** { *; }
-keep class io.smooch.core.** { *; }