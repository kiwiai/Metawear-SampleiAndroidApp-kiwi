# MetaWear Android App #

## About ##
This app provides examples of how to use the MetaWear API and a simple app for interacting with the board.  It uses the sample BluetoothLeGatt app on the Android developer page and the Nordic nRF Toolbox app as references.

[http://developer.android.com/samples/BluetoothLeGatt/index.html](http://developer.android.com/samples/BluetoothLeGatt/index.html)  
[https://play.google.com/store/apps/details?id=no.nordicsemi.android.nrftoolbox&hl=en](https://play.google.com/store/apps/details?id=no.nordicsemi.android.nrftoolbox&hl=en)

kiwi integration
1) sign up for an account at developer.kiwiwearables.com

2) change LN81 and LN83, in AccelerometerFragment.java to your user account credentails, and change LN27 in KiwiHelper.java to your account credentials

3) add device name to your account in developer.kiwiwearables.com to see live raw data, capture and create motions

Sample iOS App for MetaWear (www.mbientlab.com)

## Build ##
The API was built in Android Studio 1.3.1. It is targeted for Android 5.1.1 (SDK 22) with Android 4.3 (SDK 18) as the minimum required SDK, and requires a JDK compiler compliance level of 1.7.
