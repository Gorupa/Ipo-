package com.example.iponinja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Ipo(
    val name: String,
    val dates: String,
    val price: String,
    val lot: String,
    val gmp: String,
    val gain: String,
    val subscription: String
)

val demoIpos = listOf(
    Ipo("Demo Technologies IPO", "Open • 24–26 Sep", "₹720–760", "19 shares", "₹125", "₹2,375", "18.4×"),
    Ipo("Demo Healthcare IPO", "Upcoming • 30 Sep", "₹410–430", "34 shares", "₹70", "₹2,380", "—"),
    Ipo("Demo Retail IPO", "Upcoming • 03 Oct", "₹280–295", "50 shares", "₹42", "₹2,100", "—")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { IpoNinjaApp() }
    }
}

@Composable
fun IpoNinjaApp() {
    var selected by remember { mutableStateOf<Ipo?>(null) }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF315CFF),
            background = Color(0xFFF7F8FA),
            surface = Color.White,
            onSurface = Color(0xFF17191D),
            onSurfaceVariant = Color(0xFF70747C)
        )
    ) {
        Surface(Modifier.fillMaxSize(), color = Color(0xFFF7F8FA)) {
            if (selected == null) {
                HomeScreen { selected = it }
            } else {
                DetailScreen(selected!!) { selected = null }
            }
        }
    }
}

@Composable
fun HomeScreen(onIpoClick: (Ipo) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(38.dp).clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF315CFF)),
                    contentAlignment = Alignment.Center
                ) { Text("N", color = Color.White, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("IPO Ninja", fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Text("Know the IPO. Decide yourself.", color = Color(0xFF777B83), fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(22.dp))
            Text("Today", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("A calm view of what's happening in IPOs.", color = Color(0xFF777B83), fontSize = 14.sp)
            Spacer(Modifier.height(14.dp))
        }

        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Summary("Open", "1")
                    Summary("Upcoming", "2")
                    Summary("Watchlist", "0")
                }
            }
        }

        item { Text("Open & upcoming", fontSize = 19.sp, fontWeight = FontWeight.SemiBold) }

        items(demoIpos) { ipo ->
            Card(
                Modifier.fillMaxWidth().clickable { onIpoClick(ipo) },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(Modifier.padding(17.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text(ipo.name, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                            Text(ipo.dates, color = Color(0xFF777B83), fontSize = 12.sp)
                        }
                        Text(ipo.gmp, color = Color(0xFF16865A), fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(15.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Mini("Price", ipo.price)
                        Mini("Lot", ipo.lot)
                        Mini("Est. gain", ipo.gain)
                    }
                    Spacer(Modifier.height(13.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subscription ${ipo.subscription}", color = Color(0xFF777B83), fontSize = 12.sp)
                        Text("View details  ›", color = Color(0xFF315CFF), fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(10.dp))
            Text(
                "GMP is unofficial market information and may change. IPO Ninja does not guarantee listing gains.",
                color = Color(0xFF8A8D94), fontSize = 11.sp, lineHeight = 16.sp
            )
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
fun Summary(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Color(0xFF777B83), fontSize = 11.sp)
    }
}

@Composable
fun Mini(label: String, value: String) {
    Column {
        Text(label, color = Color(0xFF8A8D94), fontSize = 10.sp)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun DetailScreen(ipo: Ipo, onBack: () -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(Modifier.height(20.dp))
            TextButton(onClick = onBack) { Text("‹  Back") }
            Text(ipo.name, fontSize = 27.sp, fontWeight = FontWeight.Bold)
            Text(ipo.dates, color = Color(0xFF777B83), fontSize = 13.sp)
            Spacer(Modifier.height(12.dp))
        }
        item {
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text("GMP", color = Color(0xFF777B83), fontSize = 12.sp)
                    Text(ipo.gmp, color = Color(0xFF16865A), fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    Text("Unofficial estimate • demo data", color = Color(0xFF8A8D94), fontSize = 11.sp)
                }
            }
        }
        item { DetailRow("Price band", ipo.price) }
        item { DetailRow("Lot size", ipo.lot) }
        item { DetailRow("Estimated gain / lot", ipo.gain) }
        item { DetailRow("Subscription", ipo.subscription) }
        item {
            Spacer(Modifier.height(12.dp))
            Text(
                "IPO Ninja is an information tool. GMP and estimated gains are not guaranteed and should not be treated as investment advice.",
                color = Color(0xFF777B83), fontSize = 12.sp, lineHeight = 17.sp
            )
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFF777B83), fontSize = 13.sp)
        Text(value, fontWeight = FontWeight.Medium, fontSize = 13.sp)
    }
}
