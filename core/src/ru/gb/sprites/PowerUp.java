package ru.gb.sprites;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.gb.base.Sprite;
import ru.gb.math.Rect;

public class PowerUp extends Sprite {

    private Vector2 v;
    private Rect worldBounds;
    private String name;


    public PowerUp(Rect worldBounds) {
        regions = new TextureRegion[1];
        this.worldBounds = worldBounds;
        this.v = new Vector2();
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
    }

    @Override
    public void update(float delta) {
        pos.mulAdd(v, delta);

        if (isOutside(worldBounds)) {
            destroy();
        }
    }

    public void set(
            TextureRegion region,
            String name,
            Vector2 v,
            float height
    ) {
        this.regions[0] = region;
        this.name = name;
        this.v.set(v);
        setHeightProportions(height);
    }

    public String getName() {
        return name;
    }
}
