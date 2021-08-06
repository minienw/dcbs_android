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

class DCCQRTest {

    private lateinit var businessRules: List<Rule>
    private lateinit var vocExtraTestRule: VOCExtraTestRule
    private lateinit var valueSets: String
    private var validationClock: ZonedDateTime = ZonedDateTime.of(
        2021, 8, 6, 0, 0, 0, 0,
        ZoneId.of(ZoneOffset.UTC.id)
    )

    @Before
    fun setUp() {
        businessRules = readBusinessRules()
        vocExtraTestRule = readVocRules()
        valueSets = readValueSets()
    }

    @Test
    fun `VR should pass`() = assertPass(getDccQr("VR_pass.json"))

    @Test
    fun `RR should pass`() = assertPass(getDccQr("RR_pass.json"))

    @Test
    fun `TR should pass`() = assertPass(getDccQr("TR_pass.json"))

    @Test
    fun `GR-NL-0000 should fail`() = assertFail("GR-NL-0000")

    @Test
    fun `GR-NL-0001 should fail`() = assertFail("GR-NL-0001")

    @Test
    fun `TR-NL-0000 should fail`() = assertFail("TR-NL-0000")

    @Test
    fun `TR-NL-0001 should fail`() = assertFail("TR-NL-0001")

    @Test
    fun `TR-NL-0003 should fail`() = assertFail("TR-NL-0003")

    @Test
    fun `TR-NL-0004 should fail`() = assertFail("TR-NL-0004")

    @Test
    fun `VR-NL-0000 should fail`() = assertFail("VR-NL-0000")

    @Test
    fun `VR-NL-0001 should fail`() = assertFail("VR-NL-0001")

    @Test
    fun `VR-NL-0002 should fail`() = assertFail("VR-NL-0002")

    @Test
    fun `VR-NL-0005 should fail`() = assertFail("VR-NL-0005")

    @Test
    fun `RR-NL-0000 should fail`() = assertFail("RR-NL-0000")

    @Test
    fun `RR-NL-0001 should fail`() = assertFail("RR-NL-0001")

    @Test
    fun `RR-NL-0003 should fail`() = assertFail("RR-NL-0003")

    @Test
    fun `RR-NL-0004 should fail`() = assertFail("RR-NL-0004")

    private fun assertPass(dccQR: DCCQR) {
        val from = CountryRiskHelper.ORANGE_VERY_HIGH_RISK_VOC
        val to = CountryRiskHelper.NL
        val result = processRules(dccQR, from, to)
        Assert.assertEquals(0, result.size)
    }

    private fun assertFail(ruleIdentifier: String) {
        val dccQR = getDccQr(ruleIdentifier + "_fail.json")
        val from = CountryRiskHelper.ORANGE_VERY_HIGH_RISK_VOC
        val to = CountryRiskHelper.NL
        val result = processRules(dccQR, from, to)
        Assert.assertTrue(result.any { it.ruleIdentifier == ruleIdentifier })
    }

    private fun processRules(dccQR: DCCQR, from: CountryRisk, to: CountryRisk): List<DCCFailableItem> {
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
        return this.javaClass.classLoader?.let { classLoader ->
            classLoader.getResourceAsStream(filename).bufferedReader().use { it.readText() }
        } ?: ""
    }
}