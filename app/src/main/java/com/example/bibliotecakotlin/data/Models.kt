package com.exemplo.biblioteca.data

import androidx.compose.ui.graphics.Color

data class Livro(
    val titulo: String,
    val autor: String,
    val categoria: String,
    val capaTexto: String,
    val capaCor: Color,
    val disponivel: Boolean,
    val isbn: String = "",
    val edicao: String = "",
    val estante: String = ""
)

enum class StatusEmprestimo { EM_DIA, ATRASADO }

data class Emprestimo(
    val livro: Livro,
    val emprestadoEm: String,
    val devolverAte: String,
    val status: StatusEmprestimo,
    val infoPrazo: String
)

object MockData {
    val cleanCode = Livro(
        "Clean Code", "Robert C. Martin", "Engenharia de Software · 2008",
        "CLEAN\nCODE", Color(0xFF455A64), true,
        isbn = "978-85-7522-000-0", edicao = "1ª edição", estante = "Estante TI · A-12"
    )
    val sapiens = Livro(
        "Sapiens", "Yuval Noah Harari", "História · Ciências Humanas",
        "SAPIENS", Color(0xFF8D6E63), false
    )
    val calculo = Livro(
        "Cálculo — Volume 1", "James Stewart", "Matemática · 8ª edição",
        "CÁLCULO\nVOL. 1", Color(0xFF1565C0), true
    )
    val algoritmos = Livro(
        "Entendendo Algoritmos", "Aditya Bhargava", "Computação",
        "ALGORITMOS", Color(0xFF283593), false
    )
    val sociologia = Livro(
        "Sociologia", "Anthony Giddens", "Ciências Humanas",
        "SOCIOLOGIA", Color(0xFF6D4C41), false
    )
    val fisica = Livro(
        "Fundamentos de Física III", "Halliday & Resnick", "Física",
        "FÍSICA III", Color(0xFF00838F), false
    )

    val catalogo = listOf(cleanCode, sapiens, calculo)

    val emprestimos = listOf(
        Emprestimo(algoritmos, "05 ago. 2026", "02 set. 2026", StatusEmprestimo.EM_DIA, "6 dias restantes"),
        Emprestimo(sociologia, "22 jul. 2026", "19 ago. 2026", StatusEmprestimo.ATRASADO, "8 dias de atraso"),
        Emprestimo(fisica, "28 ago. 2026", "11 set. 2026", StatusEmprestimo.EM_DIA, "15 dias restantes")
    )
}