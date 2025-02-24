package com.nayane.estadoatividade

import android.Manifest
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var tipoAposta = 1 // Define o tipo de aposta inicialmente como Número

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextAposta = findViewById<EditText>(R.id.editTextAposta)
        val editTextNumero = findViewById<EditText>(R.id.editTextNumero)
        val spinnerCor = findViewById<Spinner>(R.id.spinnerCor)
        val spinnerParidade = findViewById<Spinner>(R.id.spinnerParidade)
        val buttonNumero = findViewById<Button>(R.id.buttonNumero)
        val buttonCor = findViewById<Button>(R.id.buttonCor)
        val buttonParImpar = findViewById<Button>(R.id.buttonParImpar)
        val buttonGirar = findViewById<Button>(R.id.buttonGirar)
        val buttonVoltar = findViewById<Button>(R.id.buttonVoltar)
        val textViewResultado = findViewById<TextView>(R.id.textViewResultado)

        // Configurar Spinner para Cores
        val cores = arrayOf("Vermelho", "Preto")
        val adapterCores = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cores)
        spinnerCor.adapter = adapterCores

        // Configurar Spinner para Ímpar/Par
        val paridade = arrayOf("Ímpar", "Par")
        val adapterParidade = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, paridade)
        spinnerParidade.adapter = adapterParidade

        // Adiciona funcionalidade para alternar entre os tipos de aposta
        buttonNumero.setOnClickListener {
            tipoAposta = 1
            editTextNumero.visibility = View.VISIBLE
            spinnerCor.visibility = View.GONE
            spinnerParidade.visibility = View.GONE
        }

        buttonCor.setOnClickListener {
            tipoAposta = 2
            editTextNumero.visibility = View.GONE
            spinnerCor.visibility = View.VISIBLE
            spinnerParidade.visibility = View.GONE
        }

        buttonParImpar.setOnClickListener {
            tipoAposta = 3
            editTextNumero.visibility = View.GONE
            spinnerCor.visibility = View.GONE
            spinnerParidade.visibility = View.VISIBLE
        }

        buttonVoltar.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        buttonGirar.setOnClickListener {
            val apostaValor = editTextAposta.text.toString().toIntOrNull() ?: 0
            val numeroSorteado = if (tipoAposta == 1 && editTextNumero.text.toString().toIntOrNull() == 13 && apostaValor == 13) {
                13 // A roleta "rouba" para garantir a vitória no número 13
            } else {
                Random.nextInt(37)
            }
            val corSorteada = getCor(numeroSorteado)
            var resultado = "Escolha inválida!"
            var ganhou = false
            var premio = 0

            when (tipoAposta) {
                1 -> {
                    val numEscolhido = editTextNumero.text.toString().toIntOrNull()
                    if (numEscolhido == numeroSorteado) {
                        ganhou = true
                        premio = apostaValor * 35
                        resultado = "Ganhou! $premio"
                        if (numeroSorteado == 13) {
                            mostrarImagemVitoria()
                        }
                    } else {
                        resultado = "Perdeu! Sorteado: $numeroSorteado"
                    }
                }
                2 -> {
                    val corEscolhida = spinnerCor.selectedItem.toString()
                    if (corEscolhida == corSorteada) {
                        ganhou = true
                        premio = apostaValor * 2
                        resultado = "Ganhou! $premio"
                    } else {
                        resultado = "Perdeu! Cor sorteada: $corSorteada"
                    }
                }
                3 -> {
                    val paridadeEscolhida = spinnerParidade.selectedItem.toString()
                    if ((numeroSorteado % 2 == 0 && paridadeEscolhida == "Par") ||
                        (numeroSorteado % 2 != 0 && paridadeEscolhida == "Ímpar")) {
                        ganhou = true
                        premio = apostaValor * 2
                        resultado = "Ganhou! $premio"
                    } else {
                        resultado = "Perdeu! Sorteado: $numeroSorteado"
                    }
                }
            }

            textViewResultado.text = resultado
            sendGameNotification(ganhou, premio, apostaValor)
        }
    }

    private fun getCor(numero: Int): String {
        val numerosVermelhos = setOf(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
        return when {
            numero == 0 -> "Verde"
            numero in numerosVermelhos -> "Vermelho"
            else -> "Preto"
        }
    }

    private fun mostrarImagemVitoria() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_vitoria)
        val closeButton = dialog.findViewById<ImageView>(R.id.closeButton)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun sendGameNotification(ganhou: Boolean, valor: Int, aposta: Int) {
        val channelId = "game_channel"
        val notificationId = 2

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Game Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val titulo = if (ganhou) "Parabéns, você ganhou!" else "Infelizmente, você perdeu."
        val mensagem = if (ganhou) "Você ganhou R$$valor!" else "Você perdeu R$$aposta."
        val icone = if (ganhou) R.drawable.ic_view else R.drawable.ic_close

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(icone)
            .setContentTitle(titulo)
            .setContentText(mensagem)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
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
