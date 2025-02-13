package com.nayane.estadoatividade

import android.Manifest // ✅ Importação correta
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class InitialActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial) // Verifique se activity_main.xml existe

        // Solicitar permissão de notificação no Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        val loginButton: Button = findViewById(R.id.loginButton)
        val registerButton: Button = findViewById(R.id.registerButton)

        loginButton.setOnClickListener {
            val intent = Intent(this@InitialActivity, AuthActivity::class.java)
            intent.putExtra("isLogin", true) // Modo Login
            startActivity(intent)
        }

        registerButton.setOnClickListener {
            val intent = Intent(this@InitialActivity, AuthActivity::class.java)
            intent.putExtra("isLogin", false) // Modo Cadastro
            startActivity(intent)
        }

    }
}
