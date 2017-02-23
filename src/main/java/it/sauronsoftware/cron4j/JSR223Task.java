/*
 * cron4j - A pure Java cron-like scheduler
 *
 * Copyright (C) 2007-2010 Carlo Pelliccia (www.sauronsoftware.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version
 * 2.1, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License 2.1 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License version 2.1 along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package it.sauronsoftware.cron4j;

import java.io.File;
import java.io.FileReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
/**
 * Allows to use ScriptEngine (Groovy, Jyton) languages in your cron tab like: <pre>* * * * 1-5 script:Hello.groovy</pre>
 * <b>Please note:</b> you need to make sure the JSR compatible script engine is on the class path.
 * @author Matthias Cullmann
 */
class JSR223Task extends Task {

    private final ScriptEngine engine;
    private final SimpleBindings bindings = new SimpleBindings();
    private final String scriptName;

    public JSR223Task(String scriptName, String[] args) throws Exception {

        File scriptFile = new File(scriptName);
        if (!scriptFile.exists()) {
            throw new Exception("Script file not found : " + scriptName);
        }
        ScriptEngineManager manager = new ScriptEngineManager();
        String extension = getExtension(scriptFile);
        engine = manager.getEngineByExtension(extension);
        if (engine == null) {
            throw new Exception("No engine found for extension " + extension);
        }
        bindings.put("args", args);
        bindings.put("argv", args);
        this.scriptName = scriptName;
    }

    /**
     * Implements {@link Task#execute(TaskExecutionContext)}.
     */
    public void execute(TaskExecutionContext context) throws RuntimeException {
        try {
            engine.eval(new FileReader(scriptName), bindings);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getExtension(File file) {
        String path = file.getPath();
        int dot = path.lastIndexOf('.');
        return path.substring(dot + 1);
    }

}