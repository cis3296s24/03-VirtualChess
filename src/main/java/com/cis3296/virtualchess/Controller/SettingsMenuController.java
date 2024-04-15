package com.cis3296.virtualchess.Controller;

import com.almasb.fxgl.profile.DataFile;
import com.cis3296.virtualchess.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class SettingsMenuController {


    public ComboBox ThemeDropDown;
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void backToMainMenu(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/mainmenu.fxml")));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }


    private void setTheme(){
        Properties props = new Properties();

        props.setProperty("theme", ThemeDropDown.getValue().toString());

        try {
            File configFile = new File("config.xml");
            FileOutputStream out = new FileOutputStream(configFile);
            props.storeToXML(out,"Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
