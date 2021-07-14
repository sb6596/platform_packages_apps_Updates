package com.aospextended.ota.view;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Shubham Singh on 15/07/21.
 */
public class SlideItemAnimator extends DefaultItemAnimator {

    private OnRecyclerViewListener listener;

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        View view = holder.itemView;
        ViewCompat.animate(view).cancel();
        PropertyValuesHolder translateX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -1 * view.getWidth(), 1f);
        // Animator set
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, translateX);
        objectAnimator.setDuration(300);
        objectAnimator.start();
        if(listener != null) {
            listener.onItemAdded();
        }

        return true;
    }

    public void setRecyclerViewListener(OnRecyclerViewListener listener) {
        this.listener = listener;
    }

    public interface OnRecyclerViewListener {
        void onItemAdded();
    }
}
