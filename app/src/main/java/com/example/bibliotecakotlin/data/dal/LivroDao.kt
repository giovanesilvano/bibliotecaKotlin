package com.example.bibliotecakotlin.data.dal

import android.content.ContentValues
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.bibliotecakotlin.data.local.AppDatabase
import com.example.bibliotecakotlin.data.model.Livro

internal class LivroDao(context: Context) {
    private val appContext = context.applicationContext
    private val helper = AppDatabase(appContext)

    private val select = """
        SELECT l.id, l.titulo, l.autor, l.categoria, l.capa_texto, l.capa_cor,
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
        put("capa_cor", l.capaCor.toArgb())
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
        capaCor = Color(getInt(5)),
        isbn = getString(6),
        edicao = getString(7),
        estante = getString(8),
        disponivel = getInt(9) == 1
    )

    // Livros de exemplo, inseridos uma única vez
    private fun semearSeNecessario() {
        val prefs = appContext.getSharedPreferences("biblioteca", Context.MODE_PRIVATE)
        if (prefs.getBoolean("seed_livros", false)) return
        val vazio = helper.readableDatabase.rawQuery("SELECT COUNT(*) FROM livros", null)
            .use { it.moveToFirst() && it.getInt(0) == 0 }
        if (vazio) {
            listOf(
                Livro(titulo = "Clean Code", autor = "Robert C. Martin", categoria = "Engenharia de Software",
                    capaTexto = "CLEAN CODE", capaCor = Color(0xFF1E3A8A), isbn = "000-0000000001",
                    edicao = "1ª edição", estante = "Estante A1"),
                Livro(titulo = "Dom Casmurro", autor = "Machado de Assis", categoria = "Literatura",
                    capaTexto = "DOM CASMURRO", capaCor = Color(0xFF7C2D12), isbn = "000-0000000002", edicao = "3ª edição", estante = "Estante B2"),
                Livro(titulo = "Sapiens", autor = "Yuval Noah Harari", categoria = "História",
                    capaTexto = "SAPIENS", capaCor = Color(0xFF166534), isbn = "000-0000000003", edicao = "2ª edição", estante = "Estante C1"),
                Livro(titulo = "O Pequeno Príncipe", autor = "Antoine de Saint-Exupéry", categoria = "Infantil",
                    capaTexto = "PEQUENO PRÍNCIPE", capaCor = Color(0xFF6B21A8), isbn = "000-0000000004", edicao = "1ª edição", estante = "Estante D3")
            ).forEach { inserir(it) }
        }
        prefs.edit().putBoolean("seed_livros", true).apply()
    }
}