package com.example.dualityapp.pantallas

import android.content.Context
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import android.content.res.Configuration
import android.net.Uri
import coil.compose.AsyncImage
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
        Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(bottom = 16.dp)) {
            
            // Cuerpo del Pergamino
            Card(
                shape = RoundedCornerShape(0.dp), // Es cuadrado para simular papel continuo
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEBE1B5)), // Crema Web
                border = BorderStroke(1.dp, Color(0xFFC7BC8F)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                androidx.compose.foundation.lazy.LazyColumn(
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 60.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
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
                        
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFFC7BC8F), thickness = 1.dp)
                    }

                    // --- TARJETAS DE CLASES/PERSONAJES ---
                    items(itemsFiltrados.size) { index ->
                        val item = itemsFiltrados[index]
                        val esFav = vm.idsFavoritos.contains(item.id)
                        
                        Surface(
                            color = Color(0xFFDFD4A3), // Crema oscuro para la tarjeta
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFC7BC8F)),
                            modifier = Modifier.fillMaxWidth().clickable { vm.itemInspeccionado = item }
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                                // VISOR DE IMAGEN O ICONO
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
                                            Icon(item.icono, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
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
                                    Text(item.subtitulo, color = Color(0xFFA02020), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Spacer(Modifier.height(8.dp))
                                    Text(item.descripcion, color = Color(0xFF333333), fontSize = 13.sp, fontFamily = FontFamily.Serif, lineHeight = 18.sp)
                                    Spacer(Modifier.height(12.dp))
                                    HorizontalDivider(color = Color(0xFFC7BC8F).copy(alpha = 0.5f), thickness = 1.dp)
                                    Spacer(Modifier.height(8.dp))
                                    Text(item.extra, color = Color(0xFF2B4171), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Rodillo Dorado Superior (Simula el eje del pergamino)
            Surface(
                color = Color(0xFFEAD48F), shape = RoundedCornerShape(50), shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth().height(14.dp).align(Alignment.TopCenter).offset(y = (-7).dp).padding(horizontal = 4.dp)
            ) {}
            Surface(color = Color(0xFFD4AF37), shape = RoundedCornerShape(4.dp), modifier = Modifier.width(10.dp).height(20.dp).align(Alignment.TopStart).offset(y = (-10).dp)) {}
            Surface(color = Color(0xFFD4AF37), shape = RoundedCornerShape(4.dp), modifier = Modifier.width(10.dp).height(20.dp).align(Alignment.TopEnd).offset(y = (-10).dp)) {}

            // EL LISTÓN ROJO CON FORMA DE BANDERÍN
            Surface(
                color = Color(0xFFA02020), shape = FormaBanderin, shadowElevation = 4.dp,
                modifier = Modifier.width(32.dp).height(46.dp).align(Alignment.TopCenter).offset(y = (-7).dp)
            ) {}
            
            // Rodillo Inferior
            Surface(
                color = Color(0xFFEAD48F), shape = RoundedCornerShape(50), shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth().height(14.dp).align(Alignment.BottomCenter).offset(y = 7.dp).padding(horizontal = 4.dp)
            ) {}
            Surface(color = Color(0xFFD4AF37), shape = RoundedCornerShape(4.dp), modifier = Modifier.width(10.dp).height(20.dp).align(Alignment.BottomStart).offset(y = 4.dp)) {}
            Surface(color = Color(0xFFD4AF37), shape = RoundedCornerShape(4.dp), modifier = Modifier.width(10.dp).height(20.dp).align(Alignment.BottomEnd).offset(y = 4.dp)) {}
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

        // --- CONTENEDOR DEL MAPA INTERACTIVO CON PINES CLICKABLES ---
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(2.dp, Color(0xFFD4AF37), RoundedCornerShape(12.dp))
        ) {
            val anchoMapa = maxWidth
            val altoMapa = maxWidth * (if (esHorizontal) 0.45f else 0.72f)

            Image(
                painter = painterResource(id = com.example.dualityapp.R.drawable.mapa_duality),
                contentDescription = "Mapa oficial de Duality",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(altoMapa),
                contentScale = ContentScale.Crop
            )

            vm.mapaReinos.forEachIndexed { index, reino ->
                val seleccionado = vm.indiceReinoSeleccionado == index

                Box(
                    modifier = Modifier
                        .offset(
                            x = anchoMapa * reino.posX - 16.dp,
                            y = altoMapa * reino.posY - 32.dp
                        )
                        .size(32.dp)
                        .clickable { vm.indiceReinoSeleccionado = index },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = reino.nombre,
                        tint = if (seleccionado) Color(0xFFA02020) else Color(0xFFD4AF37),
                        modifier = Modifier.size(if (seleccionado) 32.dp else 22.dp)
                    )
                }
            }
        }
        
        Spacer(Modifier.height(8.dp))

        // --- 2. INSTRUCCIONES Y REGIÓN ACTIVA ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(modifier = Modifier.weight(0.7f), verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.Info, contentDescription = null, tint = DoradoPrimario, modifier = Modifier.size(14.dp).padding(top = 2.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (vm.esIngles) "Tap any region to inspect the realm" else "Toca cualquier región para inspeccionar el reino", 
                    color = Color.LightGray, 
                    fontSize = 10.sp, 
                    lineHeight = 14.sp
                )
            }
            
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(0.3f)) {
                Text(text = if (vm.esIngles) "Active Region:" else "Región Activa:", color = Color(0xFFEAD48F), fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                Text(text = "${reinoActual.nombre} (${reinoActual.numero}/10)", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.End)
            }
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
    val FormaBanderin = GenericShape { size, _ ->
        moveTo(0f, 0f)
        lineTo(size.width, 0f)
        lineTo(size.width, size.height)
        lineTo(size.width / 2f, size.height - 15f)
        lineTo(0f, size.height)
        close()
    }

    Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        
        // Cuerpo del Pergamino
        Card(
            shape = RoundedCornerShape(0.dp), // Cuadrado para simular papel continuo
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEBE1B5)), // Crema Web
            border = BorderStroke(1.dp, Color(0xFFC7BC8F)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp) // Espacio para rodillos
        ) {
            Column(modifier = Modifier.padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 28.dp)) {
                
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
        
        // Rodillo Dorado Superior
        Surface(
            color = Color(0xFFEAD48F), // Dorado claro
            shape = RoundedCornerShape(50), 
            shadowElevation = 12.dp,
            modifier = Modifier.fillMaxWidth().height(18.dp).offset(y = 0.dp).padding(horizontal = 4.dp)
        ) {}
        Surface(color = Color(0xFFD4AF37), shape = RoundedCornerShape(4.dp), modifier = Modifier.width(12.dp).height(24.dp).align(Alignment.TopStart).offset(y = (-3).dp)) {}
        Surface(color = Color(0xFFD4AF37), shape = RoundedCornerShape(4.dp), modifier = Modifier.width(12.dp).height(24.dp).align(Alignment.TopEnd).offset(y = (-3).dp)) {}

        // EL LISTÓN ROJO CON FORMA DE BANDERÍN
        Surface(
            color = Color(0xFFA02020), // Rojo profundo web
            shape = FormaBanderin, // Aplica el recorte en V
            shadowElevation = 6.dp,
            modifier = Modifier
                .width(36.dp)
                .height(50.dp)
                .align(Alignment.TopCenter)
                .offset(y = 0.dp)
        ) {}

        // Rodillo Inferior
        Surface(
            color = Color(0xFFEAD48F), // Dorado claro
            shape = RoundedCornerShape(50), 
            shadowElevation = 12.dp,
            modifier = Modifier.fillMaxWidth().height(18.dp).align(Alignment.BottomCenter).offset(y = 0.dp).padding(horizontal = 4.dp)
        ) {}
        Surface(color = Color(0xFFD4AF37), shape = RoundedCornerShape(4.dp), modifier = Modifier.width(12.dp).height(24.dp).align(Alignment.BottomStart).offset(y = 3.dp)) {}
        Surface(color = Color(0xFFD4AF37), shape = RoundedCornerShape(4.dp), modifier = Modifier.width(12.dp).height(24.dp).align(Alignment.BottomEnd).offset(y = 3.dp)) {}
    }
}



// =========================================================================
// PANTALLA 3: CARRUSEL DE BIOMAS Y BUCLE (CORRECCIÓN DE PERGAMINO Y NIVELES)
// =========================================================================
@Composable
fun PantallaBiomas(vm: ViewModelDuality) {
    val faseActiva = vm.fasesBucle[vm.indiceFaseBucle]
    val biomaActivo = vm.biomas[vm.indiceBiomaActual]

    val FormaBanderin = GenericShape { size, _ ->
        moveTo(0f, 0f)
        lineTo(size.width, 0f)
        lineTo(size.width, size.height)
        lineTo(size.width / 2f, size.height - 15f)
        lineTo(0f, size.height)
        close()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        
        // --- 1. CARRUSEL DE ESCENARIOS Y NIVELES ---
        Card(
            shape = RoundedCornerShape(16.dp), 
            colors = CardDefaults.cardColors(containerColor = Color(0xFF192341)), // Azul profundo
            border = BorderStroke(2.dp, Color(0xFFD4AF37))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (vm.esIngles) "SCENARIOS & BIOMES" else "ESCENARIOS Y BIOMAS", color = Color(0xFFEAD48F), fontWeight = FontWeight.Black, fontSize = 16.sp, fontFamily = FontFamily.Serif)
                    Text("${vm.indiceBiomaActual + 1} / ${vm.biomas.size}", color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(Modifier.height(10.dp))

                // Contenedor de la Imagen del Nivel
                Box(modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp)).border(1.dp, Color(0xFFEAD48F).copy(alpha = 0.6f), RoundedCornerShape(12.dp))) {
                    if (biomaActivo.imagenRes != null) {
                        Image(
                            painter = painterResource(id = biomaActivo.imagenRes), 
                            contentDescription = biomaActivo.titulo, 
                            modifier = Modifier.fillMaxSize(), 
                            contentScale = ContentScale.Crop // Asegura que la imagen del nivel se vea completa y adaptada
                        )
                        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xFF0C0814).copy(alpha=0.9f)))))
                    } else {
                        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(biomaActivo.coloresGradiente)))
                    }

                    Surface(color = Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(50), border = BorderStroke(1.dp, Color(0xFFD4AF37)), modifier = Modifier.align(Alignment.TopStart).padding(14.dp)) {
                        Text(biomaActivo.etiqueta, color = Color(0xFFEAD48F), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }

                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(14.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(color = Color(0xFF2B4171), shape = RoundedCornerShape(4.dp)) { Text(biomaActivo.reino, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) }
                            Surface(color = Color(0xFFA02020), shape = RoundedCornerShape(4.dp)) { Text(biomaActivo.amenaza, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(biomaActivo.titulo, color = Color(0xFFEAD48F), fontWeight = FontWeight.Black, fontSize = 18.sp, fontFamily = FontFamily.Serif)
                        Text(biomaActivo.descripcion, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp, maxLines = 2)
                    }
                }

                Spacer(Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    OutlinedButton(onClick = { vm.indiceBiomaActual = if (vm.indiceBiomaActual == 0) vm.biomas.size - 1 else vm.indiceBiomaActual - 1 }, border = BorderStroke(1.dp, Color(0xFFD4AF37))) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color(0xFFEAD48F)); Text(if (vm.esIngles) "Previous" else "Anterior", color = Color(0xFFEAD48F))
                    }
                    OutlinedButton(onClick = { vm.indiceBiomaActual = (vm.indiceBiomaActual + 1) % vm.biomas.size }, border = BorderStroke(1.dp, Color(0xFFD4AF37))) {
                        Text(if (vm.esIngles) "Next" else "Siguiente", color = Color(0xFFEAD48F)); Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFEAD48F))
                    }
                }
            }
        }

        // --- 2. TARJETA DEL BUCLE (PERGAMINO CORREGIDO) ---
        Box(modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 10.dp)) {
            
            Card(
                shape = RoundedCornerShape(0.dp), 
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEBE1B5)), // Crema Web
                border = BorderStroke(1.dp, Color(0xFFC7BC8F)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // ¡AQUÍ ESTÁ LA MAGIA! Aumentamos el padding top a 48.dp para esquivar el listón rojo
                Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 48.dp, bottom = 30.dp)) {
                    
                    Text(
                        text = if (vm.esIngles) "THE TIME LOOP" else "EL BUCLE TEMPORAL", 
                        color = Color(0xFF222222), 
                        fontWeight = FontWeight.Black, 
                        fontSize = 22.sp, 
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (vm.esIngles) "Tap each phase to check the corrosion progress:" else "Toca cada fase para consultar el avance de la corrosión:", 
                        color = Color(0xFF444444), 
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                        vm.fasesBucle.forEachIndexed { idx, fase ->
                            val seleccionado = vm.indiceFaseBucle == idx
                            Button(
                                onClick = { vm.indiceFaseBucle = idx },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (seleccionado) Color(0xFF2B4171) else Color(0xFFDFD4A3),
                                    contentColor = if (seleccionado) Color.White else Color(0xFF222222)
                                ),
                                shape = RoundedCornerShape(6.dp), 
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) { 
                                Text(if (vm.esIngles) "Phase ${fase.numero}" else "Fase ${fase.numero}", fontWeight = FontWeight.Bold, fontSize = 10.sp, maxLines = 1) 
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(20.dp))
                    
                    // TARJETA INTERNA OSCURA (Recuperada para dar ese contraste RPG perfecto)
                    Surface(
                        color = Color(0xFF192341), // Azul profundo
                        shape = RoundedCornerShape(8.dp), 
                        border = BorderStroke(1.dp, Color(0xFFD4AF37)), 
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(faseActiva.tituloCompleto, color = Color(0xFFEAD48F), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Surface(color = Color(0xFFA02020), shape = RoundedCornerShape(4.dp)) { 
                                    Text(faseActiva.insignia, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) 
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(faseActiva.descripcion, color = Color(0xFFEEEEEE), fontSize = 13.sp, lineHeight = 18.sp)
                            Spacer(Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFD4AF37).copy(alpha = 0.3f), thickness = 1.dp)
                            Spacer(Modifier.height(8.dp))
                            Text(if (vm.esIngles) "Status: ${faseActiva.efectoJugabilidad}" else "Estado: ${faseActiva.efectoJugabilidad}", color = Color(0xFFD4AF37), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            
            // --- ADORNOS DEL PERGAMINO ---
            // Rodillo Superior
            Surface(color = Color(0xFFEAD48F), shape = RoundedCornerShape(50), shadowElevation = 6.dp, modifier = Modifier.fillMaxWidth().height(14.dp).align(Alignment.TopCenter).offset(y = (-7).dp).padding(horizontal = 4.dp)) {}
            Surface(color = Color(0xFFD4AF37), shape = RoundedCornerShape(4.dp), modifier = Modifier.width(10.dp).height(20.dp).align(Alignment.TopStart).offset(y = (-10).dp)) {}
            Surface(color = Color(0xFFD4AF37), shape = RoundedCornerShape(4.dp), modifier = Modifier.width(10.dp).height(20.dp).align(Alignment.TopEnd).offset(y = (-10).dp)) {}

            // Rodillo Inferior
            Surface(color = Color(0xFFEAD48F), shape = RoundedCornerShape(50), shadowElevation = 6.dp, modifier = Modifier.fillMaxWidth().height(14.dp).align(Alignment.BottomCenter).offset(y = 7.dp).padding(horizontal = 4.dp)) {}
            Surface(color = Color(0xFFD4AF37), shape = RoundedCornerShape(4.dp), modifier = Modifier.width(10.dp).height(20.dp).align(Alignment.BottomStart).offset(y = 4.dp)) {}
            Surface(color = Color(0xFFD4AF37), shape = RoundedCornerShape(4.dp), modifier = Modifier.width(10.dp).height(20.dp).align(Alignment.BottomEnd).offset(y = 4.dp)) {}

            // Listón Rojo Banderín superior
            Surface(color = Color(0xFFA02020), shape = FormaBanderin, shadowElevation = 4.dp, modifier = Modifier.width(32.dp).height(46.dp).align(Alignment.TopCenter).offset(y = (-7).dp)) {}
        }
    }
}

// =========================================================================
// PANTALLA 4: PERFIL DE USUARIO Y CONFIGURACIÓN (ACTUALIZACIÓN INSTANTÁNEA)
// =========================================================================
@Composable
fun PantallaPerfil(vm: ViewModelDuality) {
    val usuario = vm.usuarioActual ?: return
    val configuracion = LocalConfiguration.current
    val esHorizontal = configuracion.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("DualityPrefs", Context.MODE_PRIVATE)

    var nuevoNombre by remember(usuario.nombreUsuario) { mutableStateOf(usuario.nombreUsuario) }
    var nuevoTitulo by remember(usuario.tituloOpcional) { mutableStateOf(usuario.tituloOpcional) }
    var actualPass by remember { mutableStateOf("") }
    var nuevaPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var mostrarDialogoSalir by remember { mutableStateOf(false) }
    var indiceInsigniaState by remember(usuario.correo) { mutableIntStateOf(usuario.indiceInsignia) }

    // --- EL TRUCO: GATILLO PARA FORZAR EL REDIBUJADO INSTANTÁNEO ---
    var actualizarFoto by remember { mutableStateOf(0) } 

    // 1. RECUPERAR FOTO AL INICIAR LA PANTALLA
    LaunchedEffect(usuario.correo) {
        val fotoGuardada = prefs.getString("foto_${usuario.correo}", "")
        if (!fotoGuardada.isNullOrEmpty()) {
            usuario.fotoPerfilUri = fotoGuardada
            actualizarFoto++ // Avisamos que hay una foto lista
        }
    }

    // 2. GUARDAR LA FOTO FÍSICAMENTE EN LA APP
    val selectorFoto = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = java.io.File(context.filesDir, "foto_${usuario.correo}.jpg")
                val outputStream = java.io.FileOutputStream(file)
                
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                
                val rutaLocal = file.absolutePath
                usuario.fotoPerfilUri = rutaLocal
                prefs.edit().putString("foto_${usuario.correo}", rutaLocal).apply()
                
                // ¡FORZAMOS LA ACTUALIZACIÓN VISUAL AL INSTANTE!
                actualizarFoto++ 
                
                vm.mostrarNotificacion(if (vm.esIngles) "Profile photo saved!" else "¡Foto guardada en tu Grimorio!")
            } catch (_: Exception) {
                vm.mostrarNotificacion(if (vm.esIngles) "Error saving photo" else "Error al guardar la foto")
            }
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
                val badge = insignias[indiceInsigniaState % insignias.size]
                
                // 3. DIBUJAR LA FOTO DE MANERA OPTIMIZADA
                Box(
                    modifier = Modifier.size(96.dp).clip(CircleShape).background(Brush.radialGradient(listOf(badge.second, AzulProfundo))).border(3.dp, DoradoClaro, CircleShape), 
                    contentAlignment = Alignment.Center
                ) {
                    var fotoDibujada = false
                    val gatilloFuerza = actualizarFoto // Leemos el gatillo para que Compose vigile los cambios
                    
                    if (!usuario.fotoPerfilUri.isNullOrEmpty() && gatilloFuerza > 0) {
                        // Usamos remember para decodificar la imagen rápido sin trabar el hilo de la UI
                        val bitmap = remember(usuario.fotoPerfilUri, gatilloFuerza) {
                            android.graphics.BitmapFactory.decodeFile(usuario.fotoPerfilUri)?.asImageBitmap()
                        }
                        
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap, 
                                contentDescription = "Foto Perfil", 
                                modifier = Modifier.fillMaxSize(), 
                                contentScale = ContentScale.Crop
                            )
                            fotoDibujada = true
                        }
                    }
                    
                    if (!fotoDibujada) {
                        Icon(badge.third, contentDescription = null, tint = DoradoClaro, modifier = Modifier.size(48.dp))
                    }
                }
                
                Spacer(Modifier.height(10.dp))
                Text(if (vm.usuarioActual?.nombreUsuario == "Aventurero" && vm.esIngles) "Adventurer" else usuario.nombreUsuario, fontSize = 22.sp, fontWeight = FontWeight.Black, color = DoradoClaro, fontFamily = FontFamily.Serif)
                Text(vm.traducirTitulo(usuario.tituloOpcional), fontSize = 12.sp, color = DoradoPrimario, fontWeight = FontWeight.Bold)
                Text(usuario.correo, fontSize = 12.sp, color = Color.LightGray)

                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = { selectorFoto.launch("image/*") }, colors = ButtonDefaults.buttonColors(containerColor = AzulGremio, contentColor = DoradoClaro), border = BorderStroke(1.dp, DoradoPrimario)) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(6.dp))
                        Text(if (vm.esIngles) "Upload Photo" else "Subir Foto", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { 
                            val nuevoIndice = (indiceInsigniaState + 1) % insignias.size
                            indiceInsigniaState = nuevoIndice
                            usuario.indiceInsignia = nuevoIndice
                            vm.mostrarNotificacion(if (vm.esIngles) "Badge updated" else "Insignia actualizada") 
                        }, 
                        border = BorderStroke(1.dp, DoradoPrimario)
                    ) {
                        Icon(Icons.Default.Autorenew, contentDescription = null, tint = DoradoClaro, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(6.dp))
                        Text(if (vm.esIngles) "Rotate" else "Rotar", color = DoradoClaro, fontSize = 12.sp)
                    }
                }
            }
        }

        // --- TARJETA DE AJUSTES (IDIOMA) ---
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

        // --- FORMULARIOS ---
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
            onClick = { mostrarDialogoSalir = true }, // <--- AHORA ABRE LA ALERTA
            colors = ButtonDefaults.buttonColors(containerColor = RojoCinta, contentColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null); Spacer(Modifier.width(8.dp))
            Text(if (vm.esIngles) "LOG OUT" else "CERRAR SESIÓN", fontWeight = FontWeight.Black)
        }
    }

    // --- ALERTA DE SEGURIDAD PARA CERRAR SESIÓN ---
    if (mostrarDialogoSalir) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoSalir = false },
            containerColor = Color(0xFF192341), // Azul oscuro de la app
            titleContentColor = DoradoClaro,
            textContentColor = Color.LightGray,
            shape = RoundedCornerShape(16.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = DoradoPrimario)
                    Spacer(Modifier.width(8.dp))
                    Text(if (vm.esIngles) "Security Alert" else "Alerta de Seguridad", fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
            },
            text = {
                Text(
                    text = if (vm.esIngles) 
                        "Are you sure you want to exit the Grimoire? You will need your access runes to enter again." 
                    else 
                        "¿Estás seguro de que deseas salir del Grimorio? Necesitarás tus runas de acceso para volver a entrar.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { 
                        mostrarDialogoSalir = false
                        vm.activarAnimacionCarga("Cerrando los archivos...") {
                            vm.cerrarSesionFirebase() 
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RojoCinta)
                ) {
                    Text(if (vm.esIngles) "YES, LOG OUT" else "SÍ, SALIR", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoSalir = false }) {
                    Text(if (vm.esIngles) "CANCEL" else "CANCELAR", color = DoradoClaro)
                }
            }
        )
    }
}

@Composable
fun TarjetaCambioNombre(vm: ViewModelDuality, usuario: CuentaUsuario, nuevoNombre: String, onNombre: (String) -> Unit, nuevoTitulo: String, onTitulo: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SuperficieOscura),
        border = BorderStroke(1.dp, DoradoPrimario.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(if (vm.esIngles) "Edit Name and Title" else "Editar Nombre y Título", color = DoradoClaro, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(14.dp))
            OutlinedTextField(
                value = nuevoNombre, onValueChange = onNombre,
                label = { Text(if (vm.esIngles) "New Name *" else "Nuevo Nombre *", color = Color.Gray, fontSize = 12.sp) },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DoradoPrimario, unfocusedBorderColor = Color.DarkGray, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = nuevoTitulo, onValueChange = onTitulo,
                label = { Text(if (vm.esIngles) "Title (Optional)" else "Título (Opcional)", color = Color.Gray, fontSize = 12.sp) },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DoradoPrimario, unfocusedBorderColor = Color.DarkGray, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = { 
                    usuario.nombreUsuario = nuevoNombre.ifBlank { usuario.nombreUsuario }
                    usuario.tituloOpcional = nuevoTitulo
                    vm.mostrarNotificacion(if (vm.esIngles) "Profile updated" else "Perfil actualizado")
                },
                colors = ButtonDefaults.buttonColors(containerColor = DoradoPrimario, contentColor = AzulProfundo),
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (vm.esIngles) "SAVE NAME" else "GUARDAR DATOS", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun TarjetaCambioPass(vm: ViewModelDuality, usuario: CuentaUsuario, actual: String, onActual: (String) -> Unit, nueva: String, onNueva: (String) -> Unit, conf: String, onConf: (String) -> Unit) {
    var verActual by remember { mutableStateOf(false) }
    var verNueva by remember { mutableStateOf(false) }
    var verConf by remember { mutableStateOf(false) }

    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = FondoTarjeta), border = BorderStroke(1.5.dp, DoradoPrimario.copy(alpha = 0.7f)), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(if (vm.esIngles) "Change Password" else "Cambiar Contraseña", color = DoradoClaro, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            
            OutlinedTextField(
                value = actual, 
                onValueChange = onActual, 
                label = { Text(if (vm.esIngles) "Current Password *" else "Actual *") }, 
                visualTransformation = if (verActual) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(), 
                trailingIcon = {
                    IconButton(onClick = { verActual = !verActual }) {
                        Icon(if (verActual) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = DoradoClaro)
                    }
                },
                singleLine = true, 
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = nueva, 
                onValueChange = onNueva, 
                label = { Text(if (vm.esIngles) "New (Min. 6 chars) *" else "Nueva (Mín. 6) *") }, 
                visualTransformation = if (verNueva) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(), 
                trailingIcon = {
                    IconButton(onClick = { verNueva = !verNueva }) {
                        Icon(if (verNueva) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = DoradoClaro)
                    }
                },
                singleLine = true, 
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = conf, 
                onValueChange = onConf, 
                label = { Text(if (vm.esIngles) "Confirm Password *" else "Confirmar *") }, 
                visualTransformation = if (verConf) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(), 
                trailingIcon = {
                    IconButton(onClick = { verConf = !verConf }) {
                        Icon(if (verConf) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = DoradoClaro)
                    }
                },
                singleLine = true, 
                modifier = Modifier.fillMaxWidth()
            )
            
            Button(
                onClick = {
                    when {
                        actual.isBlank() || nueva.isBlank() || conf.isBlank() -> vm.mostrarNotificacion(if (vm.esIngles) "Fill all fields." else "Llena todos los campos.")
                        nueva.length < 6 -> vm.mostrarNotificacion(if (vm.esIngles) "Minimum 6 characters." else "Mínimo 6 caracteres.")
                        nueva != conf -> vm.mostrarNotificacion(if (vm.esIngles) "Passwords don't match." else "No coinciden.")
                        else -> {
                            vm.actualizarContrasenaFirebase(
                                passActual = actual,
                                nuevaPass = nueva,
                                alExito = {
                                    onActual(""); onNueva(""); onConf("")
                                    vm.mostrarNotificacion(if (vm.esIngles) "Password successfully updated." else "Contraseña actualizada exitosamente.")
                                },
                                alError = { err -> 
                                    vm.mostrarNotificacion(err) 
                                }
                            )
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AzulGremio, contentColor = DoradoClaro), border = BorderStroke(1.dp, DoradoPrimario), modifier = Modifier.fillMaxWidth()
            ) { Text(if (vm.esIngles) "UPDATE PASSWORD" else "ACTUALIZAR", fontWeight = FontWeight.Bold) }
        }
    }
}

// =========================================================================
// PANTALLA 5: EL TABLÓN DEL GREMIO (PERGAMINOS Y RESPUESTAS ACTIVAS)
// =========================================================================
@Composable
fun PantallaComunidad(vm: ViewModelDuality) {
    var mostrarDialogoCrear by remember { mutableStateOf(false) }
    var postSeleccionadoParaRespuestas by remember { mutableStateOf<ViewModelDuality.PostGremio?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogoCrear = true },
                containerColor = Color(0xFFD4AF37), 
                contentColor = Color(0xFF192341),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Escribir Post")
            }
        }
    ) { paddingInterno ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingInterno).padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(16.dp))
            
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = if (vm.esIngles) "THE GUILD BOARD" else "EL TABLÓN DEL GREMIO", color = Color(0xFFEAD48F), fontSize = 22.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Serif, letterSpacing = 1.sp)
                Text(text = if (vm.esIngles) "Share images, recruit allies and report corrosion." else "Comparte imágenes, recluta aliados y reporta corrosión.", color = Color.LightGray, fontSize = 12.sp, textAlign = TextAlign.Center)
            }
            Spacer(Modifier.height(20.dp))

            androidx.compose.foundation.lazy.LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp) 
            ) {
                items(vm.postsComunidadLive.size) { index ->
                    TarjetaPost(
                        post = vm.postsComunidadLive[index], 
                        vm = vm,
                        onVerRespuestas = { postSeleccionadoParaRespuestas = it }
                    )
                }
            }
        }

        // --- 1. DIÁLOGO ESTILO PERGAMINO PARA CREAR POST ---
        if (mostrarDialogoCrear) {
            var textoPost by remember { mutableStateOf("") }
            
            // NUEVO: Variable para guardar la foto y el lanzador de la galería
            var imagenAdjunta by remember { mutableStateOf<android.net.Uri?>(null) }
            val selectorGaleria = androidx.activity.compose.rememberLauncherForActivityResult(
                androidx.activity.result.contract.ActivityResultContracts.GetContent()
            ) { uri ->
                imagenAdjunta = uri
            }
            
            androidx.compose.ui.window.Dialog(onDismissRequest = { mostrarDialogoCrear = false }) {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Card(
                        shape = RoundedCornerShape(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEBE1B5)), 
                        border = BorderStroke(1.dp, Color(0xFFC7BC8F)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Spacer(Modifier.height(16.dp))
                            Text(if (vm.esIngles) "Write your message" else "Redactar Mensaje", color = Color(0xFF222222), fontSize = 20.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Serif)
                            Spacer(Modifier.height(12.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                                Surface(shape = CircleShape, color = Color(0xFF2B4171), modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.padding(6.dp))
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(vm.usuarioActual?.nombreUsuario ?: "Usuario", color = Color(0xFF222222), fontWeight = FontWeight.Bold)
                            }
                            
                            OutlinedTextField(
                                value = textoPost,
                                onValueChange = { textoPost = it },
                                placeholder = { Text(if (vm.esIngles) "Share your discoveries..." else "Comparte tus descubrimientos...", color = Color(0xFF666666)) },
                                modifier = Modifier.fillMaxWidth().height(140.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF8B0000), 
                                    unfocusedBorderColor = Color(0xFFC7BC8F),
                                    focusedTextColor = Color(0xFF222222),
                                    unfocusedTextColor = Color(0xFF222222),
                                    cursorColor = Color(0xFF8B0000)
                                ),
                                maxLines = 6
                            )
                            Spacer(Modifier.height(16.dp))
                            
                            // Botones de Acción
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                OutlinedButton(
                                    // AHORA SÍ ABRE LA GALERÍA DEL CELULAR
                                    onClick = { selectorGaleria.launch("image/*") }, 
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (imagenAdjunta != null) Color(0xFF4CAF50).copy(alpha = 0.1f) else Color.Transparent,
                                        contentColor = Color(0xFF2B4171)
                                    ), 
                                    border = BorderStroke(1.dp, if (imagenAdjunta != null) Color(0xFF4CAF50) else Color(0xFF2B4171)),
                                    modifier = Modifier.weight(1f)
                                ) { 
                                    // Cambia a una palomita verde cuando seleccionas foto
                                    Icon(
                                        imageVector = if (imagenAdjunta != null) Icons.Default.CheckCircle else Icons.Default.Image, 
                                        contentDescription = null, 
                                        tint = if (imagenAdjunta != null) Color(0xFF4CAF50) else Color(0xFF2B4171),
                                        modifier = Modifier.size(18.dp)
                                    ) 
                                }
                                
                                Spacer(Modifier.width(8.dp))
                                
                                Button(
                                    onClick = { 
                                        // AHORA ENVIAMOS EL TEXTO Y EL URI DE LA FOTO A FIREBASE
                                        vm.publicarEnTablon(textoPost, imagenAdjunta) {
                                            textoPost = ""
                                            imagenAdjunta = null
                                            mostrarDialogoCrear = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA02020), contentColor = Color.White),
                                    modifier = Modifier.weight(2f)
                                ) { Text(if (vm.esIngles) "PUBLISH" else "PUBLICAR", fontWeight = FontWeight.Bold) }
                            }
                        }
                    }
                    
                    val FormaBanderin = GenericShape { size, _ ->
                        moveTo(0f, 0f); lineTo(size.width, 0f); lineTo(size.width, size.height); lineTo(size.width / 2f, size.height - 15f); lineTo(0f, size.height); close()
                    }
                    Surface(color = Color(0xFFA02020), shape = FormaBanderin, shadowElevation = 4.dp, modifier = Modifier.width(32.dp).height(44.dp).align(Alignment.TopCenter).offset(y = (-10).dp)) {}
                }
            }
        }

        // --- 2. DIÁLOGO PARA LEER Y ESCRIBIR RESPUESTAS (REDISEÑO PREMIUM) ---
        if (postSeleccionadoParaRespuestas != null) {
            val post = postSeleccionadoParaRespuestas!!
            var textoRespuesta by remember { mutableStateOf("") }

            androidx.compose.ui.window.Dialog(
                onDismissRequest = { postSeleccionadoParaRespuestas = null },
                properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF192341)), // Azul profundo web
                    border = BorderStroke(2.dp, DoradoPrimario),
                    modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        
                        // ENCABEZADO
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(if (vm.esIngles) "Replies (${post.comentarios.size})" else "Respuestas (${post.comentarios.size})", color = DoradoClaro, fontSize = 20.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Serif)
                            IconButton(onClick = { postSeleccionadoParaRespuestas = null }) { Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.LightGray) }
                        }
                        HorizontalDivider(color = DoradoPrimario.copy(alpha = 0.4f))
                        
                        // ZONA DE MENSAJES
                        Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp)) {
                            if (post.comentarios.isEmpty()) {
                                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(48.dp))
                                    Spacer(Modifier.height(16.dp))
                                    Text(if (vm.esIngles) "The silence is absolute." else "El silencio es absoluto.", color = Color.Gray, fontStyle = FontStyle.Italic)
                                    Text(if (vm.esIngles) "Be the first to reply!" else "¡Sé el primero en responder!", color = DoradoPrimario, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                androidx.compose.foundation.lazy.LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(post.comentarios.size) { i ->
                                        val comentario = post.comentarios[i]
                                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF2B4171).copy(alpha = 0.3f)), border = BorderStroke(1.dp, Color(0xFF2B4171)), shape = RoundedCornerShape(8.dp)) {
                                            Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Surface(shape = CircleShape, color = Color(0xFF2B4171), modifier = Modifier.size(20.dp)) {
                                                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.padding(4.dp))
                                                        }
                                                        Spacer(Modifier.width(8.dp))
                                                        Text(comentario.autor, color = DoradoClaro, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    }
                                                    Text(comentario.tiempo, color = Color.Gray, fontSize = 10.sp)
                                                }
                                                Spacer(Modifier.height(8.dp))
                                                Text(comentario.texto, color = Color.White, fontSize = 13.sp, lineHeight = 18.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // ZONA DE ESCRITURA (Pegada abajo)
                        Surface(color = Color(0xFF0C0814), modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = textoRespuesta,
                                    onValueChange = { textoRespuesta = it },
                                    placeholder = { Text(if (vm.esIngles) "Write a reply..." else "Escribe una respuesta...", color = Color.Gray) },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = DoradoPrimario,
                                        unfocusedBorderColor = Color(0xFF334A80),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.LightGray
                                    ),
                                    shape = RoundedCornerShape(24.dp),
                                    maxLines = 3
                                )
                                Spacer(Modifier.width(8.dp))
                                
                                IconButton(
                                    onClick = {
                                        vm.publicarRespuesta(post.id, textoRespuesta) {
                                            textoRespuesta = ""
                                        }
                                    },
                                    modifier = Modifier.size(48.dp).background(DoradoPrimario, CircleShape),
                                    enabled = textoRespuesta.isNotBlank()
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = "Enviar", tint = Color(0xFF192341))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaPost(post: ViewModelDuality.PostGremio, vm: ViewModelDuality, onVerRespuestas: (ViewModelDuality.PostGremio) -> Unit) {
    var reaccionado by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEBE1B5)), 
        border = BorderStroke(if (post.esOficial) 2.dp else 1.dp, if (post.esOficial) Color(0xFFA02020) else Color(0xFFC7BC8F)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = if (post.esOficial) Color(0xFFA02020) else Color(0xFF2B4171), border = BorderStroke(1.dp, DoradoPrimario), modifier = Modifier.size(40.dp)) {
                        Icon(if (post.esOficial) Icons.Default.LocalFireDepartment else Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(if (post.nombreAutor == "Aventurero" && vm.esIngles) "Adventurer" else post.nombreAutor, color = Color(0xFF222222), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(vm.traducirTitulo(post.tituloAutor), color = if (post.esOficial) Color(0xFFA02020) else Color(0xFF334A80), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
                // AHORA MOSTRAMOS LA FECHA Y HORA REAL
                Text(post.fechaFormateada, color = Color.DarkGray, fontSize = 10.sp)
            }
            
            Spacer(Modifier.height(12.dp))
            Text(post.mensaje, color = Color(0xFF333333), fontSize = 13.sp, lineHeight = 18.sp, fontFamily = FontFamily.Serif)
            
            // --- MOSTRAR LA IMAGEN ---
            if (post.imagenAdjuntaRes != null) {
                // Imagen local (Pruebas)
                Spacer(Modifier.height(12.dp))
                Image(
                    painter = painterResource(id = post.imagenAdjuntaRes),
                    contentDescription = "Imagen adjunta",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp)).border(1.dp, Color(0xFFC7BC8F), RoundedCornerShape(8.dp))
                )
            } else if (!post.imagenUrl.isNullOrEmpty()) {
                // IMAGEN DE FIREBASE STORAGE (Coil)
                Spacer(Modifier.height(12.dp))
                AsyncImage(
                    model = post.imagenUrl,
                    contentDescription = "Imagen del usuario",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp)).border(1.dp, Color(0xFFC7BC8F), RoundedCornerShape(8.dp))
                )
            }
            
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFC7BC8F).copy(alpha = 0.6f), thickness = 1.dp)
            Spacer(Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                
                Surface(
                    onClick = { reaccionado = !reaccionado; vm.mostrarNotificacion(if (reaccionado) "¡Honor otorgado!" else "Honor retirado") },
                    shape = RoundedCornerShape(50), color = if (reaccionado) Color(0xFFD4AF37).copy(alpha = 0.2f) else Color.Transparent
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp, horizontal = 10.dp)) {
                        Icon(imageVector = if (reaccionado) Icons.Default.WorkspacePremium else Icons.Default.StarBorder, contentDescription = "Dar Honor", tint = if (reaccionado) Color(0xFFA02020) else Color(0xFF555555), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("${if (reaccionado) post.honor + 1 else post.honor} Honor", color = if (reaccionado) Color(0xFFA02020) else Color(0xFF444444), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
                
                // BOTÓN DE RESPUESTAS AHORA ACTIVA EL DIÁLOGO
                Surface(
                    onClick = { onVerRespuestas(post) },
                    shape = RoundedCornerShape(50), color = Color.Transparent
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)) {
                        Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Respuestas", tint = Color(0xFF555555), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("${post.comentarios.size} ${if (vm.esIngles) "Replies" else "Respuestas"}", color = Color(0xFF555555), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}