package nl.rijksoverheid.dcbs.verifier.ui.scanner

import android.view.View
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.xwray.groupie.viewbinding.BindableItem
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.databinding.ItemBusinessErrorBinding
import nl.rijksoverheid.dcbs.verifier.databinding.ItemCountryPickerBinding

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */
class BusinessErrorAdapterItem(
    val title: String,
    private val isUndecided: Boolean,
) : BindableItem<ItemBusinessErrorBinding>() {
    override fun bind(viewBinding: ItemBusinessErrorBinding, position: Int) {
        val context = viewBinding.errorMessage.context
        viewBinding.errorMessage.text = title
        val textColorResId = if (isUndecided) R.color.black else R.color.red
        viewBinding.errorMessage.setTextColor(ContextCompat.getColor(context, textColorResId))
    }

    override fun getLayout(): Int {

        return R.layout.item_business_error
    }

    override fun initializeViewBinding(view: View): ItemBusinessErrorBinding {
        return ItemBusinessErrorBinding.bind(view)
    }
}
