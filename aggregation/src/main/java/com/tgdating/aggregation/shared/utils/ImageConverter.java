package com.tgdating.aggregation.shared.utils;

import com.tgdating.aggregation.model.ImageConverterRecord;
import com.tgdating.aggregation.shared.exception.InternalServerException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageConverter {
    public static ImageConverterRecord convertImage(String inputPath, String fileName) {
        try {
            BufferedImage image = ImageIO.read(new File(inputPath));
            String outputPath = replaceExtension(inputPath);
            ImageIO.write(image, "webp", new File(outputPath));
            deleteFile(inputPath);
            Path outputPathAsPath = Paths.get(outputPath);
            Long sizeInBytes = Files.size(outputPathAsPath);
            return new ImageConverterRecord(fileName, outputPath, sizeInBytes);
        } catch (Exception e) {
            throw new InternalServerException(
                    "Ошибка конвертации файла",
                    "Ошибка конвертации файла: " + e.getMessage()
            );
        }
    }

    private static String replaceExtension(String path) {
        String pathWithoutExtension = path.substring(0, path.lastIndexOf('.'));
        return pathWithoutExtension + ".webp";
    }

    private static void deleteFile(String path) {
        try {
            Path filePath = Paths.get(path);
            Files.delete(filePath);
        } catch (Exception e) {
            throw new InternalServerException(
                    "Ошибка удаления файла",
                    "Ошибка удаления файла: " + e.getMessage()
            );
        }
    }
}
