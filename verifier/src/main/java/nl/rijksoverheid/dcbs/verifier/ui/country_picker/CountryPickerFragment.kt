package nl.rijksoverheid.dcbs.verifier.ui.country_picker

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.databinding.FragmentCountryPickerBinding
import nl.rijksoverheid.dcbs.verifier.databinding.FragmentScanInstructionsBinding
import nl.rijksoverheid.dcbs.verifier.models.Countries
import nl.rijksoverheid.dcbs.verifier.persistance.PersistenceManager
import nl.rijksoverheid.dcbs.verifier.ui.scanner.ScanResultValidFragmentArgs
import nl.rijksoverheid.dcbs.verifier.ui.scanner.ScanResultValidFragmentDirections
import nl.rijksoverheid.dcbs.verifier.ui.scanner.utils.ScannerUtil
import org.koin.android.ext.android.inject

class CountryPickerFragment : Fragment(R.layout.fragment_country_picker) {

    private val args: CountryPickerFragmentArgs by navArgs()
    private val persistenceManager: PersistenceManager by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentCountryPickerBinding.bind(view)
        GroupAdapter<GroupieViewHolder>()
            .run {
                addAll(Countries.countries.map { CountryPickerAdapterItem(it.name) })
                binding.recyclerView.adapter = this
                setOnItemClickListener { item, view ->
                    Countries.countries.find { it.name == (item as? CountryPickerAdapterItem)?.title}?.let { country ->
                        if (args.isDeparture) {
                            persistenceManager.saveDepartureValue(country.code)
                        }
                        else {
                            persistenceManager.saveDestinationValue(country.code)
                        }
                    }
                    findNavController().popBackStack()
                }
            }

        if (args.isDeparture) {
            binding.toolbar.title = getString(R.string.departure_country)
        }
        else {
            binding.toolbar.title = getString(R.string.destination_country)
        }
    }
}