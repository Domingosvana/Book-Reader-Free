package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_filter.componets

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
import androidx.compose.material3.MaterialTheme
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

import java.util.Calendar


@Composable
fun YearPickerDialog(
    initialYear: Int?,
    onYearSelected: (Int?) -> Unit,
    onDismiss: () -> Unit
) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = remember { (2000..currentYear).toList().reversed() }
    var tempSelectedYear by remember { mutableStateOf(initialYear) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false // 🔥 ISSO permite ocupar toda a largura
        )
    ) {
        // Usamos um Surface ou Box para desenhar o fundo
        Surface(
            modifier = Modifier
                .fillMaxSize(), // Ocupa a tela toda
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Filtrar por Ano",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item {
                        SelectedDateRadio(
                            selected = (tempSelectedYear == null),
                            text = "Todos os anos",
                            onClick = {
                                tempSelectedYear = null
                                onYearSelected(null)
                                onDismiss()
                            }
                        )
                    }
                    items(years) { year ->
                        SelectedDateRadio(
                            selected = (year == tempSelectedYear),
                            text = year.toString(),
                            onClick = {
                                tempSelectedYear = year
                                onYearSelected(year)
                                onDismiss()
                            }
                        )
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("FECHAR", color =MaterialTheme.colorScheme.onBackground)
                }
            }
        }
    }
}
@Preview
@Composable
private fun YearPickerDialogPreview() {
    YearPickerDialog(

        initialYear = null,
        onYearSelected = {},
        onDismiss = {}
    )

}



@Composable
fun SelectedDateRadio(
    selected: Boolean,
    text: String,
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
            onClick = null, // null porque o clique já é tratado na Row (melhor acessibilidade)
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.surfaceBright, // Uma cor de destaque
                unselectedColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground
        )
    }
}



@Preview
@Composable
fun SelectedDateBoxPrevie(){
    SelectedDateRadio(
        selected = true,
        //borderColor = Color.Green,
        // onSelectRadioClick = {},
        modifier = Modifier,
        text = "",
        onClick = {},

        )
}



