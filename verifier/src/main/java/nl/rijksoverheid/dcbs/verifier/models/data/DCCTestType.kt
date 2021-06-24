package nl.rijksoverheid.dcbs.verifier.models.data

enum class DCCTestType(val value: String) {
    NucleidAcid("LP6464-4"),
    RapidImmune("LP217198-3");

    fun getDisplayName(): String {
        return when (this) {
            NucleidAcid -> "Nucleic acid amplification with probe detection"
            RapidImmune -> "Rapid immunoassay"
        }
    }

    fun validFor(country: String): Int? {
        if (country != "nl") return null
        return when (this) {
            NucleidAcid -> 72
            RapidImmune -> 48
        }
    }

    companion object {
        fun fromValue(value: String?): DCCTestType? {
            return values().firstOrNull { it.value == value }
        }
    }
}