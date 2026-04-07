plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services) // USA O ALIAS AQUI
    kotlin("plugin.serialization") version libs.versions.kotlin
}




android {
    namespace = "com.example.bookreadanddownloadforfree"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.bookreadanddownloadforfree"
        minSdk = 25
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    // ADICIONE ESTE BLOCO LOGO ABAIXO DE compileOptions
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.window.size.class1.android)
    implementation(libs.androidx.material3.android)
   // implementation(libs.firebase.auth.ktx)
    implementation(libs.googleid)
    // implementation(libs.androidx.ui.desktop)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Dagger Hilt
    //implementation(libs.hilt.android)
    //ksp(libs.hilt.android.compiler)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.sqlite.bundled)
    implementation(libs.koin.compose)
    //implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.androidx.navigation)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)

    api(libs.koin.core)

    implementation(libs.bundles.ktor)
    implementation(libs.bundles.coil)



    //LocalStore
    implementation(libs.datastore.preferences)



    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.ktor.client.okhttp)

    //coreSplashScreen
    implementation(libs.core.splashscreen)


    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
   // implementation("com.github.barteksc:android-pdf-viewer:3.2.0")//beta
// Versão estável
    //implementation("com.github.barteksc:android-pdf-viewer:3.2.0-beta.1")
    //implementation("com.github.barteksc:android-pdf-viewer:2.8.2")
// Versão mais testada
    // Para trabalhar com arquivos
    implementation("androidx.documentfile:documentfile:1.0.1")
//qual aqui e para download  // Ktor para API e download
 //   implementation("io.ktor:ktor-client-core:2.3.7")
 //   implementation("io.ktor:ktor-client-okhttp:2.3.7")

    //fonts
    implementation("androidx.compose.ui:ui-text-google-fonts:1.5.3")
   // implementation("androidx.compose.material:material-icons-extended:<versão>")


    // ADICIONE ESTAS DUAS LINHAS:
    implementation("androidx.compose.material:material-icons-core:1.7.8")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    //TopAppBar mude de cor dinamicamente com base na capa do livro, a forma mais moderna e eficiente no Android é usar a biblioteca Palette API.
    //
    //Ela analisa o Bitmap da imagem e extrai as cores predominantes (Vibrant, Muted, Dark, Light).
    implementation("androidx.palette:palette-ktx:1.0.0")


    // --- BLOCO FIREBASE CORRETO ---
    // Use o BOM para gerenciar as versões automaticamente
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))

    // IMPORTANTE: Use apenas "firebase-auth" (sem o -ktx).
    // Nas versões novas, as funções Kotlin já vêm incluídas na principal.
    implementation("com.google.firebase:firebase-auth")

    // Google Auth e Credential Manager
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Essencial para usar o .await() do Firebase
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")
    // ------------------------------

    // Remova esta linha se ela ainda estiver lá, pois ela está chamando a versão inexistente 24.0.1:
    // implementation(libs.firebase.auth.ktx)

/*
    // Import the Firebase BoM
     implementation(platform("com.google.firebase:firebase-bom:34.11.0"))

    // FIREBASE & GOOGLE AUTH (Limpo e organizado)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.auth)
  //  implementation(libs.kotlinx.coroutines.play-services) // Essencial para o .await()
    implementation("com.google.firebase:firebase-analytics")
// 4. Necessário para usar o .await() nas Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.0")

 */


}
