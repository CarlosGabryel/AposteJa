package com.nayane.estadoatividade

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nayane.estadoatividade.databinding.ActivitySecondBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.modifyButton.setOnClickListener {
            val email = binding.emailText.text.toString().trim()
            val senha = binding.passwordText.text.toString().trim()
            val confirmarSenha = binding.confirmPasswordText.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            } else if (senha != confirmarSenha) {
                Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show()
            } else {
                salvarNoBanco(email, senha)
                Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()

                // Redireciona para a tela de login (ThirdActivity)
                val intent = Intent(this, loginActivity::class.java)
                intent.putExtra("EMAIL", email)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun salvarNoBanco(email: String, senha: String) {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("EMAIL", email)
        editor.putString("SENHA", senha)
        editor.apply()
    }
}
