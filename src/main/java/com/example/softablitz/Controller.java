package com.example.softablitz;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;


public class Controller implements Initializable {

    @FXML
    private AnchorPane menuBarPane, imageViewPane;
    @FXML
    private JFXSlider brightness;
    @FXML
    private JFXSlider contrast;
    @FXML
    private JFXSlider hue;
    @FXML
    private JFXSlider saturation;
    @FXML
    private TextArea FIELD;
    @FXML
    private AnchorPane adjustPane, emojiPane;
    @FXML
    private JFXButton adjustButton;
    @FXML
    private JFXButton textButton;
    @FXML
    private AnchorPane specialEffectsPane;
    @FXML
    private ColorPicker brushColorPicker;
    @FXML
    private JFXToggleButton eraserToggleButton;
    @FXML
    private JFXSlider brushSize;
    @FXML
    private AnchorPane brushPane;
    @FXML
    private JFXButton brushButton;
    @FXML
    private AnchorPane TEXTPANE;
    @FXML
    Slider FONTADJUST;
    @FXML
    ColorPicker COLORPICKER;
    @FXML
    private JFXSlider upscaleSlider;
    @FXML
    private JFXSlider compressQuality, angleSlider;
    protected ImageView activeImageView;
    private Stack<ObservableList<Node>> stk;
    protected File file;
    double angleOfImageViewPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        angleOfImageViewPane = imageViewPane.getRotate();
        angleSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {

                imageViewPane.setRotate((angleSlider.getValue() * -1) + angleOfImageViewPane);
            }
        });

    }

    @FXML
    protected void fileButtonClicked() {
        File outputFile = new File("D:/formattedPicture.png");
        BufferedImage bImage = SwingFXUtils.fromFXImage(imageViewPane.snapshot(null, null), null);
        try {
            ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void openFile() throws FileNotFoundException {
        imageViewPane.getChildren().clear();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Select files", "*.jpg", "*.png", "*.jpeg", "*.jfif");
        fileChooser.getExtensionFilters().add(filter);
        File x = fileChooser.showOpenMultipleDialog(null).get(0);
        file = x;
        ImageView imageView = new ImageView();
        Image image = new Image((new FileInputStream(x)));
        imageView.setImage(image);
        centerFrameImage(imageView, imageViewPane);
        handleZoom(imageView);
        imageView.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                imageView.setLayoutX(mouseEvent.getScreenX());
                imageView.setLayoutY(mouseEvent.getSceneY());
            }
        });
        activeImageView = imageView;
    }

    @FXML
    protected void handleZoom(ImageView imageView) {
        imageView.maxWidth(100);
        imageView.maxHeight(100);
        imageView.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                double zoomFactor = 1.05;
                double deltaY = event.getDeltaY();
                if (deltaY < 0) {
                    zoomFactor = 0.95;
                }
                double finalZoomFactor = zoomFactor;
                imageView.setScaleX(imageView.getScaleX() * finalZoomFactor);
                imageView.setScaleY(imageView.getScaleY() * finalZoomFactor);
            }
        });
    }


    @FXML
    private void ADDBUTTONPRESSED() {
        Text text = new Text();
        text.setText(FIELD.getText());
        text.setFill(COLORPICKER.getValue());
        text.setStyle("-fx-font: 50 arial");
        text.setStyle("-fx-background-color: white");
        text.setLayoutX(imageViewPane.getWidth() / 2);
        text.setLayoutY(imageViewPane.getHeight() / 2);
        text.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                text.setLayoutX(mouseEvent.getSceneX());
                text.setLayoutY(mouseEvent.getSceneY());
            }
        });
        imageViewPane.getChildren().add(text);
        COLORPICKER.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                text.setFill(COLORPICKER.getValue());
            }
        });
        FONTADJUST.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                text.setFont(new Font(FONTADJUST.getValue()));
            }
        });
    }

    @FXML
    protected void closeEmojiPanel() {
        emojiPane.setVisible(false);
    }

    @FXML
    protected void addEmojiButtonPressed() {
        emojiPane.setVisible(true);
        emojiPane.toFront();
        for (Node e : emojiPane.getChildren()) {

            if ((e instanceof HBox)) {
                HBox hBox = (HBox) e;
                for (Node x : hBox.getChildren()) {
                    if (x instanceof ImageView) {
                        ImageView imageView = (ImageView) x;
                        JFXButton jfxButton = new JFXButton();
                        imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                AnchorPane anchorPane = new AnchorPane();
                                imageView.setFitWidth(100);
                                imageView.setFitHeight(100);
                                try {
                                    ImageView imageView1 = new ImageView(new Image(new FileInputStream("src/main/resources/com/example/softablitz/icons/close.png")));
                                    ColorAdjust colorAdjust = new ColorAdjust();
                                    colorAdjust.setBrightness(1);
                                    imageView1.setEffect(colorAdjust);
                                    jfxButton.setGraphic(imageView1);
                                } catch (FileNotFoundException ex) {
                                    ex.printStackTrace();
                                }
                                anchorPane.getChildren().add(imageView);
                                imageViewPane.getChildren().add(anchorPane);
                                emojiPane.setVisible(false);
                            }
                        });
                        imageView.setOnMouseDragged(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                imageView.setLayoutX(mouseEvent.getSceneX() - imageViewPane.getLayoutX());
                                imageView.setLayoutY(mouseEvent.getSceneY() - imageViewPane.getLayoutY());
                            }
                        });
                        handleZoom(imageView);
                    }
                }
            }
        }
    }

    public double centerFrameImage(ImageView imageView, AnchorPane anchorPane) {
        Image image = imageView.getImage();
        double ratio = Math.min((double) anchorPane.getHeight() / image.getHeight(), (double) anchorPane.getWidth() / image.getWidth());
        imageView.setFitWidth(image.getWidth() * ratio);
        imageView.setFitHeight(image.getHeight() * ratio);
        double wid = imageView.getFitWidth();
        double hig = imageView.getFitHeight();
        int padding = 0;
        imageView.setLayoutX((anchorPane.getWidth() - wid) / 2 - padding);
        imageView.setLayoutY((anchorPane.getHeight() - hig) / 2 - padding);
        anchorPane.getChildren().add(imageView);
        return ratio;
    }


    @FXML
    protected void resetAdjust() {
        saturation.setValue(50);
        contrast.setValue(50);
        hue.setValue(50);
        brightness.setValue(50);
    }


    @FXML
    protected void closeBrushPanel() {
        brushPane.setVisible(false);
        brushButton.setVisible(true);

        specialEffectMenu.setVisible(true);
        areaSelectionMenu.setVisible(true);
    }

    @FXML
    protected void closeAdjustPanel() {
        adjustPane.setVisible(false);
        adjustButton.setVisible(true);
        textButton.setVisible(true);
        brushButton.setVisible(true);

        specialEffectMenu.setVisible(true);
    }

    @FXML
    protected void closeTextPanel() {
        adjustPane.setVisible(false);
        adjustButton.setVisible(true);
        TEXTPANE.setVisible(false);
        textButton.setVisible(true);
        brushButton.setVisible(true);
        specialEffectMenu.setVisible(true);
        areaSelectionMenu.setVisible(true);
    }

    @FXML
    protected void closeEffectPanel() {
        specialEffectsPane.setVisible(false);
        specialEffectMenu.setVisible(true);
    }

    @FXML
    protected void adjustHandle() {
        brushPane.setVisible(false);
        TEXTPANE.setVisible(false);
        if (activeImageView == null)
            return;
        specialEffectMenu.setVisible(false);
        textButton.setVisible(false);
        areaSelectionMenu.setVisible(true);
        brushButton.setVisible(false);
        adjustButton.setVisible(false);
        adjustPane.setVisible(true);
        BasicAdjust basicAdjust = new BasicAdjust(brightness, contrast, hue, saturation);
        basicAdjust.setSliderListener(activeImageView, imageViewPane);
    }

    @FXML
    private MenuButton specialEffectMenu, areaSelectionMenu;

    @FXML
    private void textButtonPressed() {
        brushPane.setVisible(false);
        specialEffectsPane.setVisible(false);
        if (activeImageView == null)
            return;
        specialEffectMenu.setVisible(false);
        areaSelectionMenu.setVisible(false);
        textButton.setVisible(false);
        brushButton.setVisible(false);
        TEXTPANE.setVisible(true);
    }

    @FXML
    protected void brushButtonPressed() {
        adjustPane.setVisible(false);
        TEXTPANE.setVisible(false);
        textButton.setVisible(true);
        adjustButton.setVisible(true);

        if (activeImageView == null) {
            return;
        }
        specialEffectMenu.setVisible(false);
        areaSelectionMenu.setVisible(false);
        brushPane.setVisible(true);
        brushButton.setVisible(false);
        GraphicsContext g;
        Canvas canvas = null;
        canvas = new Canvas(imageViewPane.getWidth(), imageViewPane.getHeight());
        g = canvas.getGraphicsContext2D();
        g.drawImage(activeImageView.getImage(), activeImageView.getFitWidth(), activeImageView.getFitHeight());
        canvas.setOnMouseDragged(e -> {
            double size = brushSize.getValue();
            double x = e.getX() - size / 2;
            double y = e.getY() - size / 2;
            if (eraserToggleButton.isSelected()) {
                g.clearRect(x, y, size, size);
            } else {
                g.setFill(brushColorPicker.getValue());
                g.fillOval(x, y, size, size);
            }
        });
        imageViewPane.getChildren().add(canvas);
    }


    @FXML
    protected void specialEffectsButtonPressed() {
        adjustPane.setVisible(false);
        adjustButton.setVisible(true);
        TEXTPANE.setVisible(false);
        adjustButton.setVisible(true);
        textButton.setVisible(true);
        brushButton.setVisible(true);
        if (activeImageView == null)
            return;
        specialEffectsPane.setVisible(true);

    }

    @FXML
    private JFXSlider smoothingSlider;

    @FXML
    protected void smoothingButtonPressed() {
        smoothingSlider.setDisable(false);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        smoothingSlider.setOnMouseDragged(e -> {
            Mat image = Imgcodecs.imread(file.getAbsolutePath(), Imgcodecs.IMREAD_COLOR);
            Mat dst = image.clone();
            Imgproc.bilateralFilter(image, dst, (int) smoothingSlider.getValue(), smoothingSlider.getValue() * 2, smoothingSlider.getValue() / 2);
            RedEyeCorrection.showResult(dst, activeImageView);
        });
    }


    @FXML
    protected void UPSCALEIMAGE() throws IOException {
        UpScaleImage scale = new UpScaleImage();
        scale.resize(file.getAbsolutePath(), "D:\\upscaleOutput.jpg", upscaleSlider.getValue());
    }

    @FXML
    protected void COMPRESS() throws IOException {
        Compress compress = new Compress();
        Image image = activeImageView.getImage();
        System.out.println(compressQuality.getValue() / 100);
        compress.compressImage(image, compressQuality.getValue() / 100);
    }

    @FXML
    protected void rotate90() {
        imageViewPane.setRotate((90 + imageViewPane.getRotate()));
        angleOfImageViewPane = imageViewPane.getRotate();
    }

    @FXML
    protected void rotate180() {

        imageViewPane.setRotate((180 + imageViewPane.getRotate()));
        angleOfImageViewPane = imageViewPane.getRotate();
    }

    @FXML
    protected void flipVertical() {
        Translate flipTranslation = new Translate(0, imageViewPane.getPrefHeight());
        Rotate flipRotation = new Rotate(180, Rotate.X_AXIS);
        imageViewPane.getTransforms().addAll(flipTranslation, flipRotation);
    }

    @FXML
    protected void flipHorizontal() {
        Translate flipTranslation = new Translate(imageViewPane.getPrefWidth(), 0);
        Rotate flipRotation = new Rotate(180, Rotate.Y_AXIS);
        imageViewPane.getTransforms().addAll(flipTranslation, flipRotation);
    }

    @FXML
    protected void redEyeCorrection() throws InterruptedException {
        Imgcodecs imgCodecs = new Imgcodecs();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        RedEyeCorrection redEyeCorrection = new RedEyeCorrection();
        redEyeCorrection.correctRedEye(file, imgCodecs, activeImageView);
    }

    @FXML
    protected void zoomIn() {
        imageViewPane.maxWidth(100);
        imageViewPane.maxHeight(100);
        double finalZoomFactor = 1.05;
        imageViewPane.setScaleX(imageViewPane.getScaleX() * finalZoomFactor);
        imageViewPane.setScaleY(imageViewPane.getScaleY() * finalZoomFactor);
    }

    @FXML
    protected void zoomOut() {
        imageViewPane.maxWidth(100);
        imageViewPane.maxHeight(100);
        double finalZoomFactor = 0.95;
        imageViewPane.setScaleX(imageViewPane.getScaleX() * finalZoomFactor);
        imageViewPane.setScaleY(imageViewPane.getScaleY() * finalZoomFactor);
    }


    @FXML
    protected void rectangleSelection() throws FileNotFoundException {
        SelectArea selectArea = new SelectArea(file, activeImageView);
        selectArea.makeRectangle(imageViewPane, activeImageView, file);
    }

    @FXML
    protected void ellipseSelection() throws FileNotFoundException {
        SelectArea selectArea = new SelectArea(file, activeImageView);
        selectArea.makeEllipse(imageViewPane, activeImageView);
    }

    @FXML
    protected void lassoSelection() throws FileNotFoundException {
        SelectArea selectArea = new SelectArea(file, activeImageView);
        selectArea.makeLasso(imageViewPane, activeImageView, file);
    }

    @FXML
    protected void cropButtonPressed() throws FileNotFoundException {
        SelectArea selectArea = new SelectArea(file, activeImageView);
        selectArea.crop(file, activeImageView, imageViewPane);
    }

    @FXML
    private void chooseBackground() throws FileNotFoundException {
        SelectArea.chooseBackground(imageViewPane);
        activeImageView.toFront();
        activeImageView.setOnMouseDragged(e -> {
            activeImageView.setLayoutX(e.getSceneX() - imageViewPane.getLayoutX());
            activeImageView.setLayoutY(e.getSceneY() - imageViewPane.getLayoutY());
        });
    }

    @FXML
    protected void dropshadow() {
        activeImageView.setEffect(new DropShadow(100, Color.TURQUOISE));
    }

    @FXML
    protected void sepiatone() {
        activeImageView.setEffect(new SepiaTone());
    }

    @FXML
    protected void boxBlur() {
        activeImageView.setEffect(new BoxBlur(4, 4, 4));

    }

    @FXML
    protected void bloom() {
        activeImageView.setEffect(new Bloom());

    }

    @FXML
    protected void glow() {
        activeImageView.setEffect(new Glow(60));

    }

    @FXML
    protected void innershadow() {
        activeImageView.setEffect(new InnerShadow(200, Color.BLACK));

    }

    @FXML
    protected void reflection() {
        activeImageView.setEffect(new Reflection());

    }

    @FXML
    protected void lightdistant() {
        Light.Distant light = new Light.Distant();
        light.setAzimuth(10);
        light.setColor(Color.YELLOW);
        Lighting lighting = new Lighting();
        lighting.setLight(light);
        activeImageView.setEffect(lighting);
    }

    @FXML
    void SETFRAME1(ActionEvent event) throws FileNotFoundException {
        ImageFrame.SETFRAME1(imageViewPane);
        stk.push(imageViewPane.getChildren());
    }

    @FXML
    void SETFRAME2(ActionEvent event) throws FileNotFoundException {
        ImageFrame.SETFRAME2(imageViewPane);
        stk.push(imageViewPane.getChildren());
    }


    @FXML
    void SETFRAME3(ActionEvent event) throws FileNotFoundException {
        ImageFrame.SETFRAME3(imageViewPane);
    }


    @FXML
    void SETFRAME4(ActionEvent event) throws FileNotFoundException {
        ImageFrame.SETFRAME4(imageViewPane);
    }


    @FXML
    void SETFRAME5(ActionEvent event) throws FileNotFoundException {
        ImageFrame.SETFRAME5(imageViewPane);
        stk.push(imageViewPane.getChildren());
    }

    @FXML
    void SETFRAME6(ActionEvent event) throws FileNotFoundException {

        ImageFrame.SETFRAME6(imageViewPane);
        stk.push(imageViewPane.getChildren());
    }

    @FXML
    protected void HORIZONTOLDOUBLE() throws FileNotFoundException {
        CollageImages.HORIZONTOLDOUBLE(imageViewPane);
    }

    @FXML
    protected void VERTICALDOUBLE() throws FileNotFoundException {
        CollageImages.VERTICALDOUBLE(imageViewPane);
    }

    @FXML
    protected void HORIZONTOLTRIPLE() throws FileNotFoundException {
        CollageImages.HORIZONTOLTRIPLE(imageViewPane);
    }

    @FXML
    protected void VERTICALTRIPLE() throws FileNotFoundException {
        CollageImages.VERTICALTRIPLE(imageViewPane);
    }

    @FXML
    protected void QUAD() throws FileNotFoundException {
        CollageImages.QUAD(imageViewPane);
    }

    @FXML
    protected void SETRAIN2() throws FileNotFoundException {
        Filters filters = new Filters();
        filters.setRain2Filter(imageViewPane);
    }

    @FXML
    protected void SETRAIN3() throws FileNotFoundException {
        Filters filters = new Filters();
        filters.setRain3Filter(imageViewPane);
    }

    @FXML
    protected void SETSUNSHINE1() throws FileNotFoundException {
        Filters filters = new Filters();
        filters.setSunshine1Filter(imageViewPane);
    }

    @FXML
    protected void SETSUNSHINE2() throws FileNotFoundException {
        Filters filters = new Filters();
        filters.setSunshine2Filter(imageViewPane);
    }

    @FXML
    protected void SETSMOOTH() throws FileNotFoundException {
        Filters filters = new Filters();
        filters.setSmoothFilter(imageViewPane);
    }

    @FXML
    protected void verticalSideBlur() throws FileNotFoundException {
        SideBlur.verticalSideBlur(imageViewPane, activeImageView);
    }

    @FXML
    protected void horizontolSideBlur() throws FileNotFoundException {
        SideBlur.horizonrolSideBlur(imageViewPane, activeImageView);
    }

    public void undoButtonPressed(ActionEvent actionEvent) {
    }

    public void redoButtonPressed(ActionEvent actionEvent) {
    }

    public void pickColor() throws FileNotFoundException {

        PickColor pickColor = new PickColor(activeImageView, file);
        double ratio1 = pickColor.ratio1;
        double ratio2 = pickColor.ratio2;
        double ratio = Math.max(ratio2, ratio1);
        Mat mat = pickColor.matrix;
        final Mat[] tmpimage = {new Mat()};
        ImageView imageView = new ImageView();
        imageViewPane.getChildren().add(imageView);
        System.out.println(activeImageView.getFitHeight()+" "+activeImageView.getFitWidth());
        System.out.println(activeImageView.getImage().getHeight()+" "+activeImageView.getImage().getWidth());
        activeImageView.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                int x = (int) ((int) ratio * (mouseEvent.getSceneX() - activeImageView.getLayoutX()));
                int y = (int) ((int) ratio * (mouseEvent.getSceneY() - activeImageView.getLayoutY()));
                double arr[] = mat.get(x, y);
////                for (int i = 0; i < arr.length; i++)
////                    System.out.print(arr[i] + " ");
//                    Scalar scalar = new Scalar((int)arr[2],(int) arr[1],(int) arr[0]);
//                System.out.println(scalar);
                System.out.println(arr[2] + " " + arr[1] + " " + arr[0]);
                tmpimage[0] = new Mat(200, 200, mat.type(), new Scalar(arr[0], arr[1], arr[2]));
                PickColor.showResult(tmpimage[0], imageViewPane, imageView);
//                System.out.println();
            }
        });
        activeImageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                RedEyeCorrection.showResult(tmpimage[0], activeImageView);
            }
        });
    }
}

