package dk.soerensen.garbagev1.data

import dk.soerensen.garbagev1.domain.Bin
import dk.soerensen.garbagev1.domain.BinRepository

class BinRepositoryImpl : BinRepository {

    private val bins = listOf(
        Bin(
            id = "paper",
            title = "Paper",
            description = "Newspapers, magazines, office paper, envelopes, and other clean paper waste.",
            imageUrl = "https://cdn.lomax.dk/images/t_item_large/f_auto/v1704813619/produkter/60149410_1/affaldspiktogram-12x12cm-selvklab-paper-1.jpg"
        ),
        Bin(
            id = "cardboard",
            title = "Cardboard",
            description = "Cardboard boxes, packaging, and corrugated board (clean and dry).",
            imageUrl = "https://cdn.lomax.dk/images/t_item_large/f_auto/v1704813661/produkter/60149450_1/affaldspiktogram-12x12cm-selvklab-cardboard-1.jpg"
        ),
        Bin(
            id = "food_drink_cartons",
            title = "Food & Drink Cartons",
            description = "Milk cartons, juice cartons, and other beverage or food cartons.",
            imageUrl = "https://cdn.lomax.dk/images/t_item_large/f_auto/v1704813608/produkter/60149400_1/affaldspiktogram-12x12cm-selvklab-food-waste-1.jpg"
        ),
        Bin(
            id = "metal",
            title = "Metal",
            description = "Metal cans, aluminum foil, lids, and small metal items.",
            imageUrl = "https://encrypted-tbn1.gstatic.com/shopping?q=tbn:ANd9GcTnuCcl_FH74jBMzKL3yPlZbWUOzkaCI-Ugp2HpgZTkL5ZwQy8ZnmPF5KvbXPkTHo5wDaf2MI-U3tEWdGczcN6hL2_pSKAzH2OCu6USALdO&usqp=CAc"
        ),
        Bin(
            id = "batteries",
            title = "Batteries",
            description = "All types of household batteries and small portable batteries.",
            imageUrl = "https://cdn.lomax.dk/images/t_item_large/f_auto/v1704813712/produkter/60149510_1/affaldspiktogram-12x12cm-selvklab-batteries-1.jpg"
        ),
        Bin(
            id = "food",
            title = "Food waste",
            description = "Food scraps, leftovers, fruit and vegetable peels, and other organic waste.",
            imageUrl = "https://www.skiltex.dk/images/madaffald-klistermaerke-skiltex.webp"
        ),
        Bin(
            id = "glass",
            title = "Glass waste",
            description = "Glass bottles and jars (empty and without lids).",
            imageUrl = "https://cdn.lomax.dk/images/t_item_large/f_auto/v1704813684/produkter/60149470_1/affaldspiktogram-12x12cm-selvklab-glass-1.jpg"
        ),
        Bin(
            id = "plastic",
            title = "Plastic waste",
            description = "Plastic packaging, containers, bottles, and plastic bags.",
            imageUrl = "https://cdn.lomax.dk/images/t_item_large/f_auto/v1671623546/produkter/60129890_1/affaldspiktogram-12x12cm-selvklab-plast-1.jpg"
        ),
        Bin(
            id = "residual",
            title = "Residual waste",
            description = "Non-recyclable waste that cannot be sorted into other categories.",
            imageUrl = "https://cdn.lomax.dk/images/t_item_large/f_auto/v1704813594/produkter/60149390_1/affaldspiktogram-12x12cm-selvklab-residual-waste-1.jpg"
        ),
        Bin(
            id = "chemical",
            title = "Chemical waste",
            description = "Hazardous waste such as paint, cleaning agents, chemicals, and solvents.",
            imageUrl = "https://cdn.lomax.dk/images/t_item_large/f_auto/v1704813694/produkter/60149480_1/affaldspiktogram-12x12cm-selvklab-hazardous-waste-1.jpg"
        ),
        Bin(
            id = "wood",
            title = "Wood waste",
            description = "Wood pieces, small furniture parts, and untreated wood waste.",
            imageUrl = "https://cdn-icons-png.flaticon.com/512/3275/3275760.png"
        ),
        Bin(
            id = "daily waste",
            title = "Daily waste",
            description = "Everyday household waste that is not recyclable.",
            imageUrl = "https://cdn.lomax.dk/images/t_item_large/f_auto/v1704813594/produkter/60149390_1/affaldspiktogram-12x12cm-selvklab-residual-waste-1.jpg"
        ),
        Bin(
            id = "other",
            title = "Other waste",
            description = "Special waste types that do not fit into standard recycling categories.",
            imageUrl = "https://cdn-icons-png.flaticon.com/256/2602/2602735.png"
        ),
        Bin(
            id = "electronics",
            title = "Electronics waste",
            description = "Small electronic devices, cables, chargers, and electrical equipment.",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRvZpi9Bx_A4pSxTIImmLJs9umH0HICC7YfaQ&s"
        )
    )

    override fun getBins(): List<Bin> = bins

    override fun getBin(id: String): Bin? = bins.find { it.id == id }
}