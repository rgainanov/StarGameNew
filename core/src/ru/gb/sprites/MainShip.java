package ru.gb.sprites;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

import ru.gb.base.Ship;
import ru.gb.math.Rect;
import ru.gb.pool.BulletPool;
import ru.gb.pool.ExplosionPool;

public class MainShip extends Ship {

    private static final int DEFAULT_HP = 100;
    private static final float POWER_UP_INTERVAL = 10f;

    private static final float RELOAD_INTERVAL = 0.2f;

    private static final float HEIGHT = 0.15f;
    private static final float BOTTOM_MARGIN = 0.05f;
    private static final int INVALID_POINTER = -1;

    private boolean pressedLeft;
    private boolean pressedRight;

    private int leftPointer = INVALID_POINTER;
    private int rightPointer = INVALID_POINTER;

    private boolean laserMode;
    private boolean shieldMode;

    private float shieldAnimationTimer;
    private float shieldTimer;
    private float laserTimer;

    public MainShip(TextureAtlas atlas, BulletPool bulletPool, ExplosionPool explosionPool, Sound bulletSound) {
        super(atlas.findRegion("main_ship"), 1, 2, 2);
        this.reloadInterval = RELOAD_INTERVAL;
        this.bulletPool = bulletPool;
        this.explosionPool = explosionPool;
        this.bulletRegion = atlas.findRegion("bulletMainShip");
        this.bulletV = new Vector2(0, 0.5f);
        this.bulletHeight = 0.01f;
        this.bulletDamage = 1;
        this.bulletPos = new Vector2();
        this.hp = DEFAULT_HP;
        this.bulletSound = bulletSound;
        this.v = new Vector2();
        this.v0 = new Vector2(0.5f, 0);
        this.laserMode = false;
        this.shieldAnimationTimer = 0;
        this.shieldMode = false;
    }

    public void setLaserMode(boolean laserMode) {
        this.laserMode = laserMode;
    }

    public boolean isShieldMode() {
        return shieldMode;
    }

    public void setShieldMode(boolean shieldMode) {
        this.shieldMode = shieldMode;
    }

    @Override
    public void update(float delta) {
        bulletPos.set(this.pos.x, getTop());
        super.update(delta);
        if (getLeft() > worldBounds.getRight()) {
            setRight(worldBounds.getLeft());
        }
        if (getRight() < worldBounds.getLeft()) {
            setLeft(worldBounds.getRight());
        }

        if (laserMode) {
            laserTimer += delta;
            if (laserTimer > POWER_UP_INTERVAL) {
                setReloadInterval(RELOAD_INTERVAL);
                laserMode = false;
                laserTimer = 0;
            }
        }

        if (shieldMode) {
            shieldAnimation(delta);
            shieldTimer += delta;
            if (shieldTimer > POWER_UP_INTERVAL) {
                setShieldMode(false);
                shieldTimer = 0;
                shieldAnimationTimer = 0;
            }
        }

    }

    private void shieldAnimation(float delta) {
        frame = 0;
        shieldAnimationTimer += delta;
        if (shieldAnimationTimer > 0.2f) {
            shieldAnimationTimer = 0;
            frame = 1;
        }
    }

    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.LEFT:
                pressedLeft = true;
                moveLeft();
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                pressedRight = true;
                moveRight();
                break;
        }
        return false;
    }

    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.LEFT:
                pressedLeft = false;
                if (pressedRight) {
                    moveRight();
                } else {
                    stop();
                }
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                pressedRight = false;
                if (pressedLeft) {
                    moveLeft();
                } else {
                    stop();
                }
                break;
        }
        return false;
    }

    public boolean isBulletCollision(Bullet bullet) {
        return !(bullet.getRight() < getLeft()
                || bullet.getLeft() > getRight()
                || bullet.getBottom() > pos.y
                || bullet.getTop() < getBottom()
        );
    }

    public boolean isPowerUpCollision(PowerUp powerUp) {
        return !(powerUp.getRight() < getLeft()
                || powerUp.getLeft() > getRight()
                || powerUp.getBottom() > pos.y
                || powerUp.getTop() < getBottom()
        );
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button) {
        if (touch.x < worldBounds.pos.x) {
            if (leftPointer != INVALID_POINTER) {
                return false;
            }
            leftPointer = pointer;
            moveLeft();
        } else {
            if (rightPointer != INVALID_POINTER) {
                return false;
            }
            rightPointer = pointer;
            moveRight();
        }
        return false;
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer, int button) {
        if (pointer == leftPointer) {
            leftPointer = INVALID_POINTER;
            if (rightPointer != INVALID_POINTER) {
                moveRight();
            } else {
                stop();
            }
        } else if (pointer == rightPointer) {
            rightPointer = INVALID_POINTER;
            if (leftPointer != INVALID_POINTER) {
                moveLeft();
            } else {
                stop();
            }
        }
        stop();
        return false;
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        this.worldBounds = worldBounds;
        setHeightProportions(HEIGHT);
        setBottom(worldBounds.getBottom() + BOTTOM_MARGIN);
    }

    private void moveRight() {
        v.set(v0);
    }

    private void moveLeft() {
        v.set(v0).rotateDeg(180);
    }

    private void stop() {
        v.setZero();
    }

    public void startNewGame() {
        this.hp = DEFAULT_HP;
        leftPointer = INVALID_POINTER;
        rightPointer = INVALID_POINTER;
        pressedLeft = false;
        pressedRight = false;
        stop();
        pos.x = worldBounds.pos.x;
        flushDestroyed();
    }
}