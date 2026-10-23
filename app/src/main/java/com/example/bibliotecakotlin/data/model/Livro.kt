// data/model/Livro.kt  (ajuste ao seu modelo atual, apenas ganha "id")
package com.example.bibliotecakotlin.data.model

import androidx.compose.ui.graphics.Color

data class Livro(
    val id: Long = 0,
    val titulo: String,
    val autor: String,
    val categoria: String,
    val capaTexto: String,
    val capaCor: Color,
    val disponivel: Boolean = true,
    val isbn: String,
    val edicao: String,
    val estante: String,
    val quantidade: Int = 1,
    val quantidadeDisponivel: Int = 1
)