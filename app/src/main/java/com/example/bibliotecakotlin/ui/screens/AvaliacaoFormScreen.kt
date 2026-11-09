package com.example.bibliotecakotlin.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bibliotecakotlin.ui.components.BotaoPrimario
import com.example.bibliotecakotlin.ui.components.HeaderAzul
import com.example.bibliotecakotlin.ui.theme.Background
import com.example.bibliotecakotlin.ui.theme.Orange
import com.example.bibliotecakotlin.ui.theme.TextDark
import com.example.bibliotecakotlin.ui.theme.TextGray
import com.example.bibliotecakotlin.ui.viewmodel.AvaliacaoViewModel

@Composable
fun AvaliacaoFormScreen(
    livroId: Long?,
    avaliacaoId: Long?,
    onConcluir: () -> Unit,
    model: AvaliacaoViewModel = viewModel()
) {
    val contexto = LocalContext.current

    LaunchedEffect(livroId, avaliacaoId) {
        if (avaliacaoId != null) {
            model.buscarPorId(avaliacaoId)
        } else if (livroId != null) {
            model.novaPara(livroId)
        }
    }

    LaunchedEffect(model.salvo) {
        if (model.salvo) {
            model.consumirSalvo()
            Toast.makeText(contexto, "Avaliação salva.", Toast.LENGTH_SHORT).show()
            onConcluir()
        }
    }

    LaunchedEffect(model.mensagem) {
        model.mensagem?.let {
            Toast.makeText(contexto, it, Toast.LENGTH_LONG).show()
            model.consumirMensagem()
        }
    }

    Column(Modifier.fillMaxSize().background(Background)) {
        HeaderAzul("Sua opinião", if (avaliacaoId == null) "Avaliar Livro" else "Editar Avaliação")

        Column(
            Modifier.imePadding().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(model.livroTitulo, style = MaterialTheme.typography.titleLarge, color = TextDark)

            Text("Nota", color = TextGray, style = MaterialTheme.typography.bodyMedium)
            Row {
                (1..5).forEach { valor ->
                    TextButton(onClick = { model.mudarNota(valor) }) {
                        Text(if (valor <= model.nota) "★" else "☆", color = Orange, style = MaterialTheme.typography.displaySmall)
                    }
                }
            }

            OutlinedTextField(
                value = model.comentario,
                onValueChange = model::mudarComentario,
                label = { Text("Comentário") },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            BotaoPrimario(
                when {
                    model.carregando -> "Salvando..."
                    avaliacaoId == null -> "Salvar avaliação"
                    else -> "Salvar alterações"
                },
                onClick = model::salvar
            )
            TextButton(onClick = onConcluir, modifier = Modifier.fillMaxWidth()) {
                Text("Cancelar")
            }
        }
    }
}
