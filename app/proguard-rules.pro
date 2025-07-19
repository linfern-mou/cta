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
# Ignore warnings from Netty
-dontwarn io.netty.**
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean
-dontwarn org.apache.log4j.Level
-dontwarn org.apache.log4j.Logger
-dontwarn org.apache.log4j.Priority
-dontwarn org.apache.logging.log4j.Level
-dontwarn org.apache.logging.log4j.LogManager
-dontwarn org.apache.logging.log4j.Logger
-dontwarn org.apache.logging.log4j.message.MessageFactory
-dontwarn org.apache.logging.log4j.spi.ExtendedLogger
-dontwarn org.apache.logging.log4j.spi.ExtendedLoggerWrapper
-dontwarn org.conscrypt.BufferAllocator
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.HandshakeListener
-dontwarn org.eclipse.jetty.npn.NextProtoNego$ClientProvider
-dontwarn org.eclipse.jetty.npn.NextProtoNego$Provider
-dontwarn org.eclipse.jetty.npn.NextProtoNego$ServerProvider
-dontwarn org.eclipse.jetty.npn.NextProtoNego
-dontwarn reactor.blockhound.integration.BlockHoundIntegration

# Ktor Server


-keep class io.ktor.server.config.HoconConfigLoader { *; }


# Logback (Custom rules, see https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-logback-android.pro)
# to ignore warnings coming from slf4j and logback

# Remove slf4j log
-assumenosideeffects class * implements org.slf4j.Logger {
    public *** trace(...);
    public *** debug(...);
    public *** info(...);
    public *** warn(...);
    public *** error(...);
}

-assumenosideeffects class * implements org.slf4j.LoggerFactory {
    public *** getLogger(...);
}


-keepattributes SourceFile,LineNumberTable

-keepattributes Signature,InnerClasses
# 保留所有 Netty 类
-keep class io.netty.** { *; }

# 保留 Netty 的内部类
-keep class io.netty.**$* { *; }

# 保留 Netty 的注解
-keep @interface io.netty.**

# 保留 Netty 的 native 方法
-keepclasseswithmembernames,includedescriptorclasses class io.netty.** {
    native <methods>;
}

# 保留 Netty 的序列化相关类
-keepclassmembers class io.netty.** implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 保留 Netty 的 ChannelFuture 相关类
-keep class io.netty.channel.ChannelFuture { *; }
-keep class io.netty.util.concurrent.Future { *; }

# 保留 Netty 的异常类
-keep class io.netty.** extends java.lang.Exception { *; }

# 保留 Netty 的注解处理器
-keepclassmembers class * extends io.netty.** {
    @io.netty.** *;
}

# 不警告 Netty 相关的类
-dontwarn io.netty.**
# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn java.beans.BeanInfo
-dontwarn java.beans.IntrospectionException
-dontwarn java.beans.Introspector
-dontwarn java.beans.PropertyDescriptor
-dontwarn javax.lang.model.element.Modifier



# 禁用代码混淆
-dontobfuscate