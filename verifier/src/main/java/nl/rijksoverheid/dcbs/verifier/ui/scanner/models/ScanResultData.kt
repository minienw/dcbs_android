package nl.rijksoverheid.dcbs.verifier.ui.scanner.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ScanResultData(val verifiedQr: VerifiedQr?) : Parcelable
