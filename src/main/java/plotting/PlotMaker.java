package plotting;

import calculator.Calculator;
import calculator.UnknownWordException;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;

public class PlotMaker {

    private final class VariableData {
        private final HashSet<Integer> indexes;
        private final HashMap<Character, Double> values;

        VariableData(HashSet<Integer> indexes, HashMap<Character, Double> values) {
            this.indexes = indexes;
            this.values = values;
        }
    }

    private Calculator calculator;

    public PlotMaker(Calculator calculator) throws NullPointerException {

        if (calculator == null) {
            throw new NullPointerException("calculator is null.");
        }
        this.calculator = calculator;
    }

    private VariableData findVariables(String expression) {
        HashSet<Integer> variableIndexes = new HashSet<>();
        HashMap<Character, Double> variableValues = new HashMap<>();

        do {
            try {
                calculator.calculate(expression);
                break;
            } catch (UnknownWordException exception) {
                if (exception.getWord().length() == 1) {
                    variableIndexes.add(exception.getIndex());
                    variableValues.putIfAbsent(expression.charAt(exception.getIndex()), 0d);
                    char[] arr = expression.toCharArray();
                    arr[exception.getIndex()] = '0';
                    expression = new String(arr);
                } else {
                    throw exception;
                }
            }
        } while (true);
        return new VariableData(variableIndexes, variableValues);
    }

    private String insertValues(String expression, VariableData data) {
        StringBuilder result = new StringBuilder(expression.length());

        for (int i = 0; i < expression.length(); i++) {
            if (data.indexes.contains(i)) {
                result.append(data.values.get(expression.charAt(i)));
            } else {
                result.append(expression.charAt(i));
            }
        }

        return result.toString();
    }

    private static void disableWarning() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe u = (Unsafe) theUnsafe.get(null);

            Class cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
        } catch (Exception e) {
            // ignore
        }
    }

    private void createPlot(String expression, VariableData data) {
        disableWarning();

        // Define a function to plot
        Mapper mapper = new Mapper() {
            @Override
            public double f(double x, double y) {
                boolean flag = true;

                for (Character variable : data.values.keySet()) {
                    if (flag) {
                        data.values.replace(variable, x);
                        flag = false;
                    } else {
                        data.values.replace(variable, y);
                        flag = true;
                    }
                }

                return calculator.calculate(insertValues(expression, data));
            }
        };

        // Define range and precision for the function to plot
        Range range = new Range(-10, 10);
        int steps = 64;

        // Create the object to represent the function over the given range.
        final Shape surface = Builder.buildOrthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
        surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
                surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
        surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(false);

        // Create a chart
        Chart chart = AWTChartComponentFactory.chart(Quality.Fastest, "awt");
        chart.getScene().getGraph().add(surface);

        boolean flag = true;
        for (Character variable : data.values.keySet()) {
            if (flag) {
                chart.getAxeLayout().setXAxeLabel(Character.toString(variable));
                flag = false;
            } else {
                chart.getAxeLayout().setYAxeLabel(Character.toString(variable));
                flag = true;
            }
        }

        chart.getAxeLayout().setZAxeLabel("                  Result");
        ChartLauncher.openChart(chart);
    }

    public void showPlot(String mathExpression) throws NullPointerException, IllegalArgumentException {
        VariableData data = findVariables(mathExpression);
        if (data.values.size() > 2) {
            throw new IllegalArgumentException("Three or more variables in the expression.");
        }
        createPlot(mathExpression, data);
        System.out.print("\n" +
                "------------------------------------\n" +
                "Rotate     : Left click and drag mouse\n" +
                "Scale      : Roll mouse wheel\n" +
                "Z Shift    : Right click and drag mouse\n" +
                "Animate    : Double left click\n" +
                //"Screenshot : Press 's'\n" +
                "------------------------------------\n"
                + "\n");
    }
}