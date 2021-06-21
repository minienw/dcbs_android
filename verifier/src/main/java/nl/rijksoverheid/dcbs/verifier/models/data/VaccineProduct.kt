package nl.rijksoverheid.dcbs.verifier.models.data

enum class VaccineProduct(val value: String) {
    Comirnaty("EU/1/20/1528"),
    Moderna("EU/1/20/1507"),
    Vaxzevria("EU/1/21/1529"),
    Janssen("EU/1/20/1525"),
    Cvncov("CVnCoV"),
    Sputnikv("Sputnik-V"),
    Convidecia("Convidecia"),
    ApiVacCorona("EpiVacCorona"),
    BbibpCorv("BBIBP-CorV"),
    InActivatedVeroCell("Inactivated-SARS-CoV-2-Vero-Cell"),
    CoronaVac("CoronaVac"),
    Covaxin("Covaxin");

    fun getDisplayName(): String {
        return when (this) {
            Comirnaty -> "Comirnaty"
            Moderna -> "Moderna"
            Vaxzevria -> "Vaxzevria"
            Janssen -> "Janssen"
            Cvncov -> "CVnCoV"
            Sputnikv -> "Sputnik-V"
            Convidecia -> "Convidecia"
            ApiVacCorona -> "EpiVacCorona"
            BbibpCorv -> "BBIBP-CorV"
            InActivatedVeroCell -> "Inactivated SARS-CoV-2 (Vero Cell)"
            CoronaVac -> "CoronaVac"
            Covaxin -> "Covaxin (BBV152 A, B, C)"
        }
    }

    companion object {
        fun fromValue(value: String?): VaccineProduct? {
            return values().firstOrNull { it.value == value }
        }
    }
}