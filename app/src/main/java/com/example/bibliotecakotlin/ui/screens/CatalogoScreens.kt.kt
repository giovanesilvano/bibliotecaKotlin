package com.example.bibliotecakotlin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exemplo.biblioteca.data.Livro
import com.exemplo.biblioteca.data.MockData
import com.exemplo.biblioteca.ui.components.*
import com.exemplo.biblioteca.ui.theme.*

@Composable
fun CatalogoScreen() {
    var busca by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf("Todos") }
    val filtros = listOf("Todos", "Ciências", "Humanas", "TI")

    Column(Modifier.fillMaxSize().background(Background)) {
        HeaderAzul("Biblioteca Universitária", "Catálogo", mostrarAvatar = true)

        LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { CampoBusca(busca, { busca = it }, "Buscar por título, autor ou ISBN") }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    filtros.forEach { f ->
                        FilterChip(
                            selected = filtro == f,
                            onClick = { filtro = f },
                            label = { Text(f, fontWeight = FontWeight.SemiBold) },
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                                labelColor = Primary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true, selected = filtro == f, borderColor = Border
                            )
                        )
                    }
                }
            }

            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Livros em destaque", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.weight(1f))
                    Text("248 títulos", color = TextGray, fontSize = 14.sp)
                }
            }

            items(MockData.catalogo) { livro -> CardLivroCatalogo(livro) }
        }
    }
}

@Composable
private fun CardLivroCatalogo(livro: Livro) {
    CardBranco {
        Row(Modifier.padding(20.dp)) {
            CapaLivro(livro.capaTexto, livro.capaCor, largura = 90, altura = 130)
            Spacer(Modifier.width(20.dp))
            Column(Modifier.weight(1f)) {
                Text(livro.titulo, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(Modifier.height(4.dp))
                Text(livro.autor, color = TextGray, fontSize = 15.sp)
                Spacer(Modifier.height(8.dp))
                Text(livro.categoria, color = TextGray, fontSize = 13.sp)
                Spacer(Modifier.weight(1f))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    StatusDisponibilidade(livro.disponivel)
                    Spacer(Modifier.weight(1f))
                    Text("Ver detalhes", color = Primary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        }
    }
}