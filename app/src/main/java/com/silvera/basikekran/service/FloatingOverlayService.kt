package com.silvera.basikekran.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.FrameLayout

/**
 * Root veya WRITE_SECURE_SETTINGS izni olmayan cihazlarda, gerçek sistem
 * çözünürlüğünü değiştiremediğimiz için üstte siyah çubuklu bir kayan
 * pencere (overlay) göstererek görsel "basık ekran" hissi simüle eder.
 *
 * Not: Bu, oyunun gerçek render çözünürlüğünü değiştirmez; sadece
 * ekranın üst ve alt kısımlarını görsel olarak kırparak oyuncuya
 * daha basık bir görüntü alanı hissi verir.
 */
class FloatingOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var topBar: FrameLayout? = null
    private var bottomBar: FrameLayout? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val barHeightPercent = intent?.getFloatExtra(EXTRA_BAR_HEIGHT_PERCENT, 0f) ?: 0f
        showOverlay(barHeightPercent)
        return START_STICKY
    }

    private fun showOverlay(barHeightPercent: Float) {
        removeOverlay()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val metrics = resources.displayMetrics
        val barHeightPx = (metrics.heightPixels * barHeightPercent).toInt().coerceAtLeast(0)

        if (barHeightPx <= 0) return

        val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE

        val commonFlags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN

        topBar = FrameLayout(this).apply { setBackgroundColor(Color.BLACK) }
        bottomBar = FrameLayout(this).apply { setBackgroundColor(Color.BLACK) }

        val topParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            barHeightPx,
            overlayType,
            commonFlags,
            PixelFormat.OPAQUE
        ).apply { gravity = Gravity.TOP }

        val bottomParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            barHeightPx,
            overlayType,
            commonFlags,
            PixelFormat.OPAQUE
        ).apply { gravity = Gravity.BOTTOM }

        try {
            windowManager?.addView(topBar, topParams)
            windowManager?.addView(bottomBar, bottomParams)
        } catch (e: Exception) {
            // İzin reddedilmiş veya cihaz desteklemiyor olabilir; sessizce yut,
            // ViewModel tarafında kullanıcıya zaten bilgi mesajı gösterildi.
        }
    }

    private fun removeOverlay() {
        try {
            topBar?.let { windowManager?.removeView(it) }
            bottomBar?.let { windowManager?.removeView(it) }
        } catch (e: Exception) {
            // view zaten eklenmemiş olabilir
        }
        topBar = null
        bottomBar = null
    }

    override fun onDestroy() {
        removeOverlay()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_BAR_HEIGHT_PERCENT = "bar_height_percent"

        fun start(context: Context, barHeightPercent: Float) {
            val intent = Intent(context, FloatingOverlayService::class.java)
                .putExtra(EXTRA_BAR_HEIGHT_PERCENT, barHeightPercent)
            context.startService(intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, FloatingOverlayService::class.java))
        }
    }
}
