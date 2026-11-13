package com.cis3296.virtualchess;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Smoke tests confirming the build packages the resources the app loads at runtime.
 */
class ResourcesTest {

    @Test
    void mainMenuScreenIsOnClasspath() {
        assertNotNull(getClass().getResource("/com/cis3296/virtualchess/mainmenu.fxml"));
        assertNotNull(getClass().getResource("/com/cis3296/virtualchess/menuStyle.css"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"pawn", "knight", "bishop", "rook", "queen", "king"})
    void bothColorsHaveAnImageForEachPiece(String piece) {
        for (String color : new String[]{"white", "black"}) {
            String path = "/assets/piecesNorm/" + color + "_" + piece + ".png";
            assertNotNull(getClass().getResource(path), "missing " + path);
        }
    }
}
