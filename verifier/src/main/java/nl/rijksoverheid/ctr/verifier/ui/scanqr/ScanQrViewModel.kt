package nl.rijksoverheid.ctr.verifier.ui.scanqr

import androidx.lifecycle.ViewModel
import nl.rijksoverheid.ctr.verifier.persistance.PersistenceManager

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

abstract class ScanQrViewModel : ViewModel() {

}

class ScanQrViewModelImpl(
    private val persistenceManager: PersistenceManager
) : ScanQrViewModel() {

}
