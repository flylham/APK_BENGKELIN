package com.bengkelin.app.ui.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bengkelin.app.data.model.User
import com.bengkelin.app.data.model.UserRole
import com.bengkelin.app.ui.theme.BengkelinNavy
import com.bengkelin.app.ui.theme.BengkelinOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleHomeScreen(
    user: User?,
    onNavigateToWorkshopSearch: () -> Unit = {},
    onNavigateToEstimator: () -> Unit = {},
    onLogout: () -> Unit
) {
    val role = user?.userRole ?: UserRole.PENGGUNA
    val context = LocalContext.current
    var showSosDialog by remember { mutableStateOf(false) }
    var selectedVehicle by remember { mutableStateOf("Motor") }
    var selectedIssue by remember { mutableStateOf("Ban Bocor / Kempes") }
    var locationNotes by remember { mutableStateOf("") }

    val emergencyPhone = "+6285606401220"
    val rawWaNumber = "6285606401220"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Bengkelin", fontWeight = FontWeight.Bold)
                        Text(
                            text = "${user?.region ?: "Malang Raya"} • ${role.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Keluar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BengkelinNavy),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Halo, ${user?.name ?: "Pengguna Bengkelin"}!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Email: ${user?.email ?: "-"}",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Telepon: ${user?.phoneNumber ?: "-"}",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    if (role == UserRole.PEMILIK_BENGKEL && !user?.workshopName.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Nama Bengkel: ${user?.workshopName}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BengkelinOrange
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Role specific features
            when (role) {
                UserRole.PENGGUNA -> {
                    FeatureCard(
                        title = "Pencarian Bengkel Terdekat (Google Maps)",
                        desc = "Temukan bengkel motor & mobil terdekat di Kota Malang, Kab Malang, dan Kota Batu.",
                        icon = Icons.Default.Search,
                        onClick = onNavigateToWorkshopSearch
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FeatureCard(
                        title = "Panggilan Montir Darurat (SOS 24 Jam)",
                        desc = "Montir darurat jemput ke lokasi di Malang Raya via WhatsApp $emergencyPhone.",
                        icon = Icons.Default.Emergency,
                        onClick = { showSosDialog = true }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FeatureCard(
                        title = "Estimator Biaya & Cek Tanjakan Batu",
                        desc = "Hitung estimasi biaya servis transparan & tes keamanan rem tanjakan Klemuk/Cangar.",
                        icon = Icons.Default.Calculate,
                        onClick = onNavigateToEstimator
                    )
                }
                UserRole.PEMILIK_BENGKEL -> {
                    FeatureCard(
                        title = "Status Verifikasi Bengkel",
                        desc = if (user?.isVerified == true) "Bengkel aktif dan dapat dicari di Malang Raya." else "Sedang dalam proses verifikasi oleh Admin Bengkelin.",
                        icon = Icons.Default.Storefront
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FeatureCard(
                        title = "Kelola Layanan & Montir",
                        desc = "Atur jenis servis, ganti oli, tune-up, dan tarif bengkel Anda.",
                        icon = Icons.Default.Settings
                    )
                }
                UserRole.ADMIN -> {
                    FeatureCard(
                        title = "Verifikasi Bengkel Baru",
                        desc = "Validasi bengkel motor & mobil pendaftar baru di Malang Raya.",
                        icon = Icons.Default.AdminPanelSettings
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FeatureCard(
                        title = "Manajemen Pengguna & Wilayah",
                        desc = "Kelola data pengguna Kota Malang, Kab Malang, dan Kota Batu.",
                        icon = Icons.Default.Assessment
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Keluar dari Akun")
            }
        }
    }

    // DIALOG SOS 24 JAM TERHUBUNG KE WHATSAPP +6285606401220
    if (showSosDialog) {
        val problems = listOf(
            "Ban Bocor / Kempes",
            "Aki Mati / Jumper",
            "Mesin Mogok / Overheat",
            "Kehabisan Bensin",
            "Derek / Towing Darurat"
        )

        AlertDialog(
            onDismissRequest = { showSosDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Emergency,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Layanan Montir Darurat (SOS 24 Jam)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Malang Raya • Respon Cepat via WhatsApp",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Badge Kontak WhatsApp
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(18.dp))
                            Column {
                                Text(text = "WhatsApp Montir Siaga 24 Jam:", fontSize = 11.sp, color = Color(0xFF166534))
                                Text(text = emergencyPhone, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                            }
                        }
                    }

                    // Pilih Jenis Kendaraan
                    Text(text = "Pilih Kendaraan:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedVehicle == "Motor",
                            onClick = { selectedVehicle = "Motor" },
                            label = { Text("🛵 Motor") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = selectedVehicle == "Mobil",
                            onClick = { selectedVehicle = "Mobil" },
                            label = { Text("🚗 Mobil") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Pilih Kendala
                    Text(text = "Kendala Darurat:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        problems.forEach { problem ->
                            FilterChip(
                                selected = selectedIssue == problem,
                                onClick = { selectedIssue = problem },
                                label = { Text(problem, fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Catatan Lokasi
                    OutlinedTextField(
                        value = locationNotes,
                        onValueChange = { locationNotes = it },
                        label = { Text("Patokan Lokasi di Malang Raya", fontSize = 12.sp) },
                        placeholder = { Text("Contoh: Dekat SPBU Suhat / Jl. Ijen", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = false,
                        maxLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val message = "🚨 *DARURAT SOS 24 JAM BENGKELIN MALANG RAYA*\n\n" +
                                "👤 *Nama Pengendara:* ${user?.name ?: "Pengguna Bengkelin"}\n" +
                                "📞 *Nomor HP:* ${user?.phoneNumber ?: "-"}\n" +
                                "🚗 *Jenis Kendaraan:* $selectedVehicle\n" +
                                "⚠️ *Kendala:* $selectedIssue\n" +
                                "📍 *Wilayah:* ${user?.region ?: "Malang Raya"}\n" +
                                "📝 *Patokan Lokasi:* ${if (locationNotes.isNotBlank()) locationNotes else "Sekitar ${user?.region ?: "Malang"}"}\n\n" +
                                "Mohon segera kirimkan montir darurat ke lokasi saya. Terima kasih."

                        val encodedMessage = Uri.encode(message)
                        val waUrl = "https://api.whatsapp.com/send?phone=$rawWaNumber&text=$encodedMessage"

                        val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl)).apply {
                            setPackage("com.whatsapp")
                        }

                        try {
                            context.startActivity(waIntent)
                        } catch (e: Exception) {
                            // Fallback jika WhatsApp native belum ada di emulator: buka di browser / wa.me
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl))
                            context.startActivity(browserIntent)
                        }

                        showSosDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Kirim SOS ke WhatsApp (+6285606401220)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSosDialog = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

@Composable
private fun FeatureCard(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BengkelinOrange.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = BengkelinOrange,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
            }
        }
    }
}
