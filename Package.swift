// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "DeviceShake",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "DeviceShake",
            targets: ["DeviceShakePlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "DeviceShakePlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/DeviceShakePlugin"),
        .testTarget(
            name: "DeviceShakePluginTests",
            dependencies: ["DeviceShakePlugin"],
            path: "ios/Tests/DeviceShakePluginTests")
    ]
)