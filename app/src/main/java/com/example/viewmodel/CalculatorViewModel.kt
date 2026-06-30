package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.askGemini
import com.example.data.HistoryItem
import com.example.data.HistoryRepository
import com.example.math.evaluateMath
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CalculatorViewModel(private val repository: HistoryRepository) : ViewModel() {

    val history = repository.allHistory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _result = MutableStateFlow("")
    val result: StateFlow<String> = _result.asStateFlow()
    
    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking = _isAiThinking.asStateFlow()
    
    private val _chatHistory = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val chatHistory = _chatHistory.asStateFlow()

    fun onEvent(event: CalculatorEvent) {
        when (event) {
            is CalculatorEvent.Number -> appendExpression(event.number.toString())
            is CalculatorEvent.Operation -> appendExpression(event.op)
            CalculatorEvent.Clear -> {
                _expression.value = ""
                _result.value = ""
            }
            CalculatorEvent.Delete -> {
                if (_expression.value.isNotEmpty()) {
                    _expression.value = _expression.value.dropLast(1)
                }
            }
            CalculatorEvent.Calculate -> calculate()
            CalculatorEvent.AiSolve -> solveWithAi()
            is CalculatorEvent.Chat -> sendChatMessage(event.message)
            is CalculatorEvent.ToggleFavorite -> viewModelScope.launch {
                repository.toggleFavorite(event.id, event.isFavorite)
            }
        }
    }

    private fun appendExpression(str: String) {
        _expression.value += str
    }

    private fun calculate() {
        val expr = _expression.value
        if (expr.isBlank()) return
        try {
            val res = evaluateMath(expr)
            val resStr = if (res % 1.0 == 0.0) res.toInt().toString() else res.toString()
            _result.value = resStr
            
            viewModelScope.launch {
                repository.insert(HistoryItem(expression = expr, result = resStr))
            }
        } catch (e: Exception) {
            _result.value = "Error"
        }
    }

    private fun solveWithAi() {
        val expr = _expression.value
        if (expr.isBlank()) return
        
        _isAiThinking.value = true
        _result.value = "AI is solving..."
        
        viewModelScope.launch {
            val response = askGemini(
                prompt = "Solve this math problem or explain this expression: $expr. Provide a concise step-by-step explanation and the final answer.",
                systemPrompt = "You are an expert AI Math Solver."
            )
            _result.value = response
            
            repository.insert(HistoryItem(expression = expr, result = "AI Solved", aiExplanation = response))
            _isAiThinking.value = false
        }
    }
    
    private fun sendChatMessage(message: String) {
        if (message.isBlank()) return
        val currentChat = _chatHistory.value.toMutableList()
        currentChat.add(Pair(message, "AI is thinking..."))
        _chatHistory.value = currentChat
        
        viewModelScope.launch {
            val response = askGemini(
                prompt = message,
                systemPrompt = "You are a helpful AI Assistant for math, science, and learning. You help explain concepts clearly using simple language."
            )
            val updatedChat = _chatHistory.value.toMutableList()
            updatedChat[updatedChat.lastIndex] = Pair(message, response)
            _chatHistory.value = updatedChat
        }
    }
}

sealed class CalculatorEvent {
    data class Number(val number: Int) : CalculatorEvent()
    data class Operation(val op: String) : CalculatorEvent()
    object Clear : CalculatorEvent()
    object Delete : CalculatorEvent()
    object Calculate : CalculatorEvent()
    object AiSolve : CalculatorEvent()
    data class Chat(val message: String) : CalculatorEvent()
    data class ToggleFavorite(val id: Int, val isFavorite: Boolean) : CalculatorEvent()
}
