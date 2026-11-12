package com.example.dualityapp.estado

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dualityapp.R
import com.example.dualityapp.datos.*
import com.example.dualityapp.tema.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// =========================================================================
// GESTOR DE ESTADO (VIEWMODEL) - CONECTADO A FIREBASE
// =========================================================================

class ViewModelDuality : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // 1. ESTADOS DE USUARIO
    val usuariosRegistrados = mutableStateListOf(
        CuentaUsuario(
            nombreUsuario = "Vigía de Kairos",
            correo = "demo@duality.com",
            contrasena = "123456",
            tituloOpcional = "Cronista Astral",
            indiceInsignia = 0
        )
    )

    var usuarioActual by mutableStateOf<CuentaUsuario?>(null)
    var pantallaAcceso by mutableStateOf("LOGIN")

    // 2. ESTADOS DE ANIMACIÓN Y NAVEGACIÓN
    var estaCargando by mutableStateOf(false)
    var mensajeCarga by mutableStateOf("Sincronizando con el Reloj Astral...")
    var mensajeNotificacion by mutableStateOf<String?>(null)
    var pestanaActualInferior by mutableIntStateOf(0)

    // 4. CONFIGURACIÓN DE IDIOMA
    var esIngles by mutableStateOf(false) // false = Español, true = Inglés

    // Traductor automático para títulos traídos desde Firebase
    fun traducirTitulo(tituloEsp: String): String {
        if (!esIngles) return tituloEsp
        
        return when (tituloEsp.lowercase().trim()) {
            "aventurero" -> "Adventurer"
            "explorador novato" -> "Novice Explorer"
            "explorador del gremio" -> "Guild Explorer"
            "iniciado" -> "Initiate"
            "primordial de dualidad" -> "Primordial of Duality"
            "mago de agua" -> "Water Mage"
            "administrador" -> "Administrator"
            else -> tituloEsp // Si no lo reconoce, lo deja igual
        }
    }

    // 3. ESTADOS DE LA WIKI, MAPA Y BUCLE
    var categoriaWikiSeleccionada by mutableStateOf("personajes")
    var busquedaWiki by mutableStateOf("")
    var itemInspeccionado by mutableStateOf<RegistroWiki?>(null)

    val idsFavoritos = mutableStateListOf<String>()
    var mostrarSoloFavoritos by mutableStateOf(false)

    var indiceReinoSeleccionado by mutableStateOf(0)
    var indiceFaseBucle by mutableStateOf(0)
    var indiceBiomaActual by mutableStateOf(0)

    init {
        // =======================================================
        // CONFIGURACIÓN DE CACHÉ LOCAL (MODO OFFLINE)
        // =======================================================
        val settings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
            // Configuración de persistencia y caché local
            .setLocalCacheSettings(com.google.firebase.firestore.PersistentCacheSettings.newBuilder().build())
            .build()
        db.firestoreSettings = settings

        // Escuchar los mensajes de la comunidad en tiempo real
        inicializarTablonGremio()

        // Verificar si ya hay una sesión activa en Firebase
        val currentUser = auth.currentUser
        if (currentUser != null) {
            usuarioActual = CuentaUsuario(
                nombreUsuario = currentUser.displayName ?: "Aventurero",
                correo = currentUser.email ?: "",
                contrasena = "******"
            )
        }
    }

    fun mostrarNotificacion(mensaje: String) {
        mensajeNotificacion = mensaje
        viewModelScope.launch {
            delay(3000)
            if (mensajeNotificacion == mensaje) mensajeNotificacion = null
        }
    }

    fun activarAnimacionCarga(mensaje: String, alTerminar: () -> Unit) {
        viewModelScope.launch {
            mensajeCarga = mensaje
            estaCargando = true
            delay(1100)
            alTerminar()
            estaCargando = false
        }
    }

    fun alternarFavorito(id: String) {
        if (idsFavoritos.contains(id)) {
            idsFavoritos.remove(id)
            mostrarNotificacion("Eliminado de tus pergaminos favoritos")
        } else {
            idsFavoritos.add(id)
            mostrarNotificacion("Guardado en tus pergaminos favoritos")
        }
    }

    // =========================================================================
    // ACCIONES DE AUTENTICACIÓN CON FIREBASE
    // =========================================================================

    fun iniciarSesionFirebase(correo: String, pass: String, alExito: () -> Unit, alError: (String) -> Unit) {
        activarAnimacionCarga("Autenticando en el Reloj Astral...") {
            auth.signInWithEmailAndPassword(correo, pass)
                .addOnSuccessListener { result ->
                    val firebaseUser = result.user
                    usuarioActual = CuentaUsuario(
                        nombreUsuario = firebaseUser?.displayName ?: "Aventurero",
                        correo = firebaseUser?.email ?: correo,
                        contrasena = pass
                    )
                    alExito()
                }
                .addOnFailureListener { e ->
                    // TRADUCTOR DE ERRORES DE FIREBASE
                    val mensaje = e.message?.lowercase() ?: ""
                    val errorTraducido = when {
                        mensaje.contains("invalid-credential") || mensaje.contains("invalid_login_credentials") -> "El correo o la contraseña rúnica son incorrectos."
                        mensaje.contains("user-not-found") -> "No existe ningún aventurero registrado con este correo."
                        mensaje.contains("wrong-password") -> "La contraseña rúnica es incorrecta."
                        mensaje.contains("network-request-failed") -> "La conexión con el Reloj Astral falló. Revisa tu internet."
                        mensaje.contains("too-many-requests") -> "Demasiados intentos fallidos. El gremio ha bloqueado el acceso temporalmente."
                        else -> "Credenciales inválidas o error de conexión."
                    }
                    alError(errorTraducido)
                }
        }
    }

    fun registrarseFirebase(nombre: String, correo: String, pass: String, titulo: String, alExito: () -> Unit, alError: (String) -> Unit) {
        activarAnimacionCarga("Registrando en el Gremio...") {
            auth.createUserWithEmailAndPassword(correo, pass)
                .addOnSuccessListener { result ->
                    val firebaseUser = result.user
                    val nuevoUsuario = CuentaUsuario(
                        nombreUsuario = nombre,
                        correo = correo,
                        contrasena = pass,
                        tituloOpcional = titulo
                    )
                    
                    db.collection("usuarios").document(firebaseUser?.uid ?: "unknown")
                        .set(mapOf("nombre" to nombre, "titulo" to titulo, "correo" to correo))
                    
                    usuarioActual = nuevoUsuario
                    alExito()
                }
                .addOnFailureListener { e ->
                    // TRADUCTOR DE ERRORES DE FIREBASE AL REGISTRAR
                    val mensaje = e.message?.lowercase() ?: ""
                    val errorTraducido = when {
                        mensaje.contains("email-already-in-use") -> "El correo ya está registrado en los archivos de Aethelgard."
                        mensaje.contains("invalid-email") -> "El formato del correo es inválido."
                        mensaje.contains("network-request-failed") -> "La conexión falló. Revisa tu internet."
                        else -> "Error al inscribir tu alma en el Gremio."
                    }
                    alError(errorTraducido)
                }
        }
    }

    fun cerrarSesionFirebase() {
        auth.signOut()
        usuarioActual = null
        pestanaActualInferior = 0
        mostrarNotificacion("Sesión cerrada correctamente")
    }

    fun actualizarContrasenaFirebase(passActual: String, nuevaPass: String, alExito: () -> Unit, alError: (String) -> Unit) {
        val user = auth.currentUser
        
        if (user != null && user.email != null) {
            activarAnimacionCarga("Forjando nueva runa de acceso...") {
                // 1. Creamos la credencial con el correo y la contraseña ACTUAL
                val credencial = com.google.firebase.auth.EmailAuthProvider.getCredential(user.email!!, passActual)
                
                // 2. Reautenticamos al usuario por seguridad
                user.reauthenticate(credencial)
                    .addOnSuccessListener {
                        // 3. Si Firebase confirma que es él, AHORA SÍ actualizamos la contraseña
                        user.updatePassword(nuevaPass)
                            .addOnSuccessListener {
                                usuarioActual?.contrasena = nuevaPass
                                alExito()
                            }
                            .addOnFailureListener { e ->
                                alError(e.localizedMessage ?: "Error al guardar la nueva contraseña.")
                            }
                    }
                    .addOnFailureListener {
                        alError(if (esIngles) "Current password is wrong or session expired." else "La contraseña actual es incorrecta o expiró.")
                    }
            }
        } else {
            alError(if (esIngles) "No active session." else "No hay sesión activa.")
        }
    }

    fun recuperarContrasenaFirebase(correo: String, alExito: () -> Unit, alError: (String) -> Unit) {
        activarAnimacionCarga("Consultando los archivos antiguos...") {
            auth.sendPasswordResetEmail(correo)
                .addOnSuccessListener { alExito() }
                .addOnFailureListener { _ -> alError("Error al enviar el correo. Verifica que la dirección sea correcta.") }
        }
    }

    // =========================================================================
    // BASE DE DATOS LOCAL BILINGÜE (LORE OFICIAL DE DUALITY)
    // =========================================================================
    
    // Variables dinámicas que cambian según el interruptor de idioma
    val catalogoWiki get() = if (esIngles) wikiEn else wikiEs
    val mapaReinos get() = if (esIngles) reinosEn else reinosEs
    val fasesBucle get() = if (esIngles) fasesEn else fasesEs
    val biomas get() = if (esIngles) biomasEn else biomasEs

    // --- 1. WIKI (ESPAÑOL E INGLÉS) ---
    private val wikiEs = listOf(
        // Personajes
        RegistroWiki("p1", "personajes", "Protagonista (Ópalo / Zyren)", "Ser Primordial de Segunda Generación", "Resultado de un romance efímero entre el Dios de la Creación y la Diosa de la Destrucción. Posee la dualidad de crear y destruir.", "Origen: Creación y Destrucción", AzulGremio, Icons.Default.AutoAwesome, R.drawable.opalo),
        RegistroWiki("p2", "personajes", "Elizaveta", "Humana • Maga de Distancia", "Especialista en ataques a distancia y magia de purificación. Guía al protagonista en la ciudad oculta.", "Afinidad: Magia", DoradoPrimario, Icons.Default.Flare, R.drawable.elizaveta),
        RegistroWiki("p3", "personajes", "Rasku", "Híbrido de Perro • Pícaro", "Veloz combatiente de corto alcance. Enseña supervivencia y reglas del Gremio.", "Afinidad: Sigilo", EsmeraldaGremio, Icons.Default.Bolt, R.drawable.rasku),
        RegistroWiki("p4", "personajes", "Gral. Calabaza", "Defensor Acorazado", "Guerrero veterano que resguarda la ciudad oculta.", "Afinidad: Defensa", RojoCinta, Icons.Default.Shield, R.drawable.general_calabaza),
        
        // Clases
        RegistroWiki("c1", "clases", "Primordial de Dualidad (Ópalo / Zyren)", "Habilidad de Creación y Destrucción", "Clase única. Su poder interno es inestable: pueden sanar usando energía de Creación o desintegrar enemigos con Destrucción, pero corren el riesgo de corromperse a sí mismos.", "Poder Oculto de Segunda Generación", AzulGremio, Icons.Default.AutoAwesome),
        RegistroWiki("c2", "clases", "Maga a Distancia (Elizaveta)", "Control de área y proyectiles mágicos", "Ataca desde la retaguardia para evitar el contacto físico con las criaturas corrosivas de la destrucción.", "Rol: Soporte Ofensivo", AzulGremio, Icons.Default.AutoFixHigh),
        RegistroWiki("c3", "clases", "Pícaro Híbrido (Rasku)", "Velocidad y ataques furtivos", "Golpea a corto alcance y se retira rápidamente antes de que el enemigo pueda responder. Excelente para la exploración ágil.", "Rol: Daño Rápido", AzulGremio, Icons.Default.DirectionsRun),
        RegistroWiki("c4", "clases", "Exorcista de la Creación", "Purificación y Bendiciones", "Clérigos de la Iglesia de la Creación capaces de frenar la corrosión en las personas y sanar el ambiente contaminado por el Vacío.", "Rol: Sanador", AzulGremio, Icons.Default.Science),
        RegistroWiki("c5", "clases", "Defensor Pesado (General Calabaza)", "Tanque y provocación", "Atrae el daño hacia su escudo pesado para proteger a los civiles y mantener la línea frontal en el gremio.", "Rol: Tanque Absoluto", AzulGremio, Icons.Default.Shield),

        // Ítems y Reliquias
        RegistroWiki("i1", "items", "Arma Encantada del Gremio", "Equipamiento Militar Estándar", "Forjadas con magia especial, son las únicas armas capaces de hacer daño físico a las masas oscuras humanoides.", "Uso: Combate contra la Destrucción", AzulGremio, Icons.Default.Hardware),
        RegistroWiki("i2", "items", "Bendición de la Creación", "Agua Sagrada Exorcista", "Sanación canalizada por la Iglesia capaz de frenar el deterioro en la piel de los heridos y limpiar terrenos contaminados.", "Uso: Purificación de Corrosión", AzulGremio, Icons.Default.WaterDrop),

        // Criaturas del Vacío
        RegistroWiki("e1", "enemigos", "Masas Oscuras (Infectados)", "Criaturas de Destrucción Humanoides", "Seres de energía oscura terriblemente tóxica. Buscan desintegrar a los mortales con solo tocarlos, esparciendo corrosión en el ambiente.", "Debilidad: Armas encantadas del Gremio", RojoCinta, Icons.Default.Dangerous),
        RegistroWiki("e2", "enemigos", "Bestias de Corrosión", "Fauna corrompida por el vacío", "Animales del bosque asimilados por la energía destructiva. Su mordida deja marcas corrosivas que enferman a las personas.", "Cura: Bendición de Exorcistas", RojoCinta, Icons.Default.Pets),
        RegistroWiki("e3", "enemigos", "Diosa de la Destrucción", "Deidad Primigenia", "Cree firmemente que no existe la eternidad; todo debe morir y quedar en la nada. Borra los recuerdos de su hijo (el protagonista) en cada ciclo.", "Afinidad Absoluta: El Fin de la Existencia", RojoCinta, Icons.Default.VisibilityOff),

        // Gremio, Rangos y Misiones
        RegistroWiki("g1", "gremio", "Ciudad Oculta de los Exploradores", "Refugio Multirracial", "Una ciudad pacífica oculta en un bosque donde no importa la raza ni el origen. Civiles y guerreros conviven unidos por el gusto a las aventuras.", "Liderazgo: Reglas estrictas de convivencia", AzulGremio, Icons.Default.LocationCity),
        RegistroWiki("g2", "gremio", "Guerra Inminente", "Tensiones del Mundo Exterior", "Fuera del bosque, el mundo sufre de discriminación, conflictos políticos y el miedo a la corrosión que se expande.", "Consecuencia: Refugiados constantes", AzulGremio, Icons.Default.Warning),
        RegistroWiki("g3", "gremio", "La Iglesia de la Creación", "Culto de los Exorcistas", "Creyentes del Dios de la Creación que luchan para purificar las áreas corrompidas. Sin embargo, no son suficientes para frenar el avance de la Destrucción.", "Magia: Sanación y Purificación", AzulGremio, Icons.Default.Church),

        // Razas y Facciones
        RegistroWiki("r1", "razas", "Dios de la Creación", "Deidad Primigenia de la Vida", "Desea preservar la vida por la eternidad y expandirse infinitamente. Al ver su mundo aniquilado, lo reconstruye creando el Bucle infinito.", "Ideal: Vida Eterna", DoradoPrimario, Icons.Default.WbSunny),
        RegistroWiki("r2", "razas", "Dios del Tiempo", "El Observador Recluido", "Escondido del resto del mundo, es el único además de las deidades que conoce la verdad sobre los bucles infinitos. Es él quien revela el secreto al protagonista.", "Conocimiento Absoluto", DoradoPrimario, Icons.Default.HourglassEmpty),
        RegistroWiki("r3", "razas", "Humanos y Civiles", "Población Común", "Altamente susceptibles a la corrosión destructiva. Muchos sufren en medio de las tensiones bélicas y dependen de los aventureros para sobrevivir.", "Debilidad: Mortales", DoradoPrimario, Icons.Default.EmojiPeople),
        RegistroWiki("r4", "razas", "Híbridos", "Razas Bestiales (Ej. Perro/Humano)", "Discriminados en las ciudades grandes, suelen encontrar refugio y libertad total en las instalaciones del Gremio de Exploradores.", "Rasgos: Físico adaptativo", DoradoPrimario, Icons.Default.Pets)
    )

    private val wikiEn = listOf(
        // Personajes
        RegistroWiki("p1", "personajes", "Protagonist (Opal / Zyren)", "Second Generation Primordial", "Result of a fleeting romance between the God of Creation and Goddess of Destruction. Holds the duality to create and destroy.", "Origin: Creation & Destruction", AzulGremio, Icons.Default.AutoAwesome, R.drawable.opalo),
        RegistroWiki("p2", "personajes", "Elizaveta", "Human • Ranged Mage", "Specialist in ranged attacks and purification magic. Guides the protagonist in the hidden city.", "Affinity: Magic", DoradoPrimario, Icons.Default.Flare, R.drawable.elizaveta),
        RegistroWiki("p3", "personajes", "Rasku", "Dog Hybrid • Rogue", "Fast close-combat fighter. Teaches survival and Guild rules.", "Affinity: Stealth", EsmeraldaGremio, Icons.Default.Bolt, R.drawable.rasku),
        RegistroWiki("p4", "personajes", "Gen. Pumpkin", "Armored Defender", "Veteran warrior guarding the hidden city.", "Affinity: Defense", RojoCinta, Icons.Default.Shield, R.drawable.general_calabaza),
        
        // Clases
        RegistroWiki("c1", "clases", "Duality Primordial (Opal / Zyren)", "Creation and Destruction Skill", "Unique class. Their internal power is unstable: they can heal using Creation energy or disintegrate enemies with Destruction, but risk corrupting themselves.", "Hidden Second Gen Power", AzulGremio, Icons.Default.AutoAwesome),
        RegistroWiki("c2", "clases", "Ranged Mage (Elizaveta)", "Area control and magic projectiles", "Attacks from the rearguard to avoid physical contact with the corrosive creatures of destruction.", "Role: Offensive Support", AzulGremio, Icons.Default.AutoFixHigh),
        RegistroWiki("c3", "clases", "Hybrid Rogue (Rasku)", "Speed and stealth attacks", "Strikes at close range and retreats quickly before the enemy can respond. Excellent for agile exploration.", "Role: Fast Damage", AzulGremio, Icons.Default.DirectionsRun),
        RegistroWiki("c4", "clases", "Creation Exorcist", "Purification and Blessings", "Clerics of the Church of Creation capable of halting corrosion in people and healing the environment contaminated by the Void.", "Role: Healer", AzulGremio, Icons.Default.Science),
        RegistroWiki("c5", "clases", "Heavy Defender (Gen. Pumpkin)", "Tank and taunt", "Draws damage to his heavy shield to protect civilians and hold the front line in the guild.", "Role: Absolute Tank", AzulGremio, Icons.Default.Shield),

        // Ítems
        RegistroWiki("i1", "items", "Guild Enchanted Weapon", "Standard Military Equipment", "Forged with special magic, they are the only weapons capable of dealing physical damage to the humanoid dark masses.", "Use: Combat against Destruction", AzulGremio, Icons.Default.Hardware),
        RegistroWiki("i2", "items", "Blessing of Creation", "Exorcist Holy Water", "Healing channeled by the Church capable of halting skin deterioration in the wounded and cleansing contaminated lands.", "Use: Corrosion Purification", AzulGremio, Icons.Default.WaterDrop),

        // Enemigos
        RegistroWiki("e1", "enemigos", "Dark Masses (Infected)", "Humanoid Creatures of Destruction", "Beings of terribly toxic dark energy. They seek to disintegrate mortals just by touching them, spreading corrosion.", "Weakness: Guild Enchanted Weapons", RojoCinta, Icons.Default.Dangerous),
        RegistroWiki("e2", "enemigos", "Corrosion Beasts", "Fauna corrupted by the void", "Forest animals assimilated by destructive energy. Their bite leaves corrosive marks that sicken people.", "Cure: Exorcist Blessing", RojoCinta, Icons.Default.Pets),
        RegistroWiki("e3", "enemigos", "Goddess of Destruction", "Primeval Deity", "Firmly believes that eternity does not exist; everything must die and fade into nothingness. Erases her son's memories in each cycle.", "Absolute Affinity: The End of Existence", RojoCinta, Icons.Default.VisibilityOff),

        // Gremio
        RegistroWiki("g1", "gremio", "Hidden City of Explorers", "Multiracial Refuge", "A peaceful city hidden in a forest where race or origin does not matter. Civilians and warriors live together united by a taste for adventure.", "Leadership: Strict rules of coexistence", AzulGremio, Icons.Default.LocationCity),
        RegistroWiki("g2", "gremio", "Impending War", "Outer World Tensions", "Outside the forest, the world suffers from discrimination, political conflicts, and the fear of expanding corrosion.", "Consequence: Constant refugees", AzulGremio, Icons.Default.Warning),
        RegistroWiki("g3", "gremio", "Church of Creation", "Cult of Exorcists", "Believers in the God of Creation who fight to purify corrupted areas. However, they are not enough to stop the advance of Destruction.", "Magic: Healing and Purification", AzulGremio, Icons.Default.Church),

        // Razas
        RegistroWiki("r1", "razas", "God of Creation", "Primeval Deity of Life", "Desires to preserve life for eternity and expand infinitely. Seeing his world annihilated, he rebuilds it, creating the infinite Loop.", "Ideal: Eternal Life", DoradoPrimario, Icons.Default.WbSunny),
        RegistroWiki("r2", "razas", "God of Time", "The Reclusive Watcher", "Hidden from the rest of the world, he is the only one besides the deities who knows the truth about the infinite loops.", "Absolute Knowledge", DoradoPrimario, Icons.Default.HourglassEmpty),
        RegistroWiki("r3", "razas", "Humans and Civilians", "Common Population", "Highly susceptible to destructive corrosion. Many suffer amidst war tensions and depend on adventurers to survive.", "Weakness: Mortals", DoradoPrimario, Icons.Default.EmojiPeople),
        RegistroWiki("r4", "razas", "Hybrids", "Bestial Races (E.g. Dog/Human)", "Discriminated against in large cities, they often find refuge and total freedom in the Guild's facilities.", "Traits: Adaptive Physique", DoradoPrimario, Icons.Default.Pets)
    )

    // --- 2. MAPA DE REINOS (ESPAÑOL E INGLÉS) ---
    private val reinosEs = listOf(
        ReinoMapa(1, "Kairos", "BOSQUE OCCIDENTAL", "Vigía del Tiempo Quebrado", "Asentamiento fortificado entre pinares espesos. Primer punto de control.", "Rango Bronce (Niv. 5-12)", "Trampas y bestias de sombra.", "Acechador Mayor"),
        ReinoMapa(2, "Vlaskaya", "CUMBRES NORTE", "Ciudadela de las Agujas", "Custodiada por sierpes aladas en la cordillera oscura.", "Rango Plata (Niv. 15-22)", "Vientos huracanados.", "Arconte de Jade"),
        ReinoMapa(3, "Silvaris", "VALLE CENTRAL", "Encrucijada de los Ríos", "Rutas comerciales atacadas por la propagación de la corrosión.", "Rango Bronce (Niv. 5-15)", "Miasma tóxico.", "Gólem de Raíz"),
        ReinoMapa(4, "Shkaldrim", "MINAS DEL NORTE", "Bastión de Escarcha", "Yacimientos donde se forjan armas encantadas.", "Rango Oro (Niv. 20-28)", "Hielo y criaturas de vacío.", "Coloso de Escarcha"),
        ReinoMapa(5, "Durnhelm", "PÁRAMOS ESTE", "Fortaleza de Hierro", "Antigua capital enana que resiste asedios.", "Rango Oro (Niv. 25-30)", "Magma y corrupción.", "Rey Caído"),
        ReinoMapa(6, "Valcordia", "ISLA CENTRAL", "Metrópoli de Tensiones", "Capital dividida por la discriminación.", "Rango Plata (Niv. 12-20)", "Conflictos sociales.", "Inquisidor Corrompido"),
        ReinoMapa(7, "Gorak-Thul", "DESIERTO SUR", "Ruinas Primigenias", "Dunas implacables con criaturas masivas exiliadas.", "Rango Maestro (Niv. 30-35)", "Tormentas de arena.", "Gusano de las Dunas"),
        ReinoMapa(8, "Shivara", "PANTANOS SURESTE", "Dominio de las Sombras", "Aguas estancadas, hogar de cultistas.", "Rango Oro (Niv. 28-32)", "Agua altamente tóxica.", "Bruja del Pantano"),
        ReinoMapa(9, "Lorialis", "ARCHIPIÉLAGO SUR", "Santuario de Coral", "Islas con mareas que traen criaturas del abismo.", "Rango Plata (Niv. 18-25)", "Sirenas oscuras.", "Leviatán Menor"),
        ReinoMapa(10, "Thalania", "ISLAS DEL ESTE", "Los Reinos Gemelos", "Naciones al borde de la aniquilación por el vacío.", "Rango Maestro (Niv. 35+)", "Anomalías puras.", "Avatar del Bucle")
    )
    private val reinosEn = listOf(
        ReinoMapa(1, "Kairos", "WESTERN FOREST", "Watcher of Broken Time", "Fortified settlement among thick pine forests. First checkpoint.", "Bronze Rank (Lv. 5-12)", "Traps & shadow beasts.", "Major Stalker"),
        ReinoMapa(2, "Vlaskaya", "NORTHERN PEAKS", "Citadel of Spires", "Guarded by winged serpents in the dark mountain range.", "Silver Rank (Lv. 15-22)", "Hurricane winds.", "Jade Archon"),
        ReinoMapa(3, "Silvaris", "CENTRAL VALLEY", "Crossroads of Rivers", "Trade routes attacked by the spread of corrosion.", "Bronze Rank (Lv. 5-15)", "Toxic miasma.", "Root Golem"),
        ReinoMapa(4, "Shkaldrim", "NORTHERN MINES", "Frost Bastion", "Deposits where enchanted weapons are forged.", "Gold Rank (Lv. 20-28)", "Ice & void creatures.", "Frost Colossus"),
        ReinoMapa(5, "Durnhelm", "EASTERN MOORS", "Iron Fortress", "Ancient dwarven capital resisting sieges.", "Gold Rank (Lv. 25-30)", "Magma & corruption.", "Fallen King"),
        ReinoMapa(6, "Valcordia", "CENTRAL ISLAND", "Metropolis of Tensions", "Capital divided by discrimination.", "Silver Rank (Lv. 12-20)", "Social conflicts.", "Corrupted Inquisitor"),
        ReinoMapa(7, "Gorak-Thul", "SOUTHERN DESERT", "Primeval Ruins", "Relentless dunes with massive exiled creatures.", "Master Rank (Lv. 30-35)", "Sandstorms.", "Dune Worm"),
        ReinoMapa(8, "Shivara", "SOUTHEAST SWAMPS", "Domain of Shadows", "Stagnant waters, home to cultists.", "Gold Rank (Lv. 28-32)", "Highly toxic water.", "Swamp Witch"),
        ReinoMapa(9, "Lorialis", "SOUTHERN ARCHIPELAGO", "Coral Sanctuary", "Islands with tides bringing abyssal creatures.", "Silver Rank (Lv. 18-25)", "Dark sirens.", "Minor Leviathan"),
        ReinoMapa(10, "Thalania", "EASTERN ISLANDS", "The Twin Realms", "Nations on the brink of annihilation by the void.", "Master Rank (Lv. 35+)", "Pure anomalies.", "Loop Avatar")
    )

    // --- 3. BUCLE Y BIOMAS (ESPAÑOL E INGLÉS) ---
    private val fasesEs = listOf(
        FaseBucle("I", "I. Amnesia", "Fase I: Tutorial", "Ciudad", "Protagonista amnésico rescatado por Elizaveta y Rasku.", "Integración al Gremio."),
        FaseBucle("II", "II. Déjà vus", "Fase II: Recuerdos", "Tensión", "Experimenta déjà vus sobre sucesos pasados.", "Lore y misiones secundarias."),
        FaseBucle("III", "III. Corrosión", "Fase III: El Vacío", "Peligro", "Masas oscuras que esparcen corrosión irreversible.", "Combate y purificación."),
        FaseBucle("IV", "IV. Revelación", "Fase IV: Secreto", "Ruptura", "Revelación del bucle infinito por el Dios del Tiempo.", "Poder dual desbloqueado.")
    )
    private val fasesEn = listOf(
        FaseBucle("I", "I. Amnesia", "Phase I: Tutorial", "City", "Amnesiac protagonist rescued by Elizaveta & Rasku.", "Guild Integration."),
        FaseBucle("II", "II. Déjà vus", "Phase II: Memories", "Tension", "Experiences déjà vu regarding past events.", "Lore and side quests."),
        FaseBucle("III", "III. Corrosion", "Phase III: The Void", "Danger", "Dark masses spreading irreversible corrosion.", "Combat & purification."),
        FaseBucle("IV", "IV. Revelation", "Phase IV: Secret", "Rupture", "Infinite loop revealed by the God of Time.", "Dual power unlocked.")
    )

    private val biomasEs = listOf(
        DiapositivaBioma("Ciudad Oculta", "TUTORIAL", "Kairos (Niv. 1-5)", "Paz Relativa", "Refugio donde humanos e híbridos conviven.", listOf(EsmeraldaGremio, AzulProfundo), R.drawable.bosque_combate),
        DiapositivaBioma("Caminos Contaminados", "CORROSIÓN", "Valcordia (Niv. 6)", "Masas Oscuras", "Zonas afectadas por el vacío.", listOf(Color(0xFF115E59), AzulProfundo), R.drawable.bosque_oscuro_combate)
    )
    private val biomasEn = listOf(
        DiapositivaBioma("Hidden City", "TUTORIAL", "Kairos (Lv. 1-5)", "Relative Peace", "Refuge where humans and hybrids coexist.", listOf(EsmeraldaGremio, AzulProfundo), R.drawable.bosque_combate),
        DiapositivaBioma("Contaminated Paths", "CORROSION", "Valcordia (Lv. 6)", "Dark Masses", "Zones affected by the void.", listOf(Color(0xFF115E59), AzulProfundo), R.drawable.bosque_oscuro_combate)
    )

    // =========================================================================
    // COMUNIDAD (TABLÓN DEL GREMIO) - CONEXIÓN A FIREBASE FIRESTORE
    // =========================================================================
    data class ComentarioGremio(val autor: String, val insignia: androidx.compose.ui.graphics.vector.ImageVector, val color: androidx.compose.ui.graphics.Color, val texto: String, val tiempo: String)

    data class PostGremio(
        val id: String,
        val nombreAutor: String,
        val tituloAutor: String,
        val avatarUri: String = "",
        val mensaje: String,
        val imagenAdjuntaRes: Int? = null,
        var honor: Int,
        var comentarios: List<ComentarioGremio> = emptyList(),
        val tiempoEs: String,
        val tiempoEn: String,
        val esOficial: Boolean = false
    )

    // Posts de muestra (con Administrador)
    val postsPrueba = listOf(
        PostGremio("1", "Administrador", "Primordial de Dualidad", "", "¡Bienvenidos al Tablón del Gremio! Aquí podrán compartir sus hallazgos sobre la corrosión, subir capturas y buscar aliados para expediciones.", null, 156, 
            listOf(ComentarioGremio("Elizaveta_Fan", Icons.Default.AutoAwesome, AzulGremio, "¡Increíble iniciativa! Estaré compartiendo mis teorías pronto.", "Hace 30 min")), 
            "Hace 1 hora", "1 hour ago", true),
        PostGremio("2", "ExploradorNovato", "Iniciado", "", "Ayer estuve cerca del Bosque Occidental (Kairos) y logré tomar esta fotografía de las masas oscuras antes de escapar. ¡Tengan cuidado!", R.drawable.bosque_oscuro_combate, 89, 
            listOf(
                ComentarioGremio("Rasku_Pro", Icons.Default.Bolt, EsmeraldaGremio, "Hermano, tuviste suerte de salir vivo de ahí. Esas cosas son letales.", "Hace 2 horas"),
                ComentarioGremio("Gral_Calabaza", Icons.Default.Shield, RojoCinta, "Eviten esa zona por ahora. Enviaremos un escuadrón de limpieza.", "Hace 1 hora")
            ), 
            "Hace 3 horas", "3 hours ago"),
        PostGremio("3", "Elizaveta_Fan", "Mago de Agua", "", "Definitivamente la Iglesia de la Creación oculta algo. Si el Dios del Tiempo sabe la verdad, ¿por qué los exorcistas mienten? ¿Qué opinan ustedes?", null, 45, emptyList(), "Hace 5 horas", "5 hours ago")
    )

    // Inicializamos con los posts de prueba
    var postsComunidadLive = mutableStateListOf<PostGremio>().apply { addAll(postsPrueba) }

    // Alias de compatibilidad
    val postsComunidad get() = postsComunidadLive

    // Función 1: ESCUCHAR LOS MENSAJES EN TIEMPO REAL Y ANCLAR EL DEL ADMIN
    fun inicializarTablonGremio() {
        db.collection("comunidad_posts")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                
                if (snapshot != null) {
                    postsComunidadLive.clear()
                    
                    // 1. INYECTAMOS EL MENSAJE FIJO DEL ADMINISTRADOR HASTA ARRIBA
                    postsComunidadLive.add(
                        PostGremio(
                            id = "post_fijo_admin",
                            nombreAutor = "Administrador",
                            tituloAutor = "Primordial de Dualidad",
                            avatarUri = "",
                            mensaje = if (esIngles) "Welcome to the Guild Board! Here you can share your findings on corrosion, upload screenshots, and look for allies for expeditions." else "¡Bienvenidos al Tablón del Gremio! Aquí podrán compartir sus hallazgos sobre la corrosión, subir capturas y buscar aliados para expediciones.",
                            imagenAdjuntaRes = null,
                            honor = 999, // Un número alto de honor para el Admin
                            comentarios = emptyList(),
                            tiempoEs = "📌 Fijado",
                            tiempoEn = "📌 Pinned",
                            esOficial = true
                        )
                    )

                    // 2. LUEGO CARGAMOS TODOS LOS MENSAJES REALES DE FIREBASE
                    for (doc in snapshot.documents) {
                        val nombre = doc.getString("autorNombre") ?: "Aventurero"
                        val titulo = doc.getString("autorTitulo") ?: "Iniciado"
                        val mensaje = doc.getString("contenido") ?: ""
                        val uriImagen = doc.getString("imagenUri") ?: ""
                        val honor = doc.getLong("honor")?.toInt() ?: 0
                        val esAdmin = doc.getBoolean("esOficial") ?: false
                        
                        val fechaStr = "Recientemente" 
                        val fechaStrEn = "Recently"

                        postsComunidadLive.add(
                            PostGremio(
                                id = doc.id,
                                nombreAutor = nombre,
                                tituloAutor = titulo,
                                avatarUri = "", // En el futuro aquí iría la foto de perfil del autor
                                mensaje = mensaje,
                                // Si hay una ruta de imagen de Firebase, se manejaría con Coil. Por ahora lo dejamos nulo para las de internet.
                                imagenAdjuntaRes = null, 
                                honor = honor,
                                comentarios = emptyList(), // En el futuro se leerían de una subcolección
                                tiempoEs = fechaStr,
                                tiempoEn = fechaStrEn,
                                esOficial = esAdmin
                            )
                        )
                    }
                }
            }
    }

    // Función 2: PUBLICAR UN NUEVO MENSAJE (Ahora acepta imágenes)
    fun publicarEnTablon(mensaje: String, uriImagen: String? = null, alTerminar: () -> Unit) {
        val usuario = usuarioActual
        if (usuario == null || mensaje.isBlank()) {
            mostrarNotificacion(if (esIngles) "Write a message first." else "Escribe un mensaje primero.")
            return
        }

        activarAnimacionCarga("Enviando cuervo mensajero...") {
            val nuevoPost = hashMapOf(
                "autorId" to (auth.currentUser?.uid ?: "unknown"),
                "autorNombre" to usuario.nombreUsuario,
                "autorTitulo" to usuario.tituloOpcional,
                "contenido" to mensaje,
                "imagenUri" to (uriImagen ?: ""),
                "honor" to 0,
                "esOficial" to (usuario.tituloOpcional.lowercase().contains("admin") || usuario.nombreUsuario.lowercase().contains("administrador") || usuario.nombreUsuario.lowercase().contains("zyren")),
                "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp() 
            )

            db.collection("comunidad_posts").add(nuevoPost)
                .addOnSuccessListener {
                    mostrarNotificacion(if (esIngles) "Post published!" else "¡Mensaje publicado!")
                    alTerminar()
                }
                .addOnFailureListener {
                    mostrarNotificacion(if (esIngles) "Error publishing." else "Error al publicar.")
                    alTerminar()
                }
        }
    }
}