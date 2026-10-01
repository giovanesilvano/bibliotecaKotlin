package com.example.bibliotecakotlin.auth

object AuthManager {

    data class Usuario(
        val nome: String,
        val email: String,
        val senha: String
    )

    private val usuarios = mutableListOf<Usuario>()

    fun cadastrar(
        nome: String,
        email: String,
        senha: String
    ): Boolean {
        val emailJaCadastrado = usuarios.any {
            it.email.equals(email.trim(), ignoreCase = true)
        }

        if (emailJaCadastrado) {
            return false
        }

        usuarios.add(
            Usuario(
                nome = nome.trim(),
                email = email.trim(),
                senha = senha
            )
        )

        return true
    }

    fun login(
        email: String,
        senha: String
    ): Boolean {
        return usuarios.any {
            it.email.equals(email.trim(), ignoreCase = true) &&
                    it.senha == senha
        }
    }
}