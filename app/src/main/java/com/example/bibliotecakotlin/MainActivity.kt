package com.example.bibliotecakotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.bibliotecakotlin.navigation.AppNavigation
import com.example.bibliotecakotlin.ui.theme.BibliotecaTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            BibliotecaTheme {
                val navController = rememberNavController()

                AppNavigation(
                    navController = navController
                )
            }
        }
    }
}