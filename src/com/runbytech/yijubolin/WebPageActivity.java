package com.runbytech.yijubolin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

@SuppressLint("SetJavaScriptEnabled")
public class WebPageActivity extends SherlockActivity {

	private ProgressDialog progressDialog;
	private boolean useProgressDialog = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);

		WebView webView = (WebView) findViewById(R.id.general_webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new HideAddressViewClient());

		String url = this.getIntent().getStringExtra("url");
		webView.loadUrl(url);

		// setting app status...
		String sysModel = android.os.Build.MODEL;
		Log.d("WebPageActivity", "current model: " + sysModel);
		if (sysModel.toLowerCase().contains("xiaomi"))
			useProgressDialog = false;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.back, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		WebPageActivity.this.finish();

		return true;
	}

	/**
	 * 用Handler来更新UI
	 */
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				progressDialog = ProgressDialog.show(WebPageActivity.this, "",
						"正载入数据...", true, true);
			}
			// 关闭ProgressDialog, work in Device only!
			if (msg.what == 0 && progressDialog != null)
				progressDialog.dismiss();

			// Log.d(TAG, "<<< handleMessage");

		}
	};

	private class HideAddressViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// open dialog in handler, not directly here
			if (useProgressDialog)
				handler.sendEmptyMessage(1);
		}

		@Override
		public void onPageFinished(WebView view, String url) {

			// close dialog in handler, also, and must
			if (useProgressDialog)
				handler.sendEmptyMessage(0);

			// Log.d(TAG, ">>> onPageFinished");
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// 向handler发消息
			handler.sendEmptyMessage(0);

			Log.d("WebPageActivity", ">>> onPageError");
		}

	}

}
