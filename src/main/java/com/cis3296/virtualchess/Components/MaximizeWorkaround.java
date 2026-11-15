package com.cis3296.virtualchess.Components;

import javafx.animation.PauseTransition;
import javafx.beans.InvalidationListener;
import javafx.geometry.Rectangle2D;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Works around a JavaFX problem seen on Linux under WSLg (and present in JavaFX 21 through 25): pressing the
 * window's maximize button grows the window to fill the screen, then it immediately snaps back to its old size
 * while still reporting itself as maximized. Restoring it has the opposite problem: the window comes back 64
 * pixels smaller than it was (the size of its shadow), so it shrinks a little every time. Any JavaFX window
 * does this, not just this app's.
 * <p>
 * The window manager does tell JavaFX the maximized size before the snap-back, so this remembers that size and,
 * if the window snaps back, sizes the window to it directly (which sticks). On restore it puts the window back
 * to the size it had before maximizing. Where maximizing works properly, neither problem happens and this does
 * nothing.
 */
public final class MaximizeWorkaround {

    private static final Logger log = LoggerFactory.getLogger(MaximizeWorkaround.class);

    // How long to let the window manager and JavaFX settle before checking the window's size
    private static final Duration SETTLE_TIME = Duration.millis(350);
    // JavaFX can snap back again after a fix, so check (and fix) a few times
    private static final int MAX_ATTEMPTS = 3;

    private final Stage stage;
    private Rectangle2D normalBounds;   // the window's latest bounds while not maximized
    private Rectangle2D restoreBounds;  // where the window was when it was maximized, to go back to
    private Rectangle2D offeredBounds;  // the maximized size the window manager gave, before any snap-back
    // Restoring briefly reports "maximized" again; while this is true, that doesn't count as a new maximize
    private boolean restoring;
    private int attempts;

    private MaximizeWorkaround(Stage stage) {
        this.stage = stage;
    }

    /**
     * Watches the window for the snap-back. Call once, after the window has its initial size.
     */
    public static void install(Stage stage) {
        MaximizeWorkaround workaround = new MaximizeWorkaround(stage);
        workaround.normalBounds = bounds(stage);
        InvalidationListener boundsWatcher = observable -> workaround.boundsChanged();
        stage.xProperty().addListener(boundsWatcher);
        stage.yProperty().addListener(boundsWatcher);
        stage.widthProperty().addListener(boundsWatcher);
        stage.heightProperty().addListener(boundsWatcher);
        stage.maximizedProperty().addListener((observable, was, maximized) -> workaround.maximizedChanged(maximized));
    }

    private void boundsChanged() {
        if (stage.isFullScreen()) {
            return;
        }
        Rectangle2D now = bounds(stage);
        if (!stage.isMaximized()) {
            normalBounds = now;
        } else if (!restoring && restoreBounds != null && differsInBothDimensions(now, restoreBounds)) {
            // While maximizing, a size unlike the old one came from the window manager. Width and height change
            // in separate steps, so a size that's only half different is in between. The last such size is the
            // real one (the first may include the window's shadow).
            offeredBounds = now;
        }
    }

    private void maximizedChanged(boolean maximized) {
        if (restoring) {
            return; // the brief flicker while restoring
        }
        attempts = 0;
        if (maximized) {
            restoreBounds = normalBounds;
            offeredBounds = null;
            fixMaximizedSize();
        } else if (restoreBounds != null) {
            restoring = true;
            fixRestoredSize();
        }
    }

    private void fixMaximizedSize() {
        afterSettling(() -> {
            if (!stage.isMaximized() || stage.isFullScreen() || offeredBounds == null) {
                return;
            }
            if (!sameSize(bounds(stage), offeredBounds) && attempts < MAX_ATTEMPTS) {
                attempts++;
                log.debug("Maximized window snapped back to {}; sizing it to {}", bounds(stage), offeredBounds);
                setBounds(offeredBounds);
                fixMaximizedSize();
            }
        });
    }

    private void fixRestoredSize() {
        afterSettling(() -> {
            if (stage.isMaximized() || stage.isFullScreen()) {
                restoring = false;
                return;
            }
            if (!sameSize(bounds(stage), restoreBounds) && attempts < MAX_ATTEMPTS) {
                attempts++;
                log.debug("Restored window came back as {}; sizing it to {}", bounds(stage), restoreBounds);
                setBounds(restoreBounds);
                fixRestoredSize();
            } else {
                restoring = false;
            }
        });
    }

    private static void afterSettling(Runnable check) {
        PauseTransition settle = new PauseTransition(SETTLE_TIME);
        settle.setOnFinished(event -> check.run());
        settle.play();
    }

    private void setBounds(Rectangle2D bounds) {
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
    }

    private static Rectangle2D bounds(Stage stage) {
        return new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
    }

    private static boolean differsInBothDimensions(Rectangle2D a, Rectangle2D b) {
        return Math.abs(a.getWidth() - b.getWidth()) >= 1 && Math.abs(a.getHeight() - b.getHeight()) >= 1;
    }

    private static boolean sameSize(Rectangle2D a, Rectangle2D b) {
        return Math.abs(a.getWidth() - b.getWidth()) < 1 && Math.abs(a.getHeight() - b.getHeight()) < 1;
    }
}
