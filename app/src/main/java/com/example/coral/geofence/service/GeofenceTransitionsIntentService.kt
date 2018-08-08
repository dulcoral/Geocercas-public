package com.example.coral.geofence.service

import android.app.IntentService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.example.coral.geofence.R
import com.example.coral.geofence.model.Constants
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent


class GeofenceTransitionsIntentService : IntentService(TAG) {

    override fun onHandleIntent(intent: Intent) {

        val event = GeofencingEvent.fromIntent(intent)
        if (event.hasError()) {
            Log.e(TAG, "Error code: " + event.errorCode)
            return
        }

        intent.setClass(this, LocationUpdateService::class.java)
        val transition = event.geofenceTransition
        when (transition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                showNotification(resources.getString(R.string.enter), Constants.NOTIFICATION_ENTER)
                startService(intent)
            }

            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                startService(intent)
                showNotification(resources.getString(R.string.dwells), Constants.NOTIFICATION_DWELL)
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                showNotification(resources.getString(R.string.exit), Constants.NOTIFICATION_EXIT)
                stopService(intent)
            }

            else -> {
            }
        }
    }

    private fun showNotification(message: String, notifId: Int) {
        val builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(notifId, builder.build())
    }

    companion object {
        private val TAG = GeofenceTransitionsIntentService::class.java.simpleName

        fun newPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, GeofenceTransitionsIntentService::class.java)
            return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }


    }
}