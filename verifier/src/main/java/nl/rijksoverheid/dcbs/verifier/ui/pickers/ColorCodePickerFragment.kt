package nl.rijksoverheid.dcbs.verifier.ui.pickers

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import nl.rijksoverheid.ctr.shared.ext.launchUrl
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.databinding.FragmentColorCodePickerBinding
import nl.rijksoverheid.dcbs.verifier.models.CountryColorCode
import nl.rijksoverheid.dcbs.verifier.persistance.PersistenceManager
import org.koin.android.ext.android.inject

class ColorCodePickerFragment : Fragment(R.layout.fragment_color_code_picker) {

    private val persistenceManager: PersistenceManager by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = context ?: return
        val binding = FragmentColorCodePickerBinding.bind(view)
        GroupAdapter<GroupieViewHolder>()
            .run {
                addAll(CountryColorCode.values().map { PickerAdapterItem(it.getDisplayName(context)) })
                binding.recyclerView.adapter = this
                setOnItemClickListener { item, _ ->

                    CountryColorCode.fromDisplayName(context, (item as? PickerAdapterItem)?.title)?.value?.let { colorCodeValue ->
                        persistenceManager.saveDepartureValue(colorCodeValue)
                    }

                    findNavController().popBackStack()
                }
            }
        ViewCompat.setNestedScrollingEnabled(binding.recyclerView, false)
        binding.button.setOnClickListener {
            getString(R.string.url_color_code).launchUrl(requireContext())
        }
    }
}