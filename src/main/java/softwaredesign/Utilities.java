package softwaredesign;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scijava.util.Types;

import java.io.*;
import java.net.URL;
import java.util.Objects;

import static org.scijava.util.FileUtils.urlToFile;

public class Utilities {
    private Utilities() {
        throw new IllegalStateException("Utility class.");
    }

    public static boolean isInvalidUserInput(String name, String weight, String height, String age, String sex) {
        return Objects.equals(name, "") || Objects.equals(name, "Your username") || sex == null || weight == null || height == null || age == null;
    }

    /** Checks if username is already present in the json file
     * @param name username to check
     * @return <strong>true</strong> if username is a duplicate <br>
     * <strong>false</strong> if username is NOT a duplicate
     * */
    public static boolean validateUsername(String name, JSONArray usersJSONArray) {
        for (Object o : usersJSONArray) {
            if (Objects.equals(((JSONObject) o).get("name"), name)) {
                // Username exists in DB, return true
                return true;
            }
        }
        // Username does NOT exist in DB, return false
        return false;
    }


    /** Helper function to get path to directory.
     * @returns If entry point of the application was in the JAR file, returns path to the directory containing this JAR file.
     * Otherwise, returns the directory containing the class file (entry point).
     * */
    public static String getDirPath() {
        URL urlToProject = Types.location(Types.load(Utilities.class.getName()));
        File file = urlToFile(urlToProject);

        return file.toString().substring(0, file.toString().lastIndexOf("\\") + 1);
    }

    /** Helper function to parse JSON file.
     * @param dirPath path to directory containing JSON file.
     * @param jsonFileName JSON file name.
     * @return JSONArray containing all JSONObject entries.
     * @throws ParseException Should not throw this ideally.
     * @throws IOException Should not throw this ideally.
     * */
    public static JSONArray getJSONArray(String dirPath, String jsonFileName) throws ParseException, IOException {
        FileReader jsonFile;
        try {
            jsonFile = new FileReader(dirPath + jsonFileName);
        } catch (FileNotFoundException e) {
            return null;
        }

        Object obj = new JSONParser().parse(jsonFile);

        return (JSONArray) obj;
    }

    /** Helper function to write JSON file.
     * @param dirPath path to directory containing JSON file.
     * @param jsonFileName JSON file name.
     * @param newJSONObject JSONObject to write
     * @throws ParseException Should not throw this ideally.
     * @throws IOException Should not throw this ideally.
     * */
    public static void writeJSONArray(String dirPath, String jsonFileName, JSONObject newJSONObject) throws ParseException, IOException {
        JSONArray arr = getJSONArray(dirPath, jsonFileName);

       arr.add(newJSONObject);

        FileWriter writer = new FileWriter(dirPath + jsonFileName);
        writer.write(arr.toJSONString());
        writer.close();
    }

    /** Helper function to read JSON file.
     * @param dirPath path to directory containing JSON file.
     * @return JSONArray containing all users JSONObject entries.
     * @throws ParseException Should not throw this ideally.
     * @throws IOException Should not throw this ideally.
     * */
    public static JSONArray getUsersJSONArray(String dirPath) throws ParseException, IOException {
        return getJSONArray(dirPath, "users.json");
    }

    /** Helper function to write JSON file.
     * @param dirPath path to directory containing JSON file.
     * @param newUserJSONObject JSONObject containing user's data
     * @throws ParseException Should not throw this ideally.
     * @throws IOException Should not throw this ideally.
     * */
    public static void writeUserJSONArray(String dirPath, JSONObject newUserJSONObject) throws ParseException, IOException {
        writeJSONArray(dirPath, "users.json", newUserJSONObject);
    }
}
