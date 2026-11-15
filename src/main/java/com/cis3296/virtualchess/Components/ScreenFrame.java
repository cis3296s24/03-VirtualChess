package com.cis3296.virtualchess.Components;

import javafx.scene.Node;
import javafx.scene.layout.Region;

/**
 * Holds one screen and fits it to the window. Every screen sits in one of these, and the frame is what fills
 * the window (it carries the menu background, so there's never a gap around a screen).
 * <p>
 * Screens are fitted in one of two ways:
 * <ul>
 *     <li><b>Fill</b>: the screen is resized to the whole window and lays itself out (the game screen, whose
 *     board grows and shrinks with the window).</li>
 *     <li><b>Scale</b>: the screen keeps the size it was designed at and is scaled up or down as a whole to fit,
 *     centered (the menus, which are laid out for {@value #DESIGN_WIDTH} x {@value #DESIGN_HEIGHT}). Every menu
 *     uses the same reference size, so text stays the same size moving between them.</li>
 * </ul>
 */
public final class ScreenFrame extends Region {

    /** The size the menu screens were designed at. */
    public static final double DESIGN_WIDTH = 810;
    public static final double DESIGN_HEIGHT = 800;
    // Beyond this, scaled-up menus look oversized and their images get blurry
    private static final double MAX_SCALE = 1.5;

    private final Node content;
    private final boolean fill;

    /**
     * @param content the screen's root
     * @param fill    true to resize the screen to the window; false to scale it to fit
     */
    public ScreenFrame(Node content, boolean fill) {
        this.content = content;
        this.fill = fill;
        getStyleClass().add("screen-frame");
        getChildren().add(content);
    }

    /**
     * @return the screen this frame holds
     */
    public Node content() {
        return content;
    }

    @Override
    protected void layoutChildren() {
        double width = getWidth();
        double height = getHeight();
        if (fill) {
            content.resizeRelocate(0, 0, width, height);
            return;
        }
        double contentWidth = content.prefWidth(-1);
        double contentHeight = content.prefHeight(-1);
        double scale = Math.min(MAX_SCALE, Math.min(
                width / Math.max(DESIGN_WIDTH, contentWidth),
                height / Math.max(DESIGN_HEIGHT, contentHeight)));
        content.resize(contentWidth, contentHeight);
        // Scaling pivots on the screen's center, so centering the unscaled screen centers the scaled one too
        content.relocate(Math.round((width - contentWidth) / 2), Math.round((height - contentHeight) / 2));
        content.setScaleX(scale);
        content.setScaleY(scale);
    }

    @Override
    protected double computePrefWidth(double height) {
        return fill ? content.prefWidth(height) : Math.max(DESIGN_WIDTH, content.prefWidth(-1));
    }

    @Override
    protected double computePrefHeight(double width) {
        return fill ? content.prefHeight(width) : Math.max(DESIGN_HEIGHT, content.prefHeight(-1));
    }

    @Override
    protected double computeMinWidth(double height) {
        return 0;
    }

    @Override
    protected double computeMinHeight(double width) {
        return 0;
    }
}
