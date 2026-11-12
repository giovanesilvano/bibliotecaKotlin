package com.example.bibliotecakotlin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bibliotecakotlin.data.model.StatusEmprestimo
import com.example.bibliotecakotlin.ui.components.*
import com.example.bibliotecakotlin.ui.theme.*
import com.example.bibliotecakotlin.ui.viewmodel.EmprestimoViewModel

@Composable
fun DevolverScreen(
    onConfirmar: () -> Unit,
    vm: EmprestimoViewModel = viewModel()
) {
    var busca by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { vm.listar() }
    LaunchedEffect(vm.concluido) {
        if (vm.concluido) {
            vm.consumirResultado()
            onConfirmar()
        }
    }

    val ativos = vm.emprestimos.filter { it.status != StatusEmprestimo.DEVOLVIDO }
    val termo = busca.trim()
    val emprestimo = ativos.firstOrNull {
        termo.isBlank() ||
                it.livro.titulo.contains(termo, ignoreCase = true) ||
                it.livro.isbn.contains(termo, ignoreCase = true) ||
                it.id.toString() == termo
    }

    Column(Modifier.fillMaxSize().background(Background)) {
        HeaderAzul("Finalizar empréstimo", "Devolver Livro")

        Column(Modifier.verticalScroll(rememberScrollState()).padding(20.dp)) {
            Text("Localize o empréstimo", style = MaterialTheme.typography.titleLarge, color = TextDark)
            Spacer(Modifier.height(6.dp))
            Text("Informe o título ou código do livro que deseja devolver.", color = TextGray, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            CampoBusca(busca, { busca = it }, "Título ou código")

            vm.mensagem?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = Red, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(24.dp))

            if (emprestimo == null) {
                Text(
                    if (vm.carregando) "Carregando..." else "Nenhum empréstimo ativo encontrado.",
                    color = TextGray, style = MaterialTheme.typography.bodyMedium
                )
            } else {
                val atrasado = emprestimo.status == StatusEmprestimo.ATRASADO
                val corStatus = if (atrasado) Red else Primary
                val fundoStatus = if (atrasado) RedLight else PrimaryLight
                val livro = emprestimo.livro

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Empréstimo ativo", style = MaterialTheme.typography.titleLarge,
                        color = TextDark, modifier = Modifier.weight(1f)
                    )
                    Badge(if (atrasado) "Atrasado" else "Em dia", corStatus, fundoStatus)
                }
                Spacer(Modifier.height(12.dp))

                CardBranco {
                    Row(Modifier.padding(20.dp)) {
                        CapaLivro(livro.capaTexto, livro.capaCor, largura = 120, altura = 170, autor = livro.autor)
                        Spacer(Modifier.width(20.dp))
                        Column {
                            Text(livro.titulo, style = MaterialTheme.typography.headlineSmall, color = TextDark)
                            Text(livro.autor, color = TextGray, style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.height(20.dp))
                            Text("EMPRESTADO EM", color = TextLight, style = MaterialTheme.typography.labelSmall)
                            Text(
                                emprestimo.emprestadoEm, color = TextDark,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Spacer(Modifier.height(12.dp))
                            Text("DEVOLUÇÃO PREVISTA", color = TextLight, style = MaterialTheme.typography.labelSmall)
                            Text(
                                emprestimo.devolverAte, color = corStatus,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                    Row(
                        Modifier.fillMaxWidth().background(fundoStatus).padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.size(56.dp).clip(CircleShape)
                                .background(if (atrasado) RedSoft else Color.White)
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                emprestimo.infoPrazo, color = corStatus,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                if (atrasado) "Realize a devolução para regularizar sua situação."
                                else "Devolva até a data prevista para evitar atraso.",
                                color = corStatus, style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Column(
                Modifier.fillMaxWidth()
                    .background(PrimaryLight, RoundedCornerShape(16.dp))
                    .border(1.dp, Border, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Text("Antes de confirmar", color = Primary, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Entregue o exemplar no balcão da biblioteca ou deposite-o na caixa de devolução indicada.",
                    color = Primary, style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(32.dp))
            BotaoPrimario(
                "Confirmar Devolução",
                onClick = { emprestimo?.let { vm.devolver(it.id) } }
            )
            Spacer(Modifier.height(12.dp))
            TextoCentralizadoCinza("A confirmação será registrada no seu histórico.")
            Spacer(Modifier.height(16.dp))
        }
    }
}