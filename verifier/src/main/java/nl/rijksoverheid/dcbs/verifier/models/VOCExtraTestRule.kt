package nl.rijksoverheid.dcbs.verifier.models

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

class VOCExtraTestRule(
    val enabled: Boolean,
    val singlePCRTestHours: Int,
    val secondDosePCRMinTestHours: Int,
    val secondDoseAntiGenMinTestHours: Int
)