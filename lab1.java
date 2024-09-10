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
    private static final int PATH = new Color(118, 63, 231).getRGB();

    private static final int OPENLANDCOST = 275;
    private static final int ROUGHMEADOWCOST = 450;
    private static final int EASYFORESTCOST = 300;
    private static final int SLOWFORESTCOST = 350;
    private static final int WALKFORESTCOST = 450;
    private static final int IMPASSIBLEVEGCOST = 600;
    private static final int LKESWMPMRSHCOST = 500;
    private static final int PAVEDROADCOST = 10;
    private static final int FOOTPATHCOST = 10;

    private static double totalCost = 0;

    public static class WeightedPoint {
        public Point point;
        public double weight;
        public WeightedPoint parent;
        public double cost;

        public WeightedPoint(Point point, double weight, WeightedPoint parent, double cost) {
            this.point = point;
            this.weight = weight;
            this.parent = parent;
            this.cost = cost;
        }
    }
  
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
        WeightedPoint newPoint = point;

        while(newPoint != null) {
            image.setRGB((int) newPoint.point.getX(), (int) newPoint.point.getY(), PATH);
            newPoint = newPoint.parent;
        }

        return image;
    }

    private static WeightedPoint getPath(BufferedImage image, Double[][] elevations, Point currPoint, Point finPoint) throws IOException {
        PriorityQueue<WeightedPoint> queue = new PriorityQueue<>(new Comparator<WeightedPoint>() {
            @Override
            public int compare(WeightedPoint x, WeightedPoint y) {
                if(x.weight < y.weight) {
                    return -1;
                } else
                    return 1;
            }
        });
        queue.add(new WeightedPoint(currPoint, 0, null, 0));
        ArrayList<Point> seen = new ArrayList<>();
        WeightedPoint result = null;

        while (queue.size() != 0) {
            WeightedPoint point = queue.remove();
            if(point.point.getX() == finPoint.getX() && point.point.getY() == finPoint.getY()) {
                totalCost += point.cost;
                return point;
            }
            
            Point p1 = new Point((int) point.point.getX(),(int) point.point.getY()-1);     // North
            Point p2 = new Point((int) point.point.getX()+1,(int) point.point.getY()-1);     // North-East
            Point p3 = new Point((int) point.point.getX()+1,(int) point.point.getY());     // East
            Point p4 = new Point((int) point.point.getX()+1,(int) point.point.getY()+1);     // South-East
            Point p5 = new Point((int) point.point.getX(),(int) point.point.getY()+1);     // South
            Point p6 = new Point((int) point.point.getX()-1,(int) point.point.getY()+1);     // South-West
            Point p7 = new Point((int) point.point.getX()-1,(int) point.point.getY());     // West
            Point p8 = new Point((int) point.point.getX()-1,(int) point.point.getY()-1);     // North-West
            List<Point> newPoints = Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8);

            
            for(Point p : newPoints) {
                if(!seen.contains(p)) {
                    if(p.getX() < image.getWidth() && p.getX() >= 0 &&  p.getY() < image.getHeight() && p.getY() >= 0) {
                        seen.add(p);
                        int rgb = image.getRGB((int) p.getX(), (int) p.getY());
                        int x = (int) p.getX();
                        int y = (int) p.getY();
                        int lastX = (int) point.point.getX();
                        int lastY = (int) point.point.getY();
                        double pixelDist = 0;
                        if(p.getX() != point.point.getX()) {
                            pixelDist += 10.29;
                        }
                        if(p.getY() != point.point.getY()) {
                            pixelDist += 7.55;
                        }
                        if(rgb == OPENLAND) {
                            queue.add(new WeightedPoint(p, getHeuristic(p, finPoint) + point.cost + OPENLANDCOST + (elevations[x][y] - elevations[lastX][lastY]) + pixelDist, point, point.cost + pixelDist));
                        }
                        if(rgb == ROUGHMEADOW) {
                            queue.add(new WeightedPoint(p, getHeuristic(p, finPoint) + point.cost + ROUGHMEADOWCOST + (elevations[x][y] - elevations[lastX][lastY]) + pixelDist, point, point.cost + pixelDist));
                        }
                        if(rgb == EASYFOREST) {
                            queue.add(new WeightedPoint(p, getHeuristic(p, finPoint) + point.cost + EASYFORESTCOST + (elevations[x][y] - elevations[lastX][lastY]) + pixelDist, point, point.cost + pixelDist));
                        }
                        if(rgb == SLOWFOREST) {
                            queue.add(new WeightedPoint(p, getHeuristic(p, finPoint) + point.cost + SLOWFORESTCOST + (elevations[x][y] - elevations[lastX][lastY]) + pixelDist, point, point.cost + pixelDist));
                        }
                        if(rgb == WALKFOREST) {
                            queue.add(new WeightedPoint(p, getHeuristic(p, finPoint) + point.cost + WALKFORESTCOST + (elevations[x][y] - elevations[lastX][lastY]) + pixelDist, point, point.cost + pixelDist));
                        }
                        if(rgb == IMPASSIBLEVEG) {
                            queue.add(new WeightedPoint(p, getHeuristic(p, finPoint) + point.cost + IMPASSIBLEVEGCOST + (elevations[x][y] - elevations[lastX][lastY]) + pixelDist, point, point.cost + pixelDist));
                        }
                        if(rgb == LKESWMPMRSH) {
                            queue.add(new WeightedPoint(p, getHeuristic(p, finPoint) + point.cost + LKESWMPMRSHCOST + (elevations[x][y] - elevations[lastX][lastY]) + pixelDist, point, point.cost + pixelDist));
                        }
                        if(rgb == PAVEDROAD) {
                            queue.add(new WeightedPoint(p, getHeuristic(p, finPoint) + point.cost + PAVEDROADCOST + (elevations[x][y] - elevations[lastX][lastY]) + pixelDist, point, point.cost + pixelDist));
                        }
                        if(rgb == FOOTPATH) {
                            queue.add(new WeightedPoint(p, getHeuristic(p, finPoint) + point.cost + FOOTPATHCOST + (elevations[x][y] - elevations[lastX][lastY]) + pixelDist, point, point.cost + pixelDist));
                        }
                    }
                }
            }
        }
        
        return null;
    }

    public static void main(String[] args) {
        try {
            BufferedImage inputImage = ImageIO.read(new File(args[0]));
            Double[][] elevations = readElevation(args[1]);
            ArrayList<Point> points = readPoints(args[2]);
            String outputImage = args[3];

            List<WeightedPoint> result = new ArrayList<>();
            for(int i = 0; i < points.size()-1; i++) {
                Point currentPoint = points.get(i);
                Point nextPoint = points.get(i+1);

                result.add(getPath(inputImage, elevations, currentPoint, nextPoint));     
            }


            for(WeightedPoint r : result) {
                inputImage = printPathToImage(inputImage, r);
            }

            System.out.println(totalCost + " m");
            ImageIO.write(inputImage, "png", new File(outputImage));
        } catch (FileNotFoundException e) {
            e.printStackTrace();;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}