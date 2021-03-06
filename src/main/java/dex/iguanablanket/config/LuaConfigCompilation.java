package dex.iguanablanket.config;

import dex.iguanablanket.IguanaBlanket;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import javax.script.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;

public class LuaConfigCompilation {
    public static HashMap<String, Float> blockslowdown = new HashMap<>();
    public static HashMap<String, Float> blockhardness = new HashMap<>();
    public static HashMap<String, Integer> stacksizes = new HashMap<>();
    public static HashMap<String, Float> weights = new HashMap<>();
    public static HashMap<String, String> enchants = new HashMap<>();
    public static HashMap<String, HashMap> syncedData = new HashMap<>();

    public static void updateMapsFloat(LuaTable table, HashMap<String, Float> map) {
        LuaValue[] keys = table.checktable().keys();
        for (int i = 0; i < table.checktable().keyCount(); i++) {
            map.put(keys[i].toString(), table.get(keys[i]).tofloat());
        }
    }

    public static void updateMapsInt(LuaTable table, HashMap<String, Integer> map) {
        LuaValue[] keys = table.checktable().keys();
        for (int i = 0; i < table.checktable().keyCount(); i++) {
            map.put(keys[i].toString(), table.get(keys[i]).toint());
        }
    }

    public static void updateMapsString(LuaTable table, HashMap<String, String> map) {
        LuaValue[] keys = table.checktable().keys();
        for (int i = 0; i < table.checktable().keyCount(); i++) {
            map.put(keys[i].toString(), table.get(keys[i]).toString());
        }
    }

    public static void getConfig(String config, LuaTable table) {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine scriptEngine = sem.getEngineByExtension(".lua");

        try {
            Reader x = new FileReader(config);
            try {
                CompiledScript script = ((Compilable) scriptEngine).compile(x);
                Bindings sb = new SimpleBindings();
                script.eval(sb);
                LuaFunction test = (LuaFunction) sb.get("generateTable");
                LuaTable b = test.call(table).checktable();

                updateMapsFloat(b.get("itemweights").checktable(), weights);
                updateMapsFloat(b.get("blockweights").checktable(), weights);
                updateMapsInt(b.get("itemstacksizes").checktable(), stacksizes);
                updateMapsInt(b.get("blockstacksizes").checktable(), stacksizes);
                updateMapsFloat(b.get("blockslowdownfactor").checktable(), blockslowdown);
                updateMapsFloat(b.get("blockhardnessscale").checktable(), blockhardness);
                updateMapsString(b.get("enchantmentsakesslowdownignorant").checktable(), enchants);
                stacksizes.forEach((s, integer) -> {
                    if (integer <= 0) {
                        stacksizes.put(s, 1);
                    } else if (integer > 64) {
                        stacksizes.put(s, 64);
                    }
                });

                syncedData.put("weights", weights);
                syncedData.put("stacksizes", stacksizes);

            } catch (ScriptException e) {
                IguanaBlanket.logger.error("Iguana was passed a bad lua script!");
                IguanaBlanket.logger.catching(e);
            }
        } catch (FileNotFoundException e) {
            IguanaBlanket.logger.error("iguana could not find the specified script file!");
            IguanaBlanket.logger.catching(e);
        }

    }

    static class Runner implements Runnable {
        final String script1;
        final LuaTable table;

        Runner(String script1, LuaTable table) {
            this.script1 = script1;
            this.table = table;
        }

        public void run() {
            try {
                getConfig(script1, table);
            } catch (Exception e) {
                IguanaBlanket.logger.catching(e);
            }
        }
    }

    public static HashMap<String, HashMap> threadedmain(final String script, LuaTable table) {
        try {
            Thread thread = new Thread(new LuaConfigCompilation.Runner(script, table),"IguanaLuaConfigRunner");
            thread.start();
            thread.join();
            IguanaBlanket.logger.info("Completed Loading Iguana Config Data");
            return syncedData;
        } catch ( Exception e ) {
            IguanaBlanket.logger.catching(e);
        }
        return null;
    }
}
