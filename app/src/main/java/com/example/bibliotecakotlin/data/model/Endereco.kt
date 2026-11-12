package com.example.bibliotecakotlin.data.model

import com.google.gson.annotations.SerializedName

data class Endereco(
    @SerializedName("cep") val cep: String? = null,
    @SerializedName("logradouro") val rua: String? = null,
    @SerializedName("complemento") val complemento: String? = null,
    @SerializedName("bairro") val bairro: String? = null,
    @SerializedName("localidade") val cidade: String? = null,
    @SerializedName("uf") val uf: String? = null,
    @SerializedName("ddd") val ddd: String? = null,
    @SerializedName("erro") val erro: Boolean? = null,
)
