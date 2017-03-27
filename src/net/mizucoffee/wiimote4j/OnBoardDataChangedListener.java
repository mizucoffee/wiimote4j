package net.mizucoffee.wiimote4j;

/**
 * Created by mizucoffee on 3/27/17.
 */
public interface OnBoardDataChangedListener {
    void onChanged(int rf,int rb,int lf,int lb);
}
