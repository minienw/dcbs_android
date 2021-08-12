package nl.rijksoverheid.dcbs.verifier.models.data

enum class VaccineHolder(val value: String) {
    AstraZeneca("ORG-100001699"),
    BioNTech("ORG-100030215"),
    Janssen("ORG-100001417"),
    Moderna("ORG-100031184"),
    CureVac("ORG-100006270"),
    Cansino("ORG-100013793"),
    ChinaSinopharm("ORG-100020693"),
    EuropeSinopharm("ORG-100010771"),
    ZhijunSinopharm("ORG-100024420"),
    Novavax("ORG-100032020"),
    Gamaleya("Gamaleya-Research-Institute"),
    Vector("Vector-Institute"),
    SinoVac("Sinovac-Biotech"),
    Bharat("Bharat-Biotech"),
    SerumInstituteIndia("ORG-100001981");

    fun getDisplayName(): String {
        return when (this) {
            AstraZeneca -> "AstraZeneca AB"
            BioNTech -> "Biontech Manufacturing GmbH"
            Janssen -> "Janssen-Cilag International"
            Moderna -> "Moderna Biotech Spain S.L."
            CureVac -> "Curevac AG"
            Cansino -> "CanSino Biologics"
            ChinaSinopharm -> "China Sinopharm International Corp. - Beijing locatie"
            EuropeSinopharm -> "Sinopharm Weiqida Europe Pharmaceutical s.r.o. - Praag locatie"
            ZhijunSinopharm -> "Sinopharm Zhijun (Shenzhen) Pharmaceutical Co. Ltd. - Shenzhen locatie"
            Novavax -> "Novavax CZ AS"
            Gamaleya -> "Gamaleya Research Institute"
            Vector -> "Vector Institute"
            SinoVac -> "Sinovac Biotech"
            Bharat -> "Bharat Biotech"
            SerumInstituteIndia -> "Serum Institute Of India Private Limited"
        }
    }

    companion object {
        fun fromValue(value: String?): VaccineHolder? {
            return values().firstOrNull { it.value == value }
        }
    }
}