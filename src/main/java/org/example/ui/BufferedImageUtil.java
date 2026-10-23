package org.example.ui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * BufferedImageUtil
 *
 * @author Marcos Quispe
 * @since 1.0
 */
public class BufferedImageUtil {

    public static BufferedImage readImage(String fileName, Class<?> class1) {
        // si no reconoce la imagen, ejecutar mvn clean compile para que sea agregado a la carpeta target
        try (InputStream inputStream = class1.getClassLoader().getResourceAsStream(fileName)) { // no funciona bien con webp
            if (inputStream == null) {
                System.err.println("No se encontro la imagen: " + fileName);
                return null;
            }
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
