package com.iponinja.app

import com.iponinja.app.BuildConfig

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

// ---------- Data ----------

data class Wrap<T>(val data: T)
data class Sub(val qib: Double, val nii: Double, val retail: Double, val employee: Double, val overall: Double)
data class Dates(val open: String, val close: String, val allotment: String, val refund: String, val listing: String)
data class Ipo(
    val id: String, val name: String, val symbol: String, val type: String, val status: String,
    val priceLow: Double, val priceHigh: Double, val lotSize: Int, val gmp: Double, val gmpUpdatedAt: String,
    val subscription: Sub, val issueSize: String, val dates: Dates, val description: String, val registrar: String
)

interface Api {
    @GET("api/ipos") suspend fun all(): Wrap<List<Ipo>>
}

private fun buildApi(): Api = Retrofit.Builder()
    .baseUrl(BuildConfig.API_BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(Api::class.java)

// ---------- Formatting helpers ----------

fun String.toFriendlyDateTime(): String = try {
    OffsetDateTime.parse(this).format(DateTimeFormatter.ofPattern("d MMM, h:mm a"))
} catch (e: Exception) {
    this.take(16).replace("T", " ")
}

fun String.toFriendlyDate(): String = try {
    LocalDate.parse(this).format(DateTimeFormatter.ofPattern("d MMM yyyy"))
} catch (e: Exception) {
    this
}

fun statusColor(status: String, closingSoon: Boolean): Color = when {
    status == "OPEN" && closingSoon -> StatusColors.ClosingSoon
    status == "OPEN" -> StatusColors.Open
    status == "UPCOMING" -> StatusColors.Upcoming
    else -> StatusColors.Closed
}

fun statusLabel(status: String, closingSoon: Boolean): String = when {
    status == "OPEN" && closingSoon -> "Closing soon"
    status == "OPEN" -> "Open"
    status == "UPCOMING" -> "Upcoming"
    else -> status.lowercase().replaceFirstChar { it.uppercase() }
}

fun isClosingSoon(x: Ipo): Boolean = try {
    x.status == "OPEN" && LocalDate.parse(x.dates.close).let {
        val days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), it)
        days in 0..1
    }
} catch (e: Exception) {
    false
}

// ---------- ViewModel ----------

private const val PREFS = "ipo_ninja_prefs"
private const val KEY_FAVORITES = "favorites"

class Vm(app: Application) : AndroidViewModel(app) {
    var data by mutableStateOf<List<Ipo>>(emptyList())
    var loading by mutableStateOf(true)
    var refreshing by mutableStateOf(false)
    var error by mutableStateOf(false)
    var favorites by mutableStateOf<Set<String>>(emptySet())

    private val prefs get() = getApplication<Application>().getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    init {
        favorites = prefs.getStringSet(KEY_FAVORITES, emptySet()).orEmpty().toSet()
        refresh(initial = true)
    }

    fun refresh(initial: Boolean = false) {
        viewModelScope.launch {
            if (initial) loading = true else refreshing = true
            error = false
            try {
                data = buildApi().all().data
            } catch (e: Exception) {
                error = true
            }
            loading = false
            refreshing = false
        }
    }

    fun toggleFavorite(id: String) {
        favorites = if (id in favorites) favorites - id else favorites + id
        prefs.edit().putStringSet(KEY_FAVORITES, favorites).apply()
    }
}

// ---------- App shell ----------

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IpoNinjaTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    App()
                }
            }
        }
    }
}

@Composable
fun App(vm: Vm = viewModel()) {
    val nav = rememberNavController()
    NavHost(nav, "home") {
        composable("home") { Home(vm) { nav.navigate("detail/${it.id}") } }
        composable("detail/{id}") { backEntry ->
            val id = backEntry.arguments?.getString("id")
            Detail(vm = vm, id = id, onBack = { nav.popBackStack() })
        }
    }
}

// ---------- Home ----------

private enum class Filter(val label: String) { ALL("All"), OPEN("Open"), CLOSING("Closing soon"), UPCOMING("Upcoming"), FAVORITES("Favorites") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(vm: Vm, open: (Ipo) -> Unit) {
    var filter by remember { mutableStateOf(Filter.ALL) }
    var query by remember { mutableStateOf("") }
    var searchOpen by remember { mutableStateOf(false) }

    val filtered = remember(vm.data, filter, query, vm.favorites) {
        vm.data
            .filter { x ->
                when (filter) {
                    Filter.ALL -> true
                    Filter.OPEN -> x.status == "OPEN" && !isClosingSoon(x)
                    Filter.CLOSING -> isClosingSoon(x)
                    Filter.UPCOMING -> x.status == "UPCOMING"
                    Filter.FAVORITES -> x.id in vm.favorites
                }
            }
            .filter { x ->
                query.isBlank() || x.name.contains(query, ignoreCase = true) || x.symbol.contains(query, ignoreCase = true)
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (searchOpen) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = { Text("Search IPOs") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text("IPO Ninja", fontWeight = FontWeight.SemiBold)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (searchOpen) query = ""
                        searchOpen = !searchOpen
                    }) {
                        Icon(if (searchOpen) Icons.Filled.Close else Icons.Filled.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { vm.refresh() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { pad ->
        PullToRefreshBox(
            isRefreshing = vm.refreshing,
            onRefresh = { vm.refresh() },
            modifier = Modifier.padding(pad).fillMaxSize()
        ) {
            when {
                vm.loading -> LoadingState()
                vm.error && vm.data.isEmpty() -> ErrorState { vm.refresh(initial = true) }
                else -> LazyColumn(
                    Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
                ) {
                    item {
                        Text("IPO intelligence, simplified.", style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "GMP  •  Subscription  •  Allotment",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(Filter.entries.toList()) { f ->
                                FilterChip(
                                    selected = filter == f,
                                    onClick = { filter = f },
                                    label = { Text(f.label) }
                                )
                            }
                        }
                    }
                    if (vm.error) item {
                        Text(
                            "Showing last loaded data — refresh failed.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (filtered.isEmpty()) {
                        item { EmptyState(filter) }
                    } else {
                        items(filtered, key = { it.id }) { x ->
                            IpoCard(
                                x = x,
                                isFavorite = x.id in vm.favorites,
                                onToggleFavorite = { vm.toggleFavorite(x.id) },
                                onClick = { open(x) }
                            )
                        }
                    }
                    item {
                        Text(
                            "GMP-based figures are estimates. GMP is unofficial and can change.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))
            Text("Loading IPOs…", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ErrorState(onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Could not load IPO data", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(
                "Check your connection or the Render API status.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRetry) { Text("Retry") }
        }
    }
}

@Composable
private fun EmptyState(filter: Filter) {
    Box(Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                when (filter) {
                    Filter.FAVORITES -> "No favorites yet"
                    else -> "No IPOs in this filter"
                },
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                when (filter) {
                    Filter.FAVORITES -> "Tap the heart on an IPO to save it here."
                    else -> "Try a different filter."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun IpoCard(x: Ipo, isFavorite: Boolean, onToggleFavorite: () -> Unit, onClick: () -> Unit) {
    val closingSoon = isClosingSoon(x)
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(x.name, style = MaterialTheme.typography.titleLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(x.symbol, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(statusLabel(x.status, closingSoon), statusColor(x.status, closingSoon))
                    IconButton(onClick = onToggleFavorite, modifier = Modifier.size(36.dp)) {
                        Icon(
                            if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text("₹${x.priceLow.toInt()} – ₹${x.priceHigh.toInt()}", style = MaterialTheme.typography.bodyMedium)

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("GMP", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "₹${x.gmp.toInt()}  (${"%.1f".format(x.gmp / x.priceHigh * 100)}%)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Est. gain / lot", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹${(x.gmp * x.lotSize).toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Lot ${x.lotSize}  •  Sub ${"%.2f".format(x.subscription.overall)}×", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Updated ${x.gmpUpdatedAt.toFriendlyDateTime()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun StatusBadge(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.16f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = color)
    }
}

// ---------- Detail ----------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Detail(vm: Vm, id: String?, onBack: () -> Unit) {
    val x = vm.data.firstOrNull { it.id == id }
    val isFavorite = id != null && id in vm.favorites

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(x?.name ?: "IPO details", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (x != null) {
                        IconButton(onClick = { vm.toggleFavorite(x.id) }) {
                            Icon(
                                if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { pad ->
        when {
            x != null -> DetailContent(x, Modifier.padding(pad))
            vm.loading -> LoadingState()
            else -> Box(Modifier.padding(pad).fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("IPO not found", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onBack) { Text("Go back") }
                }
            }
        }
    }
}

@Composable
private fun DetailContent(x: Ipo, modifier: Modifier = Modifier) {
    val closingSoon = isClosingSoon(x)
    LazyColumn(
        modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        item {
            Card(shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        StatusBadge(statusLabel(x.status, closingSoon), statusColor(x.status, closingSoon))
                        Text(x.type, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("₹${x.priceLow.toInt()} – ₹${x.priceHigh.toInt()}", style = MaterialTheme.typography.headlineSmall)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "GMP ₹${x.gmp.toInt()}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "(${"%.2f".format(x.gmp / x.priceHigh * 100)}%)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        "Updated ${x.gmpUpdatedAt.toFriendlyDateTime()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile("Est. listing", "₹${(x.priceHigh + x.gmp).toInt()}", Modifier.weight(1f))
                StatTile("Est. gain / lot", "₹${(x.gmp * x.lotSize).toInt()}", Modifier.weight(1f))
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile("Investment / lot", "₹${(x.priceHigh * x.lotSize).toInt()}", Modifier.weight(1f))
                StatTile("Lot size", "${x.lotSize} shares", Modifier.weight(1f))
            }
        }

        item { SectionCard("Subscription") {
            SubRow("QIB", x.subscription.qib)
            SubRow("NII", x.subscription.nii)
            SubRow("Retail", x.subscription.retail)
            SubRow("Employee", x.subscription.employee)
            HorizontalDivider(Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            SubRow("Overall", x.subscription.overall, emphasize = true)
        } }

        item { SectionCard("Important dates") {
            DateRow("Open", x.dates.open)
            DateRow("Close", x.dates.close)
            DateRow("Allotment", x.dates.allotment)
            DateRow("Refund", x.dates.refund)
            DateRow("Listing", x.dates.listing)
        } }

        item { SectionCard("IPO details") {
            DetailRow("Issue size", x.issueSize)
            DetailRow("Registrar", x.registrar)
            if (x.description.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(x.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } }

        item {
            Text(
                "GMP is unofficial. Estimated listing price and gain are calculations based on the displayed GMP, not guarantees.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier, shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            content()
        }
    }
}

@Composable
private fun SubRow(label: String, value: Double, emphasize: Boolean = false) {
    Column(Modifier.padding(vertical = 2.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = if (emphasize) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium)
            Text(
                "${"%.2f".format(value)}×",
                style = if (emphasize) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
                fontWeight = if (emphasize) FontWeight.SemiBold else FontWeight.Normal,
                color = if (emphasize) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { (value / 5.0).coerceIn(0.0, 1.0).toFloat() },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = if (emphasize) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    }
}

@Composable
private fun DateRow(label: String, date: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(date.toFriendlyDate(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
