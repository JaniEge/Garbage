package dk.soerensen.garbagev1.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.UUID
import javax.inject.Provider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ItemEntity::class, BinEntity::class],
    version = 2,
    exportSchema = false
)
abstract class GarbageDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao
    abstract fun binDao(): BinDao

    class Callback(
        private val itemDaoProvider: Provider<ItemDao>,
        private val binDaoProvider: Provider<BinDao>
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                prepopulateBins()
                prepopulateItems()
            }
        }

        private data class BinDto(
            val id: String,
            val title: String,
            val description: String,
            val imageUrl: String
        )

        private data class GarbageItemSeedDto(
            val id: String,
            val name: String,
            val binId: String,       // vi gemmer bin-id (fx "paper")
            val description: String
        )

        private suspend fun prepopulateBins() {
            val binDao = binDaoProvider.get()

            val initialBins = listOf(
                BinDto(
                    id = "paper",
                    title = "Paper",
                    description = "Newspapers, magazines, office paper, envelopes, and other clean paper waste.",
                    imageUrl = "https://cdn.lomax.dk/images/t_item_large/f_auto/v1704813619/produkter/60149410_1/affaldspiktogram-12x12cm-selvklab-paper-1.jpg"
                ),
                BinDto(
                    id = "cardboard",
                    title = "Cardboard",
                    description = "Cardboard boxes, packaging, and corrugated board (clean and dry).",
                    imageUrl = "https://cdn.lomax.dk/images/t_item_large/f_auto/v1704813661/produkter/60149450_1/affaldspiktogram-12x12cm-selvklab-cardboard-1.jpg"
                ),
                BinDto(
                    id = "food_drink_cartons",
                    title = "Food & Drink Cartons",
                    description = "Milk cartons, juice cartons, and other beverage or food cartons.",
                    imageUrl = "https://cdn.lomax.dk/images/t_item_large/f_auto/v1704813608/produkter/60149400_1/affaldspiktogram-12x12cm-selvklab-food-waste-1.jpg"
                ),
                BinDto(
                    id = "metal",
                    title = "Metal",
                    description = "Metal cans, aluminum foil, lids, and small metal items.",
                    imageUrl = "https://encrypted-tbn1.gstatic.com/shopping?q=tbn:ANd9GcTnuCcl_FH74jBMzKL3yPlZbWUOzkaCI-Ugp2HpgZTkL5ZwQy8ZnmPF5KvbXPkTHo5wDaf2MI-U3tEWdGczcN6hL2_pSKAzH2OCu6USALdO&usqp=CAc"
                ),
                BinDto(
                    id = "batteries",
                    title = "Batteries",
                    description = "All types of household batteries and small portable batteries.",
                    imageUrl = "https://cdn.lomax.dk/images/t_item_large/f_auto/v1704813712/produkter/60149510_1/affaldspiktogram-12x12cm-selvklab-batteries-1.jpg"
                ),
                BinDto(
                    id = "food",
                    title = "Food waste",
                    description = "Food scraps, leftovers, fruit and vegetable peels, and other organic waste.",
                    imageUrl = "https://www.skiltex.dk/images/madaffald-klistermaerke-skiltex.webp"
                ),
                BinDto(
                    id = "glass",
                    title = "Glass waste",
                    description = "Glass bottles and jars (empty and without lids).",
                    imageUrl = "https://cdn.lomax.dk/images/t_item_large/f_auto/v1704813684/produkter/60149470_1/affaldspiktogram-12x12cm-selvklab-glass-1.jpg"
                ),
                BinDto(
                    id = "plastic",
                    title = "Plastic waste",
                    description = "Plastic packaging, containers, bottles, and plastic bags.",
                    imageUrl = "https://cdn.lomax.dk/images/t_item_large/f_auto/v1671623546/produkter/60129890_1/affaldspiktogram-12x12cm-selvklab-plast-1.jpg"
                ),
                BinDto(
                    id = "residual",
                    title = "Residual waste",
                    description = "Non-recyclable waste that cannot be sorted into other categories.",
                    imageUrl = "https://cdn.lomax.dk/images/t_item_large/f_auto/v1704813594/produkter/60149390_1/affaldspiktogram-12x12cm-selvklab-residual-waste-1.jpg"
                ),
                BinDto(
                    id = "chemical",
                    title = "Chemical waste",
                    description = "Hazardous waste such as paint, cleaning agents, chemicals, and solvents.",
                    imageUrl = "https://cdn.lomax.dk/images/t_item_large/f_auto/v1704813694/produkter/60149480_1/affaldspiktogram-12x12cm-selvklab-hazardous-waste-1.jpg"
                ),
                BinDto(
                    id = "wood",
                    title = "Wood waste",
                    description = "Wood pieces, small furniture parts, and untreated wood waste.",
                    imageUrl = "https://cdn-icons-png.flaticon.com/512/3275/3275760.png"
                ),
                BinDto(
                    id = "daily_waste",
                    title = "Daily waste",
                    description = "Everyday household waste that is not recyclable.",
                    imageUrl = "https://cdn.lomax.dk/images/t_item_large/f_auto/v1704813594/produkter/60149390_1/affaldspiktogram-12x12cm-selvklab-residual-waste-1.jpg"
                ),
                BinDto(
                    id = "other",
                    title = "Other waste",
                    description = "Special waste types that do not fit into standard recycling categories.",
                    imageUrl = "https://cdn-icons-png.flaticon.com/256/2602/2602735.png"
                ),
                BinDto(
                    id = "electronics",
                    title = "Electronics waste",
                    description = "Small electronic devices, cables, chargers, and electrical equipment.",
                    imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRvZpi9Bx_A4pSxTIImmLJs9umH0HICC7YfaQ&s"
                ),
                BinDto(
                    id = "bulky_waste",
                    title = "Bulky waste",
                    description = "Large household items such as furniture, mattresses, carpets, and big items that don't fit in regular bins.",
                    imageUrl = "https://cdn.josafety.dk/media/catalog/product/cache/618acdf0e9257170c15469528d31905a/A/F/AF3002_3dbc23a4_1_7.webp"
                ),
                BinDto(
                    id = "clothes",
                    title = "clothes",
                    description = "Clothing, fabric scraps, towels, bed linen, and other textiles that are clean and dry.",
                    imageUrl = "https://www.peoples.dk/wp-content/uploads/2022/06/TEKSTILAFFALD-rgb-72dpi.jpg"
                )
            )

            // DTO Entity
            initialBins.forEach {
                binDao.insert(
                    BinEntity(
                        id = it.id,
                        title = it.title,
                        description = it.description,
                        imageUrl = it.imageUrl
                    )
                )
            }
        }

        private suspend fun prepopulateItems() {
            val itemDao = itemDaoProvider.get()

            val initialItems = listOf(
                GarbageItemSeedDto(id = "deep-link-item", name = "Newspaper", binId = "paper", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Banana", binId = "food", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Magazine", binId = "paper", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Milk carton", binId = "plastic", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Shoe box", binId = "cardboard", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Can", binId = "metal", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Book", binId = "paper", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Aluminium foil", binId = "metal", description = "Clean only"),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Teddy bear", binId = "daily_waste", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Flower pot", binId = "daily_waste", description = "Plastic"),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Cables", binId = "metal", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Envelope", binId = "paper", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Detergent", binId = "plastic", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Musical instrument", binId = "wood", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Cookware", binId = "metal", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Hammer", binId = "metal", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Curtain clips", binId = "metal", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Jar", binId = "glass", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Carpet", binId = "bulky_waste", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Postcard", binId = "cardboard", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Chips bag", binId = "other", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Tooth brush", binId = "plastic", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Shampoo bottle", binId = "plastic", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Capsule", binId = "metal", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Needle", binId = "metal", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Letter", binId = "paper", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Plastic bottle", binId = "plastic", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Meat", binId = "food", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Clothes", binId = "other", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Cutlery", binId = "metal", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Paint", binId = "chemical", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Chlorine", binId = "chemical", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Computer", binId = "electronics", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Battery", binId = "batteries", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Printer", binId = "electronics", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Potato", binId = "food", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Cabbage", binId = "food", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Kale", binId = "food", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Cauliflower", binId = "food", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Onion", binId = "food", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Beetroot", binId = "food", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Celeriac", binId = "food", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Celery", binId = "food", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Flour", binId = "food", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Sugar", binId = "food", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Rice", binId = "food", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "AA Battery", binId = "batteries", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Glass bottle", binId = "glass", description = "Empty bottle without lid"),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Banana peel", binId = "food", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Cardboard box", binId = "cardboard", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Pizza box", binId = "cardboard", description = "Clean and dry only"),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Juice carton", binId = "food_drink_cartons", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Soda can", binId = "metal", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Apple core", binId = "food", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Plastic bag", binId = "plastic", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Old phone", binId = "electronics", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Charger", binId = "electronics", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Old mattress", binId = "bulky_waste", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Broken chair", binId = "bulky_waste", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Wood plank", binId = "wood", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Tissue paper", binId = "residual", description = ""),
                GarbageItemSeedDto(id = UUID.randomUUID().toString(), name = "Chewing gum", binId = "residual", description = "")
            )

            initialItems.forEach {
                itemDao.insert(
                    ItemEntity(
                        id = it.id,
                        name = it.name,
                        bin = it.binId,
                        description = it.description
                    )
                )
            }
        }
    }
}