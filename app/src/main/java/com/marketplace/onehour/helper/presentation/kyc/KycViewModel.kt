package com.marketplace.onehour.helper.presentation.kyc

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class KycViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(KycState())
    val uiState: StateFlow<KycState> = _uiState.asStateFlow()

    fun onDocTypeSelected(docType: String) {
        _uiState.value = _uiState.value.copy(selectedDocType = docType)
    }

    fun toggleConsent(checked: Boolean) {
        _uiState.value = _uiState.value.copy(isConsentChecked = checked)
    }

    fun setFrontDoc(fileName: String) {
        _uiState.value = _uiState.value.copy(frontDocName = fileName)
    }

    fun setBackDoc(fileName: String) {
        _uiState.value = _uiState.value.copy(backDocName = fileName)
    }
}
