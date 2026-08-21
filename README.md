# Silvera Basık Ekran

Mobil oyunlarda ekran oranı yönetimi için Kotlin + Jetpack Compose ile geliştirilmiş Android uygulaması.

## Özellikler

- Standoff 2 ve PUBG Mobile desteği (modüler yapı, yeni oyun eklenebilir)
- Hızlı ekran oranı seçimi: 16:9, 4:3, 21:9, Dikey, Yatay, Özel
- Oyun başına özel profil kaydetme (SharedPreferences)
- Neon mor / cam (glassmorphism) temalı, animasyonlu arayüz
- Alt navigasyon: Ana Sayfa, Oyunlar, Ayarlar, Hakkında
- Kapsamlı hata yönetimi (geçersiz değer, kurulu olmayan oyun, desteklenmeyen cihaz vb.)

## Geliştirme

Proje Android Studio, GitHub Codespaces veya GitHub Actions üzerinden derlenebilir.

```
./gradlew assembleDebug
```

## GitHub Actions ile Otomatik APK Derleme

`.github/workflows/build.yml` dosyası her `push` işleminde veya elle tetiklendiğinde
(`workflow_dispatch`) otomatik olarak APK üretir ve **Silvera-Basik-Ekran.apk**
adıyla artifact olarak yükler.

Workflow adımları:
1. Repository checkout
2. JDK 17 kurulumu
3. Android SDK kurulumu
4. Gradle kurulumu + cache
5. Gradle Wrapper doğrulama/oluşturma
6. `assembleDebug` ile APK derleme
7. APK'nın artifact olarak yüklenmesi

## Teknik Detaylar

- **Dil:** Kotlin
- **UI:** Jetpack Compose + Material3
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34
- **Mimari:** MVVM (ViewModel + StateFlow)
- **Yerel Depolama:** SharedPreferences

## Android Kısıtlaması Hakkında

Bu uygulama, Android'in izin verdiği resmi mekanizmaları (uygulama başlatma, ayar/profil
yönetimi) kullanır. Başka uygulamaların ekran çözünürlüğünü zorla değiştirmez, kod
enjeksiyonu yapmaz veya oyun dosyalarını değiştirmez.
