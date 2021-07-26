package nl.rijksoverheid.dcbs.verifier.ui.pickers

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import nl.rijksoverheid.ctr.appconfig.AppConfigViewModel
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.databinding.FragmentCountryPickerBinding
import nl.rijksoverheid.dcbs.verifier.models.CountryRisk
import nl.rijksoverheid.dcbs.verifier.persistance.PersistenceManager
import nl.rijksoverheid.dcbs.verifier.utils.AppConfigCachedUtil
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class CountryPickerFragment : Fragment(R.layout.fragment_country_picker) {

    private val persistenceManager: PersistenceManager by inject()
    private val appConfigUtil: AppConfigCachedUtil by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentCountryPickerBinding.bind(view)
        appConfigUtil.getCountries(false)?.filter { it.isColourCode != true }?.let { countries ->
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
    }

}