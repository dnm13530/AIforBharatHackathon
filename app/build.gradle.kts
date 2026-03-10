import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

// Load local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.manasa.olympiadedgeai"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.manasa.olympiadedgeai"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // AWS Credentials from local.properties
        val accessKey: String = localProperties.getProperty("AWS_ACCESS_KEY") ?: ""
        val secretKey: String = localProperties.getProperty("AWS_SECRET_KEY") ?: ""
        val apiUrl: String = localProperties.getProperty("API_GATEWAY_URL") ?: ""
        
        buildConfigField("String", "AWS_ACCESS_KEY", "\"$accessKey\"")
        buildConfigField("String", "AWS_SECRET_KEY", "\"$secretKey\"")
        buildConfigField("String", "API_GATEWAY_URL", "\"$apiUrl\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.room.ktx)
    
    // AWS SDK Core & Cognito
    implementation(libs.aws.core)
    implementation(libs.aws.cognito) // Added this line
    
    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.gson)
    implementation(libs.okhttp.logging)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
