package com.marketplace.onehour.helper.presentation.kyc

data class KycState(
    val selectedDocType: String = "Aadhaar / National ID Card",
    val frontDocName: String? = "aadhaar_front_scan.jpg",
    val backDocName: String? = "aadhaar_back_scan.jpg",
    val isConsentChecked: Boolean = true,
    val verificationStatus: String = "PENDING_VERIFICATION",
    val isLoading: Boolean = false
)
