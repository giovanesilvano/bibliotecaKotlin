package com.example.bibliotecakotlin.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bibliotecakotlin.ui.components.*
import com.example.bibliotecakotlin.ui.theme.*
import com.example.bibliotecakotlin.ui.viewmodel.CatalogoViewModel

@Composable
fun LivroFormScreen(
    livroId: Long?,
    onConcluir: () -> Unit,
    vm: CatalogoViewModel = viewModel()
) {
    val context = LocalContext.current

    var titulo by remember { mutableStateOf("") }
    var autor by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    var edicao by remember { mutableStateOf("") }
    var estante by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("1") }
    var capaCor by remember { mutableStateOf(Color(0xFF1E88E5)) }

    LaunchedEffect(livroId) {
        if (livroId != null) {
            vm.buscarPorId(livroId)
        } else {
            vm.limparLivro()
        }
    }

    LaunchedEffect(vm.livro) {
        vm.livro?.let { l ->
            titulo = l.titulo
            autor = l.autor
            categoria = l.categoria
            isbn = l.isbn
            edicao = l.edicao
            estante = l.estante
            quantidade = l.quantidade.toString()
            capaCor = l.capaCor
        }
    }

    LaunchedEffect(vm.salvo) {
        if (vm.salvo) {
            vm.consumirSalvo()
            Toast.makeText(context, "Livro salvo.", Toast.LENGTH_SHORT).show()
            onConcluir()
        }
    }

    LaunchedEffect(vm.mensagem) {
        vm.mensagem?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            vm.consumirMensagem()
        }
    }

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
            OutlinedTextField(
                value = quantidade,
                onValueChange = { valor -> quantidade = valor.filter { it.isDigit() } },
                label = { Text("Quantidade") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    if (titulo.isBlank() || autor.isBlank() || categoria.isBlank()) {
                        Toast.makeText(context, "Informe título, autor e categoria.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val qtd = quantidade.toIntOrNull()?.takeIf { it > 0 } ?: 1
                    vm.salvar(
                        id = livroId ?: 0L,
                        titulo = titulo,
                        autor = autor,
                        categoria = categoria,
                        isbn = isbn,
                        edicao = edicao,
                        estante = estante,
                        quantidade = qtd,
                        capaCor = capaCor
                    )
                },
                enabled = !vm.carregando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (livroId == null) "Cadastrar" else "Salvar alterações")
            }

            TextButton(onClick = onConcluir, modifier = Modifier.fillMaxWidth()) {
                Text("Cancelar")
            }
        }
    }
}