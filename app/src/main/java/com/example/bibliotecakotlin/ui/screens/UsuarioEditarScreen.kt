package com.example.bibliotecakotlin.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bibliotecakotlin.ui.components.*
import com.example.bibliotecakotlin.ui.theme.*
import com.example.bibliotecakotlin.ui.viewmodel.CepViewModel
import com.example.bibliotecakotlin.ui.viewmodel.UsuarioViewModel

@Composable
fun UsuarioEditarScreen(
    usuarioId: Long,
    onConcluir: () -> Unit,
    vm: UsuarioViewModel = viewModel(),
    cepModel: CepViewModel = viewModel()
) {
    val context = LocalContext.current
    var novaSenha by remember { mutableStateOf("") }

    LaunchedEffect(usuarioId) { vm.buscarPorId(usuarioId) }
    LaunchedEffect(vm.usuario) {
        vm.usuario?.let { cepModel.mudarCep(it.cep) }
    }
    LaunchedEffect(cepModel.endereco) {
        cepModel.endereco?.let { vm.receberEndereco(it) }
    }
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
            Modifier.imePadding().verticalScroll(rememberScrollState()).padding(20.dp),
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

            Text("Endereço", style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = cepModel.cep,
                    onValueChange = cepModel::mudarCep,
                    label = { Text("CEP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    enabled = !cepModel.carregando,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = cepModel::consultar, enabled = !cepModel.carregando) {
                    Text("Buscar CEP")
                }
            }
            if (cepModel.carregando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            cepModel.mensagem?.let { Text(it) }
            OutlinedTextField(
                value = vm.rua,
                onValueChange = vm::mudarRua,
                label = { Text("Rua") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = vm.numero,
                onValueChange = vm::mudarNumero,
                label = { Text("Número") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = vm.complemento,
                onValueChange = vm::mudarComplemento,
                label = { Text("Complemento (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = vm.bairro,
                onValueChange = vm::mudarBairro,
                label = { Text("Bairro") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = vm.cidade,
                onValueChange = vm::mudarCidade,
                label = { Text("Cidade") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = vm.uf,
                onValueChange = vm::mudarUf,
                label = { Text("UF") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))
            BotaoPrimario(
                if (vm.carregando) "Salvando..." else "Salvar",
                onClick = { if (!vm.carregando) vm.salvar(novaSenha, cepModel.cep) }
            )
            TextButton(onClick = onConcluir, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
        }
    }
}