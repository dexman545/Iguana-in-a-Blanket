package dex.iguanablanket;

import org.luaj.vm2.*;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;

import java.io.IOException;

public class LuaConfigLoader {
    // These globals are used by the server to compile scripts.
    static Globals server_globals;

    public static void main(String args) {
        // Create server globals with just enough library support to compile user scripts.
        server_globals = new Globals();
        server_globals.load(new JseBaseLib());
        server_globals.load(new PackageLib());

        // To load scripts, we occasionally need a math library in addition to compiler support.
        // To limit scripts using the debug library, they must be closures, so we only install LuaC.
        server_globals.load(new JseMathLib());
        LoadState.install(server_globals);
        LuaC.install(server_globals);

        // Set up the LuaString metatable to be read-only since it is shared across all scripts.
        LuaString.s_metatable = new ReadOnlyLuaTable(LuaString.s_metatable);

        // Example normal scripts that behave as expected.
        runScriptInSandbox( "return 'foo'" );
        runScriptInSandbox( "return ('abc'):len()" );
        runScriptInSandbox( "return getmetatable('abc')" );
        runScriptInSandbox( "return getmetatable('abc').len" );
        runScriptInSandbox( "return getmetatable('abc').__index" );

        // Example user scripts that attempt rogue operations, and will fail.
        runScriptInSandbox( "return setmetatable('abc', {})" );
        runScriptInSandbox( "getmetatable('abc').len = function() end" );
        runScriptInSandbox( "getmetatable('abc').__index = {}" );
        runScriptInSandbox( "getmetatable('abc').__index.x = 1" );
        runScriptInSandbox( "while true do print('loop') end" );

        // Example use of other shared metatables, which should also be made read-only.
        // This toy example allows booleans to be added to numbers.
        runScriptInSandbox( "return 5 + 6, 5 + true, false + 6" );
        LuaBoolean.s_metatable = new ReadOnlyLuaTable(LuaValue.tableOf(new LuaValue[] {
                LuaValue.ADD, new TwoArgFunction() {
            public LuaValue call(LuaValue x, LuaValue y) {
                return LuaValue.valueOf(
                        (x == TRUE ? 1.0 : x.todouble()) +
                                (y == TRUE ? 1.0 : y.todouble()) );
            }
        },
        }));
        runScriptInSandbox( "return 5 + 6, 5 + true, false + 6" );
    }

    // Run a script in a lua thread and limit it to a certain number
    // of instructions by setting a hook function.
    // Give each script its own copy of globals, but leave out libraries
    // that contain functions that can be abused.
    static void runScriptInSandbox(String script) {
        //exit if null
        if (script == null) {return;}

        // Each script will have it's own set of globals, which should
        // prevent leakage between scripts running on the same server.
        Globals user_globals = new Globals();
        user_globals.load(new JseBaseLib());
        user_globals.load(new PackageLib());
        user_globals.load(new Bit32Lib());
        user_globals.load(new TableLib());
        user_globals.load(new JseMathLib());

        // The debug library must be loaded for hook functions to work, which
        // allow us to limit scripts to run a certain number of instructions at a time.
        // However we don't wish to expose the library in the user globals,
        // so it is immediately removed from the user globals once created.
        user_globals.load(new DebugLib());
        LuaValue sethook = user_globals.get("debug").get("sethook");
        user_globals.set("debug", LuaValue.NIL);

        // Set up the script to run in its own lua thread, which allows us
        // to set a hook function that limits the script to a specific number of cycles.
        // Note that the environment is set to the user globals, even though the
        // compiling is done with the server globals.
        LuaValue chunk = server_globals.load(script, "main", user_globals);
        LuaThread thread = new LuaThread(user_globals, chunk);

        // Set the hook function to immediately throw an Error, which will not be
        // handled by any Lua code other than the coroutine.
        LuaValue hookfunc = new ZeroArgFunction() {
            public LuaValue call() {
                // A simple lua error may be caught by the script, but a
                // Java Error will pass through to top and stop the script.
                throw new Error("Script overran resource limits.");
            }
        };
        final int instruction_count = 20;
        sethook.invoke(LuaValue.varargsOf(new LuaValue[] { thread, hookfunc,
                LuaValue.EMPTYSTRING, LuaValue.valueOf(instruction_count) }));

        // When we resume the thread, it will run up to 'instruction_count' instructions
        // then call the hook function which will error out and stop the script.
        Varargs result = thread.resume(LuaValue.NIL);
        System.out.println("[["+script+"]] -> "+result);
    }

    // Simple read-only table whose contents are initialized from another table.
    static class ReadOnlyLuaTable extends LuaTable {
        public ReadOnlyLuaTable(LuaValue table) {
            presize(table.length(), 0);
            for (Varargs n = table.next(LuaValue.NIL); !n.arg1().isnil(); n = table
                    .next(n.arg1())) {
                LuaValue key = n.arg1();
                LuaValue value = n.arg(2);
                super.rawset(key, value.istable() ? new ReadOnlyLuaTable(value) : value);
            }
        }
        public LuaValue setmetatable(LuaValue metatable) { return error("table is read-only"); }
        public void set(int key, LuaValue value) { error("table is read-only"); }
        public void rawset(int key, LuaValue value) { error("table is read-only"); }
        public void rawset(LuaValue key, LuaValue value) { error("table is read-only"); }
        public LuaValue remove(int pos) { return error("table is read-only"); }
    }

    static class Runner implements Runnable {
        final String script1, script2;
        Runner(String script1, String script2) {
            this.script1 = script1;
            this.script2 = script2;
        }
        public void run() {
            try {
                //runScriptInSandbox(script1);
                //runScriptInSandbox(script2);
                main("test");

            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }

    public static void threadedmain(final String[] args) throws IOException {
        final String script1 = args.length > 0? args[0]: null;
        final String script2 = args.length > 1? args[1]: null;
        try {
            Thread[] thread = new Thread[10];
            for (int i = 0; i < thread.length; ++i)
                thread[i] = new Thread(new Runner(script1, script2),"Runner-"+i);
            for (Thread item : thread) item.start();
            for (Thread value : thread) value.join();
            System.out.println("done");
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
