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
    exports com.cis3296.virtualchess.Entities.Pieces;
    opens com.cis3296.virtualchess.Entities.Pieces to javafx.fxml;
    exports com.cis3296.virtualchess.Controller;
    opens com.cis3296.virtualchess.Controller to javafx.fxml;
    exports com.cis3296.virtualchess.Board;
    opens com.cis3296.virtualchess.Board to javafx.fxml;
    exports com.cis3296.virtualchess.Entities;
    opens com.cis3296.virtualchess.Entities to javafx.fxml;
    exports com.cis3296.virtualchess.Systems;
    opens com.cis3296.virtualchess.Systems to javafx.fxml;
    exports com.cis3296.virtualchess.Components;
    opens com.cis3296.virtualchess.Components to javafx.fxml;
}