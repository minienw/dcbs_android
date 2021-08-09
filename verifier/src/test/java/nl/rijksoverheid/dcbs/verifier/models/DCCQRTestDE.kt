package nl.rijksoverheid.dcbs.verifier.models

import com.google.gson.Gson
import dgca.verifier.app.engine.data.Rule
import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableItem
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

class DCCQRTestDE {

    private lateinit var businessRules: List<Rule>
    private lateinit var vocExtraTestRule: VOCExtraTestRule
    private lateinit var valueSets: String
    private var validationClock: ZonedDateTime = ZonedDateTime.of(
        2021, 8, 6, 0, 0, 0, 0,
        ZoneId.of(ZoneOffset.UTC.id)
    )

    @Before
    fun setUp() {
        businessRules = DCCQRTestHelper.readBusinessRules()
        vocExtraTestRule = DCCQRTestHelper.readVocRules()
        valueSets = DCCQRTestHelper.readValueSets()
    }

    @Test
    fun `VR should pass`() = assertPass(getDccQr("VR_pass.json"))

    @Test
    fun `RR should pass`() = assertPass(getDccQr("RR_pass.json"))

    @Test
    fun `TR should pass`() = assertPass(getDccQr("TR_pass.json"))

    @Test
    fun `TR-DE-0001 should fail`() = assertFail("TR-DE-0001")

    @Test
    fun `TR-DE-0002 should fail`() = assertFail("TR-DE-0002")

    @Test
    fun `TR-DE-0003 should fail`() = assertFail("TR-DE-0003")

    @Test
    fun `TR-DE-0004 should fail`() = assertFail("TR-DE-0004")

    @Test
    fun `TR-DE-0005 should fail`() = assertFail("TR-DE-0005")

    @Test
    fun `VR-DE-0001 should fail`() = assertFail("VR-DE-0001")

    @Test
    fun `VR-DE-0002 should fail`() = assertFail("VR-DE-0002")

    @Test
    fun `VR-DE-0003 should fail`() = assertFail("VR-DE-0003")

    @Test
    fun `VR-DE-0004 should fail`() = assertFail("VR-DE-0004")

    @Test
    fun `RR-DE-0001 should fail`() = assertFail("RR-DE-0001")

    @Test
    fun `RR-DE-0002 should fail`() = assertFail("RR-DE-0002")

    private fun assertPass(dccQR: DCCQR) {
        val from = CountryRiskHelper.ORANGE_VERY_HIGH_RISK_VOC
        val to = CountryRiskHelper.DE
        val result = processRules(dccQR, from, to)
        Assert.assertEquals(0, result.size)
    }

    private fun assertFail(ruleIdentifier: String) {
        val dccQR = getDccQr("${ruleIdentifier}_fail.json")
        val from = CountryRiskHelper.ORANGE_VERY_HIGH_RISK_VOC
        val to = CountryRiskHelper.DE
        val result = processRules(dccQR, from, to)
        Assert.assertTrue(result.any { it.ruleIdentifier == ruleIdentifier })
    }

    private fun processRules(dccQR: DCCQR, from: CountryRisk, to: CountryRisk): List<DCCFailableItem> {
        return dccQR.processBusinessRules(from, to, vocExtraTestRule, businessRules, valueSets, Gson().toJson(dccQR), validationClock)
    }

    private fun getDccQr(filename: String) : DCCQR {
        return DCCQRTestHelper.getDccQr("dcc/de/${filename}")
    }
}