package com.example.bibliotecakotlin.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bibliotecakotlin.data.model.Usuario
import com.example.bibliotecakotlin.ui.components.*
import com.example.bibliotecakotlin.ui.theme.*
import com.example.bibliotecakotlin.ui.viewmodel.UsuarioViewModel

@Composable
fun UsuariosScreen(
    onEditar: (Long) -> Unit,
    vm: UsuarioViewModel = viewModel()
) {
    val context = LocalContext.current
    var excluir by remember { mutableStateOf<Usuario?>(null) }

    LaunchedEffect(Unit) { vm.listar() }
    LaunchedEffect(vm.mensagem) {
        vm.mensagem?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            vm.consumirMensagem()
        }
    }

    excluir?.let { u ->
        AlertDialog(
            onDismissRequest = { excluir = null },
            title = { Text("Excluir usuário") },
            text = { Text("Deseja excluir ${u.nome}? Os empréstimos dele também serão removidos.") },
            confirmButton = {
                TextButton(onClick = { vm.excluir(u.id); excluir = null }) { Text("Excluir", color = Red) }
            },
            dismissButton = { TextButton(onClick = { excluir = null }) { Text("Cancelar") } }
        )
    }

    Column(Modifier.fillMaxSize().background(Background)) {
        HeaderAzul("Administração", "Usuários")

        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (vm.usuarios.isEmpty()) {
                item {
                    Text(
                        if (vm.carregando) "Carregando..." else "Nenhum usuário cadastrado.",
                        color = TextGray, fontSize = 14.sp
                    )
                }
            }
            items(vm.usuarios, key = { it.id }) { u ->
                CardBranco {
                    Column(Modifier.padding(20.dp)) {
                        Text(u.nome, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text(u.email, color = TextGray, fontSize = 14.sp)
                    }
                    HorizontalDivider(color = PrimaryLight, modifier = Modifier.padding(horizontal = 20.dp))
                    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)) {
                        TextButton(onClick = { onEditar(u.id) }) { Text("Editar", color = Primary) }
                        TextButton(onClick = { excluir = u }) { Text("Excluir", color = Red) }
                    }
                }
            }
        }
    }
}