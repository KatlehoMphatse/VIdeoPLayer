package com.example.videoplayer;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class HelloController implements Initializable {

    @FXML
    public Button play,pause, Stop;
    @FXML
    private String path;
    @FXML
    private MediaView mediaview;
    @FXML
    private MediaPlayer mediaplayer;
    @FXML
    private Slider progressBar;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Label timeLabel;
    @FXML
    private Label totalTime;

    @FXML
    private void onClickAddFile(ActionEvent event){
        try{
            mediaplayer.stop();

            mediaplayer.dispose();
        }catch(Exception ignored){

        }

        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        path = file.toURI().toString();



        if(path != null){


            Media media = new Media(path);
            mediaplayer= new MediaPlayer(media);
            mediaview.setMediaPlayer(mediaplayer);

            DoubleProperty widthProp = mediaview.fitWidthProperty();
            DoubleProperty heightProp = mediaview.fitHeightProperty();

            widthProp.bind(Bindings.selectDouble(mediaview.sceneProperty(),"width"));
            heightProp.bind(Bindings.selectDouble(mediaview.sceneProperty(),"height"));

            mediaplayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                progressBar.setValue(newValue.toSeconds());
                }
            });


            progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    mediaplayer.seek(Duration.seconds(progressBar.getValue()));

                }
            });

            progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    mediaplayer.seek(Duration.seconds(progressBar.getValue()));

                }
            });
            mediaplayer.setOnReady(new Runnable() {
                @Override
                public void run() {
                    Duration total = media.getDuration();
                    progressBar.setMax(total.toSeconds());
                }
            });
            volumeSlider.setValue(mediaplayer.getVolume() * 100);
            volumeSlider.valueProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    mediaplayer.setVolume(volumeSlider.getValue()/100);
                }
            });

            mediaplayer.play();

        }



    }

    @FXML
    public void onClickPlay(ActionEvent event){
        mediaplayer.play();
    }

    @FXML
    public void onClickPause(ActionEvent event){
        mediaplayer.pause();
    }

    @FXML
    public void onClickStop(){
        mediaplayer.stop();
    }




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            mediaplayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                Duration currentTime = mediaplayer.getCurrentTime();
                Duration duration = mediaplayer.getMedia().getDuration();

                String currentTimeStr = formatDuration(currentTime);
                String durationStr = formatDuration(duration);

                timeLabel.setText(currentTimeStr + " / " + durationStr);
            });
        }catch (Exception ignored){

        }
    }

    private String formatDuration(Duration duration) {
        int intDuration = (int) Math.floor(duration.toSeconds());
        int hours = intDuration / 3600;
        int minutes = (intDuration - hours * 3600) / 60;
        int seconds = intDuration - hours * 3600 - minutes * 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}