package com.badlogic.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;


import java.util.Iterator;

public class Drop extends ApplicationAdapter {
	private Texture dropImage;
	private Texture bucketImage;
	private Music rain;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private long lastDropTime;



	private void spawnRaindrop(){
		Rectangle rdrop = new Rectangle();
		rdrop.x = MathUtils.random(0,800-64);
		rdrop.y = 480;
		rdrop.width = 64;
		rdrop.height = 64;
		raindrops.add(rdrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void create () {
		dropImage = new Texture(Gdx.files.internal("drop.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

		rain = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		rain.setVolume(.25f);
		rain.setLooping(true);
		rain.play();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		bucket = new Rectangle();
		bucket.x = (800/2) - (64/2);
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;

		raindrops = new Array<Rectangle>();
		spawnRaindrop();

	}

	@Override
	public void render () {
		ScreenUtils.clear(0,0,0.2f,1);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(bucketImage,bucket.x,bucket.y);


		if(Gdx.input.isTouched()){
			Vector3 touchpos = new Vector3();
			touchpos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchpos);
			bucket.x = touchpos.x - 64/2;
		}

		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();

		if(bucket.x < 0) bucket.x = 0;
		if(bucket.x > 800-64) bucket.x = 800 - 64;
		if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();
		for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if(raindrop.y + 64 < 0) iter.remove();
			if(raindrop.overlaps(bucket)){
				iter.remove();
			}
		}
		batch.end();
		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);
		for(Rectangle raindrop: raindrops) {

			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		batch.end();



	}
	
	@Override
	public void dispose () {
		dropImage.dispose();
		bucketImage.dispose();
		rain.dispose();
		batch.dispose();
	}
}
