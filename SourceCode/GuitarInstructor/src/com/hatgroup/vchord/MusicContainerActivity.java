package com.hatgroup.vchord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.hatgroup.vchord.common.Constants;
import com.hatgroup.vchord.common.CustomToast;
import com.hatgroup.vchord.entities.Song;
import com.hatgroup.vchord.repo.SongRepo;
import com.hatgroup.vchord.utils.Parser;
import com.hatgroup.vchord.utils.Parser.ParsedObject;
import com.hatgroup.vchord.utils.Security;
import com.hatgroup.vchord.utils.SongSetting;
import com.hatgroup.vchord.utils.ThemeUtils;
import com.hatgroup.vchord.utils.Utilities;
import com.hatgroup.vchord.utils.WebService;
import com.hatgroup.vchord.widgets.AutoScrollSettingDialog;
import com.hatgroup.vchord.widgets.BaseDialogFragment;
import com.hatgroup.vchord.widgets.CommentDialog;
import com.hatgroup.vchord.widgets.CustomTextView;
import com.hatgroup.vchord.widgets.MediaDialog;
import com.hatgroup.vchord.widgets.PurchaseDialog;
import com.hatgroup.vchord.widgets.TonesAdjustmentDialog;

public class MusicContainerActivity extends SherlockFragmentActivity {

	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private Bundle savedInstanceState = null;
	private AlertDialog dialog;
	
	private View memuLayout = null;
	private ScrollView scrollView;
	private LinearLayout rootLayout;
	private ScaleGestureDetector mScaleDetector;

	private int scrollPos = 0;
	private int actualHeight = 0;

	private Handler mHandler;
	private Runnable mScrollDown;

	private boolean isScrolling = false;

	// We support 20 level of scroll speeds
	public static final int MAX_SCROLL_SPEED = 20;
	// Every speed should have a 10 miliseconds step
	public static final int TIME_STEP = 10;
	public static final int INITIAL_DELAY_VALUE = 10;
	public static long DELAY_VALUE = 0;
	private int NUMBER_OF_PIXELS = 1;
	//private float ZOOM_PERCENT = 0.3f;
	private int ZOOM_SIZE = 4;

	List<CustomTextView> listTextViews = new ArrayList<CustomTextView>();
	private int colorIndex = 0;

	/**
	 * For song and info
	 */
	private Song song;
	private SongSetting songSettings;
	private int scrollSpeed;
	private int delayScrollTime;
	private Date START_DELAY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.savedInstanceState = savedInstanceState;
		init();
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(MusicContainerActivity.this, requestCode, resultCode, data);  
    }

    private void onClickLogin() {
        createFacebookConnection();
    }

    private void onClickLogout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
            if (session != null && session.isOpened()) {
            	Toast.makeText(getApplicationContext(), R.string.facebook_error_logout, Toast.LENGTH_LONG).show();
            }            
        }
    }
    
    public void createFacebookConnection() {
       
       Session session = Session.getActiveSession();
	      Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS); 
	      if (session == null) {
	          if (savedInstanceState != null) {
	              session = Session.restoreSession(MusicContainerActivity.this, null, statusCallback, savedInstanceState);
	          }
	          if (session == null) {
	              session = new Session(MusicContainerActivity.this);
	          }
	          Session.setActiveSession(session);
	          if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
	              session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
	          }
	      }

        if (!session.isOpened() && !session.isClosed() && session.getState() != SessionState.OPENING) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Log.w("Facebook test", "Open active session");
            Session.openActiveSession(this, true, statusCallback);
        }
    }
    
    @SuppressLint("NewApi")
	void publishToWall() {
        Session session = Session.getActiveSession(); 
		
        String key = Security.keyGeneration();
        String lyric = Security.decrypt(song.getLyric(), key);
        
        String titleLyrics =  song.getTitle() + "\r\n" + lyric ; 
        
        Bundle postParams = new Bundle();
        postParams.putString("message", titleLyrics);
        postParams.putString("name", "Facebook SDK for Android");
        postParams.putString("caption", "Build great social apps and get more installs.");
        postParams.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
        postParams.putString("link", "https://developers.facebook.com/android");
        postParams.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

        final Context context = this;
        Request.Callback callback = new Request.Callback() {
            public void onCompleted(Response response) {
                FacebookRequestError error = response.getError();
                if (error != null) {
                	if (error.getErrorMessage().contains("(#506) Duplicate message")) {
                		Toast.makeText(context, R.string.facebook_error_message_506, Toast.LENGTH_LONG).show();
                	}
                } else {
                    JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
                    String postId = null;
                    try {
                        postId = graphResponse.getString("id");
                    } catch (JSONException e) {
                        Log.i("Facebook error", "JSON error " + e.getMessage());
                    }
                    Toast.makeText(context, R.string.share_facebook_successfull_toast, Toast.LENGTH_LONG).show();
                }
            }
        };

        Request request = new Request(session, "me/feed", postParams, HttpMethod.POST, callback);             
        RequestAsyncTask task = new RequestAsyncTask(request);        
        task.execute();
    }

    private class SessionStatusCallback implements Session.StatusCallback {    	
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            String message = "Facebook session status changed - " + session.getState() + " - Exception: " + exception;
            //Toast.makeText(FacebookShareActivity.this, message, Toast.LENGTH_SHORT).show();
            Log.w("Facebook test", message);

            if (session.isOpened()) {
                publishToWall();                    
            } 
//            else if (session.isOpened()) {                	
//                OpenRequest open = new OpenRequest(MusicContainerActivity.this).setCallback(this);                    
//                List<String> permission = new ArrayList<String>();
//                permission.add("publish_actions");
//                permission.add("manage_pages");
//                open.setPermissions(permission);
//                Log.w("Facebook test", "Open for publish");
//                session.openForPublish(open);
//            }
        }        
    }
    
    private AlertDialog confirmShareOnFacebookDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(MusicContainerActivity.this);
		// Get the layout inflater
		LayoutInflater inflater = MusicContainerActivity.this.getLayoutInflater();

		builder.setTitle(ThemeUtils.getString(MusicContainerActivity.this, R.string.text_title_facebook));

		View myView = inflater.inflate(R.layout.activity_share_on_facebook, null);

		builder.setView(myView)
			// Add action buttons
			.setPositiveButton(R.string.text_submit,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {//					
							onClickLogin();
						}
					})
			.setNegativeButton(R.string.text_cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
		
		Session session = Session.getActiveSession(); 
		if (session != null && session.isOpened()) {
			builder.setView(myView).setNeutralButton(R.string.logout_facebook,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					 onClickLogout();					
				}
			});
		}

		AlertDialog dialog = builder.create();	

		return dialog;
	}

	private void init() {

		// Init layout and main instances variables
		// setContentView(R.layout.music_page_with_auto_scroll);
		setContentView(R.layout.song_detail_layout);
	
		this.scrollView = (ScrollView) findViewById(R.id.vertical_scrollview_id);
		//root layout is the layout will wrap MusicContainer view group
		this.rootLayout = (LinearLayout) findViewById(R.id.vertical_outer_layout_id);
		this.song = (Song) getIntent().getSerializableExtra(
				Constants.SERIALIZED_SONG);

		// Get old or default delayValue
		this.songSettings = SongSetting.getInstance(song.getSetting());
		this.scrollSpeed = songSettings
				.getSettingValueAsInt(SongSetting.SCROLL_SPEED);
		DELAY_VALUE = (MAX_SCROLL_SPEED - scrollSpeed) * TIME_STEP
				+ INITIAL_DELAY_VALUE;
		this.delayScrollTime = songSettings
				.getSettingValueAsInt(SongSetting.DELAY_SCROLL_TIME);

		registerAutoScroll();

		parseData();

		loadContentWrapMode(rootLayout);

		initAndRegisterZoomAction();

		// registerActionForButtons();		
	}

	private void loadContentWrapMode(ViewGroup root) {
		ViewGroup viewGroup = inflateMusicContainer(root);
		for (CustomTextView textView : listTextViews) {
			textView.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			if (textView.getParsedObject().hasNewLine()) {
				viewGroup = inflateMusicContainer(root);
			}
			viewGroup.addView(textView);
		}
		colorIndex = 0;
	}

	private ViewGroup inflateMusicContainer(ViewGroup root) {
		LayoutInflater inflater = LayoutInflater.from(this);
		ViewGroup msPaneLayout = (ViewGroup) inflater.inflate(
				R.layout.music_pane_container, root, false);
		ViewGroup viewGroup = (ViewGroup) msPaneLayout
				.findViewById(R.id.theMusicPane);
		// Switch color
		colorIndex = 1 - colorIndex;
		viewGroup.setBackgroundColor(Constants.HIGHTED_COLORS[colorIndex]);
		root.addView(viewGroup);
		return viewGroup;
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// getMenuInflater().inflate(R.menu.main, menu);
	// return true;
	// }

	public void goHome(View view) {
		startActivity(new Intent(this, TabNavigationActivity.class));
	}

	public void playTheSong(View view) {
		AsyncTask<String, Void, String> asyncTask = new AsyncTask<String, Void, String>() {
			ProgressDialog progressDialog = null;
			String songURL = null;

			@Override
			public void onPreExecute() {
				progressDialog = ProgressDialog.show(
						MusicContainerActivity.this, ThemeUtils.getString(
								getApplicationContext(),
								R.string.text_title_progress), ThemeUtils
								.getString(getApplicationContext(),
										R.string.text_connecting));
			}

			@Override
			protected String doInBackground(String... args) {
				String musicLink = song.getMusic_link();
				songURL = WebService.performGet(Constants.SONG_URL_CONVERT
						+ musicLink);
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				if (songURL != null) {
					songURL = songURL.replace("\"", "").replace("\\/", "/");
					final MediaDialog dialog = new MediaDialog(
							MusicContainerActivity.this);
					dialog.setCanceledOnTouchOutside(true);
					dialog.showMyDialog(songURL);
				}
			}
		};
		asyncTask.execute("");
	}

	private void parseData() {
		String key = Security.keyGeneration();
		String lyric = Security.decrypt(song.getLyric(), key);
		Parser parser = new Parser(lyric);
		
		List<ParsedObject> pos = parser.parseData();
		float zoom = songSettings.getSettingValueAsFloat(SongSetting.ZOOM);
		for (ParsedObject po : pos) {
			// Wrap mode by default
			CustomTextView textView = new CustomTextView(this, po);
			textView.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			String token = po.trimmedToken().replace("[", "").replace("]", "");
			String word = po.trimmedWord();
			String content = String.format(Constants.TEMPLATE, token, word);
			textView.setTextContent(Html.fromHtml(content));
			if(zoom > 0 && zoom != CustomTextView.getInitialTextSize()){
				textView.setTextSize(zoom);
			}
			listTextViews.add(textView);
		}

		if (songSettings.getSettingValueAsInt(SongSetting.TONES) != 0) {
			convertChords(songSettings.getSettingValueAsInt(SongSetting.TONES));
		}
	}

	private void registerAutoScroll() {
		rootLayout.setClickable(true);
		rootLayout.setEnabled(true);
		rootLayout.setFocusable(true);
		rootLayout.setFocusableInTouchMode(true);
		rootLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// If on delay time, ignore touch action
				if (START_DELAY != null) {
					return;
				}
				if (isScrolling == true) {
					stopAutoScroll();
				} else {
					runAutoScroll();
				}
			}
		});

	}

	public void runAutoScroll() {
		if (DELAY_VALUE >= INITIAL_DELAY_VALUE
				&& DELAY_VALUE <= MAX_SCROLL_SPEED * TIME_STEP) {
			if (isScrolling == false) {
				mHandler = new Handler();
				mScrollDown = new Runnable() {
					public void run() {
						moveScrollView();
						mHandler.postDelayed(this, DELAY_VALUE);
						// if (scrollPos == 0) {
						// mHandler.removeCallbacks(this);
						// }
					}
				};
				mHandler.postDelayed(mScrollDown, DELAY_VALUE);
				isScrolling = true;
			}
		}
	}

	private void stopAutoScroll() {
		if (isScrolling == true) {
			mHandler.removeCallbacks(mScrollDown);
			isScrolling = false;
		}
	}

	private void getScrollMaxAmount() {
		actualHeight = (rootLayout.getMeasuredHeight() - (256 * 3));
	}

	private void moveScrollView() {
		// If on top, start delay
		if (scrollView.getScrollY() == 0) {
			if (START_DELAY == null) {
				START_DELAY = new Date();
			}
		}
		if (START_DELAY != null && onDelay()) {
			return;
		} else {
			START_DELAY = null;
			scrollView.smoothScrollBy(0, 1);
		}
		View lastChild = rootLayout.getChildAt(rootLayout.getChildCount() - 1);
		int diff = lastChild.getBottom()
				- (scrollView.getHeight() + scrollView.getScrollY());
		// if diff is zero, then the bottom has been reached
		if (diff <= 0) {
			mHandler.removeCallbacks(mScrollDown);
			return;
		} else {
			scrollPos = (int) (rootLayout.getScrollY() + NUMBER_OF_PIXELS);
			if (scrollPos >= actualHeight) {
				scrollPos = 0;
			}
			scrollView.smoothScrollBy(0, scrollPos);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopAutoScroll();
	}

	/*
	 * @Override public void onResume(){ super.onResume(); runAutoScroll(); }
	 */
	@Override
	protected void onStop() {
		super.onStop();
		stopAutoScroll();
		
		//An Le
		Session session = Session.getActiveSession(); 
		if ( session != null) {
			session.removeCallback(statusCallback);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopAutoScroll();
		mHandler = null;
		mScrollDown = null;
	}

	public void reloadSongFromRepo() {
		SongRepo songRepo = new SongRepo(this);
		song = songRepo.getSongById(song.getId());

		songSettings = SongSetting.getInstance(song.getSetting());
		scrollSpeed = songSettings
				.getSettingValueAsInt(SongSetting.SCROLL_SPEED);
		DELAY_VALUE = (MAX_SCROLL_SPEED - scrollSpeed) * TIME_STEP
				+ INITIAL_DELAY_VALUE;
		delayScrollTime = songSettings
				.getSettingValueAsInt(SongSetting.DELAY_SCROLL_TIME);
	}

	public void convertChords(int adjustValue) {
		reloadSongFromRepo();
		for (CustomTextView tv : listTextViews) {
			if (!tv.getParsedObject().trimmedToken().equals("")) {
				String chord = tv.getParsedObject().trimmedToken();
				String newChord = Utilities.changeChord(chord, adjustValue);
				// tv.getParsedObject().token = newChord;
				newChord = newChord.replace("[", "").replace("]", "");
				String content = String.format(Constants.TEMPLATE, newChord, tv
						.getParsedObject().trimmedWord());
				tv.setTextContent(Html.fromHtml(content));
			}
		}
	}

	private boolean onDelay() {
		if (START_DELAY != null) {
			long diff = (new Date()).getTime() / 1000 - START_DELAY.getTime()
					/ 1000;
			if (diff >= delayScrollTime) {
				return false;
			}
			return true;
		}
		return false;
	}

	// private void registerActionForButtons() {
	// ImageButton autoscrollSettingImageButton = (ImageButton)
	// findViewById(R.id.autoscrollSettingImageButton);
	// autoscrollSettingImageButton
	// .setOnClickListener(new View.OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// stopAutoScroll();
	// reloadSongFromRepo();
	// final AutoScrollSettingDialog autoscrollSettingDialog = new
	// AutoScrollSettingDialog(
	// MusicContainerActivity.this, song);
	// autoscrollSettingDialog.setCanceledOnTouchOutside(true);
	// autoscrollSettingDialog.show();
	// }
	// });
	//
	// ImageButton btnTonesAdjust = (ImageButton) findViewById(R.id.btnUp);
	// btnTonesAdjust.setOnClickListener(new View.OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// reloadSongFromRepo();
	// final Dialog dialog = new TonesAdjustmentDialog(
	// MusicContainerActivity.this,
	// MusicContainerActivity.this.song);
	// dialog.setCanceledOnTouchOutside(true);
	// dialog.show();
	// }
	// });
	//
	// final ImageButton btnFav = (ImageButton) findViewById(R.id.btnFavorite);
	// btnFav.setImageDrawable(getResources()
	// .getDrawable(
	// (song.getFavorite() == null || song.getFavorite() == 0) ?
	// R.drawable.not_fav
	// : R.drawable.fav));
	// btnFav.setOnClickListener(new View.OnClickListener() {
	// @Override
	// public void onClick(View arg0) {
	// SongRepo songRepo = new SongRepo(MusicContainerActivity.this);
	// song.setFavorite(Math.abs(Utilities.parseInt(song.getFavorite()) - 1));
	// if (songRepo.updateSong(song)) {
	// Drawable notFav = MusicContainerActivity.this
	// .getResources()
	// .getDrawable(
	// song.getFavorite() == 0 ? R.drawable.not_fav
	// : R.drawable.fav);
	// btnFav.setImageDrawable(notFav);
	// }
	// }
	// });
	//
	// ImageButton btnComment = (ImageButton) findViewById(R.id.btnComment);
	// btnComment.setOnClickListener(new View.OnClickListener() {
	// @Override
	// public void onClick(View arg0) {
	// final CommentDialog commentDialog = new CommentDialog(
	// MusicContainerActivity.this, song);
	// commentDialog.setCanceledOnTouchOutside(true);
	// commentDialog.show();
	// }
	// });
	// }
	//
	private void initAndRegisterZoomAction() {
		if(Constants.PINCH_TO_ZOOM){
			//If pinch to zoom, hide zoom buttons
			findViewById(R.id.zoomBar).setVisibility(View.GONE);
			registerPinchToZoom();
		}
		else{
			registerZoomButtons();
		}
	}

	private void registerPinchToZoom(){
		ViewTreeObserver vto = rootLayout.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {

				rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(
						this);

				getScrollMaxAmount();

				runAutoScroll();
			}
		});


		mScaleDetector = new ScaleGestureDetector(this, new OnScaleGestureListener() {

					boolean scrollStatus = isScrolling;

					@Override
					public void onScaleEnd(ScaleGestureDetector detector) {
				ProgressDialog progressDialog = ProgressDialog.show(MusicContainerActivity.this, null, "Zooming...");
						stopAutoScroll();
						float scaleFactor = detector.getScaleFactor();
				float currentSize = listTextViews.size() == 0 ? 0 : listTextViews.get(0).getCurrentSize();
						float initialSize = CustomTextView.getInitialTextSize();
						// float newSize = currentSize * scaleFactor;
						float newSize = currentSize;

						if (scaleFactor > 1) {
					newSize = currentSize + ZOOM_SIZE;
						} else if (scaleFactor < 1) {
					newSize = currentSize - ZOOM_SIZE;
						}

				//Log.d("ZOOMING: ", String.format("Current size: %s, new size: %s", currentSize, newSize));
						if (newSize > initialSize * 3) {
							Log.d("ZOOMING: ", "WARNING : reach maximum");
						} else if (newSize < initialSize) {
							Log.d("ZOOMING: ", "WARNING : reach minimum");
						} else {
							if (scaleFactor != 1) {
						//Update zoom percentage into DB
						songSettings.updateSetting(SongSetting.ZOOM, String.valueOf(newSize));
						SongRepo songRepo = new SongRepo(MusicContainerActivity.this);		
								song.setSetting(songSettings.toString());
								if (songRepo.updateSong(song)) {
									for (TextView tv : listTextViews) {
										tv.setTextSize(newSize);
									}
								}
							}
						}
						// restore scroll status after finished zooming
						if (scrollStatus) {
							runAutoScroll();
						}
						progressDialog.dismiss();
						progressDialog = null;
					}

					@Override
					public boolean onScaleBegin(ScaleGestureDetector detector) {
						// Back up scroll status
						scrollStatus = isScrolling;
						return true;
					}

					@Override
					public boolean onScale(ScaleGestureDetector detector) {
						return false;
					}
				});
		
		scrollView.setSmoothScrollingEnabled(true);
		// rootLayout.setOnTouchListener(new OnTouchListener() {
		scrollView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mScaleDetector.onTouchEvent(event);
				// Return false, so that it will continue go to onClick()
				return false;
			}
		});
	}

	// ============================ triet.dinh =================================

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {

		LayoutInflater inflator = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.memuLayout = inflator.inflate(R.layout.menu_layout, null);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		// actionBar.setDisplayHomeAsUpEnabled(false);
		// actionBar.setDisplayShowHomeEnabled(false);
		// actionBar.setDisplayShowCustomEnabled(true);
		// actionBar.setDisplayShowTitleEnabled(false);

		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
				ActionBar.LayoutParams.MATCH_PARENT,
				ActionBar.LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER_HORIZONTAL;

		this.memuLayout.setLayoutParams(lp);

		actionBar.setCustomView(this.memuLayout);

		ImageButton itemMusicRun = (ImageButton) this.memuLayout
				.findViewById(R.id.menu_action_bar_music_run);
		itemMusicRun.setImageResource(ThemeUtils.getResId(getTheme(),
				R.attr.ic_action_music_run));
		itemMusicRun.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MenuItemClicked(R.id.menu_action_bar_music_run);
			}
		});

		ImageButton itemMusicNote = (ImageButton) this.memuLayout
				.findViewById(R.id.menu_action_bar_music_note);
		itemMusicNote.setImageResource(ThemeUtils.getResId(getTheme(),
				R.attr.ic_action_music_note));
		itemMusicNote.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MenuItemClicked(R.id.menu_action_bar_music_note);
			}
		});

		ImageButton itemComment = (ImageButton) this.memuLayout
				.findViewById(R.id.menu_action_bar_comment);
		itemComment.setImageResource(ThemeUtils.getResId(getTheme(),
				R.attr.ic_action_comment));
		itemComment.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MenuItemClicked(R.id.menu_action_bar_comment);
			}
		});

		ImageButton itemFB = (ImageButton) this.memuLayout
				.findViewById(R.id.menu_action_bar_facebook);
		itemFB.setImageResource(ThemeUtils.getResId(getTheme(),
				R.attr.ic_action_facebook));
		itemFB.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MenuItemClicked(R.id.menu_action_bar_facebook);
			}
		});

		ImageButton itemFavorite = (ImageButton) this.memuLayout
				.findViewById(R.id.menu_action_bar_favorite);

		itemFavorite.setImageResource((Utilities.parseInt(this.song.getFavorite()) > 0) ? R.drawable.ic_action_star_full : R.drawable.ic_action_star_empty);
		// if (this.song.getFavorite() > 0)
		// {
		// itemFavorite.setImageResource(R.drawable.ic_action_star_full);
		// }
		// else
		// {
		// itemFavorite.setImageResource(R.drawable.ic_action_star_empty);
		// }

		itemFavorite.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MenuItemClicked(R.id.menu_action_bar_favorite);
			}
		});

		ImageButton itemScroll = (ImageButton) this.memuLayout
				.findViewById(R.id.menu_action_bar_scroll);
		itemScroll.setImageResource(ThemeUtils.getResId(getTheme(),
				R.attr.ic_action_scroll));
		itemScroll.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MenuItemClicked(R.id.menu_action_bar_scroll);
			}
		});

		// // Inflate the menu items for use in the action bar
		// MenuInflater menuLayout = getSupportMenuInflater();
		// menuLayout.inflate(R.menu.main, menu);
		//
		// MenuItem itemMusicRun =
		// menu.findItem(R.id.menu_action_bar_music_run);
		// itemMusicRun.setIcon(ThemeUtils.getResId(this.getTheme(),
		// R.attr.ic_action_music_run));
		//
		// MenuItem itemMusicNote =
		// menu.findItem(R.id.menu_action_bar_music_note);
		// itemMusicNote.setIcon(ThemeUtils.getResId(this.getTheme(),
		// R.attr.ic_action_music_note));
		//
		// MenuItem itemComment = menu.findItem(R.id.menu_action_bar_comment);
		// itemComment.setIcon(ThemeUtils.getResId(this.getTheme(),
		// R.attr.ic_action_comment));
		//
		// MenuItem itemFB = menu.findItem(R.id.menu_action_bar_facebook);
		// itemFB.setIcon(ThemeUtils.getResId(this.getTheme(),
		// R.attr.ic_action_facebook));
		//
		// MenuItem itemFavorite = menu.findItem(R.id.menu_action_bar_favorite);
		// itemFavorite.setIcon(ThemeUtils.getResId(this.getTheme(),
		// R.attr.ic_action_favourite));
		//
		// MenuItem itemScroll = menu.findItem(R.id.menu_action_bar_scroll);
		// itemScroll.setIcon(ThemeUtils.getResId(this.getTheme(),
		// R.attr.ic_action_scroll));

		return true;
	}
	
	private boolean MenuItemClicked(int menuItemId)
	{
		switch (menuItemId) {
		case R.id.menu_action_bar_music_run:
			playTheSong(null);
			return true;
		case R.id.menu_action_bar_music_note:
			reloadSongFromRepo();
			// final Dialog dialog = new TonesAdjustmentDialog(
			// MusicContainerActivity.this, getWindowManager(),
			// MusicContainerActivity.this.song);
			// dialog.setCanceledOnTouchOutside(true);
			// dialog.show();

			final TonesAdjustmentDialog toneDialog = new TonesAdjustmentDialog(
					MusicContainerActivity.this, song);
			toneDialog.show(getSupportFragmentManager(),
					"TonesAdjustmentDialog");

			return true;
		case R.id.menu_action_bar_comment:
			// final CommentDialog commentDialog = new CommentDialog(
			// MusicContainerActivity.this, song);
			// commentDialog.setCanceledOnTouchOutside(true);
			// commentDialog.show();

			final CommentDialog commentDialog = new CommentDialog(song);
			commentDialog.show(getSupportFragmentManager(), "CommentDialog");

			return true;
		case R.id.menu_action_bar_facebook:
//			Intent intent = new Intent(MusicContainerActivity.this, ShareOnFacebookActivity.class);
//			startActivity(intent);
//			final ShareOnFacebookDialog shareOnFacebookDialog = new ShareOnFacebookDialog();
//			shareOnFacebookDialog.show(getSupportFragmentManager(), "ShareOnFacebookDialog");
//			onClickLogin();
			dialog = confirmShareOnFacebookDialog();
			dialog.show();
			return true;
		case R.id.menu_action_bar_favorite:
			SongRepo songRepo = new SongRepo(MusicContainerActivity.this);
			song.setFavorite(Math.abs(Utilities.parseInt(song.getFavorite()) - 1));
			if (songRepo.updateSong(song)) {
				Drawable notFav = MusicContainerActivity.this.getResources()
						.getDrawable(
								song.getFavorite() == 0 ? R.drawable.not_fav
										: R.drawable.fav);
				// btnFav.setImageDrawable(notFav);
			}
			// update image favorite
			ImageButton itemFavorite = (ImageButton) this.memuLayout.findViewById(R.id.menu_action_bar_favorite);
			itemFavorite.setImageResource((this.song.getFavorite() > 0) ? R.drawable.ic_action_star_full : R.drawable.ic_action_star_empty);
			
			
			
			if (this.song.getFavorite() > 0)
			{
				CustomToast.show(this, R.string.msg_info_add_favorite_success, CustomToast.MsgType.INFO);
			}
			else
			{
				CustomToast.show(this, R.string.msg_info_remove_favorite_success, CustomToast.MsgType.ERROR);
			}
			
			
			return true;
		case R.id.menu_action_bar_scroll:
			stopAutoScroll();
			reloadSongFromRepo();
			// final AutoScrollSettingDialog autoscrollSettingDialog = new
			// AutoScrollSettingDialog(
			// MusicContainerActivity.this, song);
			// autoscrollSettingDialog.setCanceledOnTouchOutside(true);
			// autoscrollSettingDialog.show();

			final AutoScrollSettingDialog autoscrollSettingDialog = new AutoScrollSettingDialog(
					MusicContainerActivity.this, song);
			autoscrollSettingDialog.show(getSupportFragmentManager(),
					"AutoscrollSettingDialog");

			return true;

		default:
			//return super.onOptionsItemSelected(item);
			return true;
		}
		
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		return MenuItemClicked(item.getItemId());
	}

	private Boolean isZoomActivated = null; 
	
	private void registerZoomButtons(){
		//Handle zoom in / zoom out buttons
		final ImageView imgZoomIn =(ImageView) findViewById(R.id.imgZoomIn);
		imgZoomIn.getBackground().setAlpha(150);
		
		final ImageView imgZoomOut =(ImageView) findViewById(R.id.imgZoomOut);
		imgZoomOut.getBackground().setAlpha(150);

		class ZoomHandler implements OnClickListener{
			@Override
			public void onClick(View v) {
				//Check if this zoom feature activated or not?
				//To save time connecting to DB, cache this flag to reuse
				if(isZoomActivated == null){
					//For now, hard-coded TRUE to enable it, will connect DB later
					isZoomActivated = BaseDialogFragment.flag;
				}
				if(isZoomActivated){
					if(v == imgZoomIn){
						zoomingByButton(true);	
					}
					else{
						zoomingByButton(false);
					}	
				}				
				else{
					Runnable callBack = new Runnable() {
						public void run() {
							isZoomActivated = true;
						}
					};
					PurchaseDialog purchaseDialog = new PurchaseDialog(Constants.LIC_ZOOM, callBack);
					purchaseDialog.show(getSupportFragmentManager(), PurchaseDialog.class.getName());
				}
			}
		};
		
		imgZoomIn.setOnClickListener(new ZoomHandler());
		imgZoomOut.setOnClickListener(new ZoomHandler());
	}
	
	private void zoomingByButton(boolean zoomIn){
		
		boolean scrollStatus = isScrolling;
		
		ProgressDialog progressDialog = ProgressDialog.show(this, null, 
				ThemeUtils.getString(this, zoomIn ? R.string.msg_zoom_in : R.string.msg_zoom_out));
		stopAutoScroll();

		float currentSize = listTextViews.size() == 0 ? 0 : listTextViews.get(0).getCurrentSize();
		float initialSize = CustomTextView.getInitialTextSize();
		float newSize = currentSize;

		if (zoomIn) {
			newSize = currentSize + ZOOM_SIZE;
		} else{
			newSize = currentSize - ZOOM_SIZE;
		}
		
		if (newSize > initialSize * 3) {
			Log.d("ZOOMING: ", "WARNING : reach maximum");
		} else if (newSize < initialSize) {
			Log.d("ZOOMING: ", "WARNING : reach minimum");
		} else {
			//Update zoom percentage into DB
			songSettings.updateSetting(SongSetting.ZOOM, String.valueOf(newSize));
			SongRepo songRepo = new SongRepo(MusicContainerActivity.this);		
			song.setSetting(songSettings.toString());
			if(songRepo.updateSong(song)){
    			for(TextView tv : listTextViews){
		    		tv.setTextSize(newSize);	
		    	}    	    				
			}
		}

		progressDialog.dismiss();
		progressDialog = null;
		
		// restore scroll status after finished zooming
		isScrolling = scrollStatus;
		if(isScrolling){
			registerAutoScroll();
		}
	}
	
	
}
