package xyz.aeonxd.commonslib.config.parser

sealed interface NumberMapper<T> {

    val mapper: (String) -> T

    data object Double : NumberMapper<kotlin.Double> {
        override val mapper: (String) -> kotlin.Double = {
            it.toDouble()
        }
    }

    data object Float : NumberMapper<kotlin.Float> {
        override val mapper: (String) -> kotlin.Float = {
            it.toFloat()
        }
    }

    data object Long : NumberMapper<kotlin.Long> {
        override val mapper: (String) -> kotlin.Long = {
            it.toLong()
        }
    }

    data object Int : NumberMapper<kotlin.Int> {
        override val mapper: (String) -> kotlin.Int = {
            it.toInt()
        }
    }

    data object Short : NumberMapper<kotlin.Short> {
        override val mapper: (String) -> kotlin.Short = {
            it.toShort()
        }
    }

    data object Byte : NumberMapper<kotlin.Byte> {
        override val mapper: (String) -> kotlin.Byte = {
            it.toByte()
        }
    }

}