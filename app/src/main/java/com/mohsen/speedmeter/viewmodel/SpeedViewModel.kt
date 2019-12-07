package com.mohsen.speedmeter.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Created by satana on 11/20/18
 */
class SpeedViewModel: ViewModel() {
    var speed : MutableLiveData<Int> = MutableLiveData()

    fun getSpeed(): LiveData<Int> {
        return speed
    }
}