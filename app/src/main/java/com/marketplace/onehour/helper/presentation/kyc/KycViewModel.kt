package com.marketplace.onehour.helper.presentation.kyc

import androidx.lifecycle.ViewModel
import com.marketplace.onehour.helper.presentation.onboarding.HelperOnboardingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class KycViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(KycState())
    val uiState: StateFlow<KycState> = _uiState.asStateFlow()

    init {
        HelperOnboardingRepository.kycDocumentType = _uiState.value.selectedDocType
        HelperOnboardingRepository.kycDocumentUrl = _uiState.value.frontDocName ?: ""
    }

    fun onDocTypeSelected(docType: String) {
        _uiState.value = _uiState.value.copy(selectedDocType = docType)
        HelperOnboardingRepository.kycDocumentType = docType
    }

    fun toggleConsent(checked: Boolean) {
        _uiState.value = _uiState.value.copy(isConsentChecked = checked)
    }

    // No real file storage backend (S3/Cloudinary) is wired up yet — the
    // filename is passed through as document_url so the KYC record still
    // gets created with something meaningful, not because it's a real file URL.
    fun setFrontDoc(fileName: String) {
        _uiState.value = _uiState.value.copy(frontDocName = fileName)
        HelperOnboardingRepository.kycDocumentUrl = fileName
    }

    fun setBackDoc(fileName: String) {
        _uiState.value = _uiState.value.copy(backDocName = fileName)
    }
}
