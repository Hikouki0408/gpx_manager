package softwaredesign;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GPXReader;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;
import java.util.List;

public class MapVisualization extends PApplet {
    UnfoldingMap map;
    static String gpxFilePath;

    public static void visualize(String pathToGPXFile) {
        gpxFilePath = pathToGPXFile;
        main(new String[0]);
    }

    public static void main(String[] args) {
        PApplet.main(new String[] { MapVisualization.class.getName() });
    };

    @Override
    public void setup() {
        /* Configure window */
        size(800, 600, JAVA2D);
        frame.setTitle("Visualized activity");

        /* Configure map to display */
        map = new UnfoldingMap(this, new Microsoft.RoadProvider());
        MapUtils.createDefaultEventDispatcher(this, map);

        /* Load data */
        List<Feature> features = GPXReader.loadData(this, gpxFilePath);
        List<Marker> markers = MapUtils.createSimpleMarkers(features);

        /* Center the map on the route */
        Location centerMapLocation = markers.get(0).getLocation();
        map.zoomAndPanTo(8, centerMapLocation);

        MultiMarker route = new MultiMarker();
        route.setMarkers(markers);
        route.setColor(color(230, 0, 0));
        route.setStrokeWeight(4);
        map.addMarkers(route);

        /* Piece of code to disable closing of the application when closing the map window.
         * Has to do with overwriting methods exit() and exitActual(), which doesnt work as well ¯\_(ツ)_/¯
         * Doesn't work for some reason, therefore commented out */
//        ((JFrame) frame).setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//        WindowListener[] wls = frame.getWindowListeners();
//        for (int i = 0; i < wls.length; i++) {
//            frame.removeWindowListener(wls[i]);
//        }
//        frame.addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent we) {
//                dispose();
//            }
//        });
    }

    @Override
    public void draw() {
        map.draw();
    }
}
