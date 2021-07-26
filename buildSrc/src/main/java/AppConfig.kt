import org.gradle.api.JavaVersion

object Config {
    const val minSdk = 26
    const val compileSdk = 29
    const val targetSdk = 29
    val javaVersion = JavaVersion.VERSION_1_8

    const val versionCode = 15
    const val versionName = "1.1.7"

    const val androidTestInstrumentation = "androidx.test.runner.AndroidJUnitRunner"
    const val proguardConsumerRules = "consumer-rules.pro"
}