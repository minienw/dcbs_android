package nl.rijksoverheid.dcbs.verifier.models.data

enum class ValueSetType(val value: String) {

    TestResult("covid-19-lab-result"),
    CountryCode("country-2-codes"),
    TestManufacturer("covid-19-lab-test-manufacturer-and-name"),
    TestType("covid-19-lab-test-type"),
    TargetedAgent("disease-agent-targeted"),
    VaccineType("sct-vaccines-covid-19"),
    VaccineAuthHolder("vaccines-covid-19-auth-holders"),
    VaccineProduct("vaccines-covid-19-names");

    companion object {
        fun fromValue(value: String?): ValueSetType? {
            return values().firstOrNull { it.value == value }
        }
    }
}