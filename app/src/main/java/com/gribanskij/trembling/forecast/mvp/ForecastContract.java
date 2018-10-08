package com.gribanskij.trembling.forecast.mvp;

import com.gribanskij.trembling.base.MvpPresenter;
import com.gribanskij.trembling.base.MvpView;
import com.gribanskij.trembling.model.dto_hazard.Xmlresponse;

public interface ForecastContract {

    interface View extends MvpView {

        void showProgress(int period);

        void hideProgress(int period);

        void showMessage(int messageResId);

        void showProbability(Xmlresponse xmlresponse, int period);

        void showPlaceOnMap(Xmlresponse xmlresponse);

        boolean getNetworkAvailableAndConnected();


    }

    interface Presenter extends MvpPresenter<View> {
        void onQueryTextSubmit(String place);

        void onActivityReady();
    }
}
