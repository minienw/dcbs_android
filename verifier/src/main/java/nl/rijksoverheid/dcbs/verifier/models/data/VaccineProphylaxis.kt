package nl.rijksoverheid.dcbs.verifier.models.data

enum class VaccineProphylaxis(val value: String) {
    MRNA("1119349007"),
    Antigen("1119305005"),
    Covid19Vaccines("J07BX03");

    fun getDisplayName(): String {
        return when (this) {
            MRNA -> "mRNA vaccine"
            Antigen -> "Antigen vaccine"
            Covid19Vaccines -> "covid-19 vaccines"
        }
    }

    companion object {
        fun fromValue(value: String?): VaccineProphylaxis? {
            return values().firstOrNull { it.value == value }
        }
    }
}