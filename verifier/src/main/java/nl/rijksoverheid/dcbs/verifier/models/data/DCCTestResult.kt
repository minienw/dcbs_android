package nl.rijksoverheid.dcbs.verifier.models.data

import android.content.Context
import nl.rijksoverheid.dcbs.verifier.R

enum class DCCTestResult(val value: String) {
    NotDetected("260415000"),
    Detected("260373001");

    fun getDisplayName(context: Context): String {
        return context.getString(when (this) {
            NotDetected -> R.string.item_test_header_negative
            Detected ->R.string.item_test_header_positive
        })
    }

    companion object {
        fun fromValue(value: String?): DCCTestResult? {
            return values().firstOrNull { it.value == value }
        }
    }
}