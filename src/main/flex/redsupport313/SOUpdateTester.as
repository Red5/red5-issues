package redsupport313
{

	import flash.display.Sprite;
	import flash.events.AsyncErrorEvent;
	import flash.events.MouseEvent;
	import flash.events.NetStatusEvent;
	import flash.events.SyncEvent;
	import flash.net.NetConnection;
	import flash.net.ObjectEncoding;
	import flash.net.SharedObject;
	import flash.text.TextField;
	import flash.text.TextFormat;
    import flash.external.ExternalInterface;
	
	public class red5SyncExample extends Sprite {

		private var nc:NetConnection;
		private var sharedObj:SharedObject;
		private var randomNr1:Number;
		private var randomNr2:Number;
		
		public function red5SyncExample() {

            buildUI();
            saveBtn.addEventListener(MouseEvent.CLICK, saveValue);
            clearBtn.addEventListener(MouseEvent.CLICK, clearValue);

			randomize();
			
			data = new TextField();
			data.width = 300;
			data.text = randomNr1 + " " + randomNr2;
			data.setTextFormat(new TextFormat("Arial", 15));
			this.addChild(data);
			
			updateBtn = new Sprite();
			updateBtn.graphics.beginFill(0x333333, 1);
			updateBtn.graphics.drawRect(0, 20, 100, 20);
			updateBtn.graphics.endFill();
			this.addChild(updateBtn);
			updateBtn.buttonMode = true;
			updateBtn.addEventListener(MouseEvent.CLICK, updateData);
			
			var updateText:TextField = new TextField();
			updateText.text = "Update";
			updateText.y = 18;
			updateText.setTextFormat(new TextFormat("Arial", 15, 0xFFFFFF));
			updateBtn.addChild(updateText);
			updateBtn.mouseChildren = false;
			
			connectToServer();
		}
		
		private function connectToServer():void {
			log("connectToServer()");
			
			nc = new NetConnection();
			nc.client = this;
			nc.addEventListener(NetStatusEvent.NET_STATUS,onNetConnectionStatus);
			nc.addEventListener(AsyncErrorEvent.ASYNC_ERROR,onAsyncError);
			
			nc.connect("rtmp://localhost/issues/_definst_", "dummy");
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
			log("onSyncSo()" + sharedObj.data);		
			data.text = sharedObj.data["object1"]["random1"] + " " + sharedObj.data["object1"]["random2"];

            for (var i:Object in event.changeList) {
                var changeObj:Object = event.changeList[i];                       
                if (changeObj.code === 'success') { 
                    log(sharedObj.data[changeObj.name]);                        
                }
                if (changeObj.code === 'change') {
                    log(sharedObj.data[changeObj.name]);
                }
            }
		}
		
		private function updateData(e:MouseEvent):void {
			log("updateData()");
			randomize();
			nc.call("updateSo", null, randomNr1, randomNr2);
		}
		
		private function randomize():void {
			randomNr1 = Math.ceil(Math.random() * 9999);
			randomNr2 = Math.ceil(Math.random() * 9999);
		}

        private function sendParticipants():void {
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
            ExternalInterface.call("console.log", entry);
        }

        // UI elements

		private var updateBtn:Sprite;
		private var data:TextField;

        private var inputLbl:TextField;
        private var input:TextField;
        private var output:TextField;
        private var saveBtn:Sprite;
        private var clearBtn:Sprite;
    
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
            input.x = 80;
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
            output.width = 250;
            output.height = 250;
            output.multiline = true;
            output.wordWrap = true;
            output.border = true;
            output.background = true;
            
            // Save button
            saveBtn = new Sprite();
            addChild(saveBtn);
            saveBtn.x = 190;
            saveBtn.y = 10;
            saveBtn.useHandCursor = true;
            saveBtn.graphics.lineStyle(1);
            saveBtn.graphics.beginFill(0xcccccc);
            saveBtn.graphics.drawRoundRect(0, 0, 30, 20, 5, 5);
            var saveLbl:TextField = new TextField();
            saveBtn.addChild(saveLbl);
            saveLbl.text = "Save";
            saveLbl.selectable = false;
            
            // Clear button
            clearBtn = new Sprite();
            addChild(clearBtn);
            clearBtn.x = 230;
            clearBtn.y = 10;
            clearBtn.useHandCursor = true;
            clearBtn.graphics.lineStyle(1);
            clearBtn.graphics.beginFill(0xcccccc);
            clearBtn.graphics.drawRoundRect(0, 0, 30, 20, 5, 5);
            var clearLbl:TextField = new TextField();
            clearBtn.addChild(clearLbl);
            clearLbl.text = "Clear";
            clearLbl.selectable = false;
        }

	}
}
