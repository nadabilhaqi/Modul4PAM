package com.example.ticketorderscreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ticketorderscreen.ui.theme.TicketOrderScreenTheme
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicketOrderScreenTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TicketOrderScreen()
                }
            }
        }
    }
}

enum class OrderStatus {
    IDLE,
    NAMA_KOSONG,
    MEMPROSES,
    BERHASIL
}

@Composable
fun TicketOrderScreen() {
    var hargaTiket by remember { mutableIntStateOf(150_000) }
    var jumlahTiket by remember { mutableIntStateOf(1) }
    var namaPembeli by remember { mutableStateOf("") }
    var orderStatus by remember { mutableStateOf(OrderStatus.IDLE) }

    var submitTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(submitTrigger) {
        if (submitTrigger == 0) return@LaunchedEffect

        if (namaPembeli.isBlank()) {
            orderStatus = OrderStatus.NAMA_KOSONG
        } else {
            orderStatus = OrderStatus.MEMPROSES
            delay(5000)
            orderStatus = OrderStatus.BERHASIL
        }
    }

    TicketOrderContent(
        hargaTiket = hargaTiket,
        jumlahTiket = jumlahTiket,
        namaPembeli = namaPembeli,
        orderStatus = orderStatus,
        onNamaChange = { newNama ->
            namaPembeli = newNama
            if (orderStatus == OrderStatus.NAMA_KOSONG) {
                orderStatus = OrderStatus.IDLE
            }
        },
        onJumlahMinus = { if (jumlahTiket > 1) jumlahTiket-- },
        onJumlahPlus = { jumlahTiket++ },
        onPesanClick = { submitTrigger++ }
    )
}

@Composable
fun TicketOrderContent(
    hargaTiket: Int,
    jumlahTiket: Int,
    namaPembeli: String,
    orderStatus: OrderStatus,
    onNamaChange: (String) -> Unit,
    onJumlahMinus: () -> Unit,
    onJumlahPlus: () -> Unit,
    onPesanClick: () -> Unit
) {
    val biruUtama = Color(0xFF1E4FD8)
    val isProcessing = orderStatus == OrderStatus.MEMPROSES

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(biruUtama)
                .padding(20.dp)
        ) {
            Text(
                "Pemesanan Tiket",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column {
                Text("Nama", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = namaPembeli,
                    onValueChange = onNamaChange,
                    placeholder = { Text("Masukkan nama Anda") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column {
                Text("Jumlah Tiket", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    JumlahButton(text = "-", onClick = onJumlahMinus)
                    Text(
                        "$jumlahTiket",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    JumlahButton(text = "+", onClick = onJumlahPlus)
                }
            }

            val totalHarga = hargaTiket * jumlahTiket
            Text(
                "Harga per tiket: ${formatRupiah(hargaTiket)}  •  Total: ${formatRupiah(totalHarga)}",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Button(
                onClick = onPesanClick,
                enabled = !isProcessing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = biruUtama)
            ) {
                Text("Pesan Tiket", fontWeight = FontWeight.Bold)
            }

            StatusBox(orderStatus)
        }
    }
}

@Composable
private fun JumlahButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(Color(0xFFE6E9F5), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        TextButton(onClick = onClick) {
            Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StatusBox(status: OrderStatus) {
    val (bg, fg, label) = when (status) {
        OrderStatus.IDLE -> Triple(Color(0xFFF0F1F5), Color.DarkGray, "Silakan pesan tiket")
        OrderStatus.NAMA_KOSONG -> Triple(Color(0xFFFDECEC), Color(0xFFD32F2F), "Nama harus diisi")
        OrderStatus.MEMPROSES -> Triple(Color(0xFFE8F0FE), Color(0xFF1E4FD8), "Memproses pesanan.........")
        OrderStatus.BERHASIL -> Triple(Color(0xFFE6F7ED), Color(0xFF1E8E3E), "Tiket berhasil dipesan!")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(10.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (status) {
            OrderStatus.NAMA_KOSONG -> Icon(Icons.Filled.Warning, contentDescription = null, tint = fg)
            OrderStatus.BERHASIL -> Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = fg)
            OrderStatus.MEMPROSES -> CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = fg
            )
            else -> {}
        }
        if (status != OrderStatus.IDLE) Spacer(Modifier.width(8.dp))
        Text("Status: ", color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
        Text(label, color = fg, fontWeight = FontWeight.SemiBold)
    }
}

private fun formatRupiah(value: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
    format.maximumFractionDigits = 0
    return format.format(value)
}

@Preview(showBackground = true)
@Composable
fun TicketOrderScreenPreview() {
    TicketOrderScreenTheme {
        TicketOrderScreen()
    }
}
