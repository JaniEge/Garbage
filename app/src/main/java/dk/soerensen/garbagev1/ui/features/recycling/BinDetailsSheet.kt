package dk.soerensen.garbagev1.ui.features.recycling

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dk.soerensen.garbagev1.domain.Bin

@Composable
fun BinDetailsSheet(
    bin: Bin,
    onTrackRecycling: (Bin) -> Unit // ✅ Ny parameter til at håndtere klik
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = bin.title, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        AsyncImage(
            model = bin.imageUrl,
            contentDescription = "${bin.title} label",
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally),
            contentScale = ContentScale.Fit
        )

        Spacer(Modifier.height(12.dp))
        Text(text = bin.description, style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(24.dp))

        // ✅ "Track Recycling" knappen fra opgavebeskrivelsen
        Button(
            onClick = {
                // Opdaterer bin med det nuværende tidsstempel
                onTrackRecycling(bin.copy(lastPickupTime = System.currentTimeMillis()))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Track Recycling")
        }

        Spacer(Modifier.height(16.dp))
    }
}