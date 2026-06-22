package com.tension_app;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddReadingFragment extends Fragment {

    private DatabaseHelper db;
    private TextInputEditText etSystolic, etDiastolic, etPulse, etDatetime, etNotes;
    private TextInputLayout tilSystolic, tilDiastolic, tilPulse;
    private RadioGroup rgArm, rgPosition;
    private SwitchMaterial switchArrhythmia;
    private TextView tvPreviewSystolic, tvPreviewDiastolic, tvPreviewPulse, tvPreviewStatus;

    private Calendar selectedDateTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_reading, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(requireContext());

        etSystolic = view.findViewById(R.id.et_systolic);
        etDiastolic = view.findViewById(R.id.et_diastolic);
        etPulse = view.findViewById(R.id.et_pulse);
        etDatetime = view.findViewById(R.id.et_datetime);
        etNotes = view.findViewById(R.id.et_notes);
        tilSystolic = view.findViewById(R.id.til_systolic);
        tilDiastolic = view.findViewById(R.id.til_diastolic);
        tilPulse = view.findViewById(R.id.til_pulse);
        rgArm = view.findViewById(R.id.rg_arm);
        rgPosition = view.findViewById(R.id.rg_position);
        switchArrhythmia = view.findViewById(R.id.switch_arrhythmia);
        tvPreviewSystolic = view.findViewById(R.id.tv_preview_systolic);
        tvPreviewDiastolic = view.findViewById(R.id.tv_preview_diastolic);
        tvPreviewPulse = view.findViewById(R.id.tv_preview_pulse);
        tvPreviewStatus = view.findViewById(R.id.tv_preview_status);

        // Set current date/time
        selectedDateTime = Calendar.getInstance();
        updateDateTimeField();

        // Date picker
        etDatetime.setOnClickListener(v -> showDateTimePicker());

        // Live preview
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { updatePreview(); }
        };
        etSystolic.addTextChangedListener(watcher);
        etDiastolic.addTextChangedListener(watcher);
        etPulse.addTextChangedListener(watcher);

        view.findViewById(R.id.btn_save).setOnClickListener(v -> saveReading(view));
    }

    private void updatePreview() {
        String sysStr = etSystolic.getText() != null ? etSystolic.getText().toString().trim() : "";
        String diaStr = etDiastolic.getText() != null ? etDiastolic.getText().toString().trim() : "";
        String pulseStr = etPulse.getText() != null ? etPulse.getText().toString().trim() : "";

        tvPreviewSystolic.setText(sysStr.isEmpty() ? "---" : sysStr);
        tvPreviewDiastolic.setText(diaStr.isEmpty() ? "---" : diaStr);
        tvPreviewPulse.setText("❤ " + (pulseStr.isEmpty() ? "--" : pulseStr) + " lpm");

        if (!sysStr.isEmpty() && !diaStr.isEmpty()) {
            try {
                BloodPressureReading temp = new BloodPressureReading();
                temp.setSystolic(Integer.parseInt(sysStr));
                temp.setDiastolic(Integer.parseInt(diaStr));
                BloodPressureReading.BPStatus status = temp.getStatus();
                tvPreviewStatus.setText(BloodPressureReading.getStatusLabel(status));
            } catch (NumberFormatException e) {
                tvPreviewStatus.setText("Ingresa valores");
            }
        } else {
            tvPreviewStatus.setText("Ingresa valores");
        }
    }

    private void showDateTimePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(
            requireContext(),
            (dp, year, month, day) -> {
                selectedDateTime.set(Calendar.YEAR, year);
                selectedDateTime.set(Calendar.MONTH, month);
                selectedDateTime.set(Calendar.DAY_OF_MONTH, day);
                TimePickerDialog timePicker = new TimePickerDialog(
                    requireContext(),
                    (tp, hour, minute) -> {
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, hour);
                        selectedDateTime.set(Calendar.MINUTE, minute);
                        updateDateTimeField();
                    },
                    selectedDateTime.get(Calendar.HOUR_OF_DAY),
                    selectedDateTime.get(Calendar.MINUTE),
                    true
                );
                timePicker.show();
            },
            selectedDateTime.get(Calendar.YEAR),
            selectedDateTime.get(Calendar.MONTH),
            selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );
        datePicker.getDatePicker().setMaxDate(now.getTimeInMillis());
        datePicker.show();
    }

    private void updateDateTimeField() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("es", "CO"));
        etDatetime.setText(sdf.format(selectedDateTime.getTime()));
    }

    private void saveReading(View rootView) {
        boolean valid = true;

        String sysStr = etSystolic.getText() != null ? etSystolic.getText().toString().trim() : "";
        String diaStr = etDiastolic.getText() != null ? etDiastolic.getText().toString().trim() : "";
        String pulseStr = etPulse.getText() != null ? etPulse.getText().toString().trim() : "";

        // Validate systolic
        if (sysStr.isEmpty()) {
            tilSystolic.setError(getString(R.string.error_required));
            valid = false;
        } else {
            int sys = Integer.parseInt(sysStr);
            if (sys < 60 || sys > 250) {
                tilSystolic.setError(getString(R.string.error_systolic_range));
                valid = false;
            } else {
                tilSystolic.setError(null);
            }
        }

        // Validate diastolic
        if (diaStr.isEmpty()) {
            tilDiastolic.setError(getString(R.string.error_required));
            valid = false;
        } else {
            int dia = Integer.parseInt(diaStr);
            if (dia < 40 || dia > 150) {
                tilDiastolic.setError(getString(R.string.error_diastolic_range));
                valid = false;
            } else if (!sysStr.isEmpty() && dia >= Integer.parseInt(sysStr)) {
                tilDiastolic.setError(getString(R.string.error_diastolic_gt));
                valid = false;
            } else {
                tilDiastolic.setError(null);
            }
        }

        // Validate pulse (optional but range-checked if provided)
        int pulse = 0;
        if (!pulseStr.isEmpty()) {
            pulse = Integer.parseInt(pulseStr);
            if (pulse < 30 || pulse > 250) {
                tilPulse.setError(getString(R.string.error_pulse_range));
                valid = false;
            } else {
                tilPulse.setError(null);
            }
        }

        if (!valid) return;

        // Arm
        String arm = "Izquierdo";
        if (rgArm.getCheckedRadioButtonId() == R.id.rb_arm_right) arm = "Derecho";

        // Position
        String position = "Sentado";
        int posId = rgPosition.getCheckedRadioButtonId();
        if (posId == R.id.rb_lying) position = "Acostado";
        else if (posId == R.id.rb_standing) position = "De pie";

        // Notes
        String notes = etNotes.getText() != null ? etNotes.getText().toString().trim() : "";

        BloodPressureReading reading = new BloodPressureReading(
            Integer.parseInt(sysStr),
            Integer.parseInt(diaStr),
            pulse,
            arm, position,
            switchArrhythmia.isChecked(),
            selectedDateTime.getTimeInMillis(),
            notes
        );

        long id = db.insertReading(reading);
        if (id > 0) {
            Snackbar.make(rootView, R.string.reading_saved, Snackbar.LENGTH_SHORT).show();
            clearForm();
            // Navigate to dashboard
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateTo(R.id.nav_dashboard);
            }
        }
    }

    private void clearForm() {
        etSystolic.setText("");
        etDiastolic.setText("");
        etPulse.setText("");
        etNotes.setText("");
        switchArrhythmia.setChecked(false);
        rgArm.check(R.id.rb_arm_left);
        rgPosition.check(R.id.rb_sitting);
        selectedDateTime = Calendar.getInstance();
        updateDateTimeField();
        updatePreview();
    }
}
