package com.summerlauncher;

import com.google.gson.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

public class MinecraftLauncher {

    private static final String VERSION = "1.21.11";

    private static final File MC_DIR = new File(
            System.getenv("APPDATA"),
            ".minecraft"
    );

    private static final File VERSION_DIR =
            new File(MC_DIR, "versions\\" + VERSION);

    private static final File VERSION_JSON =
            new File(VERSION_DIR, VERSION + ".json");

    private static final File CLIENT_JAR =
            new File(VERSION_DIR, VERSION + ".jar");

    private static final File LIBRARIES =
            new File(MC_DIR, "libraries");

    private static final File NATIVES =
            new File(
                    MC_DIR,
                    "versions\\" + VERSION + "\\natives"
            );

    public static void launch() throws Exception {

        LaunchLog.show();

        LaunchLog.log("================================");
        LaunchLog.log("SUMMER CLIENT LAUNCH SYSTEM");
        LaunchLog.log("================================");

        LaunchLog.log("Minecraft version: " + VERSION);

        checkFiles();

        JsonObject version = loadVersionJson();

        LaunchLog.log("Version JSON loaded.");
        LaunchLog.log("Main class: " +
                version.get("mainClass").getAsString());

        prepareNatives(version);

        List<String> classpath = buildClasspath(version);

        LaunchLog.log(
                "Classpath entries: " + classpath.size()
        );

        String java = findJava21();

        LaunchLog.log("Java executable:");
        LaunchLog.log(java);

        /*
         * Real Minecraft authentication is required here.
         *
         * These values must come from a legitimate
         * Microsoft/Minecraft authentication flow.
         */
        String username = System.getenv("SUMMER_MC_USERNAME");
        String uuid = System.getenv("SUMMER_MC_UUID");
        String accessToken = System.getenv("SUMMER_MC_ACCESS_TOKEN");
        String xuid = System.getenv("SUMMER_MC_XUID");

        if (username == null ||
                uuid == null ||
                accessToken == null) {

            LaunchLog.log("--------------------------------");
            LaunchLog.log("Minecraft authentication missing.");
            LaunchLog.log("--------------------------------");

            throw new IOException(
                    "Summer Client is ready to launch Minecraft,\n\n"
                    + "but no authenticated Minecraft session is available.\n\n"
                    + "Microsoft login needs to be added next."
            );
        }

        List<String> command = new ArrayList<>();

        command.add(java);

        command.add("-Xmx4G");
        command.add("-Xms512M");

        command.add(
                "-Djava.library.path=" +
                NATIVES.getAbsolutePath()
        );

        command.add(
                "-Djna.tmpdir=" +
                NATIVES.getAbsolutePath()
        );

        command.add(
                "-Dorg.lwjgl.system.SharedLibraryExtractPath=" +
                NATIVES.getAbsolutePath()
        );

        command.add(
                "-Dio.netty.native.workdir=" +
                NATIVES.getAbsolutePath()
        );

        command.add("-cp");

        command.add(String.join(
                File.pathSeparator,
                classpath
        ));

        command.add(
                version.get("mainClass").getAsString()
        );

        command.add("--username");
        command.add(username);

        command.add("--version");
        command.add(VERSION);

        command.add("--gameDir");
        command.add(MC_DIR.getAbsolutePath());

        command.add("--assetsDir");
        command.add(
                new File(MC_DIR, "assets").getAbsolutePath()
        );

        String assets = version
                .get("assets")
                .getAsString();

        command.add("--assetIndex");
        command.add(assets);

        command.add("--uuid");
        command.add(uuid);

        command.add("--accessToken");
        command.add(accessToken);

        command.add("--versionType");
        command.add("release");

        if (xuid != null && !xuid.isBlank()) {
            command.add("--xuid");
            command.add(xuid);
        }

        LaunchLog.log("--------------------------------");
        LaunchLog.log("Starting Minecraft...");
        LaunchLog.log("--------------------------------");

        ProcessBuilder builder =
                new ProcessBuilder(command);

        builder.directory(MC_DIR);
        builder.redirectErrorStream(true);

        Process minecraft = builder.start();

        LaunchLog.log("Minecraft process started.");
        LaunchLog.log("PID: " + minecraft.pid());

        Thread outputThread = new Thread(() -> {
            try {
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        minecraft.getInputStream()
                                )
                        );

                String line;

                while ((line = reader.readLine()) != null) {
                    LaunchLog.log(line);
                }

                int exitCode = minecraft.waitFor();

                LaunchLog.log(
                        "Minecraft exited with code: " + exitCode
                );

            } catch (Exception e) {
                LaunchLog.log(
                        "Process logging error: "
                                + e.getMessage()
                );
            }
        });

        outputThread.setDaemon(true);
        outputThread.start();
    }

    private static void checkFiles()
            throws IOException {

        LaunchLog.log("Checking Minecraft files...");

        if (!VERSION_DIR.exists()) {
            throw new IOException(
                    "Version directory not found:\n"
                            + VERSION_DIR
            );
        }

        if (!VERSION_JSON.exists()) {
            throw new IOException(
                    "Version JSON not found:\n"
                            + VERSION_JSON
            );
        }

        if (!CLIENT_JAR.exists()) {
            throw new IOException(
                    "Minecraft client JAR not found:\n"
                            + CLIENT_JAR
            );
        }

        if (!LIBRARIES.exists()) {
            throw new IOException(
                    "Minecraft libraries directory not found."
            );
        }

        LaunchLog.log("Minecraft installation found.");
        LaunchLog.log("Client JAR: " +
                CLIENT_JAR.length() + " bytes");
    }

    private static JsonObject loadVersionJson()
            throws IOException {

        try (Reader reader =
                     new FileReader(VERSION_JSON)) {

            return JsonParser.parseReader(reader)
                    .getAsJsonObject();
        }
    }

    private static void prepareNatives(
            JsonObject version
    ) throws IOException {

        LaunchLog.log("Preparing native libraries...");

        if (!NATIVES.exists()) {
            NATIVES.mkdirs();
        }

        JsonArray libraries =
                version.getAsJsonArray("libraries");

        int extracted = 0;

        for (JsonElement element : libraries) {

            JsonObject library =
                    element.getAsJsonObject();

            if (!allowedOnWindows(library)) {
                continue;
            }

            if (!library.has("downloads")) {
                continue;
            }

            JsonObject downloads =
                    library.getAsJsonObject("downloads");

            if (!downloads.has("classifiers")) {
                continue;
            }

            JsonObject classifiers =
                    downloads.getAsJsonObject("classifiers");

            String classifier = null;

            if (classifiers.has("natives-windows")) {
                classifier = "natives-windows";
            } else if (classifiers.has(
                    "natives-windows-64"
            )) {
                classifier = "natives-windows-64";
            }

            if (classifier == null) {
                continue;
            }

            JsonObject artifact =
                    classifiers.getAsJsonObject(
                            classifier
                    );

            if (!artifact.has("path")) {
                continue;
            }

            File jar = new File(
                    LIBRARIES,
                    artifact.get("path").getAsString()
            );

            if (!jar.exists()) {
                continue;
            }

            extractNativeJar(jar);
            extracted++;
        }

        LaunchLog.log(
                "Native libraries prepared: "
                        + extracted
        );
    }

    private static void extractNativeJar(
            File jar
    ) throws IOException {

        try (ZipInputStream zip =
                     new ZipInputStream(
                             new FileInputStream(jar)
                     )) {

            ZipEntry entry;

            while ((entry = zip.getNextEntry())
                    != null) {

                if (entry.isDirectory()) {
                    continue;
                }

                String name = entry.getName();

                if (name.contains("/")
                        || name.contains("\\")
                        || name.endsWith(".class")
                        || name.endsWith(".pom")
                        || name.endsWith(".sha1")
                        || name.endsWith(".txt")) {
                    continue;
                }

                File output =
                        new File(NATIVES, name);

                try (FileOutputStream out =
                             new FileOutputStream(output)) {

                    byte[] buffer = new byte[8192];
                    int read;

                    while ((read =
                            zip.read(buffer)) != -1) {

                        out.write(buffer, 0, read);
                    }
                }
            }
        }
    }

    private static List<String> buildClasspath(
            JsonObject version
    ) throws IOException {

        LaunchLog.log("Building Minecraft classpath...");

        List<String> result =
                new ArrayList<>();

        JsonArray libraries =
                version.getAsJsonArray("libraries");

        for (JsonElement element : libraries) {

            JsonObject library =
                    element.getAsJsonObject();

            if (!allowedOnWindows(library)) {
                continue;
            }

            if (!library.has("downloads")) {
                continue;
            }

            JsonObject downloads =
                    library.getAsJsonObject("downloads");

            if (!downloads.has("artifact")) {
                continue;
            }

            JsonObject artifact =
                    downloads.getAsJsonObject("artifact");

            if (!artifact.has("path")) {
                continue;
            }

            String path =
                    artifact.get("path").getAsString();

            if (path.contains("natives-")) {
                continue;
            }

            File jar =
                    new File(LIBRARIES, path);

            if (jar.exists()) {
                result.add(jar.getAbsolutePath());
            }
        }

        result.add(CLIENT_JAR.getAbsolutePath());

        return result;
    }

    private static boolean allowedOnWindows(
            JsonObject library
    ) {

        if (!library.has("rules")) {
            return true;
        }

        JsonArray rules =
                library.getAsJsonArray("rules");

        boolean allowed = false;

        for (JsonElement element : rules) {

            JsonObject rule =
                    element.getAsJsonObject();

            String action =
                    rule.get("action").getAsString();

            if (!rule.has("os")) {
                allowed = action.equals("allow");
                continue;
            }

            JsonObject os =
                    rule.getAsJsonObject("os");

            if (os.has("name")) {

                String name =
                        os.get("name").getAsString();

                if (!"windows".equals(name)) {
                    continue;
                }
            }

            allowed = action.equals("allow");
        }

        return allowed;
    }

    private static String findJava21()
            throws IOException {

        String javaHome =
                System.getenv("JAVA_HOME");

        if (javaHome != null) {

            File java =
                    new File(
                            javaHome,
                            "bin\\java.exe"
                    );

            if (java.exists()) {
                return java.getAbsolutePath();
            }
        }

        String java =
                System.getProperty("java.home")
                        + "\\bin\\java.exe";

        if (new File(java).exists()) {
            return java;
        }

        throw new IOException(
                "Java executable could not be found."
        );
    }
}
