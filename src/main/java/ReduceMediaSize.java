import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.coobird.thumbnailator.Thumbnails;

public class ReduceMediaSize {

    public static void main(String[] args) throws IOException {


        String outputPath = "C:/images/output";
        Path outputDir = Paths.get(outputPath);
        Files.createDirectories(outputDir);

        String inputPaths = "C:/images/output";
        Path inputDir = Paths.get(inputPaths);
        Files.createDirectories(inputDir);


        Files.walk(inputDir)
                .filter(Files::isRegularFile)
                .filter(p -> {
                    String s = p.toString().toLowerCase();
                    return s.endsWith(".jpg") || s.endsWith(".jpeg");
                })
                .forEach(path -> {

                    File out = outputDir.resolve(path.getFileName()).toFile();

                    try {
                        Thumbnails.of(path.toFile())
                                .size(1920, 1080)      // preserves aspect ratio
                                .outputQuality(0.85)
                                .toFile(out);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

}
