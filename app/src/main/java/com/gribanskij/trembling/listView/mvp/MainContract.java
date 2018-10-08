package com.gribanskij.trembling.listView.mvp;


import com.gribanskij.trembling.base.MvpPresenter;
import com.gribanskij.trembling.base.MvpView;
import com.gribanskij.trembling.model.ViewEarthquake;

import java.util.List;

public interface MainContract {

    interface View extends MvpView {

        void showProgress();

        void hideProgress();

        void showMessage(int messageResId);

        void updateEathquakeList(List<ViewEarthquake> list);

        void showEmpty();

        void hideEmpty();

        boolean getNetworkAvailableAndConnected();
    }

    interface Presenter extends MvpPresenter<View> {

        void refresh();

        void onActivityReady();

        void onChangeMag(int magId);

        void onChangeTimeInterval(int intervalId);

        void onChangePlace(int placeId);

        void onChangeLocation(int radius, double lat, double lon);
    }
}
