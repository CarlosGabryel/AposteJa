package com.nayane.estadoatividade

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nayane.estadoatividade.databinding.ActivityThirdBinding

class loginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThirdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThirdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val emailRecebido = intent.getStringExtra("EMAIL")
        if (emailRecebido != null) {
            binding.emailInput.setText(emailRecebido)
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val senha = binding.passwordInput.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                mostrarErro("Preencha todos os campos!")
            } else {
                val mensagem = validarLogin(email, senha)
                if (mensagem == "sucesso") {
                    solicitarPermissaoENotificar()
                    startActivity(Intent(this, HomeActivity::class.java))
                } else {
                    mostrarErro(mensagem)
                }
            }
        }
    }

    private fun validarLogin(email: String, senha: String): String {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val emailSalvo = sharedPreferences.getString("EMAIL", null)
        val senhaSalva = sharedPreferences.getString("SENHA", null)

        return when {
            email != emailSalvo -> "Email não encontrado!"
            senha != senhaSalva -> "Senha incorreta!"
            else -> "sucesso"
        }
    }

    private fun mostrarErro(mensagem: String) {
        binding.errorTextView.text = mensagem
        binding.errorTextView.visibility = android.view.View.VISIBLE
    }

    // ✅ Verifica a permissão antes de exibir a notificação no Android 13+
    private fun solicitarPermissaoENotificar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001 // Código de solicitação
                )
                return
            }
        }
        mostrarNotificacao(this, "Login realizado!", "Bem-vindo(a) de volta!")
    }

    private fun mostrarNotificacao(context: Context, titulo: String, mensagem: String) {
        val canalId = "canal_login"

        // Criar canal de notificação para Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                canalId, "Notificações de Login", NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }

        // Criar intent para abrir a Home ao clicar na notificação
        val intent = Intent(context, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Criar a notificação
        val notificacao = NotificationCompat.Builder(context, canalId)
            .setSmallIcon(R.drawable.ic_notification) // Substitua pelo seu ícone de notificação
            .setContentTitle(titulo)
            .setContentText(mensagem)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // ✅ Verifica se a permissão foi concedida antes de exibir a notificação
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {
            try {
                NotificationManagerCompat.from(context).notify(1, notificacao)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    // ✅ Lidar com a resposta do usuário à solicitação de permissão
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                mostrarNotificacao(this, "Login realizado!", "Bem-vindo(a) de volta!")
            }
        }
    }
}
