package com.hatgroup.vchord.common;

import android.graphics.Color;

import com.hatgroup.vchord.R;

public class Constants {
	public static final String NGANLUONG_MERCHANT_ID = "30294";
	public static final String NGANLUONG_ACCOUNT_ID = "266863";
	public static final String NGANLUONG_MERCHANT_PW = "vchord@hatgroup";
	public static final String NGANLUONG_MERCHANT_ACC = "trietdvn@gmail.com";
	public static final String NGANLUONG_TYPE_CARD_VMS = "VMS";
	public static final String NGANLUONG_TYPE_CARD_VNP = "VNP";
	public static final String NGANLUONG_TYPE_CARD_VIETTEL = "VIETTEL";
	public static final String NGANLUONG_TYPE_DIGITALS = "TYPE_DIGITAL";


	public static final String TAG_SEARCH = "TAG_SEARCH";
	public static final String TAG_FAVORITE = "TAG_FAVORITE";
	public static final String TAG_HELP = "TAG_HELP";
	
	public static final String[] TAB_LABELS = { "Tìm kiếm", "Yêu thích", "Trợ giúp" };
	public static final String[] HELP_TOPICS = { "Giới Thiệu", "Tác Giả", "Cập Nhật", "Hướng Dẫn Sử Dụng" };
	public static final int[] HELP_TOPIC_LAYOUT = {
		R.layout.help_introduction_layout, 
		R.layout.help_authors_layout, 
		R.layout.help_update_layout, 
		R.layout.help_user_guide
	};
	public static final int[] TAB_ICONS = { R.attr.ic_action_search,
	R.attr.ic_action_favourite, R.attr.ic_action_help};

	
	public static final int[] HIGHTED_COLORS = new int[] {
			Color.parseColor("#E9E9E9"), Color.WHITE };

	public static final String NEW_LINE_TOKEN = "\r\n";
	public static final String NEW_LINE_TOKEN2 = "\n";
	public static final String REVERSE_NEW_LINE_TOKEN = "\n\r";
	public static final boolean USE_LOCAL_DATA = true;
	public static final String HOST = "http://www.quin.vn";
	public static final String SONG_URL = HOST + "/guitar/api/song";
	public static final String SONG_UPDATE_URL = HOST + "/guitar/song/listupdatesong/?date=";
	public static final String ARTIST_UPDATE_URL = HOST + "/guitar/artist/listupdateartist/?date=";
	public static final String SONG_URL_CONVERT = HOST + "/guitar/api/zing.php?link=";
	public static final String COMMENT_URL = HOST + "/guitar/api/comment";
	public static final String ACC_INFO_URL = HOST + "/guitar/customer/account?imei=";
	public static final String PURCHASE_URL = HOST + "/guitar/customer/purchasefeature";
	public static final String DEPOSIT_URL = HOST + "/guitar/customer/deposit";
	public static final String LIST_NEW_SONG_URL = HOST + "/guitar/customer/listnewsong";
	public static final String PURCHASE_SONG_URL = HOST + "/guitar/customer/purchasesong";
	
	public static final String SONG_ID = "SONG_ID";
	public static final Integer UP = 1;
	public static final Integer DOWN = 0;
	public static final Integer MAX_LIST_ITEM_ON_PAGE = 100;
	public static final String EMPTY_SINGER_NAME = "N/A";

	public static final String WRAP_MODE = "Wrap";
	public static final String UN_WRAP_MODE = "Unwrap";
	public static final int MIN_TONE = 1;
	public static final int MAX_TONE = 7;

	/** Search constants */
	public static final String SONG_OR_ARTIST = "SONG_OR_ARTIST";
	public static final String RHYTHM = "RHYTHM";
	public static final String LEVEL = "LEVEL";
	public static final String IS_SONG_TITLE = "IS_SONG_TITLE";
	public static final String IS_SINGER_NAME = "IS_SINGER_NAME";

	public static final String TEMPLATE = "<font color='blue'>%s</font><br/>%s";
	public static final String SERIALIZED_SONG = "SERIALIZED_SONG";
	public static final boolean IS_UPDATE_ALLOWED = true;
	public static final String PRIVATE_KEY = "hIeu@ntrIet#song";
	public static final boolean PINCH_TO_ZOOM = false;
	
	public static String DEPOSITE_SUCESS_CODE = "00";
	public static String PURCHASE_SUCESS_CODE = "00";
	public static String LIC_SCROLL = "LIC_SCROLL";
	public static String LIC_ZOOM = "LIC_ZOOM";
	public static String LIC_TONE = "LIC_TONE";
	
	public static final String[][] CHORDS = {
			{ "[A]", "[Bb]", "[B]", "[C]", "[C#]", "[D]", "[D#]", "[E]", "[F]",
					"[F#]", "[G]", "[G#]" },
					
			{ "[Am]", "[A#m]", "[Bm]", "[Cm]", "[C#m]", "[Dm]", "[D#m]",
					"[Em]", "[Fm]", "[F#m]", "[Gm]", "[G#m]" },
					
			{ "[A7]", "[Bb7]", "[B7]", "[C7]", "[C#7]", "[D7]", "[D#7]",
					"[E7]", "[F7]", "[F#7]", "[G7]", "[G#7]" },
					
			{ "[Am7]", "[A#m7]", "[Bm7]", "[Cm7]", "[C#m7]", "[Dm7]", "[D#m7]",
					"[Em7]", "[Fm7]", "[F#m7]", "[Gm7]", "[G#m7]" } 
		};
	
}
