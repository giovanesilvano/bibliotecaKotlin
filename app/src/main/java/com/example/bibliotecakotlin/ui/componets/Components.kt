package com.exemplo.biblioteca.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AssignmentReturn
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exemplo.biblioteca.ui.theme.*

enum class Tela { LOGIN, CATALOGO, EMPRESTAR, DEVOLVER, EMPRESTIMOS }

@Composable
fun HeaderAzul(subtitulo: String, titulo: String, mostrarAvatar: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Primary)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(subtitulo, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
            Text(titulo, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        }
        if (mostrarAvatar) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f))
            )
        }
    }
}

@Composable
fun BottomNav(telaAtual: Tela, onNavigate: (Tela) -> Unit) {
    NavigationBar(containerColor = Surface, tonalElevation = 0.dp) {
        val itens = listOf(
            Triple(Tela.CATALOGO, "Catálogo", Icons.Outlined.MenuBook),
            Triple(Tela.EMPRESTAR, "Emprestar", Icons.Filled.Add),
            Triple(Tela.DEVOLVER, "Devolver", Icons.Outlined.AssignmentReturn),
            Triple(Tela.EMPRESTIMOS, "Empréstimos", Icons.Outlined.LibraryBooks)
        )
        itens.forEach { (tela, label, icone) ->
            NavigationBarItem(
                selected = telaAtual == tela,
                onClick = { onNavigate(tela) },
                icon = { Icon(icone, contentDescription = label) },
                label = { Text(label, fontSize = 12.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    unselectedIconColor = TextGray,
                    unselectedTextColor = TextGray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun CapaLivro(texto: String, cor: Color, largura: Int = 80, altura: Int = 120, autor: String? = null) {
    Box(
        modifier = Modifier
            .size(largura.dp, altura.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(cor)
            .padding(10.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column {
            Text(texto, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, lineHeight = 13.sp)
            if (autor != null) {
                Spacer(Modifier.height(6.dp))
                Text(autor, color = Color.White.copy(alpha = 0.8f), fontSize = 9.sp)
            }
        }
    }
}

@Composable
fun Badge(texto: String, corTexto: Color, corFundo: Color) {
    Text(
        texto,
        color = corTexto,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .background(corFundo, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    )
}

@Composable
fun StatusDisponibilidade(disponivel: Boolean) {
    val cor = if (disponivel) Green else Red
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(cor))
        Spacer(Modifier.width(6.dp))
        Text(if (disponivel) "Disponível" else "Indisponível", color = cor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun CampoBusca(valor: String, onChange: (String) -> Unit, placeholder: String, trailing: (@Composable () -> Unit)? = null) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        placeholder = { Text(placeholder, color = TextGray) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        trailingIcon = trailing,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            unfocusedBorderColor = Border,
            focusedContainerColor = Surface,
            unfocusedContainerColor = Surface
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun BotaoPrimario(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Primary),
        modifier = modifier.fillMaxWidth().height(56.dp)
    ) {
        Text(texto, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CardBranco(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        content = content
    )
}

@Composable
fun TextoCentralizadoCinza(texto: String) {
    Text(
        texto, color = TextGray, fontSize = 13.sp, textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}