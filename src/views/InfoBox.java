package views;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.*;

import javafx.scene.control.*;

public class InfoBox {
    public static void display(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.setTitle(title);
        alert.show();
    }
}
