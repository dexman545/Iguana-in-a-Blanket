package dex.iguanablanket;

import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;

public class Storage {
    static class Tuple<X, Y> {
        public final X x;
        public final Y y;
        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }

    HashMap<String, Tuple<PlayerEntity, Double>> test = new HashMap<>();

}
