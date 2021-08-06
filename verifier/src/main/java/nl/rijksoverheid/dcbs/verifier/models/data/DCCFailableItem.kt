package nl.rijksoverheid.dcbs.verifier.models.data

import android.content.Context
import nl.rijksoverheid.dcbs.verifier.R

class DCCFailableItem(
    val type: DCCFailableType,
    val param1: Int? = null,
    val param2: Int? = null,
    val param3: Int? = null,
    val customMessage: String? = null,
    val ruleIdentifier: String? = null
) {

    fun getDisplayName(context: Context): String {
        return when (type) {
            DCCFailableType.MissingRequiredTest -> context.getString(R.string.rule_test_required)
            DCCFailableType.TestDateExpired -> context.getString(R.string.rule_test_outdated, param1)
            DCCFailableType.TestMustBeNegative -> context.getString(R.string.rule_test_must_be_negative)
            DCCFailableType.RedNotAllowed -> context.getString(R.string.rule_red_not_allowed)
            DCCFailableType.NeedFullVaccination -> context.getString(R.string.rule_full_vaccination_required)
            DCCFailableType.RecoveryNotValid -> context.getString(R.string.rule_recovery_not_valid)
            DCCFailableType.RequireSecondTest -> context.getString(R.string.rule_require_second_test, param1)
            DCCFailableType.InvalidTestResult -> context.getString(R.string.rule_invalid_test_result)
            DCCFailableType.InvalidTestType -> context.getString(R.string.rule_invalid_test_type)
            DCCFailableType.InvalidTargetDisease -> context.getString(R.string.rule_invalid_target_disease)
            DCCFailableType.InvalidVaccineHolder -> context.getString(R.string.rule_invalid_vaccine_holder)
            DCCFailableType.InvalidVaccineType -> context.getString(R.string.rule_invalid_vaccine_type)
            DCCFailableType.InvalidVaccineProduct -> context.getString(R.string.rule_invalid_vaccine_product)
            DCCFailableType.DateOfBirthOutOfRange -> context.getString(R.string.rule_date_of_birth_out_of_range)
            DCCFailableType.InvalidCountryCode -> context.getString(R.string.rule_invalid_country_code)
            DCCFailableType.InvalidDateOfBirth -> context.getString(R.string.rule_invalid_date_of_birth)
            DCCFailableType.InvalidVaccineDate -> context.getString(R.string.rule_invalid_vaccine_date)
            DCCFailableType.InvalidTestDate -> context.getString(R.string.rule_invalid_test_date)
            DCCFailableType.InvalidRecoveryFirstTestDate -> context.getString(R.string.rule_invalid_recovery_first_test_date)
            DCCFailableType.InvalidRecoveryFromDate -> context.getString(R.string.rule_invalid_recovery_from_date)
            DCCFailableType.InvalidRecoveryToDate -> context.getString(R.string.rule_invalid_recovery_to_date)
            DCCFailableType.InvalidVaccine14Days -> context.getString(R.string.rule_vaccination_14_days)
            DCCFailableType.UndecidableFrom -> context.getString(R.string.result_inconclusive_message)
            DCCFailableType.CustomFailure -> customMessage ?: ""
            DCCFailableType.VocRequireSecondAntigen -> context.getString(R.string.voc_require_second_antigen, param1)
            DCCFailableType.VocRequireSecondPCR -> context.getString(R.string.voc_require_second_pcr, param1)
            DCCFailableType.VocRequirePCROrAntigen -> context.getString(R.string.voc_require_pcr_or_antigen, param1, param2, param3)
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
    RequireSecondTest,
    InvalidTestResult,
    InvalidTestType,
    InvalidTargetDisease,
    InvalidVaccineHolder,
    InvalidVaccineType,
    InvalidVaccineProduct,
    DateOfBirthOutOfRange,
    InvalidCountryCode,
    InvalidDateOfBirth,
    InvalidVaccineDate,
    InvalidTestDate,
    InvalidRecoveryFirstTestDate,
    InvalidRecoveryFromDate,
    InvalidRecoveryToDate,
    InvalidVaccine14Days,
    UndecidableFrom,
    CustomFailure,
    VocRequireSecondAntigen,
    VocRequireSecondPCR,
    VocRequirePCROrAntigen
}