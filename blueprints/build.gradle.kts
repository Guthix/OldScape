val oldscapeCacheVersion: String by rootProject.extra

dependencies {
    implementation(project(":dimensions"))
    api(group = "com.github.Guthix", name = "Oldscape-Cache", version = oldscapeCacheVersion)
}