package net.mizucoffee.wiimote4j.device;

import java.io.IOException;
import java.util.EventListener
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.groovy.classgen.ReturnAdder

import purejavahidapi.DeviceRemovalListener;
import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.InputReportListener;
import purejavahidapi.PureJavaHidApi;

/**
 * Wiimote
 * Wiimoteとの通信用クラス
 * 
 * @author KawakawaRitsuki
 * @version 0.1
 */

public class Wiimote {
	
	HidDevice mWiimoteDev;
	def WIMOTE_ONLY_REPORT_MODE = 0x30;
	def WIMOTE_NUNCHUCK_REPORT_MODE = 0x34;
	def led = "0000";
	def isChecked = false;
	
	static int WIIMOTE(){0}
	static int WIIBALANCE_BOARD(){1}

	/**
	 * 機器に接続します。
	 * 引数timeoutが0の場合タイムアウトしません。
	 * 引数deviceIdに代入されているデバイスに接続します。
	 * タイムアウトした場合はnullが返されます。
	 *
	 * @author KawakawaRitsuki
	 * @param timeout タイムアウト時間を指定します。
	 * @param deviceId デバイスを指定します。
	 * @return 接続結果が返されます。
	 * @version 0.1
	 */
	boolean connect(int timeout,int deviceId){
		if (deviceId != WIIMOTE() && deviceId != WIIBALANCE_BOARD()) return;
		for (int i = 0;timeout == 0 || i < timeout;i++){
			List<HidDeviceInfo> devList = PureJavaHidApi.enumerateDevices();
			devList.each{
				if (it.getVendorId() == 1406 && it.getProductId() == 774)
					if (it.getProductString() == "Nintendo RVL-CNT-01" && deviceId == WIIMOTE()){
						mWiimoteDev = PureJavaHidApi.openDevice(it.getPath());
						return true;
					}
					if (it.getProductString() == "Nintendo RVL-WBC-01" && deviceId == WIIBALANCE_BOARD()) {
						mWiimoteDev = PureJavaHidApi.openDevice(it.getPath());
						return true;
					}
			}
			if(mWiimoteDev != null) return true;
			Thread.sleep(1000);
		}
		return false;
	}
	
	/**
	 * 接続処理用コンストラクタ。諸設定をします。
	 * 
	 * @author KawakawaRitsuki
	 * @param info WiimoteのHidDeviceInfo
	 * @version 0.1
	 */
	public Wiimote(int timeout,int deviceId){
		try {
			if (!connect(timeout,deviceId)) return;//error throw
			
			mWiimoteDev.setInputReportListener(new InputReportListener() {
				@Override
				public void onInputReport(HidDevice source, byte Id, byte[] b, int len) {
					def s = Integer.toBinaryString(b[3]).padLeft(8,'0')
					led = s[0..3]
					isChecked = true;
					
					listener.onInputReport(source,Id,b,len)
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Wiimoteが接続されているかを返します。
	 * 
	 * @author KawakawaRitsuki
	 * @return 接続されているかどうか
	 * @version 0.1
	 */
	public boolean isConnected(){
		mWiimoteDev != null
	}
	
	/**
	 * Wiimoteに情報を要求します。
	 * 
	 * @author KawakawaRitsuki
	 * @version 0.1
	 */
	public void requestStatus(){
		def b = [0x15,0x00] as byte[];
		mWiimoteDev.setOutputReport(b[0],b , b.length);
	}
	
	/**
	 * Wiimoteを切断します。
	 * 現バージョンでは実装されていません。
	 * 
	 * @author KawakawaRitsuki
	 * @version 0.1
	 */
	@Deprecated
	public void disconnect(){
//		mDev.setOutputReport(arg0, arg1, arg2);
	}
	
	/**
	 * WiimoteのプレイヤーLEDを指定します。
	 * パラメーターには0と1で形成された4桁の文字列を指定します。
	 * 1で点灯、0で消灯します。
	 * 尚、プレイヤーLEDが1つの場合（バランスボード等）の場合は1つめの引数が適用されます。
	 * 
	 * @author KawakawaRitsuki
	 * @since 0.1
	 * @param led1 LED1を点灯させるか
	 * @param led2 LED1を点灯させるか
	 * @param led3 LED3を点灯させるか
	 * @param led4 LED4を点灯させるか
	 */
	public void setPlayerLed(boolean led1,boolean led2,boolean led3,boolean led4){
	    
		String led = (led4 ? "1" : "0") + (led3 ? "1" : "0") + (led2 ? "1" : "0") + (led1 ? "1" : "0")
	    def b = [0x11,Integer.parseInt(led+"0000",2)] as byte[];
		mWiimoteDev.setOutputReport(b[0],b , b.size());
		
	}
	
	/**
	 * Wiimoteのバイブレータを動作させます。
	 * 当メソッドは同期的に処理が行われます。
	 * 必要に応じて非同期的にメソッドを呼び出してください。
	 * 
	 * @author KawakawaRitsuki
	 * @since 0.1
	 * @param time 動作させる時間(ms)
	 */
	// TODO:今のLED状態を取得しないと1になってしまう。終了したタイミングで1になる。つまるところLEDと一緒に処理している。
	public void vibrate(long time){
		isChecked = false;
		requestStatus()
		while(!isChecked){}
		println led
		
		byte[] b = [0x11,Integer.parseInt(led + "0001",2)] as byte[];
		mWiimoteDev.setOutputReport(b[0],b , b.length);
		
		
		try {Thread.sleep(time);} catch (InterruptedException e) {}
		
		byte[] b2 = [0x11,Integer.parseInt(led + "0000",2)] as byte[];
		mWiimoteDev.setOutputReport(b2[0],b2 , b2.length);
	}
	
	
	
	/**
	 * Wiimoteのリポートモードを指定します。
	 * パラメータには定数を指定することをおすすめします。
	 * 
	 * @author KawakawaRitsuki
	 * @since 0.1
	 * @param mode リポートモード番号
	 */
	public void setReportMode(byte mode){
		byte[] b = [12,00,mode];
		mWiimoteDev.setOutputReport(b[0],b , b.length);
	}

	/**
	 * Wiimoteに接続されたヌンチャクを動作させる際に呼び出します。
	 * 
	 * @author KawakawaRitsuki
	 * @since 0.1
	 */
	public void setNunchuckData(){
		def b = [16,04,A4,00,40,01,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00] as byte[];
        
        mWiimoteDev.setOutputReport(b[0],b , b.length);
	}
	
	/**
	 * Wiimoteが切断された際に呼び出されるリスナを設定します。
	 * 
	 * @author KawakawaRitsuki
	 * @since 0.1
	 * @param listener 設定するリスナ
	 */
	public void setDeviceRemovalListener(DeviceRemovalListener listener){
		mWiimoteDev.setDeviceRemovalListener(listener);
	}
	
	/**
	 * Wiimoteからデータを受信した際に呼び出されるリスナを設定します。
	 * 
	 * @author KawakawaRitsuki
	 * @since 0.1
	 * @param listener 設定するリスナ
	 */
	public void setDeviceInputReportListener(DeviceInputReportListener listener){
		this.listener = listener;
	}
	private DeviceInputReportListener listener;
	
}

public interface DeviceInputReportListener extends EventListener {
    void onInputReport(HidDevice source, byte Id, byte[] b, int len);
}
