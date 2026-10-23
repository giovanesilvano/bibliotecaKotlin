// data/model/Emprestimo.kt
package com.example.bibliotecakotlin.data.model

data class Emprestimo(
    val id: Long,
    val usuarioId: Long,
    val livro: Livro,
    val emprestadoEm: String,
    val devolverAte: String,
    val devolvidoEm: String?,
    val status: StatusEmprestimo,
    val infoPrazo: String
)