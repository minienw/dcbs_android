package nl.rijksoverheid.dcbs.verifier.models.data

import android.content.Context
import nl.rijksoverheid.dcbs.verifier.R

enum class DCCFailableItem {
    MissingRequiredTest,
    TestDateExpired48,
    TestDateExpired72,
    TestMustBeNegative;

    fun getDisplayName(context: Context): String {
        return when (this) {
            MissingRequiredTest -> context.getString(R.string.rule_test_required)
            TestDateExpired48 -> context.getString(R.string.rule_test_outdated, 48)
            TestDateExpired72 -> context.getString(R.string.rule_test_outdated, 72)
            TestMustBeNegative -> context.getString(R.string.rule_test_must_be_negative)

        }
    }
}