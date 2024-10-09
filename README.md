# Image Processor Backend

- **Warning**: This Backend API is designed specifically to work in combination with Frontend : https://github.com/VishaL6i9/Image-Processing-Web-Appliction.git 
## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Installation](#installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)
- [Acknowledgments](#acknowledgments)

## Overview

The Image Processor Backend is a Spring Boot application designed to handle image uploads, processing, and retrieval. It provides various image processing functionalities such as inverting colors, flipping, rotating, resizing, and converting to grayscale. The backend interacts with the frontend to provide a seamless user experience.

## Features

- **Upload Image**: Upload images to the server.
- **Invert Colors**: Invert the colors of the uploaded image.
- **Flip Image**: Flip the image horizontally or vertically.
- **Rotate Image**: Rotate the image by a specified number of degrees.
- **Resize Image**: Resize the image to specified dimensions.
- **Grayscale Conversion**: Convert the image to grayscale.
- **Retrieve Image**: Retrieve the processed image from the server.
- **Error Handling**: Log and return error messages for failed operations.

## Technologies Used

- **Spring Boot**: A Java-based framework for building web applications.
- **Thumbnailator**: A Java library for image processing.
- **SLF4J**: A logging facade for various logging frameworks.
- **MultipartFile**: For handling file uploads in Spring Boot.
- **ImageIO**: For reading and writing images in Java.

## Installation

To get started with the Image Processor Backend, follow these steps:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/VishaL6i9/Image-Processing-Web-Application.git
   cd Image-Processing-Web-Application/backend
   ```

2. **Build the project**:
   ```bash
   ./mvnw clean install
   ```

3. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

## Usage

### API Endpoints

1. **Upload Image**:
   - **Endpoint**: `/api/image/upload`
   - **Method**: `POST`
   - **Parameters**:
     - `file`: The image file to upload.
   - **Response**: Returns the URL to retrieve the uploaded image.

2. **Retrieve Image**:
   - **Endpoint**: `/api/image/get/{fileName}`
   - **Method**: `GET`
   - **Parameters**:
     - `fileName`: The name of the file to retrieve.
   - **Response**: Returns the image file.

3. **Invert Image**:
   - **Endpoint**: `/api/image/invert`
   - **Method**: `POST`
   - **Parameters**:
     - `previousImage`: The name of the previous image file.
   - **Response**: Returns the URL to retrieve the inverted image.

4. **Flip Image**:
   - **Endpoint**: `/api/image/flip`
   - **Method**: `POST`
   - **Parameters**:
     - `previousImage`: The name of the previous image file.
     - `direction`: The direction to flip the image (`horizontal` or `vertical`).
   - **Response**: Returns the URL to retrieve the flipped image.

5. **Rotate Image**:
   - **Endpoint**: `/api/image/rotate/{degrees}`
   - **Method**: `POST`
   - **Parameters**:
     - `previousImage`: The name of the previous image file.
     - `degrees`: The number of degrees to rotate the image.
   - **Response**: Returns the URL to retrieve the rotated image.

6. **Resize Image**:
   - **Endpoint**: `/api/image/resize`
   - **Method**: `POST`
   - **Parameters**:
     - `previousImage`: The name of the previous image file.
     - `width`: The new width of the image.
     - `height`: The new height of the image.
   - **Response**: Returns the URL to retrieve the resized image.

7. **Grayscale Image**:
   - **Endpoint**: `/api/image/grayscale`
   - **Method**: `POST`
   - **Parameters**:
     - `previousImage`: The name of the previous image file.
   - **Response**: Returns the URL to retrieve the grayscale image.

## Contributing

We welcome contributions to the Image Processor Backend! If you'd like to contribute, please follow these steps:

1. **Fork the repository**.
2. **Create a new branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Make your changes** and commit them:
   ```bash
   git commit -m "Add some feature"
   ```
4. **Push to the branch**:
   ```bash
   git push origin feature/your-feature-name
   ```
5. **Create a pull request** on GitHub.

Please ensure your code follows the existing coding standards and includes appropriate tests.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.

## Contact

- **Name**: Vishal Kandakatla
- **Email**: vishalkandakatla@gmail.com

## Acknowledgments

- Thanks to the open-source community for providing the tools and libraries that made this project possible.

