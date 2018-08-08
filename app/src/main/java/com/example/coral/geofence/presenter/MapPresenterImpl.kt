package com.example.coral.geofence.presenter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.app.ActivityCompat
import com.example.coral.geofence.R
import com.example.coral.geofence.model.GoogleMapClient
import com.example.coral.geofence.model.Constants
import com.example.coral.geofence.service.GeofenceTransitionsIntentService
import com.google.android.gms.common.api.Result
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*


class MapPresenterImpl : ResultCallback<Result> {

    var delegate: MapPresenter
    var context: Context
    var activity: Activity
    private val GEOFENCE_ID = "geocercas"
    private var mCenterMarker: Marker? = null
    private var mCircle: Circle? = null


    constructor(delegate: MapPresenter, activity: Activity, context: Context) {
        this.delegate = delegate
        this.context = context
        this.activity = activity
    }

    fun initMap() {
        delegate.initMap()
    }

    fun connectClient() {
        GoogleMapClient.createGoogleApiClient(context)
        initMap()
    }

    fun setMarks(mMap: GoogleMap) {
        val mMarkers: MutableList<Marker> = mutableListOf()
        Constants.map.forEach { (key, value) ->
            mMarkers.add(mMap.addMarker(MarkerOptions().position(value).title(key).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))))
        }
        paintCircle(mMap)
    }

    private fun paintCircle(mMap: GoogleMap) {
        if (mCircle != null) {
            mCircle!!.remove()
        }
        mCircle = mMap.addCircle(cirfence)

        if (mCenterMarker != null) {
            mCenterMarker!!.remove()
        }

        mCenterMarker = mMap.addMarker(MarkerOptions().position(mCircle!!.center))
    }

     fun refreshGeofences() {
        removeGeofences()
        val geofences = mutableListOf<Geofence>()
        val geofence = Geofence.Builder()
                .setRequestId(GEOFENCE_ID)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setCircularRegion(mCircle!!.center.latitude, mCircle!!.center.longitude,
                        mCircle!!.radius.toFloat())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT)
                .setLoiteringDelay(1000)
                .build()
        geofences.add(geofence)
        addGeofences(geofences)
    }

    private fun addGeofences(geofenceList: List<Geofence>) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationServices.GeofencingApi.addGeofences(
                    GoogleMapClient.mGoogleApiClient,
                    createGeofenceRequest(geofenceList),
                    GeofenceTransitionsIntentService.newPendingIntent(context)
            ).setResultCallback(this)
        } else {
            ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    Constants.REQUEST_PERMISSION_LOCATION)
        }
    }

    private fun removeGeofences() {
        val geofences = mutableListOf<String>()
        geofences.add(GEOFENCE_ID)
        LocationServices.GeofencingApi.removeGeofences(GoogleMapClient.mGoogleApiClient, geofences)
    }

    private fun createGeofenceRequest(geofenceList: List<Geofence>): GeofencingRequest {

        return GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)
                .addGeofences(geofenceList)
                .build()
    }

    fun getLatLng(): MutableList<LatLng> {
        val points = mutableListOf<LatLng>()
        Constants.map.forEach { (key, value) ->
            points.add(value)
        }
        return points
    }

    val cirfence: CircleOptions
        get() {
            var latMin = 180.0
            var latMax = -180.0
            var longMin = 180.0
            var longMax = -180.0

            Constants.map.forEach { (key, value) ->
                if (value.latitude < latMin) {
                    latMin = value.latitude
                }
                if (value.latitude > latMax) {
                    latMax = value.latitude
                }
                if (value.longitude < longMin) {
                    longMin = value.longitude
                }
                if (value.longitude > longMax) {
                    longMax = value.longitude
                }
            }
            val latMid = (latMin + latMax) / 2
            val longMid = (longMin + longMax) / 2
            val result = FloatArray(1)
            Location.distanceBetween(latMid, longMid, latMax, longMax, result)
            val radius = result[0].toDouble()
            return CircleOptions()
                    .center(LatLng(latMid, longMid))
                    .radius(radius)
                    .fillColor(context.resources.getColor(R.color.fill))
                    .strokeColor(context.resources.getColor(R.color.stroke))
        }

    override fun onResult(p0: Result) {
    }
}