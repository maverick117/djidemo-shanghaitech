package com.dji.getstart;

import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIError;
import dji.sdk.api.DJIDroneTypeDef.DJIDroneType;
import dji.sdk.interfaces.DJIGerneralListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends DemoBaseActivity
{
    private static final String TAG = "MainActivity";
    private int type = 0;
    
    private final int SHOWDIALOG = 2;
    
    private Handler handler = new Handler(new Handler.Callback() {
        
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SHOWDIALOG:
                    showMessage(getString(R.string.demo_activation_message_title),(String)msg.obj); 
                    break;

                default:
                    break;
            }
            return false;
        }
    });
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ListView mListView = (ListView)findViewById(R.id.listView); 
        
        Intent intent= getIntent();  
        type = intent.getIntExtra("DroneType", 0);
        Log.v("type","Type" + type);

        mListView.setAdapter(new DemoListAdapter());
        mListView.setOnItemClickListener(new OnItemClickListener() {  
            public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {  
                onListItemClick(index);
            }  
        });  
        
        onInitSDK(type);
        
        new Thread(){
            public void run() {
                try {
                    DJIDrone.checkPermission(getApplicationContext(), new DJIGerneralListener() {
                        
                        @Override
                        public void onGetPermissionResult(int result) {
                            // TODO Auto-generated method stub
                            Log.e(TAG, "onGetPermissionResult = "+result);
                            Log.e(TAG, "onGetPermissionResultDescription = "+DJIError.getCheckPermissionErrorDescription(result));
                            if (result == 0) {
                                handler.sendMessage(handler.obtainMessage(SHOWDIALOG, DJIError.getCheckPermissionErrorDescription(result)));
                            } else {
                                handler.sendMessage(handler.obtainMessage(SHOWDIALOG, getString(R.string.demo_activation_error)+DJIError.getCheckPermissionErrorDescription(result)+"\n"+getString(R.string.demo_activation_error_code)+result));
                        
                            }
                        }
                    });
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();
        
        
    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }
       
    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        onUnInitSDK();
        super.onDestroy();
    }
    
    private void onInitSDK(int type){
        switch(type){
            case 0 : {
                DJIDrone.initWithType(this.getApplicationContext(),DJIDroneType.DJIDrone_Vision);
                break;
            }
            case 1 : {
                DJIDrone.initWithType(this.getApplicationContext(),DJIDroneType.DJIDrone_Inspire1);
                break;
            }
            case 2 : {
                DJIDrone.initWithType(this.getApplicationContext(),DJIDroneType.DJIDrone_Phantom3_Advanced);
                break;
            }
            case 3 : {
                DJIDrone.initWithType(this.getApplicationContext(),DJIDroneType.DJIDrone_M100);
                break;
            }
            default : {
                break;
            }
        }
        
        DJIDrone.connectToDrone();
    }
    
    private void onUnInitSDK(){
        DJIDrone.disconnectToDrone();
    }
    
    private void onListItemClick(int index) {
        Intent intent = null;
        intent = new Intent(MainActivity.this, demos[index].demoClass);
        this.startActivity(intent);
    }
    
    private static final DemoInfo[] demos = {
        new DemoInfo(R.string.demo_title_gs_protocol,R.string.demo_desc_gs_protocol, CameraProtocolDemoActivity.class),
       
        
    };

    private  class DemoListAdapter extends BaseAdapter {
        public DemoListAdapter() {
            super();
        }

        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            convertView = View.inflate(MainActivity.this, R.layout.demo_info_item, null);
            TextView title = (TextView)convertView.findViewById(R.id.title);
            TextView desc = (TextView)convertView.findViewById(R.id.desc);

            title.setText(demos[index].title);
            desc.setText(demos[index].desc);
            return convertView;
        }
        @Override
        public int getCount() {
            return demos.length;
        }
        @Override
        public Object getItem(int index) {
            return  demos[index];
        }

        @Override
        public long getItemId(int id) {
            return id;
        }
    }
    
   private static class DemoInfo{
        private final int title;
        private final int desc;
        private final Class<? extends android.app.Activity> demoClass;

        public DemoInfo(int title , int desc,Class<? extends android.app.Activity> demoClass) {
            this.title = title;
            this.desc  = desc;
            this.demoClass = demoClass;
        }
    }
   
   /** 
    * @Description : RETURN BTN RESPONSE FUNCTION
    * @author      : andy.zhao
    * @param view 
    * @return      : void
    */
   public void onReturn(View view){
       Log.d(TAG ,"onReturn");  
       this.finish();
   }
   
   public void showMessage(String title, String msg) {
       AlertDialog.Builder builder = new AlertDialog.Builder(this);
       builder.setTitle(title)
               .setMessage(msg)
               .setCancelable(false)
               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialog.cancel();
                   }
               });
       AlertDialog alert = builder.create();
       alert.show();
   }
   
}
