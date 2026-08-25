package com.marketplace.onehour.helper.presentation.kyc

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marketplace.onehour.common.components.AppTopBar
import com.marketplace.onehour.common.components.PrimaryButton
import com.marketplace.onehour.common.theme.SuccessGreen
import com.marketplace.onehour.common.theme.WarningAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycUploadScreen(
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    viewModel: KycViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var docDropdownExpanded by remember { mutableStateOf(false) }

    val docOptions = remember {
        listOf(
            "Aadhaar / National ID Card",
            "Driver's License",
            "Passport"
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "KYC & ID Verification",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PrimaryButton(
                        text = "Next: Rates & Schedule →",
                        onClick = onNextClick,
                        enabled = state.isConsentChecked && state.frontDocName != null
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Stepper Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "STEP 3 OF 4", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(text = "Identity Verification", fontSize = 12.sp, color = Color.Gray)
            }
            LinearProgressIndicator(
                progress = 0.75f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Gray.copy(alpha = 0.2f)
            )

            // Status Banner Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WarningAmber.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = WarningAmber)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "KYC Status: Pending Submission", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = WarningAmber)
                        Text(text = "Upload government ID to activate instant booking eligibility", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }

            // Document Type Dropdown
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Select Document Type", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                ExposedDropdownMenuBox(
                    expanded = docDropdownExpanded,
                    onExpandedChange = { docDropdownExpanded = !docDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = state.selectedDocType,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = docDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = docDropdownExpanded,
                        onDismissRequest = { docDropdownExpanded = false }
                    ) {
                        docOptions.forEach { doc ->
                            DropdownMenuItem(
                                text = { Text(doc) },
                                onClick = {
                                    viewModel.onDocTypeSelected(doc)
                                    docDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Document Front Upload Card
            DocumentUploadCard(
                title = "Front Side of Document",
                fileName = state.frontDocName,
                onUploadClick = { viewModel.setFrontDoc("aadhaar_front_scan.jpg") }
            )

            // Document Back Upload Card
            DocumentUploadCard(
                title = "Back Side of Document",
                fileName = state.backDocName,
                onUploadClick = { viewModel.setBackDoc("aadhaar_back_scan.jpg") }
            )

            // Consent Checkbox Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { viewModel.toggleConsent(!state.isConsentChecked) }
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.isConsentChecked,
                    onCheckedChange = { viewModel.toggleConsent(it) }
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "I consent to background verification & safety check per 1-Hour platform rules.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DocumentUploadCard(
    title: String,
    fileName: String?,
    onUploadClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUploadClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (fileName != null) SuccessGreen.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (fileName != null) Icons.Default.CheckCircle else Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = if (fileName != null) SuccessGreen else MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                        text = fileName ?: "Tap to select photo/PDF",
                        fontSize = 11.sp,
                        color = if (fileName != null) SuccessGreen else Color.Gray
                    )
                }
            }

            TextButton(onClick = onUploadClick) {
                Text(text = if (fileName != null) "Reupload" else "Upload", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
