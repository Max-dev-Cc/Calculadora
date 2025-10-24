package com.fcc.calculadora

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fcc.calculadora.databinding.FragmentPortraitBinding
import androidx.lifecycle.ViewModelProvider

class PortraitFragment : Fragment() {
    private var _binding: FragmentPortraitBinding? = null
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
        _binding = FragmentPortraitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Observadores del ViewModel ---
        calculatorViewModel.operationText.observe(viewLifecycleOwner) { text ->
            binding.operacionText.text = text
        }

        calculatorViewModel.resultText.observe(viewLifecycleOwner) { text ->
            binding.resultText.text = text
        }

        // Listener para el botón "AC" (limpiar todo)
        binding.btnAC.setOnClickListener {
            calculatorViewModel.onClear()
        }

        binding.btnDel.setOnClickListener{
            calculatorViewModel.onDelete()
        }

        //Listener para los botones numéricos
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

        //Listener para los operadores (+, -, *, /, =, etc.)
        binding.btnPorcentaje.setOnClickListener{ calculatorViewModel.onOperatorClicked("%") }
        binding.btnEntre.setOnClickListener { calculatorViewModel.onOperatorClicked("/") }
        binding.btnPor.setOnClickListener { calculatorViewModel.onOperatorClicked("*") }
        binding.btnMenos.setOnClickListener { calculatorViewModel.onOperatorClicked("-") }
        binding.btnMas.setOnClickListener { calculatorViewModel.onOperatorClicked("+") }
        binding.btnIgual.setOnClickListener{ calculatorViewModel.onOperatorClicked("=") }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}