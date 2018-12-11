package com.example.levi.alarm.view

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.media.MediaPlayer
import android.os.*
import android.support.v7.app.AppCompatActivity
import com.example.levi.alarm.R

class PlayAlarmActivity : AppCompatActivity() {
    private val TAG=PlayAlarmActivity::class.java.simpleName

    private var player: MediaPlayer? = null
    private val vibrator: Vibrator by lazy {
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    private val powerManager:PowerManager by lazy {
        getSystemService(Context.POWER_SERVICE) as PowerManager
    }
    private val keyguardManager:KeyguardManager by lazy {
        getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_alarm)
        playAndVibrate()
        brightScreen()
    }

    @SuppressLint("NewApi")
    private fun playAndVibrate(){
        player = MediaPlayer.create(this@PlayAlarmActivity, R.raw.thereforyou)
        player?.start()
        when (Build.VERSION.SDK_INT) {
            in 26..Int.MAX_VALUE -> {
                vibrator.vibrate(VibrationEffect.createOneShot(2000, 100))
            }
            else -> {
                vibrator.vibrate(longArrayOf(100, 1000, 100, 1000), -1)
            }
        }
    }

    private fun brightScreen(){
        val wl=powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_BRIGHT_WAKE_LOCK,TAG)
        wl.acquire(1000)
        val kl=keyguardManager.newKeyguardLock(TAG)
        kl.disableKeyguard()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.stop()
        player?.release()
        player = null
    }
}
