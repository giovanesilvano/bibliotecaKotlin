package com.example.bibliotecakotlin.data.model

data class Avaliacao(
    val id: Long = 0,
    val usuarioId: Long,
    val livroId: Long,
    val livroTitulo: String = "",
    val nota: Int,
    val comentario: String,
    val criadoEm: String = ""
)
