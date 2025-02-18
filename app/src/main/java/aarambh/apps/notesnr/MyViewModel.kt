package aarambh.apps.notesnr

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MyViewModel: ViewModel() {

        private val _state = MutableStateFlow(ScreenState())
        val state = _state.asStateFlow()
        init {
                _state.update {
                        it.copy(notesList = arrayListOf("Note1", "Note2", "Note3 ") )
                }
        }

        fun updateNote(note: String) {
                _state.update {
                        it.copy(note = note)
                }
        }

        fun addNote() {
                _state.update {
                        it.copy(notesList = it.notesList.apply { add(_state.value.note) })
                }
        }
}

data class ScreenState(
        var notesList: ArrayList<String> = arrayListOf(),
        var note: String = ""
)