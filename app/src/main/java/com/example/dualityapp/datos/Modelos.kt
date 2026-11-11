package com.example.dualityapp.datos

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// =========================================================================
// MODELOS DE DATOS: ESTRUCTURAS PRINCIPALES DE LA WIKI
// =========================================================================

data class CuentaUsuario(
    var nombreUsuario: String,
    val correo: String,
    var contrasena: String,
    var tituloOpcional: String = "Explorador del Gremio",
    var indiceInsignia: Int = 0,
    var fotoPerfilUri: String? = null
)

data class RegistroWiki(
    val id: String,
    val categoria: String,
    val nombre: String,
    val subtitulo: String,
    val descripcion: String,
    val extra: String,
    val colorAcento: Color,
    val icono: ImageVector,
    val imagenRes: Int? = null // <-- NUEVO: Para enlazar las imágenes de res/drawable
)

data class ReinoMapa(
    val numero: Int,
    val nombre: String,
    val insignia: String,
    val titulo: String,
    val descripcion: String,
    val nivel: String,
    val peligros: String,
    val jefe: String
)

data class FaseBucle(
    val numero: String,
    val tituloCorto: String,
    val tituloCompleto: String,
    val insignia: String,
    val descripcion: String,
    val efectoJugabilidad: String
)

data class DiapositivaBioma(
    val titulo: String,
    val etiqueta: String,
    val reino: String,
    val amenaza: String,
    val descripcion: String,
    val coloresGradiente: List<Color>,
    val imagenRes: Int? = null // <-- NUEVO: Para enlazar las imágenes del carrusel
)