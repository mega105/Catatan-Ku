package org.d3if0037.catatanku.ui.screen

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.d3if0037.catatanku.BuildConfig
import org.d3if0037.catatanku.R
import org.d3if0037.catatanku.model.MainViewModel
import org.d3if0037.catatanku.model.Note
import org.d3if0037.catatanku.model.User
import org.d3if0037.catatanku.network.ApiStatus
import org.d3if0037.catatanku.network.CatatanApi
import org.d3if0037.catatanku.network.UserDataStore
import org.d3if0037.catatanku.ui.theme.AbuMuda
import org.d3if0037.catatanku.ui.theme.CatatanKuTheme
import org.d3if0037.catatanku.ui.theme.WarnaText
import org.d3if0037.catatanku.ui.theme.WarnaUtama

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val dataStore = UserDataStore(context)
    val user by dataStore.userFlow.collectAsState(User())

    val viewModel: MainViewModel = viewModel()
    val errorMessage by viewModel.errorMessage

    var showDialog by remember { mutableStateOf(false) }
    var showCatatanDialog by remember { mutableStateOf(false) }

    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    val launcher = rememberLauncherForActivityResult(CropImageContract()) {
        bitmap = getCroppedImage(context.contentResolver, it)
        if (bitmap != null) showCatatanDialog = true
    }

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
                actions = {
                    IconButton(onClick = {
                        if (user.email.isEmpty()) {
                            CoroutineScope(Dispatchers.IO).launch { signIn(context, dataStore) }
                        } else {
                            showDialog = true
                        }
                    }) {
                        Icon(
                            modifier = Modifier.size(30.dp),
                            painter = painterResource(id = R.drawable.account_circle),
                            contentDescription = stringResource(R.string.profil),
                            tint = WarnaText
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            IconButton(
                modifier = Modifier
                    .size(57.dp)
                    .clip(RoundedCornerShape(40))
                    .background(WarnaUtama),
                onClick = {
                    val option = CropImageContractOptions(
                        null, CropImageOptions(
                            imageSourceIncludeGallery = false,
                            imageSourceIncludeCamera = true,
                            fixAspectRatio = true
                        )
                    )
                    launcher.launch(option)
                }) {
                Icon(
                    modifier = Modifier.size(43.dp),
                    imageVector = Icons.Filled.Add,
                    contentDescription = "fab",
                    tint = WarnaText
                )
            }
        }
    ) { padding ->
        ScreenContent(viewModel, Modifier.padding(padding))

        if (showDialog) {
            ProfilDialog(
                user = user,
                onDismissRequest = { showDialog = false }) {
                CoroutineScope(Dispatchers.IO).launch { signOut(context, dataStore) }
                showDialog = false
            }
        }

        if (showCatatanDialog) {
            CatatanDialog(
                bitmap = bitmap,
                onDismissRequest = { showCatatanDialog = false }) { judul, deskripsi ->
                viewModel.saveData(user.email, judul, deskripsi, bitmap!!)
                showCatatanDialog = false
            }
        }

        if (errorMessage != null) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }
}

@Composable
fun ScreenContent(viewModel: MainViewModel, modifier: Modifier) {
//    val viewModel: MainViewModel = viewModel()
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
fun ItemsGrid(catatan: Note) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .clip(shape = RoundedCornerShape(17.dp))
            .border(1.dp, AbuMuda, RoundedCornerShape(17.dp))
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(CatatanApi.getCatatanUrl(catatan.imageId))
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

private suspend fun signIn(context: Context, dataStore: UserDataStore) {
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        handleSignIn(result, dataStore)
    } catch (e: GetCredentialException) {
        Log.e("SIGN-IN", "Error:${e.errorMessage}")
    }
}

private suspend fun handleSignIn(result: GetCredentialResponse, dataStore: UserDataStore) {
    val credential = result.credential
    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            val nama = googleId.displayName ?: ""
            val email = googleId.id
            val photoUrl = googleId.profilePictureUri.toString()
            dataStore.saveData(User(nama, email, photoUrl))
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("SIGN-IN", "Error: ${e.message}")
        }
    } else {
        Log.e("SIGN-IN", "Error: unrecognized custom credential type")
    }
}

private suspend fun signOut(context: Context, dataStore: UserDataStore) {
    try {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        dataStore.saveData(User())
    } catch (e: ClearCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private fun getCroppedImage(
    resolver: ContentResolver,
    result: CropImageView.CropResult
): Bitmap? {
    if (!result.isSuccessful) {
        Log.e("IMAGE", "Error: ${result.error}")
        return null
    }

    var uri = result.uriContent ?: return null

    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        MediaStore.Images.Media.getBitmap(resolver, uri)
    } else {
        val source = ImageDecoder.createSource(resolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}

@Preview(showBackground = true)
@Composable
fun IniPrev() {
    CatatanKuTheme {
        MainScreen()
    }
}