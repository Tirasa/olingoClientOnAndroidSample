Sample Android application featuring [Apache Olingo](http://olingo.apache.org) 4.0 client library

### How to test

1. download [Android SDK](http://developer.android.com/sdk/index.html) and unpack somewhere
2. `git clone https://github.com/Tirasa/olingoClientOnAndroidSample.git`
3. `cd olingoClientOnAndroidSample`
4. change the value of `android.sdk.path` property in `pom.xml`
5. build and deploy
 1. `mvn clean package` if you want to manually deploy `target/olingo4-android-sample.apk` to any device
 2. `mvn clean package android:deploy android:run` to automatically deploy and launch on all attached devices
