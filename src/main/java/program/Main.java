package program;

import calculator.Calculator;
import calculator.UnknownWordException;
import functions.*;
import org.apache.log4j.BasicConfigurator;
import plotting.PlotMaker;

import java.util.Scanner;

/**
 * Main Class
 * */
class Main {

    public static void main(String[] args) {
        Calculator calculator = new Calculator(new Sine(), new Cosine(), new AbsoluteValue());
        PlotMaker plotMaker = new PlotMaker(calculator);
        Scanner scan = new Scanner(System.in);
        BasicConfigurator.configure();

        do {
            System.out.println();
            System.out.print("Expression: ");
            String expression = scan.nextLine();
            try {
                System.out.println("Result: " + calculator.calculate(expression));
            } catch (UnknownWordException exception) {
                try {
                    System.out.println("Please wait, plot will be ready soon.");
                    plotMaker.showPlot(expression);
                } catch (NullPointerException | IllegalArgumentException e) {
                    System.out.println("Exception: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Unexpected error.");
                }
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
            System.out.print("Exit?(y/n): ");
        } while (!scan.nextLine().toLowerCase().equals("y"));
    }
}