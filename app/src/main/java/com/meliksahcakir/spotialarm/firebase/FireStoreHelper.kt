package com.meliksahcakir.spotialarm.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.meliksahcakir.androidutils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object FireStoreHelper {

    private var userRef: DocumentReference? = null
    var uid = ""
        set(value) {
            field = value
            userRef = if (value == "") {
                null
            } else {
                Firebase.firestore.collection("users").document(uid)
            }
        }

    var statistics: Statistics? = null

    suspend fun fetchStatistics(): Result<Statistics> = withContext(Dispatchers.IO) {
        try {
            val snapshot = userRef?.get()?.await()
            val statistics = snapshot?.toObject(Statistics::class.java) ?: Statistics()
            this@FireStoreHelper.statistics = statistics
            Result.Success(statistics)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    fun updateRemoteStatistics(statistics: Statistics? = this.statistics) {
        if (statistics != null) {
            statistics.lastActiveAt = Timestamp.now()
            userRef?.set(statistics)
        }
    }
}
