package ripple.trackingmaster.devicetrackapp.core.utils

object HexUtils {
    // Convert Byte Array to Hex String (e.g., [0xAA, 0xBB] -> "aabb")
    fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // Convert Hex String to Byte Array (e.g., "aabb" -> [0xAA, 0xBB])
    fun hexToBytes(hex: String): ByteArray {
        val cleanHex = hex.replace(" ", "").lowercase()
        check(cleanHex.length % 2 == 0) { "Hex string must have an even length" }
        return cleanHex.chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }

    // Convert Hex to Decimal String (e.g., "0a" -> "10")
    fun hexToDecimalStr(hex: String): String {
        return try {
            hex.toLong(16).toString()
        } catch (e: Exception) {
            "0"
        }
    }
}