package de.intektor.pixelshooter.gui;

/**
 * @author Intektor
 */
public interface DPadHandler {

    /**
     * When the thumbGameController is activated
     */
    void activateGameController(ThumbGameController pad);

    /**
     * When the thumbGameController get deactivated
     */
    void deactivateGameController(ThumbGameController pad);

    /**
     * When the finger is moved and the thumb of the thumbGameController should translate
     */
    void moveGameController(ThumbGameController pad);
}
