package com.example.bibliotecakotlin.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.graphics.Color
import com.example.bibliotecakotlin.data.dal.LivroDao
import com.example.bibliotecakotlin.data.model.Livro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CatalogoViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = LivroDao(app)

    var livros by mutableStateOf<List<Livro>>(emptyList()); private set
    var livro by mutableStateOf<Livro?>(null); private set
    var carregando by mutableStateOf(false); private set
    var salvo by mutableStateOf(false); private set
    var mensagem by mutableStateOf<String?>(null); private set

    fun listar() {
        viewModelScope.launch {
            carregando = true
            try {
                livros = withContext(Dispatchers.IO) { dao.listar() }
            } catch (e: Exception) {
                mensagem = e.message ?: "Erro ao carregar o catálogo."
            } finally {
                carregando = false
            }
        }
    }

    fun buscarPorId(id: Long) {
        viewModelScope.launch {
            livro = try {
                withContext(Dispatchers.IO) { dao.buscarPorId(id) }
            } catch (e: Exception) {
                mensagem = e.message ?: "Erro ao carregar o livro."
                null
            }
        }
    }

    fun limparLivro() { livro = null }

    fun salvar(
        id: Long, titulo: String, autor: String, categoria: String,
        isbn: String, edicao: String, estante: String, quantidade: Int = 1,
        capaCor: Color = Color(0xFF1E88E5)
    ) {
        if (titulo.isBlank() || autor.isBlank() || categoria.isBlank()) {
            mensagem = "Informe título, autor e categoria."
            return
        }
        val qtd = if (quantidade > 0) quantidade else 1
        viewModelScope.launch {
            carregando = true
            try {
                val l = Livro(
                    id = id,
                    titulo = titulo.trim(),
                    autor = autor.trim(),
                    categoria = categoria.trim(),
                    capaTexto = titulo.trim().uppercase().take(20),
                    capaCor = capaCor,
                    isbn = isbn.trim().ifBlank { "S/N" },
                    edicao = edicao.trim().ifBlank { "-" },
                    estante = estante.trim().ifBlank { "-" },
                    quantidade = qtd,
                    quantidadeDisponivel = qtd
                )
                withContext(Dispatchers.IO) { if (id == 0L) dao.inserir(l) else dao.atualizar(l) }
                salvo = true
            } catch (e: Exception) {
                mensagem = e.message ?: "Erro ao salvar o livro."
            } finally {
                carregando = false
            }
        }
    }

    fun excluir(id: Long) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { dao.excluir(id) }
                livros = withContext(Dispatchers.IO) { dao.listar() }
            } catch (e: Exception) {
                mensagem = e.message ?: "Erro ao excluir o livro."
            }
        }
    }

    fun consumirSalvo() { salvo = false }
    fun consumirMensagem() { mensagem = null }
}