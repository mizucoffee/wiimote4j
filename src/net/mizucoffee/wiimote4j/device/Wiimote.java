package net.mizucoffee.wiimote4j.device;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	/**
	 * HidDeviceとしての接続情報が格納されています。
	 * 
	 * @author KawakawaRitsuki
	 * @version 0.1
	 */
	protected HidDevice mWiimoteDev;
	
	/**
	 * HidDeviceを代入するコンストラクタです。
	 * 
	 * @author KawakawaRitsuki
	 * @param info WiimoteのHidDeviceInfo
	 * @version 0.1
	 */
	public Wiimote(HidDeviceInfo info){
		try {
			mWiimoteDev = PureJavaHidApi.openDevice(info.getPath());
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
		return mWiimoteDev != null;
	}
	
	/**
	 * Wiimoteに情報を要求します。
	 * 
	 * @author KawakawaRitsuki
	 * @version 0.1
	 */
	public void requestStatus(){
		byte[] b = {0x15,0x00};
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
	 * 
	 * @author KawakawaRitsuki
	 * @since 0.1
	 * @param led LEDの情報
	 * @throws IllegalArgumentException パラメータが不正だった際にスローされます。
	 */
	public void setPlayerLed(String led){
		Pattern p = Pattern.compile("^[01]{4}$");
	    Matcher m = p.matcher(led);
	    
	    if(m.find()){
	    	byte[] b = {0x11,(byte) Integer.parseInt(new StringBuilder(led).reverse()+"0000",2)};
			mWiimoteDev.setOutputReport(b[0],b , b.length);
	    }else{
	    	throw new IllegalArgumentException();
	    }
		
	}
	
	/**
	 * Wiimoteのバイブレータを動作させます。
	 * 
	 * @author KawakawaRitsuki
	 * @since 0.1
	 * @param time 動作させる時間(ms)
	 */
	// TODO:今のLED状態を取得しないと1になってしまう。終了したタイミングで1になる。つまるところLEDと一緒に処理している。
	public void vibrate(long time){
		byte[] b = {0x11,(byte) Integer.parseInt("00010001",2)};
		mWiimoteDev.setOutputReport(b[0],b , b.length);
		
		try {Thread.sleep(time);} catch (InterruptedException e) {}
		
		byte[] b2 = {0x11,(byte) Integer.parseInt("00010000",2)};
		mWiimoteDev.setOutputReport(b2[0],b2 , b2.length);
	}
	
	public final static byte WIMOTE_ONLY_REPORT_MODE = 0x30;
	public final static byte WIMOTE_NUNCHUCK_REPORT_MODE = 0x34;
	
	/**
	 * Wiimoteのリポートモードを指定します。
	 * パラメータには定数を指定することをおすすめします。
	 * 
	 * @author KawakawaRitsuki
	 * @since 0.1
	 * @param mode リポートモード番号
	 */
	public void setReportMode(byte mode){
		byte[] b = {0x12,0x00,mode};
		mWiimoteDev.setOutputReport(b[0],b , b.length);
	}

	/**
	 * Wiimoteに接続されたヌンチャクを動作させる際に呼び出します。
	 * 
	 * @author KawakawaRitsuki
	 * @since 0.1
	 */
	public void setNunchuckData(){
		byte[] b = {0x16,0x04,(byte) 0xA4,0x00,0x40,0x01,0x00,0x00,0x00,0x00,0x00,
        0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
        
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
	public void setInputReportListener(InputReportListener listener){
		mWiimoteDev.setInputReportListener(listener);
	}
	
}
