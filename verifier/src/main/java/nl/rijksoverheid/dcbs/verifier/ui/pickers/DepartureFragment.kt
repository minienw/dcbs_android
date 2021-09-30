package nl.rijksoverheid.dcbs.verifier.ui.pickers

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import nl.rijksoverheid.ctr.appconfig.AppConfigViewModel
import nl.rijksoverheid.ctr.shared.utils.Accessibility.setAsAccessibilityHeading
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.databinding.FragmentDeparturePickerBinding
import nl.rijksoverheid.dcbs.verifier.models.CountryRisk
import nl.rijksoverheid.dcbs.verifier.persistance.PersistenceManager
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class DepartureFragment : Fragment(R.layout.fragment_departure_picker) {

    private lateinit var countryFragment: CountryPickerFragment
    private lateinit var colorCodeFragment: ColorCodePickerFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView(view)
    }

    private fun initializeView(view: View) {
        val binding = FragmentDeparturePickerBinding.bind(view)

        countryFragment = CountryPickerFragment()
        colorCodeFragment = ColorCodePickerFragment()
        binding.toolbar.setAsAccessibilityHeading(true)

        binding.btnCountry.setOnClickListener {
            context?.let {
                binding.btnCountry.setTextColor(ContextCompat.getColor(it, R.color.primary_blue))
                binding.btnColor.setTextColor(ContextCompat.getColor(it, R.color.primary_blue_opacity30))
            }
            showFragment(countryFragment)
        }

        binding.btnColor.setOnClickListener {
            context?.let {
                binding.btnCountry.setTextColor(ContextCompat.getColor(it, R.color.primary_blue_opacity30))
                binding.btnColor.setTextColor(ContextCompat.getColor(it, R.color.primary_blue))
            }
            showFragment(colorCodeFragment)
        }

        showFragment(countryFragment)
    }

    private fun showFragment(destinationFragment: Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.picker_content, destinationFragment)
        transaction?.commit()
    }
}