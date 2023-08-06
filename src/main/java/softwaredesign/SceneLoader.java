package softwaredesign;

import javafx.scene.Scene;
import java.util.ArrayList;

public class SceneLoader {
    private SceneLoader() {}

    private static ArrayList<Scene> stack;
    private static SceneLoader sceneLoaderInstance;

    /** Initializes the stack if it has not been initialized before.
     *  @return  an object of singleton class SceneLoader
     * */
    public static SceneLoader getInstance() {
        if (stack == null && sceneLoaderInstance == null) {
            sceneLoaderInstance = new SceneLoader();
            stack = new ArrayList<>();
        }

        return sceneLoaderInstance;
    }

    /** Pushes the scene to the stack. */
    public void push(Scene scene) { stack.add(scene); }

    /** Pops the scene off the stack
     * @return scene removed from the stack
     * */
    public Scene pop() { return stack.remove(stack.size() - 1); }

    /** Gets the latest added scene off the stack
     *  Does not remove it off the stack. Check SceneLoader.pop() instead.
     * */
    public Scene top() { return stack.get(stack.size() - 1); }

    /** Destroys an instance of SceneLoader */
    public static void killInstance() {
        stack = null;
        sceneLoaderInstance = null;
    }
}
