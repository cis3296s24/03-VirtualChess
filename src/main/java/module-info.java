module com.cis3296.virtualchess {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens com.cis3296.virtualchess to javafx.fxml;
    exports com.cis3296.virtualchess;
}