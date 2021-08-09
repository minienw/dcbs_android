package nl.rijksoverheid.dcbs.verifier.models

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import dgca.verifier.app.engine.data.Rule
import dgca.verifier.app.engine.data.source.remote.rules.RuleRemote
import dgca.verifier.app.engine.data.source.remote.rules.toRules
import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableItem
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

object DCCQRTestHelper {

    fun getDccQr(filename: String): DCCQR {
        val jsonString = readJsonFile(filename)
        return Gson().fromJson(jsonString, DCCQR::class.java)
    }

    fun readBusinessRules(): List<Rule> {
        val jsonString = readJsonFile("business_rules.json")
        val mapper = ObjectMapper()
        mapper.findAndRegisterModules()
        val remoteRules = mapper.readValue(jsonString, object : TypeReference<List<RuleRemote>>() {})
        return remoteRules.toRules()
    }

    fun readValueSets(): String {
        return readJsonFile("value_sets.json")
    }

    fun readVocRules(): VOCExtraTestRule {
        return Gson().fromJson(readJsonFile("voc_rules.json"), VOCExtraTestRule::class.java)
    }

    private fun readJsonFile(filename: String): String {
        return this.javaClass.classLoader?.let { classLoader ->
            classLoader.getResourceAsStream(filename).bufferedReader().use { it.readText() }
        } ?: ""
    }
}