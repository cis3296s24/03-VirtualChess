package com.cis3296.virtualchess.Systems;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.stage.Stage;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.ScrollEvent;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

public class STLViewer extends Application {

    @Override
    public void start(Stage primaryStage) {
        File stlFile = new File("C:\\Users\\acuff\\Downloads\\output.stl");
        TriangleMesh mesh = new TriangleMesh();

        try (FileInputStream stlStream = new FileInputStream(stlFile)) {
            Scanner scanner = new Scanner(stlStream);

            while (scanner.hasNext()) {
                String token = scanner.next();
                if (token.equals("facet")) {
                    scanner.next(); // Skip "outer"
                    scanner.next(); // Skip "loop"

                    float x1 = scanner.nextFloat();
                    float y1 = scanner.nextFloat();
                    float z1 = scanner.nextFloat();
                    mesh.getPoints().addAll(x1, y1, z1);

                    float x2 = scanner.nextFloat();
                    float y2 = scanner.nextFloat();
                    float z2 = scanner.nextFloat();
                    mesh.getPoints().addAll(x2, y2, z2);

                    float x3 = scanner.nextFloat();
                    float y3 = scanner.nextFloat();
                    float z3 = scanner.nextFloat();
                    mesh.getPoints().addAll(x3, y3, z3);

                    int vertexIndex = mesh.getPoints().size() / 3 - 1;
                    mesh.getFaces().addAll(vertexIndex - 2, 0, vertexIndex - 1, 0, vertexIndex, 0);
                }
            }

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        MeshView meshView = new MeshView(mesh);
        meshView.setMaterial(new PhongMaterial(Color.DARKGREY));

        Group root = new Group(meshView);
        SubScene subScene = new SubScene(root, 800, 600, true, SceneAntialiasing.BALANCED);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-1000);

        subScene.setCamera(camera);
        subScene.setOnScroll(scrollEvent -> {
            double delta = scrollEvent.getDeltaY();
            double z = camera.getTranslateZ();
            double newZ = z + delta;

            double minZ = -2000;
            double maxZ = -200;

            if (newZ > minZ && newZ < maxZ) {
                camera.setTranslateZ(newZ);
            }
        });

        Group group3D = new Group(subScene);
        Scene scene = new Scene(group3D, 800, 600, true);
        scene.setFill(Color.WHITE);

        primaryStage.setScene(scene);
        primaryStage.setTitle("STL Viewer with Zoom");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
