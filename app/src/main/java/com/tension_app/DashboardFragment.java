package com.tension_app;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private DatabaseHelper db;
    private TextView tvGreeting, tvUserName;
    private TextView tvLastSystolic, tvLastDiastolic, tvLastPulse, tvLastStatus, tvLastDatetime;
    private TextView tvTodayCount, tvWeekAvgSys, tvWeekAvgDia;
    private LinearLayout layoutRecentReadings;
    private TextView tvNoReadings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(requireContext());

        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvLastSystolic = view.findViewById(R.id.tv_last_systolic);
        tvLastDiastolic = view.findViewById(R.id.tv_last_diastolic);
        tvLastPulse = view.findViewById(R.id.tv_last_pulse);
        tvLastStatus = view.findViewById(R.id.tv_last_status);
        tvLastDatetime = view.findViewById(R.id.tv_last_datetime);
        tvTodayCount = view.findViewById(R.id.tv_today_count);
        tvWeekAvgSys = view.findViewById(R.id.tv_week_avg_sys);
        tvWeekAvgDia = view.findViewById(R.id.tv_week_avg_dia);
        layoutRecentReadings = view.findViewById(R.id.layout_recent_readings);
        tvNoReadings = view.findViewById(R.id.tv_no_readings);

        view.findViewById(R.id.btn_quick_add).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateTo(R.id.nav_add);
            }
        });

        view.findViewById(R.id.tv_see_all).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateTo(R.id.nav_history);
            }
        });

        setupBPCategoryTable(view);
        loadData();
        setGreeting();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void setGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour < 12) greeting = "Buenos días";
        else if (hour < 18) greeting = "Buenas tardes";
        else greeting = "Buenas noches";
        tvGreeting.setText(greeting);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String name = prefs.getString("profile_name", "");
        tvUserName.setText(name.isEmpty() ? "Mi perfil" : name);
    }

    private void loadData() {
        // Last reading
        BloodPressureReading latest = db.getLatestReading();
        if (latest != null) {
            tvLastSystolic.setText(String.valueOf(latest.getSystolic()));
            tvLastDiastolic.setText(String.valueOf(latest.getDiastolic()));
            tvLastPulse.setText(latest.getPulse() + " lpm");

            BloodPressureReading.BPStatus status = latest.getStatus();
            tvLastStatus.setText(BloodPressureReading.getStatusLabel(status));

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("es", "CO"));
            tvLastDatetime.setText(sdf.format(new Date(latest.getTimestamp())));
        }

        // Today count
        List<BloodPressureReading> today = db.getTodayReadings();
        tvTodayCount.setText(String.valueOf(today.size()));

        // 7-day stats
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -7);
        DatabaseHelper.Stats stats = db.getStats(cal.getTimeInMillis());
        if (stats.count > 0) {
            tvWeekAvgSys.setText(String.valueOf((int) Math.round(stats.avgSystolic)));
            tvWeekAvgDia.setText(String.valueOf((int) Math.round(stats.avgDiastolic)));
        } else {
            tvWeekAvgSys.setText("--");
            tvWeekAvgDia.setText("--");
        }

        // Recent readings (last 5)
        loadRecentReadings();
    }

    private void loadRecentReadings() {
        layoutRecentReadings.removeAllViews();
        List<BloodPressureReading> all = db.getAllReadings();

        if (all.isEmpty()) {
            tvNoReadings.setVisibility(View.VISIBLE);
            return;
        }
        tvNoReadings.setVisibility(View.GONE);

        int limit = Math.min(5, all.size());
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, HH:mm", new Locale("es", "CO"));

        for (int i = 0; i < limit; i++) {
            BloodPressureReading r = all.get(i);
            View itemView = buildMiniReadingCard(r, sdf);
            layoutRecentReadings.addView(itemView);
        }
    }

    private View buildMiniReadingCard(BloodPressureReading r, SimpleDateFormat sdf) {
        View card = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_reading, layoutRecentReadings, false);

        TextView tvSys = card.findViewById(R.id.tv_item_systolic);
        TextView tvDia = card.findViewById(R.id.tv_item_diastolic);
        TextView tvStatus = card.findViewById(R.id.tv_item_status);
        TextView tvPulse = card.findViewById(R.id.tv_item_pulse);
        TextView tvArm = card.findViewById(R.id.tv_item_arm);
        TextView tvDatetime = card.findViewById(R.id.tv_item_datetime);
        TextView tvArrhythmia = card.findViewById(R.id.tv_item_arrhythmia);
        TextView tvNotes = card.findViewById(R.id.tv_item_notes);
        View btnDelete = card.findViewById(R.id.btn_delete);

        tvSys.setText(String.valueOf(r.getSystolic()));
        tvDia.setText(String.valueOf(r.getDiastolic()));
        tvPulse.setText("❤ " + r.getPulse() + " lpm");
        tvArm.setText("· " + (r.getArm() != null ? r.getArm() : ""));
        tvDatetime.setText(sdf.format(new Date(r.getTimestamp())));

        BloodPressureReading.BPStatus status = r.getStatus();
        tvStatus.setText(BloodPressureReading.getStatusLabel(status));

        // Status chip background
        GradientDrawable statusBg = new GradientDrawable();
        statusBg.setShape(GradientDrawable.RECTANGLE);
        statusBg.setCornerRadius(40f);
        statusBg.setColor(BloodPressureReading.getStatusBgColor(status));
        tvStatus.setBackground(statusBg);
        tvStatus.setTextColor(BloodPressureReading.getStatusColor(status));

        // Circle indicator
        View circle = card.findViewById(R.id.tv_item_systolic).getParent() instanceof View ?
            card : card;
        // Color the systolic text
        tvSys.setTextColor(BloodPressureReading.getStatusColor(status));

        if (r.isArrhythmia()) {
            tvArrhythmia.setVisibility(View.VISIBLE);
        }
        if (r.getNotes() != null && !r.getNotes().isEmpty()) {
            tvNotes.setVisibility(View.VISIBLE);
            tvNotes.setText(r.getNotes());
        }

        btnDelete.setOnClickListener(v -> {
            db.deleteReading(r.getId());
            loadRecentReadings();
            loadData();
        });

        return card;
    }

    private void setupBPCategoryTable(View view) {
        // Set up each category row
        String[][] categories = {
            {"Hipotensión",       "< 90",   "< 60"},
            {"Óptima",            "< 120",  "< 80"},
            {"Normal",            "120–129","< 80"},
            {"Elevada",           "130–139","80–89"},
            {"Hipertensión I",    "140–159","90–99"},
            {"Hipertensión II",   "160–179","100–109"},
            {"Crisis Hipertensiva","≥ 180", "≥ 110"},
        };
        int[] colors = {
            0xFF8B5CF6, 0xFF22C55E, 0xFF3B82F6,
            0xFFF59E0B, 0xFFF97316, 0xFFEF4444, 0xFF991B1B
        };
        int[] rowIds = {
            R.id.row_low, R.id.row_optimal, R.id.row_normal,
            R.id.row_elevated, R.id.row_high1, R.id.row_high2, R.id.row_crisis
        };

        for (int i = 0; i < rowIds.length; i++) {
            View row = view.findViewById(rowIds[i]);
            if (row == null) continue;
            View dot = row.findViewById(R.id.color_dot);
            TextView tvName = row.findViewById(R.id.tv_category_name);
            TextView tvSys = row.findViewById(R.id.tv_systolic_range);
            TextView tvDia = row.findViewById(R.id.tv_diastolic_range);

            GradientDrawable dotBg = new GradientDrawable();
            dotBg.setShape(GradientDrawable.OVAL);
            dotBg.setColor(colors[i]);
            dot.setBackground(dotBg);

            tvName.setText(categories[i][0]);
            tvSys.setText(categories[i][1]);
            tvDia.setText(categories[i][2]);
        }
    }
}
