package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_filter.componets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun SelectedLangBoxDialog(
    selectedLanguages: Set<String>,
    onLanguagesChanged: (Set<String>) -> Unit,
    onDismiss: () -> Unit,
    onButtonOk: () -> Unit
) {

    val languages = listOf(
        "English", "Spanish",
        "French", "German",
        "Portuguese", "Italian", "Russian",
        "Chinese (Mandarin)",
        "Japanese", "Korean",
        "Arabic", "Hindi",
        "Turkish", "Dutch",
        "Swedish", "Polish",
        "Vietnamese", "Thai",
        "Indonesian", "Malay",
        "Persian (Farsi)", "Ukrainian",
        "Greek", "Hebrew",
        "Czech", "Romanian",
        "Hungarian", "Danish",
        "Finnish", "Norwegian", "Bulgarian", "Croatian",
        "Serbian", "Slovak",
        "Slovenian", "Lithuanian",
        "Latvian","Estonian",
        "Icelandic", "Maltese", "Afrikaans",
        "Swahili", "Zulu", "Xhosa",
        "Amharic", "Bengali", "Punjabi",
        "Tamil", "Telugu", "Urdu",
        "Sinhala", "Burmese", "Khmer",
        "Lao", "Mongolian",
        "Georgian", "Armenian",
        "Azerbaijani", "Kazakh", "Uzbek", "Kyrgyz", "Tajik",
        "Turkmen", "Pashto",
        "Kurdish", "Sindhi",
        "Nepali", "Sanskrit",
        "Tibetan", "Uyghur",
        "Hausa", "Yoruba",
        "Igbo","Somali",
        "Oromo", "Malagasy",
        "Maori", "Hawaiian",
        "Fijian", "Samoan",
        "Tongan", "Tahitian",
        "Guaraní", "Quechua",
        "Aymara", "Basque",
        "Catalan", "Galician", "Welsh",
        "Irish", "Scottish Gaelic",
        "Breton", "Corsican",
        "Sardinian", "Sicilian",
        "Luxembourgish", "Albanian",
        "Macedonian", "Bosnian",
        "Montenegrin", "Kinyarwanda",
        "Kirundi", "Chewa",
        "Shona", "Swati",
        "Tswana", "Venda",
        "Tsonga", "Sotho",
        "Northern Sotho",
        "Southern Ndebele"
    )



    // 1. Lista completa e Estado da busca
    //val allLanguages = remember { listOf("English", "Portuguese", "Spanish", "French", "German", "Italian", "Russian", "Chinese", "Japanese") } // ... sua lista completa
    var searchQuery by remember { mutableStateOf("") }

    // 2. Filtragem em tempo real
    val filteredLanguages = remember(searchQuery) {
        if (searchQuery.isEmpty()) {
            languages
        } else {
            languages.filter { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Título
                Text("Selecionar Idiomas", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.titleLarge)

                // 3. Campo de Pesquisa (Search Bar)
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    placeholder = { Text("Pesquisar idioma...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpar", tint =MaterialTheme.colorScheme.onBackground)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        focusedBorderColor = MaterialTheme.colorScheme.onBackground
                    )
                )

                // 4. Lista Filtrada
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(filteredLanguages) { language ->
                        LanguageCheckItem(
                            language = language,
                            isSelected = language in selectedLanguages,
                            onToggle = { checked ->
                                val newSet = selectedLanguages.toMutableSet()
                                if (checked) newSet.add(language) else newSet.remove(language)
                                onLanguagesChanged(newSet)
                            }
                        )
                    }
                }

                // Botões de Ação
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("CANCELAR", color = Color.Gray) }
                    TextButton(onClick = {
                        onButtonOk()
                        onDismiss()
                    }) { Text("OK", color = MaterialTheme.colorScheme.onBackground) }
                }
            }
        }
    }
}


@Preview
@Composable
private fun SelectedLangBoxDialogPreview() {
    SelectedLangBoxDialog(


        selectedLanguages = setOf("English", "Spanish"),
        onLanguagesChanged = {},
        onDismiss = {},
        onButtonOk ={}
    )

}



@Composable
fun LanguageCheckItem(
    language: String,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!isSelected) }
            .padding(vertical = 8.dp)
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle(it) },
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.surfaceBright)
        )
        Text(language, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(start = 8.dp))
    }
}







@Composable
fun SelectedLangBox(
    selected: Boolean,
    text: String = "English",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.surfaceBright,
                unselectedColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground
        )
    }
}


@Preview
@Composable
fun SelectedLangBoxPrevie(){
    SelectedLangBox(
        selected = true,
        //borderColor = Color.Green,
        // onSelectRadioClick = {},
        modifier = Modifier,
        text = "English",
        onClick = {},

        )
}


















