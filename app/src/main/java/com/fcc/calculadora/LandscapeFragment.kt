package com.fcc.calculadora

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fcc.calculadora.databinding.FragmentLandscapeBinding
import androidx.lifecycle.ViewModelProvider
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat

class LandscapeFragment : Fragment() {
    private var _binding: FragmentLandscapeBinding? = null
    private val binding get() = _binding!!
    private lateinit var calculatorViewModel: CalculatorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calculatorViewModel = ViewModelProvider(requireActivity())[CalculatorViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLandscapeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val colorActivo = ContextCompat.getColor(requireContext(), R.color.button_operator)
        val colorInactivo = ContextCompat.getColor(requireContext(), R.color.button_function)
        //Observadores del ViewModel
        calculatorViewModel.operationText.observe(viewLifecycleOwner) { text ->
            binding.operacionText.text = text
        }
        calculatorViewModel.resultText.observe(viewLifecycleOwner) { text ->
            binding.resultText.text = text
        }
        //Observador para el modo Inverso
        calculatorViewModel.isInverseActive.observe(viewLifecycleOwner) { isInverse ->
            if (isInverse) {
                binding.btnInvertir.backgroundTintList = ColorStateList.valueOf(colorActivo)
                binding.btnSin.text = getString(R.string.asin) // "sin⁻¹"
                binding.btnCos.text = getString(R.string.acos) // "cos⁻¹"
                binding.btnTan.text = getString(R.string.atan) // "tan⁻¹"
                binding.btnLogaritmo.text = getString(R.string.diez_x) // "10ˣ"
                binding.btnLogaritmoNatural.text = getString(R.string.e_x) // "eˣ"
                binding.btnRaiz.text = getString(R.string.x_cuadrado) // "x²"
                binding.btnPotencia.text = getString(R.string.raiz_n) // "ʸ√x"
            } else {
                binding.btnInvertir.backgroundTintList = ColorStateList.valueOf(colorInactivo)
                binding.btnSin.text = getString(R.string.sin) // "sin"
                binding.btnCos.text = getString(R.string.cos) // "cos"
                binding.btnTan.text = getString(R.string.tan) // "tan"
                binding.btnLogaritmo.text = getString(R.string.log) // "log"
                binding.btnLogaritmoNatural.text = getString(R.string.`in`) // "ln"
                binding.btnRaiz.text = getString(R.string.raiz) // "√"
                binding.btnPotencia.text = getString(R.string.potencia) // "xʸ"
            }
        }
        //Listeners de botones básicos
        binding.btnAC.setOnClickListener { calculatorViewModel.onClear() }
        binding.btnDel.setOnClickListener { calculatorViewModel.onDelete() }

        //Numéricos
        binding.btnCero.setOnClickListener { calculatorViewModel.onNumberClicked("0") }
        binding.btn1.setOnClickListener { calculatorViewModel.onNumberClicked("1") }
        binding.btn2.setOnClickListener { calculatorViewModel.onNumberClicked("2") }
        binding.btn3.setOnClickListener { calculatorViewModel.onNumberClicked("3") }
        binding.btn4.setOnClickListener { calculatorViewModel.onNumberClicked("4") }
        binding.btn5.setOnClickListener { calculatorViewModel.onNumberClicked("5") }
        binding.btn6.setOnClickListener { calculatorViewModel.onNumberClicked("6") }
        binding.btn7.setOnClickListener { calculatorViewModel.onNumberClicked("7") }
        binding.btn8.setOnClickListener { calculatorViewModel.onNumberClicked("8") }
        binding.btn9.setOnClickListener { calculatorViewModel.onNumberClicked("9") }
        binding.btnPunto.setOnClickListener { calculatorViewModel.onNumberClicked(".") }
        //Operadores básicos
        binding.btnPorcentaje.setOnClickListener { calculatorViewModel.onOperatorClicked("%") }
        binding.btnEntre.setOnClickListener { calculatorViewModel.onOperatorClicked("/") }
        binding.btnPor.setOnClickListener { calculatorViewModel.onOperatorClicked("*") }
        binding.btnMenos.setOnClickListener { calculatorViewModel.onOperatorClicked("-") }
        binding.btnMas.setOnClickListener { calculatorViewModel.onOperatorClicked("+") }
        binding.btnIgual.setOnClickListener { calculatorViewModel.onOperatorClicked("=") }

        //Listeners para botones científicos
        // Botón Invertir
        binding.btnInvertir.setOnClickListener {
            calculatorViewModel.onInvertClicked()
        }
        // Botones de unidades de ángulo
        binding.btnRadianes.setOnClickListener {
            calculatorViewModel.onAngleUnitClicked("RAD")
        }
        binding.btnGrados.setOnClickListener {
            calculatorViewModel.onAngleUnitClicked("DEG")
        }
        //Botones de constantes
        binding.btnPi.setOnClickListener {
            calculatorViewModel.onConstantClicked("PI")
        }
        binding.btnEuler.setOnClickListener {
            calculatorViewModel.onConstantClicked("E")
        }
        //Botones de funciones unarias (aplican inmediato)
        binding.btnSin.setOnClickListener { calculatorViewModel.onScientificFunctionClicked("sin") }
        binding.btnCos.setOnClickListener { calculatorViewModel.onScientificFunctionClicked("cos") }
        binding.btnTan.setOnClickListener { calculatorViewModel.onScientificFunctionClicked("tan") }
        binding.btnLogaritmo.setOnClickListener { calculatorViewModel.onScientificFunctionClicked("log") }
        binding.btnLogaritmoNatural.setOnClickListener { calculatorViewModel.onScientificFunctionClicked("ln") }
        binding.btnRaiz.setOnClickListener { calculatorViewModel.onScientificFunctionClicked("raiz") }
        binding.btnFactorial.setOnClickListener { calculatorViewModel.onScientificFunctionClicked("factorial") }
        //Botones de paréntesis (los tratamos como "números" para la lógica simple)
        binding.btnParentesisIzq.setOnClickListener { calculatorViewModel.onNumberClicked("(") }
        binding.btnParentesisDer.setOnClickListener { calculatorViewModel.onNumberClicked(")") }
        //Botón de Potencia
        binding.btnPotencia.setOnClickListener {
            //Revisa el estado de inversión para decidir qué operador enviar
            val isInverse = calculatorViewModel.isInverseActive.value ?: false
            if (isInverse) {
                calculatorViewModel.onOperatorClicked("yroot") // "ʸ√x"
            } else {
                calculatorViewModel.onOperatorClicked("^") // "xʸ"
            }
        }
        calculatorViewModel.angleUnit.observe(viewLifecycleOwner) { unit ->
            if (unit == CalculatorViewModel.AngleUnit.RAD) {
                // Modo RAD activo
                binding.btnRadianes.backgroundTintList = ColorStateList.valueOf(colorActivo)
                binding.btnGrados.backgroundTintList = ColorStateList.valueOf(colorInactivo)
            } else {
                // Modo DEG activo
                binding.btnRadianes.backgroundTintList = ColorStateList.valueOf(colorInactivo)
                binding.btnGrados.backgroundTintList = ColorStateList.valueOf(colorActivo)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}