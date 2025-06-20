import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Scanner;
import javax.imageio.ImageIO;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

public class lab1 {
    private static final int OPENLAND = new Color(248, 148, 18).getRGB();
    private static final int ROUGHMEADOW = new Color(255, 192, 0).getRGB();
    private static final int EASYFOREST = new Color(255, 255, 255).getRGB();
    private static final int SLOWFOREST = new Color(2, 208, 60).getRGB();
    private static final int WALKFOREST = new Color(2, 136, 40).getRGB();
    private static final int LKESWMPMRSH = new Color(0, 0, 255).getRGB();
    private static final int PAVEDROAD = new Color(71, 51, 3).getRGB();
    private static final int FOOTPATH = new Color(0, 0, 0).getRGB();
    private static final int PATH = new Color(118, 63, 231).getRGB();

    private static final int OPENLANDCOST = 50;
    private static final int ROUGHMEADOWCOST = 300;
    private static final int EASYFORESTCOST = 100;
    private static final int SLOWFORESTCOST = 250;
    private static final int WALKFORESTCOST = 300;
    private static final int LKESWMPMRSHCOST = 500;
    private static final int PAVEDROADCOST = 1;
    private static final int FOOTPATHCOST = 1;

    // A new weighted point to be used in the program
    public static class WeightedPoint {
        public Point point;             // The x, y of the point
        public double weight;           // The weight of the point (so what's next can be found)
        public WeightedPoint parent;    // The point before this point
        public double cost;             // The total cost of the path to get to the point

        public WeightedPoint(Point point, double weight, WeightedPoint parent, double cost) {
            this.point = point;
            this.weight = weight;
            this.parent = parent;
            this.cost = cost;
        }
    }
  
    // Reads in the elevations
    private static Double[][] readElevation(String filename) throws FileNotFoundException {
        Double[][] result = new Double[395][500];
        
        Scanner scanner = new Scanner(new File(filename));
        Scanner scanner2;
        int lineNum = 0;
        while(scanner.hasNextLine()) {
            scanner2 = new Scanner(scanner.nextLine());
            int counter = 0;
            while(scanner2.hasNextDouble()) {
                if(counter < 395) {
                    result[counter][lineNum] = scanner2.nextDouble();
                } else {
                    break;
                }
                counter++;
            }
            lineNum++;
            scanner2.close();
        }
        scanner.close();

        return result;
    }

    // Reads in all the points
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

    // Gets the heuristic (just finds the distance between the two points)
    private static double getHeuristic(Point p1, Point p2, Double[][] elevations) {
        int x1 = (int) p1.getX();
        int x2 = (int) p2.getX();
        int y1 = (int) p1.getY();
        int y2 = (int) p2.getY();
        
        // Distance formula
        return Math.sqrt(Math.pow((x2-x1), 2) + Math.pow((y2-y1), 2) + Math.pow(elevations[x1][y1] - elevations[x2][y2], 2));
    }

    // Prints all the paths from the final points
    private static void printPathToImage(BufferedImage image, String outputImage, ArrayList<WeightedPoint> points) throws IOException {
        BufferedImage newImage = image;

        double totalCost = 0;

        // Goes through the 'parents' of every final point until you hit the starting point and prints the point to the image
        for(WeightedPoint point : points) {
            // the the last point should have total cost of path, so the cost of each final point is added to the total
            totalCost += point.cost;
            WeightedPoint newPoint = point;
            while(newPoint != null) {
                image.setRGB((int) newPoint.point.getX(), (int) newPoint.point.getY(), PATH);
                newPoint = newPoint.parent;
            }
        }

        System.out.println(totalCost + " m");
        ImageIO.write(newImage, "png", new File(outputImage));
    }

    // Finds the final weighted point in the path
    private static WeightedPoint getPath(BufferedImage image, Double[][] elevations, Point currPoint, Point finPoint) throws IOException {
        
        // Creates a priority queue and compares the weights of the points
        PriorityQueue<WeightedPoint> queue = new PriorityQueue<>(new Comparator<WeightedPoint>() {
            @Override
            public int compare(WeightedPoint x, WeightedPoint y) {
                if(x.weight < y.weight) {
                    return -1;
                } else
                    return 1;
            }
        });

        // Adds the first point to the queue with no weight or cost
        queue.add(new WeightedPoint(currPoint, 0, null, 0));

        // A seen list because without one java runs out of heap space.
        HashSet<Point> seen = new HashSet<>();

        // Keeps going until a solution is found, or there are no more viable points in the image
        while (queue.size() != 0) {
            // Gets the next best (least weighted) point from the queue
            WeightedPoint point = queue.remove();
            // If the current point is the solution, the optimal path has been reached
            if(point.point.getX() == finPoint.getX() && point.point.getY() == finPoint.getY()) {
                return point;
            }
            
            // Gets every pixel arrount the current point and creates an array
            Point p1 = new Point((int) point.point.getX(),(int) point.point.getY()-1);     // North
            Point p2 = new Point((int) point.point.getX()+1,(int) point.point.getY()-1);   // North-East
            Point p3 = new Point((int) point.point.getX()+1,(int) point.point.getY());     // East
            Point p4 = new Point((int) point.point.getX()+1,(int) point.point.getY()+1);   // South-East
            Point p5 = new Point((int) point.point.getX(),(int) point.point.getY()+1);     // South
            Point p6 = new Point((int) point.point.getX()-1,(int) point.point.getY()+1);   // South-West
            Point p7 = new Point((int) point.point.getX()-1,(int) point.point.getY());     // West
            Point p8 = new Point((int) point.point.getX()-1,(int) point.point.getY()-1);   // North-West
            List<Point> newPoints = Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8);

            // Goes through all the new points and if they haven't been seen before, the are added to the queue
            for(Point p : newPoints) {
                if(!seen.contains(p)) {
                    // Makes sure the point is on the image (doesn't go out of bounds)
                    if(p.getX() < image.getWidth() && p.getX() >= 0 &&  p.getY() < image.getHeight() && p.getY() >= 0) {
                        seen.add(p);
                        // Gets the color of the new point as well as the x and y values
                        int rgb = image.getRGB((int) p.getX(), (int) p.getY());
                        int x = (int) p.getX();
                        int y = (int) p.getY();

                        // Gets the difference in elevations
                        double elevationDiff = elevations[(int) point.point.getX()][(int) point.point.getY()] - elevations[x][y];

                        // Gets the distance traveled to the point
                        double pixelDist = 0;
                        if(p.getX() != point.point.getX() && p.getY() != point.point.getY()) {
                            pixelDist = Math.sqrt(Math.pow(10.29, 2) + Math.pow(7.55, 2) + Math.pow((elevationDiff), 2));
                        } else {
                            if(p.getX() != point.point.getX()) {
                                pixelDist = Math.sqrt(Math.pow(10.29, 2) + Math.pow((elevationDiff), 2));
                            }
                            if(p.getY() != point.point.getY()) {
                                pixelDist = Math.sqrt(Math.pow(7.55, 2) + Math.pow((elevationDiff), 2));
                            }
                        }

                        // Finds the cost to get to the next point
                        double newPointCost = point.cost + pixelDist;

                        // Adds the point to the queue based on the rgb of the pixel, if its impassible vegetation or out of bounds, the point isn't considered
                        if(rgb == OPENLAND) {
                            queue.add(new WeightedPoint(p, getHeuristic(p, finPoint, elevations) + newPointCost + OPENLANDCOST, point, newPointCost));
                        }
                        if(rgb == ROUGHMEADOW) {
                            queue.add(new WeightedPoint(p, getHeuristic(p, finPoint, elevations) + newPointCost + ROUGHMEADOWCOST, point, newPointCost));
                        }
                        if(rgb == EASYFOREST) {
                            queue.add(new WeightedPoint(p, getHeuristic(p, finPoint, elevations) + newPointCost + EASYFORESTCOST, point, newPointCost));
                        }
                        if(rgb == SLOWFOREST) {
                            queue.add(new WeightedPoint(p, getHeuristic(p, finPoint, elevations) + newPointCost + SLOWFORESTCOST, point, newPointCost));
                        }
                        if(rgb == WALKFOREST) {
                            queue.add(new WeightedPoint(p, getHeuristic(p, finPoint, elevations) + newPointCost + WALKFORESTCOST, point, newPointCost));
                        }
                        if(rgb == LKESWMPMRSH) {
                            queue.add(new WeightedPoint(p, getHeuristic(p, finPoint, elevations) + newPointCost + LKESWMPMRSHCOST, point, newPointCost));
                        }
                        if(rgb == PAVEDROAD) {
                            queue.add(new WeightedPoint(p, getHeuristic(p, finPoint, elevations) + newPointCost + PAVEDROADCOST, point, newPointCost));
                        }
                        if(rgb == FOOTPATH) {
                            queue.add(new WeightedPoint(p, getHeuristic(p, finPoint, elevations) + newPointCost + FOOTPATHCOST, point, newPointCost));
                        }
                    }
                }
            }
        }
        
        // If nothing is found.
        return null;
    }

    public static void main(String[] args) {
        try {
            BufferedImage inputImage = ImageIO.read(new File(args[0]));
            Double[][] elevations = readElevation(args[1]);
            ArrayList<Point> points = readPoints(args[2]);
            String outputImage = args[3];

            ArrayList<WeightedPoint> result = new ArrayList<>();

            // Goes through all the point-pairs
            for(int i = 0; i < points.size()-1; i++) {
                Point currentPoint = points.get(i);
                Point nextPoint = points.get(i+1);

                result.add(getPath(inputImage, elevations, currentPoint, nextPoint));     
            }

            // prints the resulting points to the image and prints the total distance to sys. out
            printPathToImage(inputImage, outputImage, result);
        } catch (FileNotFoundException e) {
            e.printStackTrace();;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}