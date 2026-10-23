package com.example.bibliotecakotlin.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bibliotecakotlin.ui.components.*
import com.example.bibliotecakotlin.ui.theme.*
import com.example.bibliotecakotlin.ui.viewmodel.UsuarioViewModel

@Composable
fun UsuarioEditarScreen(
    usuarioId: Long,
    onConcluir: () -> Unit,
    vm: UsuarioViewModel = viewModel()
) {
    val context = LocalContext.current
    var novaSenha by remember { mutableStateOf("") }

    LaunchedEffect(usuarioId) { vm.buscarPorId(usuarioId) }
    LaunchedEffect(vm.salvo) {
        if (vm.salvo) {
            Toast.makeText(context, "Usuário atualizado.", Toast.LENGTH_SHORT).show()
            vm.consumirSalvo()
            onConcluir()
        }
    }
    LaunchedEffect(vm.mensagem) {
        vm.mensagem?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            vm.consumirMensagem()
        }
    }

    Column(Modifier.fillMaxSize().background(Background)) {
        HeaderAzul("Administração", "Editar Usuário")

        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = vm.nome,
                onValueChange = vm::mudarNome,
                label = { Text("Nome") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = vm.usuario?.email ?: "",
                onValueChange = {},
                label = { Text("E-mail") },
                singleLine = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = novaSenha,
                onValueChange = { novaSenha = it },
                label = { Text("Nova senha (deixe em branco para manter)") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))
            BotaoPrimario(
                if (vm.carregando) "Salvando..." else "Salvar",
                onClick = { if (!vm.carregando) vm.salvar(novaSenha) }
            )
            TextButton(onClick = onConcluir, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
        }
    }
}