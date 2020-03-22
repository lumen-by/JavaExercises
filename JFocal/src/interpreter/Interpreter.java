package interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Interpreter {

    private final static String WELCOME = "JFocal, version 0.05";
    private final static String PROMT = "* ";

    private final static String Q = "Q";
    private final static String QUIT = "QUIT";

    private final static String E = "E";
    private final static String ERASE = "ERASE";
    private final static String O = "O";
    private final static String OPEN = "OPEN";
    private final static String W = "W";
    private final static String WRITE = "WRITE";

    private final static String COMMAND_NOT_RECOGNIZED = "Error: Command '%s' not recognized\n";
    private final static String NOT_ENOUGH_PARAMETERS = "Error: Not enough parameters command '%s'\n";
    private final static String OPERATION_NOT_RECOGNIZED = "Error: Operation '%s' not recognized\n";
    private final static String ERROR_READING_FILE = "Error reading file '%s'\n";
    private final static String BAD_LINE_NUMBER = "Error: Bad line number %s\n";

    private Scanner scanner;
    private Map<Float, String> program;
    private boolean quit;

    public Interpreter() {
        scanner = new Scanner(System.in);
        program = new TreeMap<>();
        quit = false;
    }

    public void run() {
        System.out.println(WELCOME);
        while (!quit) {
            System.out.print(PROMT);
            String line = scanner.nextLine();
            if (line.length() > 0) {
                processLine(line);
            }
        }
    }

    private void addLineToProgram(String numLine, String line) {
        program.put(Float.valueOf(numLine), line.substring(line.indexOf(' ') + 1));
    }

    private void writeLineProgram() {
        for (Float key : program.keySet()) {
            System.out.printf("%02d.%02d %s\n", key.intValue(), (int)(key * 100 % 100), program.get(key));
        }
    }

    private void outputProgram(String fileName) {

    }

    private void inputProgram(String fileName) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(fileName), StandardCharsets.UTF_8))){
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");
                if (isValidLineNumber(tokens[0])) {
                    addLineToProgram(tokens[0], line);
                } else {
                    System.out.printf(BAD_LINE_NUMBER, tokens[0]);
                }
            }
        } catch (IOException e) {
            System.out.printf(ERROR_READING_FILE, fileName);
        }
    }

    private void readWriteProgram(String[] tokens) {
        if (tokens.length < 3) {
            System.out.printf(NOT_ENOUGH_PARAMETERS, tokens[0]);
            return;
        }
        String operation = tokens[1].toUpperCase();
        switch (operation) {
            case "I":
            case "INPUT":
                inputProgram(tokens[2]);
                break;
            case "O":
            case "OUTPUT":
                outputProgram(tokens[2]);
                break;
            default:
                System.out.printf(OPERATION_NOT_RECOGNIZED, tokens[1]);
        }
    }

    private boolean isValidLineNumber(String numLine) {
        return numLine.matches("\\d\\d?\\.\\d\\d?");
    }

    private void processLine(String line) {
        String[] tokens = line.split(" ");
        if (isValidLineNumber(tokens[0])) {
            addLineToProgram(tokens[0], line);
        } else {
            String cmd = tokens[0].toUpperCase();
            switch (cmd) {
                case Q:
                case QUIT:
                    quit = true;
                    break;
                case E:
                case ERASE:
                    program.clear();
                    break;
                case O:
                case OPEN:
                    readWriteProgram(tokens);
                    break;
                case W:
                case WRITE:
                    writeLineProgram();
                    break;
                default:
                    System.out.printf(COMMAND_NOT_RECOGNIZED, tokens[0]);
            }
        }
    }
}
