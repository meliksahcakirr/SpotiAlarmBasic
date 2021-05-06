package com.meliksahcakir.spotialarm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.meliksahcakir.spotialarm.databinding.ActivityMainBinding
import com.meliksahcakir.spotialarm.firebase.FireStoreHelper
import com.meliksahcakir.spotialarm.main.MainViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var host: NavHostFragment

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        host = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        if (Firebase.auth.currentUser == null) {
            Firebase.auth.signInAnonymously().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    setupFirebaseTools()
                }
            }
        } else {
            setupFirebaseTools()
        }
        viewModel.favoriteTracks.observe(this) { list ->
            val favList = FireStoreHelper.statistics?.favoriteTracks ?: emptyList()
            val newList = list.filter { !favList.contains(it.id) }.map { it.id }
            FireStoreHelper.statistics?.favoriteTracks?.addAll(newList)
            FireStoreHelper.updateRemoteStatistics()
        }
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.refreshData()
    }

    private fun setupFirebaseTools() {
        Firebase.auth.currentUser?.let { user ->
            FirebaseCrashlytics.getInstance().setUserId(user.uid)
            FireStoreHelper.uid = user.uid
            lifecycleScope.launch {
                FireStoreHelper.fetchStatistics()
                FireStoreHelper.statistics?.updateWithPreferences()

                viewModel.favoriteTracks.value?.let { list ->
                    val favList = FireStoreHelper.statistics?.favoriteTracks ?: emptyList()
                    val newList = list.filter { !favList.contains(it.id) }.map { it.id }
                    FireStoreHelper.statistics?.favoriteTracks?.addAll(newList)
                }
                FireStoreHelper.updateRemoteStatistics()
            }
        }
    }
}
