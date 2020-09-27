package com.example.clockapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clockapp.db.repositories.ClockRepository

class ClockViewModelFactory(
    val repository: ClockRepository
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ClockViewModel(repository) as T
    }

}