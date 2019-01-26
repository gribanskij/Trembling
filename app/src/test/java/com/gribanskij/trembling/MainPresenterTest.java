package com.gribanskij.trembling;

import android.content.SharedPreferences;
import android.content.res.Resources;

import com.gribanskij.trembling.listView.mvp.MainContract;
import com.gribanskij.trembling.listView.mvp.MainPresenter;
import com.gribanskij.trembling.model.Model;
import com.gribanskij.trembling.model.ViewEarthquake;
import com.gribanskij.trembling.model.dto_usgs.DataModel;
import com.gribanskij.trembling.utils.Mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {

    MainPresenter presenter;
    List<ViewEarthquake> buffer;

    @Mock
    MainContract.View view;
    @Mock
    Model model;
    @Mock
    SharedPreferences sharedPreferences;
    @Mock
    Mapper mapper;
    @Mock
    Resources resources;
    @Mock
    SimpleDateFormat format;


    private static final String START_TIME = "2018-10-30T18:46:19-0700";
    private static final String END_TIME = "2018-10-30T19:46:19-0700";
    private static final Float MIN_MAG = 4f;



    @Before
    public void setUp () throws Exception {
        MockitoAnnotations.initMocks(this);
        CompositeDisposable disposables = new CompositeDisposable();
        buffer = new ArrayList<>();
        presenter = new MainPresenter(model,sharedPreferences,disposables,mapper,buffer,resources,format);
        presenter.attachView(view);
    }



    @Test
    public void onSpecifyTimeSuccess () throws Exception {
        when(model.getEarthquakes(START_TIME,END_TIME,MIN_MAG)).thenReturn(Observable.just(new DataModel()));
        presenter.onSpecifyTime(START_TIME,END_TIME,MIN_MAG);
        verify(view).showProgress();
        verify(view).hideEmpty();
        verify(view).hideProgress();
        verify(view).updateEathquakeList(buffer);
        verify(view,never()).showMessage(anyInt());
    }

    @Test
    public void onSpecifyTimeError () throws Exception {
        String error = "Network error";
        when(model.getEarthquakes(START_TIME,END_TIME,MIN_MAG)).thenReturn(Observable.error(new Exception(error)));
        presenter.onSpecifyTime(START_TIME,END_TIME,MIN_MAG);
        verify(view).showProgress();
        verify(view).hideEmpty();
        verify(view).hideProgress();
        verify(view,never()).updateEathquakeList(buffer);
        verify(view).showMessage(anyInt());

    }
    @Test
    public void onSpecifyTimeNoView () throws Exception {
        presenter.detachView();
        presenter.onSpecifyTime(START_TIME,END_TIME,MIN_MAG);
        verify(view,never()).showEmpty();
        verify(view,never()).showProgress();
        verify(view,never()).hideEmpty();
        verify(view,never()).hideProgress();
        verify(view,never()).updateEathquakeList(anyList());
    }

    private List<ViewEarthquake> getFakeQuakeList (){
        List<ViewEarthquake> fakeList = new ArrayList<>();
        fakeList.add(new ViewEarthquake());
        fakeList.add(new ViewEarthquake());
        return fakeList;
    }

}