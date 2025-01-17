package com.soomla.tapjoy.example;

import android.app.Activity;
import android.content.*;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.soomla.store.*;
import com.soomla.store.data.ObscuredSharedPreferences;
import com.soomla.store.exceptions.VirtualItemNotFoundException;
import com.soomla.tapjoy.R;

public class StoreExampleActivity extends Activity{

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {

        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        mRobotView = (ImageView) findViewById(R.id.drag_img);
        mRobotView.setOnTouchListener(new MyTouchListener());
        findViewById(R.id.rightbox).setOnDragListener(new MyDragListener());

        Typeface font = Typeface.createFromAsset(getAssets(), "GoodDog.otf");
        ((TextView) findViewById(R.id.title_text)).setTypeface(font);
        ((TextView) findViewById(R.id.main_text)).setTypeface(font);

        /**
         * We initialize StoreController and add event handler to StoreEventHandlers before
         * we open the store.
         */

        /**
         * Compute your public key (that you got from the Android Market publisher site).
         *
         * Instead of just storing the entire literal string here embedded in the
         * program,  construct the key at runtime from pieces or
         * use bit manipulation (for example, XOR with some other string) to hide
         * the actual key.  The key itself is not secret information, but we don't
         * want to make it easy for an adversary to replace the public key with one
         * of their own and then fake messages from the server.
         *
         * Generally, encryption keys / passwords should only be kept in memory
         * long enough to perform the operation they need to perform.
         */
        IStoreAssets storeAssets = new MuffinRushAssets();
        StoreController.getInstance().initialize(storeAssets,
                "[YOUR PUBLIC KEY FROM GOOGLE PLAY HERE]",
                "[YOUR CUSTOM GAME SECRET HERE]");
        mEventHandler = new ExampleEventHandler(mHandler, this);

        // Checking if it's a first run and adding 10000 currencies if it is.
        // OFCOURSE... THIS IS JUST FOR TESTING.
        SharedPreferences prefs = new ObscuredSharedPreferences(SoomlaApp.getAppContext(), SoomlaApp.getAppContext().getSharedPreferences(StoreConfig.PREFS_NAME, Context.MODE_PRIVATE));
        boolean initialized = prefs.getBoolean(FIRST_RUN, false);
        if (!initialized) {
            try {
                StoreInventory.addCurrencyAmount(storeAssets.getVirtualCurrencies()[0].getItemId(), 10000);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean(FIRST_RUN, true);
                edit.commit();
            } catch (VirtualItemNotFoundException e) {
                Log.e("Example Activity", "Couldn't add first 10000 currencies.");
            }
        }

    }

    public void robotBackHome(){

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup left = (ViewGroup)findViewById(R.id.leftbox);
                ViewGroup right = (ViewGroup)findViewById(R.id.rightbox);

                if (mRobotView.getParent() != left){
                    right.removeView(mRobotView);
                    left.addView(mRobotView);
                }
            }
        });
    }

    private final class MyTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }

    private final class MyDragListener implements View.OnDragListener {
        Drawable enterShape = getResources().getDrawable(R.drawable.shape_droptarget);
        Drawable normalShape = getResources().getDrawable(R.drawable.shape);

        @Override
        public boolean onDrag(View v, DragEvent event) {
            View view = (View) event.getLocalState();
//            ViewGroup owner = (ViewGroup) view.getParent();
//            LinearLayout container = (LinearLayout) v;
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundDrawable(enterShape);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundDrawable(normalShape);
                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to ViewGroup

                    ViewGroup left = (ViewGroup)findViewById(R.id.leftbox);
                    ViewGroup right = (ViewGroup)findViewById(R.id.rightbox);

                    if (right == v){
                        left.removeView(view);
                        right.addView(view);
                        view.setVisibility(View.VISIBLE);

                        openStore();
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    view.setVisibility(View.VISIBLE);

                    v.setBackgroundDrawable(normalShape);
                default:
                    break;
            }
            return true;
        }
    }

    private void openStore() {

        Intent intent = new Intent(getApplicationContext(), StoreGoodsActivity.class);
        startActivity(intent);
    }

    private Handler mHandler = new Handler();
    private ImageView mRobotView;
    private ExampleEventHandler mEventHandler;

    private static final String PREFS_NAME      = "store.prefs";
    private static final String FIRST_RUN       = "a#AA#BB#C";
}

