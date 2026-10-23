// auth/SessaoUsuario.kt
package com.example.bibliotecakotlin.auth

object SessaoUsuario {
    var usuarioId: Long? = null
        private set

    fun iniciar(id: Long) { usuarioId = id }
    fun encerrar() { usuarioId = null }
    fun exigirUsuario(): Long = usuarioId ?: throw IllegalStateException("Sessão não iniciada")
}