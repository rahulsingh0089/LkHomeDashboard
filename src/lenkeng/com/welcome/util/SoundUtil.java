package lenkeng.com.welcome.util;

import java.io.IOException;

import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import lenkeng.com.welcome.R;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

public class SoundUtil {



	private Context context;
	private MediaPlayer mMediaPlayer;
	private ArrayList<String> currentAudioMode;
	private AudioManager audioManager;

	private MediaPlayer messagePlayer;
	private MediaPlayer OFFPlayer;
	private MediaPlayer ONPlayer;
	private MediaPlayer videoPlayer;
	private boolean isMsgplay = false;
	private boolean isOFFPlay = false;
	private boolean isONPlay = false;
	private boolean isVideoPlay = false;

	public SoundUtil(Context context) {
		this.context = context;

		// try {
		// mMediaPlayer = MediaPlayer.create(context,
		// getDefaultRingtoneUri(RingtoneManager.TYPE_NOTIFICATION));

		// mMediaPlayer.setOnCompletionListener(this);
		// mMediaPlayer.setOnPreparedListener(this);
		audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		// currentAudioMode = getActivityAudio();
		Logger.d("tag", "----audioManager--" + audioManager);
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.DISPLAY_OUTPUT_CHANGED");
		//context.registerReceiver(broadcastReceiver, filter);

		// } catch (Exception e) {
		// TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// mMediaPlayer.setLooping(true);
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (audioManager == null) {
				audioManager = (AudioManager) context
						.getSystemService(Context.AUDIO_SERVICE);
			}
			currentAudioMode = getActivityAudio();

		}
	};

	
	/* 获系统通知铃声的Uri */
	public Uri getDefaultRingtoneUri(int type) {
		return RingtoneManager.getActualDefaultRingtoneUri(context, type);
	}

	/* 播放通知铃声 */
	public void play() {
		// mMediaPlayer.prepareAsync();
		if (null != mMediaPlayer) {
			try {

				ArrayList<String> modes = new ArrayList<String>();
				// currentAudioMode=getActivityAudio();
				Logger.i("tag", "---before--currentAudioMode-----"
						+ currentAudioMode.get(0));
				modes.add("AUDIO_I2S");
				//setAudioMode(modes);

				// mMediaPlayer.reset();
				// mMediaPlayer.prepare();
				stop();
				mMediaPlayer.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/* 停止播放铃声 */
	public void stop() {

		if (null != mMediaPlayer && mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
			// mMediaPlayer.reset();
			// mMediaPlayer.release();
		}
	}

	public ArrayList<String> getActivityAudio() {

		try {

			// Class<AudioManager> clazz=AudioManager.class;
			// Method m=clazz.getMethod("getActiveAudioDevices", new
			// Class<?>[]{});
			//if (Build.MODEL.equals(Constant.MODEL_EZTV_2)) {
				Log.e("SoundUtil", "getActiveAudioDevices");
				return audioManager
						.getActiveAudioDevices(AudioManager.AUDIO_OUTPUT_ACTIVE);
				// ArrayList<String> models = new ArrayList<String>();
				// models.add("AUDIO_HDMI");
				// models.add("AUDIO_CODEC");
				// return models;
			//} else {
			//	return null;
			//}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		// if(m !=null){

		// }else{
		// return null;
		// }
	}

	public void setAudioMode(ArrayList<String> modes,int flag) {

		//if (Build.MODEL.equals(Constant.MODEL_EZTV_2)) {
			if (audioManager != null) {
				audioManager.setAudioDeviceActive(modes,
						AudioManager.AUDIO_OUTPUT_ACTIVE,flag);
				Logger.e("SoundUtil", "-----setAudioMode-----audioManager---"
						+ audioManager);
			} else {
				Logger.d("tag", "-----setAudioMode-----audioManager---"
						+ audioManager);
			}
		//}
		/*
		 * try { //Class<AudioManager> clazz=AudioManager.class; //Method
		 * m=clazz.getMethod("setAudioDeviceActive", new Class<?>[]{});
		 * 
		 * } catch (NoSuchMethodException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); }
		 */
		// if(m !=null){
		// }
	}

	public void playerMessage() {
		//changeModel(true);
		try {

			messagePlayer = MediaPlayer.create(context, R.raw.msg);
			messagePlayer.setOnCompletionListener(completionListener);
			messagePlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					// TODO Auto-generated method stub
					Logger.d("tag", "$$$---onError---message");
					isMsgplay = false;
					return false;
				}
			});
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (messagePlayer != null && messagePlayer.isPlaying()) {
						Logger.e("tag", "----++++" + messagePlayer.isPlaying());
						messagePlayer.stop();
					}
					messagePlayer.start();
					isMsgplay = messagePlayer.isPlaying();

					Logger.i("ez2", "#254# AV输出时,推送声音或者摄像头挡板提示音过后,电视无声音输出");
				}
			}, 3000);

			// messagePlayer.prepare();

			// TODO Auto-generated method stub
			/*
			 * AssetManager assetManager=context.getAssets();
			 * AssetFileDescriptor descriptor=
			 * assetManager.openFd("newmessage.wav"); messagePlayer=new
			 * MediaPlayer();
			 * messagePlayer.setDataSource(descriptor.getFileDescriptor
			 * (),descriptor.getStartOffset(),descriptor.getLength());
			 * messagePlayer.prepare(); messagePlayer.start();
			 */

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logger.e("tag", "--catch--");
			e.printStackTrace();
		}
	}

	boolean needRemove = false;
	int volum=-1;
	private void changeModel(boolean add,int flag) {

		try {
			//if (Build.MODEL.equals(Constant.MODEL_EZTV_2)) {

				ArrayList<String> modes = new ArrayList<String>();
				modes.clear();
				// if (currentAudioMode == null) {
				modes = getActivityAudio();

				// }
				if (modes != null) {
					
					/*if (modes.contains("AUDIO_I2S") && needRemove == false) {
						Logger.e("gw", "----- canageModel  1  --");
						return;
					}*/
					
					if (add) {
						if(!modes.contains("AUDIO_I2S")){
							modes.add("AUDIO_I2S");
							needRemove = true;
							Logger.e("gw", "----- canageModel  2  --");
						}
						//volum=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//						openOrcloseBackMusic(0);
						//audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 100, 0);

					} else {
						
						if (needRemove) {
							modes.remove("AUDIO_I2S");
							needRemove = false;
							Logger.e("gw", "----- canageModel  3  --");
						};
						/*audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volum, 0);
						Logger.d("gw", "---  Remove  volum  -- "+volum);
						volum=-1;*/
//						openOrcloseBackMusic(1);
						//audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0);
					}
					setAudioMode(modes,flag);
				}
				// currentAudioMode=getActivityAudio();
				// modes.clear();
				// modes.addAll(currentAudioMode);
				// modes.add("AUDIO_I2S");
				
			//}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void openOrcloseBackMusic(int openOrclose){
		/*
		if(openOrclose == 1){
			Log.d("awk", "  volum open 1= "+volum);
			if(volum !=-1){
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volum, 0);
				Log.d("awk", "  volum open 2= "+volum);
				volum=-1;
			}
		}else{
			Log.d("awk", "  volum  close  1=  "+volum);
			if(volum ==-1){
				int temp =audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
				volum=temp==0?-1:temp;
				if(temp !=0){
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
				}
				Log.d("awk", "  volum  close  2=  "+volum);
			}
		}
		*/
	}
	public void playerOFF() {
		try {
			if(ONPlayer !=null){
				Logger.d("kao", "-------  playerOFF  bef -----");
				ONPlayer.stop();
				ONPlayer.reset();
				ONPlayer.release();
				ONPlayer=null;
				isONPlay=false;
				Logger.d("kao", "-------  playerOFF  aft -----");
			}
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
		try {
			
			try {
	            AssetFileDescriptor afd = context.getResources().openRawResourceFd(R.raw.off);
	            OFFPlayer = new MediaPlayer();
	            OFFPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
	            OFFPlayer.setVolume(100.0f, 100.0f);
	            OFFPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
	            afd.close();
	            OFFPlayer.prepare();
	        } catch (IOException ex) {
	            // fall through
	        } catch (IllegalArgumentException ex) {
	           // fall through
	        } catch (SecurityException ex) {
	            // fall through
	        }
			OFFPlayer.setOnCompletionListener(completionListener);
			OFFPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
				}
			});
			OFFPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					// TODO Auto-generated method stub
					Logger.d("tag", "$$$---onError---OFF");
					//isOFFPlay = false;
					completionListener.onCompletion(mp);
					return false;
				}
			});
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						if (OFFPlayer != null && OFFPlayer.isPlaying()) {
							OFFPlayer.stop();
						}
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						if(OFFPlayer !=null){
							Logger.e("gw", "--playerOFF  changeModel(true,1)");
							changeModel(true,1);
							//audioManager.setMode(AudioManager.MODE_RINGTONE);
							
							OFFPlayer.start();
							isOFFPlay = true;
							//SystemProperties.set("sys.audio.iis", "true");
						}
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						
						completionListener.onCompletion(OFFPlayer);
						e.printStackTrace();
					}
				}
			}, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void playerON() {
		
		try {
			if(OFFPlayer !=null){
				Logger.d("kao", "----- playerON  bef   -----");
				OFFPlayer.stop();
				OFFPlayer.reset();
				OFFPlayer.release();
				OFFPlayer =null;
				isOFFPlay=false;
				Logger.d("kao", "----- playerON  aft   -----");
			}
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
		try {
			//ONPlayer = MediaPlayer.create(context, R.raw.on);
			try {
	            AssetFileDescriptor afd = context.getResources().openRawResourceFd(R.raw.on);
	            ONPlayer = new MediaPlayer();
	            ONPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
	            ONPlayer.setVolume(100.0f, 100.0f);
	            ONPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
	            afd.close();
	            ONPlayer.prepare();
	        } catch (IOException ex) {
	            // fall through
	        } catch (IllegalArgumentException ex) {
	           // fall through
	        } catch (SecurityException ex) {
	            // fall through
	        }
			ONPlayer.setOnCompletionListener(completionListener);
			ONPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
				}
			});
			
			
			ONPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					// TODO Auto-generated method stub
					Logger.d("tag", "$$$---onError---ON");
					//isONPlay = false;
					completionListener.onCompletion(mp);
					return false;
				}
			});

			
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						if (ONPlayer != null && ONPlayer.isPlaying()) {
							ONPlayer.stop();
							Logger.e("gw", "-----ONPlayer.stop()  -- ");
						}
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Logger.e("gw", "-----ONPlayer.stop()  IllegalStateException-- ");
					}
					try {
						if(ONPlayer !=null){
							Logger.e("gw", "--playerON  changeModel(true,1)");
							changeModel(true,1);
							ONPlayer.start();
							isONPlay = true;
						}
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						completionListener.onCompletion(ONPlayer);
					}
					
				}
			}, 0);
			Logger.d("tag", "$$$---beffore---isONPlay---" + isONPlay);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void playVideoMsg() {
		
		try {
			if(videoPlayer !=null && isVideoPlay){
				videoPlayer.stop();
				videoPlayer.reset();
				videoPlayer.release();
				videoPlayer =null;
				isVideoPlay=false;
			}
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
		try {
			try {
	            AssetFileDescriptor afd = context.getResources().openRawResourceFd(R.raw.videomsg);
	            videoPlayer = new MediaPlayer();
	            videoPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
	            videoPlayer.setVolume(100.0f, 100.0f);
	            videoPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
	            afd.close();
	            videoPlayer.prepare();
	        } catch (IOException ex) {
	            // fall through
	        } catch (IllegalArgumentException ex) {
	           // fall through
	        } catch (SecurityException ex) {
	            // fall through
	        }
			videoPlayer.setOnCompletionListener(completionListener);
			videoPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					// TODO Auto-generated method stub
					//isVideoPlay = false;
					Logger.e("gw", "-----onError-----playVideoMsg-----isVideoPlay   "+isVideoPlay+"OnError - Error code: " + what + " Extra code: " + extra);
					completionListener.onCompletion(mp);
					return false;
				}
			});

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						if(videoPlayer !=null){
							Logger.e("gw", "--playVideoMsg  changeModel(true,1)");
							changeModel(true,1);
							videoPlayer.start();
							isVideoPlay = videoPlayer.isPlaying();
							Logger.e("gw", "-----start-----playVideoMsg-----isVideoPlay   "+isVideoPlay);
						}
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						Logger.e("gw", "-----IllegalStateException-----playVideoMsg-----isVideoPlay   "+isVideoPlay);
						completionListener.onCompletion(videoPlayer);
					}
				}
			}, 4000);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.e("gw", "-----Exception-----playVideoMsg-----isVideoPlay   "+isVideoPlay);
		}
	}

	OnCompletionListener completionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub

			/*if (messagePlayer == null || mp == messagePlayer) {
				isMsgplay = false;
			}*/
			if (OFFPlayer == null || mp == OFFPlayer) {
				isOFFPlay = false;
			}
			if (ONPlayer == null || mp == ONPlayer) {
				isONPlay = false;
			}
			if (videoPlayer == null || mp == videoPlayer) {
				isVideoPlay = false;
			}

			if (isOFFPlay == false && isONPlay == false&& isVideoPlay == false) {
				Logger.e("gw", "-----changeModel(false,0) ------");
				changeModel(false,0);
				//SystemProperties.set("sys.audio.iis", "false");
				
				Logger.e("gw", "----isVideoPlay---"+isVideoPlay+"  isONPlay  =  "+isONPlay +"  isOFFPlay  =  "+isOFFPlay);
			}
			if(mp !=null){
				mp.release();
			}
			Intent intent=new Intent();
			intent.setAction("com.android.lk.completion");
			context.sendBroadcast(intent);
			/*
			 * try {
			 * 
			 * if(!messagePlayer.isPlaying() &&!OFFPlayer.isPlaying()
			 * &&!ONPlayer.isPlaying()){
			 * 
			 * } mp.release(); } catch (Exception e) { // TODO Auto-generated
			 * catch block e.printStackTrace(); try {
			 * setAudioMode(currentAudioMode); } catch (Exception e2) { // TODO
			 * Auto-generated catch block e2.printStackTrace(); } }
			 */
		}
	};

	OnPreparedListener prelistener = new OnPreparedListener() {

		@Override
		public void onPrepared(MediaPlayer mp) {
			// TODO Auto-generated method stub
		}
	};
}
