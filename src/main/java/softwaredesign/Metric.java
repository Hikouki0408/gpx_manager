package softwaredesign;

public abstract class Metric {
    protected String name;
    public abstract double getValue();
    public abstract void setValue(double value);
    public abstract String getName();
}

final class Speed extends Metric {
    double avgSpeed;

    public Speed() {
        name = new String("Speed");
    }

    public double getValue() { return avgSpeed; }
    public void setValue(double value) { this.avgSpeed = value; }
    public String getName() { return name; }
}

final class Time extends Metric {
    double timeElapsed;

    public Time() {
        name = "Time";
    }

    public double getValue() {
        return timeElapsed;
    }
    public void setValue(double value) { this.timeElapsed = value; }
    public String getName() { return name; }
}

final class Distance extends Metric {
    double distanceTraveled;

    public Distance() {
        name = "Distance";
    }

    public double getValue() {
        return distanceTraveled;
    }
    public void setValue(double value) { this.distanceTraveled = value; }
    public String getName() { return name; }
}

final class ElevationGain extends Metric {
    double elevationGained;

    public ElevationGain() {
        name = "ElevationGain";
    }

    public double getValue() {
        return elevationGained;
    }
    public void setValue(double value) { this.elevationGained = value; }
    public String getName() { return name; }
}

final class ElevationLoss extends Metric {
    double elevationLost;

    public ElevationLoss() {
        name = "ElevationLoss";
    }

    public double getValue() {
        return elevationLost;
    }
    public void setValue(double value) { this.elevationLost = value; }
    public String getName() { return name; }
}

final class Calories extends Metric {
    double caloriesBurnt;

    public Calories() {
        name = "Calories";
    }

    public double getValue() {
        return caloriesBurnt;
    }
    public void setValue(double value) { this.caloriesBurnt = value; }
    public String getName() { return name; }
}
