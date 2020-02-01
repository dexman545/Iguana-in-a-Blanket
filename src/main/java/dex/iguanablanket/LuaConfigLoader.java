package dex.iguanablanket;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.server.Launcher;
import org.luaj.vm2.server.LuajClassLoader;

import java.io.InputStream;
import java.io.Reader;

public class LuaConfigLoader {
    static String script =
            "print('args:', ...)\n" +
                    "print('abc.foo', ('abc').foo)\n" +
                    "getmetatable('abc').__index.foo = function() return 'bar' end\n" +
                    "print('abc.foo', ('abc').foo)\n" +
                    "print('abc:foo()', ('abc'):foo())\n" +
                    "return math.pi\n";

    public static void main() throws Exception {
        // Example using custom launcher class that instantiates debug globals.
        RunUsingCustomLauncherClass();
        RunUsingCustomLauncherClass();
    }


    static void RunUsingCustomLauncherClass() throws Exception {
        Launcher launcher = LuajClassLoader.NewLauncher(MyLauncher.class);
        // starts with pristine Globals including all luaj static variables.
        print(launcher.launch(script, new Object[] { "=========" }));
        // reuses Globals and static variables from previous step.
        print(launcher.launch(script, new Object[] { "" }));
    }

    /** Example of Launcher implementation performing specialized launching.
     * When loaded by the {@link LuajClassLoader} all luaj classes will be loaded
     * for each instance of the {@link Launcher} and not interfere with other
     * classes loaded by other instances.
     */
    public static class MyLauncher implements Launcher {
        Globals g;
        public MyLauncher() {
            g = JsePlatform.debugGlobals();
            // ... plus any other customization of the user environment
        }

        public Object[] launch(String script, Object[] arg) {
            LuaValue chunk = g.load(script, "main");
            return new Object[] { chunk.call(LuaValue.valueOf(arg[0].toString())) };
        }

        public Object[] launch(InputStream script, Object[] arg) {
            LuaValue chunk = g.load(script, "main", "bt", g);
            return new Object[] { chunk.call(LuaValue.valueOf(arg[0].toString())) };
        }

        public Object[] launch(Reader script, Object[] arg) {
            LuaValue chunk = g.load(script, "main");
            return new Object[] { chunk.call(LuaValue.valueOf(arg[0].toString())) };
        }
    }

    /** Print the return values as strings. */
    private static void print(Object[] return_values) {
        for (int i =0; i<return_values.length; ++i)
            System.out.println("Return value " + return_values[i]);
    }

    
}
