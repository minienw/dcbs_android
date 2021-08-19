package nl.rijksoverheid.dcbs.verifier

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.opencsv.CSVWriter
import nl.rijksoverheid.ctr.appconfig.AppConfigViewModel
import nl.rijksoverheid.ctr.introduction.IntroductionFragment
import nl.rijksoverheid.ctr.introduction.IntroductionViewModel
import nl.rijksoverheid.ctr.introduction.ui.status.models.IntroductionStatus
import nl.rijksoverheid.ctr.shared.MobileCoreWrapper
import nl.rijksoverheid.dcbs.verifier.automator.ValidationAutomatorViewModel
import nl.rijksoverheid.dcbs.verifier.databinding.ActivityMainBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileWriter
import java.io.Writer
import java.text.SimpleDateFormat
import java.util.*

/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */
class VerifierMainActivity : AppCompatActivity() {

    private val introductionViewModel: IntroductionViewModel by viewModel()
    private val appStatusViewModel: AppConfigViewModel by viewModel()
    private val validationAutomatorViewModel: ValidationAutomatorViewModel by viewModel()
    private val mobileCoreWrapper: MobileCoreWrapper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        if (BuildConfig.FLAVOR == "val") {
            initValidationAutomator()
            return
        }
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //if (BuildConfig.FLAVOR == "prod") {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        //}

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val introductionStatus = introductionViewModel.getIntroductionStatus()

        if (introductionStatus !is IntroductionStatus.IntroductionFinished.NoActionRequired) {
            navController.navigate(
                R.id.action_introduction,
                IntroductionFragment.getBundle(
                    introductionStatus,
                    getString(R.string.app_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE.toString())
                )
            )
        }

        appStatusViewModel.appStatusLiveData.observe(this, {
//            if (it !is AppStatus.NoActionRequired) {
//                val bundle = bundleOf(AppStatusFragment.EXTRA_APP_STATUS to it)
//                navController.navigate(R.id.action_app_status, bundle)
//            }
        })
    }

    override fun onStart() {
        super.onStart()
        // Only get app config on every app foreground when introduction is finished
        if (introductionViewModel.getIntroductionStatus() is IntroductionStatus.IntroductionFinished) {
            updateConfig()
        }
    }

    override fun onStop() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.setGraph(R.navigation.verifier_nav_graph_root)
        super.onStop()
    }

    fun updateConfig() {
        appStatusViewModel.refresh(mobileCoreWrapper)
    }

    fun lastConfigFetchTime(): Date {
        return appStatusViewModel.lastConfigFetchTime()
    }

    fun checkLastConfigFetchExpired(time: Long): Boolean {
        return appStatusViewModel.checkLastConfigFetchExpired(time)
    }

    private fun initValidationAutomator() {
        validationAutomatorViewModel.validatorResultsLiveData.observe(this, {
            saveCSVFile(it)
        })
        appStatusViewModel.appStatusLiveData.observe(this, {
            validationAutomatorViewModel.runValidation(mobileCoreWrapper)
        })
        updateConfig()
    }

    private fun saveCSVFile(results: List<String>) {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val date = sdf.format(Date())
        val filename = "Validation Report D${date} V${BuildConfig.VERSION_NAME} B${BuildConfig.VERSION_CODE} Android.csv"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveFileAndroidQ(filename, results)
        } else {
            saveFileBelowQ(filename, results)
        }
        Toast.makeText(
            applicationContext,
            String.format("%s file is exported into your Downloads folder", filename),
            Toast.LENGTH_LONG
        ).show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveFileAndroidQ(filename: String, results: List<String>) {

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, filename)
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
        }

        contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            ?.let { uri ->

                contentResolver.openOutputStream(uri)?.writer()?.use { writer ->
                    generateCSV(writer, results)
                }
            }
    }

    private fun saveFileBelowQ(filename: String, results: List<String>) {

        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(storageDir, filename)
        generateCSV(FileWriter(file), results)
    }

    private fun generateCSV(writer: Writer, results: List<String>) {

        CSVWriter(
            writer,
            CSVWriter.DEFAULT_SEPARATOR,
            CSVWriter.NO_QUOTE_CHARACTER,
            CSVWriter.NO_ESCAPE_CHARACTER
        ).use { csvWriter ->

            val headerRecord = arrayOf("QR", "Result", "Remark")
            csvWriter.writeNext(headerRecord)

            results.forEach {
                csvWriter.writeNext(arrayOf(it))
            }
        }

    }
}
