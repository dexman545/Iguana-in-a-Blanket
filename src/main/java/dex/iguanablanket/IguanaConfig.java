package dex.iguanablanket;

import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.Reloadable;

@Config.HotReload(type = Config.HotReloadType.ASYNC) //set value = X for interval of X seconds. Default: 5
@Config.Sources({"file:${configDir}"})
public interface IguanaConfig extends Config, Reloadable, Accessible {

    @DefaultValue("true")
    boolean displayEncumbranceIcon();

    @DefaultValue("100.0")
    double defaultEntityMaxWeight();

    @DefaultValue("1.0")
    double defaultEntitySusceptibility();

    @DefaultValue("1.0")
    float shulkerboxWeightReductionFactor();

    @DefaultValue("true")
    boolean playerOverburdenedDoesPushups();

}
