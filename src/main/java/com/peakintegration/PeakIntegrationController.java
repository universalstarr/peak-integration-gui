package com.peakintegration;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class PeakIntegrationController implements Observer {

    private Configurations config = Configurations.create();

    private PeakIntegrationModel model;

    private String lastPath;

    private double lineChartPos;
    @FXML
    private BorderPane borderPane1;

    // left side
    @FXML
    private Button openFilesButton;

    @FXML
    private ListView<String> filenamesListView;

    @FXML
    private BorderPane borderPane2;

    @FXML
    private Button plotButton;

    @FXML
    private Button integrateButton;

    @FXML
    private LineChart<Double, Double> lineChart;

    @FXML
    private Button setLeftButton;

    @FXML
    private Button setRightButton;

    @FXML
    private TextField leftTextField;

    @FXML
    private TextField rightTextField;

    @FXML
    private TableView tableView;
    @FXML
    private TableColumn<SpectrumIntegrationResult, String> tableColumnFilename;
    @FXML
    private TableColumn<SpectrumIntegrationResult, Double> tableColumnLower;
    @FXML
    private TableColumn<SpectrumIntegrationResult, Double> tableColumnUpper;
    @FXML
    private TableColumn<SpectrumIntegrationResult, Long> tableColumnSeconds;
    @FXML
    private TableColumn<SpectrumIntegrationResult, Double> tableColumnPeakArea;

    @FXML
    private Button saveAsButton;


    Alert alert = new Alert(Alert.AlertType.ERROR);

    @FXML
    protected void onOpenFilesButtonClick(){
        FileChooser fileChooser = new FileChooser();
        if(lastPath==null){
            lastPath= config.lastFileLocation;
        }
        if(lastPath!=null){
            fileChooser.setInitialDirectory(new File(lastPath).getParentFile());
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("sp files (*.sp)", "*.sp"));
        List<File> list = fileChooser.showOpenMultipleDialog(new Stage());
        if(list!=null){
            for(File f: list){
                filenamesListView.getItems().add(f.getPath());
                lastPath = f.getPath();
            }
            config.lastFileLocation=lastPath;
            Configurations.flush(config);
        }
    }

    @FXML
    protected void onPlotButtonClicked(){
        String filename = filenamesListView.getSelectionModel().getSelectedItem();
        if(filename==null || filename.isBlank()){
            alert.setContentText("Select a file");
            alert.show();
            return;
        }
        model.loadCurSpectrum(filename);
        Spectrum spectrum = model.getCurSpectrum();

        lineChart.getXAxis().setAutoRanging(false);
        ((ValueAxis<Double>)lineChart.getXAxis()).setLowerBound(spectrum.xs[0]);
        ((ValueAxis<Double>)lineChart.getXAxis()).setUpperBound(spectrum.xs[spectrum.xs.length-1]);
        lineChart.getYAxis().setAutoRanging(true);

        XYChart.Series<Double, Double> data = new XYChart.Series<>();
        for(int i=0; i<spectrum.xs.length; i++){
            data.getData().add(new XYChart.Data<>(spectrum.xs[i], spectrum.ys[i]));
        }
        while(lineChart.getData().size()<1) {
            lineChart.getData().add(new XYChart.Series<>());
        }
        lineChart.getData().set(0, data);
    }

    @FXML
    protected void onIntegrateButtonClick(){
//        System.out.println("onIntegrateButtonClick()");
        if(model.getIntegrationLeft()==null || model.getIntegrationRight()==null){
            alert.setContentText("lower and upper integration bound must be set");
            alert.show();
            return;
        }
        model.loadSpectra(filenamesListView.getItems());
        model.integrateAll();
    }

    @FXML
    protected void onLineChartMouseClicked(MouseEvent mouseEvent){
        System.out.printf("OnLineChartMouseClicked {x=%f, y=%f}\n", mouseEvent.getX(), mouseEvent.getY());
        lineChart.getXAxis().setAutoRanging(false);
        lineChart.getYAxis().setAutoRanging(false);
        Point2D mouseSceneCoords = new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY());
        double xPos = lineChart.getXAxis().getValueForDisplay(lineChart.getXAxis().sceneToLocal(mouseSceneCoords).getX());
        double yMin = ((ValueAxis<Double>)lineChart.getYAxis()).getLowerBound();
        double yMax = ((ValueAxis<Double>)lineChart.getYAxis()).getUpperBound();
        XYChart.Series<Double, Double> data = new XYChart.Series<>();
        data.getData().add(new XYChart.Data<>(xPos, yMin));
        data.getData().add(new XYChart.Data<>(xPos, yMax));
        while(lineChart.getData().size()<2) {
            lineChart.getData().add(new XYChart.Series<>());
        }

        lineChart.getData().set(1, data);

        lineChartPos = xPos;
    }

    @FXML
    protected void onSetLeftButtonClicked(){

        leftTextField.setText(String.valueOf(lineChartPos));
        setLeftBound(lineChartPos);

    }

    @FXML
    protected void onLeftTextFieldTextChange(){
        try{
            double value = Double.valueOf(leftTextField.getText());
            setLeftBound(value);
        } catch (Exception e){
            alert.setContentText("Requires a number");
            alert.show();
            leftTextField.setText(model.getIntegrationLeft()+"");
        }
    }

    private void setLeftBound(double leftBound){
        lineChart.getXAxis().setAutoRanging(false);
        lineChart.getYAxis().setAutoRanging(false);
        double yMin = ((ValueAxis<Double>)lineChart.getYAxis()).getLowerBound();
        double yMax = ((ValueAxis<Double>)lineChart.getYAxis()).getUpperBound();
        XYChart.Series<Double, Double> data = new XYChart.Series<>();
        data.getData().add(new XYChart.Data<>(leftBound, yMin));
        data.getData().add(new XYChart.Data<>(leftBound, yMax));
        while(lineChart.getData().size()<3) {
            lineChart.getData().add(new XYChart.Series<>());
        }

        lineChart.getData().set(2, data);

        model.setIntegrationLeft(leftBound);
    }
    @FXML
    protected void onSetRightButtonClicked(){
        rightTextField.setText(String.valueOf(lineChartPos));

        setRightBound(lineChartPos);
    }

    @FXML
    protected void onRightTextFieldTextChange(){
        try{
            double value = Double.valueOf(rightTextField.getText());
            setRightBound(value);
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Requires a number");
            alert.show();
            rightTextField.setText(model.getIntegrationRight()+"");
        }
    }
    private void setRightBound(double rightBound){
        lineChart.getXAxis().setAutoRanging(false);
        lineChart.getYAxis().setAutoRanging(false);
        double yMin = ((ValueAxis<Double>)lineChart.getYAxis()).getLowerBound();
        double yMax = ((ValueAxis<Double>)lineChart.getYAxis()).getUpperBound();
        XYChart.Series<Double, Double> data = new XYChart.Series<>();
        data.getData().add(new XYChart.Data<>(rightBound, yMin));
        data.getData().add(new XYChart.Data<>(rightBound, yMax));
        while(lineChart.getData().size()<4) {
            lineChart.getData().add(new XYChart.Series<>());
        }

        lineChart.getData().set(3, data);

        model.setIntegrationRight(rightBound);
    }

    public void onSaveAsButtonClicked(){
        FileChooser fileChooser = new FileChooser();
        if(lastPath==null){
            lastPath= config.lastFileLocation;
        }
        if(lastPath!=null){
            fileChooser.setInitialDirectory(new File(lastPath).getParentFile());
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("csv files (*.csv)", "*.csv"));
        File file = fileChooser.showSaveDialog(new Stage());
        try{
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            List<SpectrumIntegrationResult> results = model.getIntegrationResults();
            bufferedWriter.write("filename,lower,upper,seconds,area\n");
            for(var result: results){
                bufferedWriter.write(result.getFilename()+","+result.getLower()+","+result.getUpper()+","+result.getSeconds()+","+result.getArea()+"\n");
            }
            bufferedWriter.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        PeakIntegrationModel model = (PeakIntegrationModel) o;
        double startingSeconds = model.getStartingSeconds();
        List<SpectrumIntegrationResult> results = model.getIntegrationResults();
        int sz = results.size();
        javafx.application.Platform.runLater( () ->{
            tableView.getItems().addAll(results);
        });
    }

    public void setModel(Object model){
        this.model = (PeakIntegrationModel) model;
    }

    public void setUp(){
        tableColumnFilename.setCellValueFactory(new PropertyValueFactory<>("filename"));
        tableColumnLower.setCellValueFactory(new PropertyValueFactory<>("lower"));
        tableColumnUpper.setCellValueFactory(new PropertyValueFactory<>("upper"));
        tableColumnSeconds.setCellValueFactory(new PropertyValueFactory<>("seconds"));
        tableColumnPeakArea.setCellValueFactory(new PropertyValueFactory<>("area"));
    }
}