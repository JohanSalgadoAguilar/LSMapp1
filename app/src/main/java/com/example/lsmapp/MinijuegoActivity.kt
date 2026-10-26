package com.example.lsmapp

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class MinijuegoActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private var categoriaActual = "abecedario" // por defecto
    private val scores = IntArray(5) { 0 } // Almacena el score de las 5 actividades

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minijuego)

        val btnAbecedario = findViewById<Button>(R.id.btnCatAbecedario)
        val btnNumeros = findViewById<Button>(R.id.btnCatNumeros)

        val fab1 = findViewById<FloatingActionButton>(R.id.fabNivel1)
        val fab2 = findViewById<FloatingActionButton>(R.id.fabNivel2)
        val fab3 = findViewById<FloatingActionButton>(R.id.fabNivel3)
        val fab4 = findViewById<FloatingActionButton>(R.id.fabNivel4)
        val fab5 = findViewById<FloatingActionButton>(R.id.fabNivel5)

        val bounce = AnimationUtils.loadAnimation(this, R.anim.bounce_anim)
        listOf(fab1, fab2, fab3, fab4, fab5).forEach { it.startAnimation(bounce) }

        // Cambio de sección
        btnAbecedario.setOnClickListener {
            categoriaActual = "abecedario"
            Toast.makeText(this, "Categoría: Abecedario seleccionada", Toast.LENGTH_SHORT).show()
            cargarDatosFirestore()
        }

        btnNumeros.setOnClickListener {
            categoriaActual = "numeros"
            Toast.makeText(this, "Categoría: Números seleccionada", Toast.LENGTH_SHORT).show()
            cargarDatosFirestore()
        }

        // Configuración de clics para las 5 actividades
        fab1.setOnClickListener { iniciarActividad(1) }
        fab2.setOnClickListener { iniciarActividad(2) }
        fab3.setOnClickListener { iniciarActividad(3) }
        fab4.setOnClickListener { iniciarActividad(4) }
        fab5.setOnClickListener { iniciarActividad(5) }

        cargarDatosFirestore()
    }

    private fun cargarDatosFirestore() {
        // Consulta a Firestore filtrando por categoriaId tal como lo tienes guardado
        db.collection("tu_coleccion") // Reemplaza con el nombre real de tu colección si es diferente
            .whereEqualTo("categoriaId", categoriaActual)
            .get()
            .addOnSuccessListener { documents ->
                val totalElementos = documents.size()
                Toast.makeText(this, "Cargados $totalElementos elementos de $categoriaActual", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun iniciarActividad(nivel: Int) {
        when (nivel) {
            1 -> simularEvaluacionActividad(1, correctas = 5, totales = 5) // Introducción al LSM
            2 -> simularEvaluacionActividad(2, correctas = 4, totales = 5) // Identificar señas
            3 -> simularEvaluacionActividad(3, correctas = 5, totales = 5) // Seleccionar seña (Letra)
            4 -> simularEvaluacionActividad(4, correctas = 3, totales = 5) // Seleccionar seña (Práctica)
            5 -> simularEvaluacionActividad(5, correctas = 5, totales = 5) // Escribir palabra con señas
        }
    }

    private fun simularEvaluacionActividad(nivel: Int, correctas: Int, totales: Int) {
        scores[nivel - 1] = correctas
        Toast.makeText(this, "Nivel $nivel completado. Score: $correctas/$totales", Toast.LENGTH_LONG).show()
    }
}