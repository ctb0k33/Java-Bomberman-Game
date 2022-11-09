package com.github.erioldeth.bomb.Menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.core.Updatable;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.view.KeyView;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;
import static javafx.scene.input.KeyCode.*;

public class ChronMainMenu extends FXGLMenu {

    private ObjectProperty<ChoronButton> selectedButton;
    public static MediaPlayer mediaPlayer;

    public static Media media;

    private double volume;

    public ChronMainMenu(){
        super(MenuType.MAIN_MENU);
        // Button
        ChoronButton btnPlaygame  = new ChoronButton("Play Game", "Start new game",() -> {
            fireNewGame();
        });
        ChoronButton btnOptions  = new ChoronButton("Options", "Adjust in-game options",() -> {
            changeSound();
        });
        ChoronButton btnQuit  = new ChoronButton("Quit Game","Exit to desktop", () -> fireExit());

        selectedButton =  new SimpleObjectProperty<>(btnPlaygame);

        var textDescription = getUIFactoryService().newText("");
        textDescription.textProperty().bind(Bindings.createStringBinding(()->selectedButton.get().description,selectedButton));
        // box
        var vbox = new VBox(15,btnPlaygame,
                btnOptions,
                new ChoronButton("How to play","How to play", ()->{
                    instructions();
                }),
                new ChoronButton("About us","About us",()->{
                    AboutUs();
                }),
                btnQuit,
                new Text(""),
                new LineSeparator(), // draw line
                textDescription
        );
        vbox.setAlignment(Pos.CENTER_LEFT);
        vbox.setTranslateX(100);
        vbox.setTranslateY(500);

        var view = new KeyView(KeyCode.ESCAPE,Color.GREEN,23.0);

        var hBox = new HBox(15, getUIFactoryService().newText("Back",23.0),view);
        hBox.setAlignment(Pos.BOTTOM_CENTER);
        hBox.setTranslateX(getAppWidth()-150);
        hBox.setTranslateY(720);

        // add item
        getContentRoot().getChildren().addAll(createBackground(1280,720),vbox,hBox);
    }

    // instruction
    private void instructions() {
        var pane = new GridPane();
        pane.setHgap(25);
        pane.setVgap(10);
        pane.addRow(0, getUIFactoryService().newText("CONTROL"),
                new HBox(4, new KeyView(UP), new KeyView(DOWN), new KeyView(LEFT), new KeyView(RIGHT)));
        pane.addRow(1, getUIFactoryService().newText("PLACE BOMB"),
                new KeyView(SPACE));
        pane.addRow(2, getUIFactoryService().newText("OPEN MENU GAME"),
                new KeyView(ESCAPE));
        FXGL.getDialogService().showBox("HOW TO PLAY", pane, getUIFactoryService().newButton("OK"));
    }

    private void changeSound(){
        Slider slider = new Slider(0,100,50);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(10);
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                volume= slider.getValue();
                mediaPlayer.setVolume(volume/100);
            }
        });
        FXGL.getDialogService().showBox("Change Sound",slider,getUIFactoryService().newButton("OK"));
    }
    public static void start(){
//
        media = new Media(new File("BomberGirl-G15/Enchanted Bomb/src/main/resources/assets/sounds/KleeTheme.wav").toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.3);
        mediaPlayer.play();
    }
    public static void stop(){
        mediaPlayer.stop();
    }
    private void AboutUs(){
        var pane = new GridPane();
        pane.setHgap(25);
        pane.setVgap(10);
        pane.addRow(0,getUIFactoryService().newText("Trần Chiến Thắng - 21020246."));
        pane.addRow(1,getUIFactoryService().newText("Mạc Gia Khánh - 21020641."));
        pane.addRow(2,getUIFactoryService().newText("Phạm Minh Hiếu - 21020264."));
        pane.setAlignment(Pos.CENTER);
        FXGL.getDialogService().showBox("ABOUT US", pane, getUIFactoryService().newButton("OK"));
    }
    protected Button createActionButton(StringBinding stringBinding, Runnable runnable){
        return new Button();
    }
    protected Button createActionButton(String s, Runnable runnable){
        return new Button();
    }

    protected Node createBackground(double v , double v1){
        return FXGL.texture(  "background/KLEE.jpg");
    }

    protected Node createProfileview(String s){

        return new Rectangle();
    }

    protected Node createTitleview(String s){
        return new Rectangle();
    }

    protected Node createversionview(String s){
        return new Rectangle();
    }

    @Override
    public void onCreate() {
        FXGL.getGameScene().addUINode(createBackground(1200,720));
    }

    @Override
    public void addListener(@NotNull Updatable l) {
        super.addListener(l);
    }

    private static final Color SELECTED_COLOR = Color.AQUA;
    private static final Color NOT_SELECTED_COLOR = Color.ALICEBLUE;

    private class ChoronButton extends StackPane {

        private String name;
        private String description;
        private Runnable action;

        private Text text;
        private Rectangle selector;


        public ChoronButton(String name, String description, Runnable action) {
            this.name= name;
            this.description = description;
            this.action = action;
            // text
            text= getUIFactoryService().newText(name, Color.BLACK, 18);

            //added code
            text.fillProperty().bind(
                    Bindings.when(hoverProperty())
                            .then(SELECTED_COLOR)
                            .otherwise(NOT_SELECTED_COLOR)
            );
            text.strokeProperty().bind(
                    Bindings.when(hoverProperty() )
                            .then(SELECTED_COLOR)
                            .otherwise(NOT_SELECTED_COLOR)
            );
            text.setStrokeWidth(0.5);

            // selector
            selector = new Rectangle(6,17,Color.AQUA);
            selector.setTranslateX(-20);
            selector.setTranslateY(-2);
            selector.visibleProperty().bind(focusedProperty());
            selector.visibleProperty().bind(hoverProperty());


            setPickOnBounds(true);

            hoverProperty().addListener((observable,oldValue,isSelected) ->{
                if(isSelected){
                    selectedButton.setValue(this);
                }
            });

            setAlignment(Pos.CENTER_LEFT);
            setFocusTraversable(true);

            setOnMouseClicked(e -> action.run());

            setOnKeyPressed(e->{
                if(e.getCode() == KeyCode.ENTER){
                    action.run();
                }
            });

            getChildren().addAll(selector,text);
        }
    }
    private static class LineSeparator extends Parent{
        private Rectangle line = new Rectangle(400,3);
        public LineSeparator(){
            var gradient = new LinearGradient(0,0.5,1,0.5,true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.WHITE),
                    new Stop(0.5, Color.GRAY),
                    new Stop(1.0,Color.TRANSPARENT));
            line.setFill(gradient);

            getChildren().add(line);
        }
    }
}

