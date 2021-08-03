package nl.rijksoverheid.dcbs.verifier.models.data

import nl.rijksoverheid.dcbs.verifier.models.CountryRisk
import nl.rijksoverheid.dcbs.verifier.models.CountryRiskPass

enum class DCCTestType(val value: String) {
    NucleidAcid("LP6464-4"),
    RapidImmune("LP217198-3");

    fun getDisplayName(): String {
        return when (this) {
            NucleidAcid -> "Nucleic acid amplification with probe detection (NAAT / PCR)"
            RapidImmune -> "Rapid immunoassay (Antigen)"
        }
    }

    fun validFor(country: CountryRisk): Int? {
        if (country.getPassType() != CountryRiskPass.NLRules) return null
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