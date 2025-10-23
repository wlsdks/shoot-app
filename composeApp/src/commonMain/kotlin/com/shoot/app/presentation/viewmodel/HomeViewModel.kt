package com.shoot.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoot.app.data.repository.SampleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val data: List<String> = emptyList(),
    val error: String? = null
)

class HomeViewModel(
    private val sampleRepository: SampleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            sampleRepository.getSampleData()
                .onSuccess { message ->
                    sampleRepository.observeSampleData().collect { items ->
                        _uiState.value = HomeUiState(
                            isLoading = false,
                            data = items,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.value = HomeUiState(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    fun onRefresh() {
        loadData()
    }
}
