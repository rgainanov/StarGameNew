package ru.gb.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

import ru.gb.base.BaseScreen;
import ru.gb.math.Rect;
import ru.gb.pool.BulletPool;
import ru.gb.pool.EnemyPool;
import ru.gb.pool.ExplosionPool;
import ru.gb.sprites.Background;
import ru.gb.sprites.Bullet;
import ru.gb.sprites.EnemyShip;
import ru.gb.sprites.GameOver;
import ru.gb.sprites.MainShip;
import ru.gb.sprites.NewGameButton;
import ru.gb.sprites.Star;
import ru.gb.util.EnemyEmitter;

public class GameScreen extends BaseScreen {

    private static final int STAR_COUNT = 64;

    private Sound laserSound;
    private Sound bulletSound;
    private Sound explosionSound;
    private Music backgroundMusic;

    private TextureAtlas atlas;
    private TextureAtlas newExplosionAtlas;
    private Texture bg;
    private Background background;
    private GameOver gameOver;
    private NewGameButton newGameButton;

    private Star[] stars;
    private BulletPool bulletPool;
    private ExplosionPool explosionPool;
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
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("android/assets/sounds/explosion.wav"));

        bg = new Texture("textures/bg.png");
        background = new Background(bg);

        atlas = new TextureAtlas("textures/mainAtlas.tpack");
        newExplosionAtlas = new TextureAtlas("textures/explosionsAtlas.tpack");

        stars = new Star[STAR_COUNT];
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star(atlas);
        }

        bulletPool = new BulletPool();
        explosionPool = new ExplosionPool(newExplosionAtlas, explosionSound);
        enemyPool = new EnemyPool(bulletPool, explosionPool, worldBounds, bulletSound);

        mainShip = new MainShip(atlas, bulletPool, explosionPool, laserSound);

        enemyEmitter = new EnemyEmitter(enemyPool, atlas, worldBounds);

        gameOver = new GameOver(atlas);
        newGameButton = new NewGameButton(atlas, this);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        update(delta);
        checkCollisions();
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

        gameOver.resize(worldBounds);
        newGameButton.resize(worldBounds);
    }

    @Override
    public void dispose() {
        super.dispose();
        bg.dispose();
        atlas.dispose();
        bulletPool.dispose();
        laserSound.dispose();
        explosionSound.dispose();
        backgroundMusic.dispose();
        enemyPool.dispose();
        explosionPool.dispose();
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
        newGameButton.touchDown(touch, pointer, button);
        return super.touchDown(touch, pointer, button);
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer, int button) {
        mainShip.touchUp(touch, pointer, button);
        newGameButton.touchUp(touch, pointer, button);
        return super.touchUp(touch, pointer, button);
    }

    private void update(float delta) {
        for (Star star : stars) {
            star.update(delta);
        }
        if (!mainShip.isDestroyed()) {
            bulletPool.updateActiveObjects(delta);
            enemyPool.updateActiveObjects(delta);
            mainShip.update(delta);
            enemyEmitter.generate(delta);
        }
        explosionPool.updateActiveObjects(delta);
    }

    private void checkCollisions() {
        if (mainShip.isDestroyed()) {
            return;
        }
        List<EnemyShip> enemyShipList = enemyPool.getActiveObjects();
        for (EnemyShip enemyShip : enemyShipList) {
            float minDist = mainShip.getWidth();
            if (!enemyShip.isDestroyed() && mainShip.pos.dst(enemyShip.pos) < minDist) {
                enemyShip.destroy();
                mainShip.damage(enemyShip.getDamage() * 2);
            }
        }
        List<Bullet> bulletList = bulletPool.getActiveObjects();
        for (Bullet bullet : bulletList) {
            if (bullet.isDestroyed()) {
                continue;
            }
            if (bullet.getOwner() != mainShip) {
                if (mainShip.isBulletCollision(bullet)) {
                    mainShip.damage(bullet.getDamage());
                    bullet.destroy();
                }
                continue;
            }
            for (EnemyShip enemyShip : enemyShipList) {
                if (enemyShip.isBulletCollision(bullet)) {
                    enemyShip.damage(bullet.getDamage());
                    bullet.destroy();
                }
            }
        }
    }

    private void freeAllDestroyed() {
        bulletPool.freeAllDestroyed();
        explosionPool.freeAllDestroyed();
        enemyPool.freeAllDestroyed();
    }

    private void draw() {
        batch.begin();
        background.draw(batch);

        for (Star star : stars) {
            star.draw(batch);
        }
        if (!mainShip.isDestroyed()) {
            bulletPool.drawActiveObjects(batch);
            enemyPool.drawActiveObjects(batch);
            mainShip.draw(batch);
        } else {
            gameOver.draw(batch);
            newGameButton.draw(batch);
        }

        explosionPool.drawActiveObjects(batch);
        batch.end();
    }

    public void gameReset() {
        List<Bullet> bulletList = bulletPool.getActiveObjects();
        for (Bullet bullet : bulletList) {
            bullet.destroy();
        }
        List<EnemyShip> enemyShipList = enemyPool.getActiveObjects();
        for (EnemyShip enemyShip : enemyShipList) {
            enemyShip.destroyWithoutBoom();
        }
        mainShip.flushDestroyed();
    }
}
