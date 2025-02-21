package aarambh.apps.notesnr

import android.widget.NumberPicker.OnValueChangeListener
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
//import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
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
import aarambh.apps.notesnr.data.Task
import androidx.compose.material3.MaterialTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun App(viewModel: MyViewModel) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    var isDialogOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TO-DO") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Rounded.Menu, "Menu")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { isDialogOpen = true }) {
                Icon(Icons.Rounded.Add, "Add Note")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(state.value.tasksList) { task ->
                    NoteCard(
                        task = task,
                        onDelete = { viewModel.removeNote(task) },
                        onToggleComplete = { viewModel.toggleNoteCompletion(task) },
                        onEdit = { newContent, newDescription -> viewModel.editNote(task, newContent, newDescription) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        if (isDialogOpen) {
            AddNoteDialog(
                text = state.value.note,
                description = state.value.description,
                selectedPriority = state.value.selectedPriority,
                selectedDueDate = state.value.selectedDueDate,
                onTextChange = viewModel::updateNote,
                onDescriptionChange = viewModel::updateDescription,
                onPriorityChange = viewModel::updatePriority,
                onDueDateChange = viewModel::updateDueDate,
                onSaveChanges = {
                    viewModel.addNote()
                    isDialogOpen = false
                },
                onDismiss = { isDialogOpen = false }
            )
        }
    }
}

@Composable
fun NoteCard(
    task: Task,
    onDelete: () -> Unit,
    onToggleComplete: () -> Unit,
    onEdit: (String, String?) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf(task.content) }
    var editDescription by remember { mutableStateOf(task.description ?: "") }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEditing) {
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = editText,
                            onValueChange = { editText = it },
                            label = { Text("Task") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editDescription,
                            onValueChange = { editDescription = it },
                            label = { Text("Description (Optional)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    IconButton(onClick = {
                        onEdit(editText, editDescription.takeIf { it.isNotBlank() })
                        isEditing = false
                    }) {
                        Icon(Icons.Rounded.Check, "Save")
                    }
                } else {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.content,
                            style = MaterialTheme.typography.titleMedium,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        )
                        task.description?.let { desc ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Row {
                        IconButton(onClick = onToggleComplete) {
                            Icon(
                                if (task.isCompleted) Icons.Rounded.CheckCircle
                                else Icons.Filled.Check,
                                "Toggle Complete"
                            )
                        }
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Rounded.Edit, "Edit")
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Rounded.Delete, "Delete")
                        }
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Priority: ${task.priority}",
                    color = when(task.priority) {
                        Priority.HIGH -> Color.Red
                        Priority.MEDIUM -> Color.Blue
                        Priority.LOW -> Color.Green
                    }
                )
                task.dueDate?.let { dueDate ->
                    Text(
                        text = "Due: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(dueDate))}",
                        color = if (dueDate < System.currentTimeMillis()) Color.Red else Color.Gray
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteDialog(
    text: String,
    description: String,
    selectedPriority: Priority,
    selectedDueDate: Long?,
    onTextChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriorityChange: (Priority) -> Unit,
    onDueDateChange: (Long?) -> Unit,
    onSaveChanges: () -> Unit,
    onDismiss: () -> Unit
) {
    var showPriorityDropdown by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text("Add New Task", fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    label = { Text("Task") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Box {
                    OutlinedButton(
                        onClick = { showPriorityDropdown = true }
                    ) {
                        Text("Priority: $selectedPriority")
                    }
                    DropdownMenu(
                        expanded = showPriorityDropdown,
                        onDismissRequest = { showPriorityDropdown = false }
                    ) {
                        Priority.values().forEach { priority ->
                            DropdownMenuItem(
                                text = { Text(priority.name) },
                                onClick = {
                                    onPriorityChange(priority)
                                    showPriorityDropdown = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedButton(
                    onClick = { showDatePicker = true }
                ) {
                    Text(
                        selectedDueDate?.let {
                            "Due: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))}"
                        } ?: "Set Due Date"
                    )
                }
                
                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState()
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let(onDueDateChange)
                                showDatePicker = false
                            }) {
                                Text("OK")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    TextButton(onClick = onSaveChanges) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
