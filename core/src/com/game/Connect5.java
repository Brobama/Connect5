package com.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

public class Connect5 extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
    ShapeRenderer painter;
	Texture img;

	private Camera camera;
	Texture bgTexture;

    Sprite bgSprite;

    Vector2 touchDownPosition = new Vector2();
	Vector2 touchUpPosition = new Vector2();

    Vector2 inputRatio = new Vector2();
    Vector2 logicScreenSize = new Vector2();

    Cell leftPanel;
    Cell rightPanel;

    ChessBoard board;

	Button confirm;
	RestartButton restartButton;

	Music bgMusic;

	@Override
	public void create () {
		camera = new OrthographicCamera(1024, 600);
        logicScreenSize.set(1024f, 600f);
		camera.position.set(camera.viewportWidth/2f,
				camera.viewportHeight/2f, 0);
		camera.update();

        leftPanel = new Cell(4, Color.RED);
        rightPanel = new Cell(4, Color.BLUE);
		leftPanel.setCell(2, 2, 598, 598);
        rightPanel.setCell(602, 2, 1022, 598);
        board  = new ChessBoard(this);

		//viewport = new FitViewport(800, 480, camera);

		batch = new SpriteBatch();
        painter = new ShapeRenderer();
		img = new Texture("badlogic.jpg");
		bgTexture = new Texture("background.png");
		bgSprite = new Sprite(bgTexture);
		//bgSprite.setPosition(camera.viewportWidth/2f, camera.viewportHeight/2f);
		bgSprite.setCenter(camera.viewportWidth/2f, camera.viewportHeight/2f);
		bgSprite.scale(2f);

		confirm = new Button(batch, board);
		confirm.setPosition(680,30);

		restartButton = new RestartButton(batch, board);
		restartButton.setPosition(890, 530);

        Gdx.input.setInputProcessor(this);
        Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);
        Gdx.app.debug("Connect 5", "screen width = "+Gdx.graphics.getWidth()
                + "screen height = " + Gdx.graphics.getHeight());
        inputRatio.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        inputRatio.set(logicScreenSize.x/ inputRatio.x, logicScreenSize.y/inputRatio.y);

		bgMusic = Gdx.audio.newMusic(Gdx.files.internal("stone_bg.mp3"));
		bgMusic.setLooping(true);
		bgMusic.setVolume(0.3f);
		bgMusic.play();
	}

	public void update(){
		bgSprite.rotate(1f);
        board.update();
	}

	public void changeBackgroundMusicVolume(float seconds, final float volume){
		if(seconds == 0.0f){
			bgMusic.setVolume(volume);
			return;
		}
		Timer.schedule(new Timer.Task(){
			public void run(){
				bgMusic.setVolume(volume);
			}
		}, seconds);
	}

	@Override
	public void render () {

		update();


		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		bgSprite.draw(batch);
		//batch.draw(img, 0, 0);
		confirm.draw();
		restartButton.draw();

		batch.end();

        painter.setProjectionMatrix(camera.combined);
        painter.begin(ShapeRenderer.ShapeType.Filled);

        leftPanel.render(painter);
        rightPanel.render(painter);
        board.render(painter);

        painter.end();

		batch.begin();
		board.drawStones(batch);
		batch.end();

		Gdx.gl.glEnable(GL20.GL_BLEND);
		painter.begin(ShapeRenderer.ShapeType.Filled);
		board.drawHighlight(painter);
		painter.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);


	}

	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		bgTexture.dispose();
		confirm.dispose();
		board.dispose();
		restartButton.dispose();
		bgMusic.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);
		/*
        Gdx.app.debug("Connect 5", "Touch down position x="+screenX * inputRatio.x+", y="
                +
                (logicScreenSize.y - screenY*inputRatio.y));*/

		touchDownPosition.x = (float)(screenX * inputRatio.x);
		touchDownPosition.y = (float)(logicScreenSize.y - screenY * inputRatio.y);
		confirm.touchDown(touchDownPosition.x, touchDownPosition. y);
		restartButton.touchDown(touchDownPosition.x, touchDownPosition.y);
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        //Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);
        /*Gdx.app.debug("Connect 5", "Touch up position x="+screenX*inputRatio.x+", y="+
                (logicScreenSize.y - screenY*inputRatio.y));*/
		touchUpPosition.x = (float)(screenX * inputRatio.x);
		touchUpPosition.y = (float)(logicScreenSize.y - screenY * inputRatio.y);
		confirm.touchUp(touchUpPosition.x, touchUpPosition.y);
		restartButton.touchUp(touchUpPosition.x, touchUpPosition.y);
		//calculate distance
		float offsetX = touchUpPosition.x - touchDownPosition.x;
		float offsetY = touchUpPosition.y - touchDownPosition.y;
		float distance = (float) Math.sqrt(offsetX * offsetX + offsetY * offsetY);
		if(distance < 10) // avoid accident touch
			return true;

		//unit offset
		offsetX /= distance;
		offsetY /= distance;

		//get direction
		double v = Math.sqrt(2)/ 2.0;
		if(offsetY >  0 && Math.abs(offsetX) <= v ) {
			board.moveUp();
		}
		else if(offsetY < 0 && Math.abs(offsetX) <= v ) {
			board.moveDown();
		}
		else if( offsetX > 0 && Math.abs(offsetY) <= v ) {
			board.moveRight();
		}
		else if( offsetX < 0 && Math.abs(offsetY) <= v) {
			board.moveLeft();
		}

		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
