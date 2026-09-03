package com.exemplo.biblioteca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.bibliotecakotlin.ui.screens.CatalogoScreen
import com.example.bibliotecakotlin.ui.screens.DevolverScreen
import com.example.bibliotecakotlin.ui.screens.EmprestimosScreen
import com.example.bibliotecakotlin.ui.screens.LoginScreen
import com.exemplo.biblioteca.ui.components.BottomNav
import com.exemplo.biblioteca.ui.components.Tela
import com.exemplo.biblioteca.ui.screens.*
import com.exemplo.biblioteca.ui.theme.BibliotecaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BibliotecaTheme { BibliotecaApp() }
        }
    }
}

@Composable
fun BibliotecaApp() {
    var telaAtual by remember { mutableStateOf(Tela.LOGIN) }

    if (telaAtual == Tela.LOGIN) {
        LoginScreen(onLogin = { telaAtual = Tela.CATALOGO })
        return
    }

    Scaffold(
        bottomBar = { BottomNav(telaAtual) { telaAtual = it } }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(bottom = padding.calculateBottomPadding())) {
            when (telaAtual) {
                Tela.CATALOGO -> CatalogoScreen()
                Tela.EMPRESTAR -> EmprestarScreen(onConfirmar = { telaAtual = Tela.EMPRESTIMOS })
                Tela.DEVOLVER -> DevolverScreen(onConfirmar = { telaAtual = Tela.EMPRESTIMOS })
                Tela.EMPRESTIMOS -> EmprestimosScreen()
                Tela.LOGIN -> Unit
            }
        }
    }
}