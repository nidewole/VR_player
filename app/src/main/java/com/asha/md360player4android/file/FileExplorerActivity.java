package com.asha.md360player4android.file;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.asha.md360player4android.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

public class FileExplorerActivity extends ListActivity {

	private static final String TAG = "FFMpegFileExplorer";
	private String mCurrentPath=null;
	private String 			mRoot = "/sdcard";
	private TextView 		mTextViewLocation;
	private File[]			mFiles;
	private ArrayList<File> data = null ;
	private static final String[] VIDEO_EXTS=new String[] {".mp4" };
	private static final String[] AUDIO_EXTS=new String[] { ".mp3",".aac","wav"};
	private static final HashSet<String> mHashVideo;
	static {
		mHashVideo = new HashSet<String>(Arrays.asList(VIDEO_EXTS));
	}

	
	private static boolean isSelectVideo=true;
	private Button btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_explore_layout);
		mTextViewLocation = (TextView) findViewById(R.id.textview_path);
		
		String mode=getIntent().getStringExtra("SELECT_MODE");
		if(mode.equals("video")){
			setTitle("选择视频文件");
			isSelectVideo=true;
		}else{
			setTitle("选择音频文件");
			isSelectVideo=false;
		}
		getDirectory(mRoot);





		btn = (Button) findViewById(R.id.btn_mouse);
		btn.setOnGenericMotionListener(new View.OnGenericMotionListener() {

			@Override
			public boolean onGenericMotion(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				int what = event.getButtonState();
				switch (what) {
					case MotionEvent.ACTION_DOWN:
						System.out.println("按下̬");
						break;
					case MotionEvent.BUTTON_PRIMARY:
						Toast.makeText(FileExplorerActivity.this, "primary_主键", Toast.LENGTH_LONG).show();
						break;
					case MotionEvent.BUTTON_TERTIARY:
						Toast.makeText(FileExplorerActivity.this, "tertiary_三级", Toast.LENGTH_LONG).show();
						break;
					case MotionEvent.BUTTON_SECONDARY:
						Toast.makeText(FileExplorerActivity.this, "secondary_第二个", Toast.LENGTH_LONG).show();
						break;
				}
				return false;
			}
		});
		
		
		
	}
	 @Override
	public void onBackPressed() {
		 if(mCurrentPath!=null && !mCurrentPath.equals(mRoot)){
			 File f = new File(mCurrentPath);
			 mCurrentPath=f.getParent();
			 getDirectory(mCurrentPath);
			 return ;
		 }
		 super.onBackPressed();
	}
	protected static boolean checkExtension(File file) {
		if(isSelectVideo){
			for(int i=0;i<VIDEO_EXTS.length;i++) {
				if(file.getName().indexOf(VIDEO_EXTS[i]) > 0) {
					return true;
				}
			}
		}else{
			for(int i=0;i<AUDIO_EXTS.length;i++) {
				if(file.getName().indexOf(AUDIO_EXTS[i]) > 0) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void sortFilesByDirectory(File[] files) {
		Arrays.sort(files, new Comparator<File>() {

			public int compare(File f1, File f2) {
				return Long.valueOf(f1.length()).compareTo(f2.length());
			}
		});
	}

	private void getDirectory(String dirPath) {
		try {
			mTextViewLocation.setText("Location: " + dirPath);
			data = new ArrayList<>();
			File f = new File(dirPath);
			File[] temp = f.listFiles();

			sortFilesByDirectory(temp);
			
			File[] files = null;
			if(!dirPath.equals(mRoot)) {
				files = new File[temp.length + 1];
				System.arraycopy(temp, 0, files, 1, temp.length);
				files[0] = new File(f.getParent());
			} else {
				files = temp;
			}


			mFiles = files;
			setListAdapter(new FileExplorerAdapter(this, files, temp.length == files.length));
		} catch(Exception ex) {
			Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
		}
	}
	//收索所有media文件
	private void searchAllMedias(File file , ArrayList list) {

		if (file != null && file.exists() && file.isDirectory()) {

			File[] files = file.listFiles();

			if (files != null) {

				for (File f : files) {

					if (f.isDirectory()) {

						searchAllMedias(f , list);

					} else if (f.exists() && f.canRead() && isVideo(f)) {


						list.add(f);

					}

				}

			}
		}
	}

	public boolean isVideo(File f) {

		String ext = getFileExtension(f);

		return mHashVideo.contains(ext);

	}

	private String getFileExtension(File f) {

		if (f != null) {

			String filename = f.getName();

			int i = filename.lastIndexOf("\\.");

			if (i > 0 && i < filename.length() - 1) {

				return filename.substring(i + 1).toLowerCase();

			}

			return null;

		}
		return  null;
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) { //当list里的一个item被点击的时候调用
		File file = mFiles[position];
		mCurrentPath=file.getAbsolutePath();
		if (file.isDirectory()) {
			if (file.canRead())
				getDirectory(mCurrentPath);
			else {
				Toast.makeText(this, "[" + file.getName() + "] folder can't be read!", Toast.LENGTH_LONG).show();
			}
		} else {
			if(!checkExtension(file)) {
				Toast.makeText(this, "Not Support This File extensions!", Toast.LENGTH_LONG).show();
				return;
			}else if(!TextUtils.isEmpty(file.getAbsolutePath())){
				startPlayer(file.getAbsolutePath());
			}else{
				Toast.makeText(this, "Not Support This File extensions!", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	private void startPlayer(String filePath) {
		Intent i = new Intent(); ///这里发送出去，
	    Bundle b = new Bundle();
	    b.putString("SELECT_VIDEO", filePath);
	    i.putExtras(b);
	    this.setResult(RESULT_OK, i);
	    this.finish();
	    
//    	Intent i = new Intent(this, VideoPlayer.class);
//    	i.putExtra("videofilename", filePath);
//    	startActivity(i);  
    }


}
