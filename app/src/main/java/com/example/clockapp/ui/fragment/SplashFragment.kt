package com.example.clockapp.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.clockapp.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class SplashFragment: Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
            delay(1000L)
            findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
        }
    }
}