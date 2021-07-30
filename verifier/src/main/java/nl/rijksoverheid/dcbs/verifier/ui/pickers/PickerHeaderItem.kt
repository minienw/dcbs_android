package nl.rijksoverheid.dcbs.verifier.ui.pickers

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.databinding.ItemCountryPickerHeaderBinding

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */
class PickerHeaderItem(
    val title: String
) : BindableItem<ItemCountryPickerHeaderBinding>() {
    override fun bind(viewBinding: ItemCountryPickerHeaderBinding, position: Int) {
        viewBinding.title.text = title
    }

    override fun getLayout(): Int {
        return R.layout.item_country_picker_header
    }

    override fun initializeViewBinding(view: View): ItemCountryPickerHeaderBinding {
        return ItemCountryPickerHeaderBinding.bind(view)
    }
}
