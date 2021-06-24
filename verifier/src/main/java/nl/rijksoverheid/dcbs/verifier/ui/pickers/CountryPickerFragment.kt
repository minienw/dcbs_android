package nl.rijksoverheid.dcbs.verifier.ui.pickers

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.databinding.FragmentCountryPickerBinding
import nl.rijksoverheid.dcbs.verifier.models.Countries
import nl.rijksoverheid.dcbs.verifier.persistance.PersistenceManager
import org.koin.android.ext.android.inject

class CountryPickerFragment : Fragment(R.layout.fragment_country_picker) {

    private val persistenceManager: PersistenceManager by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentCountryPickerBinding.bind(view)
        GroupAdapter<GroupieViewHolder>()
            .run {
                addAll(Countries.countries.map { PickerAdapterItem(getString(it.name)) })
                binding.recyclerView.adapter = this
                setOnItemClickListener { item, view ->
                    Countries.countries.find { getString(it.name) == (item as? PickerAdapterItem)?.title }?.let { country ->
                        persistenceManager.saveDestinationValue(country.code)
                    }
                    findNavController().popBackStack()
                }
            }
    }
}