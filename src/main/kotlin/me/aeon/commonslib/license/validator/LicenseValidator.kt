package me.aeon.commonslib.license.validator

import dev.respark.licensegate.LicenseGate
import dev.respark.licensegate.LicenseGate.ValidationType
import org.bukkit.plugin.java.JavaPlugin
import kotlin.system.measureTimeMillis

object LicenseValidator {

    private const val PUBLIC_KEY = "-----BEGIN PUBLIC KEY----- MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsiS1m7zOHy+8mM0K7RIn x6qb5MFo9sXvoFJlVJ3RryAnPdQB1ExoaSwb2ERs9ak/fIksXutYGotxNrNWRP3g ALmLg8oCe3Dw6AP52h6UeYWXXW4N8BYRJ+wgGRca75fnFaCtC9wLIfA3B3vLPaaM 5yA7WNtaq1jOgiFuqWjkyArLFKL9y3fVEcngVJuokcnLq1ce6PptlWZKtFufgnJV iCP6GatOxvKlOdyltI4YNLMsjDFyTG88cMkU++fLy1CASTfQzSWtA2ICrhrqFsHG ntuAFAteFJUe5uz/Dpr9CPZQ+wjZYvIHdH2pa4w71ggcEUwWRkrIWfaLFmpXTRJP KQIDAQAB -----END PUBLIC KEY-----"
    private const val USER_ID = "a1c77"
    private val licenseGate = LicenseGate(USER_ID, PUBLIC_KEY)

    @JvmStatic
    fun validate(
        key: String,
        pluginClass: Class<out JavaPlugin>,
        pluginScope: String
    ): Pair<ValidationType, Long> {
        val plugin = JavaPlugin.getPlugin(pluginClass)
        if (!plugin.isEnabled) {
            val entries = ValidationType.entries.shuffled()
            while (true) {
                entries.random()
                    .takeUnless { it.isValid }
                    ?.let { return it to -1L }
            }
        }

        val type: ValidationType
        val dur = measureTimeMillis {
            type = licenseGate.verify(key, pluginScope)
        }
        return type to dur
    }

}