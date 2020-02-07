package dex.iguanablanket;

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

    public static void updateMaps(LuaTable table, HashMap<String, Float> map) {
        for (int i = 0; i < table.checktable().keyCount(); i++) {
            LuaValue[] keys = table.checktable().keys();
            map.put(keys[i].toString(), table.get(keys[i]).tofloat());
        }
    }

    public static void updateMapsInt(LuaTable table, HashMap<String, Integer> map) {
        for (int i = 0; i < table.checktable().keyCount(); i++) {
            LuaValue[] keys = table.checktable().keys();
            map.put(keys[i].toString(), table.get(keys[i]).toint());
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
                LuaFunction test = (LuaFunction) sb.get("test");
                LuaTable b = test.call(table).checktable();

                updateMaps(b.get("itemweights").checktable(), weights);
                updateMaps(b.get("blockweights").checktable(), weights);
                updateMapsInt(b.get("itemstacksizes").checktable(), stacksizes);
                updateMapsInt(b.get("blockstacksizes").checktable(), stacksizes);
                updateMaps(b.get("blockslowdownfactor").checktable(), blockslowdown);
                updateMaps(b.get("blockhardnessscale").checktable(), blockhardness);

            } catch (ScriptException e) {
                System.out.println("Bad Script");
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
                e.printStackTrace();
            }
        }
    }

    public static void threadedmain(final String script, LuaTable table) {
        try {
            Thread thread = new Thread(new LuaConfigCompilation.Runner(script, table),"IguanaLuaConfigRunner");
            thread.start();
            thread.join();
            System.out.println("done");
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
