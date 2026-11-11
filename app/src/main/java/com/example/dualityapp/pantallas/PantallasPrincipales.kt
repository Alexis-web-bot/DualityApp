package com.example.dualityapp.pantallas

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.dualityapp.datos.CuentaUsuario
import com.example.dualityapp.datos.RegistroWiki
import com.example.dualityapp.datos.ReinoMapa
import com.example.dualityapp.estado.ViewModelDuality
import com.example.dualityapp.tema.*

// =========================================================================
// PANTALLA 1: EL GRIMORIO WIKI (ESTILO PERGAMINO WEB EXACTO)
// =========================================================================
@Composable
fun PantallaWiki(vm: ViewModelDuality) {
    val configuracion = LocalConfiguration.current
    val esHorizontal = configuracion.orientation == Configuration.ORIENTATION_LANDSCAPE

    val categorias = if (vm.esIngles) {
        listOf(
            "personajes" to "Characters", 
            "clases" to "The 5 Classes", 
            "items" to "Items & Relics", 
            "enemigos" to "Void Creatures",
            "gremio" to "Guild, Ranks & Quests",
            "razas" to "Races & Factions"
        )
    } else {
        listOf(
            "personajes" to "Personajes", 
            "clases" to "Las 5 Clases", 
            "items" to "Ítems y Reliquias", 
            "enemigos" to "Criaturas del Vacío",
            "gremio" to "Gremio, Rangos y Misiones",
            "razas" to "Razas y Facciones"
        )
    }
    
    // Iconos actualizados para las 6 categorías
    val iconosCategorias = listOf(
        Icons.Default.Person, 
        Icons.Default.Hexagon, 
        Icons.Default.Inventory2, 
        Icons.Default.PestControl,
        Icons.Default.WorkspacePremium, // Para el gremio
        Icons.Default.Flag // Para las facciones
    )

    val itemsFiltrados = vm.catalogoWiki.filter { item ->
        val coincideCategoria = if (vm.busquedaWiki.isNotBlank()) true else item.categoria == vm.categoriaWikiSeleccionada
        val coincideBusqueda = vm.busquedaWiki.isBlank() ||
                item.nombre.contains(vm.busquedaWiki, true) ||
                item.subtitulo.contains(vm.busquedaWiki, true) ||
                item.descripcion.contains(vm.busquedaWiki, true)
        val coincideFav = !vm.mostrarSoloFavoritos || vm.idsFavoritos.contains(item.id)
        coincideCategoria && coincideBusqueda && coincideFav
    }
    
    // Textos de Cabecera Dinámicos
    val tituloSeccion = categorias.find { it.first == vm.categoriaWikiSeleccionada }?.second ?: ""
    val descripcionSeccion = when(vm.categoriaWikiSeleccionada) {
        "clases" -> if (vm.esIngles) "Fighting styles used by guild members to survive the impending war." else "Estilos de pelea utilizados por los miembros del gremio para sobrevivir a la guerra inminente."
        "personajes" -> if (vm.esIngles) "Heroes and allies fighting for the survival of Aethelgard." else "Héroes y aliados que luchan por la supervivencia de Aethelgard."
        "enemigos" -> if (vm.esIngles) "Creatures sent to eradicate life and accelerate the temporal loop restart." else "Criaturas enviadas para erradicar la vida y acelerar el reinicio del bucle temporal." // Actualizado según la captura web
        "items" -> if (vm.esIngles) "Essential tools to purify corrosion and survive." else "Herramientas esenciales para purificar la corrosión y sobrevivir." // Actualizado según la captura web
        "gremio" -> if (vm.esIngles) "The state of the world and the organization that shelters the marginalized." else "El estado del mundo y la organización que refugia a los marginados." // Nueva categoría
        "razas" -> if (vm.esIngles) "Cosmic entities and inhabitants of Aethelgard." else "Entidades cósmicas y habitantes de Aethelgard." // Nueva categoría
        else -> ""
    }

    val FormaBanderin = GenericShape { size, _ ->
        moveTo(0f, 0f)
        lineTo(size.width, 0f)
        lineTo(size.width, size.height)
        lineTo(size.width / 2f, size.height - 15f)
        lineTo(0f, size.height)
        close()
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF161026)).padding(horizontal = 16.dp, vertical = 10.dp)) {
        
        // Pestañas (Menú de Categorías Estilo Web)
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categorias.forEachIndexed { index, (key, label) ->
                val seleccionado = vm.categoriaWikiSeleccionada == key && vm.busquedaWiki.isBlank()
                OutlinedButton(
                    onClick = { vm.categoriaWikiSeleccionada = key; vm.busquedaWiki = "" },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (seleccionado) Color(0xFFD4AF37) else Color(0xFF192341),
                        contentColor = if (seleccionado) Color(0xFF192341) else Color(0xFFEAD48F)
                    ),
                    border = BorderStroke(1.dp, if (seleccionado) Color(0xFFD4AF37) else Color(0xFF334A80)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(iconosCategorias[index], contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // --- CONTENEDOR DEL PERGAMINO GIGANTE ---
        Box(modifier = Modifier.fillMaxSize()) {
            
            // Cuerpo del Pergamino
            Card(
                shape = RoundedCornerShape(0.dp), // Es cuadrado para simular papel continuo
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEBE1B5)), // Crema Web
                border = BorderStroke(1.dp, Color(0xFFC7BC8F)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxSize().padding(top = 18.dp) // Deja espacio para el rodillo superior
            ) {
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(start = 24.dp, end = 24.dp, top = 40.dp, bottom = 40.dp)) {
                    
                    // Cabecera del Pergamino
                    Text(
                        text = if (vm.esIngles) "Section: $tituloSeccion" else "Sección: $tituloSeccion", 
                        color = Color(0xFF222222), 
                        fontSize = 24.sp, 
                        fontWeight = FontWeight.Bold, 
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(descripcionSeccion, color = Color(0xFF444444), fontSize = 13.sp, fontFamily = FontFamily.Serif)
                    Spacer(Modifier.height(12.dp))
                    
                    // Etiqueta de cantidad de registros
                    Surface(color = Color(0xFF334A80), shape = RoundedCornerShape(4.dp)) { // Azul claro web
                        Text("${itemsFiltrados.size} ${if(vm.esIngles) "Records" else "Registros"}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                    
                    Spacer(Modifier.height(20.dp))
                    Divider(color = Color(0xFFC7BC8F), thickness = 1.dp)
                    Spacer(Modifier.height(20.dp))

                    // --- TARJETAS DE CLASES/PERSONAJES ---
                    itemsFiltrados.forEach { item ->
                        val esFav = vm.idsFavoritos.contains(item.id)
                        
                        Surface(
                            color = Color(0xFFDFD4A3), // Crema oscuro para la tarjeta
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFC7BC8F)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).clickable { vm.itemInspeccionado = item }
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                                // VISOR DE IMAGEN O ICONO (Cuadrado con esquinas redondas azules, como en la web)
                                Surface(
                                    shape = RoundedCornerShape(8.dp), 
                                    color = Color(0xFF334A80), // Azul brillante
                                    border = BorderStroke(1.dp, Color(0xFF2B4171)), 
                                    modifier = Modifier.size(56.dp).padding(top = 4.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        if (item.imagenRes != null) {
                                            Image(painter = painterResource(id = item.imagenRes), contentDescription = item.nombre, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                        } else {
                                            Icon(item.icono, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp)) // Icono blanco
                                        }
                                    }
                                }

                                Spacer(Modifier.width(16.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text(item.nombre, color = Color(0xFF222222), fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = FontFamily.Serif)
                                        IconButton(onClick = { vm.alternarFavorito(item.id) }, modifier = Modifier.size(24.dp)) {
                                            Icon(if (esFav) Icons.Default.Star else Icons.Default.StarBorder, contentDescription = null, tint = if (esFav) Color(0xFFA02020) else Color(0xFF888888))
                                        }
                                    }
                                    Text(item.subtitulo, color = Color(0xFFA02020), fontWeight = FontWeight.Bold, fontSize = 11.sp) // Subtítulo Rojo Web
                                    Spacer(Modifier.height(8.dp))
                                    Text(item.descripcion, color = Color(0xFF333333), fontSize = 13.sp, fontFamily = FontFamily.Serif, lineHeight = 18.sp)
                                    Spacer(Modifier.height(12.dp))
                                    Divider(color = Color(0xFFC7BC8F).copy(alpha = 0.5f), thickness = 1.dp) // Línea divisoria muy sutil
                                    Spacer(Modifier.height(8.dp))
                                    Text(item.extra, color = Color(0xFF2B4171), fontWeight = FontWeight.Bold, fontSize = 11.sp) // Extra Azul oscuro
                                }
                            }
                        }
                    }
                }
            }

            // Rodillo Dorado Superior (Simula el eje del pergamino)
            Surface(
                color = Color(0xFFEAD48F), // Dorado claro
                shape = RoundedCornerShape(50), // Bordes súper redondos como cilindro
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth().height(18.dp).offset(y = 10.dp).padding(horizontal = 4.dp)
            ) {}
            // Tapones del Rodillo Superior
            Surface(color = Color(0xFFD4AF37), shape = RoundedCornerShape(4.dp), modifier = Modifier.width(12.dp).height(24.dp).align(Alignment.TopStart).offset(y = 7.dp)) {}
            Surface(color = Color(0xFFD4AF37), shape = RoundedCornerShape(4.dp), modifier = Modifier.width(12.dp).height(24.dp).align(Alignment.TopEnd).offset(y = 7.dp)) {}

            // EL LISTÓN ROJO CON FORMA DE BANDERÍN (Muerde el rodillo superior)
            Surface(
                color = Color(0xFFA02020), // Rojo profundo web
                shape = FormaBanderin,
                shadowElevation = 6.dp,
                modifier = Modifier.width(36.dp).height(50.dp).align(Alignment.TopCenter).offset(y = 10.dp)
            ) {}
            
            // Rodillo Inferior
            Surface(
                color = Color(0xFFEAD48F), // Dorado claro
                shape = RoundedCornerShape(50), 
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth().height(18.dp).align(Alignment.BottomCenter).offset(y = (-10).dp).padding(horizontal = 4.dp)
            ) {}
            // Tapones del Rodillo Inferior
            Surface(color = Color(0xFFD4AF37), shape = RoundedCornerShape(4.dp), modifier = Modifier.width(12.dp).height(24.dp).align(Alignment.BottomStart).offset(y = (-7).dp)) {}
            Surface(color = Color(0xFFD4AF37), shape = RoundedCornerShape(4.dp), modifier = Modifier.width(12.dp).height(24.dp).align(Alignment.BottomEnd).offset(y = (-7).dp)) {}
        }
    }

    if (vm.itemInspeccionado != null) {
        DialogoInspeccion(item = vm.itemInspeccionado!!) { vm.itemInspeccionado = null }
    }
}

@Composable
fun DialogoInspeccion(item: RegistroWiki, alCerrar: () -> Unit) {
    Dialog(onDismissRequest = alCerrar) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SuperficieOscura),
            border = BorderStroke(2.dp, DoradoPrimario),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(140.dp).clip(CircleShape).background(Brush.radialGradient(listOf(item.colorAcento, AzulProfundo))).border(2.dp, DoradoClaro, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.imagenRes != null) {
                        Image(painter = painterResource(id = item.imagenRes), contentDescription = item.nombre, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Icon(item.icono, contentDescription = null, tint = DoradoClaro, modifier = Modifier.size(64.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                Surface(color = AzulGremio, shape = RoundedCornerShape(50), border = BorderStroke(1.dp, DoradoPrimario)) {
                    Text(item.subtitulo, color = DoradoClaro, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                }
                Spacer(Modifier.height(8.dp))
                Text(item.nombre, fontSize = 24.sp, fontWeight = FontWeight.Black, color = DoradoClaro, fontFamily = FontFamily.Serif)
                Spacer(Modifier.height(8.dp))
                Text(item.descripcion, fontSize = 14.sp, color = Color.White, textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Text(item.extra, fontSize = 12.sp, color = DoradoPrimario, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(18.dp))
                Button(onClick = alCerrar, colors = ButtonDefaults.buttonColors(containerColor = DoradoPrimario, contentColor = AzulProfundo), modifier = Modifier.fillMaxWidth()) {
                    Text("CERRAR ARCHIVO", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

// =========================================================================
// PANTALLA 2: MAPA DE AETHELGARD (MARCO DEL MAPA EXACTO A LA WEB)
// =========================================================================
@Composable
fun PantallaMapa(vm: ViewModelDuality) {
    val configuracion = LocalConfiguration.current
    val esHorizontal = configuracion.orientation == Configuration.ORIENTATION_LANDSCAPE
    val reinoActual = vm.mapaReinos.getOrNull(vm.indiceReinoSeleccionado) ?: vm.mapaReinos.first()

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        
        // Títulos Superiores (Colores web exactos)
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
             Surface(
                color = Color(0xFF192341),
                border = BorderStroke(1.dp, Color(0xFF334A80)),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = if (vm.esIngles) "OFFICIAL WORLD CARTOGRAPHY" else "CARTOGRAFÍA OFICIAL DEL MUNDO",
                    color = Color(0xFFAAA5C3),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    letterSpacing = 1.sp
                )
            }
            Text(
                text = if (vm.esIngles) "Continent and Realms of DUALITY" else "Continente y Reinos de DUALITY",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFEAD48F), 
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (vm.esIngles) "Select any of the 10 bastions directly on the map or in the scroll to check its lore, danger rank, and zone boss." else "Selecciona cualquiera de los 10 bastiones y regiones directamente sobre el mapa o en el pergamino para consultar su lore, rango de peligro y jefe de zona.",
                fontSize = 12.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
        }
        
        Spacer(Modifier.height(16.dp))

        // --- CONTENEDOR DEL MAPA (EFECTO DE MARCO DE GALERÍA WEB) ---
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF192341)), // Fondo azul oscuro del paspartú
            border = BorderStroke(3.dp, Color(0xFFD4AF37)), // Borde dorado fuerte exterior
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(if (esHorizontal) 2.5f else 1.35f) 
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp) // Este padding crea el espacio azul oscuro entre el borde dorado y el mapa
            ) {
                Image(
                    painter = painterResource(id = com.example.dualityapp.R.drawable.mapa_duality),
                    contentDescription = "Mapa oficial de Duality",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)) // Redondea las esquinas de la imagen del mapa
                        .border(1.dp, Color(0xFF334A80), RoundedCornerShape(8.dp)), // Borde sutil interno del mapa
                    contentScale = ContentScale.Crop
                )
            }
        }
        
        Spacer(Modifier.height(8.dp))

        // Footer del mapa
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(modifier = Modifier.weight(0.7f), verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.Info, contentDescription = null, tint = DoradoPrimario, modifier = Modifier.size(14.dp).padding(top = 2.dp))
                Spacer(Modifier.width(6.dp))
                Text("Haz clic sobre cualquier número del mapa para inspeccionar el reino", color = Color.LightGray, fontSize = 10.sp, lineHeight = 14.sp)
            }
            Text(
                text = "Región Activa:\n${reinoActual.nombre} (${reinoActual.numero}/10)", 
                color = Color(0xFFEAD48F), 
                fontSize = 10.sp, 
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.3f)
            )
        }

        Spacer(Modifier.height(24.dp))

        if (esHorizontal) {
             Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                 Box(modifier = Modifier.weight(1f)) { DetalleReinoPergamino(reinoActual, vm) }
             }
        } else {
             DetalleReinoPergamino(reinoActual, vm)
        }
    }
}

@Composable
fun DetalleReinoPergamino(reino: ReinoMapa, vm: ViewModelDuality) {
    // Usamos un Box principal con padding superior para darle espacio al listón de sobresalir
    Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        
        // El Pergamino Principal (Color crema claro)
        Card(
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEBE1B5)), // Crema Web
            border = BorderStroke(1.dp, Color(0xFFC7BC8F)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)) {
                
                // Etiqueta Superior
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(color = Color(0xFF2B4171), shape = RoundedCornerShape(2.dp)) { // Azul web
                        Text(
                            text = reino.insignia,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(if (vm.esIngles) "Realm #${reino.numero}" else "Reino #${reino.numero}", color = Color(0xFFA02020), fontWeight = FontWeight.Black, fontSize = 12.sp) // Rojo web
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Título y Descripción (Textos muy oscuros)
                Text(reino.titulo, color = Color(0xFF222222), fontSize = 22.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Serif)
                Spacer(Modifier.height(10.dp))
                Text(reino.descripcion, color = Color(0xFF333333), fontSize = 13.sp, lineHeight = 20.sp)
                
                Spacer(Modifier.height(20.dp))
                
                // Caja de Detalles (Nivel, Peligros, Jefe)
                Surface(
                    color = Color(0xFFDFD4A3), // Crema ligeramente más oscuro
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFC7BC8F)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row {
                            Text(if (vm.esIngles) "Recommended Level: " else "Nivel Recomendado: ", color = Color(0xFF2B4171), fontSize = 12.sp, fontWeight = FontWeight.Bold) // Azul web
                            Text(reino.nivel, color = Color(0xFF222222), fontSize = 12.sp)
                        }
                        Row {
                            Text(if (vm.esIngles) "Zone Hazards: " else "Peligros de Zona: ", color = Color(0xFF2B4171), fontSize = 12.sp, fontWeight = FontWeight.Bold) // Azul web
                            Text(reino.peligros, color = Color(0xFF222222), fontSize = 12.sp)
                        }
                        Row {
                            Text(if (vm.esIngles) "Region Boss: " else "Jefe de Región: ", color = Color(0xFFA02020), fontSize = 12.sp, fontWeight = FontWeight.Bold) // Rojo web
                            Text(reino.jefe, color = Color(0xFF222222), fontSize = 12.sp, fontStyle = FontStyle.Italic)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
                
                // --- BOTONES DE VIAJE RÁPIDO ---
                Text(if (vm.esIngles) "FAST TRAVEL (10 REALMS):" else "VIAJE RÁPIDO POR LOS 10 REINOS:", color = Color(0xFF222222), fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                Spacer(Modifier.height(12.dp))
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    vm.mapaReinos.chunked(2).forEach { par ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            par.forEach { reinoIterado ->
                                val idx = reinoIterado.numero - 1
                                val seleccionado = vm.indiceReinoSeleccionado == idx
                                
                                Button(
                                    onClick = { vm.indiceReinoSeleccionado = idx },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (seleccionado) Color(0xFF2B4171) else Color(0xFFE2D6A8), // Azul activo, crema inactivo
                                        contentColor = if (seleccionado) Color.White else Color(0xFF222222)
                                    ),
                                    shape = RoundedCornerShape(4.dp),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                                    modifier = Modifier.weight(1f).height(42.dp)
                                ) { 
                                    Text(
                                        text = reinoIterado.nombre, 
                                        fontWeight = FontWeight.Bold, 
                                        fontSize = 12.sp, 
                                        maxLines = 1, 
                                        overflow = TextOverflow.Ellipsis
                                    ) 
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // EL LISTÓN ROJO CON FORMA DE BANDERÍN
        val FormaBanderin = GenericShape { size, _ ->
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            lineTo(size.width / 2f, size.height - 15f) // El corte triangular invertido
            lineTo(0f, size.height)
            close()
        }

        Surface(
            color = Color(0xFFA02020), // Rojo profundo web
            shape = FormaBanderin, // Aplica el recorte en V
            shadowElevation = 4.dp,
            modifier = Modifier
                .width(32.dp)
                .height(44.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-16).dp)
        ) {}
    }
}



// =========================================================================
// PANTALLA 3: CARRUSEL DE BIOMAS Y BUCLE (100% BILINGÜE Y PULIDA)
// =========================================================================
@Composable
fun PantallaBiomas(vm: ViewModelDuality) {
    val faseActiva = vm.fasesBucle[vm.indiceFaseBucle]
    val biomaActivo = vm.biomas[vm.indiceBiomaActual]

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        
        // --- CARRUSEL DE ESCENARIOS ---
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SuperficieOscura), border = BorderStroke(2.dp, DoradoPrimario)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    // Título bilingüe
                    Text(if (vm.esIngles) "SCENARIOS & BIOMES" else "ESCENARIOS Y BIOMAS", color = DoradoClaro, fontWeight = FontWeight.Black, fontSize = 16.sp, fontFamily = FontFamily.Serif)
                    Text("${vm.indiceBiomaActual + 1} / ${vm.biomas.size}", color = DoradoPrimario, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(Modifier.height(10.dp))

                Box(
                    modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp)).border(1.dp, DoradoClaro.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                ) {
                    if (biomaActivo.imagenRes != null) {
                        Image(painter = painterResource(id = biomaActivo.imagenRes), contentDescription = biomaActivo.titulo, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, AzulProfundo.copy(alpha=0.9f)))))
                    } else {
                        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(biomaActivo.coloresGradiente)))
                    }

                    Surface(color = Color.Black.copy(alpha = 0.65f), shape = RoundedCornerShape(50), border = BorderStroke(1.dp, DoradoPrimario), modifier = Modifier.align(Alignment.TopStart).padding(14.dp)) {
                        Text(biomaActivo.etiqueta, color = DoradoClaro, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }

                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(14.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(color = AzulGremio, shape = RoundedCornerShape(4.dp)) { Text(biomaActivo.reino, color = DoradoClaro, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) }
                            Surface(color = RojoCinta, shape = RoundedCornerShape(4.dp)) { Text(biomaActivo.amenaza, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(biomaActivo.titulo, color = DoradoClaro, fontWeight = FontWeight.Black, fontSize = 18.sp, fontFamily = FontFamily.Serif)
                        Text(biomaActivo.descripcion, color = Color.White.copy(alpha = 0.9f), fontSize = 11.sp, maxLines = 2)
                    }
                }

                Spacer(Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    // Botones Anterior/Siguiente bilingües
                    OutlinedButton(onClick = { vm.indiceBiomaActual = if (vm.indiceBiomaActual == 0) vm.biomas.size - 1 else vm.indiceBiomaActual - 1 }, border = BorderStroke(1.dp, DoradoPrimario)) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = DoradoClaro); Text(if (vm.esIngles) "Previous" else "Anterior", color = DoradoClaro)
                    }
                    OutlinedButton(onClick = { vm.indiceBiomaActual = (vm.indiceBiomaActual + 1) % vm.biomas.size }, border = BorderStroke(1.dp, DoradoPrimario)) {
                        Text(if (vm.esIngles) "Next" else "Siguiente", color = DoradoClaro); Icon(Icons.Default.ChevronRight, contentDescription = null, tint = DoradoClaro)
                    }
                }
            }
        }

        // --- TARJETA DEL BUCLE (CON ESTILO PERGAMINO WEB) ---
        Card(
            shape = RoundedCornerShape(8.dp), 
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEBE1B5)), // Crema Web
            border = BorderStroke(1.dp, Color(0xFFC7BC8F)), 
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Títulos bilingües y oscurecidos para el fondo crema
                Text(if (vm.esIngles) "THE TIME LOOP" else "EL BUCLE TEMPORAL", color = Color(0xFF222222), fontWeight = FontWeight.Black, fontSize = 18.sp, fontFamily = FontFamily.Serif)
                Text(if (vm.esIngles) "Tap each phase to check the corrosion progress:" else "Toca cada fase para consultar el avance de la corrosión:", color = Color(0xFF333333), fontSize = 12.sp)
                Spacer(Modifier.height(14.dp))

                // Botones de las fases (Solución al texto aplastado)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    vm.fasesBucle.forEachIndexed { idx, fase ->
                        val seleccionado = vm.indiceFaseBucle == idx
                        Button(
                            onClick = { vm.indiceFaseBucle = idx },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (seleccionado) Color(0xFF2B4171) else Color(0xFFDFD4A3), // Azul Web si está activo
                                contentColor = if (seleccionado) Color.White else Color(0xFF222222)
                            ),
                            shape = RoundedCornerShape(6.dp), 
                            contentPadding = PaddingValues(0.dp), // Evita que el padding interno aplaste el texto
                            modifier = Modifier.weight(1f).height(40.dp)
                        ) { 
                            // Texto en 1 sola línea (maxLines = 1) para que no se parta en "Fase \n III"
                            Text(if (vm.esIngles) "Phase ${fase.numero}" else "Fase ${fase.numero}", fontWeight = FontWeight.Bold, fontSize = 10.sp, maxLines = 1) 
                        }
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Tarjeta interna oscura de descripción
                Surface(
                    color = Color(0xFF192341), // Azul profundo web
                    shape = RoundedCornerShape(8.dp), 
                    border = BorderStroke(1.dp, Color(0xFFD4AF37)), 
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(faseActiva.tituloCompleto, color = Color(0xFFEAD48F), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Surface(color = Color(0xFF2B4171), shape = RoundedCornerShape(4.dp)) { 
                                Text(faseActiva.insignia, color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) 
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(faseActiva.descripcion, color = Color(0xFFEEEEEE), fontSize = 12.sp, lineHeight = 18.sp)
                        Spacer(Modifier.height(10.dp))
                        Text(faseActiva.efectoJugabilidad, color = Color(0xFFD4AF37), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// =========================================================================
// PANTALLA 4: PERFIL DE USUARIO Y CONFIGURACIÓN (BILINGÜE)
// =========================================================================
@Composable
fun PantallaPerfil(vm: ViewModelDuality) {
    val usuario = vm.usuarioActual ?: return
    val configuracion = LocalConfiguration.current
    val esHorizontal = configuracion.orientation == Configuration.ORIENTATION_LANDSCAPE

    var nuevoNombre by remember(usuario.nombreUsuario) { mutableStateOf(usuario.nombreUsuario) }
    var nuevoTitulo by remember(usuario.tituloOpcional) { mutableStateOf(usuario.tituloOpcional) }
    var actualPass by remember { mutableStateOf("") }
    var nuevaPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }

    val selectorFoto = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            usuario.fotoPerfilUri = uri.toString()
            vm.mostrarNotificacion(if (vm.esIngles) "📸 Profile photo updated" else "📸 Foto de perfil cargada")
        }
    }

    val insignias = listOf(
        Triple(if (vm.esIngles) "Initiate" else "Iniciado", EsmeraldaGremio, Icons.Default.Shield),
        Triple(if (vm.esIngles) "Primordial" else "Primordial", RojoCinta, Icons.Default.Whatshot),
        Triple(if (vm.esIngles) "Mage" else "Mago", AzulGremio, Icons.Default.AutoAwesome),
        Triple(if (vm.esIngles) "Leader" else "Líder", DoradoPrimario, Icons.Default.WorkspacePremium)
    )

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        
        // --- TARJETA DE PERFIL ---
        Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = SuperficieOscura), border = BorderStroke(2.dp, DoradoPrimario), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                val badge = insignias[usuario.indiceInsignia % insignias.size]
                Box(modifier = Modifier.size(96.dp).clip(CircleShape).background(Brush.radialGradient(listOf(badge.second, AzulProfundo))).border(3.dp, DoradoClaro, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(badge.third, contentDescription = null, tint = DoradoClaro, modifier = Modifier.size(48.dp))
                }
                Spacer(Modifier.height(10.dp))
                Text(usuario.nombreUsuario, fontSize = 22.sp, fontWeight = FontWeight.Black, color = DoradoClaro, fontFamily = FontFamily.Serif)
                Text(if (vm.esIngles && usuario.tituloOpcional == "Explorador del Gremio") "Guild Explorer" else usuario.tituloOpcional, fontSize = 12.sp, color = DoradoPrimario, fontWeight = FontWeight.Bold)
                Text(usuario.correo, fontSize = 12.sp, color = Color.LightGray)

                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = { selectorFoto.launch("image/*") }, colors = ButtonDefaults.buttonColors(containerColor = AzulGremio, contentColor = DoradoClaro), border = BorderStroke(1.dp, DoradoPrimario)) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(6.dp))
                        Text(if (vm.esIngles) "Upload Photo" else "Subir Foto", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(onClick = { usuario.indiceInsignia = (usuario.indiceInsignia + 1) % insignias.size; vm.mostrarNotificacion(if (vm.esIngles) "Badge updated" else "Insignia actualizada") }, border = BorderStroke(1.dp, DoradoPrimario)) {
                        Icon(Icons.Default.Autorenew, contentDescription = null, tint = DoradoClaro, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(6.dp))
                        Text(if (vm.esIngles) "Rotate" else "Rotar", color = DoradoClaro, fontSize = 12.sp)
                    }
                }
            }
        }

        // --- NUEVO: TARJETA DE AJUSTES (IDIOMA) ---
        Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = FondoTarjeta), border = BorderStroke(1.5.dp, DoradoPrimario.copy(alpha = 0.7f)), modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = DoradoClaro)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(if (vm.esIngles) "App Language" else "Idioma de la App", color = DoradoClaro, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(if (vm.esIngles) "English" else "Español", color = Color.LightGray, fontSize = 12.sp)
                    }
                }
                Switch(
                    checked = vm.esIngles,
                    onCheckedChange = { vm.esIngles = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = AzulProfundo, checkedTrackColor = DoradoPrimario, uncheckedThumbColor = Color.LightGray, uncheckedTrackColor = SuperficieOscura)
                )
            }
        }

        // --- FORMULARIOS (Pasando el idioma) ---
        if (esHorizontal) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) { TarjetaCambioNombre(vm, usuario, nuevoNombre, { nuevoNombre = it }, nuevoTitulo, { nuevoTitulo = it }) }
                Box(modifier = Modifier.weight(1f)) { TarjetaCambioPass(vm, usuario, actualPass, { actualPass = it }, nuevaPass, { nuevaPass = it }, confirmPass, { confirmPass = it }) }
            }
        } else {
            TarjetaCambioNombre(vm, usuario, nuevoNombre, { nuevoNombre = it }, nuevoTitulo, { nuevoTitulo = it })
            TarjetaCambioPass(vm, usuario, actualPass, { actualPass = it }, nuevaPass, { nuevaPass = it }, confirmPass, { confirmPass = it })
        }

        // --- BOTÓN CERRAR SESIÓN ---
        Button(
            onClick = {
                vm.activarAnimacionCarga("Cerrando los archivos...") {
                    vm.cerrarSesionFirebase()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = RojoCinta, contentColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null); Spacer(Modifier.width(8.dp))
            Text(if (vm.esIngles) "LOG OUT" else "CERRAR SESIÓN", fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun TarjetaCambioNombre(vm: ViewModelDuality, usuario: CuentaUsuario, nuevoNombre: String, onNombre: (String) -> Unit, nuevoTitulo: String, onTitulo: (String) -> Unit) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = FondoTarjeta), border = BorderStroke(1.5.dp, DoradoPrimario.copy(alpha = 0.7f)), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(if (vm.esIngles) "✏️ Edit Name and Title" else "✏️ Modificar Nombre y Título", color = DoradoClaro, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            
            OutlinedTextField(value = nuevoNombre, onValueChange = onNombre, label = { Text(if (vm.esIngles) "New Name *" else "Nuevo Nombre *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = nuevoTitulo, onValueChange = onTitulo, label = { Text(if (vm.esIngles) "Title (Optional)" else "Título (Opcional)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            
            Button(
                onClick = {
                    if (nuevoNombre.trim().isEmpty()) { vm.mostrarNotificacion(if (vm.esIngles) "⚠️ Name is required." else "⚠️ El nombre es obligatorio.") }
                    else {
                        usuario.nombreUsuario = nuevoNombre.trim()
                        usuario.tituloOpcional = if (nuevoTitulo.isBlank()) (if (vm.esIngles) "Explorer" else "Explorador") else nuevoTitulo.trim()
                        vm.mostrarNotificacion(if (vm.esIngles) "✅ Name updated." else "✅ Nombre actualizado.")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DoradoPrimario, contentColor = AzulProfundo), modifier = Modifier.fillMaxWidth()
            ) { Text(if (vm.esIngles) "SAVE NAME" else "GUARDAR NOMBRE", fontWeight = FontWeight.Black) }
        }
    }
}

@Composable
fun TarjetaCambioPass(vm: ViewModelDuality, usuario: CuentaUsuario, actual: String, onActual: (String) -> Unit, nueva: String, onNueva: (String) -> Unit, conf: String, onConf: (String) -> Unit) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = FondoTarjeta), border = BorderStroke(1.5.dp, DoradoPrimario.copy(alpha = 0.7f)), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(if (vm.esIngles) "🔒 Change Password" else "🔒 Cambiar Contraseña", color = DoradoClaro, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            
            OutlinedTextField(value = actual, onValueChange = onActual, label = { Text(if (vm.esIngles) "Current Password *" else "Actual *") }, visualTransformation = PasswordVisualTransformation(), singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = nueva, onValueChange = onNueva, label = { Text(if (vm.esIngles) "New (Min. 6 chars) *" else "Nueva (Mín. 6) *") }, visualTransformation = PasswordVisualTransformation(), singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = conf, onValueChange = onConf, label = { Text(if (vm.esIngles) "Confirm Password *" else "Confirmar *") }, visualTransformation = PasswordVisualTransformation(), singleLine = true, modifier = Modifier.fillMaxWidth())
            
            Button(
                onClick = {
                    when {
                        actual.isBlank() || nueva.isBlank() || conf.isBlank() -> vm.mostrarNotificacion(if (vm.esIngles) "⚠️ Fill all fields." else "⚠️ Llena todos los campos.")
                        actual != usuario.contrasena -> vm.mostrarNotificacion(if (vm.esIngles) "❌ Current password doesn't match." else "❌ La actual no coincide.")
                        nueva.length < 6 -> vm.mostrarNotificacion(if (vm.esIngles) "⚠️ Minimum 6 characters." else "⚠️ Mínimo 6 caracteres.")
                        nueva != conf -> vm.mostrarNotificacion(if (vm.esIngles) "⚠️ Passwords don't match." else "⚠️ No coinciden.")
                        else -> {
                            usuario.contrasena = nueva; onActual(""); onNueva(""); onConf("")
                            vm.mostrarNotificacion(if (vm.esIngles) "✅ Password updated." else "✅ Contraseña actualizada.")
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AzulGremio, contentColor = DoradoClaro), border = BorderStroke(1.dp, DoradoPrimario), modifier = Modifier.fillMaxWidth()
            ) { Text(if (vm.esIngles) "UPDATE PASSWORD" else "ACTUALIZAR", fontWeight = FontWeight.Bold) }
        }
    }
}