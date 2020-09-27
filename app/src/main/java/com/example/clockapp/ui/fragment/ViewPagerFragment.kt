package com.example.clockapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.clockapp.R
import com.example.clockapp.adapter.ViewPagerAdapter
import com.example.clockapp.ui.activity.MainActivity
import com.example.clockapp.util.Utilities
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_viewpager.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ViewPagerFragment: Fragment(R.layout.fragment_viewpager) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utilities.isViewPagerFragment = true
        setUpViewPager()
    }

    private fun setUpViewPager() {
        val viewPagerAdapter = ViewPagerAdapter(this)

        vpFragment.adapter = viewPagerAdapter

        TabLayoutMediator(tlClock, vpFragment){tab, position ->
            when(position){
                0 -> tab.text = "Alarm"
                1 -> tab.text = "Stopwatch"
                2 -> tab.text = "Timer"
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Utilities.isViewPagerFragment = false
    }
}