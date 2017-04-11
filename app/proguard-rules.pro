# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Android\android-sdk/tools/proguard/proguard-android.txt
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

-keep interface org.yamj.api.common.** { *; }
###--- com.android.support:design:25.1.0
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

###--- com.android.support:cardview-v7:25.1.0'
# http://stackoverflow.com/questions/29679177/cardview-shadow-not-appearing-in-lollipop-after-obfuscate-with-proguard/29698051
-keep class android.support.v7.widget.RoundRectDrawable { *; }

###--- com.github.wooplr:Spotlight:1.2.3
-keep class com.wooplr.spotlight.** { *; }
-keep interface com.wooplr.spotlight.**
-keep enum com.wooplr.spotlight.**

###--- com.squareup.picasso:picasso:2.5.2
### OKHTTP

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote okhttp3.internal.Platform


### OKIO

###--- com.squareup.okhttp:okhttp:2.4.0
# java.nio.file.* usage which cannot be used at runtime. Animal sniffer annotation.
-dontwarn okio.Okio
# JDK 7-only method which is @hide on Android. Animal sniffer annotation.
-dontwarn okio.DeflaterSink

# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

# OkHttp (from https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-square-okhttp3.pro)
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Okio (from https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-square-okio.pro)
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**

###--- pl.droidsonroids.gif:android-gif-drawable:1.2.6
-keep public class pl.droidsonroids.gif.GifIOException{<init>(int);}

###--- com.squareup.leakcanary
-dontwarn com.squareup.haha.guava.**
-dontwarn com.squareup.haha.perflib.**
-dontwarn com.squareup.haha.trove.**
-dontwarn com.squareup.leakcanary.**
-keep class com.squareup.haha.** { *; }
-keep class com.squareup.leakcanary.** { *; }

# Marshmallow removed Notification.setLatestEventInfo()
-dontwarn android.app.Notification

###--- Customs

-keep interface org.yamj.api.common.** { *; }
-keep class org.yamj.api.common.** { *; }
-dontwarn org.yamj.api.common.**

#-keep public interface org.yamj.api.common.* { *; }
-keepnames class info.movito.themoviedbapi.tools.** { *; }

-keep class info.movito.themoviedbapi.** { *; }
-keep interface info.movito.themoviedbapi.** { *; }

-dontwarn org.apache.commons.**
-keep class org.slf4j.** { *; }
-keep interface org.slf4j.** { *; }
-dontwarn org.slf4j.**

#-libraryjars libs/jackson-annotations-2.0.0.jar
#-libraryjars libs/jackson-core-2.0.0.jar
-keepattributes *Annotation*,EnclosingMethod,Signature
-keepnames class com.fasterxml.jackson.** { *; }
 -dontwarn com.fasterxml.jackson.databind.**
 -keep class org.codehaus.** { *; }
 -keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
 public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *; }
-keep public class your.class.** {
  public void set*(***);
  public *** get*();
}

-dontskipnonpubliclibraryclassmembers

-keepattributes *Annotation*,EnclosingMethod

-keepnames class org.codehaus.jackson.** { *; }

-dontwarn javax.xml.**
-dontwarn javax.xml.stream.events.**
-dontwarn com.fasterxml.jackson.databind.**

###--- Firebase

# Proguard flags for consumers of the Google Play services SDK
# https://developers.google.com/android/guides/setup#add_google_play_services_to_your_project

# Keep SafeParcelable value, needed for reflection. This is required to support backwards
# compatibility of some classes.
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

# Needed for Parcelable/SafeParcelable classes & their creators to not get renamed, as they are
# found via reflection.
-keepnames class * implements android.os.Parcelable
-keepclassmembers class * implements android.os.Parcelable {
  public static final *** CREATOR;
}

# Keep the classes/members we need for client functionality.
-keep @interface android.support.annotation.Keep
-keep @android.support.annotation.Keep class *
-keepclasseswithmembers class * {
  @android.support.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
  @android.support.annotation.Keep <methods>;
}

# Keep the names of classes/members we need for client functionality.
-keep @interface com.google.android.gms.common.annotation.KeepName
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
  @com.google.android.gms.common.annotation.KeepName *;
}

# Keep Dynamite API entry points
-keep @interface com.google.android.gms.common.util.DynamiteApi
-keep @com.google.android.gms.common.util.DynamiteApi public class * {
  public <fields>;
  public <methods>;
}

# Needed when building against pre-Marshmallow SDK.
-dontwarn android.security.NetworkSecurityPolicy

# Needed when building against Marshmallow SDK.
-dontwarn android.app.Notification

# Needed for isDeviceProtectedStorage when building against a pre-Nougat SDK.
-dontwarn android.content.Context


##-------------Begin Settings For Guava ------------------------------------------------------------
#https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-guava.pro
# Configuration for Guava
#
# disagrees with instructions provided by Guava project: https://code.google.com/p/guava-libraries/wiki/UsingProGuardWithGuava
#
# works if you add the following line to the Gradle dependencies
#
# provided 'javax.annotation:jsr250-api:1.0'
-keep class com.google.common.io.Resources {
    public static <methods>;
}
-keep class com.google.common.collect.Lists {
    public static ** reverse(**);
}
-keep class com.google.common.base.Charsets {
    public static <fields>;
}

-keep class com.google.common.base.Joiner {
    public static Joiner on(String);
    public ** join(...);
}

-keep class com.google.common.collect.MapMakerInternalMap$ReferenceEntry
-keep class com.google.common.cache.LocalCache$ReferenceEntry

-dontwarn sun.misc.Unsafe
-dontwarn javax.annotation.**

##-------------End Settings For Guava --------------------------------------------------------------

## GLide

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
