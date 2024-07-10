package xyz.aeonxd.commonslib.config.parser

@Suppress("UNUSED")
sealed class NumberMapper<T> {

    abstract fun map(input: String): T


    data object Double : NumberMapper<kotlin.Double>() {
        override fun map(input: String) = input.toDouble()
    }

    data object Float : NumberMapper<kotlin.Float>() {
        override fun map(input: String) = input.toFloat()
    }

    data object Long : NumberMapper<kotlin.Long>() {
        override fun map(input: String) = input.toLong()
    }

    data object Int : NumberMapper<kotlin.Int>() {
        override fun map(input: String) = input.toInt()
    }

    data object Short : NumberMapper<kotlin.Short>() {
        override fun map(input: String) = input.toShort()
    }

    data object Byte : NumberMapper<kotlin.Byte>() {
        override fun map(input: String) = input.toByte()
    }

}