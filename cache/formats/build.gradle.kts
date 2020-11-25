val jgltfVersion: String by extra("2.0.0")

dependencies {
    api(project(":cache"))
    implementation(group = "de.javagl", name = "jgltf-model", version = jgltfVersion)
}