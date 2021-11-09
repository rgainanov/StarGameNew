package ru.gb.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import java.util.List;

import ru.gb.base.BaseScreen;
import ru.gb.base.Font;
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
    private static final float FONT_SIZE = 0.015f;
    private static final float MARGIN = 0.01f;
    private static final String FRAGS = "Frags: ";
    private static final String HP = "HP: ";
    private static final String LEVEL = "Level: ";


    private Sound laserSound;
    private Sound bulletSound;
    private Sound explosionSound;
    private Music backgroundMusic;

    private TextureAtlas atlas;
    //    private TextureAtlas newExplosionAtlas;
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

    private int frags;
    private StringBuilder sbFrags;
    private StringBuilder sbHp;
    private StringBuilder sbLevel;

    private Font font;

    @Override
    public void show() {
        super.show();
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/music.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.05f);
        backgroundMusic.play();

        laserSound = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.wav"));
        bulletSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bullet.wav"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.wav"));

        bg = new Texture("textures/bg.png");
        background = new Background(bg);

        atlas = new TextureAtlas("textures/mainAtlas.tpack");
//        newExplosionAtlas = new TextureAtlas("textures/explosionsAtlas.tpack");

        stars = new Star[STAR_COUNT];
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star(atlas);
        }

        bulletPool = new BulletPool();
        explosionPool = new ExplosionPool(atlas, explosionSound);
        enemyPool = new EnemyPool(bulletPool, explosionPool, worldBounds, bulletSound);

        mainShip = new MainShip(atlas, bulletPool, explosionPool, laserSound);

        enemyEmitter = new EnemyEmitter(enemyPool, atlas, worldBounds);

        frags = 0;
        sbFrags = new StringBuilder();
        sbHp = new StringBuilder();
        sbLevel = new StringBuilder();
        font = new Font("font/font.fnt", "font/font.png");
        font.setSize(FONT_SIZE);

        gameOver = new GameOver(atlas);
        newGameButton = new NewGameButton(atlas, this);
    }

    public void startNewGame() {

        frags = 0;

        bulletPool.freeAllActiveObjects();
        explosionPool.freeAllActiveObjects();
        enemyPool.freeAllActiveObjects();

        mainShip.startNewGame();
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
        font.dispose();
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
        if (!mainShip.isDestroyed()) {
            mainShip.touchDown(touch, pointer, button);
        } else {
            newGameButton.touchDown(touch, pointer, button);
        }
        return super.touchDown(touch, pointer, button);
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer, int button) {
        if (!mainShip.isDestroyed()) {
            mainShip.touchUp(touch, pointer, button);
        } else {
            newGameButton.touchUp(touch, pointer, button);
        }
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
            enemyEmitter.generate(delta, frags);
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
                    if (enemyShip.isDestroyed()) {
                        frags++;
                    }
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
        printInfo();
        batch.end();
    }

    private void printInfo() {
        sbFrags.setLength(0);
        font.draw(
                batch,
                sbFrags.append(FRAGS).append(frags),
                worldBounds.getLeft() + MARGIN,
                worldBounds.getTop() - MARGIN
        );
        sbHp.setLength(0);
        font.draw(
                batch,
                sbHp.append(HP).append(mainShip.getHp()),
                worldBounds.pos.x,
                worldBounds.getTop() - MARGIN,
                Align.center
        );
        sbLevel.setLength(0);
        font.draw(
                batch,
                sbLevel.append(LEVEL).append(enemyEmitter.getLevel()),
                worldBounds.getRight() - MARGIN,
                worldBounds.getTop() - MARGIN,
                Align.right
        );
    }

}
