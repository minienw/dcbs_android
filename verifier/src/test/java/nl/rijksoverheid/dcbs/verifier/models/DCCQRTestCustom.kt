package nl.rijksoverheid.dcbs.verifier.models

import dgca.verifier.app.engine.data.Rule
import nl.rijksoverheid.dcbs.verifier.models.data.DCCFailableItem
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

class DCCQRTestCustom {

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
    fun `CR-NL-0001 should fail`() = assertFail("CR-NL-0001", CountryRiskHelper.ORANGE_VERY_HIGH_RISK_VOC)

    @Test
    fun `CR-NL-0002 should fail when ORANGE_VERY_HIGH_RISK_NEU `() =
        assertFail("CR-NL-0002", CountryRiskHelper.ORANGE_VERY_HIGH_RISK_NEU)

    @Test
    fun `CR-NL-0002 should fail when ORANGE_VERY_HIGH_RISK_VOC_NEU`() =
        assertFail("CR-NL-0002", CountryRiskHelper.ORANGE_VERY_HIGH_RISK_VOC_NEU)

    @Test
    fun `CR-NL-0003 should fail when ORANGE_VERY_HIGH_RISK_VOC`() =
        assertFail("CR-NL-0003", CountryRiskHelper.ORANGE_VERY_HIGH_RISK_VOC)

    @Test
    fun `CR-NL-0003 should fail when ORANGE_VERY_HIGH_RISK_VOC_NEU`() =
        assertFail("CR-NL-0003", CountryRiskHelper.ORANGE_VERY_HIGH_RISK_VOC_NEU)

    @Test
    fun `CR-NL-0004 should fail`() = assertFail("CR-NL-0004", CountryRiskHelper.ORANGE_VERY_HIGH_RISK_VOC_NEU, onlyOneError = false)

    @Test
    fun `CR-NL-0005 should fail`() = assertFail("CR-NL-0005", CountryRiskHelper.ORANGE_VERY_HIGH_RISK_VOC_NEU)

    @Test
    fun `CR-NL-0006 should fail`() = assertFail("CR-NL-0006", CountryRiskHelper.ORANGE_VERY_HIGH_RISK_VOC_NEU)

    @Test
    fun `CR-NL-0007 should fail`() = assertFail("CR-NL-0007", CountryRiskHelper.ORANGE_VERY_HIGH_RISK_VOC_NEU, onlyOneError = false)

    private fun assertFail(ruleIdentifier: String, from: CountryRisk, onlyOneError: Boolean = true, fileIdentifier : String? = null) {
        val filename = fileIdentifier ?: ruleIdentifier
        val dccQR = DCCQRTestHelper.getDccQr("dcc/custom/${filename}_fail.json")
        val to = CountryRiskHelper.NL
        val result = processRules(dccQR, from, to)
        if (onlyOneError) {
            Assert.assertEquals(1, result.size)
        }
        Assert.assertTrue(result.any { it.ruleIdentifier == ruleIdentifier })
    }

    private fun processRules(dccQR: DCCQR, from: CountryRisk, to: CountryRisk): List<DCCFailableItem> {
        return dccQR.processBusinessRules(from, to, businessRules, valueSets, validationClock)
    }
}