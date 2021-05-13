package com.meliksahcakir.spotialarm.feedback

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.meliksahcakir.androidutils.Event
import com.meliksahcakir.spotialarm.R
import com.meliksahcakir.spotialarm.firebase.Feedback
import com.meliksahcakir.spotialarm.firebase.FireStoreHelper
import com.meliksahcakir.spotialarm.firebase.diffInSeconds
import kotlinx.coroutines.launch

class FeedbackViewModel(private val app: Application) : AndroidViewModel(app) {

    companion object {
        private const val FB_INTERVAL = 10L
    }

    private val _warningEvent = MutableLiveData<Event<String>>()
    val warningEvent: LiveData<Event<String>> get() = _warningEvent

    private var fbTimestamp: Timestamp? = null

    fun onFeedbackSendButtonClicked(name: String, email: String, message: String) {
        if (message.isBlank()) {
            _warningEvent.value = Event(app.getString(R.string.empty_message))
            return
        }
        val uid = Firebase.auth.currentUser?.uid ?: ""
        val feedback = Feedback(uid, name, email, message)
        if (fbTimestamp == null || feedback.timestamp.diffInSeconds(fbTimestamp!!) >= FB_INTERVAL) {
            fbTimestamp = feedback.timestamp
            viewModelScope.launch {
                FireStoreHelper.sendFeedback(feedback)
                _warningEvent.value = Event(app.getString(R.string.feedback_received))
            }
        } else {
            _warningEvent.value = Event(app.getString(R.string.wait_couple_seconds))
        }
    }
}
