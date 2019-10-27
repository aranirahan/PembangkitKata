package com.aranirahan.pembangkitkata.helper

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class LiveDataHelper private constructor() {

    private val _bestIndividual = MediatorLiveData<String>()
    fun updateBestIndiviual(bestIndividual: String) {
        _bestIndividual.postValue(bestIndividual)
    }

    fun observeBestIndiviual(): LiveData<String> {
        return _bestIndividual
    }

    private val _populate = MediatorLiveData<String>()
    fun updatePopulate(populate: String) {
        _populate.postValue(populate)
    }

    fun observePopulate(): LiveData<String> {
        return _populate
    }

    private val _rws = MediatorLiveData<String>()
    fun updateRws(rws: String) {
        _rws.postValue(rws)
    }

    fun observeRws(): LiveData<String> {
        return _rws
    }

    private val _crossOver = MediatorLiveData<String>()
    fun updateCrossOver(crossOver: String) {
        _crossOver.postValue(crossOver)
    }

    fun observeCrossOver(): LiveData<String> {
        return _crossOver
    }

    private val _mutation = MediatorLiveData<String>()
    fun updateMutation(mutation: String) {
        _mutation.postValue(mutation)
    }

    fun observeMutation(): LiveData<String> {
        return _mutation
    }

    private val _elitisme = MediatorLiveData<String>()
    fun updateElitisme(elitisme: String) {
        _elitisme.postValue(elitisme)
    }

    fun observeElitisme(): LiveData<String> {
        return _elitisme
    }

    private val _generation = MediatorLiveData<Int>()
    fun updateGeneration(generation: Int) {
        _generation.postValue(generation)
    }

    fun observeGeneration(): LiveData<Int> = _generation

    private val _fitnessRef = MediatorLiveData<Double>()
    fun updateFitnessRef(fitnessRef: Double) {
        _fitnessRef.postValue(fitnessRef)
    }

    fun observeFitnessRef(): LiveData<Double> = _fitnessRef

    companion object {
        private var liveDataHelper: LiveDataHelper? = null
        val instance: LiveDataHelper
            @Synchronized get() {
                if (liveDataHelper == null) liveDataHelper =
                    LiveDataHelper()
                return liveDataHelper as LiveDataHelper
            }
    }
}