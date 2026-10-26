package com.example.lsmapp

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Teal = Color(0xFF0B8F9E)
private val Aqua = Color(0xFF12A39F)
private val Deep = Color(0xFF0A6F86)
private val Moss = Color(0xFF4F8A74)
private val Rust = Color(0xFFC9582A)
private val Amber = Color(0xFFF08A0C)

private data class FloatingLetter(val c: String, val x: Float, val y: Float, val size: Int, val rot: Float, val dir: Int)

private val letters = listOf(
    FloatingLetter("v", .06f, .08f, 44, -12f, 1), FloatingLetter("w", .24f, .05f, 34, 8f, -1),
    FloatingLetter("c", .74f, .07f, 50, -6f, 1), FloatingLetter("a", .88f, .17f, 40, 14f, -1),
    FloatingLetter("t", .05f, .32f, 46, -10f, -1), FloatingLetter("s", .90f, .38f, 44, 10f, 1),
    FloatingLetter("o", .04f, .58f, 38, 6f, 1), FloatingLetter("b", .86f, .62f, 48, -8f, -1),
    FloatingLetter("x", .10f, .80f, 36, 12f, -1), FloatingLetter("e", .30f, .90f, 42, -14f, 1),
    FloatingLetter("u", .62f, .91f, 44, 8f, -1), FloatingLetter("n", .82f, .84f, 34, -6f, 1),
    FloatingLetter("k", .50f, .03f, 30, 10f, 1), FloatingLetter("h", .93f, .52f, 30, -12f, 1)
)

private val messages = listOf("Preparando tus señas…", "Cargando el catálogo…", "¡Casi listo!")

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val infinite = rememberInfiniteTransition(label = "splash")
    val spin by infinite.animateFloat(
        0f, 360f, infiniteRepeatable(tween(1800, easing = LinearEasing)), label = "spin"
    )
    val pulse by infinite.animateFloat(
        0.9f, 1.15f, infiniteRepeatable(tween(1300, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "pulse"
    )

    val logoScale = remember { Animatable(0.6f) }
    val progress = remember { Animatable(0f) }
    var msg by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        launch { logoScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)) }
        launch { while (true) { delay(1200); msg = (msg + 1) % messages.size } }
        progress.animateTo(1f, tween(3000, easing = FastOutSlowInEasing))
        delay(300)
        onFinished()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(Aqua, Teal, Deep, Moss),
                    start = Offset(0f, 0f), end = Offset(1000f, 2400f)
                )
            )
            .drawBehind {
                drawRect(
                    Brush.radialGradient(
                        listOf(Amber, Rust.copy(alpha = .85f), Color.Transparent),
                        center = Offset(size.width * 1.05f, size.height * 1.05f),
                        radius = size.width * 1.1f
                    )
                )
            }
    ) {
        val w = constraints.maxWidth
        val h = constraints.maxHeight

        // Letras flotantes (cada una con su propio movimiento)
        letters.forEachIndexed { i, l -> FloatingLetterView(l, i, w, h) }

        // Contenido central
        Column(
            modifier = Modifier.align(Alignment.Center).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(260.dp)) {
                // Resplandor
                Canvas(Modifier.fillMaxSize().scale(pulse)) {
                    drawCircle(
                        Brush.radialGradient(listOf(Color.White.copy(alpha = .28f), Color.Transparent))
                    )
                }
                // Anillo giratorio
                Canvas(Modifier.size(236.dp)) {
                    val stroke = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    drawCircle(Color.White.copy(alpha = .3f), style = stroke)
                    rotate(spin) {
                        drawArc(
                            Color.White, startAngle = -90f, sweepAngle = 90f,
                            useCenter = false, style = stroke,
                            topLeft = Offset.Zero, size = Size(size.width, size.height)
                        )
                    }
                }
                // Logo
                Image(
                    painter = painterResource(R.drawable.aprende_lsm_logo),
                    contentDescription = "Logo de Aprende LSM",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(190.dp)
                        .scale(logoScale.value)
                        .shadow(18.dp, RoundedCornerShape(percent = 22))
                        .clip(RoundedCornerShape(percent = 22))
                )
            }

            Spacer(Modifier.height(20.dp))
            Text(
                "Aprende LSM",
                color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Catálogo de Lengua de Señas Mexicana",
                color = Color.White.copy(alpha = .92f), fontSize = 16.sp,
                fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(28.dp))
            Box(
                Modifier.width(240.dp).height(8.dp)
                    .clip(RoundedCornerShape(50)).background(Color.White.copy(alpha = .25f))
            ) {
                Box(
                    Modifier.fillMaxHeight().fillMaxWidth(progress.value)
                        .clip(RoundedCornerShape(50))
                        .background(Brush.horizontalGradient(listOf(Color.White, Color(0xFFFFD9A0))))
                )
            }
            Spacer(Modifier.height(12.dp))
            Crossfade(targetState = msg, label = "msg") {
                Text(messages[it], color = Color.White.copy(alpha = .85f), fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun FloatingLetterView(l: FloatingLetter, index: Int, w: Int, h: Int) {
    val t = rememberInfiniteTransition(label = "letter$index")
    val dur = 2600 + (index % 5) * 450
    val start = StartOffset(index * 350)
    val dx by t.animateFloat(
        -24f, 24f,
        infiniteRepeatable(tween(dur + 700, easing = FastOutSlowInEasing), RepeatMode.Reverse, start),
        label = "dx"
    )
    val dy by t.animateFloat(
        -30f, 30f,
        infiniteRepeatable(tween(dur, easing = FastOutSlowInEasing), RepeatMode.Reverse, start),
        label = "dy"
    )
    val rot by t.animateFloat(
        -22f, 22f,
        infiniteRepeatable(tween(dur + 1200, easing = FastOutSlowInEasing), RepeatMode.Reverse, start),
        label = "rot"
    )
    val glow by t.animateFloat(
        .18f, .45f,
        infiniteRepeatable(tween(dur + 300, easing = LinearEasing), RepeatMode.Reverse, start),
        label = "glow"
    )
    val density = LocalDensity.current.density

    Text(
        text = l.c,
        color = Color.White,
        fontSize = l.size.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .offset {
                IntOffset(
                    (l.x * w + dx * l.dir * density).toInt(),
                    (l.y * h + dy * density).toInt()
                )
            }
            .graphicsLayer {
                rotationZ = l.rot + rot * l.dir
                alpha = glow
            }
    )
}