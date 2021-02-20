package com.meliksahcakir.spotialarm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.meliksahcakir.spotialarm.databinding.ActivityMainBinding
import com.meliksahcakir.spotialarm.main.MainViewModel
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
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.refreshData()
    }
}
