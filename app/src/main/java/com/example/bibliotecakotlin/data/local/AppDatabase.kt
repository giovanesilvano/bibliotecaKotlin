// data/local/AppDatabase.kt
package com.example.bibliotecakotlin.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File

internal class AppDatabase(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    File(context.noBackupFilesDir, "biblioteca.db").absolutePath,
    null,
    4
) {
    override fun onConfigure(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE usuarios(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL CHECK(length(nome) BETWEEN 3 AND 100),
                email TEXT NOT NULL UNIQUE CHECK(length(email) BETWEEN 3 AND 254),
                senha BLOB NOT NULL,
                salt BLOB NOT NULL,
                cep TEXT NOT NULL DEFAULT '',
                rua TEXT NOT NULL DEFAULT '',
                numero TEXT NOT NULL DEFAULT '',
                complemento TEXT NOT NULL DEFAULT '',
                bairro TEXT NOT NULL DEFAULT '',
                cidade TEXT NOT NULL DEFAULT '',
                uf TEXT NOT NULL DEFAULT ''
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE livros(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                titulo TEXT NOT NULL,
                autor TEXT NOT NULL,
                categoria TEXT NOT NULL,
                capa_texto TEXT NOT NULL,
                isbn TEXT NOT NULL,
                edicao TEXT NOT NULL,
                estante TEXT NOT NULL,
                quantidade INTEGER NOT NULL DEFAULT 1,
                capa BLOB
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE emprestimos(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario_id INTEGER NOT NULL,
                livro_id INTEGER NOT NULL,
                emprestado_em INTEGER NOT NULL,
                devolver_ate INTEGER NOT NULL,
                devolvido_em INTEGER,
                FOREIGN KEY(usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
                FOREIGN KEY(livro_id) REFERENCES livros(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE avaliacoes(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario_id INTEGER NOT NULL,
                livro_id INTEGER NOT NULL,
                nota INTEGER NOT NULL CHECK(nota BETWEEN 1 AND 5),
                comentario TEXT NOT NULL,
                criado_em INTEGER NOT NULL,
                UNIQUE(usuario_id, livro_id),
                FOREIGN KEY(usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
                FOREIGN KEY(livro_id) REFERENCES livros(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        // Os livros de exemplo são inseridos pelo LivroDao (semearSeNecessario).
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS avaliacoes")
        db.execSQL("DROP TABLE IF EXISTS emprestimos")
        db.execSQL("DROP TABLE IF EXISTS livros")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }
}