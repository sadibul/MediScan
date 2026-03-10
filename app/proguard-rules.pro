# ═══════════════════════════════════════════════════════════════════
#  MediScan — ProGuard / R8 Rules for Release Build
# ═══════════════════════════════════════════════════════════════════

# ── Preserve stack traces (useful for crash reporting) ──────────
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ─────────────────────────────────────────────────────────────────
#  Kotlin
# ─────────────────────────────────────────────────────────────────
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$WhenMappings { <fields>; }
-keepclassmembers class kotlin.Lazy { <fields>; }

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ─────────────────────────────────────────────────────────────────
#  Hilt / Dagger
# ─────────────────────────────────────────────────────────────────
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.android.AndroidEntryPoint <methods>;
}
-keepclasseswithmembers class * {
    @javax.inject.Inject <fields>;
    @javax.inject.Inject <methods>;
    @javax.inject.Inject <init>(...);
}

# ─────────────────────────────────────────────────────────────────
#  Firebase
# ─────────────────────────────────────────────────────────────────
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Firestore model classes — keep all data classes used with @DocumentId / toObject()
-keepclassmembers class com.mediscan.app.data.model.** {
    <fields>;
    <init>(...);
}
-keep class com.mediscan.app.data.model.** { *; }

# ─────────────────────────────────────────────────────────────────
#  Retrofit + OkHttp
# ─────────────────────────────────────────────────────────────────
-keep class retrofit2.** { *; }
-keepattributes Signature, Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keep interface com.mediscan.app.data.remote.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# ─────────────────────────────────────────────────────────────────
#  Gson
# ─────────────────────────────────────────────────────────────────
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
# Keep all model/request/response data classes used by Gson
-keep class com.mediscan.app.data.remote.dto.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
# Prevent Gson stripping inner classes
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# ─────────────────────────────────────────────────────────────────
#  Room
# ─────────────────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-dontwarn androidx.room.**

# ─────────────────────────────────────────────────────────────────
#  Jetpack Compose
# ─────────────────────────────────────────────────────────────────
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ─────────────────────────────────────────────────────────────────
#  Navigation Compose
# ─────────────────────────────────────────────────────────────────
-keep class androidx.navigation.** { *; }

# ─────────────────────────────────────────────────────────────────
#  Coil
# ─────────────────────────────────────────────────────────────────
-keep class coil.** { *; }
-dontwarn coil.**

# ─────────────────────────────────────────────────────────────────
#  Vico Charts
# ─────────────────────────────────────────────────────────────────
-keep class com.patrykandpatrick.vico.** { *; }
-dontwarn com.patrykandpatrick.vico.**

# ─────────────────────────────────────────────────────────────────
#  Google Maps / Location
# ─────────────────────────────────────────────────────────────────
-keep class com.google.maps.android.** { *; }
-dontwarn com.google.maps.android.**

# ─────────────────────────────────────────────────────────────────
#  Accompanist
# ─────────────────────────────────────────────────────────────────
-keep class com.google.accompanist.** { *; }
-dontwarn com.google.accompanist.**

# ─────────────────────────────────────────────────────────────────
#  WorkManager (bundled, Phase 10 not implemented — keep for future)
# ─────────────────────────────────────────────────────────────────
-keep class androidx.work.** { *; }
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

# ─────────────────────────────────────────────────────────────────
#  Shimmer
# ─────────────────────────────────────────────────────────────────
-keep class com.valentinilk.shimmer.** { *; }
-dontwarn com.valentinilk.shimmer.**

# ─────────────────────────────────────────────────────────────────
#  Security Crypto (EncryptedSharedPreferences)
# ─────────────────────────────────────────────────────────────────
-keep class androidx.security.crypto.** { *; }

# ─────────────────────────────────────────────────────────────────
#  App-specific — keep all ViewModels
# ─────────────────────────────────────────────────────────────────
-keep class com.mediscan.app.ui.viewmodel.** { *; }

# ─────────────────────────────────────────────────────────────────
#  Miscellaneous
# ─────────────────────────────────────────────────────────────────
-dontwarn sun.misc.**
-dontwarn java.lang.invoke.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
