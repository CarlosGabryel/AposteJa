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

fun mostrarNotificacao(context: Context, titulo: String, mensagem: String) {
    val canalId = "canal_login"

    // Criar canal de notificação (somente para Android 8+)
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
