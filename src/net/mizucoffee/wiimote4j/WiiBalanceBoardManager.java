package net.mizucoffee.wiimote4j;

import purejavahidapi.HidDevice;
import purejavahidapi.InputReportListener;

public class WiiBalanceBoardManager implements InputReportListener {

    private int cabRf = 0;
    private int cabRb = 0;
    private int cabLf = 0;
    private int cabLb = 0;

    private int rf = 0;
    private int rb = 0;
    private int lf = 0;
    private int lb = 0;

    private OnBoardDataChangedListener listener;

    @Override
    public void onInputReport(HidDevice hidDevice, byte b, byte[] bytes, int i) {
        if (Byte.toUnsignedInt(bytes[3]) == 8){
            set0kg( Byte.toUnsignedInt(bytes[3]),
                    Byte.toUnsignedInt(bytes[5]),
                    Byte.toUnsignedInt(bytes[7]),
                    Byte.toUnsignedInt(bytes[9]));
            return;
        }

        if(cabRf == 0 && cabRb == 0 && cabLf == 0 && cabLb == 0) set0kg(
                Byte.toUnsignedInt(bytes[3]),
                Byte.toUnsignedInt(bytes[5]),
                Byte.toUnsignedInt(bytes[7]),
                Byte.toUnsignedInt(bytes[9]));

        rf = Byte.toUnsignedInt(bytes[3]) - cabRf;
        rb = Byte.toUnsignedInt(bytes[5]) - cabRb;
        lf = Byte.toUnsignedInt(bytes[7]) - cabLf;
        lb = Byte.toUnsignedInt(bytes[9]) - cabLb;


        if(listener != null) listener.onChanged(rf,rb,lf,lb);

    }

    public void getStatus(){
        if(listener != null) listener.onChanged(rf,rb,lf,lb);
    }

    private void set0kg(int rf,int rb,int lf,int lb){
        cabRf = rf;
        cabRb = rb;
        cabLf = lf;
        cabLb = lb;
    }

    public void setOnBoardDataChangedListener(OnBoardDataChangedListener l){
        this.listener = l;
    }
    public void removeOnBoardDataChangedListener(){
        this.listener = null;
    }
}