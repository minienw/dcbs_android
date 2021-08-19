/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *  Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *  SPDX-License-Identifier: EUPL-1.2
 *
 */

package nl.rijksoverheid.dcbs.verifier.automator

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import nl.rijksoverheid.ctr.shared.MobileCoreWrapper
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.gildor.coroutines.okhttp.await
import java.util.concurrent.TimeUnit


abstract class ValidationAutomatorViewModel : ViewModel() {
    val validatorResultsLiveData = MutableLiveData<List<String>>()
    abstract fun runValidation(mobileCoreWrapper: MobileCoreWrapper)
}

class ValidationAutomatorViewModelImpl : ValidationAutomatorViewModel() {

    val reports = ArrayList<String>()

    override fun runValidation(mobileCoreWrapper: MobileCoreWrapper) {
        viewModelScope.launch {
            validateAll(mobileCoreWrapper)
        }
    }

    private suspend fun validateAll(mobileCoreWrapper: MobileCoreWrapper) {

        coroutineScope {
            qrCodeUrls.forEach { item ->
                    async { handle(item, mobileCoreWrapper) }
            }
        }

        validatorResultsLiveData.postValue(reports.sortedBy { it })
        reports.sortedBy { it }.forEach {
            Log.d("efecemk", it)
        }
    }

    private suspend fun handle(url: String, mobileCoreWrapper: MobileCoreWrapper) {

        getQR(url)?.let { bitmap ->
            decodeQR(bitmap)?.let { qrContent ->
                val result = mobileCoreWrapper.verify(qrContent.toByteArray())
                if (result.error.isNotEmpty()) {
                    report(url, "Failed", result.error)
                } else {
                    report(url, "Success", "")
                }
            } ?: run {
                report(url, "Failed", "Not a QR Code")
            }
        }
    }

    fun report(url: String, status: String, remark: String) {
        val item = url.replace(base, "")
        reports.add("$item,$status,$remark")
    }

    private suspend fun decodeQR(bitmap: Bitmap): String? {

        val newBitmap = addWhiteBorder(bitmap, 20F)
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC
            )
            .build()

        val scanner = BarcodeScanning.getClient(options)
        val image = InputImage.fromBitmap(newBitmap, 0)
        val results = scanner.process(image).await()
        return results.firstOrNull()?.rawValue
    }

    private suspend fun getQR(url: String): Bitmap? {
        val client = OkHttpClient.Builder()
            .readTimeout(20, TimeUnit.SECONDS)
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS).build()
        val request = Request.Builder().url(url).build()

        val response = client.newCall(request).await()
        return BitmapFactory.decodeStream(response.body?.byteStream())
    }

    private fun addWhiteBorder(bmp: Bitmap, borderSize: Float): Bitmap? {
        val bmpWithBorder = Bitmap.createBitmap(bmp.width + borderSize.toInt() * 2, bmp.height + borderSize.toInt() * 2, bmp.config)
        val canvas = Canvas(bmpWithBorder)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bmp, borderSize, borderSize, null)
        return bmpWithBorder
    }


    companion object {

        const val base = "https://raw.githubusercontent.com/eu-digital-green-certificates/dcc-quality-assurance/validation5/"

        val qrCodeUrls = listOf(
            "${base}AT/1.0.0/TEST.png",
            "${base}AT/1.3.0/TEST.png",
            "${base}AT/1.0.0/VAC.png",
            "${base}AT/1.3.0/VAC.png",
            "${base}AT/1.0.0/REC.png",
            "${base}AT/1.3.0/REC.png",
            "${base}BE/1.0.1/TEST.png",
            "${base}BE/1.3.0/TEST.png",
            "${base}BE/1.0.1/VAC.png",
            "${base}BE/1.3.0/VAC.png",
            "${base}BE/1.0.1/REC.png",
            "${base}BE/1.3.0/REC.png",
            "${base}BE/1.3.0/specialcases/VAC_INCOMPLETE_DOB.png",
            "${base}BG/1.0.0/VAC.png",
            "${base}BG/1.3.0/VAC.png",
            "${base}BG/1.0.0/REC.png",
            "${base}BG/1.3.0/REC.png",
            "${base}BG/1.3.0/specialcases/TEST.png",
            "${base}BG/1.0.0/specialcases/VAC-NULL-DATETIME.png",
            "${base}BG/1.0.0/specialcases/REC-NULL-DATETIME.png",
            "${base}BG/1.0.0/specialcases/REC-VALID-BEFORE-DSC.png",
            "${base}BG/1.3.0/specialcases/REC-VALID-BEFORE-DSC.png",
            "${base}CH/1.2.1/VAC.png",
            "${base}CH/1.3.0/VAC.png",
            "${base}CH/1.2.1/REC.png",
            "${base}CH/1.3.0/REC.png",
            "${base}CH/1.2.1/specialcases/TEST_CH_BAG.png",
            "${base}CH/1.2.1/specialcases/TEST_ma_field_empty.png",
            "${base}CH/1.3.0/specialcases/TEST_empty_ma_field.png",
            "${base}CH/1.2.1/specialcases/VAC_CH_BAG.png",
            "${base}CH/1.2.1/specialcases/REC_CH_BAG.png",
            "${base}CY/1.0.0/TEST.png",
            "${base}CY/1.3.0/TEST.png",
            "${base}CY/1.0.0/VAC.png",
            "${base}CY/1.3.0/VAC.png",
            "${base}CY/1.0.0/REC.png",
            "${base}CY/1.3.0/REC.png",
            "${base}CZ/1.0.1/TEST.png",
            "${base}CZ/1.3.0/TEST.png",
            "${base}CZ/1.0.1/VAC.png",
            "${base}CZ/1.3.0/VAC.png",
            "${base}CZ/1.0.1/REC.png",
            "${base}CZ/1.3.0/REC.png",
            "${base}CZ/1.3.0/specialcases/TEST_ma_wrong_value.png",
            "${base}DE/1.0.0/TEST.png",
            "${base}DE/1.0.0/VAC.png",
            "${base}DE/1.0.0/REC.png",
            "${base}DK/1.3.0/TEST-AG.png",
            "${base}DK/1.3.0/TEST-PCR.png",
            "${base}DK/1.3.0/VAC.png",
            "${base}DK/1.3.0/REC.png",
            "${base}EE/1.0.0/TEST.png",
            "${base}EE/1.3.0/TEST.png",
            "${base}EE/1.0.0/VAC.png",
            "${base}EE/1.3.0/VAC.png",
            "${base}EE/1.0.0/REC.png",
            "${base}EE/1.3.0/REC.png",
            "${base}EL/1.0.0/TEST.png",
            "${base}EL/1.3.0/TEST_PCR_v1.3_2021-07-20.png",
            "${base}EL/1.3.0/TEST_RAT_v1.3_2021-07-20.png",
            "${base}EL/1.0.0/VAC.png",
            "${base}EL/1.3.0/VAC.png",
            "${base}EL/1.3.0/VAC_JANSSEN_v1.3_2021-07-20.png",
            "${base}EL/1.3.0/VAC_MODERNA_v1.3_2021-07-20.png",
            "${base}EL/1.3.0/VAC_rec_v1.3_2021-07-20.png",
            "${base}EL/1.0.0/REC.png",
            "${base}EL/1.3.0/REC.png",
            "${base}ES/1.3.0/TEST_NAAT.png",
            "${base}ES/1.3.0/TEST_RAT.png",
            "${base}ES/1.3.0/VAC.png",
            "${base}ES/1.3.0/REC.png",
            "${base}ES/1.3.0/specialcases/TEST_NAAT_2.png",
            "${base}ES/1.3.0/specialcases/TEST_RAT_2.png",
            "${base}ES/1.3.0/specialcases/VAC_1.png",
            "${base}ES/1.3.0/specialcases/VAC_2.png",
            "${base}ES/1.3.0/specialcases/VAC_3.png",
            "${base}ES/1.3.0/specialcases/REC_1.png",
            "${base}ES/1.3.0/specialcases/REC_2.png",
            "${base}ES/1.3.0/specialcases/REC_3.png",
            "${base}ES/1.3.0/specialcases/REC_4.png",
            "${base}FI/1.3.0/TEST1.png",
            "${base}FI/1.3.0/TEST2.png",
            "${base}FI/1.0.0/VAC.png",
            "${base}FI/1.3.0/VAC1.png",
            "${base}FI/1.3.0/VAC2.png",
            "${base}FI/1.3.0/VAC3.png",
            "${base}FI/1.3.0/REC.png",
            "${base}FO/1.3.0/TEST.png",
            "${base}FO/1.3.0/VAC.png",
            "${base}FR/1.3.0/TEST.png",
            "${base}FR/1.0.0/VAC.png",
            "${base}FR/1.3.0/specialcases/VAC.png",
            "${base}HR/1.0.0/TEST.png",
            "${base}HR/1.3.0/TEST.png",
            "${base}HR/1.0.0/VAC.png",
            "${base}HR/1.3.0/VAC.png",
            "${base}HR/1.3.0/VAC_1DOSE.png",
            "${base}HR/1.0.0/REC.png",
            "${base}HR/1.3.0/REC.png",
            "${base}HU/1.3.0/TEST_NAAT.png",
            "${base}HU/1.3.0/VAC.png",
            "${base}HU/1.3.0/REC.png",
            "${base}HU/1.3.0/specialcases/TEST_RAT.png",
            "${base}IE/1.3.0/TEST_NAT.png",
            "${base}IE/1.3.0/TEST_RAT.png",
            "${base}IE/1.3.0/VAC.png",
            "${base}IE/1.3.0/REC.png",
            "${base}IS/1.3.0/TEST_PCR.png",
            "${base}IS/1.3.0/TEST_RAT.png",
            "${base}IS/1.3.0/VAC.png",
            "${base}IS/1.3.0/REC.png",
            "${base}IT/1.0.0/TEST.png",
            "${base}IT/1.3.0/TEST.png",
            "${base}IT/1.0.0/VAC.png",
            "${base}IT/1.3.0/VAC.png",
            "${base}IT/1.0.0/REC.png",
            "${base}IT/1.3.0/REC.png",
            "${base}IT/1.3.0/specialcases/TEST_NAAT.png",
            "${base}IT/1.0.0/specialcases/VAC_DOSE_1.png",
            "${base}IT/1.3.0/specialcases/VAC_DOSE_1.png",
            "${base}LI/1.1.0/TEST.png",
            "${base}LI/1.3.0/TEST.png",
            "${base}LI/1.1.0/VAC.png",
            "${base}LI/1.3.0/VAC.png",
            "${base}LI/1.1.0/REC.png",
            "${base}LI/1.3.0/REC.png",
            "${base}LT/1.0.0/TEST.png",
            "${base}LT/1.3.0/TEST-NAAT.png",
            "${base}LT/1.3.0/TEST-RAT.png",
            "${base}LT/1.0.0/VAC.png",
            "${base}LT/1.3.0/VAC.png",
            "${base}LT/1.0.0/REC.png",
            "${base}LT/1.3.0/REC.png",
            "${base}LU/1.3.0/TEST_NAAT.png",
            "${base}LU/1.3.0/TEST_RAT.png",
            "${base}LU/1.3.0/VAC_standard.png",
            "${base}LU/1.3.0/REC_standard.png",
            "${base}LU/1.3.0/specialcases/VAC_noday.png",
            "${base}LU/1.3.0/specialcases/VAC_nomonth.png",
            "${base}LU/1.3.0/specialcases/VAC_noyear.png",
            "${base}LU/1.3.0/specialcases/REC_nomonth.png",
            "${base}LV/1.0.0/TEST.png",
            "${base}LV/1.3.0/TEST.png",
            "${base}LV/1.0.0/VAC.png",
            "${base}LV/1.0.0/VAC2.png",
            "${base}LV/1.3.0/VAC.png",
            "${base}LV/1.0.0/REC.png",
            "${base}LV/1.3.0/REC.png",
            "${base}LV/1.0.0/specialcases/TEST_NULL_values_in_nm_ma.png",
            "${base}LV/1.0.0/specialcases/TEST_without_issuer.png",
            "${base}LV/1.3.0/specialcases/TEST_in_other_country.png",
            "${base}LV/1.0.0/specialcases/VAC_not_allowed_characters_in_fnt.png",
            "${base}LV/1.3.0/specialcases/VAC_in_other_country.png",
            "${base}LV/1.0.0/specialcases/REC_df_minu_fr_equals_9.png",
            "${base}LV/1.3.0/specialcases/REC_in_other_country.png",
            "${base}MK/1.3.0/TEST.png",
            "${base}MK/1.3.0/VAC.png",
            "${base}MK/1.3.0/REC.png",
            "${base}MT/1.3.0/VAC.png",
            "${base}NL/1.3.0/TEST.png",
            "${base}NL/1.0.0/VAC.png",
            "${base}NL/1.3.0/VAC.png",
            "${base}NL/1.3.0/VAC_AW.png",
            "${base}NL/1.3.0/VAC_CW.png",
            "${base}NL/1.3.0/VAC_SX.png",
            "${base}NL/1.3.0/REC.png",
            "${base}NL/1.3.0/specialcases/VAC_JANSSEN_2_of_1.png",
            "${base}NL/1.3.0/specialcases/VAC_PFIZER_1_of_1.png",
            "${base}NL/1.3.0/specialcases/VAC_PFIZER_3_of_2.png",
            "${base}NL/1.3.0/specialcases/VAC_PFIZER_3_of_3.png",
            "${base}NO/1.3.0/TEST.png",
            "${base}NO/1.3.0/VAC.png",
            "${base}NO/1.3.0/REC.png",
            "${base}NO/1.3.0/specialcases/VAC_NONDIGITALCITIZEN.png",
            "${base}NO/1.3.0/specialcases/REC_NONDIGITALCITIZEN.png",
            "${base}PL/1.0.0/TEST.png",
            "${base}PL/1.0.0/VAC.png",
            "${base}PL/1.0.0/REC.png",
            "${base}PL/1.0.0/specialcases/VAC-11.png",
            "${base}PL/1.0.0/specialcases/VAC-12.png",
            "${base}PL/1.0.0/specialcases/VAC-13.png",
            "${base}PT/1.0.0/TEST.png",
            "${base}PT/1.3.0/TEST.png",
            "${base}PT/1.0.0/VAC.png",
            "${base}PT/1.3.0/VAC.png",
            "${base}PT/1.0.0/REC.png",
            "${base}PT/1.3.0/REC.png",
            "${base}RO/1.3.0/TEST.png",
            "${base}RO/1.3.0/VAC.png",
            "${base}RO/1.3.0/REC.png",
            "${base}RO/1.3.0/specialcases/VAC-11.png",
            "${base}RO/1.3.0/specialcases/VAC-12.png",
            "${base}SE/1.3.0/TEST.png",
            "${base}SE/1.3.0/VAC.png",
            "${base}SE/1.3.0/REC.png",
            "${base}SI/1.0.0/TEST.png",
            "${base}SI/1.3.0/test-AG.png",
            "${base}SI/1.3.0/test-PCR.png",
            "${base}SI/1.0.0/VAC.png",
            "${base}SI/1.3.0/VAC.png",
            "${base}SI/1.0.0/REC.png",
            "${base}SI/1.3.0/REC.png",
            "${base}SK/1.3.0/TEST.png",
            "${base}SK/1.2.1/VAC.png",
            "${base}SK/1.2.1/REC.png",
            "${base}SM/1.3.0/TEST.png",
            "${base}SM/1.3.0/VAC.png",
            "${base}SM/1.3.0/REC.png",
            "${base}SM/1.3.0/specialcases/TEST.png",
            "${base}TR/1.3.0/TEST.png",
            "${base}TR/1.3.0/VAC.png",
            "${base}TR/1.3.0/REC.png",
            "${base}UA/1.3.0/TEST.png",
            "${base}UA/1.3.0/VAC.png",
            "${base}UA/1.3.0/REC.png",
            "${base}VA/1.3.0/TEST.png",
            "${base}VA/1.3.0/VAC.png",
            "${base}VA/1.3.0/REC.png",
            "${base}TSI/1.3.0/TEST_1.png",
            "${base}TSI/1.3.0/TEST_2.png",
            "${base}TSI/1.3.0/TEST_3.png",
            "${base}TSI/1.3.0/TEST_4.png",
            "${base}TSI/1.3.0/VAC_1.png",
            "${base}TSI/1.3.0/VAC_2.png",
            "${base}TSI/1.3.0/VAC_3.png",
            "${base}TSI/1.3.0/VAC_4.png",
            "${base}TSI/1.3.0/VAC_5.png",
            "${base}TSI/1.3.0/REC_1.png",
            "${base}TSI/1.3.0/REC_2.png",
            "${base}TSI/1.3.0/MULTI_1.png",
            "${base}TSI/1.3.0/MULTI_2.png"
        )

    }
}
