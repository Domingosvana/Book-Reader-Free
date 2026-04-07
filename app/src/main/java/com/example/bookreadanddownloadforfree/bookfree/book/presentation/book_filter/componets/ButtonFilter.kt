package com.example.bookreadanddownloadforfree.bookfree.book.presentation.book_filter.componets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun ButtonFilter(
    onPenCardClick: () -> Unit,
    onValue: Any?,
    onValueTitle: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = onValueTitle,
            color = MaterialTheme.colorScheme.onSurfaceVariant, // Cor dinâmica
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Surface(
            onClick = onPenCardClick,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = onValue.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun ButtonFilterreviw() {
    ButtonFilter(
        onPenCardClick = {},
        onValue = true,
        onValueTitle = "Ano de Lançamento"
    )
}













/*
                     OutlinedTextField(
                         value = "",
                         onValueChange = {},
                         label = { Text(text = stringResource(id = R.string.filter_ano_label)) },
                         modifier = modifier
                             .fillMaxWidth(),
                         singleLine = true,
                         maxLines = 1,
                         leadingIcon = {
                             Icon(Icons.Filled.ArrowDropDown,contentDescription = null)
                         }


                     )




                     // Source - https://stackoverflow.com/q
             // Posted by Sayed Rizwan Hashmi, modified by community. See post 'Timeline' for change history
             // Retrieved 2026-01-26, License - CC BY-SA 4.0

             @Composable
             fun RoleSearchDialog(vm: AnnouncementViewModel) {
             Dialog(
                 onDismissRequest = {
                     vm.isSendRoleDialog.value = false
                 },
                 properties = DialogProperties(
                     usePlatformDefaultWidth = false, // experimental,
                 )
             ) {
                 Spacer(modifier = Modifier.height(20.dp))

                 Column(
                     modifier = Modifier
                         .wrapContentHeight()
                         .width(320.dp)
                         .height(400.dp)
                         .padding(all = 16.dp)
                         .background(
                             color = Color(0xFFFFFFFF),
                             shape = RoundedCornerShape(15.dp)
                         ),
                     horizontalAlignment = Alignment.CenterHorizontally
                 ) {

                     /*Header*/
                     Row(
                         modifier = Modifier
                             .fillMaxWidth()
                             .clip(RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)),
                         horizontalArrangement = Arrangement.SpaceBetween
                     ) {

                         Text(
                             modifier = Modifier
                                 .padding(16.dp)
                                 .align(Alignment.CenterVertically),
                             text = "Select Users",
                             style = TextStyle(
                                 fontFamily = FontFamily(Font(R.font.opensans_bold)),
                                 fontSize = 16.sp,
                                 color = Color(0xFFB20710)
                             ),
                             textAlign = TextAlign.Start
                         )

                         Image(
                             modifier = Modifier
                                 .size(48.dp)
                                 .padding(10.dp)
                                 .clip(RoundedCornerShape(10.dp))
                                 .clickable {
                                     vm.isSendRoleDialog.value = false
                                 },
                             painter = painterResource(R.drawable.ic_attachment_remove), contentDescription = "Icon"
                         )
                     }

                         var allCheckedState = remember { mutableStateOf(vm.isAllRoleSelected.value) }


                         Row(
                             modifier = Modifier
                                 .fillMaxWidth()
                                 .padding(start = 16.dp, top = 5.dp),
                             horizontalArrangement = Arrangement.Start,
                         ) {
                             Text(
                                 text = "All",
                                 modifier = Modifier
                                     .padding(start = 10.dp, end = 16.dp)
                                     .weight(0.8f)
                                     .align(Alignment.CenterVertically),
                                 style = TextStyle(
                                     fontFamily = FontFamily(
                                         Font(
                                             resId = R.font.opensans_semibold
                                         )
                                     ),
                                     fontSize = 12.sp,
                                     color = Color(0xFF121212),
                                 )
                             )

                             Checkbox(
                                 checked = allCheckedState.value,
                                 onCheckedChange = {
                                     allCheckedState.value = it
                                     vm.listOfRolesMaster.forEachIndexed  { index , item ->
                                         vm.listOfRolesMaster[index].isCheck = it
                                     }
                                     vm.isAllRoleSelected.value = it
                                 }
                             )
                         }


                         vm.listOfRolesMaster.forEachIndexed { index, item ->

                             Log.e(TAG, "ALL_FLOOR1 item ${item.isCheck}")
                             var myState by remember { mutableStateOf(item.isCheck) }
                             Log.e(TAG, "ALL_FLOOR myState = $myState == ${item.isCheck}")

                             Row(
                                 modifier = Modifier
                                     .fillMaxWidth()
                                     .padding(start = 16.dp, top = 5.dp),
                                 horizontalArrangement = Arrangement.Start,
                             ) {
                                 Text(
                                     text = item.name,
                                     modifier = Modifier
                                         .padding(start = 10.dp, end = 16.dp)
                                         .weight(0.8f)
                                         .align(Alignment.CenterVertically),
                                     style = TextStyle(
                                         fontFamily = FontFamily(
                                             Font(
                                                 resId = R.font.opensans_semibold
                                             )
                                         ),
                                         fontSize = 12.sp,
                                         color = Color(0xFF121212),
                                     )
                                 )

                                 Checkbox(
                                     checked = item.isCheck,
                                     onCheckedChange = { isChecked ->
                                         item.isCheck = isChecked
                                     }
                                 )
                             }

                     }
                 }
             }
             }


              */
