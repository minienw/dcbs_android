package nl.rijksoverheid.dcbs.verifier.models.data

enum class CountryCode(val value: String) {
    Andorra("AD"),
    UnitedArabEmirates("AE"),
    Afghanistan("AF"),
    Antigua("AG"),
    Netherlands("NL");

    fun getDisplayName(): String {
        return when (this) {
            Netherlands -> "Netherlands"
            else -> "test test"
        }
    }

    companion object {
        fun fromValue(value: String?): CountryCode? {
            return values().firstOrNull { it.value == value }
        }
    }
}