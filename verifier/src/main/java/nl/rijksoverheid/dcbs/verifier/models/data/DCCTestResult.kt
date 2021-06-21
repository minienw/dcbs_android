package nl.rijksoverheid.dcbs.verifier.models.data

enum class DCCTestResult(val value: String) {
    NotDetected("260415000"),
    Detected("260373001");

    fun getDisplayName(): String {
        return when (this) {
            NotDetected -> "Negatief"
            Detected -> "Positief"
        }
    }

    companion object {
        fun fromValue(value: String?): DCCTestResult? {
            return values().firstOrNull { it.value == value }
        }
    }
}