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
            """SELECT id, nome, email, cep, rua, numero, complemento, bairro, cidade, uf
               FROM usuarios WHERE id = ?""",
            arrayOf(id.toString())
        ).use { c ->
            if (c.moveToFirst()) Usuario(
                id = c.getLong(0),
                nome = c.getString(1),
                email = c.getString(2),
                cep = c.getString(3),
                rua = c.getString(4),
                numero = c.getString(5),
                complemento = c.getString(6),
                bairro = c.getString(7),
                cidade = c.getString(8),
                uf = c.getString(9)
            ) else null
        }

    fun atualizar(
        id: Long, nome: String, novaSenha: String?,
        cep: String, rua: String, numero: String, complemento: String,
        bairro: String, cidade: String, uf: String
    ): Int {
        val v = ContentValues().apply {
            put("nome", nome)
            put("cep", cep)
            put("rua", rua)
            put("numero", numero)
            put("complemento", complemento)
            put("bairro", bairro)
            put("cidade", cidade)
            put("uf", uf)
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