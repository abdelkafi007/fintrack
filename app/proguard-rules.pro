# Add project specific ProGuard rules here.

# Keep Room entities
-keep class com.fintrack.data.local.entity.** { *; }

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep Compose
-dontwarn androidx.compose.**

# Keep Vico charts
-keep class com.patrykandpatrick.vico.** { *; }

# Keep data classes used with Room
-keepclassmembers class * {
    @androidx.room.* <fields>;
    @androidx.room.* <methods>;
}

# General optimizations
-optimizationpasses 5
-dontusemixedcaseclassnames
-verbose
