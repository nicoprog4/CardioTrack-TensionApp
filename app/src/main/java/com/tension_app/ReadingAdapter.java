package com.tension_app;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReadingAdapter extends RecyclerView.Adapter<ReadingAdapter.ViewHolder> {

    public interface OnDeleteListener {
        void onDelete(BloodPressureReading reading, int position);
    }

    private List<BloodPressureReading> readings;
    private final OnDeleteListener deleteListener;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("es", "CO"));

    public ReadingAdapter(List<BloodPressureReading> readings, OnDeleteListener deleteListener) {
        this.readings = readings;
        this.deleteListener = deleteListener;
    }

    public void setReadings(List<BloodPressureReading> readings) {
        this.readings = readings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_reading, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        BloodPressureReading r = readings.get(position);
        BloodPressureReading.BPStatus status = r.getStatus();

        h.tvSystolic.setText(String.valueOf(r.getSystolic()));
        h.tvDiastolic.setText(String.valueOf(r.getDiastolic()));
        h.tvPulse.setText("❤ " + r.getPulse() + " lpm");
        h.tvArm.setText("· " + (r.getArm() != null ? r.getArm() : ""));
        h.tvDatetime.setText(sdf.format(new Date(r.getTimestamp())));
        h.tvStatus.setText(BloodPressureReading.getStatusLabel(status));

        // Status chip style
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(40f);
        bg.setColor(BloodPressureReading.getStatusBgColor(status));
        h.tvStatus.setBackground(bg);
        h.tvStatus.setTextColor(BloodPressureReading.getStatusColor(status));

        // Systolic color
        h.tvSystolic.setTextColor(BloodPressureReading.getStatusColor(status));

        // Circle bg
        GradientDrawable circleBg = new GradientDrawable();
        circleBg.setShape(GradientDrawable.OVAL);
        int bgColor = BloodPressureReading.getStatusBgColor(status);
        circleBg.setColor(bgColor);
        h.tvSystolic.getParent();  // just ensure view hierarchy

        h.tvArrhythmia.setVisibility(r.isArrhythmia() ? View.VISIBLE : View.GONE);

        if (r.getNotes() != null && !r.getNotes().isEmpty()) {
            h.tvNotes.setVisibility(View.VISIBLE);
            h.tvNotes.setText(r.getNotes());
        } else {
            h.tvNotes.setVisibility(View.GONE);
        }

        h.btnDelete.setOnClickListener(v -> {
            int pos = h.getAdapterPosition();
            if (pos != RecyclerView.NO_ID && deleteListener != null) {
                deleteListener.onDelete(r, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return readings != null ? readings.size() : 0;
    }

    public void removeAt(int position) {
        readings.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreAt(BloodPressureReading reading, int position) {
        readings.add(position, reading);
        notifyItemInserted(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSystolic, tvDiastolic, tvStatus, tvPulse, tvArm, tvDatetime, tvArrhythmia, tvNotes;
        View btnDelete;

        ViewHolder(View v) {
            super(v);
            tvSystolic = v.findViewById(R.id.tv_item_systolic);
            tvDiastolic = v.findViewById(R.id.tv_item_diastolic);
            tvStatus = v.findViewById(R.id.tv_item_status);
            tvPulse = v.findViewById(R.id.tv_item_pulse);
            tvArm = v.findViewById(R.id.tv_item_arm);
            tvDatetime = v.findViewById(R.id.tv_item_datetime);
            tvArrhythmia = v.findViewById(R.id.tv_item_arrhythmia);
            tvNotes = v.findViewById(R.id.tv_item_notes);
            btnDelete = v.findViewById(R.id.btn_delete);
        }
    }
}
