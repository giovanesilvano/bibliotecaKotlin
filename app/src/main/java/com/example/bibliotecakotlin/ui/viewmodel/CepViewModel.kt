package com.example.bibliotecakotlin.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bibliotecakotlin.data.api.ViaCepApi
import com.example.bibliotecakotlin.data.model.Endereco
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class CepViewModel : ViewModel() {
    var cep by mutableStateOf("")
        private set
    var endereco by mutableStateOf<Endereco?>(null)
        private set
    var carregando by mutableStateOf(false)
        private set
    var mensagem by mutableStateOf<String?>(null)
        private set

    fun mudarCep(valor: String){
        if(carregando || valor.length > 9) return
        cep = valor
        endereco = null
        mensagem = null
    }

    fun consultar(){
        if(carregando) return
        endereco = null
        mensagem = null

        if(!Regex("[0-9]{5}-?[0-9]{3}").matches(cep)){
            mensagem = "informe um CEP válido"
            return
        }
        val cepConsulta = cep.replace("-", "")
        carregando = true
        viewModelScope.launch {
            try {
                val resposta = ViaCepApi.service.buscar(cepConsulta)
                when{
                    resposta.erro == true -> mensagem = "CEP não encontrado"
                    resposta.cep.isNullOrBlank() -> mensagem = "Resposta Inválida"
                    else -> endereco = resposta
                }
            }catch (erro: CancellationException){
                throw erro
            }
            catch (_: Exception){
                mensagem = "não foi possível consultar o CEP"
            } finally {
                carregando = false
            }
        }
    }
}
