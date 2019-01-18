package com.hatgroup.vchord.widgets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hatgroup.vchord.R;
import com.hatgroup.vchord.common.Constants;
import com.hatgroup.vchord.entities.Song;
import com.hatgroup.vchord.utils.HttpClient;
import com.hatgroup.vchord.utils.ThemeUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class CommentDialog extends DialogFragment {
	private Song song;
	private View myView;
	RatingBar ratingBar;

	public CommentDialog(Song song) {
		this.song = song;

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		builder.setTitle(ThemeUtils.getString(getActivity(),
				R.string.text_title_comment));

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout

		myView = inflater.inflate(R.layout.comment_dialog_layout, null);

		builder.setView(myView)
				// Add action buttons
				.setPositiveButton(R.string.text_submit,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								//postComment();
							}
						})
				.setNegativeButton(R.string.text_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								CommentDialog.this.getDialog().cancel();
							}
						})

		;

		final AlertDialog dialog = builder.create();

		// override here to dismiss only when submit comment successfully
		dialog.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface arg0) {
				// TODO Auto-generated method stub

				Button okButton = ((AlertDialog) dialog)
						.getButton(AlertDialog.BUTTON_POSITIVE);
				okButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (postComment()) {
							dialog.dismiss();
						} else {
						}

					}
				});
			}
		});

		ratingBar = (RatingBar) myView.findViewById(R.id.ratingBar);

		return dialog;

	}

	private boolean postComment() {

		final Context context = getActivity().getApplicationContext();

		String songId = song.getId().toString();
		String subject = ((TextView) myView
				.findViewById(R.id.txtCommentSubject)).getText().toString();
		if (subject == null || subject.equals("")) {
			Toast.makeText(
					context,
					ThemeUtils.getString(context, R.string.msg_require_subject),
					Toast.LENGTH_SHORT).show();
			return false;
		}
		String comment = ((TextView) myView
				.findViewById(R.id.txtCommentContent)).getText().toString();
		if (comment == null || comment.equals("")) {
			Toast.makeText(
					context,
					ThemeUtils.getString(context, R.string.msg_require_content),
					Toast.LENGTH_SHORT).show();
			return false;
		}

		RequestParams reqParams = new RequestParams();
		reqParams.put("song_id", songId);
		reqParams.put("subject", subject);
		reqParams.put("content", comment);
		reqParams.put("sender", "");
		reqParams.put("rating", (int) ratingBar.getRating() + "");

		HttpClient.getInstance().post(Constants.COMMENT_URL, reqParams,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(Throwable t, String respMessage) {
						super.onFailure(t, respMessage);
						Toast.makeText(
								context,
								ThemeUtils.getString(context,
										R.string.msg_error_network),
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(String response) {
						super.onSuccess(response);
						Toast.makeText(
								context,
								ThemeUtils.getString(context,
										R.string.msg_info_comment_success),
								Toast.LENGTH_SHORT).show();
					}
				});

		return true;

		/*
		 * final List<BasicNameValuePair> params = new
		 * ArrayList<BasicNameValuePair>(); params.add(new
		 * BasicNameValuePair("song_id", songId)); params.add(new
		 * BasicNameValuePair("subject", subject)); params.add(new
		 * BasicNameValuePair("content", comment)); params.add(new
		 * BasicNameValuePair("sender", "asasasa")); params.add(new
		 * BasicNameValuePair("rating", "0"));
		 * 
		 * AsyncTask<String, Void, String> asyncTask = new AsyncTask<String,
		 * Void, String>() {
		 * 
		 * @Override protected String doInBackground(String... args) { String
		 * res = WebService.performPost(Constants.COMMENT_URL, params); if(res
		 * == null){ Toast.makeText(context, ThemeUtils.getString(context,
		 * R.string.msg_error_network), Toast.LENGTH_SHORT).show(); } return
		 * res; } }; asyncTask.execute("");
		 */
	}
}
