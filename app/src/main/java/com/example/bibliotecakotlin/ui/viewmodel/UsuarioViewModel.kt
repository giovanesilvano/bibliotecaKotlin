package com.example.bibliotecakotlin.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bibliotecakotlin.auth.SessaoUsuario
import com.example.bibliotecakotlin.data.dal.UsuarioAdminDao
import com.example.bibliotecakotlin.data.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsuarioViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = UsuarioAdminDao(app)

    var usuarios by mutableStateOf<List<Usuario>>(emptyList()); private set
    var usuario by mutableStateOf<Usuario?>(null); private set
    var nome by mutableStateOf(""); private set
    var carregando by mutableStateOf(false); private set
    var salvo by mutableStateOf(false); private set
    var mensagem by mutableStateOf<String?>(null); private set

    fun listar() {
        viewModelScope.launch {
            carregando = true
            try {
                usuarios = withContext(Dispatchers.IO) { dao.listar() }
            } catch (e: Exception) {
                mensagem = e.message ?: "Erro ao carregar usuários."
            } finally {
                carregando = false
            }
        }
    }

    fun buscarPorId(id: Long) {
        viewModelScope.launch {
            try {
                val u = withContext(Dispatchers.IO) { dao.buscarPorId(id) }
                usuario = u
                nome = u?.nome ?: ""
            } catch (e: Exception) {
                mensagem = e.message ?: "Erro ao carregar o usuário."
            }
        }
    }

    fun mudarNome(valor: String) { nome = valor }

    fun salvar(novaSenha: String) {
        val u = usuario ?: return
        if (nome.trim().length !in 3..100) {
            mensagem = "O nome deve ter entre 3 e 100 caracteres."
            return
        }
        if (novaSenha.isNotEmpty() && novaSenha.length < 6) {
            mensagem = "A nova senha deve ter ao menos 6 caracteres."
            return
        }
        viewModelScope.launch {
            carregando = true
            try {
                withContext(Dispatchers.IO) { dao.atualizar(u.id, nome.trim(), novaSenha) }
                salvo = true
            } catch (e: Exception) {
                mensagem = e.message ?: "Erro ao salvar o usuário."
            } finally {
                carregando = false
            }
        }
    }

    fun excluir(id: Long) {
        if (id == SessaoUsuario.usuarioId) {
            mensagem = "Não é possível excluir o usuário logado."
            return
        }
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { dao.excluir(id) }
                usuarios = withContext(Dispatchers.IO) { dao.listar() }
            } catch (e: Exception) {
                mensagem = e.message ?: "Erro ao excluir o usuário."
            }
        }
    }

    fun consumirSalvo() { salvo = false }
    fun consumirMensagem() { mensagem = null }
}