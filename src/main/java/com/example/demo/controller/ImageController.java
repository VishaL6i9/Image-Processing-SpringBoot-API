package com.example.demo.controller;

import filters.GrayscaleFilter;
import filters.InvertFilter;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

@RestController
@RequestMapping("/api/image")
@CrossOrigin(origins = "http://localhost:3000")
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
    private static final String UPLOAD_DIR = "uploads";

    @PostMapping("/upload")
public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
    try {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        logger.info("File uploaded successfully: {}", filePath);

        return ResponseEntity.ok("http://localhost:8080/api/image/get/" + fileName);
    } catch (IOException e) {
        logger.error("Failed to upload image: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image: " + e.getMessage());
    }
}

    @GetMapping("/get/{fileName}")
public ResponseEntity<byte[]> getImage(@PathVariable String fileName) {
    try {
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            logger.error("Invalid file name: {}", fileName);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file name".getBytes());
        }

        Path imagePath = Paths.get(UPLOAD_DIR).resolve(fileName);

        if (!Files.exists(imagePath)) {
            logger.error("File not found: {}", imagePath);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found".getBytes());
        }

        byte[] imageBytes = Files.readAllBytes(imagePath);

        String mimeType = Files.probeContentType(imagePath);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(mimeType));
        headers.setContentLength(imageBytes.length);

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    } catch (IOException e) {
        logger.error("Failed to retrieve image: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(("Failed to retrieve image: " + e.getMessage()).getBytes());

    }
}

   @PostMapping("/invert")
public ResponseEntity<String> invertImage(@RequestParam("previousImage") String previousImage) {
    try {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path previousImagePath = uploadPath.resolve(previousImage);
        BufferedImage originalImage = ImageIO.read(previousImagePath.toFile());
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        String fileName = UUID.randomUUID() + "_" + previousImage;
        Path filePath = uploadPath.resolve(fileName);

        BufferedImage processedImage = Thumbnails.of(originalImage)
                .size(width, height)
                .addFilter(new InvertFilter())
                .asBufferedImage();

        ImageIO.write(processedImage, "png", filePath.toFile());

        return ResponseEntity.ok("http://localhost:8080/api/image/get/" + fileName);
    } catch (IOException e) {
        logger.error("Failed to invert image: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to invert image: " + e.getMessage());
    }
}

  @PostMapping("/flip")
public ResponseEntity<String> flipImage(@RequestParam("previousImage") String previousImage, @RequestParam("direction") String direction) {
    try {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path previousImagePath = uploadPath.resolve(previousImage);
        BufferedImage originalImage = ImageIO.read(previousImagePath.toFile());
        BufferedImage flippedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
        Graphics2D g = flippedImage.createGraphics();

        if ("horizontal".equalsIgnoreCase(direction)) {
            g.drawImage(originalImage, originalImage.getWidth(), 0, 0, originalImage.getHeight(), 0, 0, originalImage.getWidth(), originalImage.getHeight(), null);
        } else if ("vertical".equalsIgnoreCase(direction)) {
            g.drawImage(originalImage, 0, originalImage.getHeight(), originalImage.getWidth(), 0, 0, 0, originalImage.getWidth(), originalImage.getHeight(), null);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid flip direction");
        }

        g.dispose();

        String fileName = UUID.randomUUID() + "_" + previousImage;
        Path filePath = uploadPath.resolve(fileName);
        ImageIO.write(flippedImage, "png", filePath.toFile());

        return ResponseEntity.ok("http://localhost:8080/api/image/get/" + fileName);
    } catch (IOException e) {
        logger.error("Failed to flip image: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to flip image: " + e.getMessage());
    }
}

  @PostMapping("/rotate/{degrees}")
public ResponseEntity<String> rotateImage(@RequestParam("previousImage") String previousImage, @PathVariable int degrees) {
    try {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path previousImagePath = uploadPath.resolve(previousImage);
        BufferedImage originalImage = ImageIO.read(previousImagePath.toFile());

        double angle = Math.toRadians(degrees);
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int newWidth = (int) Math.round(Math.abs(originalWidth * Math.cos(angle)) + Math.abs(originalHeight * Math.sin(angle)));
        int newHeight = (int) Math.round(Math.abs(originalWidth * Math.sin(angle)) + Math.abs(originalHeight * Math.cos(angle)));

        String fileName = UUID.randomUUID() + "_" + previousImage;
        Path filePath = uploadPath.resolve(fileName);

        BufferedImage rotatedImage = Thumbnails.of(originalImage)
                .size(newWidth, newHeight)
                .rotate(degrees)
                .asBufferedImage();

        ImageIO.write(rotatedImage, "png", filePath.toFile());

        return ResponseEntity.ok("http://localhost:8080/api/image/get/" + fileName);
    } catch (IOException e) {
        logger.error("Failed to rotate image: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to rotate image: " + e.getMessage());
    }
}


  @PostMapping("/resize")
public ResponseEntity<String> resizeImage(@RequestParam("previousImage") String previousImage, @RequestParam int width, @RequestParam int height) {
    try {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path previousImagePath = uploadPath.resolve(previousImage);
        BufferedImage originalImage = ImageIO.read(previousImagePath.toFile());

        String fileName = UUID.randomUUID() + "_" + previousImage;
        Path filePath = uploadPath.resolve(fileName);

        BufferedImage resizedImage = Thumbnails.of(originalImage)
                .size(width, height)
                .asBufferedImage();

        ImageIO.write(resizedImage, "png", filePath.toFile());

        return ResponseEntity.ok("http://localhost:8080/api/image/get/" + fileName);
    } catch (IOException e) {
        logger.error("Failed to resize image: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to resize image: " + e.getMessage());
    }
}

   @PostMapping("/grayscale")
public ResponseEntity<String> grayscaleImage(@RequestParam("previousImage") String previousImage) {
    try {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path previousImagePath = uploadPath.resolve(previousImage);
        BufferedImage originalImage = ImageIO.read(previousImagePath.toFile());
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        String fileName = UUID.randomUUID() + "_" + previousImage;
        Path filePath = uploadPath.resolve(fileName);

        BufferedImage processedImage = Thumbnails.of(originalImage)
                .size(width, height)
                .addFilter(new GrayscaleFilter())
                .asBufferedImage();

        ImageIO.write(processedImage, "png", filePath.toFile());

        return ResponseEntity.ok("http://localhost:8080/api/image/get/" + fileName);
    } catch (IOException e) {
        logger.error("Failed to convert image to grayscale: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to convert image to grayscale: " + e.getMessage());
    }
}
}