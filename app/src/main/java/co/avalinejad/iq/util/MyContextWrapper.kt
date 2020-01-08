package co.avalinejad.iq.util

class MyContextWrapper(base: Context) : ContextWrapper(base) {
    companion object {
        @TargetApi(Build.VERSION_CODES.N)
        fun wrap(contextt: Context, newLocale: Locale): ContextWrapper {
            var context = contextt
            val res = context.resources
            val configuration = res.configuration
            when {
                VersionUtils.isAfter24 -> {
                    configuration.setLocale(newLocale)
                    val localeList = LocaleList(newLocale)
                    LocaleList.setDefault(localeList)
                    configuration.locales = localeList
                    context = context.createConfigurationContext(configuration)
                }
                VersionUtils.isAfter17 -> {
                    configuration.setLocale(newLocale)
                    context = context.createConfigurationContext(configuration)
                }
                else -> {
                    @Suppress("DEPRECATION")
                    configuration.locale = newLocale
                    @Suppress("DEPRECATION")
                    res.updateConfiguration(configuration, res.displayMetrics)
                }
            }
            return ContextWrapper(context)
        }
    }
}