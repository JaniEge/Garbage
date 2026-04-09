package dk.soerensen.garbagev1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint

// ✅ Brug kun disse Firebase imports
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

import dk.soerensen.garbagev1.data.database.BinEntity
import dk.soerensen.garbagev1.data.database.ItemEntity
import dk.soerensen.garbagev1.domain.Theme
import dk.soerensen.garbagev1.ui.features.settings.SettingsViewModel
import dk.soerensen.garbagev1.ui.navigation.MainNavigation
import dk.soerensen.garbagev1.ui.theme.GarbageV1Theme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Kør én gang for at fylde databasen
        //seedBinsToFirebase()
        //seedItemsToFirebase()

        setContent {
            val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

            GarbageV1Theme(
                darkTheme = when (uiState.theme) {
                    Theme.DARK -> true
                    Theme.LIGHT -> false
                    Theme.SYSTEM -> isSystemInDarkTheme()
                }
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { paddingValues ->
                    MainNavigation(
                        modifier = Modifier.padding(paddingValues),
                        intent = intent
                    )
                }
            }
        }
    }

    private fun seedBinsToFirebase() {
        // ✅ Nu virker denne linje pga. de korrekte imports
        val db = Firebase.firestore
        val bins = listOf(
            BinEntity(id = "rest", title = "Restaffald", description = "Beskidt papir, bleer, støvsugerposer", imageUrl = "https://cdn.lomax.dk/v-638062831836100000/productimages/8856001_1_main.jpg?width=400&height=400&mode=pad"),
            BinEntity(id = "paper", title = "Papir", description = "Aviser, reklamer, kontorpapir", imageUrl = "https://cdn.lomax.dk/v-638062831836100000/productimages/8856002_1_main.jpg?width=400&height=400&mode=pad"),
            BinEntity(id = "cardboard", title = "Pap", description = "Papkasser, æsker, bølgepap", imageUrl = "https://cdn.lomax.dk/v-638062831836100000/productimages/8856002_1_main.jpg?width=400&height=400&mode=pad"),
            BinEntity(id = "plastic", title = "Plast", description = "Hård og blød plast, emballage", imageUrl = "https://cdn.lomax.dk/v-638062831836100000/productimages/8856003_1_main.jpg?width=400&height=400&mode=pad"),
            BinEntity(id = "metal", title = "Metal", description = "Dåser, sølvpapir, kapsler", imageUrl = "https://cdn.lomax.dk/v-638062831836100000/productimages/8856004_1_main.jpg?width=400&height=400&mode=pad"),
            BinEntity(id = "glass", title = "Glas", description = "Flasker, syltetøjsglas", imageUrl = "https://cdn.lomax.dk/v-638062831836100000/productimages/8856005_1_main.jpg?width=400&height=400&mode=pad"),
            BinEntity(id = "bio", title = "Madaffald", description = "Madrester, skræller, kaffegrums", imageUrl = "https://cdn.lomax.dk/v-638062831836100000/productimages/8856006_1_main.jpg?width=400&height=400&mode=pad"),
            BinEntity(id = "textile", title = "Tekstil", description = "Ødelagt tøj, stofrester", imageUrl = "https://cdn.lomax.dk/v-638062831836100000/productimages/8856008_1_main.jpg?width=400&height=400&mode=pad"),
            BinEntity(id = "food_carton", title = "Mad- & Drikkekartoner", description = "Mælkekartoner, juicekartoner", imageUrl = "https://cdn.lomax.dk/v-638062831836100000/productimages/8856007_1_main.jpg?width=400&height=400&mode=pad"),
            BinEntity(id = "farligt", title = "Farligt affald", description = "Batterier, kemikalier, lyspærer", imageUrl = "https://cdn.lomax.dk/v-638062831836100000/productimages/8856009_1_main.jpg?width=400&height=400&mode=pad")
        )
        bins.forEach { db.collection("bins").document(it.id).set(it) }
    }

    private fun seedItemsToFirebase() {
        val db = Firebase.firestore
        val items = listOf(
            ItemEntity(id = "maelk", title = "Mælkekarton", binId = "food_carton", description = "Husk at tømme den"),
            ItemEntity(id = "juice", title = "Juicekarton", binId = "food_carton", description = "Tømt for indhold"),
            ItemEntity(id = "pizza", title = "Pizzabakke", binId = "rest", description = "Beskidt pap skal i restaffald"),
            ItemEntity(id = "avis", title = "Avis", binId = "paper", description = "Tørt og rent papir"),
            ItemEntity(id = "banan", title = "Bananskræl", binId = "bio", description = "Grønt affald"),
            ItemEntity(id = "cola", title = "Coladåse", binId = "metal", description = "Dåser uden pant"),
            ItemEntity(id = "vin", title = "Vinflaske", binId = "glass", description = "Husk at fjerne proppen"),
            ItemEntity(id = "smor", title = "Smørbakke", binId = "plastic", description = "Skyllet for fedtstof"),
            ItemEntity(id = "batteri", title = "Batterier", binId = "farligt", description = "Små batterier i pose"),
            ItemEntity(id = "ble", title = "Brugt ble", binId = "rest", description = "Skal pakkes ind"),
            ItemEntity(id = "stovsug", title = "Støvsugerpose", binId = "rest", description = "Husk at lukke den"),
            ItemEntity(id = "tojs", title = "Gammel t-shirt", binId = "textile", description = "Hulleri eller pletter"),
            ItemEntity(id = "konserv", title = "Konservesdåse", binId = "metal", description = "Tømt og skyllet"),
            ItemEntity(id = "æg", title = "Æggebakke", binId = "cardboard", description = "Ren pap"),
            ItemEntity(id = "plastpose", title = "Plastikpose", binId = "plastic", description = "Både hård og blød plast")
        )
        items.forEach { db.collection("items").document(it.id).set(it) }
    }
}