package nl.rijksoverheid.ctr.verifier.models.data

enum class DCCTestType(val value: String) {
    NucleidAcid("LP6464-4"),
    RapidImmune("LP217198-3");

    fun getDisplayName(): String {
        return when (this) {
            NucleidAcid -> "Nucleic acid amplification with probe detection"
            RapidImmune -> "Rapid immunoassay"
        }
    }

    companion object {
        fun fromValue(value: String?): DCCTestType? {
            return values().firstOrNull { it.value == value }
        }
    }
}