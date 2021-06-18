package nl.rijksoverheid.ctr.introduction.ui.status

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import nl.rijksoverheid.ctr.introduction.ui.status.models.IntroductionStatus

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */
class IntroductionStatusFragment : Fragment() {

    companion object {
        private const val EXTRA_INTRODUCTION_STATUS = "EXTRA_INTRODUCTION_STATUS"
        private const val EXTRA_INTRODUCTION_VERSION = "EXTRA_INTRODUCTION_VERSION"

        fun getBundle(
            introductionStatus: IntroductionStatus, versionString: String
        ): Bundle {
            val bundle = Bundle()
            bundle.putParcelable(EXTRA_INTRODUCTION_STATUS, introductionStatus)
            bundle.putString(EXTRA_INTRODUCTION_VERSION, versionString)
            return bundle
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val introductionStatus = arguments?.getParcelable<IntroductionStatus>(
            EXTRA_INTRODUCTION_STATUS
        )

        when (introductionStatus) {
            is IntroductionStatus.IntroductionNotFinished -> {
                findNavController().navigate(
                    IntroductionStatusFragmentDirections.actionSetup(
                        introductionStatus.introductionData,
                        arguments?.getString(EXTRA_INTRODUCTION_VERSION) ?: ""
                    )
                )
            }
            is IntroductionStatus.IntroductionFinished.ConsentNeeded -> {
                findNavController().navigate(
                    IntroductionStatusFragmentDirections.actionNewTerms(
                        introductionStatus.newTerms
                    )
                )
            }
            is IntroductionStatus.IntroductionFinished.NoActionRequired -> {

            }
        }
    }
}
