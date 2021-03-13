package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.offline.OfflineCache;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.IGPSSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class NewMapController extends BasicController implements InvalidationListener {

    private static final Logger logger = LoggerFactory.getLogger(NewMapController.class);

    private static final int ZOOM_DEFAULT = 60;

    private static final double MAP_START_CORRECTION = 0.017;

    @FXML
    private MapView mapView;

    @FXML
    public TextField currentLocation;

    @FXML
    private Label currentDistance;

    private CoordinateLine track;


    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.MAP_CONTROLLER;
    }

    public void initMapAndControls(Projection projection) {

        // init MapView-Cache
        final OfflineCache offlineCache = mapView.getOfflineCache();
        final String cacheDir = System.getProperty("java.io.tmpdir") + "/mapjfx-cache";
        logger.info("using dir for cache: {}", cacheDir);
        try {
            Files.createDirectories(Paths.get(cacheDir));
            offlineCache.setCacheDirectory(cacheDir);
            offlineCache.setActive(true);
        } catch (IOException e) {
            logger.warn("could not activate offline cache", e);
        }

        // watch the MapView's initialized property to finish initialization
        mapView.initializedProperty().addListener((observable, oldValue, newValue) -> {
            if (Boolean.TRUE.equals(newValue)) {
                afterMapIsInitialized();
            }
        });

        mapView.setMapType(MapType.OSM);

        // finally initialize the map view
        mapView.initialize(Configuration.builder()
                .projection(projection)
                .showZoomControls(false)
                .build());
    }

    private void afterMapIsInitialized() {
        mapView.setZoom(ZOOM_DEFAULT);
        Coordinate startPosition = new Coordinate(pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration.getInstance().START_POSITION_LAT+MAP_START_CORRECTION, pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration.getInstance().START_POSITION_LON-MAP_START_CORRECTION);
        mapView.setCenter(startPosition);
    }

    @Override
    public void invalidated(Observable observable) {
        UIThreadManager.getInstance().addImmediate(() -> {
            Coordinate nextCoordinate = new Coordinate(((IGPSSensor) observable).getPosition().get(IGPSSensor.LATITUDE_KEY), ((IGPSSensor) observable).getPosition().get(IGPSSensor.LONGITUDE_KEY));
            currentLocation.setText(nextCoordinate.getLatitude() + ";" + nextCoordinate.getLongitude());
            currentDistance.setText("Distance: " + distance(((IGPSSensor) observable).getPosition().get(IGPSSensor.LATITUDE_KEY), ((IGPSSensor) observable).getPosition().get(IGPSSensor.LONGITUDE_KEY)) + "m");
            final List<Coordinate> coordinates = new ArrayList<>();
            if (track != null) {
                track.setVisible(false);
                track.getCoordinateStream().forEach(coordinates::add);
                mapView.removeCoordinateLine(track);
                track = null;
            }
            coordinates.add(nextCoordinate);
            track = new CoordinateLine(coordinates)
                    .setColor(Color.DODGERBLUE)
                    .setWidth(7)
                    .setClosed(false);
            mapView.addCoordinateLine(track);
            track.setVisible(true);
        });
    }

    public static int distance(double currentLat, double currentLon) {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        currentLon = Math.toRadians(currentLon);
        double lon2 = Math.toRadians(pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration.getInstance().START_POSITION_LON);
        currentLat = Math.toRadians(currentLat);
        double lat2 = Math.toRadians(pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration.getInstance().START_POSITION_LAT);

        // Haversine formula
        double dlon = lon2 - currentLon;
        double dlat = lat2 - currentLat;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(currentLat) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result in meters
        return (int) ((c * r) * 1000);
    }
}
