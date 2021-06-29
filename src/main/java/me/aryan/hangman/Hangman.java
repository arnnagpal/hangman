package me.aryan.hangman;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This is a basic hangman game that has been rushed in under 30 minutes.
 * This will be cleaned up and made "better" later on.
 *
 * @author Aryan Nagpal
 */
public class Hangman {
    public List<String> words = new ArrayList<>();

    private final URL DICTIONARY_URL;
    public boolean gameRunning = true;
    private boolean won = false;

    private String chosenWord = null;
    private int lives = 5;

    private List<String> correctlyGuessed = new ArrayList<>();

    Hangman(boolean start) throws Exception {
        DICTIONARY_URL = new URL("https://raw.githubusercontent.com/dwyl/english-words/master/words_alpha.txt");

        if (start)
            start();
    }

    public static void main(String[] args) {
        Console console = System.console();
        if (console == null && !GraphicsEnvironment.isHeadless()) {
            String filename = Hangman.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6);
            try {
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "cmd", "/k", "java -jar \"" + filename + "\""});
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                new Hangman(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void start() {
        loadWords();
        chooseWord();

        Scanner scanner = new Scanner(System.in);
        while (gameRunning) {
            if (lives <= 0) {
                System.out.println("You ran out of lives and lost! :(");
                System.out.println("The word was " + chosenWord);

                if (playAgain(scanner)) {
                    break;
                } else {
                    continue;
                }
            } else if (won) {
                if (playAgain(scanner)) {
                    break;
                } else {
                    continue;
                }
            }

            for (int i = 0; i < chosenWord.length(); i++) {
                if (correctlyGuessed.contains(chosenWord.split("")[i])) {
                    System.out.printf("%s ", chosenWord.split("")[i]);
                } else {
                    System.out.print("_ ");
                }
            }

            System.out.printf("\nGuess a letter or a word! Lives: %s \n", lives);
            handleCommand(scanner.next());
        }


    }

    private void stop() {
        gameRunning = false;
    }

    private void chooseWord() {
        System.out.printf("Successfully loaded %s words\n", words.size());

        ThreadLocalRandom random = ThreadLocalRandom.current();
        chosenWord = words.get(random.nextInt(words.size()));
        words.remove(chosenWord);

        correctlyGuessed.add(chosenWord.split("")[0]);
    }

    public void handleCommand(String cmd) {
        if (cmd.equalsIgnoreCase("stop-game")) {
            stop();
        } else {
            guess(cmd);
        }
    }

    private void guess(String guess) {
        if (guess.length() > 1) {
            //is word
            System.out.println("Checking as word.");
            if (guess.equalsIgnoreCase(chosenWord)) {
                correctlyGuessed.addAll(Arrays.asList(chosenWord.split("")));
            }
        } else {
            //is character
            System.out.println("Checking as letter.");

            if (correctlyGuessed.contains(guess)) {
                System.out.println("You've already guessed that letter successfully, try again.");
                return;
            }

            int i = correctlyGuessed.size();
            for (String c : guess.split("")) {
                for (String cc : chosenWord.split("")) {
                    if (c.equalsIgnoreCase(cc)) {
                        correctlyGuessed.add(cc);
                    }
                }
            }

            if (i == correctlyGuessed.size()) {
                lives--;
            }
        }

        if (chosenWord.length() == correctlyGuessed.size()) {
            System.out.println("Congrats you beat the game! Word was " + chosenWord);
            won = true;
        }
    }

    private boolean playAgain(Scanner scanner) {
        System.out.println("Try again? y/N");
        String answer = scanner.next();
        if (answer.equalsIgnoreCase("y")) {
            lives = 5;
            won = false;
            correctlyGuessed.clear();

            chooseWord();
            return false;
        } else if (answer.equalsIgnoreCase("n")) {
            gameRunning = false;
            System.out.println("bye");
            return true;
        } else {
            System.out.println("Unknown answer, try again.");
            return false;
        }

    }

    public void loadWords() {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) DICTIONARY_URL.openConnection();
            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
