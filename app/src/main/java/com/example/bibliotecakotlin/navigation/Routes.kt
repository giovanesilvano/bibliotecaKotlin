package com.example.bibliotecakotlin.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Login

@Serializable
data object Cadastro

@Serializable
data object Catalogo

@Serializable
data object Emprestar

@Serializable
data object Devolver

@Serializable
data object Emprestimos

@Serializable
data object Usuarios

@Serializable
data class UsuarioEditar(val id: Long)

@Serializable
data object LivroNovo

@Serializable
data class LivroEditar(val id: Long)