package com.gribanskij.trembling.forecast.mvp;

import android.util.Log;

import com.gribanskij.trembling.R;
import com.gribanskij.trembling.base.BasePresenter;
import com.gribanskij.trembling.model.Model;
import com.gribanskij.trembling.model.dto_hazard.Xmlresponse;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class ForecastPresenter extends BasePresenter<ForecastContract.View> implements ForecastContract.Presenter {


    public static final String LOG_TAG = ForecastPresenter.class.getSimpleName();

    private final int DAYS_PER_MONTH = 30;
    private final int DAYS_PER_YEAR = 365;
    private final int DAYS_PER_3_YEARS = 1095;
    private final int RADIUS_KM = 70;
    private final float MAGNITUDE = 5f;

    private final int MONTH = 1;
    private final int YEAR = 2;
    private final int YEARS = 3;
    private final int ERROR = 4;

    private final int MONTH_ERROR = 5;
    private final int YEAR_ERROR = 6;
    private final int YEARS_ERROR = 7;

    private Model model;
    private CompositeDisposable disposables = new CompositeDisposable();
    private Xmlresponse xmlresponse_month;
    private Xmlresponse xmlresponse_year;
    private Xmlresponse xmlresponse_3_year;

    public ForecastPresenter(Model model) {
        this.model = model;
    }


    @Override
    public void onQueryTextSubmit(String place) {
        getView().showProgress(MONTH);
        getView().showProgress(YEAR);
        getView().showProgress(YEARS);
        query(place);
    }

    @Override
    public void onActivityReady() {
        if (xmlresponse_month != null) {
            getView().showProbability(xmlresponse_month, MONTH);
            getView().showPlaceOnMap(xmlresponse_month);
        } else {
            getView().showProbability(null, MONTH_ERROR);
        }

        if (xmlresponse_year != null) {
            getView().showProbability(xmlresponse_year, YEAR);
            getView().showPlaceOnMap(xmlresponse_year);
        } else {
            getView().showProbability(null, YEAR_ERROR);
        }

        if (xmlresponse_3_year != null) {
            getView().showProbability(xmlresponse_3_year, YEARS);
            getView().showPlaceOnMap(xmlresponse_3_year);
        } else {
            getView().showProbability(null, YEARS_ERROR);
        }

        if (xmlresponse_3_year == null && xmlresponse_year == null && xmlresponse_month == null) {
            //getView().showProgress(MONTH);
            //().showProgress(YEAR);
            //getView().showProgress(YEARS);
            //query("Irkutsk");
        }

    }

    @Override
    public void destroy() {
        super.destroy();
        disposables.dispose();
        model = null;
    }


    private void query(String location) {


        if (!getView().getNetworkAvailableAndConnected()) {
            getView().hideProgress(MONTH);
            getView().hideProgress(YEAR);
            getView().hideProgress(YEARS);
            getView().showMessage(R.string.no_internet_connection);
            return;
        }

        if (location == null) {
            location = "Irkutsk";
        }

        disposables
                .add(model.getProbability(location, MAGNITUDE, RADIUS_KM, DAYS_PER_MONTH)
                        .subscribeWith(new DisposableObserver<Xmlresponse>() {
                            @Override
                            public void onNext(Xmlresponse response) {
                                xmlresponse_month = response;
                            }

                            @Override
                            public void onError(Throwable e) {
                                getView().hideProgress(MONTH);
                                getView().showProbability(null, MONTH_ERROR);
                                xmlresponse_month = null;
                                getView().showMessage(R.string.error_loading_data);
                                Log.i(LOG_TAG, "onError: " + e.toString());
                            }

                            @Override
                            public void onComplete() {
                                getView().hideProgress(MONTH);

                                if (xmlresponse_month.getError() == null) {
                                    getView().showProbability(xmlresponse_month, MONTH);
                                    getView().showPlaceOnMap(xmlresponse_month);
                                } else {
                                    getView().showProbability(null, MONTH_ERROR);
                                    xmlresponse_month = null;
                                }

                                Log.i(LOG_TAG, "onComplete_MONTH: ");
                                Log.i(LOG_TAG, ": " + xmlresponse_month);
                            }
                        })

                );
        disposables
                .add(model.getProbability(location, MAGNITUDE, RADIUS_KM, DAYS_PER_YEAR)
                        .subscribeWith(new DisposableObserver<Xmlresponse>() {
                            @Override
                            public void onNext(Xmlresponse response) {
                                xmlresponse_year = response;
                            }

                            @Override
                            public void onError(Throwable e) {
                                getView().hideProgress(YEAR);
                                getView().showProbability(null, YEAR_ERROR);
                                xmlresponse_year = null;
                                getView().showMessage(R.string.error_loading_data);
                                Log.i(LOG_TAG, "onError: " + e.toString());
                            }

                            @Override
                            public void onComplete() {
                                getView().hideProgress(YEAR);

                                if (xmlresponse_year.getError() == null) {
                                    getView().showProbability(xmlresponse_year, YEAR);
                                    getView().showPlaceOnMap(xmlresponse_year);
                                } else {
                                    getView().showProbability(null, YEAR_ERROR);
                                    xmlresponse_year = null;
                                }


                                Log.i(LOG_TAG, "onComplete_YEAR: ");
                                Log.i(LOG_TAG, ": " + xmlresponse_year);
                            }
                        })

                );
        disposables
                .add(model.getProbability(location, MAGNITUDE, RADIUS_KM, DAYS_PER_3_YEARS)
                        .subscribeWith(new DisposableObserver<Xmlresponse>() {
                            @Override
                            public void onNext(Xmlresponse response) {
                                xmlresponse_3_year = response;
                            }

                            @Override
                            public void onError(Throwable e) {
                                getView().hideProgress(YEARS);
                                getView().showProbability(null, YEARS_ERROR);
                                xmlresponse_3_year = null;
                                getView().showMessage(R.string.error_loading_data);
                                Log.i(LOG_TAG, "onError: " + e.toString());
                            }

                            @Override
                            public void onComplete() {
                                getView().hideProgress(YEARS);

                                if (xmlresponse_3_year.getError() == null) {
                                    getView().showProbability(xmlresponse_3_year, YEARS);
                                    getView().showPlaceOnMap(xmlresponse_3_year);
                                } else {
                                    getView().showProbability(null, YEARS_ERROR);
                                    xmlresponse_3_year = null;
                                }

                                Log.i(LOG_TAG, "onComplete_3_YEARS: ");
                                Log.i(LOG_TAG, ": " + xmlresponse_3_year);
                            }
                        })

                );
    }

}
