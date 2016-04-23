package com.example.baidu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Uri uri;
	private Button take;
	private Button select;
	private ImageView photo;
	private File f;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		take=(Button)findViewById(R.id.take_photo);
		select=(Button)findViewById(R.id.select_photo);
		photo=(ImageView)findViewById(R.id.photo);
		f=new File(Environment.getExternalStorageDirectory(),"x.jpg");
	}
	public void click(View view)
	{
		if(view.getId()==R.id.take_photo)
		{
			
			if(f.exists())
			{	
				try {
					f.delete();
					f.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			uri=Uri.fromFile(f);
			Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(intent, 0);
			return;
		}
		Intent intent=new Intent("android.intent.action.GET_CONTENT");
		intent.setType("image/*");
		startActivityForResult(intent, 2);
	}
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 switch(requestCode)
		 {
		 case 0:
			 if(resultCode==RESULT_OK)
			 {
				 Intent intent=new Intent("com.android.camera.action.CROP");
				 intent.setDataAndType(uri, "image/*");
				 intent.putExtra("scale",true);
				 intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				 startActivityForResult(intent, 1);
				 
			 }
			 break;
		 case 1:
			 if(resultCode==RESULT_OK)
			 {
				 Bitmap bitmap;
				 bitmap=BitmapFactory.decodeFile(f.getPath());
				 photo.setImageBitmap(bitmap);
			 }
			 break;
		 case 2:
			 if(resultCode==RESULT_OK)
			 {
				 if(Build.VERSION.SDK_INT>=19)
					 handleImageonKitKat(data);
				 else
					 handleImagebeforeKitKat(data);
			 }
		 }
	 }
	 private void handleImageonKitKat(Intent data)
	 {
		 String imagepath=null;
		 Uri uri=data.getData();
		 if(DocumentsContract.isDocumentUri(this, uri))
		 {
			 String docId=DocumentsContract.getDocumentId(uri);
			 if("com.android.providers.media.documents".equals(uri.getAuthority()))
			 {
				 String id=docId.split(":")[1];
				 String select=MediaStore.Images.Media._ID+"="+id;
				 imagepath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, select);
			 }
			 else
			 {
				 Uri contenturi=ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
				 imagepath=getImagePath(contenturi, null);
			 }
		 }
		 else
		 {
			 imagepath=getImagePath(uri, null);
		 }
		 displayImage(imagepath);
	 }
	 private void handleImagebeforeKitKat(Intent data)
	 {
		 Uri uri=data.getData();
		 displayImage(getImagePath(uri,null));
	 }
	 private String getImagePath(Uri uri,String select)
	 {
		 String path=null;
		 Cursor cursor=getContentResolver().query(uri,null, select, null, null);
		 if(cursor!=null)
		 {
			 if(cursor.moveToFirst())
			 {
				 path=cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA));
			 }
			 cursor.close();
		 }
		 return path;
		 
	 }
	 private void displayImage(String imagepath)
	 {
		 if(imagepath!=null)
		 {
			 Bitmap bitmap=BitmapFactory.decodeFile(imagepath);
			 photo.setImageBitmap(bitmap);
			 return;
		 }
		 Toast.makeText(this, "no photo",Toast.LENGTH_SHORT).show();
	 }
}
