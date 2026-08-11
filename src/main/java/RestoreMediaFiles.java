
import java.io.*;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.*;


public class RestoreMediaFiles {
    public static void main(String[] args) throws IOException {
        Set<String> jpgs = new HashSet<>();

        String inputpath = "/Users/nick/Desktop/Programming.txt";

        Pattern pattern = Pattern.compile("[\\w-]+\\.jpg");
        Pattern pattern2 = Pattern.compile("[\\w-]+\\.webp");

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(inputpath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);

                while (matcher.find()) {
                    jpgs.add(matcher.group());
                }
            }
        }
        String source = "/Users/nick/Desktop/anki_image_backup";
        String destination = "/Users/nick/Library/Application Support/Anki2/User 1/collection.media";


        jpgs.forEach(jpg -> {
            moveImage(jpg, source, destination);
        });

    }

    public static void moveImage(String filename, String sourceDir, String destDir) {
        Path source = Paths.get(sourceDir, filename);
        Path destination = Paths.get(destDir, filename);

        try {
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Moved: " + filename);
        } catch (NoSuchFileException e) {
            System.out.println("Not found: " + filename);
        } catch (IOException e) {
            System.out.println("Failed to move " + filename);
            e.printStackTrace();
        }
    }

}



