package com.tension_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.List;

public class HistoryFragment extends Fragment {

    private DatabaseHelper db;
    private RecyclerView recyclerView;
    private ReadingAdapter adapter;
    private LinearLayout layoutEmpty;
    private TextView tvTotalCount;
    private ChipGroup chipGroup;

    private enum FilterMode { ALL, TODAY, WEEK, MONTH, OPTIMAL, HIGH, ARRHYTHMIA }
    private FilterMode currentFilter = FilterMode.ALL;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(requireContext());

        recyclerView = view.findViewById(R.id.rv_history);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        tvTotalCount = view.findViewById(R.id.tv_total_count);
        chipGroup = view.findViewById(R.id.chip_group_filter);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        setupChips(view);
        loadReadings();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadReadings();
    }

    private void setupChips(View view) {
        Chip chipAll = view.findViewById(R.id.chip_all);
        Chip chipToday = view.findViewById(R.id.chip_today);
        Chip chipWeek = view.findViewById(R.id.chip_week);
        Chip chipMonth = view.findViewById(R.id.chip_month);
        Chip chipOptimal = view.findViewById(R.id.chip_optimal);
        Chip chipHigh = view.findViewById(R.id.chip_high);
        Chip chipArrhythmia = view.findViewById(R.id.chip_arrhythmia);

        chipAll.setOnClickListener(v -> { currentFilter = FilterMode.ALL; loadReadings(); });
        chipToday.setOnClickListener(v -> { currentFilter = FilterMode.TODAY; loadReadings(); });
        chipWeek.setOnClickListener(v -> { currentFilter = FilterMode.WEEK; loadReadings(); });
        chipMonth.setOnClickListener(v -> { currentFilter = FilterMode.MONTH; loadReadings(); });
        chipOptimal.setOnClickListener(v -> { currentFilter = FilterMode.OPTIMAL; loadReadings(); });
        chipHigh.setOnClickListener(v -> { currentFilter = FilterMode.HIGH; loadReadings(); });
        chipArrhythmia.setOnClickListener(v -> { currentFilter = FilterMode.ARRHYTHMIA; loadReadings(); });
    }

    private void loadReadings() {
        List<BloodPressureReading> readings;
        Calendar cal = Calendar.getInstance();

        switch (currentFilter) {
            case TODAY:
                readings = db.getTodayReadings();
                break;
            case WEEK:
                cal.add(Calendar.DAY_OF_YEAR, -7);
                readings = db.getReadingsSince(cal.getTimeInMillis());
                break;
            case MONTH:
                cal.add(Calendar.DAY_OF_YEAR, -30);
                readings = db.getReadingsSince(cal.getTimeInMillis());
                break;
            case OPTIMAL:
                readings = db.getReadingsByStatus("Óptima");
                break;
            case HIGH:
                // Filter elevated + above
                readings = db.getAllReadings();
                readings.removeIf(r -> {
                    BloodPressureReading.BPStatus s = r.getStatus();
                    return s == BloodPressureReading.BPStatus.OPTIMAL ||
                           s == BloodPressureReading.BPStatus.NORMAL ||
                           s == BloodPressureReading.BPStatus.LOW;
                });
                break;
            case ARRHYTHMIA:
                readings = db.getReadingsWithArrhythmia();
                break;
            default:
                readings = db.getAllReadings();
                break;
        }

        int total = db.getAllReadings().size();
        tvTotalCount.setText(total + " registros totales");

        if (readings.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            return;
        }

        recyclerView.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);

        if (adapter == null) {
            adapter = new ReadingAdapter(readings, this::onDeleteReading);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setReadings(readings);
        }
    }

    private void onDeleteReading(BloodPressureReading reading, int position) {
        new AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_confirm)
            .setMessage(R.string.delete_confirm_msg)
            .setPositiveButton(R.string.delete, (dialog, which) -> {
                db.deleteReading(reading.getId());
                adapter.removeAt(position);
                View rootView = getView();
                if (rootView != null) {
                    Snackbar.make(rootView, R.string.reading_deleted, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, v -> {
                            long id = db.insertReading(reading);
                            reading.setId(id);
                            adapter.restoreAt(reading, position);
                            loadReadings();
                        })
                        .show();
                }
                loadReadings();
            })
            .setNegativeButton(R.string.cancel, null)
            .show();
    }
}
