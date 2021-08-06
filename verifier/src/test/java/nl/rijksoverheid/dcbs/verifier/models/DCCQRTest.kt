package nl.rijksoverheid.dcbs.verifier.models

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import dgca.verifier.app.engine.data.Rule
import dgca.verifier.app.engine.data.source.remote.rules.RuleRemote
import dgca.verifier.app.engine.data.source.remote.rules.toRules
import kotlinx.coroutines.test.runBlockingTest
import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableItem
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

class DCCQRTest {

    lateinit var businessRules: List<Rule>
    lateinit var vocExtraTestRule: VOCExtraTestRule
    lateinit var valueSets: String
    var validationClock = ZonedDateTime.of(2021, 7, 6, 0, 0, 0, 0,
        ZoneId.of(ZoneOffset.UTC.id))

    @Before
    fun setUp() {
        businessRules = readBusinessRules()
        vocExtraTestRule = readVocRules()
        valueSets = readValueSets()
    }

    @Test
    fun `VR should pass`() = runBlockingTest {

        val dccQR = getDccQr("VR_pass.json")
        assertPass(dccQR)
    }

    @Test
    fun `RR should pass`() = runBlockingTest {

        val dccQR = getDccQr("RR_pass.json")
        assertPass(dccQR)
    }

    @Test
    fun `TR should pass`() = runBlockingTest {

        val dccQR = getDccQr("TR_pass.json")
        assertPass(dccQR)
    }

    private fun assertPass(dccQR: DCCQR) {
        val from = CountryRiskHelper.ORANGE_VERY_HIGH_RISK_VOC
        val to = CountryRiskHelper.NL
        val result = processRules(dccQR, from, to)
        Assert.assertEquals(0, result.size)
    }

    private fun processRules(dccQR: DCCQR, from: CountryRisk, to: CountryRisk) : List<DCCFailableItem> {
        return dccQR.processBusinessRules(from, to, vocExtraTestRule, businessRules, valueSets, Gson().toJson(dccQR), validationClock)
    }

    private fun getDccQr(filename: String): DCCQR {
        val jsonString = readJsonFile("dcc/$filename")
        return Gson().fromJson(jsonString, DCCQR::class.java)
    }

    private fun readBusinessRules(): List<Rule> {
        val jsonString = readJsonFile("business_rules.json")
        val mapper = ObjectMapper()
        mapper.findAndRegisterModules()
        val remoteRules = mapper.readValue(jsonString, object : TypeReference<List<RuleRemote>>() {})
        return remoteRules.toRules()
    }

    private fun readValueSets(): String {
        return readJsonFile("value_sets.json")
    }

    private fun readVocRules(): VOCExtraTestRule {
        return Gson().fromJson(readJsonFile("voc_rules.json"), VOCExtraTestRule::class.java)
    }

    private fun readJsonFile(filename: String): String {
        return this.javaClass.classLoader.getResourceAsStream(filename).bufferedReader().use { it.readText() }
    }
}