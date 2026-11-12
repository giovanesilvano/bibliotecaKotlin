package com.example.bibliotecakotlin.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

object ImagemConversor {
    private const val TAMANHO_MAXIMO = 800

    fun paraBlob(imagem: Bitmap): ByteArray{
        return ByteArrayOutputStream().use { saida ->
            check(imagem.compress(Bitmap.CompressFormat.JPEG, 80, saida)){
                "Não foi possível salvar imagem."
            }
            saida.toByteArray()
        }
    }

    suspend fun paraBlob(contexto: Context, uri: Uri): ByteArray{
        return withContext(Dispatchers.IO){
            val entrada = checkNotNull(
                contexto.contentResolver.openInputStream(uri)
            ){
                "Não foi possível abrir imagem"
            }

            val bytes = entrada.use { imagem ->
                imagem.readBytes()
            }
            paraBlob(reduzir(bytes))
        }
    }

    private fun reduzir(bytes: ByteArray): Bitmap{
        val tamanho = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, tamanho)

        var escala = 1
        while (tamanho.outWidth / escala > TAMANHO_MAXIMO || tamanho.outHeight / escala > TAMANHO_MAXIMO){
            escala *= 2
        }

        val opcoes = BitmapFactory.Options().apply { inSampleSize = escala }
        return checkNotNull(BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opcoes)){
            "Não foi possível ler imagem"
        }
    }
}
