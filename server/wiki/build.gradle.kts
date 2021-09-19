plugins {
    application
}

application { mainClass.set("io.guthix.oldscape.wiki.YamlDownloader") }

dependencies {
    implementation(project(":server:world"))
    implementation(project(":server:world:plugins:core:combat"))
    implementation(project(":server:world:plugins:core:equipment"))
    implementation(project(":server:world:plugins:core:monster"))
    implementation(project(":server:world:plugins:core:obj"))
    implementation(project(":server:world:plugins:core:stat"))
    implementation(project(":wiki:downloader"))
    implementation(deps.logback)
    implementation(deps.kotlin.serialization.json)
    implementation(deps.kotlin.serialization.yaml)
}