package com.example.shakev1;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);




        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(6, 6));
        entries.add(new BarEntry(4, 10));
        entries.add(new BarEntry(2, 22));

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