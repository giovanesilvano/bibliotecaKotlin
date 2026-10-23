// ui/screens/UsuariosScreen.kt
package com.example.bibliotecakotlin.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bibliotecakotlin.data.model.Usuario
import com.example.bibliotecakotlin.ui.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreen(
    onEditar: (Long) -> Unit,
    onNovo: () -> Unit,
    onVoltar: () -> Unit,
    vm: UsuarioViewModel = viewModel()
) {
    var excluindo by remember { mutableStateOf<Usuario?>(null) }
    LaunchedEffect(Unit) { vm.listar() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Usuários") }, navigationIcon = {
            TextButton(onClick = onVoltar) { Text("Voltar") } }) },
        floatingActionButton = { FloatingActionButton(onClick = onNovo) { Text("+") } }
    ) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            vm.mensagem?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            if (vm.carregando) CircularProgressIndicator()
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(vm.usuarios) { u ->
                    Card(Modifier.fillMaxWidth().clickable { onEditar(u.id) }) {
                        Row(
                            Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(u.nome, style = MaterialTheme.typography.titleMedium)
                                Text(u.email, style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { excluindo = u }) {
                                Icon(Icons.Default.Delete, contentDescription = "Excluir")
                            }
                        }
                    }
                }
            }
        }
    }

    excluindo?.let { u ->
        AlertDialog(
            onDismissRequest = { excluindo = null },
            title = { Text("Excluir usuário") },
            text = { Text("Excluir ${u.nome}? Os empréstimos dele também serão removidos.") },
            confirmButton = { TextButton(onClick = { vm.excluir(u.id); excluindo = null }) { Text("Excluir") } },
            dismissButton = { TextButton(onClick = { excluindo = null }) { Text("Cancelar") } }
        )
    }
}