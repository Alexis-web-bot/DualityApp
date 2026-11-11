package com.example.dualityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.example.dualityapp.estado.ViewModelDuality
import com.example.dualityapp.pantallas.EnrutadorRaiz
import com.example.dualityapp.tema.AzulProfundo
import com.example.dualityapp.tema.DoradoPrimario
import com.example.dualityapp.tema.SuperficieOscura

// =========================================================================
// PUNTO DE ENTRADA PRINCIPAL (APP COMPANION DUALITY)
// Aquí arranca la aplicación. Inyectamos nuestro tema Dark Fantasy y
// llamamos al enrutador principal que gestiona el menú y el ViewModel.
// =========================================================================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Aplicamos nuestro esquema de colores personalizado (Dark Fantasy)
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = DoradoPrimario,
                    background = AzulProfundo,
                    surface = SuperficieOscura,
                    onPrimary = AzulProfundo,
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            ) {
                // Instanciamos nuestro gestor de estado (resistente a giros)
                val viewModel: ViewModelDuality = androidx.lifecycle.viewmodel.compose.viewModel()

                // Lanzamos el Enrutador que dibujará toda la interfaz
                EnrutadorRaiz(vm = viewModel)
            }
        }
    }
}