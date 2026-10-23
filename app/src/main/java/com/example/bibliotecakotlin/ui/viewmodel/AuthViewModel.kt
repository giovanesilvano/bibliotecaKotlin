// ui/viewmodel/AuthViewModel.kt
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
import com.example.bibliotecakotlin.validation.CadastroValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = UsuarioDao(app)

    var carregando by mutableStateOf(false); private set
    var concluido by mutableStateOf(false); private set
    var mensagem by mutableStateOf<String?>(null); private set

    fun cadastrar(nome: String, email: String, senha: String, confirmar: String = senha) {
        CadastroValidator.validar(nome, email, senha, confirmar)?.let { mensagem = it; return }
        viewModelScope.launch {
            carregando = true
            try {
                withContext(Dispatchers.IO) {
                    val salt = AuthManager.criarSalt()
                    dao.cadastrar(nome.trim(), email, AuthManager.gerarHash(senha, salt), salt)
                }
                concluido = true
            } catch (e: Exception) {
                mensagem = e.message ?: "Erro ao cadastrar"
            } finally {
                carregando = false
            }
        }
    }

    fun login(email: String, senha: String) {
        viewModelScope.launch {
            carregando = true
            try {
                val id = withContext(Dispatchers.IO) {
                    val salva = dao.buscarSenha(email)
                    if (salva != null && AuthManager.validarSenha(senha, salva.salt, salva.hash))
                        salva.usuarioID else null
                }
                if (id != null) {
                    SessaoUsuario.iniciar(id)
                    concluido = true
                } else mensagem = "E-mail ou senha inválidos"
            } catch (e: Exception) {
                mensagem = e.message ?: "Erro ao entrar"
            } finally {
                carregando = false
            }
        }
    }

    fun consumirResultado() { concluido = false; mensagem = null }
}