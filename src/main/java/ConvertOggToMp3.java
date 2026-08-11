import java.io.IOException;
import java.nio.file.*;

public class ConvertOggToMp3 {

    static String src = "/Users/nick/Library/Application Support/Anki2/User 1/collection.media";
    static String dest = "/Users/nick/Desktop/OGG";
    static String mp3Path = "/Users/nick/Desktop/MP3";

    public static void main(String[] args) throws Exception {

//        copyOggFiles(src, dest);
        convert(dest, mp3Path);
    }

    public static void convert(String src, String dst) throws Exception {
        Path srcdir = Paths.get(src);
        Path dstdir = Paths.get(dst);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(srcdir, "*.ogg")) {

            for (Path ogg : stream) {

                String name = ogg.getFileName().toString();
                name = name.substring(0, name.lastIndexOf('.'));

                Path mp3 = dstdir.resolve(name + ".mp3");

                Process process = new ProcessBuilder(
                        "ffmpeg",
                        "-y",
                        "-i", ogg.toString(),
                        "-codec:a", "libmp3lame",
                        "-q:a", "2",              // VBR ~190 kbps
                        mp3.toString())
                        .inheritIO()
                        .start();

                int exit = process.waitFor();

                if (exit == 0) {
                    System.out.println("Converted: " + ogg.getFileName());
                } else {
                    System.err.println("Failed: " + ogg.getFileName());
                }
            }
        }
    }
    public static void copyOggFiles(String src, String dst) throws IOException {

        Path srcDir = Paths.get(src);
        Path dstDir = Paths.get(dst);
        Files.createDirectories(dstDir);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(srcDir, "*.ogg")) {
            for (Path srcFile : stream) {
                Path dstFile = dstDir.resolve(srcFile.getFileName());

                Files.copy(
                        srcFile,
                        dstFile,
                        StandardCopyOption.REPLACE_EXISTING
                );

                System.out.println("Copied: " + srcFile.getFileName());
            }
        }
    }

}