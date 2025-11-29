import Foundation
import Capacitor
import CoreMotion
import AudioToolbox

@objc(DeviceShakePlugin)
public class DeviceShakePlugin: CAPPlugin, CAPBridgedPlugin {
    
    // MARK: - Plugin Identifier
    public let identifier = "DeviceShakePlugin"
    public let jsName = "DeviceShake"
    
    // MARK: - Plugin Methods Declaration
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "enableListening", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "stopListening", returnType: CAPPluginReturnPromise)
    ]
    
    // MARK: - Properties
    private let motionManager = CMMotionManager()
    private var isListening = false
    
    // Shake detection configuration
    private let shakeThreshold: Double = 2.5  // Acceleration threshold (in G's)
    private let shakeCooldown: TimeInterval = 1.0  // Minimum time between shakes
    private var lastShakeTime: Date = Date.distantPast
    
    // MARK: - Enable Listening
    @objc func enableListening(_ call: CAPPluginCall) {
        // Check if already listening
        if isListening {
            call.resolve()
            return
        }
        
        // Check if accelerometer is available
        guard motionManager.isAccelerometerAvailable else {
            call.reject("Accelerometer is not available on this device")
            return
        }
        
        // Configure update interval (60 Hz)
        motionManager.accelerometerUpdateInterval = 1.0 / 60.0
        
        // Start accelerometer updates
        motionManager.startAccelerometerUpdates(to: .main) { [weak self] (data, error) in
            guard let self = self else { return }
            
            if let error = error {
                print("DeviceShake: Accelerometer error - \(error.localizedDescription)")
                return
            }
            
            guard let acceleration = data?.acceleration else { return }
            
            // Calculate total acceleration magnitude
            // Subtract 1G for gravity and get absolute magnitude
            let x = acceleration.x
            let y = acceleration.y
            let z = acceleration.z
            
            // Calculate the magnitude of acceleration vector
            let magnitude = sqrt(x * x + y * y + z * z)
            
            // Check if acceleration exceeds threshold (accounting for gravity ~1G)
            if magnitude > self.shakeThreshold {
                self.handleShakeDetected()
            }
        }
        
        isListening = true
        print("DeviceShake: Started listening for shake events")
        call.resolve()
    }
    
    // MARK: - Stop Listening
    @objc func stopListening(_ call: CAPPluginCall) {
        if motionManager.isAccelerometerActive {
            motionManager.stopAccelerometerUpdates()
        }
        isListening = false
        print("DeviceShake: Stopped listening for shake events")
        call.resolve()
    }
    
    // MARK: - Handle Shake Detection
    private func handleShakeDetected() {
        let now = Date()
        
        // Check cooldown to prevent multiple rapid triggers
        guard now.timeIntervalSince(lastShakeTime) >= shakeCooldown else {
            return
        }
        
        lastShakeTime = now
        
        // Trigger vibration feedback
        triggerVibration()
        
        // Notify JavaScript listeners
        print("DeviceShake: Shake detected! Notifying listeners...")
        notifyListeners("shake", data: [:])
    }
    
    // MARK: - Vibration Feedback
    private func triggerVibration() {
        // Use AudioServices for haptic feedback
        // 1519 = peek, 1520 = pop, 1521 = nope
        AudioServicesPlaySystemSound(1520)
        
        // Alternative: Use Haptic Feedback (iOS 10+)
        if #available(iOS 10.0, *) {
            let generator = UIImpactFeedbackGenerator(style: .heavy)
            generator.prepare()
            generator.impactOccurred()
        }
    }
    
    // MARK: - Cleanup
    deinit {
        if motionManager.isAccelerometerActive {
            motionManager.stopAccelerometerUpdates()
        }
    }
}