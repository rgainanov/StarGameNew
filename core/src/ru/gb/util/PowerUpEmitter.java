package ru.gb.util;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.gb.math.Rect;
import ru.gb.math.Rnd;
import ru.gb.pool.PowerUpPool;
import ru.gb.sprites.PowerUp;

public class PowerUpEmitter {

    private static final float GENERATE_INTERVAL = 4f;

    private final PowerUpPool powerUpPool;
    private final Rect worldBounds;

    private final TextureRegion pillBlue;
    private final TextureRegion pillGreen;
    private final TextureRegion pillRed;
    private final TextureRegion boltGold;
    private final TextureRegion shieldSilver;
    private final TextureRegion starGold;
    private final TextureRegion thingsBronze;

    private float generateTimer;

    private Vector2 v;

    public PowerUpEmitter(PowerUpPool powerUpPool, TextureAtlas atlas, Rect worldBounds) {
        this.worldBounds = worldBounds;
        this.powerUpPool = powerUpPool;

        this.pillBlue = atlas.findRegion("pill_blue");
        this.pillGreen = atlas.findRegion("pill_green");
        this.pillRed = atlas.findRegion("pill_red");
        this.boltGold = atlas.findRegion("bolt_gold");
        this.shieldSilver = atlas.findRegion("shield_silver");
        this.starGold = atlas.findRegion("star_gold");
        this.thingsBronze = atlas.findRegion("things_bronze");
        this.v = new Vector2();
    }

    public void generate(float delta) {
        generateTimer += delta;
        if (generateTimer >= GENERATE_INTERVAL) {
            generateTimer = 0f;
            PowerUp powerUp = powerUpPool.obtain();
            float type = (float) Math.random();
            if (type < 0.1f) {
                v = new Vector2(Rnd.nextFloat(-0.005f, 0.005f), Rnd.nextFloat(-0.2f, -0.05f));
                powerUp.set(pillBlue, "pillBlue", v, 0.03f);
            } else if (type < 0.2f) {
                v = new Vector2(Rnd.nextFloat(-0.005f, 0.005f), Rnd.nextFloat(-0.2f, -0.05f));
                powerUp.set(pillGreen, "pillGreen", v, 0.03f);
            } else if (type < 0.3f) {
                v = new Vector2(Rnd.nextFloat(-0.005f, 0.005f), Rnd.nextFloat(-0.2f, -0.05f));
                powerUp.set(pillRed, "pillRed", v, 0.03f);
            } else if (type < 0.75f) {
                v = new Vector2(Rnd.nextFloat(-0.005f, 0.005f), Rnd.nextFloat(-0.2f, -0.05f));
                powerUp.set(boltGold, "boltGold", v, 0.03f);
            } else if (type < 0.85f) {
                v = new Vector2(Rnd.nextFloat(-0.005f, 0.005f), Rnd.nextFloat(-0.2f, -0.05f));
                powerUp.set(shieldSilver, "shieldSilver", v, 0.03f);
            } else {
                v = new Vector2(Rnd.nextFloat(-0.005f, 0.005f), Rnd.nextFloat(-0.2f, -0.05f));
                powerUp.set(starGold, "starGold", v, 0.03f);
            }

            powerUp.pos.x = Rnd.nextFloat(
                    worldBounds.getLeft() + powerUp.getHalfHeight(),
                    worldBounds.getRight() - powerUp.getHalfHeight()
            );

            powerUp.setBottom(worldBounds.getTop());
        }
    }
}
