package com.example.bibliotecakotlin.data.dal

import android.content.ContentValues
import android.content.Context
import com.example.bibliotecakotlin.auth.AuthManager
import com.example.bibliotecakotlin.data.local.AppDatabase
import com.example.bibliotecakotlin.data.model.Usuario

internal class UsuarioAdminDao(context: Context) {
    private val helper = AppDatabase(context.applicationContext)

    fun listar(): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        helper.readableDatabase.rawQuery(
            "SELECT id, nome, email FROM usuarios ORDER BY nome COLLATE NOCASE", null
        ).use { c ->
            while (c.moveToNext()) lista += Usuario(c.getLong(0), c.getString(1), c.getString(2))
        }
        return lista
    }

    fun buscarPorId(id: Long): Usuario? =
        helper.readableDatabase.rawQuery(
            "SELECT id, nome, email FROM usuarios WHERE id = ?", arrayOf(id.toString())
        ).use { c ->
            if (c.moveToFirst()) Usuario(c.getLong(0), c.getString(1), c.getString(2)) else null
        }

    fun atualizar(id: Long, nome: String, novaSenha: String?): Int {
        val v = ContentValues().apply {
            put("nome", nome)
            if (!novaSenha.isNullOrEmpty()) {
                val salt = AuthManager.criarSalt()
                put("salt", salt)
                put("senha", AuthManager.gerarHash(novaSenha, salt))
            }
        }
        return helper.writableDatabase.update("usuarios", v, "id = ?", arrayOf(id.toString()))
    }

    fun excluir(id: Long): Int =
        helper.writableDatabase.delete("usuarios", "id = ?", arrayOf(id.toString()))
}