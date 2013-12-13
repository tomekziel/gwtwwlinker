/************************************************************************
 * This is a part of gwtwwlinker project
 * https://github.com/tomekziel/gwtwwlinker 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 **************************************************************************/
package prj.gwtwwlinker.workerlinker.code;

import java.util.Set;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.dev.About;
import com.google.gwt.dev.util.DefaultTextOutput;

/**
 * This linker removes unnecessary GWT stuff to make the generated JS work inside HTML5 web worker
 * Part of https://github.com/tomekziel/gwtwwlinker 
 * 
 * Derived from project Titaniumj Mobile, where it was described as 
 * "This linker removes unnecessary GWT stuff to
 * make the generated JS work inside Titanium
 * TiMobileHybridLinker.java is part of Ti4j 3.1.0 Copyright 2013 Emitrom LLC"
 * https://github.com/emitrom/titanium4j/blob/master/src/com/emitrom/ti4j/mobile/linker/TiMobileLinker.java 
 */
@LinkerOrder(LinkerOrder.Order.PRIMARY)
public class WorkerLinker extends AbstractLinker {

    @Override
    public String getDescription() {
        return "gwtwwlinker - generate js for html5 web worker";
    }

    public ArtifactSet link(TreeLogger logger, LinkerContext context, ArtifactSet artifacts)
                    throws UnableToCompleteException {

        ArtifactSet toReturn = new ArtifactSet(artifacts);
        DefaultTextOutput out = new DefaultTextOutput(true);
        long compilationTime = System.currentTimeMillis();
        out.print("(function(){");
        out.newline();

        // get compilation result
        Set<CompilationResult> results = artifacts.find(CompilationResult.class);
        if (results.size() == 0) {
            logger.log(TreeLogger.WARN, "Requested 0 permutations");
            return toReturn;
        }

        CompilationResult result = results.iterator().next();

        // get the generated javascript
        String[] javaScript = result.getJavaScript();
        out.print("var $wnd;var $doc;");
        out.print("var $workergwtbridge;"); // gwtwwlinker - mind the prototype here
        out.print("var $moduleName, $moduleBase;");
        out.newline();
        out.print("if(typeof(window) != 'undefined'){ $wnd = window;  $doc = $wnd.document; }");
        out.newline();        
        out.print("else{ $wnd = {JSON: JSON}; }"); // gwtwwlinker - mind the $wnd.JSON passthrough used by autobeans
        out.newline();
        out.print("var $gwt_version = \"" + About.getGwtVersionNum() + "\";");
        out.newlineOpt();
        out.print(javaScript[0]);
        out.newline();
        out.print("var $stats = function(){};");
        out.newline();
        out.print("var $sessionId = function(){};");
        out.newline();
        out.print("var navigator = {};");
        out.newline();
        out.print("navigator.userAgent = 'timobile';");
        out.newline();
        out.print("$strongName = '" + result.getStrongName() + "';");
        out.newline();
        out.print("$ti4jCompilationDate = " + compilationTime + ";");
        out.newline();
        
        // gwtwwlinker - register web worker message receiver and pass it to the bridge function
        // registered in SampleWorker class
        out.print("self.addEventListener('message', function(e) {   $workergwtbridge(e.data); }, false);");
        out.newline();
        
        out.print("gwtOnLoad(null,'" + context.getModuleName() + "',null);");
        out.newline();
        out.print("})();");
        out.newline();

        
        // gwtwwlinker - WARNING! You must take care of naming your result JS here
        // this demo doesn't care of multiple permutation and cache issues!
        
        //String filename = context.getModuleName() + result.getStrongName()+ ".cache.js";
        String filename = "worker.js";
        
        toReturn.add(emitString(logger, out.toString(), filename));

        return toReturn;
    }

}
