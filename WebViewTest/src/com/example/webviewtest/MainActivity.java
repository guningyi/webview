package com.example.webviewtest;


import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

	 /** Called when the activity is first created. */
	WebView wv;
	ProgressDialog pd;
	Handler handler;
	    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();//执行初始化函数
        loadurl(wv,"http://www.baidu.com");
        handler=new Handler(){
        	public void handleMessage(Message msg)
    	    {//定义一个Handler，用于处理下载线程与UI间通讯
    	      if (!Thread.currentThread().isInterrupted())
    	      {
    	        switch (msg.what)
    	        {
    	        case 0:
    	        	pd.show();//显示进度对话框        	
    	        	break;
    	        case 1:
    	        	pd.hide();//隐藏进度对话框，不可使用dismiss()、cancel(),否则再次调用show()时，显示的对话框小圆圈不会动。
    	        	break;
    	        }
    	      }
    	      super.handleMessage(msg);
    	    }
        };
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	   public void init(){//初始化
	    	wv=(WebView)findViewById(R.id.wv);
	        wv.getSettings().setJavaScriptEnabled(true);//可用JS
	        wv.setScrollBarStyle(0);//滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
	        wv.setWebViewClient(new WebViewClient(){   
	            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
	            	loadurl(view,url);//载入网页
	                return true;   
	            }//重写点击动作,用webview载入
	 
	        });
	        wv.setWebChromeClient(new WebChromeClient(){
	        	public void onProgressChanged(WebView view,int progress){//载入进度改变而触发 
	             	if(progress==100){
	            		handler.sendEmptyMessage(1);//如果全部载入,隐藏进度对话框
	            	}   
	                super.onProgressChanged(view, progress);   
	            }   
	        });
	 
	    	pd=new ProgressDialog(MainActivity.this);
	        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        pd.setMessage("数据载入中，请稍候！");
	    }
	    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回键
	        if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {   
	            wv.goBack();   
	            return true;   
	        }else if(keyCode == KeyEvent.KEYCODE_BACK){
	        	ConfirmExit();//按了返回键，但已经不能返回，则执行退出确认
	        	return true; 
	        }   
	        return super.onKeyDown(keyCode, event);   
	    }
	    public void ConfirmExit(){//退出确认
	    	AlertDialog.Builder ad=new AlertDialog.Builder(MainActivity.this);
	    	ad.setTitle("退出");
	    	ad.setMessage("是否退出软件?");
	    	ad.setPositiveButton("是", new DialogInterface.OnClickListener() {//退出按钮
				@Override
				public void onClick(DialogInterface dialog, int i) {
					// TODO Auto-generated method stub
					MainActivity.this.finish();//关闭activity
	 
				}
			});
	    	ad.setNegativeButton("否",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int i) {
					//不退出不用执行任何操作
				}
			});
	    	ad.show();//显示对话框
	    }
	    public void loadurl(final WebView view,final String url){
	    	new Thread(){
	        	public void run(){
	        		handler.sendEmptyMessage(0);
	        		view.loadUrl(url);//载入网页
	        	}
	        }.start();
	    }

}
