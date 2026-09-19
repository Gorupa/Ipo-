package com.example.iponinja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class DemoIpo(
    val id: String,
    val name: String,
    val symbol: String,
    val status: String,
    val type: String,
    val low: Int,
    val high: Int,
    val lot: Int,
    val gmp: Int,
    val subscription: Double,
    val issueSize: String,
    val open: String,
    val close: String,
    val allotment: String,
    val listing: String
)

private val demoIpos = listOf(
    DemoIpo("1","Example Technologies IPO","EXAMPLE","OPEN","Mainboard",700,735,20,85,2.91,"₹2,450 Cr","18 Sep 2026","22 Sep 2026","23 Sep 2026","25 Sep 2026"),
    DemoIpo("2","Sample Consumer IPO","SAMPLE","UPCOMING","Mainboard",420,445,33,52,0.0,"₹1,180 Cr","24 Sep 2026","28 Sep 2026","29 Sep 2026","1 Oct 2026")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { IpoNinjaApp() }
    }
}

@Composable
private fun IpoNinjaApp() {
    var selected by remember { mutableStateOf<DemoIpo?>(null) }
    if (selected == null) {
        HomeScreen(onOpen = { selected = it })
    } else {
        DetailScreen(ipo = selected!!, onBack = { selected = null })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(onOpen: (DemoIpo) -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("IPO Ninja") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text("IPO intelligence, simplified.", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(4.dp))
                Text("GMP • Subscription • Allotment", style = MaterialTheme.typography.bodyMedium)
            }
            item { Text("🔥 Live & Upcoming", style = MaterialTheme.typography.titleLarge) }

            items(demoIpos) { ipo ->
                IpoCard(ipo, onOpen)
            }

            item {
                Text(
                    "Demo data for the first UI build. GMP is unofficial and can change.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun IpoCard(ipo: DemoIpo, onOpen: (DemoIpo) -> Unit) {
    val gain = ipo.gmp * ipo.lot
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onOpen(ipo) },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(ipo.name, style = MaterialTheme.typography.titleLarge)
                    Text(ipo.symbol, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(
                    ipo.status,
                    color = if (ipo.status == "OPEN") Color(0xFF169B70) else MaterialTheme.colorScheme.primary
                )
            }
            Text("₹${ipo.low} – ₹${ipo.high}")
            Text("GMP ₹${ipo.gmp}  •  ${"%.1f".format(ipo.gmp.toDouble() / ipo.high * 100)}%",
                style = MaterialTheme.typography.titleMedium)
            Text("₹$gain estimated gain / lot", style = MaterialTheme.typography.titleMedium)
            HorizontalDivider()
            Text("Lot ${ipo.lot}  •  Subscription ${"%.2f".format(ipo.subscription)}×",
                style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailScreen(ipo: DemoIpo, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ipo.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("‹") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {
            item { Text("₹${ipo.low} – ₹${ipo.high}", style = MaterialTheme.typography.headlineMedium) }
            item { Text("GMP ₹${ipo.gmp}  •  ${"%.2f".format(ipo.gmp.toDouble() / ipo.high * 100)}%") }
            item { Text("Estimated listing ₹${ipo.high + ipo.gmp}", style = MaterialTheme.typography.titleLarge) }
            item { Text("Estimated gain / lot ₹${ipo.gmp * ipo.lot}") }
            item { Text("Investment / lot ₹${ipo.high * ipo.lot}") }

            item { Text("Subscription", style = MaterialTheme.typography.titleLarge) }
            item { Text("Overall ${"%.2f".format(ipo.subscription)}×") }

            item { Text("IPO details", style = MaterialTheme.typography.titleLarge) }
            item { Text("Issue size: ${ipo.issueSize}\nType: ${ipo.type}\nLot size: ${ipo.lot}") }

            item { Text("Important dates", style = MaterialTheme.typography.titleLarge) }
            item { Text("Open: ${ipo.open}\nClose: ${ipo.close}\nAllotment: ${ipo.allotment}\nListing: ${ipo.listing}") }

            item {
                Text(
                    "GMP is unofficial. Estimated listing price and gain are calculations, not guarantees.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
