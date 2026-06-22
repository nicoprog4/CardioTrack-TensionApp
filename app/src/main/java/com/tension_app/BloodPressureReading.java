package com.tension_app;

public class BloodPressureReading {
    private long id;
    private int systolic;
    private int diastolic;
    private int pulse;
    private String arm;       // "Izquierdo" or "Derecho"
    private String position;  // "Sentado", "Acostado", "De pie"
    private boolean arrhythmia;
    private long timestamp;   // milliseconds
    private String notes;

    public BloodPressureReading() {}

    public BloodPressureReading(int systolic, int diastolic, int pulse,
                                String arm, String position,
                                boolean arrhythmia, long timestamp, String notes) {
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.pulse = pulse;
        this.arm = arm;
        this.position = position;
        this.arrhythmia = arrhythmia;
        this.timestamp = timestamp;
        this.notes = notes;
    }

    // ---- Pressure classification (AHA guidelines) ----
    public enum BPStatus {
        LOW,        // Hypotension: <90/<60
        OPTIMAL,    // Optimal: <120 and <80
        NORMAL,     // Normal: 120-129 and <80
        ELEVATED,   // Elevated: 130-139 or 80-89
        HIGH1,      // Stage 1: 140-159 or 90-99
        HIGH2,      // Stage 2: 160-179 or 100-109
        CRISIS      // Crisis: ≥180 or ≥110
    }

    public BPStatus getStatus() {
        if (systolic < 90 || diastolic < 60) return BPStatus.LOW;
        if (systolic >= 180 || diastolic >= 110) return BPStatus.CRISIS;
        if (systolic >= 160 || diastolic >= 100) return BPStatus.HIGH2;
        if (systolic >= 140 || diastolic >= 90) return BPStatus.HIGH1;
        if (systolic >= 130 || diastolic >= 80) return BPStatus.ELEVATED;
        if (systolic >= 120) return BPStatus.NORMAL;
        return BPStatus.OPTIMAL;
    }

    public static String getStatusLabel(BPStatus status) {
        switch (status) {
            case LOW: return "Hipotensión";
            case OPTIMAL: return "Óptima";
            case NORMAL: return "Normal";
            case ELEVATED: return "Elevada";
            case HIGH1: return "Hipertensión I";
            case HIGH2: return "Hipertensión II";
            case CRISIS: return "Crisis Hipertensiva";
            default: return "Desconocido";
        }
    }

    public static int getStatusColor(BPStatus status) {
        switch (status) {
            case LOW: return 0xFF8B5CF6;
            case OPTIMAL: return 0xFF22C55E;
            case NORMAL: return 0xFF3B82F6;
            case ELEVATED: return 0xFFF59E0B;
            case HIGH1: return 0xFFF97316;
            case HIGH2: return 0xFFEF4444;
            case CRISIS: return 0xFF991B1B;
            default: return 0xFF94A3B8;
        }
    }

    public static int getStatusBgColor(BPStatus status) {
        switch (status) {
            case LOW: return 0xFFEDE9FE;
            case OPTIMAL: return 0xFFDCFCE7;
            case NORMAL: return 0xFFDBEAFE;
            case ELEVATED: return 0xFFFEF3C7;
            case HIGH1: return 0xFFFFEDD5;
            case HIGH2: return 0xFFFEE2E2;
            case CRISIS: return 0xFFFEE2E2;
            default: return 0xFFF1F5F9;
        }
    }

    // ---- Getters and setters ----
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getSystolic() { return systolic; }
    public void setSystolic(int systolic) { this.systolic = systolic; }

    public int getDiastolic() { return diastolic; }
    public void setDiastolic(int diastolic) { this.diastolic = diastolic; }

    public int getPulse() { return pulse; }
    public void setPulse(int pulse) { this.pulse = pulse; }

    public String getArm() { return arm; }
    public void setArm(String arm) { this.arm = arm; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public boolean isArrhythmia() { return arrhythmia; }
    public void setArrhythmia(boolean arrhythmia) { this.arrhythmia = arrhythmia; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
