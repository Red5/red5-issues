package
{
/*
    Thanks to remus@nusofthq.com for creating the application source for testing this issue.
 */

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
	
	public class red5SyncExample extends Sprite
	{
		private var nc:NetConnection;
		private var updateBtn:Sprite;
		private var data:TextField;
		private var _so:SharedObject;
		private var randomNr1:Number;
		private var randomNr2:Number;
		
		public function red5SyncExample()
		{	
			randomize();
			
			data = new TextField();
			data.width = 300;
			data.text = randomNr1 + " " + randomNr2;
			data.setTextFormat(new TextFormat("Arial",15));
			this.addChild(data);
			
			updateBtn = new Sprite();
			updateBtn.graphics.beginFill(0x333333,1);
			updateBtn.graphics.drawRect(0,20,100,20);
			updateBtn.graphics.endFill();
			this.addChild(updateBtn);
			updateBtn.buttonMode = true;
			updateBtn.addEventListener(MouseEvent.CLICK, updateData);
			
			var updateText:TextField = new TextField();
			updateText.text = "Update";
			updateText.y = 18;
			updateText.setTextFormat(new TextFormat("Arial",15,0xFFFFFF));
			updateBtn.addChild(updateText);
			updateBtn.mouseChildren = false;
			
			connectToServer();
		}
		
		private function connectToServer():void{
			trace("connectToServer()");
			
			nc = new NetConnection();
			nc.client = this;
			nc.addEventListener(NetStatusEvent.NET_STATUS,onNetConnectionStatus);
			nc.addEventListener(AsyncErrorEvent.ASYNC_ERROR,onAsyncError);
			
			nc.connect("rtmp://localhost/issues/_definst_", "dummy");
		}
		
		private function onNetConnectionStatus(e:NetStatusEvent):void{
			trace("onNetConnectionStatus("+e.info.code+")");
			if (e.info.code == "NetConnection.Connect.Success"){
				
				startSOSync();
			}else if (e.info.code == "NetConnection.Connect.Failed"){
				trace("Could not connect to server");
			}else if (e.info.code == "NetConnection.Connect.Closed") {
				trace("Connection closed");
			}
		}
		
		private function onAsyncError(e:AsyncErrorEvent):void{
			trace("asyncError("+e+")")
		}
		
		private function startSOSync():void{
			trace("startSOSync()");
			_so = SharedObject.getRemote("example_so", nc.uri,false);
			_so.addEventListener(SyncEvent.SYNC,onSyncSo);
			_so.connect(nc);
		}
		
		private function onSyncSo(event:SyncEvent):void{
			trace("onSyncSo()" + _so.data);
			
			data.text = _so.data["object1"]["random1"] + " " + _so.data["object1"]["random2"];
		}
		
		private function updateData(e:MouseEvent):void{
			trace("updateData()");
			randomize();
			nc.call("updateSo",null, randomNr1, randomNr2);
		}
		
		private function randomize():void{
			randomNr1 = Math.ceil(Math.random() * 9999);
			randomNr2 = Math.ceil(Math.random() * 9999);
		}
	}
}