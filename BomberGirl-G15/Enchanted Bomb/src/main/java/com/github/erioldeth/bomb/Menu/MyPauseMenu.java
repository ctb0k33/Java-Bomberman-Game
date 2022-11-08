package com.github.erioldeth.bomb.Menu;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.core.util.EmptyRunnable;
import com.almasb.fxgl.dsl.FXGL;
import com.github.erioldeth.bomb.Sound;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getDialogService;
import static javafx.scene.paint.Color.YELLOW;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */


public class MyPauseMenu
		extends FXGLMenu {
	
	private Animation<?> animation;
	
	public MyPauseMenu() {
		super(MenuType.GAME_MENU);
		Rectangle border = new Rectangle(170, 100);
		border.setTranslateX(FXGL.getAppWidth() / 2.0 - 75);
		border.setTranslateY(FXGL.getAppHeight() / 2.0 - 100);
		border.setFill(YELLOW);
		
		Text text = new Text("GAME IS PAUSED");
		text.setTranslateX(FXGL.getAppWidth() / 2.0 - 35);
		text.setTranslateY(FXGL.getAppHeight() / 2.0 - 80);
		
		var pane = new GridPane();
		pane.setHgap(0);
		pane.setVgap(5);
		
		Line line1 = new Line();
		line1.setTranslateY(FXGL.getAppHeight() / 2.0 - 100);
		line1.setStartX(FXGL.getAppWidth() / 2.0 - 75);
		line1.setEndX(FXGL.getAppWidth() / 2.0 + 95);
		
		Line line2 = new Line();
		line2.setTranslateX(FXGL.getAppWidth() / 2.0 - 75);
		line2.setStartY(FXGL.getAppHeight() / 2.0 - 100);
		line2.setEndY(FXGL.getAppHeight() / 2.0);
		
		Line line3 = new Line();
		line3.setTranslateX(FXGL.getAppWidth() / 2.0 + 95);
		line3.setStartY(FXGL.getAppHeight() / 2.0 - 100);
		line3.setEndY(FXGL.getAppHeight() / 2.0);
		
		Line line4 = new Line();
		line4.setTranslateY(FXGL.getAppHeight() / 2.0);
		line4.setStartX(FXGL.getAppWidth() / 2.0 - 75);
		line4.setEndX(FXGL.getAppWidth() / 2.0 + 95);
		
		Button resume = new Button();
		resume.setText("Resume");
		resume.setMinSize(150, 30);
		resume.setOnAction(e -> fireResume());
		Button back = new Button();
		back.setText("Back");
		back.setMinSize(150, 30);
		back.setOnAction(e -> {
			getDialogService().showConfirmationBox("Back To Main Menu. Agree?", answer -> {
				if(answer) {
					var gameController = FXGL.getGameController();
					gameController.gotoMainMenu();
					Sound.loop.stop();
					ChronMainMenu.start();
				}
			});
			//            ChronMainMenu.start();
		});
		pane.addRow(1, resume);
		pane.addRow(2, back);
		pane.setTranslateX(FXGL.getAppHeight() / 2.0 + 135);
		pane.setTranslateY(FXGL.getAppHeight() / 2.0 - 80);
		getContentRoot().getChildren().addAll(border, pane, text, line1, line2, line3, line4);
		
		getContentRoot().setScaleX(0);
		getContentRoot().setScaleY(0);
		
		animation = FXGL.animationBuilder()
		                .duration(Duration.seconds(0.66))
		                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
		                .scale(getContentRoot())
		                .from(new Point2D(0, 0))
		                .to(new Point2D(1, 1))
		                .build();
	}
	
	@Override
	public void onCreate() {
		animation.setOnFinished(EmptyRunnable.INSTANCE);
		animation.stop();
		animation.start();
	}
	
	@Override
	protected void onUpdate(double tpf) {
		animation.onUpdate(tpf);
	}
	
}