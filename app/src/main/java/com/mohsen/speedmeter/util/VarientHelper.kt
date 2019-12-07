package com.mohsen.speedmeter.util

import com.mohsen.speedmeter.BuildConfig

fun isIrancell(): Boolean {
    return BuildConfig.FLAVOR.equals("Irancell")
}

fun isMCI(): Boolean {
    return BuildConfig.FLAVOR.equals("MCI")
}