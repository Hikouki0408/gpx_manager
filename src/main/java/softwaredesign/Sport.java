package softwaredesign;

import java.util.List;

public class Sport {
    private final String name;
    private List<Metric> metricList;
    private double sportCoefficient;

    public Sport(String name) {
        this.name = name;
        switch (name) {
            case "Walking":
                this.sportCoefficient = 3.3;
                break;
            case "Running":
                this.sportCoefficient = 7;
                break;
            case "Kayaking":
                this.sportCoefficient = 4;
                break;
            case "Swimming":
                this.sportCoefficient = 6;
                break;
            case "Cycling":
                this.sportCoefficient = 5.5;
                break;
            default:
                break;
        }
    }

    public void setMetricList(List<Metric> metricList) { this.metricList = metricList; }

    public double getSportCoefficient() { return sportCoefficient; }
    public List<Metric> getMetricList() { return metricList; }
    public String getName() { return name; }

}
