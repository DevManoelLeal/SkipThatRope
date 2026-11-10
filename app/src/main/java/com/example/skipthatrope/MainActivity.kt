package com.example.skipthatrope

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import com.example.skipthatrope.ui.theme.SkipThatRopeTheme
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.toArgb

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkipThatRopeTheme {
                LoginScreen()
            }
        }
    }
}

private val Cream = Color(0xFFFFFDF0)
private val TitleYellow = Color(0xFFFFC800)
private val TitleShadow = Color(0xFF7B2D2D)
private val GlowGreen = Color(0xFF3DDC84)
private val TaglineGreen = Color(0xFFB9F6CA)
private val BulletGreen = Color(0xFFC8F7D8)
private val BrandGreen = Color(0xFF00B84A)
private val FieldBorder = Color(0xFF6FD99A)
private val Lime = Color(0xFFDEFF8F)
private val TaglineFont: FontFamily = FontFamily.SansSerif
private val BodyFont: FontFamily = FontFamily.SansSerif

@Composable
fun LoginScreen(
    onLogin: (username: String, password: String) -> Unit = { _, _ -> },
    onCreateAccount: () -> Unit = {},
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState()),
    ) {
        ArchedTitle(
            text = "Skipping with you",
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(top = 24.dp),
        )

        Spacer(Modifier.height(24.dp))

        Tagline(modifier = Modifier.padding(start = 24.dp))

        Spacer(Modifier.height(40.dp))

        LoginCard(
            username = username,
            onUsernameChange = { username = it },
            password = password,
            onPasswordChange = { password = it },
            onLogin = { onLogin(username, password) },
            onCreateAccount = onCreateAccount,
        )
    }
}

@Composable
private fun ArchedTitle(text: String, modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val textSizePx = with(density) { 46.sp.toPx() }
    val shadowOffsetPx = with(density) { 2.dp.toPx() }

    Box(
        modifier = modifier.drawBehind {
            val padding = 16.dp.toPx()
            val diameter = size.width - padding * 2
            val oval = RectF(padding, padding + 40.dp.toPx(), padding + diameter, padding + 40.dp.toPx() + diameter)
            val path = Path().apply { addArc(oval, 200f, 140f) }

            val length = PathMeasure(path, false).length

            fun paint(color: Int) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.color = color
                textSize = textSizePx
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                letterSpacing = 0.02f
            }

            val yellow = paint(TitleYellow.toArgb())
            val maroon = paint(TitleShadow.toArgb())
            val hOffset = (length - yellow.measureText(text)) / 2f

            drawIntoCanvas { canvas ->
                val nc = canvas.nativeCanvas
                // shadow layer first
                nc.save()
                nc.translate(shadowOffsetPx, shadowOffsetPx)
                nc.drawTextOnPath(text, path, hOffset, 0f, maroon)
                nc.restore()
                // main text
                nc.drawTextOnPath(text, path, hOffset, 0f, yellow)
            }
        },
    )
}
@Composable
private fun Tagline(modifier: Modifier = Modifier) {
    val glowStyle = TextStyle(
        fontFamily = TaglineFont,
        fontWeight = FontWeight.Medium,
        fontSize = 32.sp,
        lineHeight = 52.sp,
        color = TaglineGreen,
        shadow = Shadow(color = GlowGreen, offset = Offset.Zero, blurRadius = 8f),
    )

    Column(modifier) {
        Text("TRACK YOUR:", style = glowStyle)
        listOf("CALORIES", "JUMPS", "WORKOUT TIME").forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.width(24.dp))
                Box(
                    Modifier
                        .size(22.dp)
                        .shadow(10.dp, CircleShape, ambientColor = GlowGreen, spotColor = GlowGreen)
                        .background(BulletGreen, CircleShape),
                )
                Spacer(Modifier.width(24.dp))
                Text(item, style = glowStyle)
            }
        }
    }
}

@Composable
private fun LoginCard(
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onCreateAccount: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(9.dp, BrandGreen), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(Cream, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 28.dp, bottomEnd = 28.dp))
            .padding(horizontal = 24.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FieldLabel("Username")
        GreenTextField(
            value = username,
            onValueChange = onUsernameChange,
            keyboardType = KeyboardType.Text,
        )

        Spacer(Modifier.height(16.dp))

        FieldLabel("Password")
        GreenTextField(
            value = password,
            onValueChange = onPasswordChange,
            keyboardType = KeyboardType.Password,
            isPassword = true,
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = BrandGreen),
        ) {
            Text("Login", fontFamily = BodyFont, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        }

        Button(
            onClick = onCreateAccount,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(44.dp),
            shape = RoundedCornerShape(22.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = Color.Black),
        ) {
            Text("CREATE ACCOUNT", fontFamily = BodyFont, fontSize = 16.sp)
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = BrandGreen,
        fontFamily = BodyFont,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
    )
}

@Composable
private fun GreenTextField(
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    isPassword: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandGreen,
            unfocusedBorderColor = FieldBorder,
            focusedContainerColor = Cream,
            unfocusedContainerColor = Cream,
            cursorColor = BrandGreen,
        ),
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun LoginScreenPreview() {
    LoginScreen()
}