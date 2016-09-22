package net.mizucoffee.wiimote4j.device;

import java.io.IOException;
import java.util.List;

import purejavahidapi.HidDevice;
import purejavahidapi.HidDeviceInfo;
import purejavahidapi.InputReportListener;
import purejavahidapi.PureJavaHidApi;

/**
 * DeviceConnector
 * Wii周辺機器の接続を補助するクラス
 * 
 * @author KawakawaRitsuki
 * @version 0.1
 */
public class DeviceConnector {
	
	private HidDeviceInfo devInfo;
	private int device;
	
	public static final int WIIMOTE          = 0;
	public static final int WIIBALANCE_BOARD = 1;

	/**
	 * 機器に接続します。
	 * 引数timeoutが0の場合タイムアウトしません。
	 * 引数deviceIdに代入されているデバイスに接続します。
	 * 
	 * @author KawakawaRitsuki
	 * @param timeout タイムアウト時間を指定します。
	 * @param deviceId デバイスを指定します。
	 * @return 接続成功失敗
	 * @version 0.1
	 */
	public boolean connect(int timeout,int deviceId){
		if (devInfo == null){
			List<HidDeviceInfo> devList;
			devInfo = null;
			if (deviceId == WIIMOTE){
				System.out.println("Searching now...\n\nPlease push wiimote 1 & 2 button.");
			}else if(deviceId == WIIBALANCE_BOARD){
				System.out.println("Searching now...\n\nPlease push balance board power button.");
			}else{
				return false;
			}
			
			device: for(int i = 0;timeout == 0 || i < timeout;i++){
				devList = PureJavaHidApi.enumerateDevices();
				for (HidDeviceInfo info : devList) {
					if (info.getVendorId() == (short) 0x057E && info.getProductId() == (short) 0x0306) {
						devInfo = info;
						
						if(info.getProductString().equals("Nintendo RVL-CNT-01")){
							if(deviceId == WIIMOTE){
								device = WIIMOTE;
								break device;
							}
						}else if(info.getProductString().equals("Nintendo RVL-WBC-01")){
							if(deviceId == WIIBALANCE_BOARD){
								device = WIIBALANCE_BOARD;
								break device;
							}
						}else{
							devInfo = null;
						}
					}
				}
				try { Thread.sleep(1000); } catch (InterruptedException e) {}
			}
			
			if (devInfo == null){
				System.err.println("Device not found.");
				return false;
			} else {
				System.out.println("Found device.");
				if(device == WIIMOTE)
					System.out.println("This device is Wiimote.");
				else if(device == WIIBALANCE_BOARD)
					System.out.println("This device is WiiBalanceBoard");
				return true;
			}
		}else{
			System.err.println("Already connected.");
			return true;
		}
	}
	
	/**
	 * 機器が接続されているかを返します。
	 * 
	 * @author KawakawaRitsuki
	 * @return 接続されているかどうか
	 * @version 0.1
	 */
	public boolean isConnected(){
		return devInfo != null;
	}
	
	/**
	 * 機器がどの機器かを返します
	 * 
	 * @author KawakawaRitsuki
	 * @return 機器の定数
	 * @version 0.1
	 */
	public int getDeviceType(){
		return device;
	}
	
	/**
	 * HidDeviceInfoを返します。
	 * 
	 * @author KawakawaRitsuki
	 * @return devInfo 接続されているHidDeviceInfoを返します
	 * @version 0.1
	 */
	public HidDeviceInfo getDevInfo(){
		return devInfo;
	}
}