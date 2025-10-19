package com.fcc.calculadora

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData

class CalculatorViewModel : ViewModel() {

    // LiveData permite que los Fragmentos "observen" el texto
    // y se actualicen automáticamente cuando cambia.
    val operationText = MutableLiveData<String>("0")
    val resultText = MutableLiveData<String>("")

    fun onNumberClicked(number: String) {
        val currentText = operationText.value ?: "0"

        // Aquí va tu lógica del punto decimal y del "0"
        if (currentText == "0" && number != ".") {
            operationText.value = number
        } else {
            // Aquí iría la lógica para evitar múltiples puntos
            operationText.value = currentText + number
        }
    }

    fun onOperatorClicked(operator: String) {
        if (operator == "=") {
            // Aquí llamas a tu lógica de cálculo (ej. con una librería como mXparser)
            // val resultado = ...
            // resultText.value = resultado.toString()
        } else {
            // Aquí va tu lógica de operadores (+, -, etc.)
            operationText.value = (operationText.value ?: "") + operator
        }
    }

    fun onDelete() {
        //logica de btnDel
        val currentText = operationText.value ?: "0"
        if (currentText.isNotEmpty() && currentText != "0") {
            operationText.value = currentText.dropLast(1).ifEmpty { "0" }
        }
    }

    fun onClear() {
        //logica de btnAC
        operationText.value = "0"
        resultText.value = ""
    }
}