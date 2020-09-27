package com.example.clockapp.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.clockapp.ui.fragment.AlarmFragment
import com.example.clockapp.ui.fragment.CountDownFragment
import com.example.clockapp.ui.fragment.StopwatchFragment

class ViewPagerAdapter(
    fragment: Fragment
): FragmentStateAdapter(fragment) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> AlarmFragment()
            1 -> StopwatchFragment()
            else -> CountDownFragment()
        }
    }
}