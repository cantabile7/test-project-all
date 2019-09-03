package mytest.pt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

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

//		// ��ȥ����״̬��
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		// ��ȥ���������
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// ������Ϸ�������ݸ�GameView����
		myView = new GameView(this);
		loadGameProgress();
		setContentView(myView);



		myView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {

					AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
					builder.setTitle("��ѡ��Ҫִ�еĲ���");
					final String[] items = new String[] {"���¿�ʼ���ؿ�", "�ָ���ʼ�ؿ�"};
					builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							switch (i){
								case 0:
									//���¿�ʼ���ؿ�
									myView.cellStates.clear();
									myView.puzzCells.clear();
									myView.makePuzzCells();
									myView.restart();
									Toast.makeText(getApplicationContext(),"�����¿�ʼ���ؿ�",Toast.LENGTH_SHORT).show();
									dialogInterface.dismiss();
									break;
								case 1:
									//�ָ���ʼ�ؿ�
									myView.cellStates.clear();
									myView.puzzCells.clear();
									myView.col = 4;
									myView.row = 3;
									myView.initGame();
									myView.makePuzzCells();
									myView.restart();
									Toast.makeText(getApplicationContext(),"�ѻָ���ʼ�ؿ�",Toast.LENGTH_SHORT).show();
									dialogInterface.dismiss();
									break;
							}
						}
					});
					builder.show();

				return false;
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		if(Control.isBgm == false){

		}
		else if(!Control.player.isPlaying()){
			Control.player.start();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// ��ͣ����
//		if(Control.player.isPlaying()){
//			Control.player.pause();
//		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// ֹͣ���ţ��ͷ���Դ
//		if(Control.player.isPlaying()){
//			Control.player.stop();
//		}
//		Control.player.release();


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
				myView.row = Integer.parseInt(props[5]);
				myView.col = Integer.parseInt(props[6]);
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
			String s = String.format("%d|%d|%d|%d|%s|%d|%d",
							cell.imgId, cell.rect.left, cell.rect.top, cell.zOrder, 
							Boolean.toString(cell.fixed), myView.row, myView.col);
			// ƴͼ��֮����"#"����
			progress = progress + s + "#";
		}
		// ������ƴͼ���״̬��������
		editor.putString("PROGRESS", progress);
		editor.commit();
	}
}
