package com.nayane.estadoatividade

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.nayane.estadoatividade.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Falha ao obter o token", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("FCM", "Token do dispositivo: $token")
        }

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        binding.welcomeText.text = "Bem-vindo, Aposte da melhor forma!!"

        binding.logoutButton.setOnClickListener {
            // Encerra a sessão e volta para a tela de login
            val intent = Intent(this, InitialActivity::class.java)
            startActivity(intent)
        }

        binding.jogoButton.setOnClickListener{
            //Redirecionamento para a guia do jogo
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.saldoButton.setOnClickListener{
            //Redirecionamento para a guia do jogo
            val intent = Intent(this, SaldoActivity::class.java)
            startActivity(intent)
        }
    }
}
