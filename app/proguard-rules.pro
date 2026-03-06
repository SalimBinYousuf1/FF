# Add project specific ProGuard rules here.
-keep class com.vibeplayer.data.model.** { *; }
-keep class androidx.media3.** { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Coil
-keep class coil.** { *; }
