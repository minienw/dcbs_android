package nl.rijksoverheid.ctr.introduction.ui.onboarding

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import nl.rijksoverheid.ctr.introduction.R
import nl.rijksoverheid.ctr.introduction.databinding.FragmentOnboardingBinding
import nl.rijksoverheid.ctr.shared.ext.findNavControllerSafety
import nl.rijksoverheid.ctr.shared.ext.getNavigationIconView
import nl.rijksoverheid.ctr.shared.utils.Accessibility.setAccessibilityFocus

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */
class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {

    private val args: OnboardingFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentOnboardingBinding.bind(view)

        val adapter =
            OnboardingPagerAdapter(
                childFragmentManager,
                lifecycle,
                args.introductionData.onboardingItems
            )

        if (args.introductionData.onboardingItems.isNotEmpty()) {
            binding.indicators.initIndicator(adapter.itemCount)
            initViewPager(binding, adapter)
        }

        setBackPressListener(binding)

        setBindings(binding, adapter)
    }

    private fun setBindings(
        binding: FragmentOnboardingBinding,
        adapter: OnboardingPagerAdapter
    ) {
        binding.toolbar.setNavigationOnClickListener {
            binding.viewPager.currentItem = binding.viewPager.currentItem - 1
        }
        binding.button.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem == adapter.itemCount - 1) {
                findNavControllerSafety(R.id.nav_onboarding)?.navigate(
                    OnboardingFragmentDirections.actionPrivacyPolicy(
                        args.introductionData
                    )
                )
            } else {
                binding.viewPager.currentItem = currentItem + 1
                binding.toolbar.getNavigationIconView()?.setAccessibilityFocus()
            }
        }
    }

    private fun setBackPressListener(binding: FragmentOnboardingBinding) {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentItem = binding.viewPager.currentItem
                if (currentItem == 0) {
                    val canPop = findNavController().popBackStack()
                    if (!canPop) {
                        requireActivity().finish()
                    }
                } else {
                    binding.viewPager.currentItem = binding.viewPager.currentItem - 1
                }
            }
        })
    }

    private fun initViewPager(
        binding: FragmentOnboardingBinding,
        adapter: OnboardingPagerAdapter
    ) {
        binding.viewPager.offscreenPageLimit = args.introductionData.onboardingItems.size
        binding.viewPager.adapter = adapter
        binding.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            @SuppressLint("StringFormatInvalid")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.toolbar.visibility = if (position == 0) View.GONE else View.VISIBLE
                binding.indicators.updateSelected(position)

                binding.indicators.contentDescription = getString(
                    R.string.onboarding_page_indicator_label,
                    (position + 1).toString(),
                    adapter.itemCount.toString()
                )

                // Apply bottom elevation if the view inside the viewpager is scrollable
                val scrollView =
                    childFragmentManager.fragments[position]?.view?.findViewById<ScrollView>(R.id.scroll)
                if (scrollView?.canScrollVertically(1) == true) {
                    binding.bottom.cardElevation =
                        resources.getDimensionPixelSize(R.dimen.scroll_view_button_elevation)
                            .toFloat()
                } else {
                    binding.bottom.cardElevation = 0f
                }
            }
        })
    }
}
