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
    private SensorManager sensorManager;
    private Sensor sensor, sensor2;
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


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(TYPE_ACCELEROMETER);
        sensor2 = sensorManager.getDefaultSensor(TYPE_GYROSCOPE);

        img = findViewById(R.id.img);
        chart = findViewById(R.id.chart);
        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        btn =findViewById(R.id.button2);
        stopSensor= findViewById(R.id.stopsnr);


        listener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                showDiagram(event.values[0], event.values[1], event.values[2]);
                colorConverter(event.values[0], event.values[1], event.values[2]);
                rotateImage(event.values[0], event.values[1]);

                 x = event.values[0];
                 y = event.values[1];
                 z = event.values[2];

                if (x < -5 || y < -5 || z < -5) {
                    showDizzyToast();
                }
            }

        };
        stopSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i) {
                    sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    chart.setVisibility(chart.VISIBLE);
                    switch1.setChecked(true);
                    switch2.setChecked(false);
                    i=false;
                }
                else {
                    sensorManager.unregisterListener(listener);
                    chart.setVisibility(chart.INVISIBLE);
                    switch1.setChecked(false);
                    switch2.setChecked(false);
                    i=true;

                }

                }

            });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (i) {
                    sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                    chart.setVisibility(chart.VISIBLE);
                    switch1.setChecked(true);
                    switch2.setChecked(false);
                    i=false;
            }
                else {
                    sensorManager.registerListener(listener, sensor2, SensorManager.SENSOR_DELAY_NORMAL);
                    chart.setVisibility(chart.VISIBLE);
                    switch1.setChecked(false);
                    switch2.setChecked(true);
                    i=true;

                }
        }});

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Handle switch state change
                if (isChecked) {


                    switch2.setChecked(false);
                    // Switch is ON
                    if (sensor != null) {
                        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                        chart.setVisibility(chart.VISIBLE);


                    }

                } else {
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
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Handle switch state change
                if (isChecked) {
                    switch1.setChecked(false);
                    // Switch is ON
                    if (sensor2 != null) {
                        sensorManager.registerListener(listener, sensor2, SensorManager.SENSOR_DELAY_NORMAL);
                        chart.setVisibility(chart.VISIBLE);
//
                    }

                } else {
                    sensorManager.unregisterListener(listener);
                    chart.setVisibility(chart.INVISIBLE);
                    switch2.setChecked(false);
                    // Очищаем данные графика
                    chart.clear();
                    chart.invalidate();
                    img.setRotation(0f);
                    // Также можно обнулить текстовые поля
                    legendX.setText("X: 0.00");
                    legendY.setText("Y: 0.00");
                    legendZ.setText("Z: 0.00");

                }
            }
        });
    }


    private void showDizzyToast() {
        String[] list = {"I feel dizzy", "My head is spinning", "Feeling woozy", "Getting dizzy", "My head is spinning...",
                "Feeling woozy", "Getting dizzy", "Whoa, that made me dizzy", "The room is spinning", "Feeling lightheaded", "Need to sit down"};

        int randomNumber = (int) (Math.random() * list.length);

        toast = Toast.makeText(this, list[randomNumber], Toast.LENGTH_SHORT);
        toast.show();
//
//
    }


    public void rotateImage(float x, float y) {
        float angle = (float) Math.toDegrees(Math.atan2(x, y));
        img.setRotation(angle);

    }

    public String getFormattedValue(float value) {
        return String.format("%.2f", value);
    }

    public int[] colorConverter(float x, float y, float z){
        double R_linear = (double) (3.24096994 * x - 1.53738318 * y - 0.49861076 * z);
        double G_linear = (double) (-0.96924364 * x + 1.87596750 * y + 0.04155506 * z);
        double B_linear = (double) (0.05563008 * x - 0.20397696 * y + 1.05697151 * z);
        double r = gammaCorrect(R_linear);
        double g = gammaCorrect(G_linear);
        double b = gammaCorrect(B_linear);

        int rInt = (int) Float.parseFloat(getFormattedValue(Math.round(clamp(r, 0.0, 1.0) * 255)));
        int gInt = (int)Float.parseFloat(getFormattedValue(Math.round(clamp(g, 0.0, 1.0) * 255)));
        int bInt = (int)Float.parseFloat(getFormattedValue(Math.round(clamp(b, 0.0, 1.0) * 255)));
        Log.i("TAG", "colorConverter: "+ new int[]{rInt, gInt, bInt});
        return new int []{rInt, gInt, bInt};

    }
    private static double gammaCorrect(double linear) {
        if (linear <= 0.0031308) {
            return 12.92 * linear;
        } else {
            return 1.055 * Math.pow(linear, 1.0 / 2.4) - 0.055;
        }
    }

    public void showDiagram(float x, float y, float z) {


        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, x));
        entries.add(new BarEntry(1, y));
        entries.add(new BarEntry(2, z));

        BarDataSet dataset = new BarDataSet(entries, "Sensor Data");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        int[] colors= colorConverter(x,y,z);
        dataset.setColors(Color.rgb(colors[0],colors[1],colors[2]));


        // Создаем метки для осей
        ArrayList<String> labels = new ArrayList<>();
        labels.add("X");
        labels.add("Y");
        labels.add("Z");

        BarData data = new BarData(dataset);
        chart.setData(data);

        // НАСТРОЙКА ЛЕГЕНДЫ
        Legend legend = chart.getLegend();
        legend.setEnabled(true); // Включаем легенду
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

        // Настройка внешнего вида chart
        chart.getDescription().setEnabled(false);

        legendY = findViewById(R.id.legendY);
        legendX = findViewById(R.id.legendX);
        legendZ = findViewById(R.id.legendZ);


        legendX.setText("X: " + getFormattedValue(x));
        legendY.setText("Y: " + getFormattedValue(y));
        legendZ.setText("Z: " + getFormattedValue(z));


        // Настройка оси X
        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);

        // НАСТРОЙКА ОСИ Y ДЛЯ ОТРИЦАТЕЛЬНЫХ ЗНАЧЕНИЙ
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMinimum(-20f); // Минимальное значение (ниже ожидаемых отрицательных)
        leftAxis.setAxisMaximum(20f);  // Максимальное значение (выше ожидаемых положительных)
        leftAxis.setDrawZeroLine(true); // Рисуем линию на нуле
        leftAxis.setZeroLineColor(Color.WHITE);
        leftAxis.setZeroLineWidth(2f);

        // Настройка сетки для лучшей читаемости
        leftAxis.setGridColor(Color.parseColor("#333333"));
        leftAxis.setGridLineWidth(1f);

        // Отключаем правую ось Y
        chart.getAxisRight().setEnabled(false);

        // Настройка значений на столбцах
        dataset.setValueTextColor(Color.WHITE);
        dataset.setValueTextSize(10f);
        dataset.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.2f", value);
            }
        });

        // Настройка отступов и внешнего вида столбцов
        data.setBarWidth(0.3f); // Ширина столбцов
        chart.setFitBars(true);
        chart.setExtraOffsets(10f, 10f, 10f, 30f); // Отступы для легенды


        // Обновление
        chart.invalidate();
    }


    @Override
    protected void onPause() {
        sensorManager.unregisterListener(listener);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }


}
