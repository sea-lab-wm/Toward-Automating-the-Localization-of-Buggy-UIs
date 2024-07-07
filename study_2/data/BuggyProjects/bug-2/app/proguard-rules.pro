# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/anton/Android/Sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontobfuscate

# requery: https://github.com/requery/requery/blob/master/requery-android/proguard-rules.pro
-dontwarn java.lang.FunctionalInterface
-dontwarn java.util.**
-dontwarn java.time.**
-dontwarn javax.annotation.**
-dontwarn javax.cache.**
-dontwarn javax.naming.**
-dontwarn javax.transaction.**
-dontwarn java.sql.**
-dontwarn javax.sql.**
-dontwarn android.support.**
-dontwarn io.requery.cache.**
-dontwarn io.requery.rx.**
-dontwarn io.requery.reactivex.**
-dontwarn io.requery.reactor.**
-dontwarn io.requery.query.**
-dontwarn io.requery.android.sqlcipher.**
-dontwarn io.requery.android.sqlitex.**
-keepclassmembers enum io.requery.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# requery: https://github.com/requery/requery/blob/master/requery-android/example/proguard-rules.pro
-keep class * extends java.lang.Enum {
}
-dontwarn rx.internal.**
-dontwarn android.support.**

# sqlite-android: https://github.com/requery/sqlite-android/blob/master/sqlite-android/proguard-rules.pro
-keepclasseswithmembers class io.requery.android.database.** {
  native <methods>;
  public <init>(...);
}
-keepnames class io.requery.android.database.** { *; }
-keep public class io.requery.android.database.sqlite.SQLiteFunction { *; }
-keep public class io.requery.android.database.sqlite.SQLiteCustomFunction { *; }
-keep public class io.requery.android.database.sqlite.SQLiteCursor { *; }
-keep public class io.requery.android.database.sqlite.SQLiteDebug** { *; }
-keep public class io.requery.android.database.sqlite.SQLiteDatabase { *; }
-keep public class io.requery.android.database.sqlite.SQLiteOpenHelper { *; }
-keep public class io.requery.android.database.sqlite.SQLiteStatement { *; }
-keep public class io.requery.android.database.CursorWindow { *; }
-keepattributes Exceptions,InnerClasses

# Exclude R from ProGuard to enable the font addon auto detection
# https://github.com/mikepenz/Android-Iconics#proguard
-keep class .R
-keep class **.R$* {
    <fields>;
}

# Proguard causing RuntimeException (Unmarshalling unknown type code)
# in Parcelable class
# https://stackoverflow.com/q/21342700/8035065
-keep class android.support.** { *; }
-dontwarn android.support.**

# https://stackoverflow.com/a/21380449/8035065
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}
