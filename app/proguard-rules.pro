// ProGuard rules for FocusBloom
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod

// Keep Room entities
-keep class com.focusbloom.data.local.entity.** { *; }
-keep class com.focusbloom.domain.model.** { *; }

// Keep serializable/parcelable
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

// Keep Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

// Keep Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp { *; }

// Keep Coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

// Keep Compose
-keep class androidx.compose.** { *; }

// Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}