package com.example.deviceshake;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "DeviceShake")
public class DeviceShakePlugin extends Plugin implements SensorEventListener {

    private static final String TAG = "DeviceShakePlugin";
    
    // Sensor manager and sensor
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean isListening = false;
    
    // Shake detection configuration
    private static final float SHAKE_THRESHOLD = 12.0f;  // Acceleration threshold (m/s²)
    private static final int SHAKE_COOLDOWN_MS = 1000;    // Cooldown between shakes
    private long lastShakeTime = 0;
    
    // Previous acceleration values for high-pass filter
    private float lastX = 0, lastY = 0, lastZ = 0;
    private boolean initialized = false;
    
    @Override
    public void load() {
        super.load();
        // Initialize sensor manager
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        Log.d(TAG, "DeviceShake plugin loaded");
    }

    @PluginMethod
    public void enableListening(PluginCall call) {
        // Check if already listening
        if (isListening) {
            call.resolve();
            return;
        }
        
        // Check if accelerometer is available
        if (accelerometer == null) {
            call.reject("Accelerometer is not available on this device");
            return;
        }
        
        // Register listener with normal delay
        boolean registered = sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI  // ~60ms updates
        );
        
        if (registered) {
            isListening = true;
            initialized = false;
            Log.d(TAG, "Started listening for shake events");
            call.resolve();
        } else {
            call.reject("Failed to register accelerometer listener");
        }
    }

    @PluginMethod
    public void stopListening(PluginCall call) {
        if (sensorManager != null && isListening) {
            sensorManager.unregisterListener(this);
        }
        isListening = false;
        initialized = false;
        Log.d(TAG, "Stopped listening for shake events");
        call.resolve();
    }

    // MARK: - SensorEventListener Methods
    
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }
        
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        
        // Initialize on first reading
        if (!initialized) {
            lastX = x;
            lastY = y;
            lastZ = z;
            initialized = true;
            return;
        }
        
        // Calculate delta (change in acceleration)
        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;
        
        // Store current values for next iteration
        lastX = x;
        lastY = y;
        lastZ = z;
        
        // Calculate acceleration magnitude (how hard the shake is)
        float acceleration = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
        
        // Check if shake threshold is exceeded
        if (acceleration > SHAKE_THRESHOLD) {
            handleShakeDetected();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for shake detection
    }

    // MARK: - Shake Handling
    
    private void handleShakeDetected() {
        long now = System.currentTimeMillis();
        
        // Check cooldown to prevent multiple rapid triggers
        if (now - lastShakeTime < SHAKE_COOLDOWN_MS) {
            return;
        }
        
        lastShakeTime = now;
        
        // Trigger vibration feedback
        triggerVibration();
        
        // Notify JavaScript listeners
        Log.d(TAG, "Shake detected! Notifying listeners...");
        JSObject data = new JSObject();
        notifyListeners("shake", data);
    }

    // MARK: - Vibration Feedback
    
    private void triggerVibration() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        
        if (vibrator == null || !vibrator.hasVibrator()) {
            Log.d(TAG, "No vibrator available");
            return;
        }
        
        // Vibrate for 200 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For Android 8.0 (API 26) and above
            VibrationEffect effect = VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE);
            vibrator.vibrate(effect);
        } else {
            // For older Android versions
            vibrator.vibrate(200);
        }
    }

    // MARK: - Cleanup
    
    @Override
    protected void handleOnDestroy() {
        if (sensorManager != null && isListening) {
            sensorManager.unregisterListener(this);
        }
        super.handleOnDestroy();
    }
}