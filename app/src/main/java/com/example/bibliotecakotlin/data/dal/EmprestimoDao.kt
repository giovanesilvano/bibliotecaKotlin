// data/dal/EmprestimoDao.kt
package com.example.bibliotecakotlin.data.dal

import android.content.ContentValues
import android.content.Context
import androidx.compose.ui.graphics.Color
import com.example.bibliotecakotlin.data.local.AppDatabase
import com.example.bibliotecakotlin.data.model.Emprestimo
import com.example.bibliotecakotlin.data.model.Livro
import com.example.bibliotecakotlin.data.model.StatusEmprestimo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

internal class EmprestimoDao(context: Context) {
    private val helper = AppDatabase(context)
    private val fmt = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    private val corPadrao = Color(0xFF1E88E5)

    fun emprestar(usuarioId: Long, livroId: Long, prazoDias: Int = 10): Long {
        val db = helper.writableDatabase
        db.beginTransaction()
        try {
            val ativos = db.rawQuery(
                "SELECT COUNT(*) FROM emprestimos WHERE livro_id = ? AND devolvido_em IS NULL",
                arrayOf(livroId.toString())
            ).use { c -> if (c.moveToFirst()) c.getInt(0) else 0 }

            val total = db.rawQuery(
                "SELECT quantidade FROM livros WHERE id = ?",
                arrayOf(livroId.toString())
            ).use { c -> if (c.moveToFirst()) c.getInt(0) else 0 }

            check(total > 0 && ativos < total) { "Livro indisponível" }

            val agora = System.currentTimeMillis()
            val v = ContentValues().apply {
                put("usuario_id", usuarioId)
                put("livro_id", livroId)
                put("emprestado_em", agora)
                put("devolver_ate", agora + TimeUnit.DAYS.toMillis(prazoDias.toLong()))
            }
            val id = db.insertOrThrow("emprestimos", null, v)
            db.setTransactionSuccessful()
            return id
        } finally {
            db.endTransaction()
        }
    }

    fun devolver(emprestimoId: Long, usuarioId: Long) {
        val v = ContentValues().apply { put("devolvido_em", System.currentTimeMillis()) }
        val linhas = helper.writableDatabase.update(
            "emprestimos", v,
            "id = ? AND usuario_id = ? AND devolvido_em IS NULL",
            arrayOf(emprestimoId.toString(), usuarioId.toString())
        )
        check(linhas > 0) { "Empréstimo não encontrado ou já devolvido" }
    }

    fun listarPorUsuario(usuarioId: Long): List<Emprestimo> {
        val lista = mutableListOf<Emprestimo>()
        helper.readableDatabase.rawQuery(
            """SELECT e.id, e.usuario_id, e.emprestado_em, e.devolver_ate, e.devolvido_em,
                      l.id, l.titulo, l.autor, l.categoria, l.capa_texto,
                      l.isbn, l.edicao, l.estante, l.quantidade
               FROM emprestimos e JOIN livros l ON l.id = e.livro_id
               WHERE e.usuario_id = ?
               ORDER BY e.emprestado_em DESC""",
            arrayOf(usuarioId.toString())
        ).use { c ->
            val agora = System.currentTimeMillis()
            while (c.moveToNext()) {
                val devolverAte = c.getLong(3)
                val devolvidoEm = if (c.isNull(4)) null else c.getLong(4)
                val dias = TimeUnit.MILLISECONDS.toDays(devolverAte - agora).toInt()
                val status = when {
                    devolvidoEm != null -> StatusEmprestimo.DEVOLVIDO
                    agora > devolverAte -> StatusEmprestimo.ATRASADO
                    else -> StatusEmprestimo.EM_DIA
                }
                val info = when (status) {
                    StatusEmprestimo.DEVOLVIDO -> "Devolvido em ${fmt.format(Date(devolvidoEm!!))}"
                    StatusEmprestimo.ATRASADO -> "Atrasado há ${-dias} dia(s)"
                    StatusEmprestimo.EM_DIA -> "Faltam $dias dia(s)"
                }
                val livro = Livro(
                    id = c.getLong(5),
                    titulo = c.getString(6),
                    autor = c.getString(7),
                    categoria = c.getString(8),
                    capaTexto = c.getString(9),
                    capaCor = corPadrao,
                    isbn = c.getString(10),
                    edicao = c.getString(11),
                    estante = c.getString(12),
                    quantidade = c.getInt(13),
                    quantidadeDisponivel = c.getInt(13),
                    disponivel = devolvidoEm != null
                )
                lista.add(
                    Emprestimo(
                        id = c.getLong(0), usuarioId = c.getLong(1), livro = livro,
                        emprestadoEm = fmt.format(Date(c.getLong(2))),
                        devolverAte = fmt.format(Date(devolverAte)),
                        devolvidoEm = devolvidoEm?.let { fmt.format(Date(it)) },
                        status = status, infoPrazo = info
                    )
                )
            }
        }
        return lista
    }
}