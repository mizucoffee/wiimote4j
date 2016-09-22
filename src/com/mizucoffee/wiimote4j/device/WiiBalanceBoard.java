package com.mizucoffee.wiimote4j.device;

import java.io.IOException;
import java.util.List;

import purejavahidapi.DeviceRemovalListener;
import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.InputReportListener;
import purejavahidapi.PureJavaHidApi;

/**
 * WiiBalanceBoard
 * WiiBalanceBoardとの通信用クラス
 * 
 * @author KawakawaRitsuki
 * @version 0.1
 */

public class WiiBalanceBoard extends Wiimote{
	
	public WiiBalanceBoard(HidDeviceInfo info) {
		super(info);
	}
	
	/**
	 * WiiBalanceBoardのプレイヤーLEDを指定します。
	 * Wiimoteクラスとは違いbooleanをパラメータに指定します。
	 * trueで点灯、falseで消灯します。
	 * 
	 * @author KawakawaRitsuki
	 * @since 0.1
	 * @param flag 点灯消灯の情報
	 */
	public void setPlayerID(boolean flag){
		String data;
		if(flag){
			data = "1000";
		}else{
			data = "0000";
		}
		byte[] b = {0x11,(byte) Integer.parseInt(new StringBuilder(data).reverse()+"0000",2)};
		mWiimoteDev.setOutputReport(b[0],b , b.length);
	}
}
