// ui/viewmodel/UsuarioViewModel.kt
package com.example.bibliotecakotlin.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bibliotecakotlin.auth.AuthManager
import com.example.bibliotecakotlin.auth.SessaoUsuario
import com.example.bibliotecakotlin.data.dal.UsuarioDao
import com.example.bibliotecakotlin.data.model.Usuario
import com.example.bibliotecakotlin.validation.CadastroValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsuarioViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = UsuarioDao(app)

    var usuarios by mutableStateOf<List<Usuario>>(emptyList()); private set
    var usuario by mutableStateOf<Usuario?>(null); private set
    var nome by mutableStateOf(""); private set
    var carregando by mutableStateOf(false); private set
    var salvo by mutableStateOf(false); private set
    var mensagem by mutableStateOf<String?>(null); private set

    fun listar() {
        viewModelScope.launch {
            carregando = true
            try { usuarios = withContext(Dispatchers.IO) { dao.listar() } }
            catch (e: Exception) { mensagem = e.message }
            finally { carregando = false }
        }
    }

    fun buscarPorId(id: Long) {
        viewModelScope.launch {
            carregando = true
            try {
                val u = withContext(Dispatchers.IO) { dao.buscarPorId(id) }
                usuario = u
                nome = u?.nome ?: ""
            } catch (e: Exception) { mensagem = e.message }
            finally { carregando = false }
        }
    }

    fun mudarNome(valor: String) { nome = valor }

    fun salvar(novaSenha: String = "") {
        val atual = usuario ?: return
        val trocaSenha = novaSenha.isNotBlank()
        CadastroValidator.validar(nome, atual.email, if (trocaSenha) novaSenha else null)
            ?.let { mensagem = it; return }
        viewModelScope.launch {
            carregando = true
            try {
                withContext(Dispatchers.IO) {
                    if (trocaSenha) {
                        val salt = AuthManager.criarSalt()
                        dao.alterar(atual.id, nome.trim(), AuthManager.gerarHash(novaSenha, salt), salt)
                    } else dao.alterar(atual.id, nome.trim())
                }
                salvo = true
            } catch (e: Exception) { mensagem = e.message }
            finally { carregando = false }
        }
    }

    fun excluir(id: Long) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { dao.excluir(id, SessaoUsuario.exigirUsuario()) }
                usuarios = withContext(Dispatchers.IO) { dao.listar() }
            } catch (e: Exception) { mensagem = e.message }
        }
    }

    fun consumirSalvo() { salvo = false; mensagem = null }
}