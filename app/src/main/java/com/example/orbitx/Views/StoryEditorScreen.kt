package com.example.orbitx.Views

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.orbitx.ViewModel.AuthViewModel
import com.example.orbitx.ViewModel.StickerElement
import com.example.orbitx.ViewModel.TextElement
import java.util.UUID
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.cos

data class EditableText(
    val id: String = UUID.randomUUID().toString(),
    var text: String,
    var color: Color = Color.White,
    var bgStyle: String = "NONE",
    var fontFamily: String = "DEFAULT",
    var offset: Offset = Offset(200f, 400f),
    var scale: Float = 1f,
    var rotation: Float = 0f
)

data class EditableSticker(
    val id: String = UUID.randomUUID().toString(),
    var emoji: String,
    var offset: Offset = Offset(200f, 600f),
    var scale: Float = 1.5f,
    var rotation: Float = 0f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryEditorScreen(
    imageUri: Uri,
    viewModel: AuthViewModel = viewModel(),
    onDismiss: () -> Unit,
    onUploadComplete: () -> Unit
) {
    val context = LocalContext.current
    var isUploading by remember { mutableStateOf(false) }

    val textElements = remember { mutableStateListOf<EditableText>() }
    val stickerElements = remember { mutableStateListOf<EditableSticker>() }

    // Text Input State
    var isAddingText by remember { mutableStateOf(false) }
    var currentText by remember { mutableStateOf("") }
    var currentColor by remember { mutableStateOf(Color.White) }
    var currentBgStyle by remember { mutableStateOf("NONE") }
    var currentFontFamily by remember { mutableStateOf("DEFAULT") }
    
    // Sticker Input State
    var isAddingSticker by remember { mutableStateOf(false) }

    val colors = listOf(Color.White, Color.Black, Color.Red, Color.Blue, Color.Green, Color.Yellow, Color(0xFFD82B7E))
    val emojis = listOf("🔥", "❤️", "😂", "✨", "💯", "🎉", "👀", "😎", "🚀", "🌟")

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Image Layer
        AsyncImage(
            model = imageUri,
            contentDescription = "Background",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        // Render Stickers
        stickerElements.forEachIndexed { index, sticker ->
            Text(
                text = sticker.emoji,
                fontSize = 60.sp,
                modifier = Modifier
                    .offset { IntOffset(sticker.offset.x.roundToInt(), sticker.offset.y.roundToInt()) }
                    .graphicsLayer(
                        scaleX = sticker.scale,
                        scaleY = sticker.scale,
                        rotationZ = sticker.rotation
                    )
                    .pointerInput(sticker.id) {
                        detectTransformGestures { _, pan, zoom, rotation ->
                            val currentIdx = stickerElements.indexOfFirst { it.id == sticker.id }
                            if (currentIdx != -1) {
                                val current = stickerElements[currentIdx]
                                val angleRad = current.rotation * Math.PI / 180.0
                                val rotatedPanX = (pan.x * cos(angleRad) - pan.y * sin(angleRad)).toFloat()
                                val rotatedPanY = (pan.x * sin(angleRad) + pan.y * cos(angleRad)).toFloat()
                                val truePan = Offset(rotatedPanX, rotatedPanY) * current.scale
                                
                                stickerElements[currentIdx] = current.copy(
                                    offset = current.offset + truePan,
                                    scale = current.scale * zoom,
                                    rotation = current.rotation + rotation
                                )
                            }
                        }
                    }
            )
        }

        // Render Texts
        textElements.forEachIndexed { index, textEl ->
            val font = when (textEl.fontFamily) {
                "SERIF" -> FontFamily.Serif
                "MONOSPACE" -> FontFamily.Monospace
                "CURSIVE" -> FontFamily.Cursive
                else -> FontFamily.Default
            }
            
            val bgModifier = when (textEl.bgStyle) {
                "SOLID" -> Modifier.background(textEl.color, RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 4.dp)
                "SEMI" -> Modifier.background(textEl.color.copy(alpha=0.5f), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 4.dp)
                else -> Modifier
            }
            
            val textColor = if (textEl.bgStyle == "SOLID" || textEl.bgStyle == "SEMI") {
                if (textEl.color == Color.White || textEl.color == Color.Yellow) Color.Black else Color.White
            } else {
                textEl.color
            }

            Text(
                text = textEl.text,
                color = textColor,
                fontFamily = font,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .offset { IntOffset(textEl.offset.x.roundToInt(), textEl.offset.y.roundToInt()) }
                    .graphicsLayer(
                        scaleX = textEl.scale,
                        scaleY = textEl.scale,
                        rotationZ = textEl.rotation
                    )
                    .then(bgModifier)
                    .pointerInput(textEl.id) {
                        detectTransformGestures { _, pan, zoom, rotation ->
                            val currentIdx = textElements.indexOfFirst { it.id == textEl.id }
                            if (currentIdx != -1) {
                                val current = textElements[currentIdx]
                                val angleRad = current.rotation * Math.PI / 180.0
                                val rotatedPanX = (pan.x * cos(angleRad) - pan.y * sin(angleRad)).toFloat()
                                val rotatedPanY = (pan.x * sin(angleRad) + pan.y * cos(angleRad)).toFloat()
                                val truePan = Offset(rotatedPanX, rotatedPanY) * current.scale

                                textElements[currentIdx] = current.copy(
                                    offset = current.offset + truePan,
                                    scale = current.scale * zoom,
                                    rotation = current.rotation + rotation
                                )
                            }
                        }
                    }
            )
        }

        // Top Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss, modifier = Modifier.background(Color.Black.copy(alpha=0.4f), CircleShape)) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (textElements.isNotEmpty() || stickerElements.isNotEmpty()) {
                    Text(
                        text = "🗑️",
                        color = Color.White,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .clickable {
                                textElements.clear()
                                stickerElements.clear()
                            }
                            .background(Color.Black.copy(alpha=0.4f), CircleShape)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
                Text(
                    text = "Aa",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .clickable { isAddingText = true }
                        .background(Color.Black.copy(alpha=0.4f), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
                Text(
                    text = "😀",
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .clickable { isAddingSticker = true }
                        .background(Color.Black.copy(alpha=0.4f), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        // Bottom Upload Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 32.dp, end = 16.dp)
        ) {
            if (isUploading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                FloatingActionButton(
                    onClick = {
                        isUploading = true
                        val finalTexts = textElements.map { 
                            TextElement(it.id, it.text, java.lang.Integer.toHexString(it.color.toArgb()), it.bgStyle, it.fontFamily, it.offset.x, it.offset.y, it.scale, it.rotation) 
                        }
                        val finalStickers = stickerElements.map { 
                            StickerElement(it.id, it.emoji, it.offset.x, it.offset.y, it.scale, it.rotation) 
                        }
                        viewModel.uploadStoryImage(imageUri, context, finalTexts, finalStickers) { success ->
                            isUploading = false
                            if (success) {
                                onUploadComplete()
                            } else {
                                Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    containerColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Upload", tint = Color.Black)
                }
            }
        }

        // Add Text Overlay
        if (isAddingText) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable { 
                        if (currentText.isNotBlank()) {
                            textElements.add(EditableText(text = currentText, color = currentColor, bgStyle = currentBgStyle, fontFamily = currentFontFamily))
                        }
                        isAddingText = false
                        currentText = ""
                        currentColor = Color.White
                        currentBgStyle = "NONE"
                        currentFontFamily = "DEFAULT"
                    },
                contentAlignment = Alignment.Center
            ) {
                // Top controls for styling
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 40.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { 
                            currentBgStyle = when (currentBgStyle) {
                                "NONE" -> "SOLID"
                                "SOLID" -> "SEMI"
                                else -> "NONE"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha=0.3f))
                    ) {
                        Text(if (currentBgStyle == "NONE") "A" else "A*", color = Color.White)
                    }
                    Button(
                        onClick = { 
                            currentFontFamily = when (currentFontFamily) {
                                "DEFAULT" -> "SERIF"
                                "SERIF" -> "MONOSPACE"
                                "MONOSPACE" -> "CURSIVE"
                                else -> "DEFAULT"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha=0.3f))
                    ) {
                        Text(currentFontFamily.take(3), color = Color.White)
                    }
                }
                
                val font = when (currentFontFamily) {
                    "SERIF" -> FontFamily.Serif
                    "MONOSPACE" -> FontFamily.Monospace
                    "CURSIVE" -> FontFamily.Cursive
                    else -> FontFamily.Default
                }
                
                val textColor = if (currentBgStyle == "SOLID" || currentBgStyle == "SEMI") {
                    if (currentColor == Color.White || currentColor == Color.Yellow) Color.Black else Color.White
                } else currentColor
                
                val bgModifier = when (currentBgStyle) {
                    "SOLID" -> Modifier.background(currentColor, RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 4.dp)
                    "SEMI" -> Modifier.background(currentColor.copy(alpha=0.5f), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 4.dp)
                    else -> Modifier
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BasicTextField(
                        value = currentText,
                        onValueChange = { currentText = it },
                        textStyle = TextStyle(color = textColor, fontSize = 36.sp, fontWeight = FontWeight.Bold, fontFamily = font),
                        cursorBrush = SolidColor(textColor),
                        modifier = Modifier
                            .padding(32.dp)
                            .then(bgModifier)
                    )
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 16.dp)) {
                        colors.forEach { col ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(col, CircleShape)
                                    .border(2.dp, if (currentColor == col) Color.Gray else Color.Transparent, CircleShape)
                                    .clickable { currentColor = col }
                            )
                        }
                    }
                }
            }
        }
        
        // Add Sticker Bottom Sheet (Custom Dialog)
        if (isAddingSticker) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { isAddingSticker = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E1E), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Choose a Sticker", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Simple grid approximation using Rows
                    val chunkedEmojis = emojis.chunked(5)
                    chunkedEmojis.forEach { rowEmojis ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            rowEmojis.forEach { emoji ->
                                Text(
                                    text = emoji,
                                    fontSize = 42.sp,
                                    modifier = Modifier.clickable {
                                        stickerElements.add(EditableSticker(emoji = emoji))
                                        isAddingSticker = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
