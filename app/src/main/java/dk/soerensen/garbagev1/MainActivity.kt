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
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dagger.hilt.android.AndroidEntryPoint
import dk.soerensen.garbagev1.domain.Bin
import dk.soerensen.garbagev1.domain.GarbageItem
import dk.soerensen.garbagev1.domain.Theme
import dk.soerensen.garbagev1.ui.features.settings.SettingsViewModel
import dk.soerensen.garbagev1.ui.navigation.MainNavigation
import dk.soerensen.garbagev1.ui.theme.GarbageV1Theme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Kør disse én gang hvis du vil oprette data i Firestore
        // seedBinsToFirebase()
        // seedItemsToFirebase()

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
        val db = Firebase.firestore
        val bins = listOf(
            Bin(
                id = "rest",
                title = "Restaffald",
                description = "Beskidt papir, bleer, støvsugerposer",
                imageUrl = "https://cdn.lomax.dk/v-638062831836100000/productimages/8856001_1_main.jpg?width=400&height=400&mode=pad"
            ),
            Bin(
                id = "paper",
                title = "Papir",
                description = "Aviser, reklamer, kontorpapir",
                imageUrl = "https://cdn.lomax.dk/v-638062831836100000/productimages/8856002_1_main.jpg?width=400&height=400&mode=pad"
            ),
            Bin(
                id = "cardboard",
                title = "Pap",
                description = "Papkasser, æsker, bølgepap",
                imageUrl = "https://cdn.lomax.dk/v-638062831836100000/productimages/8856002_1_main.jpg?width=400&height=400&mode=pad"
            ),
            Bin(
                id = "plastic",
                title = "Plast",
                description = "Hård og blød plast, emballage",
                imageUrl = "https://cdn.lomax.dk/v-638062831836100000/productimages/8856003_1_main.jpg?width=400&height=400&mode=pad"
            ),
            Bin(
                id = "metal",
                title = "Metal",
                description = "Dåser, sølvpapir, kapsler",
                imageUrl = "https://cdn.lomax.dk/v-638062831836100000/productimages/8856004_1_main.jpg?width=400&height=400&mode=pad"
            ),
            Bin(
                id = "glass",
                title = "Glas",
                description = "Flasker, syltetøjsglas",
                imageUrl = "https://cdn.lomax.dk/v-638062831836100000/productimages/8856005_1_main.jpg?width=400&height=400&mode=pad"
            ),
            Bin(
                id = "bio",
                title = "Madaffald",
                description = "Madrester, skræller, kaffegrums",
                imageUrl = "https://cdn.lomax.dk/v-638062831836100000/productimages/8856006_1_main.jpg?width=400&height=400&mode=pad"
            ),
            Bin(
                id = "textile",
                title = "Tekstil",
                description = "Ødelagt tøj, stofrester",
                imageUrl = "https://cdn.lomax.dk/v-638062831836100000/productimages/8856008_1_main.jpg?width=400&height=400&mode=pad"
            ),
            Bin(
                id = "food_carton",
                title = "Mad- & Drikkekartoner",
                description = "Mælkekartoner, juicekartoner",
                imageUrl = "https://cdn.lomax.dk/v-638062831836100000/productimages/8856007_1_main.jpg?width=400&height=400&mode=pad"
            ),
            Bin(
                id = "farligt",
                title = "Farligt affald",
                description = "Batterier, kemikalier, lyspærer",
                imageUrl = "https://cdn.lomax.dk/v-638062831836100000/productimages/8856009_1_main.jpg?width=400&height=400&mode=pad"
            )
        )

        bins.forEach { db.collection("bins").document(it.id).set(it) }
    }

    private fun seedItemsToFirebase() {
        val db = Firebase.firestore
        val items = listOf(
            GarbageItem(id = "maelk", name = "Mælkekarton", bin = "food_carton", description = "Husk at tømme den"),
            GarbageItem(id = "juice", name = "Juicekarton", bin = "food_carton", description = "Tømt for indhold"),
            GarbageItem(id = "pizza", name = "Pizzabakke", bin = "rest", description = "Beskidt pap skal i restaffald"),
            GarbageItem(id = "avis", name = "Avis", bin = "paper", description = "Tørt og rent papir"),
            GarbageItem(id = "banan", name = "Bananskræl", bin = "bio", description = "Grønt affald"),
            GarbageItem(id = "cola", name = "Coladåse", bin = "metal", description = "Dåser uden pant"),
            GarbageItem(id = "vin", name = "Vinflaske", bin = "glass", description = "Husk at fjerne proppen"),
            GarbageItem(id = "smor", name = "Smørbakke", bin = "plastic", description = "Skyllet for fedtstof"),
            GarbageItem(id = "batteri", name = "Batterier", bin = "farligt", description = "Små batterier i pose"),
            GarbageItem(id = "ble", name = "Brugt ble", bin = "rest", description = "Skal pakkes ind"),
            GarbageItem(id = "stovsug", name = "Støvsugerpose", bin = "rest", description = "Husk at lukke den"),
            GarbageItem(id = "tojs", name = "Gammel t-shirt", bin = "textile", description = "Hulleri eller pletter"),
            GarbageItem(id = "konserv", name = "Konservesdåse", bin = "metal", description = "Tømt og skyllet"),
            GarbageItem(id = "æg", name = "Æggebakke", bin = "cardboard", description = "Ren pap"),
            GarbageItem(id = "plastpose", name = "Plastikpose", bin = "plastic", description = "Både hård og blød plast")
        )

        items.forEach { db.collection("items").document(it.id).set(it) }
    }
}