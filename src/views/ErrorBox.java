package views;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.*;

import javafx.scene.control.*;

public class ErrorBox {
    public static void display(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.show();
    }
}
