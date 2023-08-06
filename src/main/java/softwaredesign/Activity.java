package softwaredesign;

import io.jenetics.jpx.GPX;

import java.util.UUID;

public class Activity {
    private String name;
    private Sport type;
    private String startTime;
    private String endTime;
    private GPX gpxFile;
    private UUID uuid;

    public Activity(String name, Sport type, String startTime, String endTime, GPX gpxFile) {
        this.name = name;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.gpxFile = gpxFile;
    }

    public Activity(String name, Sport type, GPX gpxFile, UUID uuid) {
        this.name = name;
        this.type = type;
        this.gpxFile = gpxFile;
        this.uuid = uuid;
    }

    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void setGPXFile(GPX gpxFile) { this.gpxFile = gpxFile; }
    public void setUUID(UUID uuid) {this.uuid = uuid; }

    public String getName() { return this.name; }
    public Sport getSport() { return this.type; }
    public String getStartTime() { return this.startTime; }
    public String getEndTime() { return this.endTime; }
    public GPX getGPXFile() { return this.gpxFile; }
    public UUID getUUID() { return this.uuid; }

}
