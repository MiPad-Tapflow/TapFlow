plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

}

android {
    namespace = "cn.ljlVink.Tapflow"
    compileSdk = 34

    defaultConfig {
        applicationId = "cn.ljlVink.Tapflow"
        minSdk = 34
        targetSdk = 34
        versionCode = 2
        versionName = "V2.0_202405_ljlVink"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(	"com.github.GrenderG:Toasty:1.5.2")
    implementation ("androidx.navigation:navigation-compose:2.7.7")
    val libsuVersion = "5.2.1"
    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.33.2-alpha")

    implementation ("com.github.getActivity:XXPermissions:18.5")
    implementation ("com.github.topjohnwu.libsu:core:${libsuVersion}")
    implementation ("com.github.topjohnwu.libsu:service:${libsuVersion}")
    implementation ("com.github.topjohnwu.libsu:nio:${libsuVersion}")

    implementation("androidx.compose.material:material:1.6.7")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
}