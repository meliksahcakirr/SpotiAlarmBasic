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
import com.meliksahcakir.spotialarm.NOON
import com.meliksahcakir.spotialarm.NOTIFICATION_ID
import com.meliksahcakir.spotialarm.NotificationUtil
import com.meliksahcakir.spotialarm.R
import com.meliksahcakir.spotialarm.broadcast.AlarmReceiver
import com.meliksahcakir.spotialarm.createPendingIntentToActivity
import com.meliksahcakir.spotialarm.data.Alarm
import timber.log.Timber
import java.time.format.DateTimeFormatter

class AlarmService : Service() {

    companion object {
        private const val VIBRATE_DELAY = 1000L
        private const val VIBRATE_PLAY = 1000L
        private const val VIBRATE_SLEEP = 1000L
    }

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        val attrs = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
        mediaPlayer?.setAudioAttributes(attrs)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
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

        var sound: Uri? = null // alarm.musicId.toUri()
        if (sound == null) {
            sound = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM)
        }
        if (sound == null) {
            sound = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE)
        }
        try {
            mediaPlayer?.setDataSource(this, sound!!)
            mediaPlayer?.prepare()
        } catch (e: Exception) {
            Timber.e(e)
        }
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
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

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
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
}
