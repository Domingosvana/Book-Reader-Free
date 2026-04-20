package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_filter.componets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.bookreadanddownloadforfree.R

@Composable
fun SelectedExtenseBoxDialo(
    selectedLanguages: Set<String>,
    onLanguagesChanged: (Set<String>) -> Unit,
    onDismiss: () -> Unit,
    onButtonOk: () -> Unit
) {
    val languages = listOf("PDF", "EPUB", "MOBI", "FB2", "AZW3")

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.9f)  // Controla largura
            .wrapContentHeight(), // Altura conforme conteúdo
        properties = DialogProperties(
            usePlatformDefaultWidth = false // Desabilita largura padrão
        ),
        title = {
            Text(stringResource(id = R.string.select_format), color = Color.White)
        },
        text = {
            Column {
                languages.forEach { language ->
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
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(language, color = Color.White)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onButtonOk()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBB86FC)
                )
            ) {
                Text(stringResource(id = R.string.ok), color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel), color = Color(0xFFBB86FC))
            }
        },
        containerColor = Color(0xFF1E1E1E),
        shape = RoundedCornerShape(12.dp)
    )
}



@Preview
@Composable
private fun SelectedExtenseBoxDialoBoxDialogPreview() {
    SelectedExtenseBoxDialo(


        selectedLanguages = setOf("English", "Spanish"),
        onLanguagesChanged = {},
        onDismiss = {},
        onButtonOk ={}
    )

}

