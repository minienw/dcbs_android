/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *  Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *  SPDX-License-Identifier: EUPL-1.2
 *
 */

package nl.rijksoverheid.dcbs.verifier.automator

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun validationAutomatorModule() = module {

    viewModel<ValidationAutomatorViewModel> {
        ValidationAutomatorViewModelImpl()
    }
}
