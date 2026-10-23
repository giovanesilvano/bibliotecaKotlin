// auth/AuthManager.kt
package com.example.bibliotecakotlin.auth

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object AuthManager {
    private const val ITERACOES = 120_000
    private const val TAMANHO_BITS = 256

    fun criarSalt(): ByteArray = ByteArray(16).also { SecureRandom().nextBytes(it) }

    fun gerarHash(senha: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(senha.toCharArray(), salt, ITERACOES, TAMANHO_BITS)
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
    }

    fun validarSenha(senha: String, salt: ByteArray, senhaDB: ByteArray): Boolean =
        java.security.MessageDigest.isEqual(gerarHash(senha, salt), senhaDB)
}