// data/dal/UsuarioDao.kt
package com.example.bibliotecakotlin.data.dal

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import com.example.bibliotecakotlin.data.local.AppDatabase
import com.example.bibliotecakotlin.data.model.Usuario

internal class SenhaSalva(val hash: ByteArray, val salt: ByteArray, val usuarioID: Long)

internal class UsuarioDao(context: Context) {
    private val helper = AppDatabase(context)

        fun cadastrar(nome: String, email: String, senha: ByteArray, salt: ByteArray): Long {
        val v = ContentValues().apply {
            put("nome", nome)
            put("email", email.trim().lowercase())
            put("senha", senha)
            put("salt", salt)
        }
        try {
            return helper.writableDatabase.insertOrThrow("usuarios", null, v)
        } catch (e: SQLiteConstraintException) {
            throw IllegalStateException("E-mail já cadastrado")
        }
    }

    fun buscarSenha(email: String): SenhaSalva? {
        helper.readableDatabase.rawQuery(
            "SELECT id, senha, salt FROM usuarios WHERE email = ?",
            arrayOf(email.trim().lowercase())
        ).use { c ->
            if (!c.moveToFirst()) return null
            return SenhaSalva(c.getBlob(1), c.getBlob(2), c.getLong(0))
        }
    }

    fun listar(): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        helper.readableDatabase.rawQuery(
            "SELECT id, nome, email FROM usuarios ORDER BY nome", null
        ).use { c ->
            while (c.moveToNext()) lista.add(Usuario(c.getLong(0), c.getString(1), c.getString(2)))
        }
        return lista
    }

    fun buscarPorId(id: Long): Usuario? {
        helper.readableDatabase.rawQuery(
            "SELECT id, nome, email FROM usuarios WHERE id = ?", arrayOf(id.toString())
        ).use { c ->
            if (!c.moveToFirst()) return null
            return Usuario(c.getLong(0), c.getString(1), c.getString(2))
        }
    }

    fun alterar(id: Long, nome: String, hash: ByteArray? = null, salt: ByteArray? = null) {
        val v = ContentValues().apply {
            put("nome", nome)
            if (hash != null && salt != null) {
                put("senha", hash)
                put("salt", salt)
            }
        }
        helper.writableDatabase.update("usuarios", v, "id = ?", arrayOf(id.toString()))
    }

    fun excluir(id: Long, usuarioAtualId: Long) {
        check(id != usuarioAtualId) { "Você não pode excluir o usuário logado" }
        helper.writableDatabase.delete("usuarios", "id = ?", arrayOf(id.toString()))
    }
}