// validation/CadastroValidator.kt  (versão minha: regras simples, ajuste às suas)
package com.example.bibliotecakotlin.validation

import android.util.Patterns

object CadastroValidator {
    /** Retorna a mensagem de erro ou null se válido. */
    fun validar(nome: String, email: String, senha: String?, confirmar: String? = senha): String? {
        if (nome.isBlank()) return "Informe o nome"
        if (email.isNotBlank() || senha != null) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) return "E-mail inválido"
        }
        if (senha != null) {
            if (senha.length < 6) return "A senha deve ter ao menos 6 caracteres"
            if (senha != confirmar) return "As senhas não conferem"
        }
        return null
    }
}