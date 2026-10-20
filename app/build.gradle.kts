
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "lk.pulsar.whatsappbridge"
    compileSdk = 35

    defaultConfig {
        applicationId = "lk.pulsar.whatsappbridge"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.2-sender-name-test"
    }
}
