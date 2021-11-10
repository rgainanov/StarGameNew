package ru.gb.pool;

import ru.gb.base.SpritesPool;;
import ru.gb.math.Rect;
import ru.gb.sprites.PowerUp;

public class PowerUpPool extends SpritesPool<PowerUp> {

    private Rect worldBounds;

    public PowerUpPool(Rect worldBounds) {
        this.worldBounds = worldBounds;
    }

    @Override
    protected PowerUp newObject() {
        return new PowerUp(worldBounds);
    }
}
