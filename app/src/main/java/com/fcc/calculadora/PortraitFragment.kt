package com.fcc.calculadora

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.fcc.calculadora.databinding.FragmentPortraitBinding

class PortraitFragment : Fragment() {
    private var _binding: FragmentPortraitBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPortraitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Listener para el botón "AC" (limpiar todo)
        binding.btnAC.setOnClickListener {
            binding.resultText.text = "0"
            binding.operacionText.text = ""
        }

        binding.btnDel.setOnClickListener{
            val currentText = binding.operacionText.text.toString()
            // Solo hacemos algo si el texto no está vacío o no es "0"
            if (currentText.isNotEmpty() && currentText != "0") {
                // Quitamos el último carácter
                val newText = currentText.dropLast(1)
                // Si el texto resultante está vacío, mostramos "0".
                // Si no, mostramos el texto recortado.
                binding.operacionText.text = newText.ifEmpty { "0" }
            }
        }

        //Listener para los botones numéricos
        binding.btnCero.setOnClickListener { onNumberClicked(it) }
        binding.btn1.setOnClickListener { onNumberClicked(it) }
        binding.btn2.setOnClickListener { onNumberClicked(it) }
        binding.btn3.setOnClickListener { onNumberClicked(it) }
        binding.btn4.setOnClickListener { onNumberClicked(it) }
        binding.btn5.setOnClickListener { onNumberClicked(it) }
        binding.btn6.setOnClickListener { onNumberClicked(it) }
        binding.btn7.setOnClickListener { onNumberClicked(it) }
        binding.btn8.setOnClickListener { onNumberClicked(it) }
        binding.btn9.setOnClickListener { onNumberClicked(it) }
        binding.btnPunto.setOnClickListener { onNumberClicked(it) }

        //Listener para los operadores (+, -, *, /, =, etc.)
        binding.btnPorcentaje.setOnClickListener{ onOperatorClicked(it) }
        binding.btnEntre.setOnClickListener { onOperatorClicked(it) }
        binding.btnPor.setOnClickListener { onOperatorClicked(it) }
        binding.btnMenos.setOnClickListener { onOperatorClicked(it) }
        binding.btnMas.setOnClickListener { onOperatorClicked(it) }
        binding.btnIgual.setOnClickListener{ onOperatorClicked(it) }
    }

    private fun onNumberClicked(view: View) {
        val button = view as Button
        val buttonText = button.text.toString()
        val operationText = binding.operacionText.text.toString()

        if(buttonText == "."){
            //dividimos las operaciones para no tener 5*3.1 y mejor tener 5,3.1
            val numeros = operationText.split('*','-','*','/')
            if (numeros.isNotEmpty() && numeros.last().contains(".")){
                //si ya tiene un punto entonces no hacemos nada
                return
            }
        }

        //si el resultado actual es "0" y se presiona un número, reemplazar el "0".
        if (operationText == "0" && buttonText != ".") {
            binding.operacionText.text = buttonText
        }else {
            //si no, simplemente añadir el número al final.
            binding.operacionText.append(buttonText)
        }
    }

    private fun onOperatorClicked(view: View) {
        val button = view as Button
        val operator = button.text.toString()
        val currentText = binding.operacionText.text.toString()

        if (operator == "=") {
            // 1. Llamar a la función que calcula el resultado
            // 2. Poner el resultado en binding.resultText
            // 3. Posiblemente mover el resultado a binding.operacionText para seguir operando
        } else {
            //Logica para +, -, *, /
            if (currentText.isNotEmpty() && currentText != "0") {
                //reemplazar el último operador si ya hay uno
                val lastChar = currentText.last()
                if (lastChar == '+' || lastChar == '-' || lastChar == '*' || lastChar == '/') {
                    //reemplaza "5+" por "5-" si el usuario cambia de opinión
                    binding.operacionText.text = currentText.dropLast(1)
                    binding.operacionText.append(operator)
                } else {
                    binding.operacionText.append(operator)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 4. Muy importante: limpiar la referencia al binding para evitar fugas de memoria.
        _binding = null
    }
}