package com.killjestkill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Integer maxSpeed = 0;
    public void setMaxSpeed(Integer maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
    public Integer getMaxSpeed() {
        return maxSpeed;
    }

    Integer speed = 0;
    public void setSpeed(Integer speed) {
        this.speed = speed;
    }
    public Integer getSpeed() {
        return speed;
    }

    Long zeroKmhTimestamp = 0L;
    public void setZeroKmhTimestamp(Long zeroKmhTimestamp) {
        this.zeroKmhTimestamp = zeroKmhTimestamp;
    }
    public long getZeroKmhTimestamp() {
        return zeroKmhTimestamp;
    }

    Long oneHundredKmhTimestamp = 0L;
    public void setOneHundredKmhTimestamp(Long oneHundredKmhTimestamp) {
        this.oneHundredKmhTimestamp = oneHundredKmhTimestamp;
    }
    public Long getOneHundredKmhTimestamp() {
        return oneHundredKmhTimestamp;
    }

    boolean fromZeroToOneHundredMeasure = false;
    public void setFromZeroToOneHundredMeasure(boolean fromZeroToOneHundredMeasure) {
        this.fromZeroToOneHundredMeasure = fromZeroToOneHundredMeasure;
    }
    public boolean isFromZeroToOneHundredMeasure() {
        return fromZeroToOneHundredMeasure;
    }

    boolean fromOneHundredToTwoHundred = false;
    public void setFromOneHundredToTwoHundred(boolean fromOneHundredToTwoHundred) {
        this.fromOneHundredToTwoHundred = fromOneHundredToTwoHundred;
    }
    public boolean isFromOneHundredToTwoHundred() {
        return fromOneHundredToTwoHundred;
    }

    public boolean stopped = false;
    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
    public boolean isStopped() {
        return stopped;
    }

    TextView textSpeed;
    TextView textTopSpeed;
    TextView textFromZero;
    TextView textFromOneHundred;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        textSpeed = findViewById(R.id.speed);
        textTopSpeed = findViewById(R.id.topspeed);
        textFromZero = findViewById(R.id.from0);
        textFromOneHundred = findViewById(R.id.from100);

        makeNotificationChannel();
        addListenerLocation();

        Button startButton = (Button)findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFromZeroToOneHundredMeasure(true);
                textFromZero.setText(getResources().getString(R.string.measure_stop));
                textFromOneHundred.setText(getResources().getString(R.string.measure_stop));
            }
        });

        Button resetButton = (Button)findViewById(R.id.reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMaxSpeed(0);
                textTopSpeed.setText(getResources().getString(R.string.top_speed)+ " " + speed + "km/h");
            }
        });

    }

    public double fromMilisToSecs(Long milis){
        return (double) milis / 1000;
    }

    private void showNotification(String title, String text, Integer id){
        NotificationCompat.Builder ncBuilder = new NotificationCompat.Builder(this, getResources().getString(R.string.CHANNEL_ID_SPEED))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat nmc = NotificationManagerCompat.from(this);
        nmc.notify(id, ncBuilder.build());
    }

    private void makeNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(getResources().getString(R.string.CHANNEL_ID_SPEED), getResources().getString(R.string.CHANNEL_NAME_SPEED), NotificationManager.IMPORTANCE_HIGH);
            nc.setDescription(getResources().getString(R.string.CHANNEL_DESC_SPEED));
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(nc);
        }
    }

    private void addListenerLocation() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener ll = new LocationListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationChanged(@NonNull Location location) {
                int speed = (int) (location.getSpeed() * 3.6);
                textSpeed.setText(speed + "km/h");
                if((getSpeed() < 500) && (speed > 500)){
                    showNotification(getResources().getString(R.string.speed_over_500_title), getResources().getString(R.string.speed_over_500_text), 1);
                }
                else if((getSpeed() < 100) && (speed > 100)){
                    showNotification(getResources().getString(R.string.speed_over_100_title), getResources().getString(R.string.speed_over_100_text), 1);
                }
                else if((getSpeed() < 90) && (speed > 90)){
                    showNotification(getResources().getString(R.string.speed_over_90_title), getResources().getString(R.string.speed_over_90_text), 1);
                }
                else if((getSpeed() < 70) && (speed > 70)){
                    showNotification(getResources().getString(R.string.speed_over_70_title), getResources().getString(R.string.speed_over_70_text), 1);
                }
                else if((getSpeed() < 50) && (speed > 50)){
                    showNotification(getResources().getString(R.string.speed_over_50_title), getResources().getString(R.string.speed_over_50_text), 1);
                }
                if(speed > getMaxSpeed()){
                    setMaxSpeed(speed);
                    textTopSpeed.setText(getResources().getString(R.string.top_speed)+ " " + speed + "km/h");
                }
                setSpeed(speed);

                if (speed == 0 && isFromZeroToOneHundredMeasure()){
                    setZeroKmhTimestamp(System.currentTimeMillis());
                    textFromZero.setText(getResources().getString(R.string.measure_go));
                    textFromOneHundred.setText(getResources().getString(R.string.measure_waiting));
                    setStopped(true);
                }
                else if (speed >= 100 && isFromZeroToOneHundredMeasure() && isStopped()){
                    setOneHundredKmhTimestamp(System.currentTimeMillis());
                    setFromZeroToOneHundredMeasure(false);
                    setFromOneHundredToTwoHundred(true);
                    setStopped(false);
                    textFromZero.setText(String.valueOf(fromMilisToSecs(getOneHundredKmhTimestamp()- getZeroKmhTimestamp())) + "s");
                    textFromOneHundred.setText(getResources().getString(R.string.measure_go));
                }
                else if (speed >= 200 && isFromOneHundredToTwoHundred()){
                    setFromOneHundredToTwoHundred(false);
                    textFromOneHundred.setText(String.valueOf(fromMilisToSecs(System.currentTimeMillis() - getOneHundredKmhTimestamp())) + "s");
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Toast.makeText(MainActivity.this, R.string.status_changed, Toast.LENGTH_SHORT).show();
            }

            public void onProviderEnabled(String provider) {
                Toast.makeText(MainActivity.this, R.string.turned_location_on, Toast.LENGTH_SHORT).show();
            }

            public void onProviderDisabled(String provider) {
                Toast.makeText(MainActivity.this, R.string.turn_on_location, Toast.LENGTH_SHORT).show();
                textSpeed.setText(R.string.welcome_message);
            }

        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        else {
            Toast.makeText(MainActivity.this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
            } else {
                Toast.makeText(MainActivity.this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }
}