package com.example.maps_harpreet_c0808294;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    Polyline line;
    Polygon shape;
    private Marker homeMarker;
    private Marker destMarker;
    private static final int REQUEST_CODE = 1;
    public static final int POLYGON_SIDES = 4;
    char titlename='A';
    String snip="";
    List<Marker> markers = new ArrayList<>();
    List<LatLng> locations = new ArrayList<>();
    LatLng l1;
    LatLng l2;
    LatLng l3;
    LatLng l4;
    LatLng currentUserLocation;
    double totalDistanceall=0.0;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                setHomeMarker(location);
            }
        };
        if (!isGrantedPermission())
            requestLocationPermission();

        else
            startUpdateLocation();

        //Apply long press
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                String address = getAddress(getApplicationContext(),latLng.latitude,latLng.longitude);
                switch (titlename){
                    case 'A':
                        Toast.makeText(getApplicationContext(), "Address is: "+address, Toast.LENGTH_LONG).show();
                        Double rr = CalculationByDistance(currentUserLocation,latLng);
                        snip="" + rr;
                        l1=latLng;
                        break;
                    case 'B':
                        Toast.makeText(getApplicationContext(), "Address is: "+address, Toast.LENGTH_LONG).show();
                        Double rr1 = CalculationByDistance(currentUserLocation,latLng);
                        snip="" + rr1;
                        l2=latLng;
                        break;
                    case 'C':
                        Toast.makeText(getApplicationContext(), "Address is: "+address, Toast.LENGTH_LONG).show();
                        Double rr2 = CalculationByDistance(currentUserLocation,latLng);
                        snip="" + rr2;
                        l3=latLng;
                        break;
                    case 'D':
                        Toast.makeText(getApplicationContext(), "Address is: "+address, Toast.LENGTH_LONG).show();
                        Double rr3 = CalculationByDistance(currentUserLocation,latLng);
                        snip="" + rr3;
                        l4=latLng;
                        break;

                }
                setMarker(latLng);

                Log.d("Showing values of varible",""+l1+"  "+l2+"  "+l3+"  "+l4);
                if(titlename=='D') {
                    titlename = 'A';
                    snip="";
                }
                else
                titlename++;
            }
        });
    }

    private static String getAddress(Context context, double LATITUDE, double LONGITUDE){
        //Set Address
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            } catch (IOException e) {
                Log.d("catch","cath");
                e.printStackTrace();
            }
        String address="";
            if (addresses != null && addresses.size() > 0) {
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                Log.d("Adress", "getAddress:  address" + address);
                Log.d("City", "getAddress:  city" + city);
                Log.d("State", "getAddress:  state" + state);
                Log.d("PS CODE", "getAddress:  postalCode" + postalCode);
                Log.d("known", "getAddress:  knownName" + knownName);
            }
        return address;
    }

    private void startUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);

        /*Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        setHomeMarker(lastKnownLocation);*/
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }


    private boolean isGrantedPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    private void setHomeMarker(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        currentUserLocation=userLocation;
        MarkerOptions options = new MarkerOptions().position(userLocation)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("Your Location");
        homeMarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
    }

    private void setMarker(LatLng latLng) {
        Log.d("Snipppet",snip);
        MarkerOptions options = new MarkerOptions().position(latLng)
                .title(Character.toString(titlename))
                .snippet(snip+"km from User Loc")
                .icon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_baseline_mark))
                .draggable(true);
     /*   MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("Your Point");
        if (destMarker != null)
            clearMap();
        destMarker = mMap.addMarker(options);
        drawLine();*/
            if (markers.size() == POLYGON_SIDES) {
                clearMap();
                totalDistanceall = 0;
            }
        markers.add(mMap.addMarker(options));
        if (markers.size() == POLYGON_SIDES) {
            calculateDistace();
            drawShape();
        }
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context,int VectorResID){
        Drawable vectorDrawable = ContextCompat.getDrawable(context,VectorResID);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }
    private void drawShape() {
        PolygonOptions options = new PolygonOptions()
                .fillColor(Color.argb(180, 0, 255, 0))
                .strokeColor(Color.RED)
                .clickable(true)
                .strokeWidth(5);
        for (int i = 0; i< POLYGON_SIDES; i++) {
            options.add(markers.get(i).getPosition());
        }
        shape = mMap.addPolygon(options);

        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            public void onPolygonClick(Polygon polygon) {

                    totalDistanceall=Math.floor(totalDistanceall * 100) / 100;
                    Toast.makeText(getApplicationContext(), "Distance B/W all Points: "+totalDistanceall+"km", Toast.LENGTH_LONG).show();


            }
        });

    }
    private void calculateDistace(){
        totalDistanceall += CalculationByDistance(l1,l2);
        Log.d("A-B distance", ""+totalDistanceall);
        totalDistanceall += CalculationByDistance(l2,l3);
        Log.d("B-C distance", ""+totalDistanceall);
        totalDistanceall += CalculationByDistance(l3,l4);
        Log.d("Total Distance of all", ""+totalDistanceall);
    }
    private void clearMap() {
      /*  if (destMarker != null){
            destMarker.remove();
            destMarker = null;
        }
        line.remove();*/
        for (Marker marker: markers)
            marker.remove();
        markers.clear();
        shape.remove();
        shape = null;
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        double KM = Math.floor(km * 100) / 100;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.d("Radius Value", "" + km + "   KM  " + kmInDec
                + " Meter   " + meterInDec);
        return KM;
    }

   /* private void drawLine() {
        PolylineOptions options = new PolylineOptions()
                .color(Color.GREEN)
                .width(10)
                .add(homeMarker.getPosition(),destMarker.getPosition());
        line = mMap.addPolyline(options);
    }*/
}