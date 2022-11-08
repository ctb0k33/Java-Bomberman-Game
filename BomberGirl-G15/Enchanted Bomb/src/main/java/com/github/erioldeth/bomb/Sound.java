package com.github.erioldeth.bomb;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class Sound {
    public static MediaPlayer mediaPlayer;
    public static MediaPlayer loop;

    public static void Loop(String filePath) {
        Media media = new Media(new File(filePath).toURI().toString());
        loop = new MediaPlayer(media);
        loop.setAutoPlay(true);
        loop.setCycleCount(MediaPlayer.INDEFINITE);
        loop.setVolume(0.3);
        loop.play();
    }

    public static void play(String filePath) {
        Media media = new Media(new File(filePath).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.3);
        mediaPlayer.play();
    }
}
