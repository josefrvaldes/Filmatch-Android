package es.josevaldes.core.utils

@OptIn(ExperimentalStdlibApi::class)
fun String.md5(): String {
    val md = java.security.MessageDigest.getInstance("MD5")
    val digest = md.digest(toByteArray())
    return digest.toHexString()
}