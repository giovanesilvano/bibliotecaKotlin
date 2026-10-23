// ui/screens/UsuarioEditarScreen.kt
package com.example.bibliotecakotlin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bibliotecakotlin.ui.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioEditarScreen(
    id: Long,
    onSalvo: () -> Unit,
    onVoltar: () -> Unit,
    vm: UsuarioViewModel = viewModel()
) {
    var novaSenha by remember { mutableStateOf("") }
    LaunchedEffect(id) { vm.buscarPorId(id) }
    LaunchedEffect(vm.salvo) { if (vm.salvo) { vm.consumirSalvo(); onSalvo() } }

    Scaffold(topBar = { TopAppBar(title = { Text("Editar usuário") }, navigationIcon = {
        TextButton(onClick = onVoltar) { Text("Voltar") } }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = vm.nome, onValueChange = vm::mudarNome,
                label = { Text("Nome") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = vm.usuario?.email ?: "", onValueChange = {}, enabled = false,
                label = { Text("E-mail") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = novaSenha, onValueChange = { novaSenha = it },
                label = { Text("Nova senha (opcional)") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            vm.mensagem?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Button(
                onClick = { vm.salvar(novaSenha) },
                enabled = !vm.carregando,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (vm.carregando) "Salvando..." else "Salvar") }
        }
    }
}