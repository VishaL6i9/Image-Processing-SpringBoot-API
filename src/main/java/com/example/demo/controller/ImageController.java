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

    // Helper method to generate a unique filename
    private String generateUniqueFilename(String originalFilename) {
        return UUID.randomUUID().toString().substring(0, 8) + "_" + originalFilename;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadPath);

            String fileName = generateUniqueFilename(file.getOriginalFilename());
            Path filePath = uploadPath.resolve(fileName);

            Thread.sleep(100);

            Files.copy(file.getInputStream(), filePath);

            logger.info("File uploaded successfully: {}", filePath);

            return ResponseEntity.ok("http://localhost:8080/api/image/get/" + fileName);
        } catch (IOException | InterruptedException e) {
            logger.error("Failed to upload image: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/get/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) {
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            logger.error("Invalid file name: {}", fileName);
            return ResponseEntity.badRequest().body("Invalid file name".getBytes());
        }

        Path imagePath = Paths.get(UPLOAD_DIR).resolve(fileName);

        try {
            if (!Files.exists(imagePath)) {
                logger.error("File not found: {}", imagePath);
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = Files.readAllBytes(imagePath);

            // Determine MIME type or default to octet-stream
            String mimeType = Files.probeContentType(imagePath);
            mimeType = (mimeType != null) ? mimeType : "application/octet-stream";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(mimeType));
            headers.setContentLength(imageBytes.length);

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            logger.error("Failed to retrieve image: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Image retrieval failed: " + e.getMessage()).getBytes());
        }
    }

    @PostMapping("/invert")
    public ResponseEntity<String> invertImage(@RequestParam("previousImage") String previousImage) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadPath);

            BufferedImage originalImage = ImageIO.read(uploadPath.resolve(previousImage).toFile());

            // invert
            BufferedImage invertedImage = Thumbnails.of(originalImage)
                    .size(originalImage.getWidth(), originalImage.getHeight())
                    .addFilter(new InvertFilter())
                    .asBufferedImage();

            String fileName = generateUniqueFilename("inverted_" + previousImage);
            Path filePath = uploadPath.resolve(fileName);

            ImageIO.write(invertedImage, "png", filePath.toFile());

            return ResponseEntity.ok("http://localhost:8080/api/image/get/" + fileName);
        } catch (IOException e) {
            logger.error("Invert operation failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invert failed: " + e.getMessage());
        }
    }

    @PostMapping("/flip")
    public ResponseEntity<String> flipImage(@RequestParam("previousImage") String previousImage, @RequestParam("direction") String direction) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadPath);

            BufferedImage originalImage = ImageIO.read(uploadPath.resolve(previousImage).toFile());
            BufferedImage flippedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
            Graphics2D g = flippedImage.createGraphics();

            // Flip
            if ("horizontal".equalsIgnoreCase(direction)) {
                g.drawImage(originalImage, originalImage.getWidth(), 0, -originalImage.getWidth(), originalImage.getHeight(), null);
            } else if ("vertical".equalsIgnoreCase(direction)) {
                g.drawImage(originalImage, 0, originalImage.getHeight(), originalImage.getWidth(), -originalImage.getHeight(), null);
            } else {
                g.dispose();
                return ResponseEntity.badRequest().body("Invalid flip direction");
            }

            g.dispose();

            String fileName = generateUniqueFilename("flipped_" + direction + "_" + previousImage);
            ImageIO.write(flippedImage, "png", uploadPath.resolve(fileName).toFile());

            return ResponseEntity.ok("http://localhost:8080/api/image/get/" + fileName);
        } catch (IOException e) {
            logger.error("Flip operation failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Flip failed: " + e.getMessage());
        }
    }

    @PostMapping("/rotate/{degrees}")
    public ResponseEntity<String> rotateImage(@RequestParam("previousImage") String previousImage, @PathVariable int degrees) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadPath);

            BufferedImage originalImage = ImageIO.read(uploadPath.resolve(previousImage).toFile());

            // new dimensions after rotation , had runtime errors before.
            double radians = Math.toRadians(degrees);
            double sin = Math.abs(Math.sin(radians));
            double cos = Math.abs(Math.cos(radians));
            int newWidth = (int) Math.floor(originalImage.getWidth() * cos + originalImage.getHeight() * sin);
            int newHeight = (int) Math.floor(originalImage.getHeight() * cos + originalImage.getWidth() * sin);

            // Rotate
            BufferedImage rotatedImage = Thumbnails.of(originalImage)
                    .size(newWidth, newHeight)
                    .rotate(degrees)
                    .asBufferedImage();

            String fileName = generateUniqueFilename("rotated_" + degrees + "_" + previousImage);
            ImageIO.write(rotatedImage, "png", uploadPath.resolve(fileName).toFile());

            return ResponseEntity.ok("http://localhost:8080/api/image/get/" + fileName);
        } catch (IOException e) {
            logger.error("Rotation failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Rotation failed: " + e.getMessage());
        }
    }

    @PostMapping("/resize")
    public ResponseEntity<String> resizeImage(@RequestParam("previousImage") String previousImage, @RequestParam int width, @RequestParam int height) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadPath);

            BufferedImage originalImage = ImageIO.read(uploadPath.resolve(previousImage).toFile());

            // Resize
            BufferedImage resizedImage = Thumbnails.of(originalImage)
                    .size(width, height)
                    .asBufferedImage();

            String fileName = generateUniqueFilename("resized_" + width + "x" + height + "_" + previousImage);
            ImageIO.write(resizedImage, "png", uploadPath.resolve(fileName).toFile());

            return ResponseEntity.ok("http://localhost:8080/api/image/get/" + fileName);
        } catch (IOException e) {
            logger.error("Resize operation failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Resize failed: " + e.getMessage());
        }
    }

    @PostMapping("/grayscale")
    public ResponseEntity<String> grayscaleImage(@RequestParam("previousImage") String previousImage) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadPath);

            BufferedImage originalImage = ImageIO.read(uploadPath.resolve(previousImage).toFile());

            // grayscale
            BufferedImage grayscaleImage = Thumbnails.of(originalImage)
                    .size(originalImage.getWidth(), originalImage.getHeight())
                    .addFilter(new GrayscaleFilter())
                    .asBufferedImage();

            String fileName = generateUniqueFilename("grayscale_" + previousImage);
            ImageIO.write(grayscaleImage, "png", uploadPath.resolve(fileName).toFile());

            return ResponseEntity.ok("http://localhost:8080/api/image/get/" + fileName);
        } catch (IOException e) {
            logger.error("Grayscale conversion failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Grayscale conversion failed: " + e.getMessage());
        }
    }
}