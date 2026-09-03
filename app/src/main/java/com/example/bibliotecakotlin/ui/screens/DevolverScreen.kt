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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exemplo.biblioteca.data.MockData
import com.exemplo.biblioteca.ui.components.*
import com.exemplo.biblioteca.ui.theme.*

@Composable
fun DevolverScreen(onConfirmar: () -> Unit) {
    var busca by remember { mutableStateOf("Sociologia") }
    val livro = MockData.sociologia

    Column(Modifier.fillMaxSize().background(Background)) {
        HeaderAzul("Finalizar empréstimo", "Devolver Livro")

        Column(Modifier.verticalScroll(rememberScrollState()).padding(20.dp)) {
            Text("Localize o empréstimo", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(Modifier.height(6.dp))
            Text("Informe o título ou código do livro que deseja devolver.", color = TextGray, fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))
            CampoBusca(busca, { busca = it }, "Título ou código")

            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Empréstimo ativo", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.weight(1f))
                Badge("Atrasado", Red, RedLight)
            }
            Spacer(Modifier.height(12.dp))

            CardBranco {
                Row(Modifier.padding(20.dp)) {
                    CapaLivro(livro.capaTexto, livro.capaCor, largura = 120, altura = 170, autor = livro.autor)
                    Spacer(Modifier.width(20.dp))
                    Column {
                        Text(livro.titulo, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text(livro.autor, color = TextGray, fontSize = 16.sp)
                        Spacer(Modifier.height(20.dp))
                        Text("EMPRESTADO EM", color = TextLight, fontSize = 11.sp, letterSpacing = 1.sp)
                        Text("22 de julho de 2026", color = TextDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("DEVOLUÇÃO PREVISTA", color = TextLight, fontSize = 11.sp, letterSpacing = 1.sp)
                        Text("19 de agosto de 2026", color = Red, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                Row(
                    Modifier.fillMaxWidth().background(RedLight).padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(56.dp).clip(CircleShape).background(Color(0xFFFFE0E0)))
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("8 dias de atraso", color = Red, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text("Realize a devolução para regularizar sua situação.", color = Red, fontSize = 13.sp)
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
                Text("Antes de confirmar", color = Primary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Entregue o exemplar no balcão da biblioteca ou deposite-o na caixa de devolução indicada.",
                    color = Primary, fontSize = 14.sp
                )
            }

            Spacer(Modifier.height(32.dp))
            BotaoPrimario("Confirmar Devolução", onClick = onConfirmar)
            Spacer(Modifier.height(12.dp))
            TextoCentralizadoCinza("A confirmação será registrada no seu histórico.")
            Spacer(Modifier.height(16.dp))
        }
    }
}