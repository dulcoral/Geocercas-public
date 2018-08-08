package com.example.coral.geofence.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices


class LocationUpdateService : Service(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private lateinit var mGoogleApiClient: GoogleApiClient

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mGoogleApiClient = createGoogleApiClient()
        mGoogleApiClient.connect()

        return Service.START_STICKY
    }

    override fun onConnected(bundle: Bundle?) {
        if (ActivityCompat.checkSelfPermission(baseContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        LocationServices.getFusedLocationProviderClient(baseContext).requestLocationUpdates(createLocationRequest(), null)
    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mGoogleApiClient.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
        }
    }

    override fun onLocationChanged(location: Location) {
        val msg = ""
        val inboxStyle = NotificationCompat.InboxStyle()
        inboxStyle.setBigContentTitle(msg)
        inboxStyle.addLine("Accuracy: " + location.accuracy)
        inboxStyle.addLine("Latitude: " + location.latitude)
        inboxStyle.addLine("Longitude: " + location.longitude)
    }

    private fun createGoogleApiClient(): GoogleApiClient {
        return GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
    }

    private fun createLocationRequest(): LocationRequest {
        val request = LocationRequest()
        request.interval = 1000
        request.fastestInterval = 2000
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        return request
    }
}
