package aarambh.apps.notesnr

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import aarambh.apps.notesnr.data.Task
import aarambh.apps.notesnr.data.TaskDatabase
import aarambh.apps.notesnr.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    private val _state = MutableStateFlow(ScreenState())
    val state: StateFlow<ScreenState> = _state.asStateFlow()

    init {
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        viewModelScope.launch {
            repository.allTasks.collect { tasks ->
                _state.value = _state.value.copy(tasksList = ArrayList(tasks))
            }
        }
    }

    fun updateNote(note: String) {
        _state.value = _state.value.copy(note = note)
    }

    fun updateDescription(description: String) {
        _state.value = _state.value.copy(description = description)
    }

    fun updatePriority(priority: Priority) {
        _state.value = _state.value.copy(selectedPriority = priority)
    }

    fun updateDueDate(dueDate: Long?) {
        _state.value = _state.value.copy(selectedDueDate = dueDate)
    }

    fun addNote() {
        val task = Task(
            content = state.value.note,
            description = state.value.description.takeIf { it.isNotBlank() },
            priority = state.value.selectedPriority,
            dueDate = state.value.selectedDueDate,
            isCompleted = false,
            createdAt = System.currentTimeMillis(),
            modifiedAt = System.currentTimeMillis()
        )
        viewModelScope.launch {
            repository.insertTask(task)
            _state.value = _state.value.copy(
                note = "",
                description = "",
                selectedPriority = Priority.MEDIUM,
                selectedDueDate = null
            )
        }
    }

    fun removeNote(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun toggleNoteCompletion(task: Task) {
        viewModelScope.launch {
            repository.updateTaskCompletion(task.id, !task.isCompleted)
        }
    }

    fun editNote(task: Task, newContent: String, newDescription: String? = null) {
        viewModelScope.launch {
            repository.updateTask(task.copy(
                content = newContent,
                description = newDescription ?: task.description,
                modifiedAt = System.currentTimeMillis()
            ))
        }
    }
}

data class ScreenState(
    var tasksList: List<Task> = emptyList(),
    var note: String = "",
    var description: String = "",
    var selectedPriority: Priority = Priority.MEDIUM,
    var selectedDueDate: Long? = null
)

enum class Priority {
    LOW, MEDIUM, HIGH
}