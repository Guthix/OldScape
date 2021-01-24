val nettyVersion: String by extra("4.1.42.Final")
val jagexByteBufVersion: String by extra("1.0.3")

dependencies {
    implementation(project(":dim"))
    implementation(group = "io.netty", name = "netty-all", version = nettyVersion)
    api(group = "io.guthix", name = "jagex-bytebuf", version = jagexByteBufVersion)
}