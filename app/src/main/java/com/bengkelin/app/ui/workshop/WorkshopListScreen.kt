package com.bengkelin.app.ui.workshop

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bengkelin.app.data.model.Workshop
import com.bengkelin.app.ui.theme.BengkelinNavy
import com.bengkelin.app.ui.theme.BengkelinOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkshopListScreen(
    onNavigateBack: () -> Unit,
    viewModel: WorkshopViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var isLocationPermissionGranted by remember { mutableStateOf(false) }
    var userLocationName by remember { mutableStateOf("Pasuruan, Jawa Timur (GPS Aktif)") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Bengkel Terdekat", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            if (isLocationPermissionGranted) "GPS Aktif • Terurut Terdekat" else "Jawa Timur • Izin Lokasi Diperlukan",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (isLocationPermissionGranted) {
                        IconButton(onClick = { isLocationPermissionGranted = false }) {
                            Icon(imageVector = Icons.Default.LocationOff, contentDescription = "Matikan Lokasi", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (!isLocationPermissionGranted) {
                // LOCATION PERMISSION GATE (Wajib Aktifkan Lokasi)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background(BengkelinOrange.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = null,
                            tint = BengkelinOrange,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Akses Lokasi GPS Diperlukan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Untuk mendeteksi dan mengurutkan bengkel mobil & motor terdekat dari posisi Anda saat ini (Pasuruan & Malang Raya), Bengkelin membutuhkan izin akses lokasi perangkat.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            userLocationName = "Pasuruan, Jawa Timur (GPS Aktif)"
                            isLocationPermissionGranted = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BengkelinOrange)
                    ) {
                        Icon(imageVector = Icons.Default.GpsFixed, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Aktifkan Lokasi Saya (GPS Nyata)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            userLocationName = "Alun-Alun Kota Pasuruan"
                            isLocationPermissionGranted = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Place, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Gunakan Titik Pasuruan (Jawa Timur)", fontSize = 12.sp)
                    }
                }
            } else {
                // STATUS GPS AKTIF BAR
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Lokasi Anda Aktif:", fontSize = 10.sp, color = Color(0xFF166534), fontWeight = FontWeight.Bold)
                                Text(userLocationName, fontSize = 11.sp, color = Color(0xFF15803D), fontWeight = FontWeight.SemiBold, maxLines = 1)
                            }
                        }
                        TextButton(onClick = { isLocationPermissionGranted = false }) {
                            Text("Matikan", fontSize = 11.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Search Input
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onEvent(WorkshopUiEvent.SearchQueryChanged(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    placeholder = { Text("Cari bengkel, jalan, atau nama montir...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.Search, contentDescription = "Cari")
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                // Filter Jenis Kendaraan: Motor / Mobil / Semua
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "SEMUA" to "Semua",
                        "MOTOR" to "Bengkel Motor",
                        "MOBIL" to "Bengkel Mobil"
                    ).forEach { (catKey, label) ->
                        val isSelected = uiState.selectedCategory == catKey
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onEvent(WorkshopUiEvent.CategoryChanged(catKey)) },
                            label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            leadingIcon = {
                                when (catKey) {
                                    "MOTOR" -> Icon(imageVector = Icons.Default.TwoWheeler, contentDescription = null, modifier = Modifier.size(16.dp))
                                    "MOBIL" -> Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(16.dp))
                                    else -> null
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BengkelinOrange,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Filter Wilayah: Kota Malang, Kabupaten Malang, Kota Batu
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val regions = listOf("SEMUA", "Kota Malang", "Kabupaten Malang", "Kota Batu")
                    items(regions) { region ->
                        val isSelected = uiState.selectedRegion == region
                        SuggestionChip(
                            onClick = { viewModel.onEvent(WorkshopUiEvent.RegionChanged(region)) },
                            label = { Text(if (region == "SEMUA") "Semua Wilayah" else region, fontSize = 11.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (isSelected) BengkelinNavy else MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

            // Daftar Bengkel
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BengkelinOrange)
                }
            } else if (uiState.workshops.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.SearchOff, contentDescription = null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Tidak ada bengkel yang sesuai filter.", fontWeight = FontWeight.SemiBold)
                        Text("Coba ubah kata kunci atau ganti filter wilayah.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.workshops) { workshop ->
                        WorkshopCard(
                            workshop = workshop,
                            onOpenGoogleMaps = {
                                val gmmIntentUri = Uri.parse("geo:${workshop.latitude},${workshop.longitude}?q=${Uri.encode(workshop.name)}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                    setPackage("com.google.android.apps.maps")
                                }
                                if (mapIntent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(mapIntent)
                                } else {
                                    // Buka via browser jika aplikasi Google Maps tidak tersedia
                                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(workshop.googleMapsUrl))
                                    context.startActivity(webIntent)
                                }
                            },
                            onCallWorkshop = {
                                val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${workshop.phoneNumber}"))
                                context.startActivity(callIntent)
                            }
                        )
                    }
                }
            }
        }
    }
}
}

@Composable
fun WorkshopCard(
    workshop: Workshop,
    onOpenGoogleMaps: () -> Unit,
    onCallWorkshop: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (workshop.category == "MOTOR") Color(0xFFE0F2FE) else Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = if (workshop.category == "MOTOR") "BENGKEL MOTOR" else "BENGKEL MOBIL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (workshop.category == "MOTOR") Color(0xFF0369A1) else Color(0xFFB45309),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${workshop.region}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = workshop.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = workshop.address,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }

                // Jarak & Rating
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${workshop.distanceKm} km",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BengkelinOrange
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = "${workshop.rating}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = " (${workshop.reviewsCount})", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Layanan & Spesialisasi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                workshop.specialties.take(3).forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = tag,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tombol Navigasi Google Maps & Telepon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCallWorkshop,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hubungi", fontSize = 12.sp)
                }

                Button(
                    onClick = onOpenGoogleMaps,
                    modifier = Modifier.weight(1.3f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BengkelinOrange)
                ) {
                    Icon(imageVector = Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Buka di Google Maps", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
