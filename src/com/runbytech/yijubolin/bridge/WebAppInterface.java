package com.runbytech.yijubolin.bridge;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by wenzhili on 14-1-9.
 */
public class WebAppInterface {

    Context mContext;

    /** Instantiate the interface and set the context */
    public WebAppInterface(Context c)
    {
        mContext = c;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast)
    {
        // Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
    }

    @JavascriptInterface
    public void saveFavorite(String favoriteJson){

    }

    @JavascriptInterface
    public String getFavorites(){
        return "[]";
    }
    
    @JavascriptInterface
    public void openURL(String url){
    	
    }

}
