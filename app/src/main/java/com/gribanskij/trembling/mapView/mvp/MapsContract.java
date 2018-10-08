package com.gribanskij.trembling.mapView.mvp;


import com.gribanskij.trembling.base.MvpPresenter;
import com.gribanskij.trembling.base.MvpView;
import com.gribanskij.trembling.model.ViewEarthquake;

import java.util.List;


public interface MapsContract {

    interface View extends MvpView {

        void showProgress();

        void hideProgress();

        void showEmpty();

        void hideEmpty();

        void showMessage(int messageResId);

        void updateMap(List<ViewEarthquake> earthquakes);

        boolean getNetworkAvailableAndConnected();

    }

    interface Presenter extends MvpPresenter<MapsContract.View> {

        void mapReady();

        void refresh();

        void onChangeMag(int magId);

        void onChangeTimeInterval(int intervalId);

        void onChangePlace(int placeId);

        void onChangeLocation(int radius, double lat, double lon);

    }

}
