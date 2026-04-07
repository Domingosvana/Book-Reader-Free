package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_filter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookreadanddownloadforfree.R
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_filter.componets.ButtonFilter
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_filter.componets.SelectedExtenseBoxDialo
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_filter.componets.SelectedLangBoxDialog
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_filter.componets.YearPickerDialog
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.search_screen.BookFilterAction
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.search_screen.BookFilterState
import com.example.bookreadanddownloadforfree.bookfree.book.presentation.search_screen.BookSearchViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun FilterScreenRoot(
    viewModel: BookSearchViewModel =koinViewModel(),
    OnNavigateFilterBack:() ->Unit,
    OnButtonFilterBack: () -> Unit
){
    FilterScreen(

        onAction = { action ->
            when(action){
             is  BookFilterAction.OnNavigateFilterBack -> OnNavigateFilterBack()

             is BookFilterAction.onButtonFilterBack -> OnButtonFilterBack()


                else -> Unit
            }

            viewModel.onActionFilter(action)

        },

         state = viewModel.statefilter.collectAsStateWithLifecycle()

    )
}


@Composable
fun FilterScreen(
    onAction: (BookFilterAction) -> Unit,
    state: State<BookFilterState>,
    viewModel: BookSearchViewModel = koinViewModel()
) {
    // Estados de controle dos Diálogos
    var mostrarAnoDe by remember { mutableStateOf(false) }
    var mostrarAnoTe by remember { mutableStateOf(false) }
    var mostrarLang by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LibraryScreenTopBar(
                onBackFilter = { onAction(BookFilterAction.OnNavigateFilterBack) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Text(
                text = "Período de Lançamento",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    ButtonFilter(
                        onPenCardClick = { mostrarAnoDe = true },
                        onValue = state.value.selectedYearDeMax ?: "Início",
                        onValueTitle = "Ano (de)"
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    ButtonFilter(
                        onPenCardClick = { mostrarAnoTe = true },
                        onValue = state.value.selectedYearDeMin ?: "Fim",
                        onValueTitle = "Ano (até)"
                    )
                }
            }

            ButtonFilter(
                onPenCardClick = { mostrarLang = true },
                onValue = if (state.value.selectedLanguages.isEmpty()) "Todos os idiomas"
                else state.value.selectedLanguages.joinToString(", "),
                onValueTitle = "Idiomas Selecionados"
            )

            TextButton(
                onClick = { onAction(BookFilterAction.Clear) },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "LIMPAR TODOS OS FILTROS",
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Empurra o botão de aplicar para o final da tela
            Spacer(modifier = Modifier.height(120.dp))

            Button(
                onClick = {
                    viewModel.filtrarLivros()
                    onAction(BookFilterAction.onButtonFilterBack)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceBright,
                )
            ) {
                Text("APLICAR FILTROS", fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }

    // --- LOGICA DOS DIÁLOGOS (OVERLAYS) ---

    // Diálogo: Ano Inicial
    if (mostrarAnoDe) {
        YearPickerDialog(
            initialYear = state.value.selectedYearDeMax,
            onYearSelected = { year ->
                onAction(BookFilterAction.onYearSelectedMax(year))
                mostrarAnoDe = false
            },
            onDismiss = { mostrarAnoDe = false }
        )
    }

    // Diálogo: Ano Final
    if (mostrarAnoTe) {
        YearPickerDialog(
            initialYear = state.value.selectedYearDeMin,
            onYearSelected = { year ->
                onAction(BookFilterAction.onYearSelectedMin(year))
                mostrarAnoTe = false
            },
            onDismiss = { mostrarAnoTe = false }
        )
    }

    // Diálogo: Seleção de Idiomas
    if (mostrarLang) {
        SelectedLangBoxDialog(
            selectedLanguages = state.value.selectedLanguages,
            onLanguagesChanged = { languages ->
                onAction(BookFilterAction.onLanguagesChanged(languages))
            },
            onDismiss = { mostrarLang = false },
            onButtonOk = {
                // Ao clicar em OK, apenas fecha o diálogo, o estado já foi atualizado
                mostrarLang = false
            }
        )
    }
}





fun <T> joinToString(
    collection: Collection<T>,
    separator: String = ",",
    prefix: String = "",
    postfix: String = ""
): String {
    val result = StringBuilder(prefix)

    for ((index, element) in collection.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(element)
    }

    result.append(postfix)
    return result.toString()
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreenTopBar(
    onBackFilter:() -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.surfaceBright),
        title = {
            Text(
                text = stringResource(id = R.string.filter_title),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackFilter) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint =  MaterialTheme.colorScheme.onBackground
                )



            }
        }
    )
}


@Composable
fun ButtonFilter(
    onClickButtonFilter: () -> Unit,
    onValueTitle: String,
    modifier: Modifier = Modifier
) {

    Button(onClick = onClickButtonFilter,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceBright)
            .fillMaxWidth(),
      //  verticalArrangement = Arrangement.Bottom,



          //  .height(50.dp)
        ) {
        Text(text = onValueTitle, fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
            )

    }


}




@Composable
fun  SelectedDateScreen(
    modifier: Modifier = Modifier,
    ) {


    Surface(
        modifier = Modifier
            .fillMaxSize()
            //   .background(Color.White)
            //.padding(16.dp)
    ) {
        YearPickerDialog(
            initialYear = null,
            onYearSelected = {},
            onDismiss = {}

        )

    }






}



@Composable
fun  SelectedDateIntem(
    modifier: Modifier = Modifier,
    onPenSelectedClick: Boolean = false ) {

}














@Composable
@Preview
fun ButtonFilterPreview(){
    ButtonFilter(
        onClickButtonFilter = {},
        onValueTitle = "Aplicar"
    )
}



@Composable
@Preview
fun LibraryScreenTopBarPreview(){
    LibraryScreenTopBar(
        onBackFilter = {}
    )
}






@Preview( )
@Composable
private fun FilterScreenPreview() {
    FilterScreenRoot(
        //onAction = {},
       // viewModel = ViewModel() ,
        OnNavigateFilterBack = {},
        OnButtonFilterBack = {}

    )
}






/*
sealed interface FilterOnAction{

    data object OnNavigateFilterBack:FilterOnAction

}

 */



















/*
@Preview( )
@Composable
private fun SelectedDateScreenPreview() {
    SelectedDateScreen()
}


@Preview( )
@Composable
private fun SelectedDateIntemPreview() {
    SelectedDateIntem()
}

 */
























































/*
@Composable
fun SelectedLangBoxDialog(
    selectedLanguages: Set<String>,  // Conjunto para múltipla seleção
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




    //val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    // val years = remember { (2000..currentYear).toList().reversed() }
    //  var tempSelectedYear by remember { mutableStateOf(initialYear) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = true // 🔥 ISSO permite ocupar toda a largura
        )
    ) {


            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF1E1E1E)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Languages", color = Color.White)

                    LazyColumn {
                        items(languages) { language ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val newSet = selectedLanguages.toMutableSet()
                                        if (language in newSet) {
                                            newSet.remove(language)
                                        } else {
                                            newSet.add(language)
                                        }
                                        onLanguagesChanged(newSet)
                                    }
                            ) {
                                Checkbox(
                                    checked = language in selectedLanguages,
                                    onCheckedChange = { checked ->
                                        val newSet = selectedLanguages.toMutableSet()
                                        if (checked) {
                                            newSet.add(language)
                                        } else {
                                            newSet.remove(language)
                                        }
                                        onLanguagesChanged(newSet)
                                    }
                                )
                                Text(language, color = Color.White)
                            }
                        }
                    }
                }


                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom
                ) {
                    TextButton(
                        onClick = onButtonOk,
                        modifier = Modifier
                    ) {
                        Text("CANCEL", color = Color(0xFFBB86FC))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(
                        onClick = {
                            // Aqui você pode salvar a seleção se necessário
                            onDismiss()
                        }
                    ) {
                        Text("OK", color = Color(0xFFBB86FC))
                    }
                }




            }






    }





}

 */






















































