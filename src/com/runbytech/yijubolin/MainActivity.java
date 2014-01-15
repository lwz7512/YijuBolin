package com.runbytech.yijubolin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.runbytech.yijubolin.bridge.WebAppInterface;
import com.runbytech.yijubolin.model.Favorite;
import com.runbytech.yijubolin.repository.FavoritesRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
public class MainActivity extends SherlockActivity {

    private static String TAG = "MainActivity";

    private WebView webView;
    private ProgressDialog progressDialog;
    private boolean useProgressDialog = true;
    private FavoritesRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        repo = new FavoritesRepository(this);

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new HideAddressViewClient());
        webView.addJavascriptInterface(new WebAppInterface(this){
            @Override
            public void saveFavorite(String favoriteJson){
                Favorite favorite = jsonToFavorite(favoriteJson);
                //The number of rows updated in the database. This should be 1.
                int result = repo.createOnce(favorite);
                Log.d(TAG, ">>> insert result: "+result);
                Toast.makeText(MainActivity.this, R.string.action_favorite_done, Toast.LENGTH_LONG).show();
            }

            @Override
            public String getFavorites(){
                List<Favorite> favorites = repo.getAllByOrder("create_time");
                String json = favoritesToJson(favorites);
                //Log.d(TAG, json);

                return json;
            }
            
            @Override
            public void openURL(String url){
            	Intent it = new Intent();
            	it.setClass(MainActivity.this, WebPageActivity.class);
            	it.putExtra("url", url);
            	MainActivity.this.startActivity(it);
            }
            
            
        }, "Android");//exposed js object name

        //webView.loadUrl("http://192.168.0.104:3000/ngapp/app");
        //webView.loadUrl("http://172.168.1.31:3000/ngapp/app");
        webView.loadUrl("http://162.243.231.159:3000/ngapp/dist");

        //setting app status...
        String sysModel = android.os.Build.MODEL;
        Log.d(TAG, "current model: "+sysModel);
        if(sysModel.toLowerCase().contains("xiaomi")) useProgressDialog = false;

    }//end of onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.abs__home: // home item
                if(webView.canGoBack()) {
                    webView.goBack();
                }else{
                    MainActivity.this.finish();
                }
                return true;

            case R.id.abs__favorites: // Favorite item
                webView.loadUrl("javascript:doFavorite()");

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private Favorite jsonToFavorite(String favorite){
        Favorite favobj = new Favorite();
        try {
            JSONObject json = new JSONObject(favorite);
            favobj.setName(json.optString("name"));
            favobj.setBusiness_id(json.optString("business_id"));
            favobj.setS_photo_url(json.optString("s_photo_url"));
            favobj.setCategories(json.optString("categories"));
            favobj.setRating_s_img_url(json.optString("rating_s_img_url"));
            favobj.setAddress(json.optString("address"));
            favobj.setTelephone(json.optString("telephone"));
            favobj.setLongitude(json.optString("longitude"));
            favobj.setLatitude(json.optString("latitude"));
            favobj.setCreate_time(json.optInt("create_time"));
            favobj.setMemo(json.optString("memo"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return favobj;
    }

    private String favoritesToJson(List<Favorite> favorites){
        JSONArray jsons = new JSONArray();
        try {
            for(Favorite favobj : favorites){
                JSONObject json = new JSONObject();

                json.put("name", favobj.getName());
                json.put("business_id", favobj.getBusiness_id());
                json.put("s_photo_url", favobj.getS_photo_url());
                json.put("categories", favobj.getCategories());
                json.put("rating_s_img_url", favobj.getRating_s_img_url());
                json.put("address", favobj.getAddress());
                json.put("telephone", favobj.getTelephone());
                json.put("longitude", favobj.getLongitude());
                json.put("latitude", favobj.getLatitude());
                json.put("create_time", favobj.getCreate_time());
                json.put("memo", favobj.getMemo());

                jsons.put(json);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsons.toString();
    }


    private class HideAddressViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //open dialog in handler, not directly here
            if(useProgressDialog) handler.sendEmptyMessage(1);
        }

        @Override
        public void onPageFinished(WebView view, String url){

            //close dialog in handler, also, and must
            if(useProgressDialog) handler.sendEmptyMessage(0);

            //Log.d(TAG, ">>> onPageFinished");
        }

        @Override
        public void onReceivedError (WebView view, int errorCode, String description, String failingUrl){
            
            handler.sendEmptyMessage(0);

            Log.d(TAG, ">>> onPageError");
        }

    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                progressDialog = ProgressDialog.show(MainActivity.this, "", "loading page...", true, true);
            }
            //ProgressDialog, work in Device only!
            if(msg.what == 0 && progressDialog!=null)
                progressDialog.dismiss();

            //Log.d(TAG, "<<< handleMessage");

        }};

    public boolean onKeyDown(int keyCoder,KeyEvent event){
        if(webView.canGoBack() && keyCoder == KeyEvent.KEYCODE_BACK){
            webView.goBack();   //goBack()
        }else{
            this.finish();
        }
        return true;
    }

}
