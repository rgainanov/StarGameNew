package ru.gb.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

import ru.gb.base.BaseScreen;
import ru.gb.math.Rect;
import ru.gb.pool.BulletPool;
import ru.gb.pool.EnemyPool;
import ru.gb.sprites.Background;
import ru.gb.sprites.Bullet;
import ru.gb.sprites.EnemyShip;
import ru.gb.sprites.MainShip;
import ru.gb.sprites.Star;
import ru.gb.util.EnemyEmitter;

public class GameScreen extends BaseScreen {

    private static final int STAR_COUNT = 64;

    private Sound laserSound;
    private Sound bulletSound;
    private Music backgroundMusic;

    private TextureAtlas atlas;
    private Texture bg;
    private Background background;

    private Star[] stars;
    private BulletPool bulletPool;
    private EnemyPool enemyPool;

    private MainShip mainShip;

    private EnemyEmitter enemyEmitter;

    @Override
    public void show() {
        super.show();
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("android/assets/sounds/music.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.05f);
        backgroundMusic.play();

        laserSound = Gdx.audio.newSound(Gdx.files.internal("android/assets/sounds/laser.wav"));
        bulletSound = Gdx.audio.newSound(Gdx.files.internal("android/assets/sounds/bullet.wav"));

        bg = new Texture("textures/bg.png");
        background = new Background(bg);

        atlas = new TextureAtlas("textures/mainAtlas.tpack");

        stars = new Star[STAR_COUNT];
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star(atlas);
        }

        bulletPool = new BulletPool();
        enemyPool = new EnemyPool(bulletPool, worldBounds, bulletSound);

        mainShip = new MainShip(atlas, bulletPool, laserSound);

        enemyEmitter = new EnemyEmitter(enemyPool, atlas, worldBounds);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        update(delta);
        freeAllDestroyed();
        draw();
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        background.resize(worldBounds);
        mainShip.resize(worldBounds);

        for (Star star : stars) {
            star.resize(worldBounds);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        bg.dispose();
        atlas.dispose();
        bulletPool.dispose();
        laserSound.dispose();
        backgroundMusic.dispose();
        enemyPool.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        mainShip.keyDown(keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        mainShip.keyUp(keycode);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button) {
        mainShip.touchDown(touch, pointer, button);
        return super.touchDown(touch, pointer, button);
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer, int button) {
        mainShip.touchUp(touch, pointer, button);
        return super.touchUp(touch, pointer, button);
    }

    private void update(float delta) {
        for (Star star : stars) {
            star.update(delta);
        }
        bulletPool.updateActiveObjects(delta);
        enemyPool.updateActiveObjects(delta);
        mainShip.update(delta);
        enemyEmitter.generate(delta);
        checkIfHit();

    }

    private void checkIfHit() {
        for (EnemyShip es: enemyPool.getActiveObjects()) {
            for (Bullet b: bulletPool.getActiveObjects()) {
                if (!es.isOutside(b) && b.getOwner().equals(mainShip)) {
                    b.destroy();
                    es.setHp(es.getHp() - 1);
                }
            }
        }
    }

    private void freeAllDestroyed() {
        bulletPool.freeAllDestroyed();
        enemyPool.freeAllDestroyed();
    }

    private void draw() {
        batch.begin();
        background.draw(batch);

        for (Star star : stars) {
            star.draw(batch);
        }

        bulletPool.drawActiveObjects(batch);
        enemyPool.drawActiveObjects(batch);
        mainShip.draw(batch);

        batch.end();
    }
}
