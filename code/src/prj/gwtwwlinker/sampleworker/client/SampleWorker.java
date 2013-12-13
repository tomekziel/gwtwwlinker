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
package prj.gwtwwlinker.sampleworker.client;

import com.google.gwt.core.client.EntryPoint;

import elemental.js.html.JsDedicatedWorkerGlobalScope;

public final class SampleWorker extends JsDedicatedWorkerGlobalScope implements EntryPoint
{
	protected SampleWorker()
	{
		// empty!
	}
	
    // Export the brige method when the application is loaded
    public void onModuleLoad() {
        exportBridge();
    }

    // This method is the bridge between HTML worker callback and GWT/Java code
    public static void bridge(String msg) {
    	advancedCalculations(msg);
    }
    
    // create a reference in the browser js to the Java method
    private native void exportBridge() /*-{
      $workergwtbridge = function(str) {
        @prj.gwtwwlinker.sampleworker.client.SampleWorker::bridge(Ljava/lang/String;)(str);
      };
    }-*/;

    // here is the core worker calculations method 
	public final static void advancedCalculations(String params)
	{
		myPostMessage("----====----");
		myPostMessage("Current timestamp "+System.currentTimeMillis());
		myPostMessage("Received message \""+params+"\"");
	}

    // Workaround for posting from static method
    private native static void myPostMessage(String str) /*-{
      self.postMessage(str);
    }-*/;
}
