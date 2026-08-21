package com.silvera.basikekran.data

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

/**
 * Gerçek ekran çözünürlüğü değiştirme yöneticisi.
 *
 * Android güvenlik modeli nedeniyle bir uygulama, kullanıcı izni olmadan
 * SİSTEM çapında ekran çözünürlüğünü değiştiremez. Bunu yapabilmenin
 * Android'in izin verdiği gerçek yolları şunlardır:
 *
 * 1) ROOT erişimi ile "wm size" / "wm density" shell komutları (cihaz root'lu ise)
 * 2) WRITE_SECURE_SETTINGS izni ADB üzerinden bir kere verilirse (root gerekmez,
 *    ama PC üzerinden tek seferlik adb komutu gerekir)
 * 3) Hiçbiri yoksa: sistem çözünürlüğü değiştirilemez, bunun yerine kayan
 *    (overlay) siyah çubuklarla görsel "basık ekran" simülasyonu sunulur.
 */
object DisplayResolutionManager {

    enum class Capability {
        ROOT,               // su erişimi var, gerçek wm size uygulanabilir
        SECURE_SETTINGS,    // WRITE_SECURE_SETTINGS izni verilmiş
        OVERLAY_ONLY,       // sadece görsel overlay simülasyonu mümkün
        NONE                // hiçbir yöntem kullanılamıyor (izin reddedildi)
    }

    /** Cihazda root (su) erişimi olup olmadığını kontrol eder. */
    fun hasRootAccess(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "id"))
            val output = BufferedReader(InputStreamReader(process.inputStream)).readText()
            process.waitFor()
            output.contains("uid=0")
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Root üzerinden gerçek sistem çözünürlüğünü değiştirir.
     * Yalnızca [hasRootAccess] true dönerse çağrılmalıdır.
     */
    fun applyResolutionRoot(width: Int, height: Int): Result<Unit> {
        return try {
            val process = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(process.outputStream)
            os.writeBytes("wm size ${width}x${height}\n")
            os.writeBytes("exit\n")
            os.flush()
            val exit = process.waitFor()
            if (exit == 0) Result.success(Unit)
            else Result.failure(Exception("wm size komutu başarısız oldu (kod: $exit)"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Root üzerinden sistem çözünürlüğünü varsayılana sıfırlar. */
    fun resetResolutionRoot(): Result<Unit> {
        return try {
            val process = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(process.outputStream)
            os.writeBytes("wm size reset\n")
            os.writeBytes("exit\n")
            os.flush()
            val exit = process.waitFor()
            if (exit == 0) Result.success(Unit)
            else Result.failure(Exception("Sıfırlama başarısız oldu (kod: $exit)"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Mevcut cihaz yeteneğini belirler (UI'da doğru yöntemi göstermek için). */
    fun detectCapability(hasOverlayPermission: Boolean): Capability {
        return when {
            hasRootAccess() -> Capability.ROOT
            hasOverlayPermission -> Capability.OVERLAY_ONLY
            else -> Capability.NONE
        }
    }
}
