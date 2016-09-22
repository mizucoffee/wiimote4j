package net.mizucoffee.wiimote4j;

/**
 * WalkManager
 * WiiBalanceBoardの歩行の状態を保持、管理します。
 * 
 * @author KawakawaRitsuki
 * @version 0.1
 */

public class WalkManager {

	/** 重心が中央にある */
	public static final int BOARD_BOTH  = 0;
	/** 重心が左にある */
	public static final int BOARD_LEFT  = 1;
	/** 重心が右にある */
	public static final int BOARD_RIGHT = 2;
	/** 歩行判定閾値 */
	public static final int WALK_THRESHOLD = 10;

	public int boardState     = BOARD_BOTH;
	public int lastBoardState = BOARD_BOTH;
	
	private OnBoardStateChangeListener boardListener = null;
	
	/**
	 * 歩行状況を判断します。
	 * 任意のタイミングで呼び出すことができます。
	 * 右左の重量を引数として判断します。
	 * 
	 * @author KawakawaRitsuki
	 * @param right 右の重量
	 * @param left 左の重量
	 * @version 0.1
	 */
	public void checkWalkStatus(int right,int left){
		if (right > WALK_THRESHOLD && left <= WALK_THRESHOLD){
			if(boardState != BOARD_RIGHT && lastBoardState == BOARD_RIGHT){
				boardState = BOARD_RIGHT;
				boardListener.onChanged(boardState);
			}
			lastBoardState = BOARD_RIGHT;
		}else if(right <= WALK_THRESHOLD && left > WALK_THRESHOLD){
			if(boardState != BOARD_LEFT && lastBoardState == BOARD_LEFT){
				boardState = BOARD_LEFT;
				boardListener.onChanged(boardState);
			}
			lastBoardState = BOARD_LEFT;
		}else {
			if(Math.abs(right - left) >= 5){
				if(right > left){
					if(boardState != BOARD_RIGHT && lastBoardState == BOARD_RIGHT){
						boardState = BOARD_RIGHT;
						boardListener.onChanged(boardState);
					}
					lastBoardState = BOARD_RIGHT;
				}else{
					if(boardState != BOARD_LEFT && lastBoardState == BOARD_LEFT){
						boardState = BOARD_LEFT;
						boardListener.onChanged(boardState);
					}
					lastBoardState = BOARD_LEFT;
				}
			}else{
				if(boardState != BOARD_BOTH && lastBoardState == BOARD_BOTH){
					boardState = BOARD_BOTH;
					boardListener.onChanged(boardState);
				}
				lastBoardState = BOARD_BOTH;
			}
		}
	}
	
	/**
	 * WiiBalanceBoardの状態が変化した際に呼び出されるリスナを設定します。
	 * 
	 * @author KawakawaRitsuki
	 * @since 0.1
	 * @param listener 設定するリスナ
	 */
	public void setBoardStateChangeListener(OnBoardStateChangeListener listener){
        this.boardListener = listener;
    }
	
	/**
	 * リスナの登録を取り消します。
	 * 
	 * @author KawakawaRitsuki
	 * @since 0.1
	 */
	public void removeBoardStateChangeListener(){
        this.boardListener = null;
    }

}
