import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.johndeweydev.awps"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.johndeweydev"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        val keystoreProperties = rootProject.file("keystore.properties")
        val properties = Properties()
        properties.load(FileInputStream(keystoreProperties))

        getByName("debug") {
            storeFile = file(properties.getProperty("debugStoreFile"))
            storePassword = properties.getProperty("debugPassword")
            keyAlias = properties.getProperty("debugKeyAlias")
            keyPassword  = properties.getProperty("debugKeyPassword")
        }

        create("release") {
            storeFile = file(properties.getProperty("releaseStoreFile"))
            storePassword = properties.getProperty("releasePassword")
            keyAlias = properties.getProperty("releaseKeyAlias")
            keyPassword  = properties.getProperty("releaseKeyPassword")
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
            applicationIdSuffix = ".debug.awps"
        }

        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            applicationIdSuffix = ".release.awps"
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    val roomVersion = "2.6.0"

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.github.mik3y:usb-serial-for-android:3.6.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("it.xabaras.android:recyclerview-swipedecorator:1.4")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Fix for duplicate class, https://gist.github.com/danielcshn/7aa57155d766d46c043fde015f054d40
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.12")

    // Fix for DateTime.now() because it requires minimum api level of 26
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    annotationProcessor("androidx.room:room-compiler:$roomVersion")
}