package com.example.bookreadanddownloadforfree.bookfree.book.data.database

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


object StringListTypeConverter {

    @TypeConverter
    fun fromString(value: String):List<String>{
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromList(list: List<String>):String{
        return Json.encodeToString(list)
    }


}


/*
Verificação de Segurança (Listas no SQLite)
Como vi que sua SearchBookEntity tem campos como authors: List<String>, o Room vai dar erro de compilação a menos que você tenha um TypeConverter. O SQLite não sabe salvar listas.

Se você ainda não tem, crie uma classe assim no seu pacote de banco de dados:

class StringListConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return if (value.isBlank()) emptyList() else value.split(",")
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(",")
    }
}
 */