package com.despreschen.mygoodaddresses.ui.add

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.despreschen.mygoodaddresses.MyGoodAddressesApplication
import com.despreschen.mygoodaddresses.R
import com.despreschen.mygoodaddresses.location.DeviceLocation
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRestaurantScreen(
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddRestaurantViewModel = viewModel(factory = AddRestaurantViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val photoStorage = remember(context) {
        (context.applicationContext as MyGoodAddressesApplication).container.photoStorage
    }

    // The file the camera is currently writing into, if any.
    var pendingPhoto by remember { mutableStateOf<File?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { captured ->
        val file = pendingPhoto
        pendingPhoto = null
        if (captured && file != null) {
            viewModel.onPhotoCaptured(file.absolutePath)
        } else if (file != null) {
            // Cancelled: the empty target file would otherwise linger.
            viewModel.onPhotoDiscarded(file)
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { granted ->
        if (granted.values.any { it }) viewModel.useCurrentLocation()
    }

    LaunchedEffect(state.savedId) {
        if (state.savedId != null) onSaved()
    }

    val problemMessage = state.locationProblem?.let { stringResource(it.messageRes()) }
    LaunchedEffect(problemMessage) {
        if (problemMessage != null) {
            snackbarHostState.showSnackbar(problemMessage)
            viewModel.onProblemShown()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_restaurant)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PhotoPicker(
                photoPath = state.photoPath,
                onTakePhoto = {
                    val (file, uri) = photoStorage.newPhotoTarget()
                    pendingPhoto = file
                    cameraLauncher.launch(uri)
                },
            )

            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = { Text(stringResource(R.string.restaurant_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.type,
                onValueChange = viewModel::onTypeChange,
                label = { Text(stringResource(R.string.restaurant_type)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedButton(
                onClick = {
                    if (viewModel.needsLocationPermission()) {
                        locationPermissionLauncher.launch(DeviceLocation.PERMISSIONS)
                    } else {
                        viewModel.useCurrentLocation()
                    }
                },
                enabled = !state.isLocating,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isLocating) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Icon(imageVector = Icons.Default.MyLocation, contentDescription = null)
                }
                Text(
                    text = stringResource(R.string.use_my_location),
                    modifier = Modifier.padding(start = 8.dp),
                )
            }

            OutlinedTextField(
                value = state.addressLine,
                onValueChange = viewModel::onAddressLineChange,
                label = { Text(stringResource(R.string.address_line)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = state.postalCode,
                    onValueChange = viewModel::onPostalCodeChange,
                    label = { Text(stringResource(R.string.postal_code)) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = state.city,
                    onValueChange = viewModel::onCityChange,
                    label = { Text(stringResource(R.string.city)) },
                    singleLine = true,
                    modifier = Modifier.weight(2f),
                )
            }

            Button(
                onClick = viewModel::save,
                enabled = state.canSave,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}

@Composable
private fun PhotoPicker(
    photoPath: String?,
    onTakePhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onTakePhoto),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        if (photoPath == null) {
            Box(contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = null)
                    Text(
                        text = stringResource(R.string.take_photo),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        } else {
            AsyncImage(
                model = photoPath,
                contentDescription = stringResource(R.string.restaurant_photo),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

private fun LocationProblem.messageRes(): Int = when (this) {
    LocationProblem.PermissionDenied -> R.string.location_permission_needed
    LocationProblem.NoFix -> R.string.location_no_fix
    LocationProblem.NoAddress -> R.string.location_no_address
}
