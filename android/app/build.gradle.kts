plugins {
 id("com.android.application")
 id("org.jetbrains.kotlin.android")
 id("org.jetbrains.kotlin.plugin.compose")
}
android {
 namespace="com.iponinja.app"
 compileSdk=35
 defaultConfig {
  applicationId="com.iponinja.app"
  minSdk=26
  targetSdk=35
  versionCode=1
  versionName="1.0"
  buildConfigField("String","API_BASE_URL","\"https://ipo-o8g1.onrender.com/\"")
 }
 buildFeatures { compose=true; buildConfig=true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}
dependencies {
 implementation("androidx.core:core-ktx:1.15.0")
 implementation("androidx.activity:activity-compose:1.10.1")
 implementation(platform("androidx.compose:compose-bom:2025.01.00"))
 implementation("androidx.compose.ui:ui")
 implementation("androidx.compose.ui:ui-tooling-preview")
 implementation("androidx.compose.material3:material3")
 implementation("androidx.compose.material:material-icons-core")
 implementation("androidx.compose.material:material-icons-extended")
 implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
 implementation("androidx.navigation:navigation-compose:2.8.6")
 implementation("com.squareup.retrofit2:retrofit:2.11.0")
 implementation("com.squareup.retrofit2:converter-gson:2.11.0")
 debugImplementation("androidx.compose.ui:ui-tooling")
}