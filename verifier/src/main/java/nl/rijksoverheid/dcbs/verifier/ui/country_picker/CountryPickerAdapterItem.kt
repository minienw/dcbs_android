package nl.rijksoverheid.dcbs.verifier.ui.country_picker

import android.view.View
import androidx.annotation.StringRes
import com.xwray.groupie.viewbinding.BindableItem
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.databinding.ItemCountryPickerBinding

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */
class CountryPickerAdapterItem(
    @StringRes val title: Int
) : BindableItem<ItemCountryPickerBinding>() {
    override fun bind(viewBinding: ItemCountryPickerBinding, position: Int) {
        viewBinding.title.setText(title)
    }

    override fun getLayout(): Int {
        return R.layout.item_country_picker
    }

    override fun initializeViewBinding(view: View): ItemCountryPickerBinding {
        return ItemCountryPickerBinding.bind(view)
    }
}
