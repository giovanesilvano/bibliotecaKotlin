package com.example.bibliotecakotlin.data.dal

import android.content.ContentValues
import android.content.Context
import androidx.compose.ui.graphics.Color
import com.example.bibliotecakotlin.data.local.AppDatabase
import com.example.bibliotecakotlin.data.model.Livro

internal class LivroDao(context: Context) {
    private val appContext = context.applicationContext
    private val helper = AppDatabase(appContext)

    private val corPadrao = Color(0xFF1E88E5)

    private val select = """
        SELECT l.id, l.titulo, l.autor, l.categoria, l.capa_texto,
               l.isbn, l.edicao, l.estante,
               NOT EXISTS(SELECT 1 FROM emprestimos e
                          WHERE e.livro_id = l.id AND e.devolvido_em IS NULL) AS disponivel
        FROM livros l
    """.trimIndent()

    fun listar(): List<Livro> {
        semearSeNecessario()
        val lista = mutableListOf<Livro>()
        helper.readableDatabase.rawQuery("$select ORDER BY l.titulo COLLATE NOCASE", null).use { c ->
            while (c.moveToNext()) lista += c.paraLivro()
        }
        return lista
    }

    fun buscarPorId(id: Long): Livro? =
        helper.readableDatabase.rawQuery("$select WHERE l.id = ?", arrayOf(id.toString())).use { c ->
            if (c.moveToFirst()) c.paraLivro() else null
        }

    fun inserir(l: Livro): Long =
        helper.writableDatabase.insertOrThrow("livros", null, valores(l))

    fun atualizar(l: Livro): Int =
        helper.writableDatabase.update("livros", valores(l), "id = ?", arrayOf(l.id.toString()))

    fun excluir(id: Long) {
        val emprestado = helper.readableDatabase.rawQuery(
            "SELECT 1 FROM emprestimos WHERE livro_id = ? AND devolvido_em IS NULL",
            arrayOf(id.toString())
        ).use { it.moveToFirst() }
        if (emprestado) throw IllegalStateException("Este livro está emprestado e não pode ser excluído.")
        helper.writableDatabase.delete("livros", "id = ?", arrayOf(id.toString()))
    }

    private fun valores(l: Livro) = ContentValues().apply {
        put("titulo", l.titulo)
        put("autor", l.autor)
        put("categoria", l.categoria)
        put("capa_texto", l.capaTexto)
        put("isbn", l.isbn)
        put("edicao", l.edicao)
        put("estante", l.estante)
    }

    private fun android.database.Cursor.paraLivro() = Livro(
        id = getLong(0),
        titulo = getString(1),
        autor = getString(2),
        categoria = getString(3),
        capaTexto = getString(4),
        capaCor = corPadrao,
        isbn = getString(5),
        edicao = getString(6),
        estante = getString(7),
        disponivel = getInt(8) == 1
    )

    // Livros de exemplo, inseridos uma única vez
    private fun semearSeNecessario() {
        val prefs = appContext.getSharedPreferences("biblioteca", Context.MODE_PRIVATE)
        if (prefs.getBoolean("seed_livros", false)) return
        val vazio = helper.readableDatabase.rawQuery("SELECT COUNT(*) FROM livros", null)
            .use { it.moveToFirst() && it.getInt(0) == 0 }
        if (vazio) {
            listOf(
                Livro(titulo = "Clean Code", autor = "Robert C. Martin", categoria = "Tecnologia",
                    capaTexto = "CC", capaCor = corPadrao, isbn = "9780132350884", edicao = "1ª", estante = "A1"),
                Livro(titulo = "Dom Casmurro", autor = "Machado de Assis", categoria = "Romance",
                    capaTexto = "DC", capaCor = corPadrao, isbn = "9788535910663", edicao = "2ª", estante = "B2"),
                Livro(titulo = "Sociologia", autor = "Anthony Giddens", categoria = "Ciências Sociais",
                    capaTexto = "SO", capaCor = corPadrao, isbn = "9788584290413", edicao = "6ª", estante = "C3"),
                Livro(titulo = "O Pequeno Príncipe", autor = "Antoine de Saint-Exupéry", categoria = "Infantil",
                    capaTexto = "PP", capaCor = corPadrao, isbn = "9788522008731", edicao = "1ª", estante = "D4")
            ).forEach { inserir(it) }
        }
        prefs.edit().putBoolean("seed_livros", true).apply()
    }
}