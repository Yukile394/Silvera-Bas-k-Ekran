# Add project specific ProGuard rules here.

# Keep Compose classes
-keep class androidx.compose.** { *; }
-keep class kotlin.** { *; }

# Keep data classes
-keep class com.silvera.basikekran.data.** { *; }

# Keep ViewModel
-keep class com.silvera.basikekran.data.MainViewModel { *; }

-dontwarn org.jetbrains.annotations.**
