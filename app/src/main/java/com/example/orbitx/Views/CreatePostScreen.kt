package com.example.orbitx.Views

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.orbitx.R
import java.util.Date
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.orbitx.model.JiteshxUser
import com.example.orbitx.ui.JiteshxScreen.CreatePostViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.navigation.NavController
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntOffset
import com.example.orbitx.ChatRepository.fetchProfileurl
import com.example.orbitx.ChatRepository.fetchusername
import com.google.firebase.auth.auth
//import androidx.compose.ui.text.googlefonts.GoogleFont
//import androidx.compose.ui.text.googlefonts.Font
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.orbitx.model.Location
import com.example.orbitx.ui.theme.fontFamily1
import com.example.orbitx.ui.theme.provider
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CreatePostScreen(navController: NavController,viewModel: CreatePostViewModel = viewModel(), modifier: Modifier,) {

    val text by viewModel.text.collectAsState()
    var locationText by remember { mutableStateOf("") }
    var HashtagText by remember { mutableStateOf("") }
    val imageUri by viewModel.imageUri.collectAsState()
    val creatingPost by viewModel.isPostCreated.collectAsState()
    val context = LocalContext.current
    val db = Firebase.firestore
    val textState = remember { mutableStateOf(TextFieldValue("")) }
    val postRef =
        db.collection("Users").document("A43t07amgUmkKxz2zVOK") // replace with the actual post ID
    var authorAvatarUrl by remember { mutableStateOf("") }
    var post by remember {
        mutableStateOf(
            JiteshxUser(
                authorName = "",
                authorAvatarUrl = "",
                timestamp = System.currentTimeMillis() / 1000,
            )
        )
    }
    var cuid =Firebase.auth.currentUser?.uid.toString()

    var profilepicurl by remember {
        mutableStateOf("")
    }
    var username by remember {
        mutableStateOf("")
    }
    fetchProfileurl(cuid){it->profilepicurl=it}
    fetchusername(cuid){it->username=it}
    val coroutineScope = rememberCoroutineScope()
    val _sheetState = ModalBottomSheetState(ModalBottomSheetValue.Expanded)


    var focusedContainerColor by remember { mutableStateOf(Color(226, 229, 234)) }
    LaunchedEffect(Unit) {
        launch {
            coroutineScope.launch {
                _sheetState.show() // Keep the sheet visible
            }
            try {
                val postSnapshot = postRef.get().await()
                if (postSnapshot.exists()) {
                    authorAvatarUrl = postSnapshot.get("authorAvatarUrl") as String
                    post = JiteshxUser(
                        authorName = postSnapshot.get("authorName") as String,
                        authorAvatarUrl = postSnapshot.get("authorAvatarUrl") as String,
                        timestamp = post.timestamp
                    )
                } else {
                    Log.d("CreatePostScreen", "Post document does not exist")
                }
            } catch (e: Exception) {
                Log.e("CreatePostScreen", "Error fetching post data", e)
            }
        }
    }

    val bottomSheetState = rememberModalBottomSheetState(false, { true })

    var openBottomSheet by rememberSaveable { mutableStateOf(true) }
    // Activity result launcher to handle the image picking
    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { viewModel.onImageSelected(it) }
        }

  //  val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Expanded)

    if (true) {
        ModalBottomSheetLayout(
            sheetState = _sheetState,
            scrimColor = Color.Transparent,


            sheetContent = {
                Box(
                    modifier = Modifier
                        .offset {
                            // Check if the sheet is expanded or not
                            val offsetY = if (_sheetState.isVisible) 0.dp else (-100).dp
                            IntOffset(0, offsetY.roundToPx())
                        }
                ) {

                }
            }
        ) {
            Surface(
                color = Color.White,
                modifier = Modifier
                    .padding(top = 0.dp)
                    .fillMaxSize()
            ) {
                Column() {
                    TopBar(
                        onBackPressed = { navController.navigateUp() },
                        onPostClicked = { viewModel.createPost(context,navController, locationText, HashtagText) }
                    )
                    Divider(
                        color = Color.LightGray,
                        thickness = 1.dp,

                    )
                    if (creatingPost) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .background(Color.Gray.copy(alpha = 0.5f)),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                trackColor = Color.LightGray,
                                color = colorResource(id = R.color.orange)

                            )
                        }
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(profilepicurl)
                                .crossfade(true)
                                .placeholder(R.drawable.ic_placeholder)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(53.dp)
                                .clip(CircleShape)
                        )
                        Column(
                            Modifier
                                .weight(1f)
                                .padding(start = 10.dp)
                        ) {
                            Text(
                                text=username,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                                    fontSize = 23.sp),
                                color = Color.Black
                            )
                            Row(
                                Modifier
                                    .padding(vertical = 8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .height(25.dp)
                                        .width(110.dp)
                                        .background(
                                            Color(226, 229, 234),
                                            shape = RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment = Alignment.Center

                                ) {
                                    Row {val today = remember {
                                        Date()
                                    }
                                        IconButton(onClick = { }) {
                                            Icon(
                                                imageVector = Icons.Filled.CalendarToday,
                                                contentDescription = "Edit Location",
                                                tint = (colorResource(id = R.color.orange)), // Change color here
                                                modifier = Modifier.size(25.dp) // Change size here
                                            )
                                        }
                                        Text(
                                            post.timestamp?.let {
                                                dateLabel(
                                                    timestamp = it,
                                                    today = today
                                                )
                                            }
                                                ?: "",
                                            color = Color.DarkGray

                                        )
                                    }
                                }
                                Box(
                                        modifier = Modifier
                                            .height(25.dp)
                                            .width(200.dp)
                                            .padding(horizontal = 10.dp)
                                            .background(
                                                Color(226, 229, 234),
                                                shape = RoundedCornerShape(10.dp)
                                            ),
                                contentAlignment = Alignment.Center

                                ) {
                                    Row {
                                        IconButton(onClick = { }) {
                                            Icon(
                                                imageVector = Icons.Filled.EditLocation,
                                                contentDescription = "Edit Location",
                                                tint = (colorResource(id = R.color.orange)), // Change color here
                                                modifier = Modifier.size(25.dp) // Change size here
                                            )
                                        }
                                        Text(
                                            if (locationText.isEmpty()) "Location" else locationText,
                                            modifier = Modifier
                                                .weight(0.5f)
                                                .align(Alignment.CenterVertically),
                                        )
                                    }
                            }
                            }

                        }

                        IconButton(onClick = { coroutineScope.launch { _sheetState.show() } }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = stringResource(R.string.menu),
                                tint = Color.Black
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .height(25.dp)
                            .width(300.dp)
                            .padding(horizontal = 70.dp)
                            .background(Color(226, 229, 234), shape = RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row {
                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = Icons.Filled.Hail,
                                    contentDescription = "Edit Location",
                                    tint = (colorResource(id = R.color.orange)), // Change color here
                                    modifier = Modifier.size(25.dp) // Change size here
                                )
                            }
                            Text(
                                if (HashtagText.isEmpty()) "Hashtag" else HashtagText,
                                modifier = Modifier
                                    .weight(0.5f)
                                    .align(Alignment.CenterVertically),
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Divider(
                        color = Color.LightGray, // Set the color of the divider
                        thickness = 1.dp,   // Set the thickness of the divider

                    )
                    TextField(
                        value = textState.value,
                        onValueChange = {
                            textState.value = it
                            viewModel.onTextChanged(it.text)
                        },
                        placeholder = {
                            Text(
                                text = "Share Your Thoughts !",
                                style = TextStyle(fontSize = 25.sp,
                                    fontFamily = fontFamily1),
                                color = Color.Gray
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = focusedContainerColor
                        ),
                        textStyle = TextStyle(
                            color = Color.Black  // Main text color
                        ),
                        modifier = Modifier
                            .background(Color.Yellow)
                            .fillMaxWidth()
                            .height(300.dp),
                    )

                    PostOptionsBottomSheet(
                        textState = textState,
                        imagePickerLauncher = imagePickerLauncher,
                        imageUri = imageUri,
                        onOptionSelected = { option ->
                            // Handle option selection if needed
                            coroutineScope.launch {
                                _sheetState.show()
                            }
                        },
                        onColorSelected = { color ->
                            focusedContainerColor = color
                        },
                        locationText = locationText,
                        onLocationTextChanged = { newLocationText ->
                            locationText = newLocationText
                            viewModel.onTextChanged("$text ")
                        },
                        onHashtagTextChanged = { newHashtagText ->
                            HashtagText = newHashtagText
                            viewModel.onTextChanged("$text")
                        }
                    )

                }
            }
        }
    }
}



@Composable
fun PostOptionsBottomSheet(
    textState: MutableState<TextFieldValue>,
    imageUri: Uri?,
    imagePickerLauncher: ManagedActivityResultLauncher<String, Uri?>,
    onOptionSelected: (String) -> Unit,
    onColorSelected:  (Color) -> Unit,
    locationText: String,
    onLocationTextChanged: (String) -> Unit,
    onHashtagTextChanged: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            contentAlignment = Alignment.Center
        ){
            Icon(
                imageVector = Icons.Filled.ArrowDropUp,
                contentDescription = "Upward Arrow",
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(Modifier.height(1.dp))
        Row {
            ImagePicker(
                imageUri = imageUri,
                onImageSelected = { imagePickerLauncher.launch("image/*") }
            )
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .width(185.dp)
                    .height(125.dp)
                    .background(Color(226, 229, 234),),
                contentAlignment = Alignment.Center
            ) {
                var showColorPicker by remember { mutableStateOf(false) }
                ExtendedFloatingActionButton(
                    onClick = { showColorPicker = true },
                    icon = {
                        Box(Modifier.size(24.dp)) {
                            val myImage: Painter = painterResource(id = R.drawable.color)
                            Image(painter = myImage, contentDescription = "Edit")
                        }
                    },
                    text = {
                        Text(
                            text = "Background Colour", style = TextStyle(
                                fontSize = 20.sp, // Change font size here
                                color = Color.Black
                            )
                        )
                    },
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier
                        .fillMaxSize(),
                    containerColor = Color(226, 229, 234)
                )

                // Show color picker dialog
                if (showColorPicker) {
                    ColorPickerDialog(
                        onColorSelected = { color ->
                            onColorSelected(color)
                            showColorPicker = false
                        },
                        onDismissRequest = { showColorPicker = false }
                    )

                }
            }
        }

        Spacer(Modifier.height(10.dp))
        Row {
            Location{ location ->
                onLocationTextChanged(location)}
            Spacer(Modifier.width(10.dp))
            Hashtag(textState = textState) { hashtag ->
                onHashtagTextChanged(hashtag)
            }
        }

    }
}


@Composable fun Hashtag(textState: MutableState<TextFieldValue>, onHashtagEntered: (String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var hashtagText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .width(185.dp)
            .height(125.dp)
            .background(Color(226, 229, 234),),
        contentAlignment = Alignment.Center
    ) {
        ExtendedFloatingActionButton(
            onClick = { showDialog = true },
            icon = {
                Box(Modifier.size(24.dp)) {
                    val myImage: Painter = painterResource(id = R.drawable.hashtag)
                    Image(painter = myImage, contentDescription = "Edit")
                }
            },
            text = {
                Text(
                    text = "Hashtag",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                )
            },
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxSize(),
            containerColor = Color(226, 229, 234)
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Enter Hashtag") },
            text = {
                TextField(
                    value = hashtagText,
                    onValueChange = { hashtagText = it },
                    label = { Text("Hashtag") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    onHashtagEntered(hashtagText)
                    //textState.value = TextFieldValue(textState.value.text + " # " + hashtagText)
                    showDialog = false
                }) {
                    Text("Confirm")
                }
            }
        )
    }
}
@Composable
fun Location(onLocationEntered: (String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var locationText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .width(185.dp)
            .height(125.dp)
            .background(Color(226, 229, 234),),
        contentAlignment = Alignment.Center
    ) {
        ExtendedFloatingActionButton(
            onClick = { showDialog = true },
            icon = {
                Box(Modifier.size(24.dp)) {
                    val myImage: Painter = painterResource(id = R.drawable.location)
                    Image(painter = myImage, contentDescription = "Edit")
                }
            },
            text = {
                Text(
                    text = "Check In",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                )
            },
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxSize(),
            containerColor = Color(226, 229, 234)
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Enter Location") },
            text = {
                Column {
                    TextField(
                        value = locationText,
                        onValueChange = { locationText = it },
                        label = { Text("Location") }
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = {
                            onLocationEntered(locationText)
                            showDialog = false
                        }) {
                            Text("Confirm")
                        }
//                        Button(onClick = {
//                            // Use the Google Places API to search for locations
//                            val intent = Intent(context, SearchLocationActivity::class.java)
//                            context.startActivity(intent)
//                        }) {
//                            Text("Search Location")
//                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Dismiss")
                }
            }
        )
    }
}
class SearchLocationActivity : AppCompatActivity() {
    private lateinit var placesClient: PlacesClient
    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.your_layout)

        // Initialize the Places API
        Places.initialize(applicationContext, "AIzaSyBVx2rnzhZzQp_9Tj0JsZb9hEC3ViDtJ_k")
        placesClient = Places.createClient(this)

        // Set up the autocomplete fragment
        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // Get the selected location's coordinates
                val latLng = place.latLng
                val locationText = "${latLng?.latitude}, ${latLng?.longitude}"
                // Return the location text to the previous activity
                val intent = Intent()
                intent.putExtra("location", locationText)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

            override fun onError(status: Status) {
                // Handle the error
            }
        })
    }
}
@Composable
fun ColorPickerDialog(onColorSelected: (Color) -> Unit, onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select a Color") },
        text = {
            ColorPickerGrid(onColorSelected = onColorSelected)
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Close")
            }
        }
    )
}

@Composable
fun ColorPickerGrid(onColorSelected: (Color) -> Unit) {
    val colors = listOf(
        Color.Red, Color.Green, Color.Blue, Color.Yellow,
        Color.Magenta, Color.Cyan, Color.Black, Color.Gray
    )
    // Creating a grid of colors
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(count = colors.size) { index ->
            val color = colors[index]
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(color = color)
                    .clickable { onColorSelected(color) }
                    .padding(4.dp)
            )
        }
    }
}
@Composable
fun TopBar(onBackPressed: () -> Unit, onPostClicked: () -> Unit) {



    Row(
        modifier = Modifier
            .fillMaxWidth()
            //.background(colorResource(id = R.color.orange))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackPressed) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }

        val customTextStyle=TextStyle(
        fontFamily = fontFamily1,
        )
        Text(
            text = "Create Post",
            style=customTextStyle,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        )
        Button(onClick = onPostClicked,
            colors = ButtonDefaults.buttonColors(containerColor = (colorResource(id = R.color.orange))) ){
            Text("POST",
                style=customTextStyle,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = Color.White
            )
        }
    }
}

fun dateLabel(timestamp: Long, today: Date): String {
    Log.d("Timestamp", "Formatting timestamp: $timestamp") // Add this line
    val timestampDate = Date(timestamp * 1000)
    val calendar = Calendar.getInstance()
    calendar.time = today

    // Check if the timestamp is today
    if (isSameDay(timestampDate, today)) {
        return "Today"
    }

    // Check if the timestamp is yesterday
    calendar.add(Calendar.DAY_OF_YEAR, -1)
    if (isSameDay(timestampDate, calendar.time)) {
        return "Yesterday"
    }

    // Check if the timestamp is tomorrow
    calendar.add(Calendar.DAY_OF_YEAR, 2)
    if (isSameDay(timestampDate, calendar.time)) {
        return "Tomorrow"
    }

    // If none of the above, return a formatted date string
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return dateFormat.format(timestampDate)
}
private fun isSameDay(date1: Date, date2: Date): Boolean {
    val calendar1 = Calendar.getInstance()
    calendar1.time = date1
    val calendar2 = Calendar.getInstance()
    calendar2.time = date2
    return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
            calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
}


@Composable
fun ImagePicker(imageUri: Uri?, onImageSelected: () -> Unit) {
    Box(
        modifier = Modifier
            .width(185.dp)
            .height(125.dp),
        contentAlignment = Alignment.Center
    ) {
        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Adjust height as needed
                    .clip(RoundedCornerShape(8.dp)) // Optional: for rounded corners
            )
        }

        ExtendedFloatingActionButton(
            onClick = { onImageSelected() },
            icon = {Box(Modifier.size(24.dp)){
                val myImage: Painter = painterResource(id = R.drawable.image_gallery)
                Image(painter = myImage, contentDescription = "Edit")
            }},
            text = { Text(text = "Photo",style = TextStyle(
                fontSize = 20.sp,// Change font size here
                color = Color.Black
            )
            ) },
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxSize(),
            containerColor = Color(226, 229, 234)
        )
    }
}