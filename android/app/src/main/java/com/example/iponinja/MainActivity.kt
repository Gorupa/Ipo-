package com.example.iponinja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class Ipo(
    val name: String,
    val dates: String,
    val price: String,
    val lot: String,
    val gmp: String,
    val gain: String,
    val subscription: String
)

private val demoIpos = listOf(
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
private fun IpoNinjaApp() {
    var selected by remember { mutableStateOf<Ipo?>(null) }

    MaterialTheme(
        colorScheme = androidx.compose.material3.lightColorScheme(
            primary = Color(0xFF315CFF),
            onPrimary = Color.White,
            background = Color(0xFFF7F8FA),
            surface = Color.White,
            onSurface = Color(0xFF17191D),
            onSurfaceVariant = Color(0xFF6C7078)
        )
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF7F8FA)) {
            if (selected == null) {
                HomeScreen(onIpoClick = { selected = it })
            } else {
                DetailScreen(selected!!, onBack = { selected = null })
            }
        }
    }
}

@Composable
private fun HomeScreen(onIpoClick: (Ipo) -> Unit) {
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
                ) {
                    Text("N", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 19.sp)
                }
                Spacer(Modifier.size(10.dp))
                Column {
                    Text("IPO Ninja", fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Text("Know the IPO. Decide yourself.", color = Color(0xFF777B83), fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(22.dp))
            Text("Today", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(
                "A calm view of what's happening in IPOs.",
                color = Color(0xFF777B83),
                fontSize = 14.sp
            )
            Spacer(Modifier.height(14.dp))
        }

        item {
            SummaryCard()
        }

        item {
            Text("Open & upcoming", fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
        }

        items(demoIpos) { ipo ->
            IpoCard(ipo, onClick = { onIpoClick(ipo) })
        }

        item {
            Spacer(Modifier.height(28.dp))
            Text(
                "GMP is unofficial market information and may change. IPO Ninja does not guarantee listing gains.",
                color = Color(0xFF8A8D94),
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun SummaryCard() {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Stat("Open", "1")
            VerticalDivider(Modifier.height(42.dp), color = Color(0xFFE7E8EC))
            Stat("Upcoming", "2")
            VerticalDivider(Modifier.height(42.dp), color = Color(0xFFE7E8EC))
            Stat("Watchlist", "0")
        }
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Color(0xFF777B83), fontSize = 11.sp)
    }
}

@Composable
private fun IpoCard(ipo: Ipo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(17.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(Modifier.weight(1f)) {
                    Text(ipo.name, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text(ipo.dates, color = Color(0xFF777B83), fontSize = 12.sp)
                }
                Text(
                    ipo.gmp,
                    color = Color(0xFF16865A),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MiniStat("Price", ipo.price)
                MiniStat("Lot", ipo.lot)
                MiniStat("Est. gain", ipo.gain)
            }

            Spacer(Modifier.height(14.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Subscription ${ipo.subscription}", color = Color(0xFF777B83), fontSize = 12.sp)
                Text("View details  ›", color = Color(0xFF315CFF), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String) {
    Column {
        Text(label, color = Color(0xFF8A8D94), fontSize = 10.sp)
        Spacer(Modifier.height(2.dp))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DetailScreen(ipo: Ipo, onBack: () -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(Modifier.height(20.dp))
            TextButton(onClick = onBack) { Text("‹  Back") }
            Spacer(Modifier.height(4.dp))
            Text(ipo.name, fontSize = 27.sp, fontWeight = FontWeight.Bold)
            Text(ipo.dates, color = Color(0xFF777B83), fontSize = 13.sp)
            Spacer(Modifier.height(16.dp))
        }

        item {
            Card(
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

        item {
            DetailRow("Price band", ipo.price)
            DetailRow("Lot size", ipo.lot)
            DetailRow("Estimated gain / lot", ipo.gain)
            DetailRow("Subscription", ipo.subscription)
        }

        item {
            Spacer(Modifier.height(12.dp))
            Text(
                "IPO Ninja is an information tool. GMP and estimated gains are not guaranteed and should not be treated as investment advice.",
                color = Color(0xFF777B83),
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFF777B83), fontSize = 13.sp)
        Text(value, fontWeight = FontWeight.Medium, fontSize = 13.sp)
    }
}
