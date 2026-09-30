plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.neurohelp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.neurohelp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        val apiUrl = providers.gradleProperty("ESPECTROCARE_API_URL")
            .getOrElse("https://espectrocare.onrender.com")
        require(apiUrl.startsWith("https://") && !apiUrl.contains('"') && !apiUrl.contains('\\'))
        buildConfigField("String", "API_BASE_URL", "\"${apiUrl.trimEnd('/')}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures { buildConfig = true }

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation("androidx.cardview:cardview:1.0.0")
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.google.material)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}