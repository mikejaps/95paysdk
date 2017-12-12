 To enable ProGuard in your project, edit project.properties
 to define the proguard.config property as described in that file.

 Add project specific ProGuard rules here.
 By default, the flags in this file are appended to flags specified
 in ${sdk.dir}/tools/proguard/proguard-android.txt
 You can edit the include path and order by changing the ProGuard
 include property in project.properties.

 For more details, see
   http://developer.android.com/guide/developing/tools/proguard.html

 Add any project specific keep options here:

 If your project uses WebView with JS, uncomment the following
 and specify the fully qualified class name to the JavaScript interface
 class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}



-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontoptimize
-verbose
-dontskipnonpubliclibraryclassmembers
-ignorewarnings
-dontnote
-dontwarn
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*


-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,
                SourceFile,LineNumberTable,*Annotation*,EnclosingMethod



########### umeng ######################

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keep public class [com.gandalf.a].R$*{
public static final int *;
}

-keep class com.umeng.**{*;}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

########### umeng ######################




-keep class de.mindpipe.android.logging.log4j.**{*;}
-keep class org.apache.log4j.**{*;}
-keep class com.google.gson.**{*;}
-keep class okhttp3.**{*;}
-keep class okio.**{*;}
-keep class com.zhy.http.okhttp.**{*;}



-keepattributes Exceptions,InnerClasses,Signature,Deprecated,
                SourceFile,LineNumberTable,*Annotation*,EnclosingMethod




########### daemon #####################

-keep class com.gandalf.daemon.**{*;}


########### daemon #####################

-keep class com.thirdpartynoexport.sub.**{*;}





