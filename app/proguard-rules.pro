# Merge
-flattenpackagehierarchy com.github.catvod.spider.merge
-dontwarn org.slf4j.impl.StaticLoggerBinder

# Spider
-keep class com.github.catvod.js.* { *; }
-keep class com.github.catvod.crawler.* { *; }
-keep class com.github.catvod.spider.* { public <methods>; }
-keep class com.github.catvod.parser.* { public <methods>; }

# AndroidX
-keep class androidx.core.** { *; }

# Gson
-keep class com.google.gson.** { *; }

# OkHttp
-dontwarn okhttp3.**
-keep class okio.** { *; }
-keep class okhttp3.** { *; }

# Logger
-keep class com.orhanobut.logger.** { *; }

# QuickJS
-keep class com.whl.quickjs.** { *; }

# Sardine
-keep class com.thegrizzlylabs.sardineandroid.** { *; }

# Smbj
-dontwarn org.xmlpull.v1.**
-dontwarn android.content.res.**
-keep class com.hierynomus.** { *; }
-keep class net.engio.mbassy.** { *; }

# Zxing
-keep class com.google.zxing.** { *; }
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn org.bouncycastle.jce.provider.BouncyCastleProvider

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn sun.misc.Service
-dontwarn sun.misc.ServiceConfigurationError
-dontwarn sun.security.action.GetBooleanAction
-dontwarn sun.security.action.GetIntegerAction
-dontwarn sun.security.action.GetLongAction



# Logback (Custom rules, see https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-logback-android.pro)
# to ignore warnings coming from slf4j and logback

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn com.sun.net.httpserver.HttpContext
-dontwarn com.sun.net.httpserver.HttpHandler
-dontwarn com.sun.net.httpserver.HttpServer
-dontwarn java.lang.System$Logger$Level
-dontwarn java.lang.System$Logger

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn org.ietf.jgss.ChannelBinding
-dontwarn org.ietf.jgss.MessageProp
-dontwarn org.ietf.jgss.Oid
-dontwarn sun.reflect.CallerSensitive
-dontwarn sun.reflect.Reflection
# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn javax.security.auth.kerberos.KeyTab
-dontwarn sun.net.util.URLUtil
-dontwarn sun.net.www.ParseUtil
-dontwarn sun.nio.ByteBuffered
-dontwarn sun.nio.ch.Interruptible
-dontwarn sun.reflect.ConstantPool
-dontwarn sun.reflect.annotation.AnnotationType
-dontwarn sun.usagetracker.UsageTrackerClient

-keepattributes SourceFile,LineNumberTable

-assumenosideeffects class org.slf4j.Logger{*;}
-keep class sun.misc.** { *; }


# 禁用代码混淆
-dontobfuscate