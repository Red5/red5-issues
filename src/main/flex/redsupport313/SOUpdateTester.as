package redsupport313
{

	import flash.display.Sprite;
	import flash.events.AsyncErrorEvent;
	import flash.events.MouseEvent;
	import flash.events.NetStatusEvent;
	import flash.events.SyncEvent;
	import flash.external.ExternalInterface;
	import flash.net.NetConnection;
	import flash.net.ObjectEncoding;
	import flash.net.SharedObject;
	import flash.text.*;
	
	public class SOUpdateTester extends Sprite {

		private var nc:NetConnection;
		private var sharedObj:SharedObject;
		private var randomNr1:Number;
		private var randomNr2:Number;

		private var host:String = "192.168.86.102";
		
		public function SOUpdateTester() {
			ExternalInterface.marshallExceptions = true;
            buildUI();
            updateBtn.addEventListener(MouseEvent.CLICK, updateData);
			sendBtn.addEventListener(MouseEvent.CLICK, getMap);
            //sendBtn.addEventListener(MouseEvent.CLICK, sendParticipants);
			randomize();
			
			connectToServer();
		}
		
		private function connectToServer():void {
			log("connectToServer()");
			
			nc = new NetConnection();
			nc.client = this;
			nc.addEventListener(NetStatusEvent.NET_STATUS,onNetConnectionStatus);
			nc.addEventListener(AsyncErrorEvent.ASYNC_ERROR,onAsyncError);
			
			nc.connect("rtmp://" + host + "/issues/_definst_", "dummy");
		}
		
		private function onNetConnectionStatus(e:NetStatusEvent):void {
			log("onNetConnectionStatus("+e.info.code+")");
			if (e.info.code == "NetConnection.Connect.Success") {
				startSOSync();
			} else if (e.info.code == "NetConnection.Connect.Failed") {
				log("Could not connect to server");
			} else if (e.info.code == "NetConnection.Connect.Closed") {
				log("Connection closed");
			}
		}
		
		private function onAsyncError(e:AsyncErrorEvent):void {
			log("asyncError("+e+")")
		}
		
		private function startSOSync():void {
			log("startSOSync()");
            // http://help.adobe.com/en_US/FlashPlatform/reference/actionscript/3/flash/net/SharedObject.html
			sharedObj = SharedObject.getRemote("soName", nc.uri);
			sharedObj.addEventListener(SyncEvent.SYNC, onSync);
			sharedObj.connect(nc);
		}
		
		private function onSync(event:SyncEvent):void {
			//log("onSyncSo() " + sharedObj.data);
			log("onSyncSo() " + event.changeList.length); 

            for (var cl:int = 0; cl < event.changeList.length; cl++) {
                var changeObj:Object = event.changeList[cl]; 
                switch (changeObj.code) {
                    case "clear":
                        log("Clear: " + (changeObj.name !== undefined ? changeObj.name : ""));
                        break;
                    case "success":
                        log("Success: " + changeObj.name);
						log("Value: " + sharedObj.data[changeObj.name]); 
                        break;
                    case "change":
                        log("Change: " + changeObj.name);
						log("Value: " + sharedObj.data[changeObj.name]); 
						if (changeObj.name === "object1" && sharedObj.data[changeObj.name]) {							
							log(sharedObj.data["object1"].random1 + " " + sharedObj.data["object1"].random2);				
						}
						if (changeObj.name === "attributeMap" && sharedObj.data[changeObj.name]) {
							//log(sharedObj.data["attributeMap"]);
							log("Key1: " + sharedObj.data["attributeMap"].key1);				
							log("Key2: " + sharedObj.data["attributeMap"].key2);
						}
                        break;
                }
            }
            /*
            for (var i:Object in event.changeList) {
                var changeObj:Object = event.changeList[i];                       
                if (changeObj.code === 'success') { 
                    log(sharedObj.data[changeObj.name]);                        
                }
                if (changeObj.code === 'change') {
                    log(sharedObj.data[changeObj.name]);
                }
            }
            */

		}
		
		private function updateData(e:MouseEvent):void {
			log("updateData()");
			randomize();
			nc.call("updateSo", null, randomNr1, randomNr2);
		}
		
		private function getMap(e:MouseEvent):void {
			log("getMap()");            
			var map:Object = {
				key1: "aaaaa",
				key2: "bbbbb"
			};
			nc.call("getMap", null, map);
		}
		
		private function randomize():void {
			randomNr1 = Math.ceil(Math.random() * 9999);
			randomNr2 = Math.ceil(Math.random() * 9999);
			input.text = randomNr1 + " " + randomNr2;
		}

        private function sendParticipants(e:MouseEvent):void {
            var value:Object = {
                participants: {
                    one: {
                        download: 300
                         },
                    two: {
                        download: 100
                         }
                }
            };
            sharedObj.setProperty("soName", value);
            sharedObj.setDirty("soName");
        }

        public function log(entry:String):void {
			output.appendText(entry + "\n");
			if (ExternalInterface.available) {
				try {
            	    ExternalInterface.call("console.log", entry);
				
				} catch(e:Error) {
					//output.appendText(e.message + "\n");
				}
			}
			//set vertical scroll position to max value
			output.scrollV = output.maxScrollV;
        }

        // UI elements

		private var updateBtn:Sprite;
		private var data:TextField;

        private var inputLbl:TextField;
        private var input:TextField;
        private var output:TextField;
        private var sendBtn:Sprite;
    
        private function buildUI():void {
            // input label
            inputLbl = new TextField();
            addChild(inputLbl);
            inputLbl.x = 10;
            inputLbl.y = 10;
            inputLbl.text = "Value to save:";
            
            // input TextField
            input = new TextField();
            addChild(input);
            input.x = 100;
            input.y = 10;
            input.width = 100;
            input.height = 20;
            input.border = true;
            input.background = true;
            input.type = TextFieldType.INPUT;
            
            // output TextField
            output = new TextField();
            addChild(output);
            output.x = 10;
            output.y = 35;
            output.width = 350;
            output.height = 250;
            output.multiline = true;
            output.wordWrap = true;
            output.border = true;
            output.background = true;
			
            // Save button
			updateBtn = new Sprite();
            addChild(updateBtn);
			updateBtn.x = 10;
			updateBtn.y = 290;
			updateBtn.useHandCursor = true;
			updateBtn.buttonMode = true;
			updateBtn.graphics.lineStyle(1);
			updateBtn.graphics.beginFill(0xcccccc);
			updateBtn.graphics.drawRoundRect(0, 0, 45, 20, 5, 5);
            var updateLbl:TextField = new TextField();
			updateBtn.addChild(updateLbl);
			updateLbl.text = "Update";
			updateLbl.selectable = false;
            
            // Clear button
			sendBtn = new Sprite();
            addChild(sendBtn);
			sendBtn.x = 60;
			sendBtn.y = 290;
			sendBtn.useHandCursor = true;
			sendBtn.buttonMode = true;
			sendBtn.graphics.lineStyle(1);
			sendBtn.graphics.beginFill(0xcccccc);
			sendBtn.graphics.drawRoundRect(0, 0, 60, 20, 5, 5);
            var sendLbl:TextField = new TextField();
			sendBtn.addChild(sendLbl);
			sendLbl.text = "Send Map";
			sendLbl.selectable = false;
        }

	}
}
