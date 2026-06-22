package com.tension_app;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;

import java.util.Calendar;
import java.util.List;

public class StatsFragment extends Fragment {

    private DatabaseHelper db;
    private BPChartView chartView;
    private TextView tvTotalReadings, tvStatsAvgSys, tvStatsAvgDia;
    private TextView tvSysMin, tvSysAvg, tvSysMax;
    private TextView tvDiaMin, tvDiaAvg, tvDiaMax;
    private TextView tvPulseMin, tvPulseAvg, tvPulseMax;
    private TextView tvArrhythmiaCount;
    private LinearLayout layoutDistribution;
    private LinearLayout layoutNoStats;

    private int periodDays = 7;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(requireContext());

        chartView = view.findViewById(R.id.chart_view);
        tvTotalReadings = view.findViewById(R.id.tv_total_readings);
        tvStatsAvgSys = view.findViewById(R.id.tv_stats_avg_sys);
        tvStatsAvgDia = view.findViewById(R.id.tv_stats_avg_dia);
        tvSysMin = view.findViewById(R.id.tv_sys_min);
        tvSysAvg = view.findViewById(R.id.tv_sys_avg);
        tvSysMax = view.findViewById(R.id.tv_sys_max);
        tvDiaMin = view.findViewById(R.id.tv_dia_min);
        tvDiaAvg = view.findViewById(R.id.tv_dia_avg);
        tvDiaMax = view.findViewById(R.id.tv_dia_max);
        tvPulseMin = view.findViewById(R.id.tv_pulse_min);
        tvPulseAvg = view.findViewById(R.id.tv_pulse_avg);
        tvPulseMax = view.findViewById(R.id.tv_pulse_max);
        tvArrhythmiaCount = view.findViewById(R.id.tv_arrhythmia_count);
        layoutDistribution = view.findViewById(R.id.layout_distribution);
        layoutNoStats = view.findViewById(R.id.layout_no_stats);

        // Period chips
        Chip chip7 = view.findViewById(R.id.chip_7days);
        Chip chip30 = view.findViewById(R.id.chip_30days);
        Chip chip3m = view.findViewById(R.id.chip_3months);

        chip7.setOnClickListener(v -> { periodDays = 7; loadStats(); });
        chip30.setOnClickListener(v -> { periodDays = 30; loadStats(); });
        chip3m.setOnClickListener(v -> { periodDays = 90; loadStats(); });

        loadStats();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStats();
    }

    private void loadStats() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -periodDays);
        long since = cal.getTimeInMillis();

        DatabaseHelper.Stats stats = db.getStats(since);
        List<BloodPressureReading> readings = db.getReadingsSince(since);

        // Update chart
        chartView.setReadings(readings);

        if (stats.count == 0) {
            layoutNoStats.setVisibility(View.VISIBLE);
            return;
        }
        layoutNoStats.setVisibility(View.GONE);

        tvTotalReadings.setText(String.valueOf(stats.count));
        tvStatsAvgSys.setText(String.valueOf((int) Math.round(stats.avgSystolic)));
        tvStatsAvgDia.setText(String.valueOf((int) Math.round(stats.avgDiastolic)));

        tvSysMin.setText(String.valueOf(stats.minSystolic));
        tvSysAvg.setText(String.valueOf((int) Math.round(stats.avgSystolic)));
        tvSysMax.setText(String.valueOf(stats.maxSystolic));

        tvDiaMin.setText(String.valueOf(stats.minDiastolic));
        tvDiaAvg.setText(String.valueOf((int) Math.round(stats.avgDiastolic)));
        tvDiaMax.setText(String.valueOf(stats.maxDiastolic));

        tvPulseMin.setText(stats.minPulse > 0 ? String.valueOf(stats.minPulse) : "--");
        tvPulseAvg.setText(stats.avgPulse > 0 ? String.valueOf((int) Math.round(stats.avgPulse)) : "--");
        tvPulseMax.setText(stats.maxPulse > 0 ? String.valueOf(stats.maxPulse) : "--");

        tvArrhythmiaCount.setText(String.valueOf(stats.arrhythmiaCount));

        buildDistribution(stats);
    }

    private void buildDistribution(DatabaseHelper.Stats stats) {
        layoutDistribution.removeAllViews();

        String[] labels = {"Hipotensión", "Óptima", "Normal", "Elevada", "Hipertensión I", "Hipertensión II", "Crisis Hipertensiva"};
        int[] colors = {
            0xFF8B5CF6, 0xFF22C55E, 0xFF3B82F6,
            0xFFF59E0B, 0xFFF97316, 0xFFEF4444, 0xFF991B1B
        };

        for (int i = 0; i < labels.length; i++) {
            int count = stats.statusCounts[i];
            if (count == 0) continue;

            float percentage = (float) count / stats.count;

            // Row: label + bar + count
            LinearLayout row = new LinearLayout(requireContext());
            row.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(0, 0, 0, 12);
            row.setLayoutParams(rowParams);

            // Label + count row
            LinearLayout labelRow = new LinearLayout(requireContext());
            labelRow.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams lrParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lrParams.setMargins(0, 0, 0, 6);
            labelRow.setLayoutParams(lrParams);

            TextView tvLabel = new TextView(requireContext());
            tvLabel.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            tvLabel.setText(labels[i]);
            tvLabel.setTextSize(13f);
            tvLabel.setTextColor(0xFF1A1F36);

            TextView tvCount = new TextView(requireContext());
            tvCount.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvCount.setText(count + " (" + (int)(percentage * 100) + "%)");
            tvCount.setTextSize(12f);
            tvCount.setTextColor(0xFF6B7280);

            labelRow.addView(tvLabel);
            labelRow.addView(tvCount);

            // Progress bar container
            LinearLayout barContainer = new LinearLayout(requireContext());
            barContainer.setOrientation(LinearLayout.HORIZONTAL);
            barContainer.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 24));

            // Background bar
            View barBg = new View(requireContext());
            GradientDrawable bgShape = new GradientDrawable();
            bgShape.setShape(GradientDrawable.RECTANGLE);
            bgShape.setCornerRadius(12f);
            bgShape.setColor(0xFFE5E7EB);
            barBg.setBackground(bgShape);
            barBg.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            // Filled bar
            View barFill = new View(requireContext());
            GradientDrawable fillShape = new GradientDrawable();
            fillShape.setShape(GradientDrawable.RECTANGLE);
            fillShape.setCornerRadius(12f);
            fillShape.setColor(colors[i]);
            barFill.setBackground(fillShape);

            // Use weight trick: container is horizontal, fill has weight = percentage
            barContainer.removeAllViews();
            LinearLayout filledRow = new LinearLayout(requireContext());
            filledRow.setOrientation(LinearLayout.HORIZONTAL);
            filledRow.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 24));

            View filled = new View(requireContext());
            GradientDrawable fd = new GradientDrawable();
            fd.setShape(GradientDrawable.RECTANGLE);
            fd.setCornerRadius(12f);
            fd.setColor(colors[i]);
            filled.setBackground(fd);
            LinearLayout.LayoutParams filledParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, percentage);
            filled.setLayoutParams(filledParams);

            View remainder = new View(requireContext());
            GradientDrawable rd = new GradientDrawable();
            rd.setShape(GradientDrawable.RECTANGLE);
            rd.setCornerRadius(12f);
            rd.setColor(0xFFE5E7EB);
            remainder.setBackground(rd);
            LinearLayout.LayoutParams remParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, 1f - percentage);
            remainder.setLayoutParams(remParams);

            filledRow.addView(filled);
            filledRow.addView(remainder);

            row.addView(labelRow);
            row.addView(filledRow);
            layoutDistribution.addView(row);
        }
    }
}
