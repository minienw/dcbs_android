package nl.rijksoverheid.dcbs.verifier.ui.pickers

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.databinding.FragmentCountryPickerBinding
import nl.rijksoverheid.dcbs.verifier.models.CountryRisk
import nl.rijksoverheid.dcbs.verifier.persistance.PersistenceManager
import nl.rijksoverheid.dcbs.verifier.utils.AppConfigCachedUtil
import org.koin.android.ext.android.inject

class CountryPickerFragment : Fragment(R.layout.fragment_country_picker) {

    private val persistenceManager: PersistenceManager by inject()
    private val appConfigUtil: AppConfigCachedUtil by inject()
    private var adapter = GroupAdapter<GroupieViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentCountryPickerBinding.bind(view)
        getCountries("")?.let { countries ->
            adapter.run {

                addAll(getAdapterData(countries))
                binding.recyclerView.adapter = this
                setOnItemClickListener { item, _ ->
                    countries.find { it.name() == (item as? PickerAdapterItem)?.title }?.let { country ->
                        persistenceManager.saveDepartureValue(country.code ?: "")
                    }
                    findNavController().popBackStack()
                }
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                getCountries(query ?: "")?.let { countries ->
                    adapter.update(getAdapterData(countries))
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                getCountries(newText ?: "")?.let { countries ->
                    adapter.update(getAdapterData(countries))
                }
                return true
            }
        })
    }

    private fun getCountries(query: String): List<CountryRisk>? {
        val countries = appConfigUtil
            .getCountries(false)
            ?.filter { it.isColourCode != true }?.sortedBy { it.name() }

        return if (query.isNotEmpty()) {
            countries?.filter { it.name().startsWith(query, true) }
        } else countries
    }

    private fun getAdapterData(countries: List<CountryRisk>): List<Section> {
        val sections = ArrayList<Section>()
        countries.groupBy { it.section() }.forEach { group ->
            val section = Section()
            section.setHeader(PickerHeaderItem(group.key ?: ""))
            group.value.forEachIndexed { index, countryRisk ->
                val isLast = index == group.value.size - 1
                section.add(PickerAdapterItem(countryRisk.name(), isLast))
            }
            sections.add(section)
        }
        return sections
    }
}