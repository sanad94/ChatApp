package com.example.a2017.chatapp.RecyclerTools;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.a2017.chatapp.R;

/**
 * Created by 2017 on 04/02/2017.
 */
public class ChatRoomsRecyclerTouchListner implements RecyclerView.OnItemTouchListener
{
    private GestureDetector gestureDetector ;
    private IclickListner clickListner ;
    private Context context;
    private int screenWidth ;
    private boolean isRtl;
    private WindowManager windowManager;

    public ChatRoomsRecyclerTouchListner(final Context context, final RecyclerView recyclerView, final IclickListner clickListner)
    {
        this.context=context;
        this.clickListner = clickListner ;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.screenWidth = windowManager.getDefaultDisplay().getWidth();
        isRtl();
        gestureDetector = new GestureDetector(context , new GestureDetector.SimpleOnGestureListener()
        {
            @Override
            public boolean onSingleTapUp(MotionEvent e)
            {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e)
            {
                final View child = recyclerView.findChildViewUnder(e.getX(),e.getY());
                if (child!=null && clickListner !=null )
                {
                    clickListner.onLongClick(child,recyclerView.getChildAdapterPosition(child));
                }
            }

        });
    }

    @Override
    public synchronized boolean onInterceptTouchEvent(final RecyclerView recyclerView, final MotionEvent e)
    {
         View child = recyclerView.findChildViewUnder(e.getX(),e.getY());
        if (child!=null && clickListner !=null  && gestureDetector.onTouchEvent(e))
        {
            if(isRtl)
            {
                if(e.getX() > screenWidth - getImageWidth(child))
                {
                    View image = child.findViewById(R.id.roomImage);
                    clickListner.onClick(image,recyclerView.getChildAdapterPosition(child));
                    return false;
                }
                clickListner.onClick(child,recyclerView.getChildAdapterPosition(child));
                return false;
            }
            if(e.getX() > getImageWidth(child))
            {
                clickListner.onClick(child,recyclerView.getChildAdapterPosition(child));
                return false;
            }

            View image = child.findViewById(R.id.roomImage);
            clickListner.onClick(image,recyclerView.getChildAdapterPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent e)
    {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept)
    {

    }

    private int getImageWidth(View view)
    {
      return view.findViewById(R.id.roomImage).getLayoutParams().width;
    }

    private void isRtl()
    {
        Configuration config = context.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            if(config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL)
            {
                this.isRtl = true ;
                return ;
            }
        }

        this.isRtl = false ;
    }


}
