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
import nl.rijksoverheid.dcbs.verifier.persistance.PersistenceManager
import nl.rijksoverheid.dcbs.verifier.utils.AppConfigCachedUtil
import org.koin.android.ext.android.inject

class ColorCodePickerFragment : Fragment(R.layout.fragment_color_code_picker) {

    private val persistenceManager: PersistenceManager by inject()
    private val appConfigUtil: AppConfigCachedUtil by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentColorCodePickerBinding.bind(view)
        appConfigUtil.getCountries(false)?.filter { it.isColourCode == true }?.let { countries ->
            GroupAdapter<GroupieViewHolder>()
                .run {
                    addAll(countries.map { PickerAdapterItem(it.name()) })
                    binding.recyclerView.adapter = this
                    setOnItemClickListener { item, _ ->
                        countries.find { it.name() == (item as? PickerAdapterItem)?.title }?.let { country ->
                            persistenceManager.saveDepartureValue(country.code ?: "")
                        }
                        findNavController().popBackStack()
                    }
                }
        }
        ViewCompat.setNestedScrollingEnabled(binding.recyclerView, false)
        binding.button.setOnClickListener {
            getString(R.string.url_color_code).launchUrl(requireContext())
        }
    }


}