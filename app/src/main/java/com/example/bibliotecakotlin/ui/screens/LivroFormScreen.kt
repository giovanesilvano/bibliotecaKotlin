package com.example.bibliotecakotlin.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bibliotecakotlin.ui.components.*
import com.example.bibliotecakotlin.ui.theme.*
import com.example.bibliotecakotlin.ui.viewmodel.CatalogoViewModel

private val CoresCapa = listOf(
    Color(0xFF1E3A8A), Color(0xFF7C2D12), Color(0xFF166534),
    Color(0xFF6B21A8), Color(0xFF0F766E), Color(0xFFB91C1C)
)

@Composable
fun LivroFormScreen(
    livroId: Long?,
    onConcluir: () -> Unit,
    vm: CatalogoViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(livroId) {
        if (livroId != null) vm.buscarPorId(livroId) else vm.limparLivro()
    }
    LaunchedEffect(vm.salvo) {
        if (vm.salvo) {
            Toast.makeText(context, "Livro salvo.", Toast.LENGTH_SHORT).show()
            vm.consumirSalvo()
            onConcluir()
        }
    }
    LaunchedEffect(vm.mensagem) {
        vm.mensagem?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            vm.consumirMensagem()
        }
    }

    val atual = vm.livro
    var titulo by remember(atual) { mutableStateOf(atual?.titulo ?: "") }
    var autor by remember(atual) { mutableStateOf(atual?.autor ?: "") }
    var categoria by remember(atual) { mutableStateOf(atual?.categoria ?: "") }
    var isbn by remember(atual) { mutableStateOf(atual?.isbn ?: "") }
    var edicao by remember(atual) { mutableStateOf(atual?.edicao ?: "") }
    var estante by remember(atual) { mutableStateOf(atual?.estante ?: "") }
    var cor by remember(atual) { mutableStateOf(atual?.capaCor ?: CoresCapa.first()) }

    Column(Modifier.fillMaxSize().background(Background)) {
        HeaderAzul(
            if (livroId == null) "Novo livro" else "Editar livro",
            if (livroId == null) "Cadastrar Livro" else "Editar Livro"
        )

        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Campo("Título", titulo) { titulo = it }
            Campo("Autor", autor) { autor = it }
            Campo("Categoria", categoria) { categoria = it }
            Campo("ISBN", isbn) { isbn = it }
            Campo("Edição", edicao) { edicao = it }
            Campo("Estante", estante) { estante = it }

            Text("Cor da capa", color = TextGray)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CoresCapa.forEach { c ->
                    Box(
                        Modifier.size(40.dp).clip(CircleShape).background(c)
                            .border(if (c == cor) 3.dp else 0.dp, if (c == cor) Primary else Color.Transparent, CircleShape)
                            .clickable { cor = c }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            BotaoPrimario(
                if (vm.carregando) "Salvando..." else "Salvar",
                onClick = {
                    if (!vm.carregando) {
                        vm.salvar(livroId ?: 0L, titulo, autor, categoria, isbn, edicao, estante, cor)
                    }
                }
            )
            TextButton(onClick = onConcluir, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
        }
    }
}

@Composable
private fun Campo(rotulo: String, valor: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        label = { Text(rotulo) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}