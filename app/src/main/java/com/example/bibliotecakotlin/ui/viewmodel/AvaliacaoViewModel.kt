package com.example.bibliotecakotlin.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bibliotecakotlin.auth.SessaoUsuario
import com.example.bibliotecakotlin.data.dal.AvaliacaoDao
import com.example.bibliotecakotlin.data.dal.LivroDao
import com.example.bibliotecakotlin.data.model.Avaliacao
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AvaliacaoViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AvaliacaoDao(application)
    private val livroDao = LivroDao(application)
    private var livroId: Long? = null
    private var avaliacaoId: Long? = null

    var avaliacoes by mutableStateOf<List<Avaliacao>>(emptyList())
        private set
    var livroTitulo by mutableStateOf("")
        private set
    var nota by mutableStateOf(0)
        private set
    var comentario by mutableStateOf("")
        private set
    var carregando by mutableStateOf(false)
        private set
    var salvo by mutableStateOf(false)
        private set
    var mensagem by mutableStateOf<String?>(null)
        private set

    fun listar() {
        viewModelScope.launch {
            carregando = true
            try {
                avaliacoes = withContext(Dispatchers.IO) {
                    dao.listarPorUsuario(SessaoUsuario.exigirUsuario())
                }
            } catch (erro: CancellationException) {
                throw erro
            } catch (_: Exception) {
                mensagem = "Não foi possível carregar as avaliações"
            } finally {
                carregando = false
            }
        }
    }

    fun novaPara(id: Long) {
        livroId = id
        avaliacaoId = null
        nota = 0
        comentario = ""
        viewModelScope.launch {
            try {
                val livro = withContext(Dispatchers.IO) { livroDao.buscarPorId(id) }
                livroTitulo = livro?.titulo.orEmpty()
            } catch (erro: CancellationException) {
                throw erro
            } catch (_: Exception) {
                mensagem = "Não foi possível carregar o livro"
            }
        }
    }

    fun buscarPorId(id: Long) {
        avaliacaoId = id
        viewModelScope.launch {
            try {
                val avaliacao = withContext(Dispatchers.IO) {
                    dao.buscarPorId(id, SessaoUsuario.exigirUsuario())
                }
                if (avaliacao == null) {
                    mensagem = "Avaliação não encontrada"
                } else {
                    livroId = avaliacao.livroId
                    livroTitulo = avaliacao.livroTitulo
                    nota = avaliacao.nota
                    comentario = avaliacao.comentario
                }
            } catch (erro: CancellationException) {
                throw erro
            } catch (_: Exception) {
                mensagem = "Não foi possível carregar a avaliação"
            }
        }
    }

    fun mudarNota(valor: Int) {
        nota = valor
    }

    fun mudarComentario(valor: String) {
        if (valor.length > 500) return
        comentario = valor
    }

    fun salvar() {
        if (carregando) return
        val id = livroId ?: return
        if (nota !in 1..5) {
            mensagem = "Escolha uma nota de 1 a 5"
            return
        }
        if (comentario.isBlank()) {
            mensagem = "Escreva um comentário"
            return
        }
        carregando = true
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val usuarioId = SessaoUsuario.exigirUsuario()
                    val editando = avaliacaoId
                    if (editando == null) {
                        dao.inserir(usuarioId, id, nota, comentario.trim())
                    } else {
                        dao.atualizar(editando, usuarioId, nota, comentario.trim())
                    }
                }
                salvo = true
            } catch (erro: CancellationException) {
                throw erro
            } catch (erro: Exception) {
                mensagem = erro.message ?: "Não foi possível salvar a avaliação"
            } finally {
                carregando = false
            }
        }
    }

    fun excluir(id: Long) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    dao.excluir(id, SessaoUsuario.exigirUsuario())
                }
                listar()
            } catch (erro: CancellationException) {
                throw erro
            } catch (erro: Exception) {
                mensagem = erro.message ?: "Não foi possível excluir a avaliação"
            }
        }
    }

    fun consumirSalvo() {
        salvo = false
    }

    fun consumirMensagem() {
        mensagem = null
    }
}
