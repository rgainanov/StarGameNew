package ru.gb.sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.gb.base.Ship;
import ru.gb.math.Rect;
import ru.gb.pool.BulletPool;

public class EnemyShip extends Ship {

    private Vector2 shipGameSpeed;

    public EnemyShip(BulletPool bulletPool, Rect worldBounds, Sound bulletSound) {
        this.bulletSound = bulletSound;
        this.bulletPool = bulletPool;
        this.worldBounds = worldBounds;
        this.bulletV = new Vector2();
        this.bulletPos = new Vector2();
        this.v = new Vector2();
        this.v0 = new Vector2();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (getTop() >= worldBounds.getTop()) {
            v.set(0f, -0.3f);
        } else {
            v.set(shipGameSpeed);
        }

        if (hp <= 0) {
            destroy();
        }
        if (getBottom() < worldBounds.getBottom()) {
            destroy();
        }
    }

    public void set(
            TextureRegion[] regions,
            Vector2 v,
            TextureRegion bulletRegion,
            float bulletHeight,
            Vector2 bulletV,
            int bulletDamage,
            int hp,
            float reloadInterval,
            float height
    ) {
        this.regions = regions;
        this.shipGameSpeed = v;
        this.v.set(v);
        this.bulletRegion = bulletRegion;
        this.bulletHeight = bulletHeight;
        this.bulletV.set(bulletV);
        this.bulletDamage = bulletDamage;
        this.hp = hp;
        this.reloadInterval = reloadInterval;
        setHeightProportions(height);
    }

}
