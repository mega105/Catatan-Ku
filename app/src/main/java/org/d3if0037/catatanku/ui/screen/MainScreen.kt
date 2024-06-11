package org.d3if0037.catatanku.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.d3if0037.catatanku.R
import org.d3if0037.catatanku.ui.theme.AbuMuda
import org.d3if0037.catatanku.ui.theme.CatatanKuTheme
import org.d3if0037.catatanku.ui.theme.WarnaText
import org.d3if0037.catatanku.ui.theme.WarnaUtama

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Catatan Ku", fontWeight = FontWeight.SemiBold)
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = WarnaUtama,
                    titleContentColor = WarnaText
                ),
                actions = {}
            )
        }
    ) { padding ->
        ScreenContent(Modifier.padding(padding))
    }
}

@Composable
fun ScreenContent(modifier: Modifier) {
    Column(modifier = modifier.padding(24.dp)) {
        Text(text = "INI ADALAH JUDUL", fontWeight = FontWeight.Bold, fontSize = 30.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "ini adalah tulisan kecil yang panjang pokoknya ya bang pokoknya panjang gaergrga banget deh sampe sampe panjaaaaang",
            color = Color.Gray,
            textAlign = TextAlign.Justify
        )
        LazyVerticalGrid(
            modifier = Modifier.padding(top = 24.dp),
            columns = GridCells.Fixed(2)
        ) {
            item {
                ItemsGrid()
            }
            item {
                ItemsGrid()
            }
            item {
                ItemsGrid()
            }
            item {
                ItemsGrid()
            }
            item {
                ItemsGrid()
            }
            item {
                ItemsGrid()
            }
            item {
                ItemsGrid()
            }
            item {
                ItemsGrid()
            }
        }
    }
}

@Composable
fun ItemsGrid() {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .clip(shape = RoundedCornerShape(17.dp))
            .border(1.dp, AbuMuda),
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = R.drawable.contoh),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Text(
                modifier = Modifier
                    .background(Color(0f, 0f, 0f, 0.5f))
                    .fillMaxWidth()
                    .padding(4.dp),
                text = "Judul",
                color = Color.White,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
            text = "aufiauefeiaw auwef iewauf i ufiaw faw efaufiaweufeaw ifueafw iaweuf iwue fiu awef iu fewa "
        )
    }
}

@Preview(showBackground = true)
@Composable
fun IniPrev() {
    CatatanKuTheme {
        MainScreen()
    }
}