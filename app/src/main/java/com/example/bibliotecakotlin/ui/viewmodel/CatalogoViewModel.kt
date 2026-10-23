// ui/viewmodel/CatalogoViewModel.kt
package com.example.bibliotecakotlin.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bibliotecakotlin.data.dal.LivroDao
import com.example.bibliotecakotlin.data.model.Livro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CatalogoViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = LivroDao(app)

    var livros by mutableStateOf<List<Livro>>(emptyList()); private set
    var carregando by mutableStateOf(false); private set
    var mensagem by mutableStateOf<String?>(null); private set

    fun listar() {
        viewModelScope.launch {
            carregando = true
            try { livros = withContext(Dispatchers.IO) { dao.listar() } }
            catch (e: Exception) { mensagem = e.message }
            finally { carregando = false }
        }
    }
}