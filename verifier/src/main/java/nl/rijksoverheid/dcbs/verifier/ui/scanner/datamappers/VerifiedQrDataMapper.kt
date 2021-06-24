package nl.rijksoverheid.dcbs.verifier.ui.scanner.datamappers

import com.google.gson.GsonBuilder
import nl.rijksoverheid.ctr.shared.MobileCoreWrapper
import nl.rijksoverheid.ctr.shared.ext.verify
import nl.rijksoverheid.dcbs.verifier.models.DCCQR
import nl.rijksoverheid.dcbs.verifier.ui.scanner.models.VerifiedQr

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */
interface VerifiedQrDataMapper {
    fun transform(qrContent: String): VerifiedQr
}

class VerifiedQrDataMapperImpl(private val mobileCoreWrapper: MobileCoreWrapper) : VerifiedQrDataMapper {
    override fun transform(
        qrContent: String
    ): VerifiedQr {

        val result =
            mobileCoreWrapper.verify(
                qrContent.toByteArray()
            ).verify()

        val data = result.decodeToString()

        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
        val dccQR = gson.fromJson(data, DCCQR::class.java)
        val hasVaccine = dccQR.dcc?.vaccines?.isNotEmpty() == true
        val hasTest = dccQR.dcc?.tests?.isNotEmpty() == true
        val hasRecovery = dccQR.dcc?.recoveries?.isNotEmpty() == true
        if (!hasVaccine && !hasTest && !hasRecovery) {
            throw Exception("no vaccine, test or recovery data")
        }

        return VerifiedQr(data = data)
    }
}
