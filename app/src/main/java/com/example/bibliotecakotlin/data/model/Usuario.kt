// data/model/Usuario.kt
package com.example.bibliotecakotlin.data.model

data class Usuario(
    val id: Long,
    val nome: String,
    val email: String,
    val cep: String = "",
    val rua: String = "",
    val numero: String = "",
    val complemento: String = "",
    val bairro: String = "",
    val cidade: String = "",
    val uf: String = ""
)