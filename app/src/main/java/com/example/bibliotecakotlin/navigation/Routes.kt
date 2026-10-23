package com.example.bibliotecakotlin.navigation

import kotlinx.serialization.Serializable

const val USUARIOS = "usuarios"
const val USUARIO_EDITAR = "usuarioEditar/{id}"
fun usuarioEditar(id: Long) = "usuarioEditar/$id"

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