package ru.gb.util;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.gb.math.Rect;
import ru.gb.math.Rnd;
import ru.gb.pool.EnemyPool;
import ru.gb.sprites.EnemyShip;

public class EnemyEmitter {

    private static final float GENERATE_INTERVAL = 4f;

    private static final float ENEMY_SMALL_HEIGHT = 0.1f;
    private static final float ENEMY_SMALL_BULLET_HEIGHT = 0.01f;
    private static final int ENEMY_SMALL_BULLET_DAMAGE = 1;
    private static final float ENEMY_SMALL_RELOAD_INTERVAL = 3f;
    private static final int ENEMY_SMALL_HP = 1;

    private static final float ENEMY_MEDIUM_HEIGHT = 0.15f;
    private static final float ENEMY_MEDIUM_BULLET_HEIGHT = 0.02f;
    private static final int ENEMY_MEDIUM_BULLET_DAMAGE = 5;
    private static final float ENEMY_MEDIUM_RELOAD_INTERVAL = 4f;
    private static final int ENEMY_MEDIUM_HP = 5;

    private static final float ENEMY_LARGE_HEIGHT = 0.2f;
    private static final float ENEMY_LARGE_BULLET_HEIGHT = 0.04f;
    private static final int ENEMY_LARGE_BULLET_DAMAGE = 10;
    private static final float ENEMY_LARGE_RELOAD_INTERVAL = 1f;
    private static final int ENEMY_LARGE_HP = 10;

    private final EnemyPool enemyPool;
    private final Rect worldBounds;
    private final TextureRegion bulletRegion;

    private final TextureRegion[] enemySmallRegions;
    private final TextureRegion[] enemyMediumRegions;
    private final TextureRegion[] enemyLargeRegions;

    private final Vector2 enemySmallV = new Vector2(0f, -0.2f);
    private final Vector2 enemyMediumV = new Vector2(0f, -0.25f);
    private final Vector2 enemyLargeV = new Vector2(0f, -0.005f);

    private final Vector2 enemySmallBulletV = new Vector2(0f, -0.3f);
    private final Vector2 enemyMediumBulletV = new Vector2(0f, -0.3f);
    private final Vector2 enemyLargeBulletV = new Vector2(0f, -0.3f);

    private float generateTimer;

    public EnemyEmitter(EnemyPool enemyPool, TextureAtlas atlas, Rect worldBounds) {
        this.enemyPool = enemyPool;
        this.worldBounds = worldBounds;
        this.bulletRegion = atlas.findRegion("bulletEnemy");
        this.enemySmallRegions = Regions.split(atlas.findRegion("enemy0"), 1, 2, 2);
        this.enemyMediumRegions = Regions.split(atlas.findRegion("enemy1"), 1, 2, 2);
        this.enemyLargeRegions = Regions.split(atlas.findRegion("enemy2"), 1, 2, 2);
    }

    public void generate(float delta) {
        generateTimer += delta;
        if (generateTimer >= GENERATE_INTERVAL) {
            generateTimer = 0f;
            EnemyShip enemy = enemyPool.obtain();
            float type = (float) Math.random();
            if (type < 0.5f) {
                enemy.set(
                        enemySmallRegions,
                        enemySmallV,
                        bulletRegion,
                        ENEMY_SMALL_BULLET_HEIGHT,
                        enemySmallBulletV,
                        ENEMY_SMALL_BULLET_DAMAGE,
                        ENEMY_SMALL_HP,
                        ENEMY_SMALL_RELOAD_INTERVAL,
                        ENEMY_SMALL_HEIGHT
                );
            } else if (type < 0.8f) {
                enemy.set(
                        enemyMediumRegions,
                        enemyMediumV,
                        bulletRegion,
                        ENEMY_MEDIUM_BULLET_HEIGHT,
                        enemyMediumBulletV,
                        ENEMY_MEDIUM_BULLET_DAMAGE,
                        ENEMY_MEDIUM_HP,
                        ENEMY_MEDIUM_RELOAD_INTERVAL,
                        ENEMY_MEDIUM_HEIGHT
                );
            } else {
                enemy.set(
                        enemyLargeRegions,
                        enemyLargeV,
                        bulletRegion,
                        ENEMY_LARGE_BULLET_HEIGHT,
                        enemyLargeBulletV,
                        ENEMY_LARGE_BULLET_DAMAGE,
                        ENEMY_LARGE_HP,
                        ENEMY_LARGE_RELOAD_INTERVAL,
                        ENEMY_LARGE_HEIGHT
                );
            }

            enemy.pos.x = Rnd.nextFloat(
                    worldBounds.getLeft() + enemy.getHalfHeight(),
                    worldBounds.getRight() - enemy.getHalfHeight()
            );

            enemy.setBottom(worldBounds.getTop());
        }

    }
}
