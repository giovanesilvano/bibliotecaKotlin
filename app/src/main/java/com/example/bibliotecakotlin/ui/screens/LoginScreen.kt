package com.example.bibliotecakotlin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exemplo.biblioteca.ui.components.BotaoPrimario
import com.exemplo.biblioteca.ui.theme.*

@Composable
fun LoginScreen(onLogin: () -> Unit) {
    var usuario by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().background(PrimaryLight),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().background(Primary).statusBarsPadding().padding(vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Biblioteca Universitária", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.weight(1f))

        Card(
            modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(100.dp).clip(RoundedCornerShape(24.dp)).background(PrimaryLight))
                Spacer(Modifier.height(24.dp))
                Text("Bem-vindo", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Acesse sua conta para consultar e gerenciar seus empréstimos.",
                    color = TextGray, fontSize = 14.sp, textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(28.dp))

                CampoLogin("Matrícula ou e-mail", usuario, { usuario = it }, "Digite sua matrícula ou e-mail")
                Spacer(Modifier.height(16.dp))
                CampoLogin("Senha", senha, { senha = it }, "Digite sua senha", senha = true)

                Spacer(Modifier.height(12.dp))
                Text(
                    "Esqueci minha senha", color = Primary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.End)
                )
                Spacer(Modifier.height(20.dp))
                BotaoPrimario("Entrar", onClick = onLogin)
                Spacer(Modifier.height(20.dp))
                Text("Use as mesmas credenciais do portal acadêmico.", color = TextGray, fontSize = 12.sp)
            }
        }

        Spacer(Modifier.weight(1f))
        Text(
            "Universidade · Sistema Integrado de Bibliotecas",
            color = TextGray, fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )
    }
}

@Composable
private fun CampoLogin(label: String, valor: String, onChange: (String) -> Unit, placeholder: String, senha: Boolean = false) {
    Column(Modifier.fillMaxWidth()) {
        Text(label, fontWeight = FontWeight.SemiBold, color = TextDark, fontSize = 14.sp)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = onChange,
            placeholder = { Text(placeholder, color = TextGray) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (senha) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, unfocusedBorderColor = Border),
            modifier = Modifier.fillMaxWidth()
        )
    }
}