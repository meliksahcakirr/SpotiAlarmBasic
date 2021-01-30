package com.meliksahcakir.spotialarm

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.meliksahcakir.spotialarm.databinding.ActivityMainBinding
import com.meliksahcakir.spotialarm.main.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var host: NavHostFragment

    private val viewModel: MainViewModel by viewModels {
        ServiceLocator.provideViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        host = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.refreshData()
    }
}
