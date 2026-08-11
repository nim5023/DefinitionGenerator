import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;

public class ReduceMediaSize {

    public static void main(String[] args) throws IOException {


        String outputPath = "/Users/nick/Desktop/anki_media";
        Path outputDir = Paths.get(outputPath);
        Files.createDirectories(outputDir);

        String inputPaths = "/Users/nick/Library/Application Support/Anki2/User 1/collection.media";
        Path inputDir = Paths.get(inputPaths);

        Files.walk(inputDir)
                .filter(Files::isRegularFile)
                .filter(p -> {
                    String s = p.toString().toLowerCase();
                    return s.endsWith(".jpg")
                            || s.endsWith(".jpeg")
                            || s.endsWith(".webp");
                })
                .forEach(path -> {

                    try {
                        BufferedImage image = ImageIO.read(path.toFile());

                        if (image == null) {
                            System.err.println("Unsupported image: " + path);
                            return;
                        }

                        File out = outputDir.resolve(path.getFileName()).toFile();
                        if (image.getWidth() > 300 || image.getHeight() > 300) {
                            Thumbnails.of(path.toFile())
                                    .size(300, 300)      // preserves aspect ratio
                                    .outputQuality(0.85)
                                    .toFile(out);
                        } else {
//                            Thumbnails.of(path.toFile())
//                                    .outputQuality(0.85)
//                                    .scale(1.0)
//                                    .toFile(out);
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                });
    }

}
