package com.example.clockapp.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.clockapp.R
import com.example.clockapp.db.ClockDatabase
import com.example.clockapp.db.repositories.ClockRepository
import com.example.clockapp.ui.viewmodel.ClockViewModel
import com.example.clockapp.ui.viewmodel.ClockViewModelFactory
import com.example.clockapp.util.Utilities
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_viewpager.*

class MainActivity : AppCompatActivity() {
    lateinit var viewModel: ClockViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = ClockDatabase(this)
        val repository =
            ClockRepository(database)
        val clockViewModelFactory = ClockViewModelFactory(repository)
        viewModel = ViewModelProvider(this, clockViewModelFactory).get(ClockViewModel::class.java)

    }

    override fun onBackPressed() {
        if(Utilities.isViewPagerFragment){
            if(vpFragment.currentItem == 0){
                super.onBackPressed()
            } else {
                vpFragment.currentItem = 0
            }
        } else {
            super.onBackPressed()
        }
    }

}