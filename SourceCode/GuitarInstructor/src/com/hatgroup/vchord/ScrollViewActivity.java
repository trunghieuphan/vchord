package com.hatgroup.vchord;
//package com.guitar.instructor;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewTreeObserver;
//import android.view.ViewTreeObserver.OnGlobalLayoutListener;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.ScrollView;
//import android.widget.SeekBar;
//import android.widget.TextView;
//
//import com.guitar.instructor.common.Constants;
//import com.guitar.instructor.utils.Parser;
//import com.guitar.instructor.utils.Utilities;
//import com.guitar.instructor.widgets.VerticalSeekBar;
//
//public class ScrollViewActivity extends Activity {
//	private TextView textView;
//	private ScrollView scrollView;
//	private ImageButton autoscrollSettingImageButton;
//	private LinearLayout linearLayout;
//
//	private int scrollPos = 0;
//	private int actualHeight = 0;
//
//	private Handler mHandler;
//	private Runnable mScrollDown;
//
//	private boolean isScrolling = false;
//	private SharedPreferences sharedPreferences;
//
//	int maxValue = 1000;
//	private long delayValue = 200;
//	private String songId;
//	private String msContent = "";
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_scroll_view);
//
//		// Get songId
//		songId = getIntent().getStringExtra(Constants.SONG_ID);
//		songId = songId != null ? songId : "";
//
//		// Get old or default delayValue
//		sharedPreferences = getPreferences(MODE_PRIVATE);
//		delayValue = sharedPreferences.getLong(songId, delayValue);
//
//		scrollView = (ScrollView) findViewById(R.id.vertical_scrollview_id);
//		linearLayout = (LinearLayout) findViewById(R.id.vertical_outer_layout_id);
//		autoscrollSettingImageButton = (ImageButton) findViewById(R.id.autoscrollSettingImageButton);
//		final Button btnDown = (Button) findViewById(R.id.btnDown);
//		final Button btnUp = (Button) findViewById(R.id.btnUp);
//
//		msContent = Parser.readFileToString();
//		addTextView(msContent, 1);
//
//		ViewTreeObserver vto = linearLayout.getViewTreeObserver();
//		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//			@Override
//			public void onGlobalLayout() {
//
//				linearLayout.getViewTreeObserver()
//						.removeGlobalOnLayoutListener(this);
//
//				getScrollMaxAmount();
//
//				runAutoScroll();
//			}
//		});
//
//		autoscrollSettingImageButton
//				.setOnClickListener(new ImageButton.OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//
//						stopAutoScroll();
//
//						// Dialog
//						final Dialog autoscrollSettingDialog = new Dialog(
//								ScrollViewActivity.this);
//						autoscrollSettingDialog
//								.setContentView(R.layout.autoscroll_setting);
//						autoscrollSettingDialog.setTitle("Autoscroll Setting");
//						autoscrollSettingDialog.setCancelable(false);
//
//						final TextView seekBarValue = (TextView) autoscrollSettingDialog
//								.findViewById(R.id.seekBarValueTextView);
////						final Button startButton = (Button) autoscrollSettingDialog
////								.findViewById(R.id.startButton);
//						final VerticalSeekBar seekBar = (VerticalSeekBar) autoscrollSettingDialog
//								.findViewById(R.id.verticalSeekbar);
//
//						int tempValue = calSeekBarValue();
//						seekBar.setProgress(tempValue);
//						seekBarValue.setText("" + tempValue);
//						// Vertical SeekBar
//						seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//							@Override
//							public void onStopTrackingTouch(SeekBar seekBar) {
//							}
//
//							@Override
//							public void onStartTrackingTouch(SeekBar seekBar) {
//							}
//
//							@Override
//							public void onProgressChanged(SeekBar seekBar,
//									int progress, boolean fromUser) {
//
//								seekBarValue.setText("" + (progress + 1));
//							}
//						});
//
////						// Start Button
////						startButton
////								.setOnClickListener(new Button.OnClickListener() {
////
////									@Override
////									public void onClick(View v) {
////										autoscrollSettingDialog.dismiss();
////										// Calculate new delayValue
////										calDelayValue(Double
////												.parseDouble((String) seekBarValue
////														.getText()));
////										SharedPreferences.Editor editor = sharedPreferences
////												.edit();
////										editor.putLong(songId, delayValue);
////										editor.commit();
////										runAutoScroll();
////									}
////								});
//
//						autoscrollSettingDialog.show();
//					}
//				});
//
//		btnDown.setOnClickListener(new Button.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				msContent = Utilities.convertChords(msContent, Constants.DOWN);
//				// TODO update GUI
//			}
//		});
//
//		btnUp.setOnClickListener(new Button.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				msContent = Utilities.convertChords(msContent, Constants.UP);
//				// TODO update GUI
//			}
//		});
//	}
//
//	private void addTextView(String chordLyrics, int upDown) {
//
//		// String newContent = Utilities.convertChords(chordLyrics, upDown);
//
//		textView = new TextView(this);
//		textView.setText(chordLyrics);
//		textView.setClickable(true);
//		textView.setEnabled(true);
//		textView.setFocusable(true);
//		textView.setFocusableInTouchMode(true);
//		textView.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				if (isScrolling == true) {
//					stopAutoScroll();
//				} else {
//					runAutoScroll();
//				}
//
//			}
//		});
//		linearLayout.addView(textView);
//	}
//
//	private void calDelayValue(double seekBarValue) {
//		delayValue = (long) (maxValue / seekBarValue);
//	}
//
//	private int calSeekBarValue() {
//		return (int) (maxValue / delayValue);
//	}
//
//	private void runAutoScroll() {
//		if (isScrolling == false) {
//			if (sharedPreferences != null) {
//				delayValue = sharedPreferences.getLong(songId, delayValue);
//			}
//			mHandler = new Handler();
//			mScrollDown = new Runnable() {
//				public void run() {
//					moveScrollView();
//					mHandler.postDelayed(this, delayValue);
//					if (scrollPos == 0) {
//						mHandler.removeCallbacks(this);
//					}
//				}
//			};
//			mHandler.postDelayed(mScrollDown, delayValue);
//			isScrolling = true;
//		}
//	}
//
//	private void stopAutoScroll() {
//		if (isScrolling == true) {
//			mHandler.removeCallbacks(mScrollDown);
//			isScrolling = false;
//		}
//	}
//
//	private void getScrollMaxAmount() {
//		actualHeight = (linearLayout.getMeasuredHeight() - (256 * 3));
//	}
//
//	private void moveScrollView() {
//		scrollPos = (int) (linearLayout.getScrollY() + 10.0);
//		if (scrollPos >= actualHeight) {
//			scrollPos = 0;
//		}
//		scrollView.smoothScrollBy(0, scrollPos);
//		Log.e("moveScrollView", "moveScrollView");
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//		stopAutoScroll();
//	}
//
//	@Override
//	protected void onStop() {
//		super.onStop();
//		stopAutoScroll();
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		stopAutoScroll();
//		sharedPreferences.edit().clear().commit();
//		mHandler = null;
//		mScrollDown = null;
//	}
//}
