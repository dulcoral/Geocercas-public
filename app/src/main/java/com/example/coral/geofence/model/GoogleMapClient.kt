package com.example.coral.geofence.model

import android.content.Context
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices

object GoogleMapClient : GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    var mGoogleApiClient: GoogleApiClient? = null

    fun createGoogleApiClient(context: Context): GoogleApiClient? {
        mGoogleApiClient = GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        connected()
        return  mGoogleApiClient
    }

    fun connected() {
        mGoogleApiClient!!.connect()
    }

    fun disconect() {
        mGoogleApiClient!!.disconnect()
    }

    override fun onConnected(bundle: Bundle?) {
    }

    override fun onConnectionSuspended(cause: Int) {
        mGoogleApiClient!!.connect()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
    }

}