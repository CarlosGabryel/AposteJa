package com.nayane.estadoatividade

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService

fun mostrarNotificacao(context: Context, titulo: String, mensagem: String) {
    val canalId = "canal_login"

    // Criar canal de notificação (somente para Android 8+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "game_channel"
        val channelName = "Game Notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH  // 🔊 Habilita som

        val audioAttributes = android.media.AudioAttributes.Builder()
            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
            .build()

        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = "Notificações do jogo de apostas"
            enableLights(true) // Habilita luz LED de notificação
            lightColor = android.graphics.Color.BLUE
            enableVibration(true) // Habilita vibração
            setSound(
                android.provider.Settings.System.DEFAULT_NOTIFICATION_URI,  // 🔊 Define o som padrão do Android
                audioAttributes  // Especifica os atributos corretamente
            )
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager?.createNotificationChannel(channel)
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

    // **Verifique a permissão antes de exibir a notificação**
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            return // Não exibe a notificação se a permissão não foi concedida
        }
    }

    // Exibir a notificação
    NotificationManagerCompat.from(context).notify(1, notificacao)
}
