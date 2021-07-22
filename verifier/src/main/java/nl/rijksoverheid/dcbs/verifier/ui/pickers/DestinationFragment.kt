package nl.rijksoverheid.dcbs.verifier.ui.pickers

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.databinding.FragmentCountryPickerBinding
import nl.rijksoverheid.dcbs.verifier.persistance.PersistenceManager
import nl.rijksoverheid.dcbs.verifier.utils.AppConfigCachedUtil
import org.koin.android.ext.android.inject
import java.util.*

class DestinationFragment : Fragment(R.layout.fragment_destination_picker) {

    private val persistenceManager: PersistenceManager by inject()
    private val appConfigUtil: AppConfigCachedUtil by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentCountryPickerBinding.bind(view)
        val businessRules = appConfigUtil.getBusinessRules()
        appConfigUtil.getCountries(true)
            ?.filter {
                businessRules?.find { rule ->
                    rule.countryCode.toUpperCase(Locale.getDefault()) == it.code
                } != null || it.code == context?.getString(R.string.country_other)
            }
            ?.let { countries ->
                GroupAdapter<GroupieViewHolder>()
                    .run {
                        addAll(countries.map { PickerAdapterItem(it.name()) })
                        binding.recyclerView.adapter = this
                        setOnItemClickListener { item, _ ->
                            countries.find { it.name() == (item as? PickerAdapterItem)?.title }
                                ?.let { country ->
                                    persistenceManager.saveDestinationValue(country.code ?: "")
                                }
                            findNavController().popBackStack()
                        }
                    }
            }
    }
}