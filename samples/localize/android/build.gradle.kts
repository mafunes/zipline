import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME

plugins {
  id("com.android.application")
  kotlin("android")
}

android {
  namespace = "com.mafunes.zipline.localize"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.mafunes.zipline.localize"
    minSdk = 24
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  compileOptions {
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  buildFeatures {
    compose = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
  }

  packagingOptions {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }

  val samples by signingConfigs.creating {
    storeFile(file("../../samples.keystore"))
    storePassword("javascript")
    keyAlias("javascript")
    keyPassword("javascript")
  }

  buildTypes {
    val debug by getting {
      applicationIdSuffix = ".debug"
      signingConfig = samples
    }
    val release by getting {
      signingConfig = samples
    }
  }
}

dependencies {
  implementation("app.cash.zipline:zipline")
  implementation("app.cash.zipline:zipline-loader")
  implementation("app.cash.zipline:zipline-profiler")
  implementation(projects.samples.localize.presenters)
  implementation(libs.android.material)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.appCompat)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle)
  implementation(libs.androidx.compose.material)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling.preview)
  debugImplementation(libs.androidx.compose.ui.tooling)
  add(PLUGIN_CLASSPATH_CONFIGURATION_NAME, "app.cash.zipline:zipline-kotlin-plugin")
  coreLibraryDesugaring(libs.android.desugarJdkLibs)
}
