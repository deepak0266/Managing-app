# Add project specific ProGuard rules here.

# Retrofit
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# SQLCipher
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }

# Supabase Ktor
-keep class io.ktor.** { *; }
-keep class io.github.jan.supabase.** { *; }
-keepattributes *Annotation*

# Models (Keep data classes used in API requests so Gson/Serialization doesn't break)
-keep class com.personal.lifeos.ai.model.** { *; }
-keep class com.personal.lifeos.core.data.local.entity.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Hilt
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
