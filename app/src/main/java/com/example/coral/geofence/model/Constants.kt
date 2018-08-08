package com.example.coral.geofence.model

import com.google.android.gms.maps.model.LatLng

object Constants {
    val map: MutableMap<String, LatLng> = mutableMapOf(
            "Urbvan" to LatLng(19.409526, -99.163399),
            "Metrobus Campeche" to LatLng(19.409516, -99.167385),
            "Metrobus Sonora" to LatLng(19.412997, -99.166258),
            "Café Cucurucho" to LatLng(19.412481, -99.161435),
            "Metro Centro Médico" to LatLng(19.407690, -99.155421))

    val REQUEST_PERMISSION_LOCATION = 1234
    val NOTIFICATION_ENTER = 111
    val NOTIFICATION_EXIT = 222
    val NOTIFICATION_DWELL = 333


}