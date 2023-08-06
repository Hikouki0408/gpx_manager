package softwaredesign;

import io.jenetics.jpx.*;
import io.jenetics.jpx.geom.Geoid;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Calculations {
    private Calculations() {
        throw new IllegalStateException("Utility calculations class.");
    }

    private static ArrayList<Double> calculateElevation(GPX gpxFile) {
        ArrayList<Double> elevations = new ArrayList<>();
        ArrayList<Double> elevationValues = new ArrayList<>();
        elevationValues.add(0.0);
        elevationValues.add(0.0);

        List<WayPoint> points = gpxFile.getTracks().get(0).getSegments().get(0).getPoints();

        for (WayPoint point: points) {
            if (point.getElevation().isPresent()) {
                elevations.add(point.getElevation().get().doubleValue());
            }
        }

        try {
            for (int i = 0; i < elevations.size()-1; i++) {
                Double prevValue = elevationValues.get(0);
                if((elevations.get(i) > elevations.get(i+1))){
                    elevationValues.set(0, prevValue + (elevations.get(i) - elevations.get(i+1)));
                } else {
                    elevationValues.set(1, prevValue + (elevations.get(i) + elevations.get(i+1)));
                }
            }
        } catch (Exception ignored) {}
        // Since there is no way of returning two values in Java,
        // we'll return an array of 2 ints (Elevation gain and loss)
        return elevationValues;
    }

    public static double calculateElevationGain(GPX gpxFile) {

        return calculateElevation(gpxFile).get(1);
    }
    public static double calculateElevationLoss(GPX gpxFile) {
        return calculateElevation(gpxFile).get(0);
    }

    public static double calculateTotalDistance(GPX gpxFile) {
        final Length distanceTraveled = gpxFile.tracks().flatMap(Track::segments).findFirst().map(TrackSegment::points).orElse(Stream.empty()).collect(Geoid.WGS84.toPathLength());

        return distanceTraveled.doubleValue();
    }

    public static double calculateAvgSpeed(GPX gpxFile) {
        return ((calculateTotalDistance(gpxFile) / 1000) / (((double)(calculateTimeElapsed(gpxFile))) / 3600));
    }

    public static double calculateTimeElapsed(GPX gpxFile) {
        Track track = gpxFile.getTracks().get(0);

        ZonedDateTime timeEnd = track.getSegments().get(0).getPoints().get(track.getSegments().get(0).getPoints().size() - 1).getTime().get();
        ZonedDateTime timeStart = track.getSegments().get(0).getPoints().get(0).getTime().get();

        return Duration.between(timeStart, timeEnd).getSeconds();
    }

    private static double calculateBMR(int weight, int height, int age, boolean sex) {
        double baseFormula = 10 * weight + 6.25 * height - 5 * age;
        return sex ? baseFormula - 161 : baseFormula + 5;
    }

    public static double calculateCalories(User user, Sport sport, double time) {
        double sportCoefficient = sport.getSportCoefficient();
        int weight = user.getWeight();
        int height = user.getHeight();
        int age = user.getAge();
        boolean sex = user.getSex();

        double baseFormula = (3.5 * sportCoefficient * weight * (time / 60)) / 200;
        return sex ? baseFormula : (baseFormula * calculateBMR(weight, height, age, false) / calculateBMR(weight, height, age, true));
    }
}
