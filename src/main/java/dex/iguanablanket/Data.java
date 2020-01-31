package dex.iguanablanket;

import java.util.UUID;

public abstract class Data {
    public static double fovClamp = 0.1;

    public enum AttributeModifier {
        ENCUMBRANCE_SLOWDOWN {
            @Override
            public String toString() {
                return "iguana.encumbrance";
            }

            @Override
            public UUID getID() {
                return UUID.nameUUIDFromBytes(this.toString().getBytes());
            }
        },
        TERRAIN_SLOWDOWN {
            @Override
            public String toString() {
                return "iguana.terrainSlowdown";
            }

            @Override
            public UUID getID() {
                return UUID.nameUUIDFromBytes(this.toString().getBytes());
            }
        },
        HEALTH_SLOWDOWN {
            @Override
            public String toString() {
                return "iguana.healthSlowdown";
            }

            @Override
            public UUID getID() {
                return UUID.nameUUIDFromBytes(this.toString().getBytes());
            }
        },
        STRENGTH {
            @Override
            public String toString() {
                return "iguana.strength";
            }

            @Override
            public UUID getID() {
                return UUID.nameUUIDFromBytes(this.toString().getBytes());
            }
        };

        public UUID getID() {
            return null;
        }

    }
}
