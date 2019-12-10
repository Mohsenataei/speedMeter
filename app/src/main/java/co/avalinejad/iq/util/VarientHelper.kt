package co.avalinejad.iq.util

import co.avalinejad.iq.BuildConfig

fun isIrancell(): Boolean {
    return co.avalinejad.iq.BuildConfig.FLAVOR.equals("Irancell")
}

fun isMCI(): Boolean {
    return co.avalinejad.iq.BuildConfig.FLAVOR.equals("MCI")
}