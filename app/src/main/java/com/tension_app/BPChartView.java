package com.tension_app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public class BPChartView extends View {

    private List<BloodPressureReading> readings;

    private final Paint paintSystolic = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintDiastolic = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintDot = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintGrid = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintFillSys = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintFillDia = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Path pathSys = new Path();
    private final Path pathDia = new Path();
    private final Path fillSys = new Path();
    private final Path fillDia = new Path();

    public BPChartView(Context context) { this(context, null); }
    public BPChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paintSystolic.setColor(0xFF4A6CF7);
        paintSystolic.setStrokeWidth(3f);
        paintSystolic.setStyle(Paint.Style.STROKE);
        paintSystolic.setStrokeCap(Paint.Cap.ROUND);
        paintSystolic.setStrokeJoin(Paint.Join.ROUND);

        paintDiastolic.setColor(0xFFEC4899);
        paintDiastolic.setStrokeWidth(3f);
        paintDiastolic.setStyle(Paint.Style.STROKE);
        paintDiastolic.setStrokeCap(Paint.Cap.ROUND);
        paintDiastolic.setStrokeJoin(Paint.Join.ROUND);

        paintDot.setStyle(Paint.Style.FILL);

        paintGrid.setColor(0xFFE5E7EB);
        paintGrid.setStrokeWidth(1f);
        paintGrid.setStyle(Paint.Style.STROKE);

        paintText.setColor(0xFF94A3B8);
        paintText.setTextSize(28f);
        paintText.setAntiAlias(true);

        paintFillSys.setColor(0x204A6CF7);
        paintFillSys.setStyle(Paint.Style.FILL);

        paintFillDia.setColor(0x20EC4899);
        paintFillDia.setStyle(Paint.Style.FILL);
    }

    public void setReadings(List<BloodPressureReading> readings) {
        this.readings = readings;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (readings == null || readings.isEmpty()) {
            drawEmpty(canvas);
            return;
        }

        // Show at most 14 readings
        List<BloodPressureReading> displayReadings = readings.size() > 14
            ? readings.subList(readings.size() - 14, readings.size())
            : readings;

        // Reverse so oldest is leftmost
        java.util.Collections.reverse(displayReadings);

        float w = getWidth();
        float h = getHeight();
        float padL = 80f, padR = 20f, padT = 20f, padB = 40f;
        float chartW = w - padL - padR;
        float chartH = h - padT - padB;

        // Find range
        int minVal = 999, maxVal = 0;
        for (BloodPressureReading r : displayReadings) {
            if (r.getSystolic() < minVal) minVal = r.getSystolic();
            if (r.getDiastolic() < minVal) minVal = r.getDiastolic();
            if (r.getSystolic() > maxVal) maxVal = r.getSystolic();
            if (r.getDiastolic() > maxVal) maxVal = r.getDiastolic();
        }
        // Add padding to range
        minVal = Math.max(40, minVal - 20);
        maxVal = Math.min(240, maxVal + 20);

        int n = displayReadings.size();

        // Draw grid lines
        int[] gridVals = {60, 80, 100, 120, 140, 160, 180, 200};
        for (int g : gridVals) {
            if (g < minVal || g > maxVal) continue;
            float y = valueToY(g, minVal, maxVal, padT, chartH);
            canvas.drawLine(padL, y, padL + chartW, y, paintGrid);
            canvas.drawText(String.valueOf(g), 0, y + 9, paintText);
        }

        // Compute points
        float[] xSys = new float[n], ySys = new float[n];
        float[] xDia = new float[n], yDia = new float[n];

        for (int i = 0; i < n; i++) {
            float x = padL + (n == 1 ? chartW / 2f : i * chartW / (n - 1));
            xSys[i] = xDia[i] = x;
            ySys[i] = valueToY(displayReadings.get(i).getSystolic(), minVal, maxVal, padT, chartH);
            yDia[i] = valueToY(displayReadings.get(i).getDiastolic(), minVal, maxVal, padT, chartH);
        }

        // Fill area under systolic
        fillSys.reset();
        fillSys.moveTo(xSys[0], padT + chartH);
        fillSys.lineTo(xSys[0], ySys[0]);
        for (int i = 1; i < n; i++) fillSys.lineTo(xSys[i], ySys[i]);
        fillSys.lineTo(xSys[n - 1], padT + chartH);
        fillSys.close();
        canvas.drawPath(fillSys, paintFillSys);

        // Fill area under diastolic
        fillDia.reset();
        fillDia.moveTo(xDia[0], padT + chartH);
        fillDia.lineTo(xDia[0], yDia[0]);
        for (int i = 1; i < n; i++) fillDia.lineTo(xDia[i], yDia[i]);
        fillDia.lineTo(xDia[n - 1], padT + chartH);
        fillDia.close();
        canvas.drawPath(fillDia, paintFillDia);

        // Draw systolic line
        pathSys.reset();
        pathSys.moveTo(xSys[0], ySys[0]);
        for (int i = 1; i < n; i++) pathSys.lineTo(xSys[i], ySys[i]);
        canvas.drawPath(pathSys, paintSystolic);

        // Draw diastolic line
        pathDia.reset();
        pathDia.moveTo(xDia[0], yDia[0]);
        for (int i = 1; i < n; i++) pathDia.lineTo(xDia[i], yDia[i]);
        canvas.drawPath(pathDia, paintDiastolic);

        // Draw dots
        for (int i = 0; i < n; i++) {
            paintDot.setColor(0xFF4A6CF7);
            canvas.drawCircle(xSys[i], ySys[i], 6f, paintDot);
            paintDot.setColor(Color.WHITE);
            canvas.drawCircle(xSys[i], ySys[i], 3f, paintDot);

            paintDot.setColor(0xFFEC4899);
            canvas.drawCircle(xDia[i], yDia[i], 6f, paintDot);
            paintDot.setColor(Color.WHITE);
            canvas.drawCircle(xDia[i], yDia[i], 3f, paintDot);
        }

        // Restore list order
        java.util.Collections.reverse(displayReadings);
    }

    private float valueToY(int value, int min, int max, float padT, float chartH) {
        float ratio = 1f - (float)(value - min) / (max - min);
        return padT + ratio * chartH;
    }

    private void drawEmpty(Canvas canvas) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(0xFF94A3B8);
        p.setTextSize(36f);
        p.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Sin datos suficientes", getWidth() / 2f, getHeight() / 2f, p);
    }
}
