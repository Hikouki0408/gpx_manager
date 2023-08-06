package softwaredesign;

public class MetricFactory {
    public Metric getMetric(String name) {
        Metric object;

        switch (name) {
            case "Speed":
                object = new Speed();
                break;
            case "Time":
                object = new Time();
                break;
            case "Distance":
                object = new Distance();
                break;
            case "ElevationGain":
                object = new ElevationGain();
                break;
            case "ElevationLoss":
                object = new ElevationLoss();
                break;
            case "Calories":
                object = new Calories();
                break;
            default:
                object = null;
        }

        return object;
    }
}
