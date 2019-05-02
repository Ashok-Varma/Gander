package com.ashokvarma.gander.internal.ui.details;

import androidx.viewpager.widget.ViewPager;

public abstract class SimpleOnPageChangedListener implements ViewPager.OnPageChangeListener {
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
    @Override
    public abstract void onPageSelected(int position);
    @Override
    public void onPageScrollStateChanged(int state) {}
}
