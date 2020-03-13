package dk.itu.moapd.mylocation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        assert(arguments != null)
        val longitude = arguments!!.getDouble("longitude")
        val latitude = arguments!!.getDouble("latitude")

        val mapFragment = SupportMapFragment.newInstance()
        mapFragment.getMapAsync { googleMap ->
            val latLng = LatLng(latitude, longitude)
            googleMap.apply {
                addMarker(MarkerOptions().position(latLng)
                    .title("Current Location"))
                mapType = GoogleMap.MAP_TYPE_NORMAL
                animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
            }
        }

        childFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_map, mapFragment)
            .commit()

        return view
    }

}
