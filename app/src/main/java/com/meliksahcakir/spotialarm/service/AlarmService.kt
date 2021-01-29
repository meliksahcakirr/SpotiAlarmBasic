package com.meliksahcakir.spotialarm.service

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.net.toUri
import com.meliksahcakir.spotialarm.NOON
import com.meliksahcakir.spotialarm.NOTIFICATION_ID
import com.meliksahcakir.spotialarm.NotificationUtil
import com.meliksahcakir.spotialarm.R
import com.meliksahcakir.spotialarm.broadcast.AlarmReceiver
import com.meliksahcakir.spotialarm.createPendingIntentToActivity
import com.meliksahcakir.spotialarm.data.Alarm
import com.meliksahcakir.spotialarm.preferences.Preferences
import timber.log.Timber
import java.time.format.DateTimeFormatter
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit

class AlarmService : Service(), MediaPlayer.OnPreparedListener {

    companion object {
        private const val VIBRATE_DELAY = 1000L
        private const val VIBRATE_PLAY = 1000L
        private const val VIBRATE_SLEEP = 1000L
        private const val MAX_VOLUME = 100
        private const val VOLUME_SILENT = 0f
        private const val VOLUME_FULL = 1f
        private const val FADE_INTERVAL = 250
    }

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var volume = 0f
    private var timer: Timer? = null

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        val usage = if (Preferences.useDeviceAlarmVolume) {
            AudioAttributes.USAGE_ALARM
        } else {
            AudioAttributes.USAGE_MEDIA
        }
        val attrs = AudioAttributes.Builder().setUsage(usage).build()
        mediaPlayer?.setAudioAttributes(attrs)
        mediaPlayer?.setOnPreparedListener(this)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        timer = Timer(true)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("onStartCommand called")
        val bundle = intent?.getBundleExtra(AlarmReceiver.EXTRA_ALARM)
        val alarm = Alarm.fromBundle(bundle)
        alarm?.let {
            onAlarmGoesOff(it)
            Timber.d("alarm found")
        }
        return START_STICKY
    }

    private fun onAlarmGoesOff(alarm: Alarm) {
        if (alarm.vibrate) {
            val pattern = longArrayOf(VIBRATE_DELAY, VIBRATE_PLAY, VIBRATE_SLEEP)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createWaveform(pattern, 0)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, 0)
            }
        }
        setupMediaPlayerDataAndStart(alarm)
        val time = alarm.alarmTime
        val formatter = DateTimeFormatter.ofPattern("hh:mm")
        val sb = StringBuilder()
        sb.append(time.format(formatter)).append(" ")
        if (alarm.hour >= NOON) {
            sb.append(getString(R.string.pm))
        } else {
            sb.append(getString(R.string.am))
        }
        if (alarm.description.isNotEmpty()) {
            sb.append(" ").append(alarm.description)
        }
        val notificationUtil = NotificationUtil(this@AlarmService)
        val notification = notificationUtil.createNotification(
            this@AlarmService,
            sb.toString(),
            getString(R.string.turn_off_alarm_now),
            createPendingIntentToActivity(alarm),
            createSnoozeIntent(alarm),
            createTurnOffIntent(alarm)
        )
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun startFadeIn() {
        volume = VOLUME_SILENT
        mediaPlayer?.setVolume(volume, volume)
        val fadeDuration = TimeUnit.SECONDS.toMillis(Preferences.fadeInDuration.toLong())
        var maxVolume = VOLUME_FULL
        if (!Preferences.useDeviceAlarmVolume) {
            maxVolume = VOLUME_FULL * Preferences.customVolume / MAX_VOLUME
        }
        val numberOfSteps = fadeDuration / FADE_INTERVAL
        val deltaVolume = maxVolume / numberOfSteps

        if (timer == null) {
            timer = Timer(true)
        }
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                volume += deltaVolume
                mediaPlayer?.setVolume(volume, volume)
                if (volume >= maxVolume) {
                    timer?.cancel()
                    timer?.purge()
                }
            }
        }
        timer?.schedule(timerTask, 0L, FADE_INTERVAL.toLong())
    }

    private fun setupMediaPlayerDataAndStart(alarm: Alarm) {
        var sound: Uri? = null // alarm.musicId.toUri()
        if (sound == null) {
            sound = Preferences.fallbackAudioContentUri?.toUri()
        }
        if (sound == null) {
            sound = RingtoneManager.getActualDefaultRingtoneUri(
                this@AlarmService,
                RingtoneManager.TYPE_ALARM
            )
        }
        if (sound == null) {
            sound = RingtoneManager.getActualDefaultRingtoneUri(
                this@AlarmService,
                RingtoneManager.TYPE_RINGTONE
            )
        }
        try {
            mediaPlayer?.setDataSource(this@AlarmService, sound!!)
            mediaPlayer?.prepareAsync()
        } catch (e: Exception) {
            Timber.e(e)
        }
        mediaPlayer?.isLooping = true
        startFadeIn()
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        timer?.purge()
        timer = null
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        vibrator?.cancel()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun createSnoozeIntent(alarm: Alarm): PendingIntent? {
        if (!alarm.snooze) return null
        val snoozeIntent = Intent(this.applicationContext, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_SNOOZE
            putExtra(AlarmReceiver.EXTRA_ALARM, alarm.toBundle())
        }
        return PendingIntent.getBroadcast(this, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createTurnOffIntent(alarm: Alarm): PendingIntent {
        val intent = Intent(this.applicationContext, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_TURN_OFF
            putExtra(AlarmReceiver.EXTRA_ALARM, alarm.toBundle())
        }
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onPrepared(player: MediaPlayer?) {
        player?.start()
    }
}
