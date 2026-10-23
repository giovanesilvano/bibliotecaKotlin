package com.example.bibliotecakotlin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bibliotecakotlin.ui.components.*
import com.example.bibliotecakotlin.ui.theme.*
import com.example.bibliotecakotlin.ui.viewmodel.CatalogoViewModel
import com.example.bibliotecakotlin.ui.viewmodel.EmprestimoViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun EmprestarScreen(
    onConfirmar: () -> Unit,
    catalogoVm: CatalogoViewModel = viewModel(),
    emprestimoVm: EmprestimoViewModel = viewModel()
) {
    var busca by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { catalogoVm.listar() }
    LaunchedEffect(emprestimoVm.concluido) {
        if (emprestimoVm.concluido) {
            emprestimoVm.consumirResultado()
            onConfirmar()
        }
    }

    val termo = busca.trim()
    val livro = catalogoVm.livros.firstOrNull {
        termo.isNotBlank() && (
                it.titulo.contains(termo, ignoreCase = true) ||
                        it.isbn.contains(termo, ignoreCase = true) ||
                        it.autor.contains(termo, ignoreCase = true)
                )
    }

    // Prazo de devolução: hoje + 10 dias
    val prazoTexto = remember {
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 10) }
        SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("pt", "BR")).format(cal.time)
    }

    Column(Modifier.fillMaxSize().background(Background)) {
        HeaderAzul("Novo empréstimo", "Emprestar Livro")

        Column(Modifier.verticalScroll(rememberScrollState()).padding(20.dp)) {
            Text("Encontre o livro", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(Modifier.height(6.dp))
            Text("Busque pelo título ou escaneie o código de barras da obra.", color = TextGray, fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))
            CampoBusca(busca, { busca = it }, "Título ou código", trailing = {
                Icon(Icons.Outlined.QrCodeScanner, contentDescription = "Escanear", tint = Primary)
            })

            emprestimoVm.mensagem?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = Red, fontSize = 14.sp)
            }

            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Livro selecionado", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                    color = TextDark, modifier = Modifier.weight(1f)
                )
                if (livro != null) {
                    Text(
                        if (livro.disponivel) "Encontrado" else "Indisponível",
                        color = if (livro.disponivel) Green else Red,
                        fontWeight = FontWeight.SemiBold, fontSize = 14.sp
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            if (livro == null) {
                Text(
                    when {
                        catalogoVm.carregando -> "Carregando..."
                        termo.isBlank() -> "Digite o título, autor ou ISBN para buscar."
                        else -> "Nenhum livro encontrado."
                    },
                    color = TextGray, fontSize = 14.sp
                )
            } else {
                CardBranco {
                    Row(Modifier.padding(20.dp)) {
                        CapaLivro(livro.capaTexto, livro.capaCor, largura = 120, altura = 170, autor = livro.autor)
                        Spacer(Modifier.width(20.dp))
                        Column {
                            if (livro.disponivel) Badge("● Disponível (${livro.quantidadeDisponivel}/${livro.quantidade})", Green, GreenLight)
                            else Badge("● Esgotado", Red, RedLight)
                            Spacer(Modifier.height(16.dp))
                            Text(livro.titulo, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Text(livro.autor, color = TextGray, fontSize = 16.sp)
                            Spacer(Modifier.height(16.dp))
                            Text(livro.categoria, color = TextGray, fontSize = 14.sp)
                            Spacer(Modifier.height(6.dp))
                            Text(livro.estante, color = TextGray, fontSize = 14.sp)
                        }
                    }
                    HorizontalDivider(color = PrimaryLight)
                    Row(Modifier.fillMaxWidth().background(Background).padding(vertical = 16.dp)) {
                        InfoColuna("ISBN", livro.isbn, Modifier.weight(1f))
                        VerticalDivider(Modifier.height(40.dp), color = Border)
                        InfoColuna("EDIÇÃO", livro.edicao, Modifier.weight(1f))
                        VerticalDivider(Modifier.height(40.dp), color = Border)
                        InfoColuna("DISPONÍVEIS", "${livro.quantidadeDisponivel}/${livro.quantidade}", Modifier.weight(1f))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Row(
                Modifier.fillMaxWidth().background(PrimaryLight, RoundedCornerShape(16.dp)).padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(56.dp).clip(CircleShape).background(Color.White))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Prazo de empréstimo", color = Primary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(buildAnnotatedString {
                        append("A devolução está prevista para ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(prazoTexto) }
                        append(". O prazo é de 10 dias.")
                    }, color = Primary, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(32.dp))
            BotaoPrimario(
                "Confirmar Empréstimo",
                onClick = {
                    if (livro != null && livro.disponivel && !emprestimoVm.carregando) {
                        emprestimoVm.emprestar(livro.id)
                    }
                }
            )
            Spacer(Modifier.height(12.dp))
            TextoCentralizadoCinza("Confirme apenas se o livro estiver em sua posse.")
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InfoColuna(rotulo: String, valor: String, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(rotulo, color = TextLight, fontSize = 12.sp, letterSpacing = 1.sp)
        Spacer(Modifier.height(4.dp))
        Text(valor, color = TextDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}