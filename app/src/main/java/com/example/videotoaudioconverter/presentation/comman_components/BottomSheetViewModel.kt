package com.example.videotoaudioconverter.presentation.comman_components

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class BottomSheetViewModelState(
    var isDateSelected: Boolean = true,
    var isSizeSelected: Boolean = false,
    var isDurationSelected: Boolean = false,
    var isNameSelected: Boolean = false,
    var selectedDateOrder: String = "new_to_old",
    var selectedSizeOrder: String = "Small_To_Large",
    var selectedNameOrder: String = "A_to_Z",
    var selectedDurationOrder: String = "new_to_old",
    var selectedSortBy: String = "Date"
)

class BottomSheetViewModel : ViewModel() {

    private val _state = MutableStateFlow(BottomSheetViewModelState())
    val state: StateFlow<BottomSheetViewModelState> get() = _state

    /** Change active sort type **/
    fun selectSortBy(type: String) {
        _state.update {
            it.copy(
                selectedSortBy = type,
                isDateSelected = type == "Date",
                isSizeSelected = type == "Size",
                isDurationSelected = type == "Duration",
                isNameSelected = type == "Name"
            )
        }
    }

    /** Change order values **/
    fun setDateOrder(order: String) {
        _state.update { it.copy(selectedDateOrder = order) }
    }

    fun setSizeOrder(order: String) {
        _state.update { it.copy(selectedSizeOrder = order) }
    }

    fun setDurationOrder(order: String) {
        _state.update { it.copy(selectedDurationOrder = order) }
    }

    fun setNameOrder(order: String) {
        _state.update { it.copy(selectedNameOrder = order) }
    }


    fun setIsDateSelected(selected: Boolean) {
        _state.update { it.copy(isDateSelected = selected) }
    }

    fun setIsSizeSelected(selected: Boolean) {
        _state.update { it.copy(isSizeSelected = selected) }
    }

    fun setIsDurationSelected(selected: Boolean) {
        _state.update { it.copy(isDurationSelected = selected) }
    }

    fun setIsNameSelected(selected: Boolean) {
        _state.update { it.copy(isNameSelected = selected) }
    }
}
