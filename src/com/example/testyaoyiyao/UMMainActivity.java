package com.example.testyaoyiyao;

import java.util.ArrayList;
import java.util.List;

import com.umeng.scrshot.adapter.UMAppAdapter;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.UMSsoHandler;
import com.umeng.socialize.controller.UMWXHandler;
import com.umeng.socialize.sensor.UMSensor.OnSensorListener;
import com.umeng.socialize.sensor.UMSensor.WhitchButton;
import com.umeng.socialize.sensor.controller.UMShakeService;
import com.umeng.socialize.sensor.controller.impl.UMShakeServiceFactory;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.widget.Toast;

public class UMMainActivity extends Activity {
	 // 声明mShakeController, 参数1为sdk 控制器描述符
		private UMShakeService mShakeController = UMShakeServiceFactory
		                              .getShakeService("摇一摇控制器");
		private UMSocialService mSocialController = UMServiceFactory.getUMSocialService("com.umeng.share",
				RequestType.SOCIAL);
		
		// 添加QQ支持, 并且设置QQ分享内容的target url
		
		private String contentUrl = "http://u.3gtv.net";
		private String weixin_appkey = "wxe28a7677e380867d";
		private void configSocialSso()
		  {
		    this.mSocialController.getConfig().supportQQPlatform(this,contentUrl);
		    this.mSocialController.getConfig().setSsoHandler( new QZoneSsoHandler(this));
		    this.mSocialController.getConfig().setPlatformOrder(SHARE_MEDIA.QQ, SHARE_MEDIA.SINA,
	                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.TENCENT);
		    this.mSocialController.getConfig().removePlatform(SHARE_MEDIA.DOUBAN, SHARE_MEDIA.RENREN,SHARE_MEDIA.QZONE,SHARE_MEDIA.EMAIL,SHARE_MEDIA.SMS);
		    this.mSocialController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		    this.mSocialController.getConfig().setSsoHandler(new SinaSsoHandler());
		    UMWXHandler wxHandler = this.mSocialController.getConfig().supportWXPlatform(this,weixin_appkey, contentUrl);
		    wxHandler.setWXTitle("指尖上的悦TV，精彩无限...");
		    // 支持微信朋友圈
		    UMWXHandler circleHandler = this.mSocialController.getConfig().supportWXCirclePlatform(this,weixin_appkey, contentUrl) ;
		    circleHandler.setCircleTitle("指尖上的悦TV，精彩无限...");
		    
		  }
            
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		configSocialSso();
		
	}
	
	/**
	* 传感器监听器，在下面的集成中使用
	*/
	private OnSensorListener mSensorListener = new OnSensorListener() {

	    @Override
	    public void onStart() {}

	    /**
	    * @Description: 摇一摇动作完成后回调 
	    */
	    @Override
	    public void onActionComplete(SensorEvent event) {
	        Toast.makeText(UMMainActivity.this, "用户摇一摇，可在这暂停游戏", Toast.LENGTH_SHORT).show();
	    }

	   

	    /**
		    * 分享完成后回调 
		    */
		@Override
		public void onComplete(SHARE_MEDIA platform , int eCode, SocializeEntity entity) {
			// TODO Auto-generated method stub
			Toast.makeText(UMMainActivity.this, "分享完成", Toast.LENGTH_SHORT).show();
			
		}
		
		/**
		    * @Description: 用户点击分享窗口的取消和分享按钮触发的回调
		    * @param button 用户在分享窗口点击的按钮，有取消和分享两个按钮
		    */
		@Override
		public void onButtonClick(WhitchButton button) {
			// TODO Auto-generated method stub
			if (button == WhitchButton.BUTTON_CANCEL) {
		        Toast.makeText(UMMainActivity.this, "取消分享", Toast.LENGTH_SHORT).show();
		        } else {
		        // 分享中, ( 用户点击了分享按钮 )
		            }
			
		}

	};
	
	@Override
	protected void onResume() {
		super.onResume();
		 UMAppAdapter appAdapter = new UMAppAdapter(UMMainActivity.this);    
	   
	    // 配置摇一摇截屏分享时用户可选的平台，最多支持五个平台
		
	    List<SHARE_MEDIA> mplatforms = new ArrayList<SHARE_MEDIA>();
	    
	    
	    mplatforms.add(SHARE_MEDIA.SINA);
	    mplatforms.add(SHARE_MEDIA.QQ);  
	    mplatforms.add(SHARE_MEDIA.TENCENT);
	    mplatforms.add(SHARE_MEDIA.WEIXIN);
	    mplatforms.add(SHARE_MEDIA.WEIXIN_CIRCLE);    
	         // 设置摇一摇分享的文字内容      
	         mShakeController.setShareContent("美好瞬间，与你分享！");      
	    // 注册摇一摇截屏分享功能,mSensorListener在2.1.2中定义         
	    mShakeController.registerShakeListender(UMMainActivity.this, appAdapter,    
	                                        mplatforms, mSensorListener);   
	}  
	  
	@Override
	protected void onPause() {
	    mShakeController.unregisterShakeListener( UMMainActivity.this );
	    super.onPause();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		 super.onActivityResult(requestCode, resultCode, data);
			/**使用SSO授权必须添加如下代码 */
		    UMSsoHandler ssoHandler = mSocialController.getConfig().getSsoHandler(requestCode) ;
		    if(ssoHandler != null){
		       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		      
		    }
	}

}
