package nl.rijksoverheid.dcbs.verifier.models.data

import android.content.Context
import nl.rijksoverheid.dcbs.verifier.R

class DCCFailableItem(val type: DCCFailableType, val param1: Int? = null, val param2: String? = null) {

    fun getDisplayName(context: Context): String {
        return when (type) {
            DCCFailableType.MissingRequiredTest -> context.getString(R.string.rule_test_required)
            DCCFailableType.TestDateExpired -> context.getString(R.string.rule_test_outdated, param1)
            DCCFailableType.TestMustBeNegative -> context.getString(R.string.rule_test_must_be_negative)
            DCCFailableType.RedNotAllowed -> context.getString(R.string.rule_red_not_allowed)
            DCCFailableType.NeedFullVaccination -> context.getString(R.string.rule_full_vaccination_required)
            DCCFailableType.RecoveryNotValid -> context.getString(R.string.rule_recovery_not_valid)
            DCCFailableType.RequireSecondTest -> context.getString(R.string.rule_require_second_test, param2, param1)
        }
    }
}

enum class DCCFailableType {
    MissingRequiredTest,
    TestDateExpired,
    TestMustBeNegative,
    RedNotAllowed,
    NeedFullVaccination,
    RecoveryNotValid,
    RequireSecondTest
}