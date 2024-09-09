import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class lab1 {
    private static Color OPENLAND = new Color(248, 148, 18);
    private static Color ROUGHMEADOW = new Color(255, 192, 0);
    private static Color EASYFOREST = new Color(255, 255, 255);
    private static Color SLOWFOREST = new Color(2, 208, 60);
    private static Color WALKFOREST = new Color(2, 136, 40);
    private static Color IMPASSIBLEVEG = new Color(5, 73, 24);
    private static Color LKESWMPMRSH = new Color(0, 0, 255);
    private static Color PAVEDROAD = new Color(71, 51, 3);
    private static Color FOOTPATH = new Color(0, 0, 0);
    private static Color OUTOFBOUNDS = new Color(205, 0, 101);
    private static Color PATH = new Color(118, 63, 231);

    private static Double[][] readElevation(String filename) throws FileNotFoundException {
        Double[][] result = new Double[500][395];
        
        Scanner scanner = new Scanner(new File(filename));
        int lineNum = 0;
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] strings = line.split(" ");

            for(int i = 0; i < strings.length-5; i++) {
                result[lineNum][i] = Double.parseDouble(strings[i]);
            }
            lineNum++;
        }
        scanner.close();

        return result;
    }

    private static ArrayList<Point> readPoints(String filename) throws FileNotFoundException {
        ArrayList<Point> result = new ArrayList<>();
        
        Scanner scanner = new Scanner(new File(filename));
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] points = line.split(" ");
            result.add(new Point(Integer.parseInt(points[0]), Integer.parseInt(points[1])));
        }
        scanner.close();

        return result;
    }

    private static double getHeuristic(Point p1, Point p2) {
        Double x1 = p1.getX();
        Double x2 = p2.getX();
        Double y1 = p1.getY();
        Double y2 = p2.getY();
        
        return Math.sqrt(Math.pow((x2-x1), 2) + Math.pow((y2-y1), 2));
    }

    private static BufferedImage getPath(String imageFile, ArrayList<Double> elevations, ArrayList<Point> points) throws IOException {
        BufferedImage image = ImageIO.read(new File(imageFile));

        for(int i = 0; i < points.size()-1; i++) {
            
        }
        
        return image;
    }

    public static void main(String[] args) {
        try {
            String inputImage = args[0];
            Double[][] elevations = readElevation(args[1]);
            ArrayList<Point> points = readPoints(args[2]);
            String outputImage = args[3];

            System.out.println(elevations[0][56]);

            //BufferedImage result = getPath(inputImage, elevations, points);
            
            //ImageIO.write(result, "png", new File(outputImage));
        } catch (FileNotFoundException e) {
            e.printStackTrace();;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
