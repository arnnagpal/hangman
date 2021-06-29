package me.aryan.hangman;

import org.junit.Assert;
import org.junit.Test;

public class HangmanTest {
    private Hangman hangman;

    public HangmanTest() {
        try {
            hangman = new Hangman(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadWordsTest() {
        hangman.loadWords();
        Assert.assertTrue(hangman.words.size() > 0);
    }

    @Test
    public void handleCommandsTest() {
        hangman.handleCommand("stop-game");
        Assert.assertFalse(hangman.gameRunning);
    }

    //TODO add more unit tests

}
