import android.graphics.drawable.Drawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.endofjanuary.placement_example.domain.models.ModelEntry
import com.endofjanuary.placement_example.ui.components.ImageWithLabel
import com.endofjanuary.placement_example.ui.dialogs.ModelExpiredDialog

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModelInRowEntry(
    entry: ModelEntry,
    navController: NavController,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit,
    selectedIds: List<String>,
    selectionMode: Boolean,
    selectModel: (model: ModelEntry) -> Unit,
    activateSelectionMode: () -> Unit,
    calcDominantColor: (drawable: Drawable, onFinish: (Color) -> Unit) -> Unit,
) {
    val openExpiredDialog = remember { mutableStateOf(false) }
    val confirmDelete = remember { mutableStateOf(false) }

    val defaultDominantColor = MaterialTheme.colorScheme.surface
    val deletedDominantColor = Color.Gray.copy(alpha = 0.5f)
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                if (selectedIds.contains(entry.meshyId) && selectionMode) deletedDominantColor
                else defaultDominantColor
            )
            .clip(RoundedCornerShape(10.dp))
            .combinedClickable(
                onClick = {
                    if (!selectionMode) {
                        if (!entry.isExpired) {
                            navController.navigate(
                                "transit_dialog/${entry.id}/${entry.meshyId}"
                            )
                        } else {
                            openExpiredDialog.value = true
                        }
                    } else {
                        selectModel(entry)
                    }
                },
                onLongClick = {
                    if (!selectionMode) activateSelectionMode()
                    selectModel(entry)
                },
            )
    ) {
        if (!entry.isExpired) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(entry.modelImageUrl)
                    .crossfade(true).build(),
                contentDescription = entry.modelDescription,
                onSuccess = {
                    calcDominantColor(it.result.drawable) { color ->
                        dominantColor = color
                    }
                },
                contentScale = ContentScale.FillBounds,
                alpha = if (selectedIds.contains(entry.meshyId) && selectionMode) 0.5f else 1f,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
            )
            Text(
                text = entry.modelDescription,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent, MaterialTheme.colorScheme.surface
                            ),
                        )
                    ),
                softWrap = true,
                maxLines = 4
            )
        } else {
            ImageWithLabel(picture = {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(entry.modelImageUrl)
                        .crossfade(true).build(),
                    contentDescription = entry.modelDescription,
                    onSuccess = {
                        calcDominantColor(it.result.drawable) { color ->
                            dominantColor = color
                        }
                    },
                    contentScale = ContentScale.FillBounds,
                    alpha = if (selectedIds.contains(entry.meshyId) && selectionMode) 0.5f else 1f,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize()
                )
                Text(
                    text = entry.modelDescription,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent, MaterialTheme.colorScheme.surface
                                ),
                            )
                        ),
                    softWrap = true,
                    maxLines = 4
                )
            })
        }
    }

    ModelExpiredDialog(
        openDialog = openExpiredDialog, confirm = confirmDelete, onConfirm = onDelete
    )
}