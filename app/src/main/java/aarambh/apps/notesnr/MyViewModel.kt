package aarambh.apps.notesnr

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import aarambh.apps.notesnr.data.Task
import aarambh.apps.notesnr.data.TaskDatabase
import aarambh.apps.notesnr.data.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    private val _state = MutableStateFlow(TasksScreenState())
    val state: StateFlow<TasksScreenState> = _state.asStateFlow()

    init {
        val database = withContext(Dispatchers.IO) {
            TaskDatabase.getDatabase(application)
        }
        repository = TaskRepository(database.taskDao())
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.allTasks.collect { tasks ->
                    withContext(Dispatchers.Main) {
                        _state.update { it.copy(taskList = tasks) }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateTaskTitle(title: String) {
        _state.update { it.copy(currentTaskTitle = title) }
    }

    fun updateTaskDescription(description: String) {
        _state.update { it.copy(currentTaskDescription = description) }
    }

    fun addTask() {
        if (_state.value.currentTaskTitle.isBlank()) return
        
        viewModelScope.launch {
            val task = Task(
                title = _state.value.currentTaskTitle,
                description = _state.value.currentTaskDescription
            )
            repository.insertTask(task)
            clearAndHideDialog()
        }
    }

    fun showAddDialog() {
        _state.update { it.copy(isDialogVisible = true) }
    }

    fun hideDialog() {
        clearAndHideDialog()
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.updateTaskCompletion(task.id, !task.isCompleted)
        }
    }

    fun startEditingTask(task: Task) {
        _state.update {
            it.copy(
                editingTaskId = task.id,
                currentTaskTitle = task.title,
                currentTaskDescription = task.description,
                isDialogVisible = true
            )
        }
    }

    fun updateTask() {
        val editingTaskId = _state.value.editingTaskId ?: return
        if (_state.value.currentTaskTitle.isBlank()) return

        viewModelScope.launch {
            repository.updateTask(
                editingTaskId,
                _state.value.currentTaskTitle,
                _state.value.currentTaskDescription
            )
            clearAndHideDialog()
        }
    }

    private fun clearAndHideDialog() {
        _state.update {
            it.copy(
                isDialogVisible = false,
                currentTaskTitle = "",
                currentTaskDescription = "",
                editingTaskId = null
            )
        }
    }
}

data class TasksScreenState(
    val taskList: List<Task> = emptyList(),
    val currentTaskTitle: String = "",
    val currentTaskDescription: String = "",
    val isDialogVisible: Boolean = false,
    val editingTaskId: Long? = null
)