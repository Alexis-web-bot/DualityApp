package com.example.dualityapp.pantallas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dualityapp.R
import com.example.dualityapp.estado.ViewModelDuality
import com.example.dualityapp.tema.*

// =========================================================================
// MÓDULO DE AUTENTICACIÓN PROFESIONAL (DARK FANTASY)
// =========================================================================

@Composable
fun PantallaLogin(vm: ViewModelDuality) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var verContrasena by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F0B1A), AzulProfundo, Color(0xFF161026))
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = SuperficieOscura.copy(alpha = 0.95f)),
            border = BorderStroke(1.5.dp, DoradoPrimario.copy(alpha = 0.8f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Emblema Ouroboros Real con Resplandor
                Surface(
                    shape = CircleShape,
                    color = AzulGremioOscuro,
                    border = BorderStroke(2.dp, DoradoClaro),
                    shadowElevation = 8.dp,
                    modifier = Modifier.size(84.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.ouroboros_dorado),
                            contentDescription = "Ouroboros Duality",
                            modifier = Modifier.size(52.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    text = "DUALITY",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = DoradoClaro,
                    letterSpacing = 5.sp,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "COMPANION APP & WIKI",
                    fontSize = 11.sp,
                    color = DoradoPrimario,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Spacer(Modifier.height(24.dp))

                // Mensaje de Error
                AnimatedVisibility(
                    visible = mensajeError != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        color = RojoCinta.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, RojoCinta),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFCA5A5))
                            Spacer(Modifier.width(8.dp))
                            Text(text = mensajeError ?: "", color = Color(0xFFFECACA), fontSize = 12.sp)
                        }
                    }
                }

                // Campo Correo
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it; mensajeError = null },
                    label = { Text("Correo del Aventurero", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = DoradoPrimario) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DoradoPrimario,
                        unfocusedBorderColor = DoradoPrimario.copy(alpha = 0.4f),
                        focusedLabelColor = DoradoClaro,
                        unfocusedLabelColor = Color.LightGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(14.dp))

                // Campo Contraseña
                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it; mensajeError = null },
                    label = { Text("Contraseña Rúnica", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = DoradoPrimario) },
                    trailingIcon = {
                        IconButton(onClick = { verContrasena = !verContrasena }) {
                            Icon(
                                if (verContrasena) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = DoradoClaro
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (verContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DoradoPrimario,
                        unfocusedBorderColor = DoradoPrimario.copy(alpha = 0.4f),
                        focusedLabelColor = DoradoClaro,
                        unfocusedLabelColor = Color.LightGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(6.dp))



                Spacer(Modifier.height(12.dp))

                // Botón Ingresar
                Button(
                    onClick = {
                        val correoLimpio = correo.trim()
                        val passLimpio = contrasena.trim()
                        when {
                            correoLimpio.isEmpty() || passLimpio.isEmpty() -> {
                                mensajeError = "Todos los campos obligatorios (*) son requeridos."
                            }
                            !correoLimpio.contains("@") -> {
                                mensajeError = "Formato de correo electrónico inválido."
                            }
                            else -> {
                                vm.iniciarSesionFirebase(
                                    correo = correoLimpio,
                                    pass = passLimpio,
                                    alExito = { vm.mostrarNotificacion("¡Acceso concedido al Gremio!") },
                                    alError = { err -> mensajeError = err }
                                )
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DoradoPrimario, contentColor = AzulProfundo),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("ENTRAR AL GRIMORIO", fontWeight = FontWeight.Black, fontSize = 14.sp)
                }

                Spacer(Modifier.height(16.dp))

                // Enlaces inferiores
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { vm.pantallaAcceso = "REGISTRO" }) {
                        Text("Crear Cuenta", color = DoradoClaro, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    TextButton(onClick = { vm.pantallaAcceso = "RECUPERAR" }) {
                        Text("¿Olvidaste tu clave?", color = Color.LightGray, fontSize = 11.sp)
                    }
                }

                Spacer(Modifier.height(24.dp))

                // --- SELLO DE SEGURIDAD ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.GppGood, contentDescription = "Seguridad", tint = Color(0xFF4CAF50), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Conexión encriptada de extremo a extremo", 
                        color = Color.DarkGray, 
                        fontSize = 10.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaRegistro(vm: ViewModelDuality) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var verPass by remember { mutableStateOf(false) }
    var tituloOpcional by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F0B1A), AzulProfundo, Color(0xFF161026))
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = SuperficieOscura.copy(alpha = 0.95f)),
            border = BorderStroke(1.5.dp, DoradoPrimario.copy(alpha = 0.8f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "REGISTRO DEL GREMIO",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = DoradoClaro,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "Inscribe tu alma en los archivos de Aethelgard",
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(20.dp))

                if (mensajeError != null) {
                    Surface(
                        color = RojoCinta.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, RojoCinta),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = mensajeError ?: "",
                            color = Color(0xFFFECACA),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it; mensajeError = null },
                    label = { Text("Nombre de Aventurero", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = DoradoPrimario) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DoradoPrimario,
                        unfocusedBorderColor = DoradoPrimario.copy(alpha = 0.4f),
                        focusedLabelColor = DoradoClaro,
                        unfocusedLabelColor = Color.LightGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it; mensajeError = null },
                    label = { Text("Correo Electrónico", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = DoradoPrimario) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DoradoPrimario,
                        unfocusedBorderColor = DoradoPrimario.copy(alpha = 0.4f),
                        focusedLabelColor = DoradoClaro,
                        unfocusedLabelColor = Color.LightGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = pass,
                    onValueChange = { pass = it; mensajeError = null },
                    label = { Text("Contraseña (Mín. 6 caracteres)", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = DoradoPrimario) },
                    visualTransformation = if (verPass) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { verPass = !verPass }) {
                            Icon(if (verPass) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = DoradoPrimario)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DoradoPrimario,
                        unfocusedBorderColor = DoradoPrimario.copy(alpha = 0.4f),
                        focusedLabelColor = DoradoClaro,
                        unfocusedLabelColor = Color.LightGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = tituloOpcional,
                    onValueChange = { tituloOpcional = it },
                    label = { Text("Título o Clan (Opcional)", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    placeholder = { Text("Ej. Guardián del Bucle") },
                    leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null, tint = AzulGremio) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DoradoPrimario,
                        unfocusedBorderColor = DoradoPrimario.copy(alpha = 0.4f),
                        focusedLabelColor = DoradoClaro,
                        unfocusedLabelColor = Color.LightGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        val nom = nombre.trim()
                        val mail = correo.trim()
                        val pwd = pass.trim()
                        when {
                            nom.isEmpty() || mail.isEmpty() || pwd.isEmpty() -> {
                                mensajeError = "Completa todos los campos obligatorios."
                            }
                            !mail.contains("@") -> {
                                mensajeError = "Formato de correo electrónico inválido."
                            }
                            pwd.length < 6 -> {
                                mensajeError = "La contraseña debe tener al menos 6 caracteres."
                            }
                            else -> {
                                vm.registrarseFirebase(
                                    nombre = nom,
                                    correo = mail,
                                    pass = pwd,
                                    titulo = tituloOpcional.ifBlank { "Explorador Novato" },
                                    alExito = { 
                                        vm.mostrarNotificacion("¡Inscripción exitosa en el Gremio!") 
                                        vm.pantallaAcceso = "LOGIN" // Redirige al login de inmediato
                                    },
                                    alError = { err -> mensajeError = err }
                                )
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DoradoPrimario, contentColor = AzulProfundo),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.Default.HowToReg, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("REGISTRAR EN EL GREMIO", fontWeight = FontWeight.Black, fontSize = 13.sp)
                }

                Spacer(Modifier.height(14.dp))

                TextButton(onClick = { vm.pantallaAcceso = "LOGIN" }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = DoradoClaro, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Volver a Iniciar Sesión", color = DoradoClaro, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(24.dp))

                // --- SELLO DE SEGURIDAD ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.GppGood, contentDescription = "Seguridad", tint = Color(0xFF4CAF50), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Conexión encriptada de extremo a extremo", 
                        color = Color.DarkGray, 
                        fontSize = 10.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaRecuperar(vm: ViewModelDuality) {
    var correo by remember { mutableStateOf("") }
    var mensajeEstado by remember { mutableStateOf<String?>(null) }
    var hayError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F0B1A), AzulProfundo, Color(0xFF161026))
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = SuperficieOscura.copy(alpha = 0.95f)),
            border = BorderStroke(1.5.dp, DoradoPrimario.copy(alpha = 0.8f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.LockReset, contentDescription = null, tint = DoradoClaro, modifier = Modifier.size(52.dp))
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "RECUPERAR ACCESO",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = DoradoClaro,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "Restablece las credenciales rúnicas de tu cuenta",
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(20.dp))

                if (mensajeEstado != null) {
                    Surface(
                        color = if (hayError) RojoCinta.copy(alpha = 0.2f) else EsmeraldaGremio.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, if (hayError) RojoCinta else EsmeraldaGremio),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = mensajeEstado ?: "",
                            color = if (hayError) Color(0xFFFECACA) else Color(0xFFA7F3D0),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it; mensajeEstado = null },
                    label = { Text("Correo Registrado", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = DoradoPrimario) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DoradoPrimario,
                        unfocusedBorderColor = DoradoPrimario.copy(alpha = 0.4f),
                        focusedLabelColor = DoradoClaro,
                        unfocusedLabelColor = Color.LightGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        val mail = correo.trim()
                        when {
                            mail.isBlank() || !mail.contains("@") -> {
                                hayError = true
                                mensajeEstado = "Por favor, ingresa un correo válido."
                            }
                            else -> {
                                vm.recuperarContrasenaFirebase(
                                    correo = mail,
                                    alExito = {
                                        hayError = false
                                        vm.mostrarNotificacion("Enlace enviado. Revisa tu bandeja de entrada.")
                                        vm.pantallaAcceso = "LOGIN"
                                    },
                                    alError = { err -> 
                                        hayError = true
                                        mensajeEstado = err
                                    }
                                )
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulGremio, contentColor = DoradoClaro),
                    border = BorderStroke(1.dp, DoradoPrimario),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("RESTABLECER CONTRASEÑA", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(Modifier.height(14.dp))

                TextButton(onClick = { vm.pantallaAcceso = "LOGIN" }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = DoradoClaro, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Regresar a Iniciar Sesión", color = DoradoClaro, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// =========================================================================
// PANTALLA SPLASH DE BIENVENIDA (PANTALLA DE CARGA INICIAL DUALITY)
// =========================================================================
@Composable
fun PantallaSplash(onTiempoTerminado: () -> Unit) {
    // LaunchedEffect arranca automáticamente cuando la pantalla se muestra
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(5000L) // 5000 milisegundos = 5 segundos exactos
        onTiempoTerminado() // Llama a la acción de avanzar al login
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0814)), // Fondo ultra oscuro de Aethelgard
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Título de la app
            Text(
                text = "DUALITY",
                color = Color(0xFFD4AF37), // Dorado primario
                fontSize = 52.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif,
                letterSpacing = 8.sp
            )
            
            Spacer(Modifier.height(8.dp))
            
            Text(
                text = "OFFICIAL WIKI GRIMOIRE",
                color = Color.LightGray,
                fontSize = 12.sp,
                letterSpacing = 3.sp
            )
            
            Spacer(Modifier.height(64.dp))
            
            // Animación circular de carga (Spinner)
            CircularProgressIndicator(
                color = Color(0xFFA02020), // Rojo Cinta
                strokeWidth = 3.dp,
                modifier = Modifier.size(40.dp)
            )
            
            Spacer(Modifier.height(16.dp))
            
            Text(
                text = "Abriendo el grimorio...",
                color = Color.Gray,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                fontSize = 14.sp
            )
        }
    }
}