package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Second;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.ImageSaveService;

import java.util.Timer;
import java.util.TimerTask;

public class ChartsController extends BasicSensorController {

    @FXML
    private ChartViewer chartView;

    private DynamicTimeSeriesCollection dataset;

    private XYPlot plot;

    private JFreeChart chart;

    private static final Logger logger = LoggerFactory.getLogger(ChartsController.class);

    private Timer autoSaveImageTimer = new Timer();

    private final long chartTimeMilliseconds = 10800;

    private boolean isTimerActive = false;

    private boolean locked = false;

    private ImageSaveService imageSaveService;

    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.CHARTS_CONTROLLER;
        dataset = new DynamicTimeSeriesCollection(1, 700, new Second());
        dataset.setTimeBase(new Second());
        dataset.addSeries(new float[1], 0, "Thrust");
        chart = ChartFactory.createTimeSeriesChart(
                "Engine Thrust", "Time", "Thrust", dataset, true, true, false);
        plot = chart.getXYPlot();
        chartView.setChart(chart);
    }

    @Override
    protected void setUIBySensors() {
        for (ISensor sensor : sensors) {
            if (sensor.getDestination().equals(chartView.getId())) {
              var rangeAxis = plot.getRangeAxis();
              rangeAxis.setRange(sensor.getMinRange(),sensor.getMaxRange());
              rangeAxis.setLabel(String.format("%s [%s]",sensor.getName(),sensor.getUnit()));
            } else {
                logger.error("Wrong UI binding - destination not found: {}",sensor.getDestination());
            }
        }
    }

    public void injectSaveService(ImageSaveService imageSaveService) {
        this.imageSaveService = imageSaveService;
    }

    @Override
    public void invalidated(Observable observable) {
        var value = (float)((ISensor) observable).getValue();

        if(!locked) {
            Platform.runLater(() -> {
                dataset.advanceTime();
                dataset.appendData(new float[]{value});
            });
            Platform.requestNextPulse();
        }

        if(Math.abs(10 - value) > 0 && !isTimerActive) {
            isTimerActive = true;
            autoSaveImageTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    locked = true;
                    imageSaveService.saveImage(chart.createBufferedImage(800,700));
                    locked = false;
                    isTimerActive = false;
                }
            }, chartTimeMilliseconds);
        }
    }
}