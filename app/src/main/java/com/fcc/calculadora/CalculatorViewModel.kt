package com.fcc.calculadora

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.DecimalFormat
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function // Importante
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

class CalculatorViewModel : ViewModel() {

    // --- ESTADO PRINCIPAL ---
    // operationText es la "única fuente de verdad".
    val operationText = MutableLiveData("0")
    // resultText es un valor "derivado" de operationText.
    val resultText = MutableLiveData("")

    private val formatoDecimal = DecimalFormat("#.##########")

    //ESTADO CIENTÍFICO
    enum class AngleUnit { RAD, DEG }
    //private var angleUnit = AngleUnit.RAD
    private val _angleUnit = MutableLiveData(AngleUnit.RAD)
    val angleUnit: LiveData<AngleUnit> = _angleUnit

    private val _isInverseActive = MutableLiveData(false)
    val isInverseActive: LiveData<Boolean> = _isInverseActive

    //FUNCIONES DE BOTONES

    fun onNumberClicked(number: String) {
        var currentText = operationText.value ?: "0"

        if (currentText == "0" && number != ".") {
            currentText = number
        } else {
            currentText += number
        }

        operationText.value = currentText
        updateResult() //actualizar el resultado
    }

    fun onOperatorClicked(operator: String) {
        val currentText = operationText.value ?: "0"

        if (operator == "=") {
            // Al presionar "=", el resultado se convierte en la nueva operación
            val currentResult = resultText.value
            if (!currentResult.isNullOrEmpty() && currentResult != "Error") {
                operationText.value = currentResult
                resultText.value = ""
            }
            return
        }

        // Evita operadores dobles (ej. "5++") reemplazando el último
        if (currentText.isNotEmpty() && currentText.last().isOperator()) {
            operationText.value = currentText.dropLast(1) + operator
        } else {
            operationText.value = currentText + operator
        }
        updateResult()
    }

    fun onDelete() {
        val currentText = operationText.value ?: "0"
        if (currentText.isNotEmpty() && currentText != "0") {
            operationText.value = currentText.dropLast(1).ifEmpty { "0" }
        } else {
            operationText.value = "0"
        }
        updateResult()
    }

    fun onClear() {
        operationText.value = "0"
        resultText.value = ""
        _isInverseActive.value = false
        //angleUnit = AngleUnit.RAD
        _angleUnit.value = AngleUnit.RAD
    }

    //LÓGICA DE EVALUACIÓN
    private fun updateResult() {
        var expression = operationText.value ?: "0"

        // Si la expresión está vacía o es solo "0", el resultado es vacío.
        if (expression.isEmpty() || expression == "0") {
            resultText.value = ""
            return
        }

        // exp4j no maneja bien los operadores al final (ej. "5+").
        // Si termina en operador, evaluamos la expresión *sin* ese operador.
        if (expression.isNotEmpty() && expression.last().isOperator()) {
            expression = expression.dropLast(1)
        }

        // Si después de quitar el operador no queda nada, salimos.
        if (expression.isEmpty()) {
            resultText.value = ""
            return
        }

        try {
            // Aquí ocurre la magia de exp4j
            val expressionBuilder = ExpressionBuilder(expression)
                .functions(getCustomFunctions()) // Añade nuestras funciones (sin, cos, log...)
                .variables("pi", "e") // Define pi y e
                .build()
                .setVariable("pi", Math.PI)
                .setVariable("e", Math.E)

            val result = expressionBuilder.evaluate()

            // Comprueba si el resultado es un error (NaN, Infinito)
            if (result.isNaN() || result.isInfinite()) {
                resultText.value = "Error"
            } else {
                resultText.value = formatoDecimal.format(result)
            }

        } catch (e: Exception) {
            // Cualquier error de sintaxis (ej. "5++2" o "sin(") se captura aquí
            resultText.value = "" // O "Error de sintaxis"
        }
    }

    private fun getCustomFunctions(): List<Function> {
        val isInverse = _isInverseActive.value ?: false
        // Lee el valor actual del LiveData
        val currentAngleUnit = _angleUnit.value ?: AngleUnit.RAD
        // Lista de funciones
        return listOf(
            //Trigonométricas (sensibles a RAD/DEG)
            object : Function(if (isInverse) "asin" else "sin", 1) {
                override fun apply(vararg args: Double): Double {
                    return if (isInverse) {
                        val resRad = asin(args[0])
                        if (currentAngleUnit == AngleUnit.DEG) Math.toDegrees(resRad) else resRad
                    } else {
                        val argRad = if (currentAngleUnit == AngleUnit.DEG) Math.toRadians(args[0]) else args[0]
                        sin(argRad)
                    }
                }
            },
            object : Function(if (isInverse) "acos" else "cos", 1) {
                override fun apply(vararg args: Double): Double {
                    return if (isInverse) {
                        val resRad = acos(args[0])
                        if (currentAngleUnit == AngleUnit.DEG) Math.toDegrees(resRad) else resRad
                    } else {
                        val argRad = if (currentAngleUnit == AngleUnit.DEG) Math.toRadians(args[0]) else args[0]
                        cos(argRad)
                    }
                }
            },
            object : Function(if (isInverse) "atan" else "tan", 1) {
                override fun apply(vararg args: Double): Double {
                    return if (isInverse) {
                        val resRad = atan(args[0])
                        if (currentAngleUnit == AngleUnit.DEG) Math.toDegrees(resRad) else resRad
                    } else {
                        val argRad = if (currentAngleUnit == AngleUnit.DEG) Math.toRadians(args[0]) else args[0]
                        tan(argRad)
                    }
                }
            },
            //Logaritmos
            object : Function(if (isInverse) "pow10" else "log", 1) { //log es log10
                override fun apply(vararg args: Double): Double {
                    return if (isInverse) 10.0.pow(args[0]) else log10(args[0])
                }
            },
            object : Function(if (isInverse) "exp" else "ln", 1) { //ln
                override fun apply(vararg args: Double): Double {
                    return if (isInverse) exp(args[0]) else ln(args[0]) //Math.log es ln
                }
            },
            //Potencias y raíces
            object : Function(if (isInverse) "sqr" else "sqrt", 1) { //raiz
                override fun apply(vararg args: Double): Double {
                    return if (isInverse) args[0] * args[0] else sqrt(args[0])
                }
            },
            object : Function("yroot", 2) { //Tu función "yroot"
                override fun apply(vararg args: Double): Double {
                    return args[0].pow(1.0 / args[1])
                }
            },
            //factorial
            object : Function("factorial", 1) {
                override fun apply(vararg args: Double): Double {
                    return calculateFactorial(args[0])
                }
            }
        )
    }

    //LÓGICA DE BOTONES CIENTÍFICOS
    fun onInvertClicked() {
        _isInverseActive.value = !(_isInverseActive.value ?: false)
        //No es necesario re-evaluar, la próxima evaluación usará el estado correcto
    }

    fun onAngleUnitClicked(unit: String) {
        //angleUnit = if (unit == "RAD") AngleUnit.RAD else AngleUnit.DEG
        _angleUnit.value = if (unit == "RAD") AngleUnit.RAD else AngleUnit.DEG
        updateResult() //Re-evalúa la expresión con la nueva unidad angular
    }

    fun onConstantClicked(constant: String) {
        val currentText = operationText.value ?: "0"
        val constantText = when(constant) {
            "PI" -> "pi"
            "E" -> "e"
            else -> ""
        }

        if (currentText == "0") {
            operationText.value = constantText
        } else {
            //Inserta "pi" o "e" (exp4j los entiende como variables)
            operationText.value = currentText + constantText
        }
        updateResult()
    }

    fun onScientificFunctionClicked(function: String) {
        val isInverse = _isInverseActive.value ?: false

        //Mapea el nombre del botón al nombre de la función en exp4j
        val functionText = when(function) {
            "sin" -> if (isInverse) "asin" else "sin"
            "cos" -> if (isInverse) "acos" else "cos"
            "tan" -> if (isInverse) "atan" else "tan"
            "log" -> if (isInverse) "pow10" else "log"
            "ln" -> if (isInverse) "exp" else "ln"
            "raiz" -> if (isInverse) "sqr" else "sqrt"
            "factorial" -> "factorial"
            else -> ""
        }

        //Inserta el texto de la función, ej: "sin("
        var currentText = operationText.value ?: "0"
        if (currentText == "0") {
            currentText = "$functionText("
        } else {
            currentText += "$functionText("
        }

        operationText.value = currentText
        //No llamamos a updateResult() porque "sin(" es un error de sintaxis
        //se actualizará cuando el usuario añada el número y cierre el paréntesis.
    }


    //FUNCIONES AUXILIARES
    private fun Char.isOperator(): Boolean {
        return this == '+' || this == '-' || this == '*' || this == '/' || this == '%' || this == '^'
    }

    //Tu función de factorial (sin cambios)
    private fun calculateFactorial(n: Double): Double {
        if (n < 0 || n != floor(n)) {
            throw IllegalArgumentException("Dominio incorrecto")
        }
        if (n > 20) { // Límite razonable para Double
            throw ArithmeticException("Resultado muy grande")
        }
        var result = 1.0
        for (i in 2..n.toInt()) {
            result *= i.toDouble()
        }
        return result
    }
}