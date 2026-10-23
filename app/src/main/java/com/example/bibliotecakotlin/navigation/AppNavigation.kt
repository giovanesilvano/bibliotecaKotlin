package com.example.bibliotecakotlin.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.bibliotecakotlin.ui.components.BottomNav
import com.example.bibliotecakotlin.ui.components.Tela
import com.example.bibliotecakotlin.ui.screens.CadastroScreen
import com.example.bibliotecakotlin.ui.screens.CatalogoScreen
import com.example.bibliotecakotlin.ui.screens.DevolverScreen
import com.example.bibliotecakotlin.ui.screens.EmprestarScreen
import com.example.bibliotecakotlin.ui.screens.EmprestimosScreen
import com.example.bibliotecakotlin.ui.screens.LivroFormScreen
import com.example.bibliotecakotlin.ui.screens.LoginScreen
import com.example.bibliotecakotlin.ui.screens.UsuarioEditarScreen
import com.example.bibliotecakotlin.ui.screens.UsuariosScreen

@Composable
fun AppNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Login
    ) {
        composable<Login> {
            LoginScreen(
                onLoginSucesso = {
                    navController.navigate(Catalogo) {
                        popUpTo(Login) {
                            inclusive = true
                        }
                    }
                },
                irParaCadastro = {
                    navController.navigate(Cadastro)
                }
            )
        }

        composable<Cadastro> {
            CadastroScreen(
                irParaLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable<Catalogo> {
            TelaComNavegacao(
                telaAtual = Tela.CATALOGO,
                navController = navController,
                mostrarUsuarios = true
            ) {
                CatalogoScreen(
                    onNovoLivro = { navController.navigate(LivroNovo) },
                    onEditarLivro = { navController.navigate(LivroEditar(it)) }
                )
            }
        }

        composable<LivroNovo> {
            LivroFormScreen(
                livroId = null,
                onConcluir = { navController.popBackStack() }
            )
        }

        composable<LivroEditar> { entry ->
            val rota = entry.toRoute<LivroEditar>()
            LivroFormScreen(
                livroId = rota.id,
                onConcluir = { navController.popBackStack() }
            )
        }

        composable<Usuarios> {
            UsuariosScreen(
                onEditar = { navController.navigate(UsuarioEditar(it)) }
            )
        }

        composable<UsuarioEditar> { entry ->
            val rota = entry.toRoute<UsuarioEditar>()
            UsuarioEditarScreen(
                usuarioId = rota.id,
                onConcluir = { navController.popBackStack() }
            )
        }

        composable<Emprestar> {
            TelaComNavegacao(
                telaAtual = Tela.EMPRESTAR,
                navController = navController
            ) {
                EmprestarScreen(
                    onConfirmar = {
                        navController.navigate(Emprestimos) {
                            popUpTo(Emprestar) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }

        composable<Devolver> {
            TelaComNavegacao(
                telaAtual = Tela.DEVOLVER,
                navController = navController
            ) {
                DevolverScreen(
                    onConfirmar = {
                        navController.navigate(Emprestimos) {
                            popUpTo(Devolver) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }

        composable<Emprestimos> {
            TelaComNavegacao(
                telaAtual = Tela.EMPRESTIMOS,
                navController = navController
            ) {
                EmprestimosScreen()
            }
        }
    }
}

@Composable
private fun TelaComNavegacao(
    telaAtual: Tela,
    navController: NavHostController,
    mostrarUsuarios: Boolean = false,
    content: @Composable () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            if (mostrarUsuarios) {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate(Usuarios) }
                ) {
                    Text("Usuários")
                }
            }
        },
        bottomBar = {
            BottomNav(
                telaAtual = telaAtual,
                onNavigate = { telaSelecionada ->
                    when (telaSelecionada) {
                        Tela.CATALOGO -> {
                            navController.navigate(Catalogo) {
                                launchSingleTop = true
                            }
                        }

                        Tela.EMPRESTAR -> {
                            navController.navigate(Emprestar) {
                                launchSingleTop = true
                            }
                        }

                        Tela.DEVOLVER -> {
                            navController.navigate(Devolver) {
                                launchSingleTop = true
                            }
                        }

                        Tela.EMPRESTIMOS -> {
                            navController.navigate(Emprestimos) {
                                launchSingleTop = true
                            }
                        }

                        Tela.LOGIN -> {
                            navController.navigate(Login) {
                                popUpTo(0) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            content()
        }
    }
}