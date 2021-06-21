package nl.rijksoverheid.dcbs.verifier.models.data

enum class TargetedDisease(val value: String) {
    Covid19("840539006");

    fun getDisplayName(): String {
        return when (this) {
            Covid19 -> "Covid19"
        }
    }

    companion object {
        fun fromValue(value: String?): TargetedDisease? {
            return values().firstOrNull { it.value == value }
        }
    }
}