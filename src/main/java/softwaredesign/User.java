package softwaredesign;

import java.util.List;
import java.util.UUID;

public class User {
    private String name;
    private int age;
    private int weight;
    private int height;
    private boolean sex;        // 0 - for men, 1 - for women
    private List<Activity> activityList;
    private UUID uuid;

    // Getters
    public String           getName()         { return name; }
    public int              getAge()          { return age; }
    public int              getWeight()       { return weight; }
    public boolean          getSex()          { return sex; }
    public int              getHeight()       { return height; }
    public List<Activity>   getActivityList() { return activityList; }
    public UUID             getUUID()         { return uuid; }

    // Setters
    public void setName(String name)                         { this.name = name; }
    public void setAge(int age)                              { this.age = age; }
    public void setWeight(int weight)                        { this.weight = weight; }
    public void setSex(boolean sex)                          { this.sex = sex; }
    public void setHeight(int height)                        { this.height = height; }
    public void setActivityList(List<Activity> activityList) { this.activityList = activityList; }
    public void setUUID(UUID uuid)                           { this.uuid = uuid; }
}
