import Foundation

@objc public class DeviceShake: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
