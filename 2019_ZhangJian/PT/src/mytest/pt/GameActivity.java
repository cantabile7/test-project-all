package mytest.pt;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {
	// �������������Ϣ��TAG
	public static final String TAG = "PT_GAME";
	// �����洢����ͨ��������ֿ��ҵ�SharedPreferences����
	public static final String PREFS_STRING = "PT_GAME_PROGRESS";
	// ��Ϸ��������Ҫ����GameView
	private GameView myView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_game);

		// ��ȥ����״̬��
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// ��ȥ���������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// ������Ϸ�������ݸ�GameView����
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
	 * ������ǰ�������Ϸ����
	 */
	private void loadGameProgress() {
		// ���
		myView.cellStates.clear();
		try {
			SharedPreferences settings = getSharedPreferences(PREFS_STRING,	MODE_PRIVATE);
			String progress = settings.getString("PROGRESS", "");
			String[] states = progress.split("[#]");
			for (String one : states) {
				String[] props = one.split("[|]");
				// ����һ��PuzzCellState����
				PuzzCellState pcs = new PuzzCellState();
				pcs.imgId = Integer.parseInt(props[0]);
				pcs.posx = Integer.parseInt(props[1]);
				pcs.posy = Integer.parseInt(props[2]);
				pcs.zOrder = Integer.parseInt(props[3]);
				pcs.fixed = Boolean.parseBoolean(props[4]);
				// ���뵽cellStates������
				myView.cellStates.add(pcs);
			}
		}
		catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}
	/**
	 * ���浱ǰ��Ϸ����
	 */
	private void saveGameProgress() {
		SharedPreferences settings = getSharedPreferences(PREFS_STRING, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		String progress = "";
		for (PuzzleCell cell : myView.puzzCells) {
			// ÿ��ƴͼ��״̬��Ϣת����һ���ַ�����״̬֮����"|"����
			String s = String.format("%d|%d|%d|%d|%s", 
							cell.imgId, cell.rect.left, cell.rect.top, cell.zOrder, 
							Boolean.toString(cell.fixed));
			// ƴͼ��֮����"#"����
			progress = progress + s + "#";
		}
		// ������ƴͼ���״̬��������
		editor.putString("PROGRESS", progress);
		editor.commit();
	}
}
