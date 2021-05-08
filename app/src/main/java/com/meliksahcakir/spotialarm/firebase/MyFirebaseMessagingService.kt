package com.meliksahcakir.spotialarm.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.meliksahcakir.spotialarm.NotificationUtil
import com.meliksahcakir.spotialarm.R
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("FROM: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            Timber.d("Data payload: ${remoteMessage.data}")
        }

        remoteMessage.notification?.let {
            val notificationUtil = NotificationUtil(applicationContext)
            val title = it.title ?: applicationContext.getString(R.string.app_name)
            notificationUtil.sendFcmNotification(title, it.body ?: "")
        }
    }

    override fun onNewToken(token: String) {
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        Timber.d("sendRegistrationTokenToServer($token)")
    }
}
