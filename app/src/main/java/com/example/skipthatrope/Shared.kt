package com.example.skipthatrope


import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

// Moved the colours here
internal val Cream = Color(0xFFFFFDF0)
internal val TitleYellow = Color(0xFFFFC800)
internal val TitleShadow = Color(0xFF7B2D2D)
internal val GlowGreen = Color(0xFF3DDC84)
internal val TaglineGreen = Color(0xFFB9F6CA)
internal val BulletGreen = Color(0xFFC8F7D8)
internal val BrandGreen = Color(0xFF00B84A)
internal val Lime = Color(0xFFDEFF8F)

// Inside the app colours
internal val SkyBlue = Color(0xFFC0F4FF)
internal val SlateGray = Color(0xFFC8D0D9)

// Fonts to be here [Change later on, I need to decide on fonts yet]
internal val TaglineFont: FontFamily = FontFamily.SansSerif
internal val BodyFont: FontFamily = FontFamily.SansSerif
internal val DisplayFont: FontFamily = FontFamily.SansSerif

//Hamburger icon that opens a small dropdown, allowing user interaction to change info
@Composable
internal fun HamburgerMenu(items: List<Pair<String, () -> Unit>>) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.Black)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { (label, action) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        expanded = false
                        action()
                    },
                )
            }
        }
    }
}
