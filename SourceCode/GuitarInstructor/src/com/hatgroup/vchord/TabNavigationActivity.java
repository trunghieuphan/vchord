package com.hatgroup.vchord;

import java.lang.reflect.Array;
import java.util.Arrays;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import com.hatgroup.vchord.R;
import com.hatgroup.vchord.common.Constants;
import com.hatgroup.vchord.fragments.FavouriteFragment;
import com.hatgroup.vchord.fragments.HelpFragment;
import com.hatgroup.vchord.fragments.SearchFragment;
import com.hatgroup.vchord.fragments.TheFragment;
import com.hatgroup.vchord.utils.ThemeUtils;

public class TabNavigationActivity extends SherlockFragmentActivity implements
		TabListener {

	String tabTags[] = { Constants.TAG_SEARCH, Constants.TAG_FAVORITE,
			Constants.TAG_HELP };

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_page);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);

		String tabLabels[] = {
				ThemeUtils.getString(this, R.string.text_search),
				ThemeUtils.getString(this, R.string.text_favorite),
				ThemeUtils.getString(this, R.string.text_help) };

		int iconResIds[] = { R.attr.ic_action_search,
				R.attr.ic_action_favourite, R.attr.ic_action_help };

		LinearLayout tabView = null;
		for (int i = 0; i < tabLabels.length; i++) {
			try {
				// render view
				tabView = (LinearLayout) LayoutInflater.from(this).inflate(
						R.layout.tab_layout, null);
				ImageView tabIcon = (ImageView) tabView
						.findViewById(R.id.tabIcon);
				tabIcon.setImageResource(ThemeUtils.getResId(this.getTheme(),
						iconResIds[i]));
				tabIcon.setBackgroundColor(Color.TRANSPARENT);

				TextView tv = (TextView) tabView.findViewById(R.id.tabText);
				tv.setTextAppearance(this,
						ThemeUtils.getResId(this.getTheme(), R.attr.text_tab));
				tv.setText(tabLabels[i]);

				// create tab
				ActionBar.Tab tab = actionBar.newTab();
				tab.setTag(tabTags[i]);
				tab.setCustomView(tabView);
				tab.setTabListener(this);

				// add tab
				actionBar.addTab(tab);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction transaction) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction transaction) {
		String tabTag = tab.getTag().toString();

		FragmentManager fragmentManager = getSupportFragmentManager();
		TheFragment fragment = (TheFragment) fragmentManager
				.findFragmentByTag(tabTag);

		if (fragment == null) {
			if (tab.getTag().toString().equals(Constants.TAG_SEARCH)) {
				fragment = new SearchFragment();
			} else if (tab.getTag().toString().equals(Constants.TAG_FAVORITE)) {
				fragment = new FavouriteFragment();
			} else if (tab.getTag().toString().equals(Constants.TAG_HELP)) {
				fragment = new HelpFragment();
			} else {
			}

		}

		FragmentTransaction trans = fragmentManager.beginTransaction();
		trans.replace(R.id.fragment_container, fragment);
		trans.commit();

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction transaction) {
		// FragmentManager fragmentManager = getSupportFragmentManager();
		// FragmentTransaction trans = fragmentManager.beginTransaction();
		// TheFragment fragment = (TheFragment) getSupportFragmentManager()
		// .findFragmentByTag(tab.getTag().toString());
		// if (fragment != null) {
		// //trans.replace(R.id.fragment_container, fragment);
		// trans.detach(fragment);
		// trans.commit();
		// }

		// TheFragment fragment = (TheFragment) getSupportFragmentManager()
		// .findFragmentByTag(tab.getTag().toString());
		// if (fragment != null) {
		// transaction.remove(fragment);
		// }

		// if (Arrays.asList(this.tabTags).contains(tab.getTag().toString())) {
		//
		//
		// }

	}
}
