package dex.iguanablanket.config;

import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.Reloadable;

@Config.HotReload(type = Config.HotReloadType.ASYNC) //set value = X for interval of X seconds. Default: 5
@Config.Sources({"file:${configDir}"})
public interface IguanaConfig extends Config, Reloadable, Accessible {

    @DefaultValue("true")
    boolean displayEncumbranceIcon();

    @DefaultValue("200.0")
    double defaultEntityMaxWeight();

    @DefaultValue("1.0")
    double defaultEntitySusceptibility();

    @DefaultValue("1.0")
    float shulkerboxWeightReductionFactor();

    @DefaultValue("true")
    boolean playerOverburdenedDoesPushups();

    @DefaultValue("0.2")
    double potionEffectWeightScaleFactor();

    @DefaultValue("default.lua")
    String luaConfig();

    @DefaultValue("0.1")
    double fovClamp();

    @DefaultValue("true")
    boolean destroyBedRespawn();

    @DefaultValue("11.2")
    double randomRespawnRange();

    @DefaultValue("true")
    boolean doesWeightEffectElytra();

    @DefaultValue("true")
    boolean doesWeightEffectSwimming();

    @DefaultValue("10")
    float maxThrowFactor();

    @DefaultValue("true")
    boolean showThrowMeter();

}
