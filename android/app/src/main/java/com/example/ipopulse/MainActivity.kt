package com.example.ipopulse
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class Wrap<T>(val data:T)
data class Sub(val qib:Double,val nii:Double,val retail:Double,val employee:Double,val overall:Double)
data class Dates(val open:String,val close:String,val allotment:String,val refund:String,val listing:String)
data class Ipo(val id:String,val name:String,val symbol:String,val type:String,val status:String,val priceLow:Double,val priceHigh:Double,val lotSize:Int,val gmp:Double,val gmpUpdatedAt:String,val subscription:Sub,val issueSize:String,val dates:Dates,val description:String,val registrar:String)
interface Api{@GET("api/ipos") suspend fun all():Wrap<List<Ipo>>}
class Vm:androidx.lifecycle.ViewModel(){var data by mutableStateOf<List<Ipo>>(emptyList());var loading by mutableStateOf(true);init{refresh()};fun refresh(){androidx.lifecycle.viewModelScope.launch{try{data=Retrofit.Builder().baseUrl(BuildConfig.API_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build().create(Api::class.java).all().data}catch(_:Exception){};loading=false}}}
@Composable fun App(vm:Vm=viewModel()){val nav=rememberNavController();MaterialTheme{NavHost(nav,"home"){composable("home"){Home(vm){nav.navigate("d/${it.id}")}};composable("d/{id}"){b->vm.data.firstOrNull{it.id==b.arguments?.getString("id")}?.let{Detail(it)}}}}}
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun Home(vm:Vm,open:(Ipo)->Unit){Scaffold(topBar={TopAppBar(title={Text("IPO Pulse")})}){p->if(vm.loading)CircularProgressIndicator(Modifier.padding(p).padding(24.dp))else LazyColumn(Modifier.padding(p).padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){item{Text("GMP • Subscription • Allotment",style=MaterialTheme.typography.titleMedium)};items(vm.data){x->Card(Modifier.fillMaxWidth().clickable{open(x)}){Column(Modifier.padding(16.dp)){Text(x.name,style=MaterialTheme.typography.titleLarge);Text("${x.status} • ${x.type}");Text("₹${x.priceLow.toInt()} – ₹${x.priceHigh.toInt()}");Text("GMP ₹${x.gmp.toInt()}  •  ${"%.2f".format(x.gmp/x.priceHigh*100)}%");Text("Est. gain / lot ₹${(x.gmp*x.lotSize).toInt()}");Text("Lot ${x.lotSize} • Subscription ${x.subscription.overall}×");Text("GMP updated ${x.gmpUpdatedAt.take(16).replace("T"," ")}")}}}}}}
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun Detail(x:Ipo){Scaffold(topBar={TopAppBar(title={Text(x.name)})}){p->LazyColumn(Modifier.padding(p).padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){item{Text("₹${x.priceLow.toInt()} – ₹${x.priceHigh.toInt()}",style=MaterialTheme.typography.headlineSmall)};item{Text("GMP ₹${x.gmp.toInt()} • Estimated listing ₹${(x.priceHigh+x.gmp).toInt()}")};item{Text("Estimated gain / lot ₹${(x.gmp*x.lotSize).toInt()} • Investment / lot ₹${(x.priceHigh*x.lotSize).toInt()}")};item{Text("Subscription",style=MaterialTheme.typography.titleLarge)};item{Text("QIB ${x.subscription.qib}×   NII ${x.subscription.nii}×\nRetail ${x.subscription.retail}×   Overall ${x.subscription.overall}×")};item{Text("IPO Details",style=MaterialTheme.typography.titleLarge)};item{Text("Issue size: ${x.issueSize}\nRegistrar: ${x.registrar}\n\n${x.description}")};item{Text("Important Dates",style=MaterialTheme.typography.titleLarge)};item{Text("Open: ${x.dates.open}\nClose: ${x.dates.close}\nAllotment: ${x.dates.allotment}\nRefund: ${x.dates.refund}\nListing: ${x.dates.listing}")};item{Text("GMP is unofficial and may change. Estimated gain is not a return guarantee.",style=MaterialTheme.typography.bodySmall)}}}}
class MainActivity:ComponentActivity(){override fun onCreate(b:Bundle?){super.onCreate(b);setContent{App()}}}
