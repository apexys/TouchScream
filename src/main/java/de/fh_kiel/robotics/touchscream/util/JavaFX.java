package de.fh_kiel.robotics.touchscream.util;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class JavaFX {

    public static <T> void setPropertyOnFXThread(final ObjectProperty<T> property, final T value)
    {
        Platform.runLater(() -> {
            property.set(value);
        });
    }

    public static <T> void setPropertyOnFXThread(final StringProperty property, final String value)
    {
        Platform.runLater(() -> {
            property.set(value);
        });
    }

    public static void runOnFXThread(Runnable aRunnable)
    {
        Platform.runLater(aRunnable);
    }

    public static void updateImageView(ImageView view, Image image)
    {
        JavaFX.setPropertyOnFXThread(view.imageProperty(), image);
    }
}
