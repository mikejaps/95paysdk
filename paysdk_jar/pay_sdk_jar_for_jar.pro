#-dontwarn **
#-target 1.7
#-dontusemixedcaseclassnames
#-dontskipnonpubliclibraryclasses
#-dontpreverify
#-verbose


-dontnote
-dontwarn

# 代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-optimizationpasses 5

# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames

# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses

# 这句话能够使我们的项目混淆后产生映射文件
# 包含有类名->混淆后类名的映射关系
-verbose

# 指定不去忽略非公共库的类成员
-dontskipnonpubliclibraryclassmembers

# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify

#EnclosingMethod 保护内部类的暴露范围
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod


# 抛出异常时保留代码行号
#-keepattributes SourceFile,LineNumberTable

# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
#-optimizations !code/simplification/cast,!field/*,!class/merging/*


#############################################
#
# Android开发中一些需要保留的公共部分
#
#############################################

# 保留我们使用的四大组件，自定义的Application等等这些类不被混淆
# 因为这些子类都有可能被外部调用
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService




#This will not remove error log
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

# 保留R下面的资源
-keep class **.R$* {*;}

# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留在Activity中的方法参数是view的方法，
# 这样以来我们在layout中写的onClick就不会被影响
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

# webView处理，项目中没有使用到webView忽略即可
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
    public *;
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.webView, jav.lang.String);
}

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


############## release for apk to test ######################

#-keep class de.mindpipe.android.logging.log4j.**{*;}
#-keep class org.apache.log4j.**{*;}
#-keep class com.google.**{*;}
#-keep class okhttp3.**{*;}
#-keep class okio.**{*;}
#-keep class com.zhy.http.okhttp.**{*;}
#-keep class com.orhanobut.hawk.**{*;}


############## release to jar to load ######################
-keep class de.mindpipe.android.logging.log4j.**{*;}
#-keep class org.apache.log4j.**{*;}
-keep class com.google.**{*;}
-keep class okhttp3.**{*;}
-keep class okio.**{*;}
-keep class com.zhy.http.okhttp.**{*;}
-keep class com.orhanobut.hawk.**{*;}
-keep class k.m.**{*;}
-keep class com.gandalf.daemon.utils.**{*;}
-keep class com.thirdparty.engine.ServiceStub{*;}


## other
#-keep class com.msm.modu1e.utils.ShellUtils{*;}
#-keep class com.msm.modu1e.utils.ShellUtils$CommandResult{*;}


################# third part sdk ##########################

#### yunfeng lib
-keep class com.mj.**{*;}
-keep class com.example.mj_demo.**{*;}


##### yufeng jar
-keep class com.migu.common.util.**{*;}
-keep class com.test.pay.damo.**{*;}
-keep class biz.source_code.base64Coder.**{*;}


#### weiyun lib
-keep class com.wyzf.**{*;}


##### yunbei lib
-keep class cn.utopay.internal.sdk.**{*;}
-keep class cn.utopay.sdk.**{*;}
-keep class com.bae.tool.**{*;}


################# third part sdk ##########################

