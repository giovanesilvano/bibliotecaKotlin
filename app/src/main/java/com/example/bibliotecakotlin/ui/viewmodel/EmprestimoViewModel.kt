// ui/viewmodel/EmprestimoViewModel.kt
package com.example.bibliotecakotlin.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bibliotecakotlin.auth.SessaoUsuario
import com.example.bibliotecakotlin.data.dal.EmprestimoDao
import com.example.bibliotecakotlin.data.dal.LivroDao
import com.example.bibliotecakotlin.data.model.Emprestimo
import com.example.bibliotecakotlin.data.model.Livro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EmprestimoViewModel(app: Application) : AndroidViewModel(app) {
    private val emprestimoDao = EmprestimoDao(app)
    private val livroDao = LivroDao(app)

    var emprestimos by mutableStateOf<List<Emprestimo>>(emptyList()); private set
    var livro by mutableStateOf<Livro?>(null); private set
    var carregando by mutableStateOf(false); private set
    var concluido by mutableStateOf(false); private set
    var mensagem by mutableStateOf<String?>(null); private set

    fun listar() {
        viewModelScope.launch {
            carregando = true
            try {
                emprestimos = withContext(Dispatchers.IO) {
                    emprestimoDao.listarPorUsuario(SessaoUsuario.exigirUsuario())
                }
            } catch (e: Exception) { mensagem = e.message }
            finally { carregando = false }
        }
    }

    fun buscarLivro(id: Long) {
        viewModelScope.launch {
            try { livro = withContext(Dispatchers.IO) { livroDao.buscarPorId(id) } }
            catch (e: Exception) { mensagem = e.message }
        }
    }

    fun emprestar(livroId: Long) {
        viewModelScope.launch {
            carregando = true
            try {
                withContext(Dispatchers.IO) {
                    emprestimoDao.emprestar(SessaoUsuario.exigirUsuario(), livroId, prazoDias = 10)
                }
                concluido = true
            } catch (e: Exception) { mensagem = e.message ?: "Erro ao emprestar" }
            finally { carregando = false }
        }
    }

    fun devolver(emprestimoId: Long) {
        viewModelScope.launch {
            carregando = true
            try {
                withContext(Dispatchers.IO) {
                    emprestimoDao.devolver(emprestimoId, SessaoUsuario.exigirUsuario())
                }
                concluido = true
                listar()
            } catch (e: Exception) { mensagem = e.message ?: "Erro ao devolver" }
            finally { carregando = false }
        }
    }

    fun consumirResultado() { concluido = false; mensagem = null }
}