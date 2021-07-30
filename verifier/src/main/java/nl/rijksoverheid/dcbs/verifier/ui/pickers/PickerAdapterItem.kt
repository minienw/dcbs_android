package nl.rijksoverheid.dcbs.verifier.ui.pickers

import android.view.View
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
class PickerAdapterItem(
    val title: String,
    val isLast: Boolean = false
) : BindableItem<ItemCountryPickerBinding>() {
    override fun bind(viewBinding: ItemCountryPickerBinding, position: Int) {
        viewBinding.title.text = title
        viewBinding.separator.visibility = if (isLast) View.GONE else View.VISIBLE
    }

    override fun getLayout(): Int {
        return R.layout.item_country_picker
    }

    override fun initializeViewBinding(view: View): ItemCountryPickerBinding {
        return ItemCountryPickerBinding.bind(view)
    }
}
