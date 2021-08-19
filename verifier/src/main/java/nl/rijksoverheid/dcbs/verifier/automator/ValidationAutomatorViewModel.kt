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
            TEST_FOLDERS.forEach { folder ->
                SCHEMES.forEach { scheme ->
                    val baseURL = "$MAIN_BASE_URL$folder/$scheme"
                    async { handle("$baseURL/REC.png?raw=true", mobileCoreWrapper) }
                    async { handle("$baseURL/VAC.png?raw=true", mobileCoreWrapper) }
                    async { handle("$baseURL/TEST.png?raw=true", mobileCoreWrapper) }
                }
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
        val item = url.replace(MAIN_BASE_URL, "").replace(".png?raw=true", "")
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
        val client = OkHttpClient()
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
        val TEST_FOLDERS = listOf(
            "AT", "BE", "BG", "CH", "CY", "CZ", "DE", "DK", "EE", "EL",
            "ES", "FI", "FR", "HR", "HU", "IE", "IS", "IT", "LI", "LT",
            "LU", "LV", "MK", "MT", "NL", "NO", "PL", "PT", "RO", "SE",
            "SI", "SK", "SM", "TR", "UA", "VA"
        )

        val SCHEMES = listOf("1.0.0", "1.0.1", "1.2.1", "1.3.0")


        const val MAIN_BASE_URL = "https://github.com/eu-digital-green-certificates/dcc-quality-assurance/blob/main/"

    }
}
