package kr.ac.dongyang.cs.myproject_android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class RecordSection extends AppCompatActivity implements OnMapReadyCallback {
    static boolean menu = false;    //false일 때 기록보기, true일 때 구간보기
    public static Handler mHandler;
    ProgressBar pb; //목표 대비 걸음수 표시할 progress bar
    ImageView ivRecord, ivSection;
    LinearLayout liRecord, liSection;
    TextView tvWalk, tvDistance, tvKcalories, tvSDistance;

    public GoogleMap mGoogleMap;
    static double latitude;
    static double longitude;
    public static ArrayList<LatLng> LOC;
    public static int ForLOC = 0;
    public static float distance = 0;

    int maxWalk = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_section);

        ivRecord = (ImageView) findViewById(R.id.ivRecord);
        ivSection = (ImageView) findViewById(R.id.ivSection);
        liRecord = (LinearLayout) findViewById(R.id.liRecord);
        liSection = (LinearLayout) findViewById(R.id.liSection);
        tvWalk = (TextView) findViewById(R.id.tvWalk);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvSDistance = (TextView) findViewById(R.id.tvSDistance);
        tvKcalories = (TextView) findViewById(R.id.tvKcalories);

        pb = (ProgressBar) findViewById(R.id.progressBar);
        LOC = new ArrayList<LatLng>();


        //Handler
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    tvWalk.setText(StepValues.Step + "/" + maxWalk);
                    pb.setProgress(StepValues.Step);
                    pb.setMax(6000);
                    tvDistance.setText(String.format("%.2f", StepValues.Step * 60*0.00001) + "km");
                    mHandler.sendEmptyMessageDelayed(10, 60);
                } catch (Exception e) {
                }
            }
        };
        mHandler.sendEmptyMessageDelayed(10, 0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }//onCreate

    //옵션
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addmenu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionDistance:
                break;
            case R.id.actionWhatIEat:
                Intent it = new Intent(getApplicationContext(), Food.class);
                startActivity(it);
                finish();
                break;
            case R.id.actionPark:
                it = new Intent(getApplicationContext(), PMapsActivity.class);
                startActivity(it);
                break;
            case R.id.actionEtc:
                it = new Intent(getApplicationContext(), Etc.class);
                startActivity(it);
                break;
            case R.id.actionHome:
                it = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(it);
                break;
        }
        return true;
    }


    //MAP관련 OnMapReadyCallback의 추상메소드 오버라이딩
    //서울로 초기위치 지정함
    @Override
    public void onMapReady(final GoogleMap map) {
        mGoogleMap = map;

        if (latitude == 0 && longitude == 0) {
            latitude = 37.56;
            longitude = 126.97;
            LatLng seoul = new LatLng(latitude, longitude);
            Marker Seoul = map.addMarker(new MarkerOptions().position(seoul).title("Marker in Seoul"));
            map.moveCamera(CameraUpdateFactory.newLatLng(seoul));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            mGoogleMap.animateCamera(zoom);
        } else {
        }

        //시스템위치서비스에 접근
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //위치가 변할 때 LocationManager로부터 공지받고 각 메소드 실행하는 LocationListener객체생성
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //위치 변할 때 자동으로 호출
                updateMap(location, map);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {//provider의 상태가 변할 때 자동으로 호출
                alertStatus(provider);
            }

            @Override
            public void onProviderEnabled(String provider) {//사용자에 의해 provider가 사용가능하게 될 때 자동으로 호출
                alertProvider(provider);
            }

            @Override
            public void onProviderDisabled(String provider) {// 사용자에 의해 provider가 사용 불가능하게 될 때 자동으로 호출
                checkProvider(provider);
            }
        };

        String locationProvider = LocationManager.NETWORK_PROVIDER;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    }

    public void updateMap(final Location location, GoogleMap map) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng nowLOC = new LatLng(latitude, longitude);

        map.clear();
        Marker mk = map.addMarker(new MarkerOptions().position(nowLOC).title("현재위치"));
        mk.showInfoWindow();
        //CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        // map.animateCamera(zoom);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(nowLOC, 15));

        LOC.add(nowLOC);
        ForLOC++;
        for (int i = 0; i < ForLOC - 1; i++) {
            mGoogleMap.addPolyline(new PolylineOptions().add(LOC.get(i), LOC.get(i + 1)).width(5).color(Color.RED));
            Location A = new Location("A");
            A.setLatitude(LOC.get(i).latitude);
            A.setLongitude(LOC.get(i).longitude);
            Location B = new Location("B");
            B.setLatitude(LOC.get(i + 1).latitude);
            B.setLongitude(LOC.get(i + 1).longitude);
            distance += A.distanceTo(B);
        }
        tvSDistance.setText(String.format("%.2f", distance * 0.001) + "km");

    }

    public void checkProvider(String provider) {
        Toast.makeText(this, provider + "에 의한 위치 서비스가 꺼져있습니다. 켜주세요", Toast.LENGTH_SHORT).show();
        Intent it = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(it);
    }

    public void alertProvider(String provider) {
        Toast.makeText(this, provider + "서비스가 켜졌습니다.", Toast.LENGTH_SHORT).show();
    }

    public void alertStatus(String provider) {
    }

    public void showRecord(View v) {
        menu = false;
        if (!menu) {
            ivRecord.setImageResource(R.drawable.record_2);
            ivSection.setImageResource(R.drawable.section_1);
            liRecord.setVisibility(View.VISIBLE);
            liSection.setVisibility(View.GONE);
        }
    }

    //구간보기 터치했을 때
    public void showSection(View v) {
        menu = true;
        if (menu) {
            ivSection.setImageResource(R.drawable.section_2);
            ivRecord.setImageResource(R.drawable.record_1);
            liRecord.setVisibility(View.GONE);
            liSection.setVisibility(View.VISIBLE);
        }
    }
}


