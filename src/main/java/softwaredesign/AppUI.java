package softwaredesign;

import de.fhpotsdam.unfolding.data.GPXReader;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import javafx.application.Application;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

public class AppUI extends Application {

    static final int APPLICATION_WIDTH  = 640;
    static final int APPLICATION_HEIGHT = 360;

    static final String DATA_DIR_NAME  = "data";
    static final String USERS_FILENAME  = "users.json";
    static final String DELIM_CHAR = "\\";

    static final String DIR_PATH        = Utilities.getDirPath();
    static final String DATA_DIR_PATH   = DIR_PATH + DATA_DIR_NAME + DELIM_CHAR;

    private static JSONArray usersJSONArray;
    private static JSONArray activityJSONArray;

    private User currUser = new User(); // might need to make it public

    SceneLoader scenes = SceneLoader.getInstance();

    static {
        try {
            usersJSONArray = Utilities.getUsersJSONArray(DATA_DIR_PATH);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    /** Initializes the current user object.
     * @param name name of the user.
     * @param weight weight of the user.
     * @param height height of the user.
     * @param age age of the user.
     * @param sex sex of the user.
     * @param uuid uuid of the user
     * */
    private void initCurrUser(String name, int weight, int height, int age, boolean sex, UUID uuid) {
        currUser.setName(name);
        currUser.setWeight(weight);
        currUser.setHeight(height);
        currUser.setAge(age);
        currUser.setSex(sex);
        currUser.setUUID(uuid);
    }


    /** Creates a "Back" button. Whenever the user's input is handled pops the previous scene off the stack in
     * SceneLoader instance and sets the current stage's scene to the retrieved scene.
     * @param stage main stage of the application.
     * @return object of class Button.
     * */
    private Button createBackButton(Stage stage) {
        Button backButton = new Button("Back");

        backButton.setPrefWidth(60);
        backButton.setLayoutX(570);
        backButton.setLayoutY(290);

        backButton.setOnAction(event -> {
            scenes.pop();
            stage.setScene(scenes.top());
        });



        return backButton;
    }

    /** Creates an "Exit" button. Whenever the user's input is handled kills the instance of the SceneLoader
     * singleton and closes the stage (application).
     * @param stage main stage of the application.
     * @return object of class Button.
     * */
    private Button createExitButton(Stage stage) {
        Button exitButton = new Button("Exit");

        exitButton.setPrefWidth(60);
        exitButton.setLayoutX(570);
        exitButton.setLayoutY(320);

        exitButton.setOnAction(event -> {
            SceneLoader.killInstance();
            stage.close();
        });

        return exitButton;
    }

    /** Creates a "Create new activity" scene. Specifies its layout then pushes it onto the stack in SceneLoader instance
     * @param stage main stage of the application.
     * */
    private void createNewActivityScene(Stage stage) {
        Pane newActivitySceneLayout = new Pane();
        Scene newActivityScene = new Scene(newActivitySceneLayout, APPLICATION_WIDTH, APPLICATION_HEIGHT);

        AtomicBoolean warningDisplayed = new AtomicBoolean(false);
        warningDisplayed.set(false);

        AtomicReference<Activity> newActivity = new AtomicReference<>(null);

        /* Create Activity name field */
        TextField activityNameField = new TextField("Activity name");
        activityNameField.setFocusTraversable(false);
        activityNameField.setAlignment(Pos.CENTER);
        activityNameField.setOnMouseClicked(event -> {
            if (Objects.equals(activityNameField.getText(), "Activity name")) {
                activityNameField.setText("");
            }
        });

        /* Create Sport type selection field */
        ComboBox<String> sportTypeBox = new ComboBox<>();
        sportTypeBox.setPrefWidth(150);
        sportTypeBox.setPromptText("Sport type");
        ArrayList<String> sportTypes = new ArrayList<>();
        sportTypes.add("Walking");
        sportTypes.add("Running");
        sportTypes.add("Cycling");
        sportTypes.add("Swimming");
        sportTypes.add("Kayaking");

        sportTypeBox.getItems().addAll(sportTypes);

        AtomicReference<GPX> gpxObject = new AtomicReference<>(null);
        AtomicBoolean importedGPXsuccessfuly = new AtomicBoolean(false);

        /* Create GPX import button */
        Button gpxFileImportButton = new Button("Import GPX");
        gpxFileImportButton.setPrefWidth(150);
        gpxFileImportButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Import a GPS Exchange Format file (GPX)");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GPS Exchange Format", "*.gpx"));

            File gpxFile = fileChooser.showOpenDialog(stage);

            try {
                gpxObject.set(GPX.read(gpxFile.getPath()));
                importedGPXsuccessfuly.set(true);
                gpxFileImportButton.setText(gpxFile.getName());
            } catch (NullPointerException | IOException ignored) {}
        });

        /* Create Submit button */
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
              String activityName = activityNameField.getCharacters().toString().strip();
              String sportType = sportTypeBox.getSelectionModel().getSelectedItem();
              Label wrongInputLabel = new Label();
              wrongInputLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10; -fx-text-fill: red");

              if (Objects.equals(activityName, "") || Objects.equals(activityName, "Activity name") || sportType == null || !importedGPXsuccessfuly.get()) {
                  wrongInputLabel.setText("One or more inputs are invalid.");

                  wrongInputLabel.setLayoutX(260);
                  wrongInputLabel.setLayoutY(250);

                  if (!warningDisplayed.get()) {
                      newActivitySceneLayout.getChildren().add(wrongInputLabel);
                      warningDisplayed.set(true);
                  }
              } else {
                  // Populate Activity object
                  Sport activitySportType = new Sport(sportType);
                  newActivity.set(new Activity(activityName, activitySportType, gpxObject.get(), UUID.randomUUID()));

                  Track track = newActivity.get().getGPXFile().getTracks().get(0);
                  ZonedDateTime timeEnd = track.getSegments().get(0).getPoints().get(track.getSegments().get(0).getPoints().size() - 1).getTime().get();
                  ZonedDateTime timeStart = track.getSegments().get(0).getPoints().get(0).getTime().get();

                  newActivity.get().setStartTime(timeStart.format(RFC_1123_DATE_TIME));
                  newActivity.get().setEndTime(timeEnd.format(RFC_1123_DATE_TIME));

                  // Write activity to JSON file
                  JSONObject activityJSONObject = new JSONObject();
                  activityJSONObject.put("name", newActivity.get().getName());
                  activityJSONObject.put("uuid", newActivity.get().getUUID().toString());
                  activityJSONObject.put("type", newActivity.get().getSport().getName());
                  activityJSONObject.put("startTime", newActivity.get().getStartTime());
                  activityJSONObject.put("endTime", newActivity.get().getEndTime());

                  try {
                      Utilities.writeJSONArray(DATA_DIR_PATH, currUser.getUUID().toString()  + ".json", activityJSONObject );
                  } catch (ParseException | IOException e) {
                      e.printStackTrace();
                  }

                  // Create a GPX file based on the user imported one
                  try {
                      GPX.write(newActivity.get().getGPXFile(), Path.of(DATA_DIR_PATH + newActivity.get().getUUID().toString() + ".gpx"));
                  } catch (IOException e) {
                      e.printStackTrace();
                  }

                  try {
                      createActivitiesScene(stage);
                  } catch (ParseException | IOException e) {
                      e.printStackTrace();
                  }
                  stage.setScene(scenes.top());
              }
        });

        /* Position all elements */
        activityNameField.setLayoutX(250);
        activityNameField.setLayoutY(50);

        sportTypeBox.setLayoutX(250);
        sportTypeBox.setLayoutY(80);

        gpxFileImportButton.setLayoutX(250);
        gpxFileImportButton.setLayoutY(110);

        submitButton.setLayoutX(300);
        submitButton.setLayoutY(220);


        /* Create a Back button */
        Button backButton = createBackButton(stage);

        /* Create Exit button */
        Button exitButton = createExitButton(stage);

        newActivitySceneLayout.getChildren().add(backButton);
        newActivitySceneLayout.getChildren().add(exitButton);
        newActivitySceneLayout.getChildren().add(submitButton);
        newActivitySceneLayout.getChildren().add(sportTypeBox);
        newActivitySceneLayout.getChildren().add(activityNameField);
        newActivitySceneLayout.getChildren().add(gpxFileImportButton);

        scenes.push(newActivityScene);
    }

    /** Creates an "Activities" scene. Specifies its layout then pushes it onto the stack in SceneLoader instance
     * @param stage main stage of the application.
     * */
    private void createActivitiesScene(Stage stage) throws ParseException, IOException {
        Pane activitySceneLayout = new Pane();
        Scene activityScene = new Scene(activitySceneLayout, APPLICATION_WIDTH, APPLICATION_HEIGHT);

        /* Fetch activities based on the file with matching UUID name*/
        activityJSONArray = Utilities.getJSONArray(DATA_DIR_PATH, currUser.getUUID().toString() + ".json");

        if (!activityJSONArray.isEmpty()) {
            ((JSONObject) activityJSONArray.get(0)).get("uuid");

            List<Activity> fetchedActivities = new ArrayList<Activity>();

            for (Object activity : activityJSONArray) {
                String activityName = ((JSONObject) activity).get("name").toString();
                String activityStartTime = ((JSONObject) activity).get("startTime").toString();
                String activityEndTime = ((JSONObject) activity).get("endTime").toString();
                String activityType = ((JSONObject) activity).get("type").toString();
                String activityGPXFileName = ((JSONObject) activity).get("uuid").toString();

                String pathToGPXFile = DATA_DIR_PATH + activityGPXFileName + ".gpx";
                GPX gpxFile = GPX.read(pathToGPXFile);

                MetricFactory metricFactory = new MetricFactory();
                Metric distance, calories, elevationGain, elevationLoss, speed, time;

                distance = metricFactory.getMetric("Distance");
                calories = metricFactory.getMetric("Calories");
                elevationGain = metricFactory.getMetric("ElevationGain");
                elevationLoss = metricFactory.getMetric("ElevationLoss");
                speed = metricFactory.getMetric("Speed");
                time = metricFactory.getMetric("Time");

                Sport activitySport = new Sport(activityType);

                // calcucltae metrics
                distance.setValue(Calculations.calculateTotalDistance(gpxFile));
                speed.setValue(Calculations.calculateAvgSpeed(gpxFile));
                time.setValue(Calculations.calculateTimeElapsed(gpxFile));
                elevationGain.setValue(Calculations.calculateElevationGain(gpxFile));
                elevationLoss.setValue(Calculations.calculateElevationLoss(gpxFile));
                calories.setValue(Calculations.calculateCalories(currUser, activitySport, time.getValue()));

                List<Metric> metricList = new ArrayList<>();
                metricList.add(distance);
                metricList.add(calories);
                metricList.add(elevationGain);
                metricList.add(elevationLoss);
                metricList.add(speed);
                metricList.add(time);


                activitySport.setMetricList(metricList);
                fetchedActivities.add(new Activity(activityName, activitySport, activityStartTime, activityEndTime, gpxFile));
            }
            currUser.setActivityList(fetchedActivities);

        } else {
            currUser.setActivityList(new ArrayList<Activity>());
        }

        ArrayList<String> activities = new ArrayList<>();
//        for (Object user: activityJSONArray) {
//            activities.add(((JSONObject) user).get("name").toString());
//        }

        for (Activity activity: currUser.getActivityList()) {
            activities.add(activity.getName());
        }

        /* Create and setup labels for displaying metrics */
        String distanceTravelledText = new String("Total distance: ");
        String timeStartText = new String("Start time: ");
        String timeEndText = new String("Finish time: ");
        String timeElapsedText = new String("Time elapsed: ");
        String avgSpeedText = new String("Avg speed: ");
        String elevationGainText = new String("Elevation gain: ");
        String elevationLossText = new String("Elevation loss: ");
        String caloriesText = new String("Calories burned: ");

        AtomicBoolean metricsDisplayed = new AtomicBoolean(false);
        Label yourMetricsLabel          = new Label("Your metrics: ");
        Label distanceTravelledLabel    = new Label(distanceTravelledText);
        Label timeStartLabel            = new Label(timeStartText);
        Label timeEndLabel              = new Label(timeEndText);
        Label timeElapsedLabel          = new Label(timeElapsedText);
        Label avgSpeedLabel             = new Label(avgSpeedText);
        Label elevationGainLabel            = new Label(elevationGainText);
        Label elevationLossLabel            = new Label(elevationLossText);
        Label caloriesBurnedLabel       = new Label(caloriesText);

        yourMetricsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14");

        yourMetricsLabel.setLayoutX(250);
        yourMetricsLabel.setLayoutY(25);

        timeStartLabel.setLayoutX(250);
        timeStartLabel.setLayoutY(45);

        timeEndLabel.setLayoutX(250);
        timeEndLabel.setLayoutY(65);

        timeElapsedLabel.setLayoutX(250);
        timeElapsedLabel.setLayoutY(105);

        distanceTravelledLabel.setLayoutX(250);
        distanceTravelledLabel.setLayoutY(145);

        elevationGainLabel.setLayoutX(250);
        elevationGainLabel.setLayoutY(165);

        elevationLossLabel.setLayoutX(250);
        elevationLossLabel.setLayoutY(185);

        avgSpeedLabel.setLayoutX(250);
        avgSpeedLabel.setLayoutY(225);

        caloriesBurnedLabel.setLayoutX(250);
        caloriesBurnedLabel.setLayoutY(245);

        /* Create ListView of usernames */
        ListView<String> activitesListView = new ListView<>();
        activitesListView.setPrefWidth(220);
        activitesListView.getItems().addAll(activities);
        activitesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        activitesListView.setOnMouseReleased(event1 -> {
            String gpxFileName = ((JSONObject) activityJSONArray.get(activitesListView.getSelectionModel().getSelectedIndex())).get("uuid").toString();
            String pathToGPXFile = DATA_DIR_PATH + gpxFileName + ".gpx";

            Button showMapButton = new Button("Map");
            showMapButton.setPrefWidth(60);
            showMapButton.setLayoutX(570);
            showMapButton.setLayoutY(80);

            /* Calculate all metrics */
            GPX gpxObject = null;
            try {
                gpxObject = GPX.read(pathToGPXFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            DecimalFormat twodecimalFormat = new DecimalFormat("#.00");


            if (metricsDisplayed.get()) {
                activitySceneLayout.getChildren().remove(yourMetricsLabel);
                activitySceneLayout.getChildren().remove(distanceTravelledLabel);
                activitySceneLayout.getChildren().remove(timeStartLabel);
                activitySceneLayout.getChildren().remove(timeEndLabel);
                activitySceneLayout.getChildren().remove(timeElapsedLabel);
                activitySceneLayout.getChildren().remove(avgSpeedLabel);
                activitySceneLayout.getChildren().remove(elevationGainLabel);
                activitySceneLayout.getChildren().remove(elevationLossLabel);
                activitySceneLayout.getChildren().remove(caloriesBurnedLabel);
            }

            Activity selectedActivity = currUser.getActivityList().get(activitesListView.getSelectionModel().getSelectedIndex());
            List<Metric> metrics = selectedActivity.getSport().getMetricList();
            for(Metric metric: metrics) {
                switch (metric.getName()) {
                    case "Distance":
                        distanceTravelledLabel.setText(distanceTravelledText + twodecimalFormat.format(metric.getValue() / 1000) + "km");
                        break;
                    case "Calories":
                        caloriesBurnedLabel.setText(caloriesText + (int) metric.getValue() + "kcal");
                        break;
                    case "ElevationGain":
                        elevationGainLabel.setText(elevationGainText + (int) metric.getValue() + "m");
                        break;
                    case "ElevationLoss":
                        elevationLossLabel.setText(elevationLossText + (int) metric.getValue() + "m");
                        break;
                    case "Speed":
                        avgSpeedLabel.setText(avgSpeedText + twodecimalFormat.format(metric.getValue()) + "km/h");
                        break;
                    case "Time":
                        long timeElapsed = (long) metric.getValue();
                        timeElapsedLabel.setText(timeElapsedText +  String.format("%d:%02d:%02d", timeElapsed / 3600, (timeElapsed % 3600) / 60, (timeElapsed % 60)));
                        break;
                    default:
                        break;
                }
            }

            timeStartLabel.setText(timeStartText + selectedActivity.getStartTime());
            timeEndLabel.setText(timeEndText + selectedActivity.getEndTime());

            activitySceneLayout.getChildren().add(yourMetricsLabel);
            activitySceneLayout.getChildren().add(timeStartLabel);
            activitySceneLayout.getChildren().add(timeEndLabel);
            activitySceneLayout.getChildren().add(distanceTravelledLabel);
            activitySceneLayout.getChildren().add(caloriesBurnedLabel);
            activitySceneLayout.getChildren().add(elevationGainLabel);
            activitySceneLayout.getChildren().add(elevationLossLabel);
            activitySceneLayout.getChildren().add(avgSpeedLabel);
            activitySceneLayout.getChildren().add(timeElapsedLabel);

            if (!metricsDisplayed.get()) {
                metricsDisplayed.set(true);
            }

            showMapButton.setOnAction(event2 -> {
                MapVisualization.visualize(pathToGPXFile);
            });

            activitySceneLayout.getChildren().add(showMapButton);
        });

        /* Create Select activity label */
        Label selectActivityLabel = new Label("Your activities:");
        selectActivityLabel.setStyle("-fx-font-weight: bold");

        /* Create Current user label */
        Label currentUserLabel = new Label(currUser.getName());
        currentUserLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14");

        /* New activity button */
        Button newActivityButton = new Button("New activity");
        newActivityButton.setPrefWidth(90);
        newActivityButton.setLayoutX(540);
        newActivityButton.setLayoutY(15);
        newActivityButton.setOnAction(event -> {
            createNewActivityScene(stage);
            stage.setScene(scenes.top());
        });

        /* Back to user selection Button */
        Button changeUserButton = new Button("Change user");
        changeUserButton.setPrefWidth(90);
        changeUserButton.setLayoutX(540);
        changeUserButton.setLayoutY(290);
        changeUserButton.setOnAction(event -> {
            try {
                usersJSONArray = Utilities.getUsersJSONArray(DATA_DIR_PATH);
            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }
            createLoginScene(stage);
            stage.setScene(scenes.top());
        });

        /* Create Exit button */
        Button exitButton = createExitButton(stage);

        /* Position elements */
        currentUserLabel.setLayoutX(15);
        currentUserLabel.setLayoutY(0);

        selectActivityLabel.setLayoutX(15);
        selectActivityLabel.setLayoutY(14);

        activitesListView.setLayoutX(15);
        activitesListView.setLayoutY(30);
        activitesListView.setMaxHeight(315);

        /* Render all */
        activitySceneLayout.getChildren().add(selectActivityLabel);
        activitySceneLayout.getChildren().add(activitesListView);
        activitySceneLayout.getChildren().add(changeUserButton);
        activitySceneLayout.getChildren().add(exitButton);
        activitySceneLayout.getChildren().add(currentUserLabel);
        activitySceneLayout.getChildren().add(newActivityButton);

        scenes.push(activityScene);
    }

    /** Creates an "Login" scene. Specifies its layout then pushes it onto the stack in SceneLoader instance
     * @param stage main stage of the application.
     * */
    private void createLoginScene(Stage stage) {
        /* Second scene: login page */
        Pane loginSceneLayout = new Pane();
        Scene loginScene = new Scene(loginSceneLayout, APPLICATION_WIDTH, APPLICATION_HEIGHT);

        ArrayList<String> usernames = new ArrayList<>();
        for (Object user: usersJSONArray) {
            usernames.add(((JSONObject) user).get("name").toString());
        }

        /* Create ListView of usernames */
        ListView<String> usernamesListView = new ListView<>();
        usernamesListView.setPrefWidth(220);
        usernamesListView.getItems().addAll(usernames);
        usernamesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        usernamesListView.setOnMouseReleased(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                String selectedUsername = usernamesListView.getSelectionModel().getSelectedItem();

                // LOAD USER JSON INITIALISE THE USER OBJECT
                JSONObject userJSONObject = (JSONObject) usersJSONArray.get(usernamesListView.getSelectionModel().getSelectedIndex());
                String userName = userJSONObject.get("name").toString();
                int userWeight = Integer.parseInt(userJSONObject.get("weight").toString());
                int userHeight = Integer.parseInt(userJSONObject.get("height").toString());
                int userAge = Integer.parseInt(userJSONObject.get("age").toString());
                boolean userSex = (boolean) userJSONObject.get("sex");
                UUID userUUID = UUID.fromString(userJSONObject.get("uuid").toString());

                initCurrUser(userName,  userWeight, userHeight, userAge, userSex, userUUID);
                currUser.setName(selectedUsername);

                try {
                    createActivitiesScene(stage);
                } catch (ParseException | IOException e) {
                    e.printStackTrace();
                }
                stage.setScene(scenes.top());

            }
        });

        /* Create Select Usernames label */
        Label selectUsernameLabel = new Label("Select user profile:");
        selectUsernameLabel.setStyle("-fx-font-weight: bold");

        /* Create No Account hyperlink */
        Hyperlink noAccountHyperlink = new Hyperlink("Don't have an account?");
        noAccountHyperlink.setStyle("-fx-border-color: transparent; -fx-underline: false");
        noAccountHyperlink.setOnAction(event -> {
            createSignupScene(stage);
            stage.setScene(scenes.top());
        });

        /* Create a Back button */
        Button backButton = createBackButton(stage);

        /* Create Exit button */
        Button exitButton = createExitButton(stage);

        /* Position elements */
        selectUsernameLabel.setLayoutX(15);
        selectUsernameLabel.setLayoutY(14);

        usernamesListView.setLayoutX(15);
        usernamesListView.setLayoutY(30);
        usernamesListView.setMaxHeight(315);

        noAccountHyperlink.setLayoutX(500);
        noAccountHyperlink.setLayoutY(15);

        /* Render all */
        loginSceneLayout.getChildren().add(selectUsernameLabel);
        loginSceneLayout.getChildren().add(usernamesListView);
        loginSceneLayout.getChildren().add(noAccountHyperlink);
        loginSceneLayout.getChildren().add(backButton);
        loginSceneLayout.getChildren().add(exitButton);

        scenes.push(loginScene);
    }

    /** Creates an "Sign Up" scene. Specifies its layout then pushes it onto the stack in SceneLoader instance
     * @param stage main stage of the application.
     * */
    private void createSignupScene(Stage stage) {
        Pane signupSceneLayout = new Pane();
        Scene signupScene = new Scene(signupSceneLayout, APPLICATION_WIDTH, APPLICATION_HEIGHT);

        AtomicBoolean warningDisplayed = new AtomicBoolean(false);
        warningDisplayed.set(false);

        /* Create Username field */
        TextField usernameField = new TextField("Your username");
        usernameField.setFocusTraversable(false);
        usernameField.setAlignment(Pos.CENTER);
        usernameField.setOnMouseClicked(event -> {
            if (Objects.equals(usernameField.getText(), "Your username")) {
                usernameField.setText("");
            }
        });

        /* Create Sex selection field */
        ComboBox<String> sexField = new ComboBox<>();
        sexField.setPrefWidth(150);
        sexField.setPromptText("Sex");
        ArrayList<String> sexList = new ArrayList<>();
        sexList.add("Male");
        sexList.add("Female");

        sexField.getItems().addAll(sexList);

        /* Create Weight field */
        ComboBox<String> weightField = new ComboBox<>();
        weightField.setPrefWidth(150);
        weightField.setPromptText("Your weight (kg)");
        ArrayList<String> weightList = new ArrayList<>();
        for (int i = 10; i <= 300; i++) {
            weightList.add(String.valueOf(i));
        }

        weightField.getItems().addAll(weightList);

        /* Create Height field */
        ComboBox<String> heightField = new ComboBox<>();
        heightField.setPrefWidth(150);
        heightField.setPromptText("Your height (cm)");
        ArrayList<String> heightList = new ArrayList<>();
        for (int i = 50; i <= 300; i++) {
            heightList.add(String.valueOf(i));
        }

        heightField.getItems().addAll(heightList);

        /* Create Age field */
        ComboBox<String> ageField = new ComboBox<>();
        ageField.setPrefWidth(150);
        ageField.setPromptText("Age");
        ArrayList<String> ageList = new ArrayList<>();
        for (int i = 1; i <= 110; i++) {
            ageList.add(String.valueOf(i));
        }

        ageField.getItems().addAll(ageList);

        /* Create Submit button */
        Button submitButton = new Button("Submit");
        submitButton.setOnMouseClicked(event -> {
            Label wrongInputLabel = new Label();
            wrongInputLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10; -fx-text-fill: red");

            if (Utilities.isInvalidUserInput(usernameField.getCharacters().toString().strip(),
                                sexField.getSelectionModel().getSelectedItem(),
                                weightField.getSelectionModel().getSelectedItem(),
                                heightField.getSelectionModel().getSelectedItem(),
                                ageField.getSelectionModel().getSelectedItem()))
            {
                wrongInputLabel.setText("One or more inputs are invalid.");

                wrongInputLabel.setLayoutX(260);
                wrongInputLabel.setLayoutY(250);

                if (!warningDisplayed.get()) {
                    signupSceneLayout.getChildren().add(wrongInputLabel);
                    warningDisplayed.set(true);
                }
            } else if (Utilities.validateUsername(usernameField.getCharacters().toString().strip(), usersJSONArray)) {
                wrongInputLabel.setText("Such username already exist. Please enter another one.");
                wrongInputLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10; -fx-text-fill: red");

                wrongInputLabel.setLayoutX(205);
                wrongInputLabel.setLayoutY(250);

                if (!warningDisplayed.get()) {
                    signupSceneLayout.getChildren().add(wrongInputLabel);
                    warningDisplayed.set(true);
                }
            } else {
                // Get what user have entered and initialize current user
                String userName = usernameField.getCharacters().toString().strip();
                int userWeight = Integer.parseInt(weightField.getSelectionModel().getSelectedItem());
                int userHeight = Integer.parseInt(heightField.getSelectionModel().getSelectedItem());
                int userAge = Integer.parseInt(ageField.getSelectionModel().getSelectedItem());
                boolean userSex = Objects.equals(sexField.getSelectionModel().getSelectedItem(), "Female");

                initCurrUser(userName, userWeight, userHeight, userAge, userSex, UUID.randomUUID());

                // Create and populate user object then write it to users.json
                JSONObject userJSONObject = new JSONObject();
                userJSONObject.put("name",   userName);
                userJSONObject.put("uuid",   currUser.getUUID().toString());
                userJSONObject.put("weight", userWeight);
                userJSONObject.put("height", userHeight);
                userJSONObject.put("age", userAge);
                userJSONObject.put("sex",    userSex);

                try {
                    Utilities.writeUserJSONArray(DATA_DIR_PATH, userJSONObject);
                } catch (ParseException | IOException e) {
                    e.printStackTrace();
                }

                File activityJSONFile = new File(DATA_DIR_PATH + currUser.getUUID().toString() + ".json");

                try {
                    if (activityJSONFile.createNewFile()) {
                        // If the users.json file does NOT exist
                        FileWriter writer = new FileWriter(DATA_DIR_PATH + currUser.getUUID().toString() + ".json");
                        writer.write("[]");
                        writer.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    createActivitiesScene(stage);
                } catch (ParseException | IOException e) {
                    e.printStackTrace();
                }
                stage.setScene(scenes.top());
            }
        });

        /* Create a Back button */
        Button backButton = createBackButton(stage);

        /* Create Exit button */
        Button exitButton = createExitButton(stage);

        /* Position elements */
        usernameField.setLayoutX(250);
        usernameField.setLayoutY(50);

        sexField.setLayoutX(250);
        sexField.setLayoutY(80);

        weightField.setLayoutX(250);
        weightField.setLayoutY(110);

        heightField.setLayoutX(250);
        heightField.setLayoutY(140);

        ageField.setLayoutX(250);
        ageField.setLayoutY(170);

        submitButton.setLayoutX(300);
        submitButton.setLayoutY(220);

        /* Render all */
        signupSceneLayout.getChildren().add(usernameField);
        signupSceneLayout.getChildren().add(sexField);
        signupSceneLayout.getChildren().add(weightField);
        signupSceneLayout.getChildren().add(heightField);
        signupSceneLayout.getChildren().add(ageField);
        signupSceneLayout.getChildren().add(submitButton);
        signupSceneLayout.getChildren().add(backButton);
        signupSceneLayout.getChildren().add(exitButton);

        scenes.push(signupScene);
    }

    /** Creates an "Home" scene. Specifies its layout then pushes it onto the stack in SceneLoader instance
     * @param stage main stage of the application.
     * */
    private void createHomeScene(Stage stage) {
        /* Create home scene */
        VBox homeSceneLayout = new VBox(15);
        Scene homeScene = new Scene(homeSceneLayout, APPLICATION_WIDTH, APPLICATION_HEIGHT);
        homeSceneLayout.setAlignment(Pos.CENTER);

        /* Create a header with app name */
        Label nameHeader = new Label("GPXManager");
        nameHeader.setStyle("-fx-font-size: 40; -fx-font-weight: bold; -fx-font-family: 'Arial Black'");

        /* Create all buttons */
        Button loginButton = new Button("Log In");
        Button signupButton = new Button("Create Account");
        Button exitButton = createExitButton(stage);

        /* Create button handlers */
        loginButton.setOnAction(event -> {
            createLoginScene(stage);
            stage.setScene(scenes.top());
        });

        signupButton.setOnAction(event -> {
            createSignupScene(stage);
            stage.setScene(scenes.top());
        });

        /* Set button styles */
        signupButton.setPrefWidth(100);
        loginButton.setPrefWidth(80);
        exitButton.setPrefWidth(60);

        /* Render all the elements */
        homeSceneLayout.getChildren().add(nameHeader);
        homeSceneLayout.getChildren().add(signupButton);
        homeSceneLayout.getChildren().add(loginButton);
        homeSceneLayout.getChildren().add(exitButton);

        scenes.push(homeScene);
    }

    /** Sets initial configuration of the project*
     * @param stage main stage of the application.
     */
    private void configureProject(Stage stage) throws IOException, ParseException {
        /* Configure stage */
        stage.setTitle("GPXManager");
        stage.setResizable(false);

        /* Create data folder and create it */
        File dataDir = new File(DATA_DIR_PATH);

        if (!dataDir.exists()) {
            if (dataDir.mkdir()) {
                System.out.println("Successfuly created data folder.");
            } else {
                System.err.println("GPXManager: Could not create data folder.");
            }
        } else {
            System.out.println("Data folder already exists.");
        }

        /* Check for users.json file and create it*/
        File usersJSONFile = new File(DATA_DIR_PATH + USERS_FILENAME);

        if (usersJSONFile.createNewFile()) {
            // If the users.json file does NOT exist
            System.out.println("Successfuly created user.json.");
            FileWriter writer = new FileWriter(DATA_DIR_PATH + USERS_FILENAME);
            writer.write("[]");
            writer.close();
        } else {
            // If the users.json file already exists
            System.err.println("GPXManager: Could not create user.json: it already exist.");
        }

        /* Get the users from a users.json */
        usersJSONArray = Utilities.getUsersJSONArray(DATA_DIR_PATH);
    }

    @Override
    public void start(Stage stage) throws Exception {
        /* Configure application and launch it */
        configureProject(stage);
        stage.show();

        /* Create entry page */
        createHomeScene(stage);
        stage.setScene(scenes.top());
    }

    public static void main(String[] args) {
        launch(args);
    }
}