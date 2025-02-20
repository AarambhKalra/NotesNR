package aarambh.apps.notesnr

import android.widget.NumberPicker.OnValueChangeListener
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun App(viewModel: MyViewModel) {

    val state = viewModel.state.collectAsStateWithLifecycle()
    var isDialogOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {

                    Text(text = "Notes")
                },
                actions = {
                    Icon(
                        imageVector = Icons.Rounded.Menu,
                        contentDescription = null
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {isDialogOpen = true}) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color.White)
        ) {
            if (isDialogOpen) {
                AddDialog(
                    text = state.value.note,
                    onTextChange = {
                        viewModel.updateNote(it)
                    },
                    onSaveChanges = {
                        viewModel.addNote()
                        isDialogOpen = false
                    },
                    onDismiss = { isDialogOpen = false }
                )
            }
            LazyColumn() {
                items(state.value.notesList) { note ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(text = note, modifier = Modifier.padding(12.dp))
                        IconButton(
                            onClick = {
                                viewModel.removeNote(note)
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) { Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = null
                        ) }
                    }
                }
            }

        }


    }
}


@Composable
fun AddDialog(
    modifier: Modifier = Modifier,
    text: String = "",
    onTextChange: (String) -> Unit = {},
    onSaveChanges: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Add Note", fontSize = 24.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(12.dp))
                TextFieldState(text = text, placeholder = "Enter Note", onValueChange = onTextChange)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        onSaveChanges()
                        onDismiss()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = "Save")
                }
            }
        }

    }

}

@Composable
fun TextFieldState(text: String, onValueChange: (String) -> Unit = {}, placeholder: String) {
    OutlinedTextField(
        value = text,
        onValueChange = {
            onValueChange(it)
        },
        placeholder = {
            Text(text = placeholder)
        }
    )
}
@Preview
@Composable
fun AppPreview() {
    App(
        MyViewModel()
    )
}





    



