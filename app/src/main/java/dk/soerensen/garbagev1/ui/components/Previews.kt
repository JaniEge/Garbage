package dk.soerensen.garbagev1.ui.components

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dk.soerensen.garbagev1.domain.Bin
import dk.soerensen.garbagev1.domain.GarbageItem
import java.util.UUID


class BinProvider : PreviewParameterProvider<Bin> {
    override val values = sequenceOf(
        Bin("food", "Food waste", "Leftovers, fruit and vegetable scraps.", "https://via.placeholder.com/600x400.png?text=Food+bin+label"),
        Bin("metal", "Metal", "Cans, foil, metal lids.", "https://via.placeholder.com/600x400.png?text=Metal+bin+label"),
        Bin("paper", "Paper", "Newspapers and cardboard.", "https://via.placeholder.com/600x400.png?text=Paper+bin+label"),
        Bin("glass", "Glass", "Bottles and jars.", "https://via.placeholder.com/600x400.png?text=Glass+bin+label"),
        Bin("plastic", "Plastic", "Plastic packaging and containers.", "https://via.placeholder.com/600x400.png?text=Plastic+bin+label"),
    )
}

class BinOrNullProvider : PreviewParameterProvider<Bin?> {
    override val values = sequenceOf(
        Bin("food", "Food waste", "Leftovers, fruit and vegetable scraps.", "https://via.placeholder.com/600x400.png?text=Food+bin+label"),
        null
    )
}


class GarbageItemProvider : PreviewParameterProvider<GarbageItem> {
    override val values = sequenceOf(
        GarbageItem(UUID.randomUUID().toString(), "Banana peel", "Food waste"),
        GarbageItem(UUID.randomUUID().toString(), "Tin can", "Metal")
    )
}

class GarbageItemOrNullProvider : PreviewParameterProvider<GarbageItem?> {
    override val values = sequenceOf(
        GarbageItem(UUID.randomUUID().toString(), "Glass bottle", "Glass"),
        null
    )
}

class BooleanProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}

fun previewBins() = listOf(
    Bin("food", "Food waste", "Leftovers, fruit and vegetable scraps.", "https://via.placeholder.com/600x400.png?text=Food+bin+label"),
    Bin("metal", "Metal", "Cans, foil, metal lids.", "https://via.placeholder.com/600x400.png?text=Metal+bin+label"),
    Bin("paper", "Paper", "Newspapers and cardboard.", "https://via.placeholder.com/600x400.png?text=Paper+bin+label"),
    Bin("glass", "Glass", "Bottles and jars.", "https://via.placeholder.com/600x400.png?text=Glass+bin+label"),
    Bin("plastic", "Plastic", "Plastic packaging and containers.", "https://via.placeholder.com/600x400.png?text=Plastic+bin+label"),
)

fun previewGarbageItems() = listOf(
    GarbageItem(UUID.randomUUID().toString(), "Coffee grounds", "Food waste"),
    GarbageItem(UUID.randomUUID().toString(), "Newspaper", "Paper"),
    GarbageItem(UUID.randomUUID().toString(), "Yogurt cup", "Plastic"),
    GarbageItem(UUID.randomUUID().toString(), "Soda can", "Metal"),
)

@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
annotation class ThemedPreviews