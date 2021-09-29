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

class DCCQRTestLT {

    private lateinit var businessRules: List<Rule>
    private lateinit var valueSets: String
    private var validationClock: ZonedDateTime = ZonedDateTime.of(
        2021, 8, 6, 0, 0, 0, 0,
        ZoneId.of(ZoneOffset.UTC.id)
    )

    @Before
    fun setUp() {
        businessRules = DCCQRTestHelper.readAllRules()
        valueSets = DCCQRTestHelper.readValueSets()
    }

    @Test
    fun `VR should pass`() = assertPass(DCCQRTestHelper.getDccQr("dcc/VR_pass.json"))

    @Test
    fun `RR should pass`() = assertPass(DCCQRTestHelper.getDccQr("dcc/RR_pass.json"))

    @Test
    fun `TR should pass`() = assertPass(DCCQRTestHelper.getDccQr("dcc/TR_pass.json"))

    @Test
    fun `GR-LT-0001 should fail`() = assertFail("GR-LT-0001")

    @Test
    fun `TR-LT-0000 should fail`() = assertFail("TR-LT-0000")

    @Test
    fun `TR-LT-0001 should fail`() = assertFail("TR-LT-0001")

    @Test
    fun `TR-LT-0002 should fail`() = assertFail("TR-LT-0002")

    @Test
    fun `TR-LT-0003 should fail`() = assertFail("TR-LT-0003")

    @Test
    fun `TR-LT-0004 should fail`() = assertFail("TR-LT-0004")

    @Test
    fun `TR-LT-0005 should fail`() = assertFail("TR-LT-0005")

    @Test
    fun `VR-LT-0000 should fail`() = assertFail("VR-LT-0000")

    @Test
    fun `VR-LT-0001 should fail`() = assertFail("VR-LT-0001")

    @Test
    fun `VR-LT-0002 should fail`() = assertFail("VR-LT-0002")

    @Test
    fun `VR-LT-0003 should fail`() = assertFail("VR-LT-0003")

    @Test
    fun `RR-LT-0000 should fail`() = assertFail("RR-LT-0000")

    @Test
    fun `RR-LT-0001 should fail`() = assertFail("RR-LT-0001")

    @Test
    fun `RR-LT-0002 should fail`() = assertFail("RR-LT-0002")

    private fun assertPass(dccQR: DCCQR) {
        val from = CountryRiskHelper.ORANGE_VERY_HIGH_RISK_VOC
        val to = CountryRiskHelper.LT
        val result = processRules(dccQR, from, to)
        Assert.assertEquals(0, result.size)
    }

    private fun assertFail(ruleIdentifier: String) {
        val dccQR = DCCQRTestHelper.getDccQr("dcc/lt/${ruleIdentifier}_fail.json")
        val from = CountryRiskHelper.ORANGE_VERY_HIGH_RISK_VOC
        val to = CountryRiskHelper.LT
        val result = processRules(dccQR, from, to)
        Assert.assertEquals(1, result.size)
        Assert.assertTrue(result.any { it.ruleIdentifier == ruleIdentifier })
    }

    private fun processRules(dccQR: DCCQR, from: CountryRisk, to: CountryRisk): List<DCCFailableItem> {
        return dccQR.processBusinessRules(from, to, businessRules, valueSets, validationClock)
    }
}