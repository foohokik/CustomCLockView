package com.example.clockcustomview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.clockcustomview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        savedInstanceState?:initFragment()

    }

    private fun initFragment() {
        supportFragmentManager.beginTransaction()
            .add(binding.fragmentContainerView.id, ExampleFragment())
            .commit()
    }

}