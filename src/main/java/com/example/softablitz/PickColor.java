package com.example.softablitz;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

import static org.opencv.imgproc.Imgproc.COLOR_BGR2BGRA;
import static org.opencv.imgproc.Imgproc.cvtColor;

public class PickColor {

    double ratio1, ratio2;
    Mat matrix;

    PickColor(ImageView imageView, File file) throws FileNotFoundException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        InputStream stream = new FileInputStream(file.getAbsolutePath());
        Image image = new Image(stream);
        ratio1 = image.getHeight() / imageView.getFitHeight();
        ratio2 = image.getWidth() / imageView.getFitWidth();
        // get original image from file path and convert it to 4 channel mode using COLOR_BGR2BGRA
        // 4 channel means rbga where a is alpha represents transparency of image
        // alpha = 0 means fully transparent
        // alpha = 255 means fully opaque
        // alpha >0 && alpha <255 means fully translusent
        matrix = Imgcodecs.imread(file.getAbsolutePath(), 4);
//        cvtColor(matrix, matrix, COLOR_BGR2BGRA);
    }

    public static void showResult(Mat img, AnchorPane imageViewPane,ImageView imageView) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", img, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
            Image image = SwingFXUtils.toFXImage(bufImage, null);
            imageView.setImage(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageViewPane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                imageView.setLayoutX(mouseEvent.getSceneX());
                imageView.setLayoutY(mouseEvent.getSceneY());
            }
        });
    }


}
