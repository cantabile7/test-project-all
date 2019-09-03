package mytest.pt;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {
	// 用于输出调试信息的TAG
	public static final String TAG = "PT_GAME";
	// 给定存储名，通过这个名字可找到SharedPreferences对象
	public static final String PREFS_STRING = "PT_GAME_PROGRESS";
	// 游戏进度数据要传给GameView
	private GameView myView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_game);

		// 隐去顶部状态栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 隐去程序标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// 加载游戏进度数据给GameView对象
		myView = new GameView(this);
		loadGameProgress();
		setContentView(myView);
	}

	@Override
	protected void onStop() {
		super.onStop();
		//
		saveGameProgress();
	}
	/**
	 * 加载以前保存的游戏进度
	 */
	private void loadGameProgress() {
		// 清空
		myView.cellStates.clear();
		try {
			SharedPreferences settings = getSharedPreferences(PREFS_STRING,	MODE_PRIVATE);
			String progress = settings.getString("PROGRESS", "");
			String[] states = progress.split("[#]");
			for (String one : states) {
				String[] props = one.split("[|]");
				// 构造一个PuzzCellState对象
				PuzzCellState pcs = new PuzzCellState();
				pcs.imgId = Integer.parseInt(props[0]);
				pcs.posx = Integer.parseInt(props[1]);
				pcs.posy = Integer.parseInt(props[2]);
				pcs.zOrder = Integer.parseInt(props[3]);
				pcs.fixed = Boolean.parseBoolean(props[4]);
				// 加入到cellStates数组中
				myView.cellStates.add(pcs);
			}
		}
		catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}
	/**
	 * 保存当前游戏进度
	 */
	private void saveGameProgress() {
		SharedPreferences settings = getSharedPreferences(PREFS_STRING, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		String progress = "";
		for (PuzzleCell cell : myView.puzzCells) {
			// 每个拼图块状态信息转换成一个字符串，状态之间用"|"连接
			String s = String.format("%d|%d|%d|%d|%s", 
							cell.imgId, cell.rect.left, cell.rect.top, cell.zOrder, 
							Boolean.toString(cell.fixed));
			// 拼图块之间用"#"连接
			progress = progress + s + "#";
		}
		// 将所有拼图块的状态保存起来
		editor.putString("PROGRESS", progress);
		editor.commit();
	}
}
