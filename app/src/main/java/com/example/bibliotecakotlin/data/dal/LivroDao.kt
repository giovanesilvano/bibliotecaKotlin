// data/dal/LivroDao.kt
package com.example.bibliotecakotlin.data.dal

import android.content.Context
import android.database.Cursor
import androidx.compose.ui.graphics.Color
import com.example.bibliotecakotlin.data.local.AppDatabase
import com.example.bibliotecakotlin.data.model.Livro

internal class LivroDao(context: Context) {
    private val helper = AppDatabase(context)

    private val select = """
        SELECT l.id, l.titulo, l.autor, l.categoria, l.capa_texto, l.capa_cor,
               l.isbn, l.edicao, l.estante,
               (SELECT COUNT(*) FROM emprestimos e
                 WHERE e.livro_id = l.id AND e.devolvido_em IS NULL) AS ativos
        FROM livros l"""

    private fun Cursor.toLivro() = Livro(
        id = getLong(0), titulo = getString(1), autor = getString(2),
        categoria = getString(3), capaTexto = getString(4),
        capaCor = Color(getInt(5)), isbn = getString(6), edicao = getString(7),
        estante = getString(8), disponivel = getInt(9) == 0
    )

    fun listar(): List<Livro> {
        val lista = mutableListOf<Livro>()
        helper.readableDatabase.rawQuery("$select ORDER BY l.titulo", null).use { c ->
            while (c.moveToNext()) lista.add(c.toLivro())
        }
        return lista
    }

    fun buscarPorId(id: Long): Livro? {
        helper.readableDatabase.rawQuery("$select WHERE l.id = ?", arrayOf(id.toString())).use { c ->
            return if (c.moveToFirst()) c.toLivro() else null
        }
    }
}