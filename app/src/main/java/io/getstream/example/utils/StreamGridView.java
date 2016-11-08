package io.getstream.example.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

public class StreamGridView extends GridView
{
    boolean expanded = false;

    public StreamGridView(Context context)
    {
        super(context);
    }

    public StreamGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public StreamGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST));
        getLayoutParams().height = getMeasuredHeight();
    }
}