package com.example.bibliotecakotlin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exemplo.biblioteca.data.Emprestimo
import com.exemplo.biblioteca.data.MockData
import com.exemplo.biblioteca.data.StatusEmprestimo
import com.exemplo.biblioteca.ui.components.*
import com.exemplo.biblioteca.ui.theme.*

@Composable
fun EmprestimosScreen() {
    Column(Modifier.fillMaxSize().background(Background)) {
        HeaderAzul("Sua biblioteca", "Meus Empréstimos", mostrarAvatar = true)

        LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    CardResumo("3", "Empréstimos ativos", Primary, PrimaryLight, Modifier.weight(1f))
                    CardResumo("1", "Prazo nesta semana", Orange, OrangeLight, Modifier.weight(1f))
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Livros com você", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.weight(1f))
                    Text("Ordenar", color = Primary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
            items(MockData.emprestimos) { CardEmprestimo(it) }
        }
    }
}

@Composable
private fun CardResumo(numero: String, rotulo: String, cor: Color, fundo: Color, modifier: Modifier) {
    Column(modifier.background(fundo, RoundedCornerShape(16.dp)).padding(20.dp)) {
        Box(Modifier.size(52.dp).clip(CircleShape).background(Color.White))
        Spacer(Modifier.height(16.dp))
        Text(numero, color = cor, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text(rotulo, color = cor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun CardEmprestimo(e: Emprestimo) {
    val atrasado = e.status == StatusEmprestimo.ATRASADO
    CardBranco {
        Row(Modifier.padding(20.dp)) {
            CapaLivro(e.livro.capaTexto, e.livro.capaCor, largura = 90, altura = 130)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(e.livro.titulo, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.weight(1f))
                    if (atrasado) Badge("Atrasado", Red, RedLight) else Badge("Em dia", Green, GreenLight)
                }
                Text(e.livro.autor, color = TextGray, fontSize = 14.sp)
                Spacer(Modifier.height(16.dp))
                Row {
                    Column(Modifier.weight(1f)) {
                        Text("Emprestado em", color = TextLight, fontSize = 12.sp)
                        Text(e.emprestadoEm, color = TextDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                    Column(Modifier.weight(1f)) {
                        Text(if (atrasado) "Devolução prevista" else "Devolver até", color = TextLight, fontSize = 12.sp)
                        Text(e.devolverAte, color = if (atrasado) Red else TextDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }
            }
        }
        HorizontalDivider(color = PrimaryLight, modifier = Modifier.padding(horizontal = 20.dp))
        Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(e.infoPrazo, color = if (atrasado) Red else Green, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Text(if (atrasado) "Ver detalhes" else "Renovar", color = Primary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}