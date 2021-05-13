package com.meliksahcakir.spotialarm.rate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.meliksahcakir.androidutils.Event
import com.meliksahcakir.spotialarm.firebase.FireStoreHelper
import com.meliksahcakir.spotialarm.firebase.Rating
import kotlinx.coroutines.launch

enum class ReviewState {
    RATING_SELECTION,
    GOOGLE_PLAY_REVIEW_REQUEST,
    COMPLETED
}

class RatingViewModel(private val app: Application) : AndroidViewModel(app) {

    private val _currentState = MutableLiveData(ReviewState.RATING_SELECTION)
    val currentState: LiveData<ReviewState> = _currentState

    private val _googlePlayReviewEvent = MutableLiveData<Event<Unit>>()
    val googlePlayReviewEvent: LiveData<Event<Unit>> = _googlePlayReviewEvent

    fun onSubmitButtonClicked(rate: Int) {
        if (_currentState.value == ReviewState.RATING_SELECTION) {
            val uid = Firebase.auth.currentUser?.uid ?: ""
            val rating = Rating(uid, rate)
            viewModelScope.launch {
                _currentState.value = ReviewState.GOOGLE_PLAY_REVIEW_REQUEST
                FireStoreHelper.sendRating(rating)
            }
        } else if (_currentState.value == ReviewState.GOOGLE_PLAY_REVIEW_REQUEST) {
            _googlePlayReviewEvent.value = Event(Unit)
            _currentState.value = ReviewState.COMPLETED
        }
    }
}
