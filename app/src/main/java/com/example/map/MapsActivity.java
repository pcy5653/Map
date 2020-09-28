package com.example.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;



public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = ;
    private GoogleMap mMap;
    private Geocoder geocoder; //좌표값 얻기위해
    private static final int REQUEST_CODE_PERMISSIONS = 1000;
    private static final int REQUESTING_LOCATION_UPDATES_KEY = 1000;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean requestingLocationUpdates;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    Places.initialize(

    getApplicationContext(),AIzaSyDqH0Z0yIN3tgQX2wFEarUiLLLTVuqPlYg);
    PlacesClient placesClient = Places.createClient(this); //장소 API클라이언트 초기화 하는 중인데 초기화 문제인지 해결하기가 어렵습니다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(37.4872361, 126.818354);
        mMap.addMarker(new MarkerOptions().position(sydney).title("유한대학교"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(Intent.ACTION_DIAL);

            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_PERMISSIONS); //권한 요청 물어보기
            return;
        }
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {  //버튼을 눌렀을 때 마지막 위치 정보 제공
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng mylocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions()
                            .position(mylocation)
                            .title("현재 위치"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

                }
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS:
                ;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "권한 체크 거부 됨", Toast.LENGTH_SHORT).show();
        }
    }

    // 여기부터 updateUI(); 코딩 까지는 현재 위치를 지속적으로 얻는 코딩입니다.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {

                }
            }
        };

    }


    protected void LocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(String.valueOf(REQUESTING_LOCATION_UPDATES_KEY),
                requestingLocationUpdates);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        updateValuesFromBundle(savedInstanceState);

    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            requestingLocationUpdates = savedInstanceState.getBoolean(
                    String.valueOf(REQUESTING_LOCATION_UPDATES_KEY));
        }
        updateUI();  //복원된 상태와 일치하도록 UI 업데이트 하라는 지문인데 오류가 떠서 어떻게 해결해야 할지 모르겠습니다.


    }

    AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
            getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

    autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.NAME));
    
    autocompleteFragment.setOnPlaceSelectedListener(new

    public void PlaceSelectionListener() {
        @Override
        public void onPlaceSelected (@NotNull Place place){
            // TODO: Get info about the selected place.
            Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
        }


        @Override
        public void onError (@NotNull Status status){
            // TODO: Handle the error.
            Log.i(TAG, "An error occurred: " + status);
        }
    });

    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

    Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .build(this);

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

    RectangularBounds bounds = RectangularBounds.newInstance(
            new LatLng(37.715133, 126.734086),
            new LatLng(37.413294, 127.269311)); //서울 위도 경도

    FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
            .setLocationBias(bounds)
            .setOrigin(new LatLng(37.4872361, 126.818354))  //대학교위치
            .setTypeFilter(TypeFilter.ADDRESS)
            .build();

    public PlacesClient getPlacesClient() {  //따로 무슨 설정을 해야하는지 모르겠습니다. 첫줄 addOnSuccessListener 하고 응답 작성할 때부터 오류가 이어집니다.

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                Log.i(TAG, prediction.getPlaceId());
                Log.i(TAG, prediction.getPrimaryText(null).toString());
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });
    }
}