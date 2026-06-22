package com.tension_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "cardiotrack.db";
    private static final int DB_VERSION = 1;

    // Readings table
    static final String TABLE_READINGS = "readings";
    static final String COL_ID = "_id";
    static final String COL_SYSTOLIC = "systolic";
    static final String COL_DIASTOLIC = "diastolic";
    static final String COL_PULSE = "pulse";
    static final String COL_ARM = "arm";
    static final String COL_POSITION = "position";
    static final String COL_ARRHYTHMIA = "arrhythmia";
    static final String COL_TIMESTAMP = "timestamp";
    static final String COL_NOTES = "notes";

    private static final String CREATE_READINGS =
        "CREATE TABLE " + TABLE_READINGS + " (" +
        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COL_SYSTOLIC + " INTEGER NOT NULL, " +
        COL_DIASTOLIC + " INTEGER NOT NULL, " +
        COL_PULSE + " INTEGER, " +
        COL_ARM + " TEXT, " +
        COL_POSITION + " TEXT, " +
        COL_ARRHYTHMIA + " INTEGER DEFAULT 0, " +
        COL_TIMESTAMP + " INTEGER NOT NULL, " +
        COL_NOTES + " TEXT" +
        ")";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_READINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_READINGS);
        onCreate(db);
    }

    // ---- CRUD ----

    public long insertReading(BloodPressureReading r) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = toContentValues(r);
        return db.insert(TABLE_READINGS, null, cv);
    }

    public boolean deleteReading(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_READINGS, COL_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public List<BloodPressureReading> getAllReadings() {
        return query(null, null, COL_TIMESTAMP + " DESC");
    }

    public List<BloodPressureReading> getReadingsSince(long sinceMs) {
        return query(COL_TIMESTAMP + ">=?", new String[]{String.valueOf(sinceMs)}, COL_TIMESTAMP + " DESC");
    }

    public BloodPressureReading getLatestReading() {
        List<BloodPressureReading> all = query(null, null, COL_TIMESTAMP + " DESC LIMIT 1");
        return all.isEmpty() ? null : all.get(0);
    }

    public List<BloodPressureReading> getTodayReadings() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return getReadingsSince(cal.getTimeInMillis());
    }

    public List<BloodPressureReading> getReadingsWithArrhythmia() {
        return query(COL_ARRHYTHMIA + "=1", null, COL_TIMESTAMP + " DESC");
    }

    public List<BloodPressureReading> getReadingsByStatus(String statusName) {
        // We do status filtering in Java since status is computed
        List<BloodPressureReading> all = getAllReadings();
        List<BloodPressureReading> filtered = new ArrayList<>();
        for (BloodPressureReading r : all) {
            if (BloodPressureReading.getStatusLabel(r.getStatus()).equals(statusName)) {
                filtered.add(r);
            }
        }
        return filtered;
    }

    // ---- Stats helpers ----

    public static class Stats {
        public int count;
        public double avgSystolic, avgDiastolic, avgPulse;
        public int minSystolic, maxSystolic;
        public int minDiastolic, maxDiastolic;
        public int minPulse, maxPulse;
        public int arrhythmiaCount;
        public int[] statusCounts = new int[7]; // indexed by BPStatus ordinal
    }

    public Stats getStats(long sinceMs) {
        List<BloodPressureReading> readings = sinceMs > 0 ? getReadingsSince(sinceMs) : getAllReadings();
        Stats s = new Stats();
        if (readings.isEmpty()) return s;

        s.count = readings.size();
        s.minSystolic = Integer.MAX_VALUE;
        s.minDiastolic = Integer.MAX_VALUE;
        s.minPulse = Integer.MAX_VALUE;

        long sumSys = 0, sumDia = 0, sumPulse = 0;
        int pulseCount = 0;

        for (BloodPressureReading r : readings) {
            sumSys += r.getSystolic();
            sumDia += r.getDiastolic();

            if (r.getSystolic() < s.minSystolic) s.minSystolic = r.getSystolic();
            if (r.getSystolic() > s.maxSystolic) s.maxSystolic = r.getSystolic();
            if (r.getDiastolic() < s.minDiastolic) s.minDiastolic = r.getDiastolic();
            if (r.getDiastolic() > s.maxDiastolic) s.maxDiastolic = r.getDiastolic();

            if (r.getPulse() > 0) {
                sumPulse += r.getPulse();
                pulseCount++;
                if (r.getPulse() < s.minPulse) s.minPulse = r.getPulse();
                if (r.getPulse() > s.maxPulse) s.maxPulse = r.getPulse();
            }

            if (r.isArrhythmia()) s.arrhythmiaCount++;

            BloodPressureReading.BPStatus status = r.getStatus();
            s.statusCounts[status.ordinal()]++;
        }

        s.avgSystolic = (double) sumSys / s.count;
        s.avgDiastolic = (double) sumDia / s.count;
        s.avgPulse = pulseCount > 0 ? (double) sumPulse / pulseCount : 0;
        if (s.minPulse == Integer.MAX_VALUE) s.minPulse = 0;

        return s;
    }

    // ---- Helpers ----

    private List<BloodPressureReading> query(String selection, String[] selectionArgs, String orderBy) {
        SQLiteDatabase db = getReadableDatabase();
        List<BloodPressureReading> list = new ArrayList<>();
        Cursor c = db.query(TABLE_READINGS, null, selection, selectionArgs, null, null, orderBy);
        if (c.moveToFirst()) {
            do {
                list.add(fromCursor(c));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    private ContentValues toContentValues(BloodPressureReading r) {
        ContentValues cv = new ContentValues();
        cv.put(COL_SYSTOLIC, r.getSystolic());
        cv.put(COL_DIASTOLIC, r.getDiastolic());
        cv.put(COL_PULSE, r.getPulse());
        cv.put(COL_ARM, r.getArm());
        cv.put(COL_POSITION, r.getPosition());
        cv.put(COL_ARRHYTHMIA, r.isArrhythmia() ? 1 : 0);
        cv.put(COL_TIMESTAMP, r.getTimestamp());
        cv.put(COL_NOTES, r.getNotes());
        return cv;
    }

    private BloodPressureReading fromCursor(Cursor c) {
        BloodPressureReading r = new BloodPressureReading();
        r.setId(c.getLong(c.getColumnIndexOrThrow(COL_ID)));
        r.setSystolic(c.getInt(c.getColumnIndexOrThrow(COL_SYSTOLIC)));
        r.setDiastolic(c.getInt(c.getColumnIndexOrThrow(COL_DIASTOLIC)));
        r.setPulse(c.getInt(c.getColumnIndexOrThrow(COL_PULSE)));
        r.setArm(c.getString(c.getColumnIndexOrThrow(COL_ARM)));
        r.setPosition(c.getString(c.getColumnIndexOrThrow(COL_POSITION)));
        r.setArrhythmia(c.getInt(c.getColumnIndexOrThrow(COL_ARRHYTHMIA)) == 1);
        r.setTimestamp(c.getLong(c.getColumnIndexOrThrow(COL_TIMESTAMP)));
        r.setNotes(c.getString(c.getColumnIndexOrThrow(COL_NOTES)));
        return r;
    }
}
