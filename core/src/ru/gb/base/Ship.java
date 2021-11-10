package ru.gb.base;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.gb.math.Rect;
import ru.gb.pool.BulletPool;
import ru.gb.pool.ExplosionPool;
import ru.gb.sprites.Bullet;
import ru.gb.sprites.Explosion;

public class Ship extends Sprite {

    private static final float DAMAGE_ANIMATE_INTERVAL = 0.1f;

    protected ExplosionPool explosionPool;
    protected BulletPool bulletPool;
    protected Sound bulletSound;
    protected TextureRegion bulletRegion;
    protected Vector2 bulletV;
    protected Vector2 bulletPos;
    protected float bulletHeight;
    protected int bulletDamage;
    protected int hp;

    protected Vector2 v;
    protected Vector2 v0;

    protected float reloadTimer;
    protected float reloadInterval;

    protected Rect worldBounds;

    private float damageAnimateTimer = DAMAGE_ANIMATE_INTERVAL;

    public Ship() {

    }

    public float getReloadInterval() {
        return reloadInterval;
    }

    public void setReloadInterval(float reloadInterval) {
        this.reloadInterval = reloadInterval;
    }

    public Ship(TextureRegion region, int rows, int cols, int frames) {
        super(region, rows, cols, frames);
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getDamage() {
        return bulletDamage;
    }

    public void damage(int hp) {
        this.hp -= hp;
        if (this.hp <= 0) {
            destroy();
        }
        damageAnimateTimer = 0f;
        frame = 1;
    }

    private void shoot() {
        Bullet bullet = bulletPool.obtain();
        bullet.set(this, bulletRegion, bulletPos, bulletV, worldBounds, bulletHeight, bulletDamage);
        bulletSound.play(0.007f);
    }

    public void destroyWithoutBoom() {
        super.destroy();
    }

    @Override
    public void destroy() {
        super.destroy();
        boom();
    }

    @Override
    public void update(float delta) {
        pos.mulAdd(v, delta);
        reloadTimer += delta;
        if (reloadTimer >= reloadInterval) {
            reloadTimer = 0f;
//            bulletPos.set(pos);
            shoot();
        }
        damageAnimateTimer += delta;
        if (damageAnimateTimer >= DAMAGE_ANIMATE_INTERVAL) {
            frame = 0;
        }
    }

    private void boom() {
        Explosion explosion = explosionPool.obtain();
        explosion.set(this.pos, getHeight());
    }
}
