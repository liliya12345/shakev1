package com.example.shakev1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button btn;
    private SensorManager sensorManager;
    private Sensor sensor;
    private Sensor sensor2;
    private Sensor sensor3;
    private TextView x;
    private TextView y;
    private TextView z;

    private SensorEventListener listener;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor =sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor2 =sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensor3 =sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        x= findViewById(R.id.x);
        y= findViewById(R.id.y);
        z= findViewById(R.id.z);
        listener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                switch(event.sensor.getType()){
                    case Sensor.TYPE_ACCELEROMETER:
                        Log.i("TYPE_ACCELEROMETER", "x"+event.values[0]+" y:"+event.values[1]+"z:"+event.values[2]);
                        showDiagram(event.values[0],event.values[1],event.values[2]);
                        break;

                    case Sensor.TYPE_GYROSCOPE:
                        Log.i("TYPE_GYROSCOPE", "x"+event.values[0]+" y:"+event.values[1]+"z:"+event.values[2]);
                        break;
                    case Sensor.TYPE_RELATIVE_HUMIDITY:
                        Log.i("TYPE_RELATIVE_HUMIDITY", "x"+event.values[0]);
                        break;
                }


            }
        };
        sensorManager.registerListener(listener,sensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener,sensor2,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener,sensor3,SensorManager.SENSOR_DELAY_FASTEST);




    }

    public void showDiagram(float x, float y, float z){
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(6, x));
        entries.add(new BarEntry(4, y));
        entries.add(new BarEntry(2, z));

        BarDataSet dataset = new BarDataSet(entries, "# of Calls");
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("x");
        labels.add("y");
        labels.add("z");

        BarChart chart = new BarChart(this);
        setContentView(chart);
        BarData data = new BarData(dataset);
        chart.setData(data);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);

    }
}