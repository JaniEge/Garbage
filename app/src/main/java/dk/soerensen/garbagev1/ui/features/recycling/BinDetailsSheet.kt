package dk.soerensen.garbagev1.ui.features.recycling

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dk.soerensen.garbagev1.domain.Bin

@Composable
fun BinDetailsSheet(bin: Bin) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = bin.title, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        AsyncImage(
            model = bin.imageUrl,
            contentDescription = "${bin.title} label",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(12.dp))
        Text(text = bin.description, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(24.dp))
    }
}