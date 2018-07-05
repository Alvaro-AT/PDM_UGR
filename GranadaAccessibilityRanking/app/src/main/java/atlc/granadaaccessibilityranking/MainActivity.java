package atlc.granadaaccessibilityranking;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener, OnMapReadyCallback {
    //Clase con los datos que vamos a mostrar en los infowindow
    private class myModel {
        public double rate;
        public String place_name;
        public String category;
        public int num_opiniones;
    }

    private GoogleMap mMap;
    DBReader mDbHelper = null;
    private HashMap<Marker, myModel> markerMap;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mDbHelper = new DBReader(getBaseContext());
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
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
        markerMap = new HashMap<>();
        mMap = googleMap;
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Intent intent = new Intent(getBaseContext(), AddNewPlace.class);
                intent.putExtra("LATITUDE", latLng.latitude);
                intent.putExtra("LONGITUDE", latLng.longitude);
                startActivity(intent);
                finish();
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {

                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.info_window, null);

                String id = marker.getId();

                RatingBar ratingBar = (RatingBar) v.findViewById(R.id.ratingBar3);
                ratingBar.setRating((float) markerMap.get(marker).rate);
                TextView place_name = (TextView) v.findViewById(R.id.place_name);
                place_name.setText(markerMap.get(marker).place_name);
                TextView category = (TextView) v.findViewById(R.id.category);
                category.setText(markerMap.get(marker).category);
                TextView num_opiniones = (TextView) v.findViewById(R.id.num_opiniones);
                num_opiniones.setText(Integer.toString(markerMap.get(marker).num_opiniones) + " opiniones");

                return v;
            }
        });

        enableMyLocation();
        getMarkers();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);

        String provider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        try {
            Location location = locationManager.getLastKnownLocation(provider);
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
        } catch (Exception ex) {
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getMarkers();
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap.setMyLocationEnabled(true);
    }

    private void getMarkers() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                "place_name",
                "place_lat",
                "place_lon",
                "categoria"
        };

        Cursor c = db.query(
                "SITIOS",                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        while (c.moveToNext()) {
            LatLng pos = new LatLng(c.getDouble(c.getColumnIndexOrThrow("place_lat")), c.getDouble(c.getColumnIndexOrThrow("place_lon")));
            String name = c.getString(c.getColumnIndexOrThrow("place_name"));
            name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            String categoria = c.getString(c.getColumnIndexOrThrow("categoria"));
            double nota = mDbHelper.getMeanMark(name.toLowerCase());
            int num_opiniones = mDbHelper.getNumRows(name.toLowerCase());
            myModel model = new myModel();
            model.category = categoria;
            model.num_opiniones = num_opiniones;
            model.rate = nota;
            model.place_name = name;

            MarkerOptions marker = new MarkerOptions().position(pos).title(name);

            Marker m = mMap.addMarker(marker);
            markerMap.put(m, model);
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    String place_name = marker.getTitle();
                    Intent intent = new Intent(getBaseContext(), OpinionActivity.class);
                    intent.putExtra("NAME", place_name);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_UP) {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    criteria.setAltitudeRequired(false);
                    criteria.setBearingRequired(false);
                    criteria.setCostAllowed(true);
                    criteria.setPowerRequirement(Criteria.POWER_MEDIUM);

                    String provider = locationManager.getBestProvider(criteria, true);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return super.dispatchKeyEvent(event);
                    }
                    Location location = locationManager.getLastKnownLocation(provider);
                    Intent intent = new Intent(this, BlackActivity.class);
                    intent.putExtra("LATITUDE", location.getLatitude());
                    intent.putExtra("LONGITUDE", location.getLongitude());
                    startActivity(intent);
                }
        }
        return super.dispatchKeyEvent(event);
    }
}
