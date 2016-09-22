package net.mizucoffee.wiimote4j;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * MouseManager
 * ネイティブAPIを呼び出してマウスを操作します。
 * 
 * @author KawakawaRitsuki
 * @since 0.1
 */
public interface MouseManager extends Library {
	  MouseManager INSTANCE = (MouseManager) Native.loadLibrary("move", MouseManager.class);
	  void move(double width,double height,double dx,double dy);
	  void press(double height,boolean left);
	  void release(double height,boolean left);
}