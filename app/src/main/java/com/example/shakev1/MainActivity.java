package com.example.shakev1;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.hardware.Sensor.TYPE_GYROSCOPE;
import static java.lang.Math.clamp;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button btn;
    private boolean isToastShowing = false;
    private Handler handler = new Handler();
    private SensorManager sensorManager;
    private Sensor sensor, sensor2; // Accelerometer och gyroskop sensorer
    private ImageView img;
    private TextView legendX;
    private TextView legendZ;
    private TextView legendY;
    private Switch switch1;
    private Switch switch2;
    private Button button;
    private boolean i;
    private TextView accelerate;
    private float x, y, z;
    private SensorEventListener listener;
    private Toast toast;
    private BarChart chart;
    private ImageView stopSensor;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initiera sensorhanterare och sensorer
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(TYPE_ACCELEROMETER);
        sensor2 = sensorManager.getDefaultSensor(TYPE_GYROSCOPE);

        // Hitta vyer från layout
        img = findViewById(R.id.img);
        chart = findViewById(R.id.chart);
        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        btn = findViewById(R.id.button2);
        stopSensor = findViewById(R.id.stopsnr);

        // Skapa sensorlyssnare
        listener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
                // Krävs av interfacet men används inte
            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                // Hantera sensorändringar
                showDiagram(event.values[0], event.values[1], event.values[2]);
                colorConverter(event.values[0], event.values[1], event.values[2]);
                rotateImage(event.values[0], event.values[1]);

                x = event.values[0];
                y = event.values[1];
                z = event.values[2];

                Log.i("isToastShowing", "showDizzyToast: " + isToastShowing);

                // Visa "dizzy" toast om värdena är låga och ingen toast visas
                if ((x < -8 || y < -8 || z < -8) && !isToastShowing) {
                    showDizzyToast();
                }
            }
        };

        // Klicklyssnare för att stoppa/starta sensor
        stopSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i) {
                    // Starta accelerometer
                    sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    chart.setVisibility(chart.VISIBLE);
                    switch1.setChecked(true);
                    switch2.setChecked(false);
                    i = false;
                } else {
                    // Stoppa sensor
                    sensorManager.unregisterListener(listener);
                    chart.setVisibility(chart.INVISIBLE);
                    switch1.setChecked(false);
                    switch2.setChecked(false);
                    i = true;
                }
            }
        });

        // Klicklyssnare för att byta mellan sensorer
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i) {
                    // Byt till accelerometer
                    sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    chart.setVisibility(chart.VISIBLE);
                    switch1.setChecked(true);
                    switch2.setChecked(false);
                    i = false;
                } else {
                    // Byt till gyroskop
                    sensorManager.registerListener(listener, sensor2, SensorManager.SENSOR_DELAY_NORMAL);
                    chart.setVisibility(chart.VISIBLE);
                    switch1.setChecked(false);
                    switch2.setChecked(true);
                    i = true;
                }
            }
        });

        // Switch för accelerometer
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switch2.setChecked(false);
                    // Aktivera accelerometer
                    if (sensor != null) {
                        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                        chart.setVisibility(chart.VISIBLE);
                    }
                } else {
                    // Inaktivera sensor och rensa data
                    sensorManager.unregisterListener(listener);
                    chart.setVisibility(chart.INVISIBLE);
                    switch1.setChecked(false);
                    chart.clear();
                    chart.invalidate();
                    img.setRotation(0f);
                    legendX.setText("X: 0.00");
                    legendY.setText("Y: 0.00");
                    legendZ.setText("Z: 0.00");
                }
            }
        });

        // Switch för gyroskop
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switch1.setChecked(false);
                    // Aktivera gyroskop
                    if (sensor2 != null) {
                        sensorManager.registerListener(listener, sensor2, SensorManager.SENSOR_DELAY_NORMAL);
                        chart.setVisibility(chart.VISIBLE);
                    }
                } else {
                    // Inaktivera sensor och rensa data
                    sensorManager.unregisterListener(listener);
                    chart.setVisibility(chart.INVISIBLE);
                    switch2.setChecked(false);
                    chart.clear();
                    chart.invalidate();
                    img.setRotation(0f);
                    legendX.setText("X: 0.00");
                    legendY.setText("Y: 0.00");
                    legendZ.setText("Z: 0.00");
                }
            }
        });
    }

    // Visa "dizzy" toast med slumpmässigt meddelande
    private void showDizzyToast() {
        String[] list = {"I feel dizzy", "My head is spinning", "Feeling woozy", "Getting dizzy",
                "My head is spinning...", "Whoa, that made me dizzy",
                "The room is spinning", "Feeling lightheaded", "Need to sit down"};

        int randomNumber = (int) (Math.random() * list.length);

        Toast toast = Toast.makeText(this, list[0], Toast.LENGTH_SHORT);
        isToastShowing = true;
        toast.show();

        // PROBLEM: Flaggan isToastShowing sätts aldrig till false!
        // Lägg till detta:
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isToastShowing = false;
            }
        }, 2000); // Återställ efter 2 sekunder
    }

    // Rotera bild baserat på sensorvärden
    public void rotateImage(float x, float y) {
        float angle = (float) Math.toDegrees(Math.atan2(x, y));
        img.setRotation(angle);
    }

    // Formatera värden till två decimaler
    public String getFormattedValue(float value) {
        return String.format("%.2f", value);
    }

    // Konvertera sensorvärden till RGB-färg
    public int[] colorConverter(float x, float y, float z) {
        double R_linear = (double) (3.24096994 * x - 1.53738318 * y - 0.49861076 * z);
        double G_linear = (double) (-0.96924364 * x + 1.87596750 * y + 0.04155506 * z);
        double B_linear = (double) (0.05563008 * x - 0.20397696 * y + 1.05697151 * z);
        double r = gammaCorrect(R_linear);
        double g = gammaCorrect(G_linear);
        double b = gammaCorrect(B_linear);

        int rInt = (int) Float.parseFloat(getFormattedValue(Math.round(clamp(r, 0.0, 1.0) * 255)));
        int gInt = (int) Float.parseFloat(getFormattedValue(Math.round(clamp(g, 0.0, 1.0) * 255)));
        int bInt = (int) Float.parseFloat(getFormattedValue(Math.round(clamp(b, 0.0, 1.0) * 255)));
        Log.i("TAG", "colorConverter: " + new int[]{rInt, gInt, bInt});
        return new int[]{rInt, gInt, bInt};
    }

    // Gamma-korrigering för färgkonvertering
    private static double gammaCorrect(double linear) {
        if (linear <= 0.0031308) {
            return 12.92 * linear;
        } else {
            return 1.055 * Math.pow(linear, 1.0 / 2.4) - 0.055;
        }
    }

    // Visa sensordata i diagram
    public void showDiagram(float x, float y, float z) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, x));
        entries.add(new BarEntry(1, y));
        entries.add(new BarEntry(2, z));

        BarDataSet dataset = new BarDataSet(entries, "Sensor Data");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        int[] colors = colorConverter(x, y, z);
        dataset.setColors(Color.rgb(colors[0], colors[1], colors[2]));

        // Skapa etiketter för axlar
        ArrayList<String> labels = new ArrayList<>();
        labels.add("X");
        labels.add("Y");
        labels.add("Z");

        BarData data = new BarData(dataset);
        chart.setData(data);

        // Konfigurera legend
        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(20f);
        legend.setYEntrySpace(5f);
        legend.setTextColor(Color.WHITE);
        legend.setTextSize(12f);
        legend.setFormSize(12f);
        legend.setForm(Legend.LegendForm.SQUARE);

        // Konfigurera diagramutseende
        chart.getDescription().setEnabled(false);

        legendY = findViewById(R.id.legendY);
        legendX = findViewById(R.id.legendX);
        legendZ = findViewById(R.id.legendZ);

        legendX.setText("X: " + getFormattedValue(x));
        legendY.setText("Y: " + getFormattedValue(y));
        legendZ.setText("Z: " + getFormattedValue(z));

        // Konfigurera X-axel
        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);

        // Konfigurera Y-axel för negativa värden
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMinimum(-20f);
        leftAxis.setAxisMaximum(20f);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setZeroLineColor(Color.WHITE);
        leftAxis.setZeroLineWidth(2f);
        leftAxis.setGridColor(Color.parseColor("#333333"));
        leftAxis.setGridLineWidth(1f);

        // Inaktivera höger Y-axel
        chart.getAxisRight().setEnabled(false);

        // Konfigurera värden på staplar
        dataset.setValueTextColor(Color.WHITE);
        dataset.setValueTextSize(10f);
        dataset.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.2f", value);
            }
        });

        // Konfigurera utseende på staplar
        data.setBarWidth(0.3f);
        chart.setFitBars(true);
        chart.setExtraOffsets(10f, 10f, 10f, 30f);

        // Uppdatera diagram
        chart.invalidate();
    }

    @Override
    protected void onPause() {
        // Avregistrera sensorlyssnare när appen pausas
        sensorManager.unregisterListener(listener);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Sensorregistrering hanteras av switcharna
    }
}