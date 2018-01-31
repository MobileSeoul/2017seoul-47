package kr.ac.dongyang.cs.myproject_android;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ToggleButton toggle1;
    EditText edtPosition,edtPark,edtDistance;
    final Geocoder geocoder = new Geocoder(this, Locale.KOREA);
    double longitude,latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pmaps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);

        toggle1 = (ToggleButton)findViewById(R.id.toggle1);
        edtPosition = (EditText)findViewById(R.id.edtPosition);
        edtPark = (EditText)findViewById(R.id.edtPark);
        edtDistance = (EditText)findViewById(R.id.edtDistance);


        String serviceUrl = "http://openAPI.seoul.go.kr:8088/655750784f616b653131396249527461/xml/SearchParkInformationByAddressService/1/100";

        new DownloadWebpageTask().execute(serviceUrl);

    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String>{
        @Override
        protected void onPostExecute(String result) {
            displayPark(result);
        }

        @Override
        protected String doInBackground(String... urls) {
            try{
                return (String)downloadUrl((String)urls[0]);
            }catch (IOException e){
                return "다운로드 실패";
            }
        }
    }

    /*protected void onPostExecute(String result){
        displayPark(result);
    }*/

    private void displayPark(String result){
        ArrayList<park> plist = new ArrayList<park>();
        String xpos = null, ypos = null, name = null;
        String tag = null;

        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(result));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT){
                if (eventType == XmlPullParser.START_TAG){
                    tag = xpp.getName();
                }else if (eventType == XmlPullParser.TEXT){
                    if(tag.equals("LATITUDE")){
                        xpos = xpp.getText();
                    }
                    else if (tag.equals("LONGITUDE")){
                        ypos = xpp.getText();
                    }
                    else if (tag.equals("P_PARK")) {
                        name = xpp.getText();
                    }
                }else if (eventType == XmlPullParser.END_TAG){
                    tag = xpp.getName();
                    if (tag.equals("row")){
                        park entity = new park();
                        entity.setXpos(Double.valueOf(xpos));
                        entity.setYpos(Double.valueOf(ypos));
                        entity.setName(name);

                        String parkName = entity.getName();

                        plist.add(entity);

                        displayPark(entity.getXpos(),entity.getYpos(),parkName);
                    }
                }
                eventType = xpp.next();
            }


        }catch (Exception e){

        }
    }

    private void displayPark(final Double X, final Double Y, final String pname){
        final String name = pname;
        final LatLng pLOC = new LatLng(X,Y);
        final Marker pmk = mMap.addMarker(new MarkerOptions()
                .position(pLOC)
                .title(name)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.park)));
        pmk.showInfoWindow();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                edtPark.setText(marker.getTitle());

                double d = Distance(latitude,longitude,marker.getPosition().latitude,marker.getPosition().longitude);

                edtDistance.setText(String.valueOf(d)+"m");
                return false;

            }
        });
    }

    public double Distance(Double latitude1,Double longitude1,Double latitude2,Double longitude2){
        Location startPos = new Location("A");
        Location endPos = new Location("B");

        startPos.setLatitude(latitude1);
        startPos.setLongitude(longitude1);
        endPos.setLatitude(latitude2);
        endPos.setLongitude(longitude2);

        double distance = startPos.distanceTo(endPos);
        double rdistance = Math.round(distance*1000)/1000.0;

        return rdistance;
    }



    private String downloadUrl(String myurl) throws IOException{
        HttpURLConnection conn = null;
        try{
            URL url = new URL(myurl);
            conn = (HttpURLConnection)url.openConnection();
            BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf,"utf-8"));
            String line = null;
            String page = "";
            while ((line = bufreader.readLine()) != null){
                page += line;
            }
            return page;
        } finally {
            conn.disconnect();
        }
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

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.500541,126.867595),15));

        final LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);


        toggle1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(toggle1.isChecked()){
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,1,mLocationListener);
                        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,100,1,mLocationListener);

                    }else {
                        lm.removeUpdates(mLocationListener);
                    }
                }catch (SecurityException ex){}
            }
        });

    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            LatLng LOC;
            //edtPosition.setText(latitude+","+longitude);
            List<Address> list = null;
            try{
                list = geocoder.getFromLocation(latitude,longitude,1);
            }catch (IOException e){

            }



            if(list != null){
                if(list.size()==0){
                    edtPosition.setText(latitude+","+longitude);
                }
                else{
                    edtPosition.setText(list.get(0).getAddressLine(0).toString());
                }

                LOC = new LatLng(latitude,longitude);
                Marker mk = mMap.addMarker(new MarkerOptions().position(LOC));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LOC,15));
                mk.showInfoWindow();

            }


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            alertStatus(provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            alertProvider(provider);
        }

        @Override
        public void onProviderDisabled(String provider) {

            checkProvider(provider);

        }
    };



    public void checkProvider(String provider){
        Toast.makeText(this,provider+"에 의한 위치서비스가 꺼져 있습니다 켜주세요~",Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    public void  alertProvider(String provider){
        Toast.makeText(this,provider+"서비스가 켜졌습니다",Toast.LENGTH_SHORT).show();
    }

    public void alertStatus(String provider){
        Toast.makeText(this,"위치서비스가"+provider+"로 변경되었습니다",Toast.LENGTH_SHORT).show();
    }



}
