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
package prj.gwtwwlinker.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import elemental.events.EventListener;
import elemental.events.MessageEvent;
import elemental.html.Window;
import elemental.html.Worker;

public class WebApp implements EntryPoint {

	public void onModuleLoad() {
		final Button startButton = new Button("Send msg to worker");
		RootPanel.get("container1").add(startButton);
		
		final Window window = elemental.client.Browser.getWindow();
		
        // gwtwwlinker - WARNING! This demo doesn't care of multiple permutation and cache issues!
		// you may need to take care of it yourself! 
		final Worker worker = window.newWorker("../sampleworker/worker.js");		
		
		startButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				worker.postMessage("Hello worker");
			}
		});
		
		worker.setOnerror(new EventListener() {
			@Override
			public void handleEvent(elemental.events.Event evt) {
				window.alert("Error message form worker: "+evt.toString());
				
			}
		});
		
		worker.setOnmessage(new EventListener() {
			@Override
			public void handleEvent(elemental.events.Event evt) {
				if(evt instanceof MessageEvent){
					MessageEvent event = (MessageEvent)evt;
					Object obj = event.getData();
					Label l = new Label("Worker says: "+obj.toString());
					RootPanel.get("container2").insert(l, 0);
				}
			}
        });
		
	}

}

