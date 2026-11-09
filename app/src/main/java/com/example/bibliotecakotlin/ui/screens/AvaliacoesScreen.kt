package com.example.bibliotecakotlin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bibliotecakotlin.data.model.Avaliacao
import com.example.bibliotecakotlin.ui.components.CardBranco
import com.example.bibliotecakotlin.ui.components.HeaderAzul
import com.example.bibliotecakotlin.ui.theme.Background
import com.example.bibliotecakotlin.ui.theme.Orange
import com.example.bibliotecakotlin.ui.theme.Primary
import com.example.bibliotecakotlin.ui.theme.PrimaryLight
import com.example.bibliotecakotlin.ui.theme.Red
import com.example.bibliotecakotlin.ui.theme.TextDark
import com.example.bibliotecakotlin.ui.theme.TextGray
import com.example.bibliotecakotlin.ui.theme.TextLight
import com.example.bibliotecakotlin.ui.viewmodel.AvaliacaoViewModel

@Composable
fun AvaliacoesScreen(
    onEditar: (Long) -> Unit,
    model: AvaliacaoViewModel = viewModel()
) {
    var excluir by remember { mutableStateOf<Avaliacao?>(null) }

    LaunchedEffect(Unit) { model.listar() }

    excluir?.let { avaliacao ->
        AlertDialog(
            onDismissRequest = { excluir = null },
            title = { Text("Excluir avaliação") },
            text = { Text("Deseja excluir sua avaliação de \"${avaliacao.livroTitulo}\"?") },
            confirmButton = {
                TextButton(onClick = { model.excluir(avaliacao.id); excluir = null }) {
                    Text("Excluir", color = Red)
                }
            },
            dismissButton = { TextButton(onClick = { excluir = null }) { Text("Cancelar") } }
        )
    }

    Column(Modifier.fillMaxSize().background(Background)) {
        HeaderAzul("Sua opinião", "Minhas Avaliações", mostrarAvatar = true)

        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            model.mensagem?.let { mensagem ->
                item { Text(mensagem, color = Red, style = MaterialTheme.typography.bodyMedium) }
            }
            if (model.avaliacoes.isEmpty()) {
                item {
                    Text(
                        if (model.carregando) "Carregando..." else "Você ainda não avaliou nenhum livro.",
                        color = TextGray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            items(model.avaliacoes, key = { it.id }) { avaliacao ->
                CardBranco {
                    Column(Modifier.padding(20.dp)) {
                        Text(avaliacao.livroTitulo, style = MaterialTheme.typography.titleMedium, color = TextDark)
                        Text(
                            "★".repeat(avaliacao.nota) + "☆".repeat(5 - avaliacao.nota),
                            color = Orange,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(avaliacao.comentario, color = TextDark, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        Text("Avaliado em ${avaliacao.criadoEm}", color = TextLight, style = MaterialTheme.typography.bodySmall)
                    }
                    HorizontalDivider(color = PrimaryLight, modifier = Modifier.padding(horizontal = 20.dp))
                    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)) {
                        TextButton(onClick = { onEditar(avaliacao.id) }) { Text("Editar", color = Primary) }
                        TextButton(onClick = { excluir = avaliacao }) { Text("Excluir", color = Red) }
                    }
                }
            }
        }
    }
}
