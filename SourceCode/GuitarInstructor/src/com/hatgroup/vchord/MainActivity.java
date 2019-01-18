package com.hatgroup.vchord;

import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.internal.widget.IcsAdapterView;
import com.actionbarsherlock.internal.widget.IcsAdapterView.OnItemSelectedListener;
import com.actionbarsherlock.internal.widget.IcsLinearLayout;
import com.actionbarsherlock.internal.widget.IcsSpinner;
import com.hatgroup.vchord.adapters.NavListAdapter;
import com.hatgroup.vchord.fragments.AccountInfoFragment;
import com.hatgroup.vchord.fragments.FavouriteFragment;
import com.hatgroup.vchord.fragments.HelpFragment;
import com.hatgroup.vchord.fragments.SearchFragment;
import com.hatgroup.vchord.fragments.SongPurchaseFragment;
import com.hatgroup.vchord.fragments.TheFragment;
import com.hatgroup.vchord.utils.ThemeUtils;
import com.hatgroup.vchord.utils.Utilities;

public class MainActivity extends SherlockFragmentActivity {
	// Declare Variables
	ListView list;
	NavListAdapter adapter;

	TheFragment searchFragment = new SearchFragment();
	TheFragment favoriteFagment = new FavouriteFragment();
	TheFragment helpFragment = new HelpFragment();
	TheFragment accInfoFragment = new AccountInfoFragment(); 
	TheFragment songPurchaseFragment = new SongPurchaseFragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_page);

		ActionBar actionBar = getSupportActionBar();

		String title[] = { ThemeUtils.getString(this, R.string.text_search),
				ThemeUtils.getString(this, R.string.text_favorite),
				ThemeUtils.getString(this, R.string.text_account),
				ThemeUtils.getString(this, R.string.text_update),
				ThemeUtils.getString(this, R.string.text_help),
				
				};

		int[] icon = {
				ThemeUtils.getResId(this.getTheme(), R.attr.ic_action_search),
				ThemeUtils.getResId(this.getTheme(), R.attr.ic_action_favourite),
				ThemeUtils.getResId(this.getTheme(), R.attr.ic_action_account),
				ThemeUtils.getResId(this.getTheme(), R.attr.ic_action_update),
				ThemeUtils.getResId(this.getTheme(), R.attr.ic_action_help),
				
				};

		// Generate subtitle
		String[] subtitle = new String[] {
				ThemeUtils.getString(this, R.string.text_search_desc),
				ThemeUtils.getString(this, R.string.text_favorite_desc),
				ThemeUtils.getString(this, R.string.text_account_desc),
				ThemeUtils.getString(this, R.string.text_update_desc),
				ThemeUtils.getString(this, R.string.text_help_desc),
				};

		// Pass results to NavListAdapter Class
		adapter = new NavListAdapter(this, title, subtitle, icon);

		// /////////////////////////
		// create ICS spinner
		
		
		IcsSpinner spinner = new IcsSpinner(this, null,
				R.attr.actionDropDownStyle);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onNothingSelected(IcsAdapterView<?> parent) {

			}

			@Override
			public void onItemSelected(IcsAdapterView<?> parent, View view,
					int position, long id) {
				
				Utilities.hideSoftKeyboard(view);
				
				FragmentTransaction ft = getSupportFragmentManager()
						.beginTransaction();
				// Locate Position
				switch (position) {
				case 0:
					ft.replace(R.id.fragment_container, searchFragment);
					break;
				case 1:
					ft.replace(R.id.fragment_container, favoriteFagment);
					break;
				case 2:
					ft.replace(R.id.fragment_container, accInfoFragment);
					break;
				case 3:
					ft.replace(R.id.fragment_container, songPurchaseFragment);
					break;
				case 4:
					ft.replace(R.id.fragment_container, helpFragment);
					break;				
				}
				ft.commit();
			}
		});

		IcsLinearLayout listNavLayout = (IcsLinearLayout) getLayoutInflater()
				.inflate(R.layout.abs__action_bar_tab_bar_view, null);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER;
		listNavLayout.addView(spinner, params);
		listNavLayout.setGravity(Gravity.RIGHT); // <-- align the spinner to the
													// right

		//
		// configure action bar
		actionBar.setCustomView(listNavLayout, new ActionBar.LayoutParams(
				Gravity.RIGHT));
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		
	}

	// // Not using options menu in this tutorial
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// getSupportMenuInflater().inflate(R.menu.activity_main, menu);
	// return super.onCreateOptionsMenu(menu);
	// }
}