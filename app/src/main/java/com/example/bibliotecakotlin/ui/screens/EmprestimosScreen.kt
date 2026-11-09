package com.example.bibliotecakotlin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bibliotecakotlin.data.model.Emprestimo
import com.example.bibliotecakotlin.data.model.StatusEmprestimo
import com.example.bibliotecakotlin.ui.components.*
import com.example.bibliotecakotlin.ui.theme.*
import com.example.bibliotecakotlin.ui.viewmodel.EmprestimoViewModel

@Composable
fun EmprestimosScreen(vm: EmprestimoViewModel = viewModel()) {
    var cancelar by remember { mutableStateOf<Emprestimo?>(null) }

    LaunchedEffect(Unit) { vm.listar() }

    val ativos = vm.emprestimos.filter { it.status != StatusEmprestimo.DEVOLVIDO }
    val atrasados = ativos.count { it.status == StatusEmprestimo.ATRASADO }

    cancelar?.let { e ->
        AlertDialog(
            onDismissRequest = { cancelar = null },
            title = { Text("Cancelar empréstimo") },
            text = { Text("Deseja cancelar o empréstimo de \"${e.livro.titulo}\"?") },
            confirmButton = {
                TextButton(onClick = { vm.cancelar(e.id); cancelar = null }) { Text("Cancelar empréstimo", color = Red) }
            },
            dismissButton = { TextButton(onClick = { cancelar = null }) { Text("Voltar") } }
        )
    }

    Column(Modifier.fillMaxSize().background(Background)) {
        HeaderAzul("Sua biblioteca", "Meus Empréstimos", mostrarAvatar = true)

        LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    CardResumo(ativos.size.toString(), "Empréstimos ativos", Primary, PrimaryLight, Modifier.weight(1f))
                    CardResumo(atrasados.toString(), "Em atraso", Orange, OrangeLight, Modifier.weight(1f))
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Livros com você", style = MaterialTheme.typography.titleLarge, color = TextDark, modifier = Modifier.weight(1f))
                    Text("Ordenar", color = Primary, style = MaterialTheme.typography.labelLarge)
                }
            }
            vm.mensagem?.let { msg ->
                item { Text(msg, color = Red, style = MaterialTheme.typography.bodyMedium) }
            }
            if (ativos.isEmpty()) {
                item {
                    Text(
                        if (vm.carregando) "Carregando..." else "Você não tem empréstimos ativos.",
                        color = TextGray, style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            items(ativos, key = { it.id }) { e -> CardEmprestimo(e, onCancelar = { cancelar = e }) }
        }
    }
}

@Composable
private fun CardResumo(numero: String, rotulo: String, cor: Color, fundo: Color, modifier: Modifier) {
    Column(modifier.background(fundo, RoundedCornerShape(16.dp)).padding(20.dp)) {
        Box(Modifier.size(52.dp).clip(CircleShape).background(Color.White))
        Spacer(Modifier.height(16.dp))
        Text(numero, color = cor, style = MaterialTheme.typography.displaySmall)
        Text(rotulo, color = cor, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun CardEmprestimo(e: Emprestimo, onCancelar: () -> Unit) {
    val atrasado = e.status == StatusEmprestimo.ATRASADO
    CardBranco {
        Row(Modifier.padding(20.dp)) {
            CapaLivro(e.livro.capaTexto, e.livro.capaCor, largura = 90, altura = 130)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(e.livro.titulo, style = MaterialTheme.typography.titleMedium, color = TextDark, modifier = Modifier.weight(1f))
                    if (atrasado) Badge("Atrasado", Red, RedLight) else Badge("Em dia", Green, GreenLight)
                }
                Text(e.livro.autor, color = TextGray, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))
                Row {
                    Column(Modifier.weight(1f)) {
                        Text("Emprestado em", color = TextLight, style = MaterialTheme.typography.bodySmall)
                        Text(e.emprestadoEm, color = TextDark, style = MaterialTheme.typography.labelLarge)
                    }
                    Column(Modifier.weight(1f)) {
                        Text(if (atrasado) "Devolução prevista" else "Devolver até", color = TextLight, style = MaterialTheme.typography.bodySmall)
                        Text(e.devolverAte, color = if (atrasado) Red else TextDark, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
        HorizontalDivider(color = PrimaryLight, modifier = Modifier.padding(horizontal = 20.dp))
        Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(e.infoPrazo, color = if (atrasado) Red else Green, style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
            Text(if (atrasado) "Ver detalhes" else "Prazo: 10 dias", color = Primary, style = MaterialTheme.typography.labelLarge)
        }
        HorizontalDivider(color = PrimaryLight, modifier = Modifier.padding(horizontal = 20.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)) {
            TextButton(onClick = onCancelar) { Text("Cancelar", color = Red) }
        }
    }
}