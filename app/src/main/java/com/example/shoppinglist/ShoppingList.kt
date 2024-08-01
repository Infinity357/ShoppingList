package com.example.shoppinglist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp

data class ShoppingItem(var Id:Int ,
                        var name:String ,
                        var quantity:Int ,
                        var isEditing:Boolean = false){

}


@Composable
fun ShoppingListApp(){
    var sItems by remember{ mutableStateOf(listOf<ShoppingItem>()) }
    var showDialogue by remember { mutableStateOf(false)  }
    var itemName  by remember {
        mutableStateOf("")
    }
    var itemQuantity  by remember { mutableStateOf("1")}

    Column(
        modifier= Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { showDialogue = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)//this will push the add Item button to the top
        ) {
            Text(text = "Add Item")
        }

        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)){
            items(sItems){
                item ->
                shoppingListItem(item = item, onEditClick = {
                    sItems = sItems.map{it.copy(isEditing = it.Id==item.Id)}
                }   , onDeleteClick = {sItems= sItems-item})
                if(item.isEditing){
                    shoppingItemEditor(item = item, onEditComplete = {
                        editedName , editedQuantity ->
                        sItems = sItems.map{it.copy(isEditing = false)}
                        val editedItem = sItems.find { it.Id == item.Id };
                        editedItem?.let{
                            it.name = editedName
                            it.quantity = editedQuantity
                        }
                    })
                }
            }
        }
    }

    if(showDialogue){
        //dialogue box
        AlertDialog(onDismissRequest = { showDialogue = false },

            confirmButton = {
                            Row (modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween){
                                Button(onClick = { if(itemName.isNotBlank()){
                                    val newItem = ShoppingItem(
                                        Id =sItems.size+1,
                                        name = itemName,
                                        quantity = itemQuantity.toInt()
                                    )
                                    sItems = sItems + newItem
                                    itemName = ""
                                    itemQuantity = "1"
                                    showDialogue = false

                                } }) {

                                    Text(text = "Add")
                                }
                                Button(onClick = {showDialogue = false}) {
                                    Text(text = "Cancel")
                                }
                            }
            },
            title = { Text(text = "Add Shopping Item") },
            text = {
                Column {
                    OutlinedTextField(
                        onValueChange = {
                            itemName = it
                                        },
                        value = itemName,
                        modifier = Modifier
                            .padding(8.dp),
                        maxLines = 5
                    )
                    OutlinedTextField(value = itemQuantity, onValueChange = {itemQuantity = it}, singleLine = true , modifier = Modifier.padding(8.dp))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun shoppingItemEditor( item : ShoppingItem , onEditComplete: (String , Int) -> Unit){
    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }
    var isEditing by remember { mutableStateOf(item.isEditing) }

    if(isEditing){
    AlertDialog(onDismissRequest = { isEditing = false},
        title = {Text(text = "Editing")},
        text={
            Column {
                OutlinedTextField(value = editedName, onValueChange = {editedName = it}, singleLine = true , modifier = Modifier.padding(8.dp))
                OutlinedTextField(value = editedQuantity, onValueChange ={editedQuantity = it}, singleLine = true , modifier = Modifier.padding(8.dp))
            }
        },
        confirmButton = {
            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween){
                Button(onClick = {
                    isEditing = false
                    onEditComplete(editedName , editedQuantity.toIntOrNull() ?: 1)
                }) {
                    Text(text = "Confirm")
                }
                Button(onClick = {isEditing = false}) {
                    Text(text = "Cancel")
                }
            }
        }
    )}
}

@Composable
fun shoppingListItem(
    item : ShoppingItem,
    onEditClick : () -> Unit,
    onDeleteClick : () -> Unit
){
    Row (
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(2.dp, Color(0XFF018786)),
                shape = RoundedCornerShape(20)
            )
    ){
        Column {
            Text(text = item.name, modifier = Modifier.padding(horizontal = 18.dp , vertical = 4.dp), fontWeight = FontWeight.W600 )
//            Spacer(modifier = Modifier.width(52.dp))
            Text(text = "Qty : ${item.quantity}", modifier = Modifier.padding(horizontal = 18.dp , vertical = 4.dp), fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier =Modifier.width(135.dp))
        Row (modifier = Modifier.padding(8.dp)){
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit , contentDescription = null)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete , contentDescription = null)
            }
        }
    }
}