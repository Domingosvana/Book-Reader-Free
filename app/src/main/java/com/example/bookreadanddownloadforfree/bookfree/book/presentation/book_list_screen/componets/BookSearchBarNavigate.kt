package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_list_screen.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bookreadanddownloadforfree.ui.theme.gradient6





















@Composable
fun BookSearchBarNavigate(
    onClickPerfil:()->Unit,
    onClickNavigate:()->Unit
){

    CompositionLocalProvider(
        LocalTextSelectionColors provides TextSelectionColors(
            handleColor = MaterialTheme.colorScheme.scrim,
            backgroundColor = MaterialTheme.colorScheme.scrim
        )
    ) {

        Column(
            modifier = Modifier
                .background(
                    //  shape = RoundedCornerShape(100),
                    brush = Brush.linearGradient(gradient6)
                )
                .minimumInteractiveComponentSize()
                .fillMaxWidth()
                //  .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Row( verticalAlignment = Alignment.CenterVertically) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onClickNavigate() }
                        .height(45.dp),
                    contentAlignment = Alignment.CenterStart // 👈 início
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "",
                        modifier = Modifier.size(30.dp),
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }



                Spacer(Modifier.width(15.dp))
                ///button icon
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clickable { onClickPerfil }
                        .align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(45.dp)
                    )
                }



            }








        }


    }







}




@Composable
@Preview
fun PreviewBookSearchBar(){

    BookSearchBarNavigate(
        onClickPerfil = {},
    onClickNavigate = {}
    )
}


























/*
@Composable
fun BookSearchBarNavigate(
    onClickPerfil:()->Unit,
    onClickNavigate:()->Unit

){

    ElevatedCard(modifier=Modifier) {
        Column(
            modifier = Modifier//.fillMaxWidth()
                .background(
                    //  shape = RoundedCornerShape(100),
                    brush = Brush.linearGradient(gradient6)

                )
                .padding(vertical = 6.dp, horizontal = 10.dp)
                //.width(440.dp)
                .fillMaxWidth()
                .height(90.dp),
                //.padding(horizontal = 40.dp, vertical = 42.dp),
                 //   verticalArrangement = Arrangement.Center,
            //horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                
            ){
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .weight(4f)
                        .clickable { onClickNavigate() }
                        //.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(90.dp)
                            .align(Alignment.CenterStart)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .weight(1f)
                        .clickable { onClickPerfil() }
                    //.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(90.dp)
                            .align(Alignment.CenterStart)
                    )
                }


            }
        }










    }


}




@Composable
@Preview
fun PreviewBookSearchBar(){

    BookSearchBarNavigate(
        onClickPerfil = {},
        onClickNavigate = {},
    )
}

 */