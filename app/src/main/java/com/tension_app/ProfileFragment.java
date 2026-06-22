package com.tension_app;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ProfileFragment extends Fragment {

    private SharedPreferences prefs;
    private TextInputEditText etName, etAge, etGender, etWeight, etHeight, etDoctor, etReminderTime;
    private SwitchMaterial switchReminder;
    private TextInputLayout tilReminderTime;
    private TextView tvAvatar, tvProfileNameDisplay, tvProfileDetails;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        etName = view.findViewById(R.id.et_name);
        etAge = view.findViewById(R.id.et_age);
        etGender = view.findViewById(R.id.et_gender);
        etWeight = view.findViewById(R.id.et_weight);
        etHeight = view.findViewById(R.id.et_height);
        etDoctor = view.findViewById(R.id.et_doctor);
        etReminderTime = view.findViewById(R.id.et_reminder_time);
        switchReminder = view.findViewById(R.id.switch_reminder);
        tilReminderTime = view.findViewById(R.id.til_reminder_time);
        tvAvatar = view.findViewById(R.id.tv_avatar);
        tvProfileNameDisplay = view.findViewById(R.id.tv_profile_name_display);
        tvProfileDetails = view.findViewById(R.id.tv_profile_details);

        loadProfile();

        // Gender picker
        etGender.setOnClickListener(v -> showGenderPicker());

        // Reminder toggle
        switchReminder.setOnCheckedChangeListener((btn, checked) -> {
            tilReminderTime.setVisibility(checked ? View.VISIBLE : View.GONE);
        });

        // Reminder time picker
        etReminderTime.setOnClickListener(v -> showTimePicker());

        // Save
        view.findViewById(R.id.btn_save_profile).setOnClickListener(v -> saveProfile(view));
    }

    private void loadProfile() {
        etName.setText(prefs.getString("profile_name", ""));
        etAge.setText(prefs.getString("profile_age", ""));
        etGender.setText(prefs.getString("profile_gender", ""));
        etWeight.setText(prefs.getString("profile_weight", ""));
        etHeight.setText(prefs.getString("profile_height", ""));
        etDoctor.setText(prefs.getString("profile_doctor", ""));
        etReminderTime.setText(prefs.getString("reminder_time", "08:00"));

        boolean reminderEnabled = prefs.getBoolean("reminder_enabled", false);
        switchReminder.setChecked(reminderEnabled);
        tilReminderTime.setVisibility(reminderEnabled ? View.VISIBLE : View.GONE);

        updateHeader();
    }

    private void updateHeader() {
        String name = prefs.getString("profile_name", "");
        String age = prefs.getString("profile_age", "");
        String gender = prefs.getString("profile_gender", "");

        if (!name.isEmpty()) {
            tvAvatar.setText(String.valueOf(name.charAt(0)).toUpperCase());
            tvProfileNameDisplay.setText(name);
        } else {
            tvAvatar.setText("?");
            tvProfileNameDisplay.setText("Mi perfil");
        }

        StringBuilder details = new StringBuilder();
        if (!age.isEmpty()) details.append(age).append(" años");
        if (!gender.isEmpty()) {
            if (details.length() > 0) details.append("  •  ");
            details.append(gender);
        }
        tvProfileDetails.setText(details.length() > 0 ? details.toString() : "Configura tu información");
    }

    private void showGenderPicker() {
        String[] options = {"Masculino", "Femenino", "Otro"};
        new AlertDialog.Builder(requireContext())
            .setTitle("Seleccionar género")
            .setItems(options, (dialog, which) -> etGender.setText(options[which]))
            .show();
    }

    private void showTimePicker() {
        String current = etReminderTime.getText() != null ? etReminderTime.getText().toString() : "08:00";
        int hour = 8, minute = 0;
        try {
            String[] parts = current.split(":");
            hour = Integer.parseInt(parts[0]);
            minute = Integer.parseInt(parts[1]);
        } catch (Exception ignored) {}

        new TimePickerDialog(requireContext(), (tp, h, m) -> {
            etReminderTime.setText(String.format("%02d:%02d", h, m));
        }, hour, minute, true).show();
    }

    private void saveProfile(View rootView) {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String age = etAge.getText() != null ? etAge.getText().toString().trim() : "";
        String gender = etGender.getText() != null ? etGender.getText().toString().trim() : "";
        String weight = etWeight.getText() != null ? etWeight.getText().toString().trim() : "";
        String height = etHeight.getText() != null ? etHeight.getText().toString().trim() : "";
        String doctor = etDoctor.getText() != null ? etDoctor.getText().toString().trim() : "";
        String reminderTime = etReminderTime.getText() != null ? etReminderTime.getText().toString().trim() : "08:00";
        boolean reminderEnabled = switchReminder.isChecked();

        prefs.edit()
            .putString("profile_name", name)
            .putString("profile_age", age)
            .putString("profile_gender", gender)
            .putString("profile_weight", weight)
            .putString("profile_height", height)
            .putString("profile_doctor", doctor)
            .putString("reminder_time", reminderTime)
            .putBoolean("reminder_enabled", reminderEnabled)
            .apply();

        updateHeader();
        Snackbar.make(rootView, R.string.profile_saved, Snackbar.LENGTH_SHORT).show();
    }
}
