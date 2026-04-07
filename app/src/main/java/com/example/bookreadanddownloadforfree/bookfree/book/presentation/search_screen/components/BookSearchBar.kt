package com.example.bookreadanddownloadforfree.bookfree.book.presentation.search_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bookreadanddownloadforfree.ui.theme.gradient6
//import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.TextField
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@Composable
fun BookSearchBar(
    searchQuery: String,
    onSearchQueryChange:(String) ->Unit,
    onImeSearch:()-> Unit,
    onClickPerfil:()->Unit,
    onButtonSearchBack:()->Unit
){

    val keyboardController = LocalSoftwareKeyboardController.current

    CompositionLocalProvider(
        LocalTextSelectionColors provides TextSelectionColors(
            handleColor = Color.Green,
            backgroundColor = Color.Yellow
        )
    ) {

        Column(
            modifier = Modifier

                    //.align(Alignment.CenterStart)
              //  .padding(start = 8.dp, end = 8.dp)

                .background(
                  //  shape = RoundedCornerShape(100),
                    brush = Brush.linearGradient(gradient6)
                )
                .minimumInteractiveComponentSize()
                .fillMaxWidth()
              //  .fillMaxSize()
              //  .padding(18.dp),
            //verticalArrangement = Arrangement.Center,
           // horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Box(){
                IconButton(
                    onClick = onButtonSearchBack,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                      //  .padding(bottom = 2.dp)

                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "",
                        modifier = Modifier.size(25.dp)
                    )
                }
            }


            Row() {

                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .weight(1f) // .fillMaxWidth(),
                    .background(
                        brush = Brush.linearGradient(gradient6),
                shape = RoundedCornerShape(12.dp)
                ),

                   // shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        cursorColor = Color.LightGray,
                        unfocusedContainerColor  = Color.Transparent, //
                        disabledContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        //focusedBorderColor = Color.Transparent,
                    ),

                    placeholder = { Text(text = "") },

                    leadingIcon = {
                        Icon(
                            modifier = Modifier
                                .size(30.dp),
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    },

                    singleLine = true,
                    keyboardActions = KeyboardActions(onSearch = {onImeSearch()}),

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),

                    trailingIcon = {
                        AnimatedVisibility(
                            visible = searchQuery.isNotEmpty(),

                        ) {
                            IconButton(
                                onClick = {onSearchQueryChange("")
                                    keyboardController?.hide()
                                }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                        }
                    },


                    )
                Spacer(Modifier.width(15.dp))
                ///button icon
                Box(
                    modifier = Modifier
                        .size(49.dp)
                        .clickable { onClickPerfil() }
                        .align(Alignment.CenterVertically)
                        .padding(end = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FilterList,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(47.dp)
                    )
                }



            }








        }


    }







}




@Composable
@Preview
fun PreviewBookSearchBar(){

    BookSearchBar(
        searchQuery = "",
        onSearchQueryChange = {},
        onImeSearch = {},
        onClickPerfil = {},
        onButtonSearchBack ={}
    )
}