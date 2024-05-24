import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

class GameHelper {
    private static final String ALPHABET = "abcdefg";
    private static final int GRID_LENGTH = 7;
    private static final int GRID_SIZE = 49;
    private static final int MAX_ATTEMPTS = 200;

    static final int HORIZONTAL_INCREMENT = 1;
    static final int VERTICAL_INCREMENT = GRID_LENGTH;

    private final int[] grid = new int[GRID_SIZE];
    private final Random random = new Random();

    private int startupCount = 0;
    private ArrayList<Integer> userSelectedPositions = new ArrayList<>();

    public String getUserInput(String prompt) {
        System.out.print(prompt + ": ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().toLowerCase();
    }

    public String getAlphabet() {
        return ALPHABET;
    }

    public int getGridLength() {
        return GRID_LENGTH;
    }

    public void displayGrid() {
        System.out.println("\n   a  b  c  d  e  f  g");
        for (int i = 0; i < GRID_SIZE; i++) {
            if (i % GRID_LENGTH == 0) {
                System.out.print("\n" + (i / GRID_LENGTH) + "  ");
            }
            if (userSelectedPositions.contains(i)) {
                System.out.print("X  ");
            } else {
                System.out.print(".  ");
            }
        }
        System.out.println();
    }

    public ArrayList<String> placeStartup(int startupSize) {
        int[] startupCoords = new int[startupSize];
        int attempts = 0;
        boolean success = false;

        startupCount++;
        int increment = getIncrement();

        while (!success && attempts++ < MAX_ATTEMPTS) {
            int location = random.nextInt(GRID_SIZE);

            for (int i = 0; i < startupCoords.length; i++) {
                startupCoords[i] = location;
                location += increment;
            }

            if (startupFits(startupCoords, increment)) {
                success = coordsAvailable(startupCoords);
            }
        }
        return savePositionToGrid(startupCoords, increment);
    }

    private ArrayList<String> savePositionToGrid(int[] startupCoords, int increment) {
        ArrayList<String> alphaCells = convertCoordsToAlphaFormat(startupCoords);
        for (int index : startupCoords) {
            grid[index] = 1;
        }
        return alphaCells;
    }

    private boolean startupFits(int[] startupCoords, int increment) {
        int finalLocation = startupCoords[startupCoords.length - 1];

        if (increment == HORIZONTAL_INCREMENT) {
            return calcRowFromIndex(startupCoords[0]) == calcRowFromIndex(finalLocation);
        } else {
            return finalLocation < GRID_SIZE;
        }

    }

    private boolean coordsAvailable(int[] startupCoords) {
        for (int coord : startupCoords) {
            if (grid[coord] != 0) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<String> convertCoordsToAlphaFormat(int[] startupCoords) {
        ArrayList<String> alphaCells = new ArrayList<>();
        for (int index : startupCoords) {
            String alphaCoords = getAlphaCoordsFromIndex(index);
            alphaCells.add(alphaCoords);
        }
        return alphaCells;
    }

    private String getAlphaCoordsFromIndex(int index) {
        int row = calcRowFromIndex(index);
        int column = index % GRID_LENGTH;

        String letter = ALPHABET.substring(column, column + 1);
        return letter + row;
    }

    private int calcRowFromIndex(int index) {
        return index / GRID_LENGTH;
    }

    private int getIncrement() {
        if (startupCount % 2 == 0) {
            return HORIZONTAL_INCREMENT;
        } else {
            return VERTICAL_INCREMENT;
        }
    }

    public void addUserSelectedPosition(int index) {
        userSelectedPositions.add(index);
    }
}

class Startup {
    private ArrayList<String> locationCells;
    private String name;

    public void setLocationCells(ArrayList<String> loc) {
        locationCells = loc;
    }

    public void setName(String n) {
        name = n;
    }

    public String checkYourself(String userInput) {
        String result = "miss";
        int index = locationCells.indexOf(userInput);
        if (index >= 0) {
            locationCells.remove(index);
            if (locationCells.isEmpty()) {
                result = "kill";
                System.out.println("Ouch! You sunk " + name + " :(");
            } else {
                result = "hit";
            }
        }
        return result;
    }
}

public class StartupBust {
    private GameHelper helper = new GameHelper();
    private ArrayList<Startup> startups = new ArrayList<>();
    private int numOfGuesses = 0;

    private void setUpGame() {
        Startup one = new Startup();
        one.setName("poniez");
        Startup two = new Startup();
        two.setName("hacqi");
        Startup three = new Startup();
        three.setName("cabista");
        startups.add(one);
        startups.add(two);
        startups.add(three);

        System.out.println("\nWelcome to StartupBust.");
        System.out.println("Your goal is to sink three Startups randomly placed on the grid.");
        System.out.println("The Startups take 3 grid positions and are place horizontally of vertically.");
        System.out.println("Try to sink them all in the fewest number of guesses.");

        for (Startup startup : startups) {
            ArrayList<String> newLocation = helper.placeStartup(3);
            startup.setLocationCells(newLocation);
        }
    }

    private void startPlaying() {
        while (!startups.isEmpty()) {
            helper.displayGrid();
            String userGuess = helper.getUserInput("\nEnter a guess (letternumber)");
            checkUserGuess(userGuess);
        }
        finishGame();
    }

    private void checkUserGuess(String userGuess) {
        numOfGuesses++;
        String result = "miss";

        for (Startup startupToTest : startups) {
            result = startupToTest.checkYourself(userGuess);

            if (result.equals("hit")) {
                helper.addUserSelectedPosition(getIndexFromUserInput(userGuess));
                break;
            }
            if (result.equals("kill")) {
                helper.addUserSelectedPosition(getIndexFromUserInput(userGuess));
                startups.remove(startupToTest);
                break;
            }
        }

        System.out.println(result);
    }

    private int getIndexFromUserInput(String userInput) {
        char columnChar = userInput.charAt(0);
        int row = Integer.parseInt(userInput.substring(1));
        int column = helper.getAlphabet().indexOf(columnChar);
        return row * helper.getGridLength() + column;
    }

    private void finishGame() {
        System.out.println("All Startups are dead!");
        if (numOfGuesses <= 18) {
            System.out.println("It only took you " + numOfGuesses + " guesses.");
        } else {
            System.out.println("Took you long enough. " + numOfGuesses + " guesses.");
        }
    }

    public static void main(String[] args) {
        StartupBust game = new StartupBust();
        game.setUpGame();
        game.startPlaying();
    }
}