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
import java.util.PriorityQueue;

public class lab1 {
    private static final int OPENLAND = new Color(248, 148, 18).getRGB();
    private static final int ROUGHMEADOW = new Color(255, 192, 0).getRGB();
    private static final int EASYFOREST = new Color(255, 255, 255).getRGB();
    private static final int SLOWFOREST = new Color(2, 208, 60).getRGB();
    private static final int WALKFOREST = new Color(2, 136, 40).getRGB();
    private static final int IMPASSIBLEVEG = new Color(5, 73, 24).getRGB();
    private static final int LKESWMPMRSH = new Color(0, 0, 255).getRGB();
    private static final int PAVEDROAD = new Color(71, 51, 3).getRGB();
    private static final int FOOTPATH = new Color(0, 0, 0).getRGB();
    private static final int OUTOFBOUNDS = new Color(205, 0, 101).getRGB();
    private static final int PATH = new Color(118, 63, 231).getRGB();

    private static final int OPENLANDCOST = 1;
    private static final int ROUGHMEADOWCOST = 6;
    private static final int EASYFORESTCOST = 2;
    private static final int SLOWFORESTCOST = 3;
    private static final int WALKFORESTCOST = 4;
    private static final int IMPASSIBLEVEGCOST = 7;
    private static final int LKESWMPMRSHCOST = 5;
    private static final int PAVEDROADCOST = 1;
    private static final int FOOTPATHCOST = 1;
    private static final int OUTOFBOUNDSCOST = 10;


    public static class WeightedPoint {
        public Point point;
        public double weight;
        public WeightedPoint parent;

        public WeightedPoint(Point point, double weight, WeightedPoint parent) {
            this.point = point;
            this.weight = weight;
            this.parent = parent;
        }
    }

    private static ArrayList<ArrayList<Double>> readElevation(String filename) throws FileNotFoundException {
        ArrayList<ArrayList<Double>> result = new ArrayList<>();
        
        Scanner scanner = new Scanner(new File(filename));
        ArrayList<Double> temp = null;
        while(scanner.hasNextLine()) {
            temp = new ArrayList<>();
            String line = scanner.nextLine();
            String[] strings = line.split(" ");
            for(int i = 0; i < strings.length; i++) {
                if(strings.length-i != 5) {
                    temp.add(Double.parseDouble(strings[i]));
                }
            }
            result.add(temp);
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

    private static BufferedImage printPathToImage(BufferedImage image, WeightedPoint point) {
        double totalDist = 0;

        WeightedPoint newPoint = point;
        while(newPoint.parent != null) {
            image.setRGB((int) newPoint.point.getX(), (int) newPoint.point.getX(), PATH);
            totalDist += point.weight;
        }

        System.out.println(totalDist);
        return image;
    }

    private static BufferedImage getPath(BufferedImage image, ArrayList<ArrayList<Double>> elevations, Point currPoint, Point finPoint) throws IOException {
        PriorityQueue<WeightedPoint> queue = new PriorityQueue<>(new Comparator<WeightedPoint>() {
            @Override
            public int compare(WeightedPoint x, WeightedPoint y) {
                if(x.weight < y.weight) {
                    return -1;
                } else
                    return 1;
            }
        });
        queue.add(new WeightedPoint(currPoint, 0, null));
        WeightedPoint result = null;
        while (queue.size() != 0) {
            WeightedPoint point = queue.peek();
            if(point.point == finPoint) {
                result = point;
                break;
            }
            queue.remove();

            Point p1 = new Point((int) point.point.getX(),(int) point.point.getY()-1);     // North
            Point p2 = new Point((int) point.point.getX()+1,(int) point.point.getY()-1);   // North-East
            Point p3 = new Point((int) point.point.getX()+1,(int) point.point.getY());     // East
            Point p4 = new Point((int) point.point.getX()+1,(int) point.point.getY()+1);   // South-East
            Point p5 = new Point((int) point.point.getX(),(int) point.point.getY()+1);     // South
            Point p6 = new Point((int) point.point.getX()-1,(int) point.point.getY()+1);   // South-West
            Point p7 = new Point((int) point.point.getX()-1,(int) point.point.getY());     // West
            Point p8 = new Point((int) point.point.getX()-1,(int) point.point.getY()-1);   // North-West
            List<Point> newPoints = Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8);

            for(Point p : newPoints) {
                if(p.getX() < image.getWidth() && p.getX() >= 0 &&  p.getY() < image.getHeight() && p.getY() >= 0) {
                    int rgb = image.getRGB((int) p.getX(), (int) p.getY());
                    if(rgb == OPENLAND) {
                        queue.add(new WeightedPoint(p, getHeuristic(p, finPoint) + OPENLANDCOST + elevations.get((int) p.getY()).get((int) p.getX()), point));
                    }
                    if(rgb == ROUGHMEADOW) {
                        queue.add(new WeightedPoint(p, getHeuristic(p, finPoint) + ROUGHMEADOWCOST + elevations.get((int) p.getY()).get((int) p.getX()), point));
                    }
                    if(rgb == EASYFOREST) {
                        queue.add(new WeightedPoint(p, getHeuristic(p, finPoint) + EASYFORESTCOST + elevations.get((int) p.getY()).get((int) p.getX()), point));
                    }
                    if(rgb == SLOWFOREST) {
                        queue.add(new WeightedPoint(p, getHeuristic(p, finPoint) + SLOWFORESTCOST + elevations.get((int) p.getY()).get((int) p.getX()), point));
                    }
                    if(rgb == WALKFOREST) {
                        queue.add(new WeightedPoint(p, getHeuristic(p, finPoint) + WALKFORESTCOST + elevations.get((int) p.getY()).get((int) p.getX()), point));
                    }
                    if(rgb == IMPASSIBLEVEG) {
                        queue.add(new WeightedPoint(p, getHeuristic(p, finPoint) + IMPASSIBLEVEGCOST + elevations.get((int) p.getY()).get((int) p.getX()), point));
                    }
                    if(rgb == LKESWMPMRSH) {
                        queue.add(new WeightedPoint(p, getHeuristic(p, finPoint) + LKESWMPMRSHCOST + elevations.get((int) p.getY()).get((int) p.getX()), point));
                    }
                    if(rgb == PAVEDROAD) {
                        queue.add(new WeightedPoint(p, getHeuristic(p, finPoint) + PAVEDROADCOST + elevations.get((int) p.getY()).get((int) p.getX()), point));
                    }
                    if(rgb == FOOTPATH) {
                        queue.add(new WeightedPoint(p, getHeuristic(p, finPoint) + FOOTPATHCOST + elevations.get((int) p.getY()).get((int) p.getX()), point));
                    }
                    if(rgb == OUTOFBOUNDS) {
                        queue.add(new WeightedPoint(p, getHeuristic(p, finPoint) + OUTOFBOUNDSCOST + elevations.get((int) p.getY()).get((int) p.getX()), point));
                    }
                }
            }
        }

        return printPathToImage(image, result);
    }

    public static void main(String[] args) {
        try {
            BufferedImage inputImage = ImageIO.read(new File(args[0]));
            ArrayList<ArrayList<Double>> elevations = readElevation(args[1]);
            ArrayList<Point> points = readPoints(args[2]);
            String outputImage = args[3];

            BufferedImage result = inputImage;
            for(int i = 0; i < points.size()-1; i++) {
                Point currentPoint = points.get(i);
                Point nextPoint = points.get(i+1);

                result = getPath(result, elevations, currentPoint, nextPoint);
            }

            ImageIO.write(result, "png", new File(outputImage));
        } catch (FileNotFoundException e) {
            e.printStackTrace();;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}