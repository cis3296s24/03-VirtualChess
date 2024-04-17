module com.cis3296.virtualchess {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.cis3296.virtualchess to javafx.fxml;
    exports com.cis3296.virtualchess;
    exports com.cis3296.virtualchess.Pieces;
    opens com.cis3296.virtualchess.Pieces to javafx.fxml;
    exports com.cis3296.virtualchess.Controller;
    opens com.cis3296.virtualchess.Controller to javafx.fxml;
    exports com.cis3296.virtualchess.Board;
    opens com.cis3296.virtualchess.Board to javafx.fxml;
    exports com.cis3296.virtualchess.Data;
    opens com.cis3296.virtualchess.Data to javafx.fxml;
}