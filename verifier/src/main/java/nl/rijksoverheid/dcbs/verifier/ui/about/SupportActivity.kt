package nl.rijksoverheid.dcbs.verifier.ui.about

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import nl.rijksoverheid.dcbs.verifier.BuildConfig
import nl.rijksoverheid.dcbs.verifier.R
import nl.rijksoverheid.dcbs.verifier.databinding.ActivitySupportBinding
import java.util.*


class SupportActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val binding = ActivitySupportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeView(binding)
    }

    private fun initializeView(binding: ActivitySupportBinding) {

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        title = getString(R.string.report_problem_title)

        binding.btnSend.setOnClickListener {
            sendEmail(binding)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun sendEmail(binding: ActivitySupportBinding) {
        val emailIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("dcbsbeheer@minienw.nl"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.report_problem_item))
        emailIntent.putExtra(Intent.EXTRA_TEXT, binding.edittextSupport.text.toString() + getDeviceLog())

        startActivity(Intent.createChooser(emailIntent, getString(R.string.report_problem_title)))
    }

    private fun getDeviceLog(): String {

        val stringBuilder = StringBuilder()
        stringBuilder.append("\n")
        stringBuilder.append("\n")
        stringBuilder.append("-----------------------------------")
        stringBuilder.append("\n")
        stringBuilder.append("App version: ").append(BuildConfig.VERSION_NAME).append(" ").append(BuildConfig.VERSION_CODE.toString())
        stringBuilder.append("\n")
        stringBuilder.append("Android version: ").append(Build.VERSION.SDK_INT)
        stringBuilder.append("\n")
        stringBuilder.append("Device: ").append(Build.MANUFACTURER).append(" ").append(Build.MODEL)
        stringBuilder.append("\n")
        stringBuilder.append("Locale: ").append(Locale.getDefault())
        stringBuilder.append("\n")
        stringBuilder.append("Timezone: ").append(TimeZone.getDefault().id)
        stringBuilder.append("\n")
        stringBuilder.append("-----------------------------------")
        stringBuilder.append("\n")
        return stringBuilder.toString()
    }

    companion object {

        fun start(context: Context?) {
            val intent = Intent(context, SupportActivity::class.java)
            context?.startActivity(intent)
        }
    }
}