package com.example.bibliotecakotlin.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bibliotecakotlin.data.dal.LivroDao
import com.example.bibliotecakotlin.data.model.Livro
import com.example.bibliotecakotlin.ui.components.*
import com.example.bibliotecakotlin.ui.theme.*

@Composable
fun LivroFormScreen(
    livroId: Long?,
    onConcluir: () -> Unit
) {
    val context = LocalContext.current
    val dao = remember { LivroDao(context) }
    val existente = remember(livroId) { livroId?.let { dao.buscarPorId(it) } }

    var titulo by remember { mutableStateOf(existente?.titulo ?: "") }
    var autor by remember { mutableStateOf(existente?.autor ?: "") }
    var categoria by remember { mutableStateOf(existente?.categoria ?: "") }
    var isbn by remember { mutableStateOf(existente?.isbn ?: "") }
    var edicao by remember { mutableStateOf(existente?.edicao ?: "") }
    var estante by remember { mutableStateOf(existente?.estante ?: "") }

    Column(Modifier.fillMaxSize().background(Background)) {
        HeaderAzul(
            if (livroId == null) "Novo livro" else "Editar livro",
            if (livroId == null) "Cadastrar Livro" else "Editar Livro"
        )

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Dados do livro",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            OutlinedTextField(
                value = titulo, onValueChange = { titulo = it },
                label = { Text("Título") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = autor, onValueChange = { autor = it },
                label = { Text("Autor") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = categoria, onValueChange = { categoria = it },
                label = { Text("Categoria") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = isbn, onValueChange = { isbn = it },
                label = { Text("ISBN") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = edicao, onValueChange = { edicao = it },
                label = { Text("Edição") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = estante, onValueChange = { estante = it },
                label = { Text("Estante") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    if (titulo.isBlank() || autor.isBlank()) {
                        Toast.makeText(context, "Informe título e autor.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val sigla = titulo.trim().split(" ")
                        .filter { it.isNotEmpty() }
                        .take(2)
                        .joinToString("") { it.first().uppercase() }

                    val livro = Livro(
                        id = existente?.id ?: 0L,
                        titulo = titulo.trim(),
                        autor = autor.trim(),
                        categoria = categoria.trim(),
                        capaTexto = sigla,
                        capaCor = existente?.capaCor ?: Color(0xFF1E88E5),
                        isbn = isbn.trim(),
                        edicao = edicao.trim(),
                        estante = estante.trim()
                    )
                    try {
                        if (existente == null) dao.inserir(livro) else dao.atualizar(livro)
                        Toast.makeText(context, "Livro salvo.", Toast.LENGTH_SHORT).show()
                        onConcluir()
                    } catch (e: Exception) {
                        Toast.makeText(context, e.message ?: "Erro ao salvar.", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (existente == null) "Cadastrar" else "Salvar alterações")
            }

            TextButton(onClick = onConcluir, modifier = Modifier.fillMaxWidth()) {
                Text("Cancelar")
            }
        }
    }
}