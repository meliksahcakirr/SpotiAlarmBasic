package com.meliksahcakir.spotialarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.meliksahcakir.spotialarm.data.AlarmRepository
import com.meliksahcakir.spotialarm.edit.EditViewModel
import com.meliksahcakir.spotialarm.main.MainViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val repository: AlarmRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(EditViewModel::class.java) -> {
                EditViewModel(repository) as T
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
