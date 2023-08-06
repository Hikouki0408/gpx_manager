package softwaredesign;

/** Supplementary launcher class to bypass JavaFX modules being loaded from maven repository. */
public class Main {
    public static void main (String[] args){
        AppUI.main(args);
    }
}
