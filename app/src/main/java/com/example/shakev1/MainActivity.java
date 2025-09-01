package com.example.shakev1;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.hardware.Sensor.TYPE_GYROSCOPE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
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
    private Sensor sensor, sensor2, sensor3;
    private ImageView img;
    private TextView legendX;
    private TextView legendZ;
    private TextView legendY;
    private Switch switch1;
    private Switch switch2;


    private float x, y, z;


    private SensorEventListener listener;
    private Toast toast;
    private BarChart chart;


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(TYPE_ACCELEROMETER);
        sensor2 = sensorManager.getDefaultSensor(TYPE_GYROSCOPE);
        sensor3 = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        img = findViewById(R.id.img);
        chart = findViewById(R.id.chart);
        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        listener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                showDiagram(event.values[0], event.values[1], event.values[2]);
                rotateImage(event.values[1], event.values[0]);

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                if (x < -5 || y < -5 || z < -5) {
                    showDizzyToast();
                }
            }

        };
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

    public void showDiagram(float x, float y, float z) {


        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, x));
        entries.add(new BarEntry(1, y));
        entries.add(new BarEntry(2, z));

        BarDataSet dataset = new BarDataSet(entries, "Accelerometer Data");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);


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


}
