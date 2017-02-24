package com.example.a2017.chatapp.RecyclerTools;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a2017.chatapp.R;

/**
 * Created by 2017 on 04/02/2017.
 */

public class ContactsDividerItemDecoration extends RecyclerView.ItemDecoration
{
    private static final int [] attrs = new int []{android.R.attr.listDivider};
    private Context context;
    private Drawable drawable;

    public ContactsDividerItemDecoration(Context context)
    {
        this.context=context;
        final TypedArray array = context.obtainStyledAttributes(attrs);
        drawable =array.getDrawable(0);
        array.recycle();
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state)
    {

        int right = parent.getWidth() - parent.getPaddingRight();
        for (int i = 0 ; i < parent.getChildCount() ; i++)
        {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + drawable.getIntrinsicHeight();
            final int imageWidth = child.findViewById(R.id.contactImage).getLayoutParams().width;
            final TextView textView = (TextView) child.findViewById(R.id.contact_name);
            int textViewMargin =((ViewGroup.MarginLayoutParams) textView.getLayoutParams()).leftMargin;
            int left = imageWidth+textViewMargin;
            Configuration config = context.getResources().getConfiguration();
            if(config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL)
            {
                textViewMargin =((ViewGroup.MarginLayoutParams) textView.getLayoutParams()).rightMargin;
                left = parent.getPaddingLeft();
                right = parent.getWidth() - imageWidth - textViewMargin;
            }
            drawable.setBounds(left,top,right,bottom);
            drawable.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        outRect.set(0,0,0,drawable.getIntrinsicHeight());
    }
}
