package com.example.bibliotecakotlin.data.dal

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import com.example.bibliotecakotlin.data.local.AppDatabase
import com.example.bibliotecakotlin.data.model.Avaliacao
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class AvaliacaoDao(context: Context) {
    private val helper = AppDatabase(context.applicationContext)
    private val formato = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

    private val select = """
        SELECT a.id, a.usuario_id, a.livro_id, l.titulo, a.nota, a.comentario, a.criado_em
        FROM avaliacoes a JOIN livros l ON l.id = a.livro_id
    """.trimIndent()

    fun inserir(usuarioId: Long, livroId: Long, nota: Int, comentario: String): Long {
        val valores = ContentValues().apply {
            put("usuario_id", usuarioId)
            put("livro_id", livroId)
            put("nota", nota)
            put("comentario", comentario)
            put("criado_em", System.currentTimeMillis())
        }
        try {
            return helper.writableDatabase.insertOrThrow("avaliacoes", null, valores)
        } catch (e: SQLiteConstraintException) {
            throw IllegalStateException("Você já avaliou este livro")
        }
    }

    fun listarPorUsuario(usuarioId: Long): List<Avaliacao> {
        val lista = mutableListOf<Avaliacao>()
        helper.readableDatabase.rawQuery(
            "$select WHERE a.usuario_id = ? ORDER BY a.criado_em DESC",
            arrayOf(usuarioId.toString())
        ).use { c ->
            while (c.moveToNext()) lista.add(paraAvaliacao(c))
        }
        return lista
    }

    fun buscarPorId(id: Long, usuarioId: Long): Avaliacao? =
        helper.readableDatabase.rawQuery(
            "$select WHERE a.id = ? AND a.usuario_id = ?",
            arrayOf(id.toString(), usuarioId.toString())
        ).use { c ->
            if (c.moveToFirst()) paraAvaliacao(c) else null
        }

    fun atualizar(id: Long, usuarioId: Long, nota: Int, comentario: String) {
        val valores = ContentValues().apply {
            put("nota", nota)
            put("comentario", comentario)
        }
        val linhas = helper.writableDatabase.update(
            "avaliacoes", valores,
            "id = ? AND usuario_id = ?",
            arrayOf(id.toString(), usuarioId.toString())
        )
        check(linhas > 0) { "Avaliação não encontrada" }
    }

    fun excluir(id: Long, usuarioId: Long) {
        val linhas = helper.writableDatabase.delete(
            "avaliacoes",
            "id = ? AND usuario_id = ?",
            arrayOf(id.toString(), usuarioId.toString())
        )
        check(linhas > 0) { "Avaliação não encontrada" }
    }

    private fun paraAvaliacao(c: Cursor) = Avaliacao(
        id = c.getLong(0),
        usuarioId = c.getLong(1),
        livroId = c.getLong(2),
        livroTitulo = c.getString(3),
        nota = c.getInt(4),
        comentario = c.getString(5),
        criadoEm = formato.format(Date(c.getLong(6)))
    )
}
