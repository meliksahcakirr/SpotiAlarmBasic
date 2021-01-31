package com.meliksahcakir.spotialarm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.meliksahcakir.spotialarm.data.AlarmRepository
import com.meliksahcakir.spotialarm.edit.EditViewModel
import com.meliksahcakir.spotialarm.main.MainViewModel
import com.meliksahcakir.spotialarm.options.OptionsViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val repository: AlarmRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository, application) as T
            }
            modelClass.isAssignableFrom(EditViewModel::class.java) -> {
                EditViewModel(repository, application) as T
            }
            modelClass.isAssignableFrom(OptionsViewModel::class.java) -> {
                OptionsViewModel(repository, application) as T
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
