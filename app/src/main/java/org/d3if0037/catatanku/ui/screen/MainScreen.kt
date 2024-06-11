package org.d3if0037.catatanku.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import org.d3if0037.catatanku.R
import org.d3if0037.catatanku.model.MainViewModel
import org.d3if0037.catatanku.model.Note
import org.d3if0037.catatanku.network.ApiStatus
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
        },
        floatingActionButton = {
            IconButton(
                modifier = Modifier.size(57.dp).clip(RoundedCornerShape(40)).background(WarnaUtama),
                onClick = {}) {
                Icon(
                    modifier = Modifier.size(43.dp),
                    imageVector = Icons.Filled.Add,
                    contentDescription = "fab",
                    tint = WarnaText
                )
            }
        }
    ) { padding ->
        ScreenContent(Modifier.padding(padding))
    }
}

@Composable
fun ScreenContent(modifier: Modifier) {
    val viewModel: MainViewModel = viewModel()
    val data by viewModel.data
    val status by viewModel.status.collectAsState()

    Column(modifier = modifier.padding(24.dp)) {
        Text(text = "INI ADALAH JUDUL", fontWeight = FontWeight.Bold, fontSize = 30.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "ini adalah tulisan kecil yang panjang pokoknya ya bang pokoknya panjang gaergrga banget deh sampe sampe panjaaaaang",
            color = Color.Gray,
            textAlign = TextAlign.Justify
        )

        when (status) {
            ApiStatus.LOADING -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Loading", fontSize = 30.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator(progress = 1f)
                }
            }

            ApiStatus.SUCCESS -> {
                LazyVerticalGrid(
                    contentPadding = PaddingValues(bottom = 50.dp),
                    modifier = Modifier.padding(top = 24.dp),
                    columns = GridCells.Fixed(2)
                ) {
                    items(data) {
                        ItemsGrid(catatan = it)
                    }
                }
            }

            ApiStatus.FAILED -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.error))
                    Button(
                        onClick = { viewModel.retrieveData() },
                        modifier = Modifier.padding(top = 16.dp),
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                    ) {
                        Text(text = stringResource(R.string.try_again))
                    }
                }
            }
        }
    }
}

@Composable
fun ItemsGrid(catatan : Note) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .clip(shape = RoundedCornerShape(17.dp))
            .border(1.dp, AbuMuda),
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(catatan.imageId)
                    .crossfade(true)
                    .build(),
                modifier = Modifier.fillMaxWidth(),
                contentDescription = catatan.judul,
                contentScale = ContentScale.Crop
            )
            Text(
                modifier = Modifier
                    .background(Color(0f, 0f, 0f, 0.5f))
                    .fillMaxWidth()
                    .padding(4.dp),
                text = catatan.judul,
                color = Color.White,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
            text = catatan.deskripsi
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