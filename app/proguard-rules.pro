# Retrofit & Gson
-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.neoplay.radio.data.api.dto.** { *; }
-keep class com.neoplay.radio.data.update.** { *; }

# Media3 / ExoPlayer
-keep class androidx.media3.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
