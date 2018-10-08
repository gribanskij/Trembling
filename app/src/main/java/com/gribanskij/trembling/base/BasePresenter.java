package com.gribanskij.trembling.base;

public abstract class BasePresenter<T extends MvpView> implements MvpPresenter<T> {

    private T view;

    @Override
    public void attachView(T mvpView) {
        view = mvpView;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void viewIsReady() {

    }

    @Override
    public void destroy() {

    }

    public T getView() {
        return view;
    }

    protected boolean isViewAttached() {
        return view != null;
    }
}
