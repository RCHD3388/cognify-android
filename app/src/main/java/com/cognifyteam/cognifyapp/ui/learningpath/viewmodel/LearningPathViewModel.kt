package com.cognifyteam.cognifyapp.ui.learningpath.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class LearningPathUiState(
    val selectedTopic: String = "Machine Learning",
    val customTopic: String = "",
    val description: String = "",
    val isFormValid: Boolean = false
)

class LearningPathViewModel : ViewModel() {
    private val _uiState = MutableLiveData(LearningPathUiState())
    val uiState: LiveData<LearningPathUiState> = _uiState

    fun onTopicSelected(topic: String) {
        _uiState.value = _uiState.value?.copy(
            selectedTopic = topic,
            isFormValid = validateForm(topic, _uiState.value?.customTopic ?: "", _uiState.value?.description ?: "")
        )
    }

    fun onCustomTopicChange(topic: String) {
        _uiState.value = _uiState.value?.copy(
            customTopic = topic,
            isFormValid = validateForm(
                _uiState.value?.selectedTopic ?: "",
                topic,
                _uiState.value?.description ?: ""
            )
        )
    }

    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value?.copy(
            description = description,
            isFormValid = validateForm(
                _uiState.value?.selectedTopic ?: "",
                _uiState.value?.customTopic ?: "",
                description
            )
        )
    }

    private fun validateForm(topic: String, customTopic: String, description: String): Boolean {
        return when {
            topic == "Other" -> customTopic.isNotBlank() && description.isNotBlank()
            else -> description.isNotBlank()
        }
    }
}