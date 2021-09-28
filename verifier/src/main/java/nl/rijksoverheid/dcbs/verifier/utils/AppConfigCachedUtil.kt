package nl.rijksoverheid.dcbs.verifier.utils

import android.content.Context
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dgca.verifier.app.engine.data.Rule
import dgca.verifier.app.engine.data.source.remote.rules.RuleRemote
import dgca.verifier.app.engine.data.source.remote.rules.toRules
import nl.rijksoverheid.ctr.appconfig.CachedAppConfigUseCase
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.models.CountryColorCode
import nl.rijksoverheid.dcbs.verifier.models.CountryRisk
import nl.rijksoverheid.dcbs.verifier.models.CountryRiskPass
import nl.rijksoverheid.dcbs.verifier.models.EURules
import nl.rijksoverheid.dcbs.verifier.models.data.ValueSetItem
import nl.rijksoverheid.dcbs.verifier.models.data.ValueSetObject
import nl.rijksoverheid.dcbs.verifier.models.data.ValueSetType
import org.json.JSONObject
import kotlin.collections.ArrayList

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */
interface AppConfigCachedUtil {
    fun getCountries(isOtherIncluded: Boolean): List<CountryRisk>?
    fun getEuropeanVerificationRules(): EURules?
    fun getBusinessRules(): List<Rule>?
    fun getValueSetsRaw(): String?
    fun getValueSetContainer(valueSetType: ValueSetType) : List<ValueSetObject>?
}

class AppConfigCachedUtilImpl(
    private val cachedAppConfigUseCase: CachedAppConfigUseCase,
    private val context: Context
) : AppConfigCachedUtil {

    override fun getCountries(isOtherIncluded: Boolean): List<CountryRisk>? {
        cachedAppConfigUseCase.getCachedAppConfigRaw()?.let { appConfig ->
            val jsonString = JSONObject(appConfig).getString("countryColors")
            val type = object : TypeToken<List<CountryRisk>>() {}.type
            val countries = Gson().fromJson<ArrayList<CountryRisk>>(jsonString, type)
            if (isOtherIncluded) {
                countries.add(getOther())
            }
            return countries.sortedBy { it.name() }
        }
        return null
    }

    override fun getEuropeanVerificationRules(): EURules? {
        cachedAppConfigUseCase.getCachedAppConfigRaw()?.let { appConfig ->
            val jsonString = JSONObject(appConfig).getString("europeanVerificationRules")
            val type = object : TypeToken<EURules>() {}.type
            return Gson().fromJson<EURules>(jsonString, type)
        }
        return null
    }

    override fun getBusinessRules(): List<Rule>? {
        cachedAppConfigUseCase.getCachedBusinessRulesRaw()?.let { businessRules ->
            val mapper = ObjectMapper()
            mapper.findAndRegisterModules()
            val remoteRules = mapper.readValue(businessRules,
                object : TypeReference<List<RuleRemote>>() {})
            return remoteRules.toRules()
        }
        return null

    }

    override fun getValueSetsRaw(): String? {
        return cachedAppConfigUseCase.getCachedValueSetsRaw()
    }

    override fun getValueSetContainer(valueSetType: ValueSetType) : List<ValueSetObject>? {
        getValueSetsRaw()?.let { rawSet ->
            val objectList = ArrayList<ValueSetObject>()
            val containerObject = JSONObject(rawSet).getJSONObject(valueSetType.value)
            containerObject.keys().forEach { key ->
                val valueSetItem = Gson().fromJson(containerObject.getString(key), ValueSetItem::class.java)
                objectList.add(ValueSetObject(key, valueSetItem))
            }
            return objectList
        }
        return null
    }

    private fun getOther(): CountryRisk {
        val other = context.getString(R.string.country_other)
        return CountryRisk(
            other,
            other,
            other,
            other,
            other,
            CountryColorCode.GREEN.value,
            CountryRiskPass.Inconclusive.value,
            isColourCode = false,
            ruleEngineEnabled = false,
            isEU = false
        )
    }
}
