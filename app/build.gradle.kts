plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.app_infounsada"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.app_infounsada"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    implementation("com.squareup.picasso:picasso:2.71828") //Convertir URL a Imagen

    implementation("com.squareup.retrofit2:retrofit:2.9.0") //Retrofit2 para consumir API-REST
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Convertir JSON en dataclass

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.6") //Corrutinas
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
