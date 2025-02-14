package com.nayane.estadoatividade

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class SaldoActivity : AppCompatActivity() {

    private lateinit var saldoTextView: TextView
    private lateinit var sacarButton: Button
    private lateinit var depositarButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saldo)

        saldoTextView = findViewById(R.id.saldoTextView)
        sacarButton = findViewById(R.id.sacarButton)
        depositarButton = findViewById(R.id.depositarButton)
        val buttonVoltar = findViewById<Button>(R.id.buttonVoltar)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        atualizarSaldoUI()

        sacarButton.setOnClickListener {
            realizarSaque(50) // Simulação: Saque fixo de 50
        }

        depositarButton.setOnClickListener {
            realizarDeposito(100) // Simulação: Depósito fixo de 100
        }

        buttonVoltar.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun obterSaldo(): Int {
        return sharedPreferences.getInt("SALDO", 200) // Saldo inicial fictício
    }

    private fun atualizarSaldo(novoSaldo: Int) {
        sharedPreferences.edit().putInt("SALDO", novoSaldo).apply()
        atualizarSaldoUI()
    }

    private fun atualizarSaldoUI() {
        saldoTextView.text = "Saldo Atual: R$ ${obterSaldo()}"
    }

    private fun realizarSaque(valor: Int) {
        val saldoAtual = obterSaldo()
        if (saldoAtual >= valor) {
            val novoSaldo = saldoAtual - valor
            atualizarSaldo(novoSaldo)
            enviarNotificacaoSaque(valor, novoSaldo)
            Toast.makeText(this, "Saque de R$ $valor realizado!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Saldo insuficiente para saque!", Toast.LENGTH_LONG).show()
        }
    }

    private fun realizarDeposito(valor: Int) {
        val novoSaldo = obterSaldo() + valor
        atualizarSaldo(novoSaldo)
        Toast.makeText(this, "Depósito de R$ $valor realizado!", Toast.LENGTH_LONG).show()
    }

    private fun enviarNotificacaoSaque(valorSacado: Int, saldoRestante: Int) {
        val channelId = "saque_channel"
        val notificationId = 3

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Notificações de Saque", NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val intentSaldo = Intent(this, SaldoActivity::class.java)
        val pendingIntentSaldo = getActivity(this, 0, intentSaldo, FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)

        val pendingIntentFechar = getActivity(this, 0, Intent(),
            FLAG_CANCEL_CURRENT or FLAG_IMMUTABLE
        )

        val comprovanteBitmap = BitmapFactory.decodeResource(resources, R.drawable.comprovante)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_money) // Ícone da notificação
            .setContentTitle("Saque realizado com sucesso!")
            .setContentText("Você sacou R$$valorSacado. Saldo atual: R$$saldoRestante.")
            .setLargeIcon(comprovanteBitmap) // Mostra a imagem do comprovante
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(comprovanteBitmap))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_view, "Ver Saldo", pendingIntentSaldo) // Botão de ação para ver saldo
            .addAction(R.drawable.ic_close, "Fechar", pendingIntentFechar) // Botão para fechar
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(this).notify(notificationId, notification)
    }
}
