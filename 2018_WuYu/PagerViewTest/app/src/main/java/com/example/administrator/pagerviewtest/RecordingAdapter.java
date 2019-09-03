package com.example.administrator.pagerviewtest;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.pagerviewtest.bean.RecordingItem;
import com.example.administrator.pagerviewtest.fragments.PlaybackFragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.example.administrator.pagerviewtest.MainActivity.FOLDER_PATH;

public class RecordingAdapter extends RecyclerView.Adapter<RecordingAdapter.ViewHolder>
        implements OnDatabaseChangedListener {

    private static final SimpleDateFormat mAddedDateFormatter =
            new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
//    private static final SimpleDateFormat mLengthFormatter =
//            new SimpleDateFormat("mm:ss", Locale.getDefault());

    private RecyclerView mRecyclerView;

    private ArrayList<RecordingItem> mList;
    private Context mContext;
    private DBHelper mDBHelper;
    private RecordingItem item;

    private Vibrator mVibrator=null;  //声明一个振动器对象

    public RecordingAdapter(Context context, RecyclerView recyclerView) {
        super();
        mContext = context;
        mRecyclerView = recyclerView;
        mDBHelper = new DBHelper(mContext);
        mDBHelper.setOnDatabaseChangedListener(this);
        mList = mDBHelper.getAllItem();
    }
    // 初始化子项item控件
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView recordingName;
        TextView recordingLength;
        TextView recordingAddedDate;
        View cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_view);
            recordingName = itemView.findViewById(R.id.recording_name_text);
            recordingLength = itemView.findViewById(R.id.recording_length_text);
            recordingAddedDate = itemView.findViewById(R.id.recording_added_date_text);
        }
    }
    // 创建子项item的布局
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view, parent, false);

        mContext = parent.getContext();

        return new ViewHolder(itemView);
    }
    // 给控件设置数据
    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        item = getItem(i);

        long itemDuration = item.getLength();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration)
                - TimeUnit.MINUTES.toSeconds(minutes);

        holder.recordingName.setText(item.getName());
        holder.recordingLength.setText(String.format("%02d:%02d", minutes, seconds));
        holder.recordingAddedDate.setText(mAddedDateFormatter.format(item.getTime()));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlaybackFragment playbackFragment = new PlaybackFragment()
                            .newInstance(getItem(holder.getAdapterPosition()));

                    FragmentTransaction transaction = ((FragmentActivity) mContext)
                            .getSupportFragmentManager()
                            .beginTransaction();

                    playbackFragment.show(transaction, "dialog_playback");
                } catch (Exception e ) {
                    e.printStackTrace();
                }
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CharSequence[] options = {"Rename", "Delete", "Share"};
//                AlertDialog.Builder deleteDialog =
                //获取服务
                if(Volume.isVibrate){
                    mVibrator = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
                    mVibrator.vibrate(new long[]{0,100}, -1);
                }
                new AlertDialog.Builder(mContext)
                        .setTitle("Options")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        createRenameFileDialog(holder.getAdapterPosition());
                                        break;
                                    case 1:
                                        createDeleteFileDialog(holder.getAdapterPosition());
                                        break;
                                    case 2:
                                        shareFile(holder.getAdapterPosition());
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                        .setCancelable(true)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

                return false;
            }
        });
    }

    private void createRenameFileDialog (final int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_rename_file, null);

        final EditText input = (EditText) view.findViewById(R.id.new_file_name);

        new AlertDialog.Builder(mContext)
                .setTitle("Rename")
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String fileName = input.getText().toString().trim() + ".3gp";
                        renameItem(position, fileName);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setView(view)
                .show();
    }

    private void createDeleteFileDialog(final int position) {
        new AlertDialog.Builder(mContext)
                .setTitle("Delete")
                .setMessage("Are you sure you would like to delete this file?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeItem(position);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void shareFile(int position) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.addCategory("android.intent.category.DEFAULT");
        // 授予Uri授予临时权限
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        File file = new File(getItem(position).getFilePath());
        Uri fileUri = FileProvider.getUriForFile(mContext,
                "com.example.administrator.pagerviewtest.fileprovider",
                file);
//        String fileType = mContext.getContentResolver().getType(fileUri);
        shareIntent.setDataAndType(fileUri, "video/3gpp");
//        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
//        shareIntent.setType("video/3gpp");
        try {
            mContext.startActivity(Intent.createChooser(shareIntent, "Share to..."));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void removeItem(int position) {
        item = getItem(position);
        File file = new File(item.getFilePath());
        file.delete();

        Toast.makeText(mContext, item.getName() + "successfully deleted",
                Toast.LENGTH_SHORT);

        mDBHelper.removeItemById(item.getId());
        mList.remove(item);
        notifyItemRemoved(position);
        Toast.makeText(mContext, "deleted successfully", Toast.LENGTH_SHORT).show();
    }

    private void renameItem(int position, String fileName) {
        item = getItem(position);
        String mFilePath = FOLDER_PATH + "/" + fileName;
        File file = new File(mFilePath);

        if (file.exists() && !file.isDirectory()) {
            Toast.makeText(mContext, "The file " + fileName + " already exists. Please choose " +
                    "a different file name", Toast.LENGTH_SHORT).show();
        } else {
            File oldFile = new File(item.getFilePath());
            oldFile.renameTo(file);
            // 更新数据库
            mDBHelper.renameItem(item, fileName, mFilePath);
            // 更新列表
            mList.set(position, mDBHelper.getItemAt(position));
//            item.setName(fileName);
//            item.setFilePath(mFilePath);
            notifyItemChanged(position);
            Toast.makeText(mContext, "renamed successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public RecordingItem getItem(int position) {
//        return mDBHelper.getItemAt(position);
        return mList.get(position);
    }

    @Override
    public int getItemCount() {
//        return mDBHelper.getCount();
        return mList.size();
    }

    @Override
    public void onAddNewItem(RecordingItem item) {
        mList.add(item);
        notifyItemInserted(getItemCount() - 1);
        mRecyclerView.scrollToPosition(getItemCount() - 1);
    }

}
