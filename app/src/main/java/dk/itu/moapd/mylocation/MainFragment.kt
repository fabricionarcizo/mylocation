package dk.itu.moapd.mylocation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.fragment_main.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MainFragment : Fragment() {

    private var mLongitude = 0.0
    private var mLatitude = 0.0

    private val mPermissions: ArrayList<String> = ArrayList()

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLocationCallback: LocationCallback

    companion object {
        private const val ALL_PERMISSIONS_RESULT = 1011
        private const val UPDATE_INTERVAL = 5000L
        private const val FASTEST_INTERVAL = 5000L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        mPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        mPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        val permissionsToRequest = permissionsToRequest(mPermissions)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (permissionsToRequest.size > 0)
                requestPermissions(
                    permissionsToRequest.toTypedArray(),
                    ALL_PERMISSIONS_RESULT
                )

        mFusedLocationProviderClient = LocationServices
            .getFusedLocationProviderClient(context!!)

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    mLongitude = location.longitude
                    mLatitude = location.latitude
                    longitude.setText(mLongitude.toString())
                    latitude.setText(mLatitude.toString())
                    address.setText(getAddress(mLongitude, mLatitude))
                }
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        longitude.keyListener = null
        latitude.keyListener = null
        address.keyListener = null

        map_button.setOnClickListener {
            val intent = Intent(activity, MapActivity::class.java).apply {
                putExtra("longitude", mLongitude)
                putExtra("latitude", mLatitude)
            }
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun permissionsToRequest(permissions: ArrayList<String>): ArrayList<String> {
        val result: ArrayList<String> = ArrayList()
        for (permission in permissions)
            if (!hasPermission(permission))
                result.add(permission)
        return result
    }

    private fun hasPermission(permission: String) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            activity?.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        else
            true

    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            context!!, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(
            context!!, Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED

    //@SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (checkPermission())
            return

        val locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = UPDATE_INTERVAL
            fastestInterval = FASTEST_INTERVAL
        }

        mFusedLocationProviderClient.requestLocationUpdates(
            locationRequest, mLocationCallback, null
        )
    }

    private fun stopLocationUpdates() {
        mFusedLocationProviderClient
            .removeLocationUpdates(mLocationCallback)
    }

    private fun getAddress(longitude: Double, latitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val stringBuilder = StringBuilder()

        try {
            val addresses: List<Address> =
                geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                stringBuilder.apply{
                    append(address.getAddressLine(0)).append("\n")
                    append(address.locality).append("\n")
                    append(address.postalCode).append("\n")
                    append(address.countryName)
                }
            } else
                return "No address found"

        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        return stringBuilder.toString()
    }

}
