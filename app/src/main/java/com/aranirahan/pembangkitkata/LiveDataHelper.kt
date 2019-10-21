package com.aranirahan.pembangkitkata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class LiveDataHelper private constructor() {
    private val _percent = MediatorLiveData<Individual>()
    fun updateIndividual(percentage: Individual) {
        _percent.postValue(percentage)
    }

    fun observeIndividual(): LiveData<Individual> {
        return _percent
    }

    private val _mutation = MediatorLiveData<String>()
    fun updateMutation(mutation: String) {
        _mutation.postValue(mutation)
    }

    fun observeMutation(): LiveData<String> {
        return _mutation
    }

    private val _generation = MediatorLiveData<Int>()
    fun updateGeneration(generation: Int) {
        _generation.postValue(generation)
    }

    fun observeGeneration(): LiveData<Int> {
        return _generation
    }

    companion object {
        private var liveDataHelper: LiveDataHelper? = null
        val instance: LiveDataHelper
            @Synchronized get() {
                if (liveDataHelper == null) liveDataHelper = LiveDataHelper()
                return liveDataHelper as LiveDataHelper
            }
    }
}