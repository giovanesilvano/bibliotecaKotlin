package com.example.bibliotecakotlin.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bibliotecakotlin.data.model.Livro
import com.example.bibliotecakotlin.ui.components.*
import com.example.bibliotecakotlin.ui.theme.*
import com.example.bibliotecakotlin.ui.viewmodel.CatalogoViewModel

@Composable
fun CatalogoScreen(
    onNovoLivro: () -> Unit,
    onEditarLivro: (Long) -> Unit,
    vm: CatalogoViewModel = viewModel()
) {
    val context = LocalContext.current
    var busca by remember { mutableStateOf("") }
    var excluir by remember { mutableStateOf<Livro?>(null) }

    LaunchedEffect(Unit) { vm.listar() }
    LaunchedEffect(vm.mensagem) {
        vm.mensagem?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            vm.consumirMensagem()
        }
    }

    val termo = busca.trim()
    val filtrados = vm.livros.filter {
        termo.isBlank() ||
                it.titulo.contains(termo, ignoreCase = true) ||
                it.autor.contains(termo, ignoreCase = true) ||
                it.categoria.contains(termo, ignoreCase = true)
    }

    excluir?.let { l ->
        AlertDialog(
            onDismissRequest = { excluir = null },
            title = { Text("Excluir livro") },
            text = { Text("Deseja excluir \"${l.titulo}\"?") },
            confirmButton = {
                TextButton(onClick = { vm.excluir(l.id); excluir = null }) { Text("Excluir", color = Red) }
            },
            dismissButton = { TextButton(onClick = { excluir = null }) { Text("Cancelar") } }
        )
    }

    Column(Modifier.fillMaxSize().background(Background)) {
        HeaderAzul("Acervo", "Catálogo", mostrarAvatar = true)

        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { CampoBusca(busca, { busca = it }, "Título, autor ou categoria") }
            item { BotaoPrimario("Cadastrar livro", onClick = onNovoLivro) }

            if (filtrados.isEmpty()) {
                item {
                    Text(
                        if (vm.carregando) "Carregando..." else "Nenhum livro encontrado.",
                        color = TextGray, fontSize = 14.sp
                    )
                }
            }

            items(filtrados, key = { it.id }) { l ->
                CardBranco {
                    Row(Modifier.padding(20.dp)) {
                        CapaLivro(l.capaTexto, l.capaCor, largura = 90, altura = 130)
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    l.titulo, fontSize = 17.sp, fontWeight = FontWeight.Bold,
                                    color = TextDark, modifier = Modifier.weight(1f)
                                )
                                if (l.disponivel) Badge("Disponível", Green, GreenLight)
                                else Badge("Emprestado", Red, RedLight)
                            }
                            Text(l.autor, color = TextGray, fontSize = 14.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(l.categoria, color = TextGray, fontSize = 13.sp)
                            Text(l.estante, color = TextGray, fontSize = 13.sp)
                        }
                    }
                    HorizontalDivider(color = PrimaryLight, modifier = Modifier.padding(horizontal = 20.dp))
                    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)) {
                        TextButton(onClick = { onEditarLivro(l.id) }) { Text("Editar", color = Primary) }
                        TextButton(onClick = { excluir = l }) { Text("Excluir", color = Red) }
                    }
                }
            }
        }
    }
}