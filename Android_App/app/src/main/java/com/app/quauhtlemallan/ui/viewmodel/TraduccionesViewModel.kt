package com.app.quauhtlemallan.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.app.quauhtlemallan.data.model.Traduccion
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TraduccionesViewModel : ViewModel() {
    private val _traducciones = MutableStateFlow<List<Traduccion>>(emptyList())
    val traducciones: StateFlow<List<Traduccion>> = _traducciones

    private val _idiomas = MutableStateFlow<List<String>>(emptyList())
    val idiomas: StateFlow<List<String>> = _idiomas

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex

    init {
        fetchIdiomasYTraducciones()
    }

    fun onTabSelected(index: Int) {
        _selectedTabIndex.value = index
    }

    private fun fetchIdiomasYTraducciones() {
        // Lógica para obtener traducciones desde Firebase
        Firebase.database.getReference("traducciones")
            .get()
            .addOnSuccessListener { snapshot ->
                val traduccionesList = mutableListOf<Traduccion>()
                val idiomasList = mutableListOf<String>()

                snapshot.children.forEach { idiomaSnapshot ->
                    val idioma = idiomaSnapshot.key ?: return@forEach
                    idiomasList.add(idioma)

                    idiomaSnapshot.children.forEach { palabraSnapshot ->
                        val espanol = palabraSnapshot.key ?: ""
                        val lenguaMaya = palabraSnapshot.value.toString()
                        traduccionesList.add(Traduccion(espanol, lenguaMaya, idioma))
                    }
                }
                _traducciones.value = traduccionesList
                _idiomas.value = idiomasList
            }
            .addOnFailureListener {
                // Manejar error
            }
    }
}
