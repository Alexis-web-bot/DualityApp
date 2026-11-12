package com.example.dualityapp.pantallas

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dualityapp.R
import com.example.dualityapp.estado.ViewModelDuality
import com.example.dualityapp.tema.*

// =========================================================================
// ENRUTADOR Y NAVEGACIÓN PRINCIPAL
// Contiene el Menú Inferior (BottomBar), Animaciones de Carga y Transiciones.
// =========================================================================

@Composable
fun EnrutadorRaiz(vm: ViewModelDuality) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1A132B), AzulProfundo, Color(0xFF090612))))
    ) {
        // Transición animada (Fade) entre Login, Carga y App Principal
        Crossfade(
            targetState = Triple(vm.estaCargando, vm.usuarioActual != null, vm.pantallaAcceso),
            animationSpec = tween(500),
            label = "TransicionRaiz"
        ) { (cargando, logueado, rutaAuth) ->
            when {
                cargando -> PantallaCargaAnimada(vm.mensajeCarga)
                !logueado -> {
                    when (rutaAuth) {
                        "REGISTRO" -> PantallaRegistro(vm)
                        "RECUPERAR" -> PantallaRecuperar(vm)
                        else -> PantallaLogin(vm)
                    }
                }
                else -> ScaffoldPrincipalApp(vm)
            }
        }

        // Notificación Toast Estilo Duality
        AnimatedVisibility(
            visible = vm.mensajeNotificacion != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 18.dp, start = 16.dp, end = 16.dp)
        ) {
            Surface(
                color = AzulProfundo,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(2.dp, DoradoPrimario),
                shadowElevation = 12.dp
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = DoradoClaro, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(text = vm.mensajeNotificacion ?: "", color = DoradoClaro, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldPrincipalApp(vm: ViewModelDuality) {
    val usuario = vm.usuarioActual ?: return

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            // Barra Superior (Se mantiene intacta con el Dragón)
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF151024)),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = AzulGremio,
                            border = BorderStroke(1.5.dp, DoradoPrimario),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = com.example.dualityapp.R.drawable.ouroboros_dorado),
                                    contentDescription = "Logo Duality",
                                    modifier = Modifier.size(24.dp),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                                )
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("DUALITY", fontWeight = FontWeight.Black, fontSize = 18.sp, color = DoradoClaro, letterSpacing = 2.sp, fontFamily = FontFamily.Serif)
                            Text(if (vm.esIngles) "OFFICIAL WIKI GRIMOIRE" else "GRIMORIO WIKI OFICIAL", fontSize = 10.sp, color = Color.LightGray)
                        }
                    }
                },
                actions = {
                    Surface(
                        onClick = { vm.pestanaActualInferior = 3 },
                        shape = RoundedCornerShape(50),
                        color = FondoTarjeta,
                        border = BorderStroke(1.dp, DoradoPrimario),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = DoradoClaro, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(usuario.nombreUsuario, color = DoradoClaro, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        },
        bottomBar = {
            // =========================================================================
            // BARRA DE NAVEGACIÓN INFERIOR (DISEÑO PREMIUM RPG)
            // =========================================================================
            Surface(
                color = Color(0xFF0C0814), // Un fondo casi negro, mucho más elegante
                border = BorderStroke(1.dp, DoradoPrimario.copy(alpha = 0.2f)),
                shadowElevation = 24.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp) // Curvas suaves en la parte superior
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp), // Más espacio para respirar
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Íconos temáticos redondeados
                    val pestañas = listOf(
                        Triple(0, if (vm.esIngles) "Grimoire" else "Grimorio", Icons.Rounded.AutoStories),
                        Triple(1, if (vm.esIngles) "Map" else "Mapa", Icons.Rounded.Explore),
                        Triple(2, if (vm.esIngles) "Loop" else "Bucle", Icons.Rounded.HourglassBottom),
                        Triple(4, if (vm.esIngles) "Tavern" else "Taberna", Icons.Rounded.Forum), // <--- NUEVA PESTAÑA COMUNIDAD
                        Triple(3, if (vm.esIngles) "Profile" else "Perfil", Icons.Rounded.Shield)
                    )

                    pestañas.forEach { (indice, titulo, icono) ->
                        val seleccionado = vm.pestanaActualInferior == indice
                        
                        // Contenedor del Botón (Píldora Perfecta)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp) // Separación entre pastillas
                                .clip(CircleShape) // ¡Esto lo hace una pastilla 100% redondeada y elegante!
                                .background(if (seleccionado) DoradoPrimario else Color.Transparent)
                                .clickable { vm.pestanaActualInferior = indice }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = icono,
                                    contentDescription = titulo,
                                    tint = if (seleccionado) AzulProfundo else DoradoClaro.copy(alpha = 0.7f), // Apagado si no está seleccionado
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = titulo,
                                    fontSize = 11.sp,
                                    fontWeight = if (seleccionado) FontWeight.Black else FontWeight.Medium,
                                    color = if (seleccionado) AzulProfundo else DoradoClaro.copy(alpha = 0.7f),
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingInterno ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingInterno)) {
            Crossfade(targetState = vm.pestanaActualInferior, animationSpec = tween(350), label = "TransicionPestanas") { pestana ->
                when (pestana) {
                    0 -> PantallaWiki(vm)
                    1 -> PantallaMapa(vm)
                    2 -> PantallaBiomas(vm)
                    4 -> PantallaComunidad(vm) // <--- LLAMADA A LA NUEVA PANTALLA
                    3 -> PantallaPerfil(vm)
                }
            }
        }
    }
}

// =========================================================================
// ANIMACIÓN DE CARGA EXIGIDA EN LA RÚBRICA (CON EL LOGO REAL)
// =========================================================================
@Composable
fun PantallaCargaAnimada(mensaje: String) {
    val animacionInfinita = rememberInfiniteTransition(label = "GiroOuroboros")
    val angulo by animacionInfinita.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "AnguloRotacion"
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(150.dp)) {
            // Anillos rúnicos girando
            Canvas(modifier = Modifier.fillMaxSize().rotate(angulo)) {
                drawCircle(color = DoradoPrimario, style = Stroke(width = 6f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 14f), 0f)))
                drawCircle(color = AzulGremio, radius = size.minDimension / 2.4f, style = Stroke(width = 8f))
            }
            
            // TU LOGO REAL (Girando en sentido contrario para efecto mágico)
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = com.example.dualityapp.R.drawable.ouroboros_dorado),
                contentDescription = "Ouroboros",
                modifier = Modifier
                    .size(76.dp)
                    .rotate(-angulo * 0.5f), // Gira suavemente a la inversa
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
        }
        Spacer(Modifier.height(24.dp))
        Text("DUALITY • GRIMORIO", fontSize = 22.sp, fontWeight = FontWeight.Black, color = DoradoClaro, letterSpacing = 3.sp, fontFamily = FontFamily.Serif)
        Spacer(Modifier.height(8.dp))
        Text(mensaje, fontSize = 14.sp, color = Color.LightGray, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        LinearProgressIndicator(color = DoradoPrimario, trackColor = SuperficieOscura, modifier = Modifier.width(200.dp).height(6.dp).clip(RoundedCornerShape(50)))
    }
}