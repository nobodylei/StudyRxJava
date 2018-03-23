package com.lei.studyrxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yanle on 2018/3/23.
 */

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_interval, btn_doOnNext, btn_skip, btn_take;
    private TextView tv_second;
    private Disposable mDisposable = null;
    private static final String TAG = "tag";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        initView();
    }

    private void initView() {
        btn_interval = findViewById(R.id.btn_interval);
        btn_doOnNext = findViewById(R.id.btn_do);
        btn_skip = findViewById(R.id.btn_skip);
        btn_take = findViewById(R.id.btn_take);
        tv_second = findViewById(R.id.tv_second);
        tv_second.setMovementMethod(new ScrollingMovementMethod());
        btn_interval.setOnClickListener(this);
        btn_doOnNext.setOnClickListener(this);
        btn_skip.setOnClickListener(this);
        btn_take.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_interval:
                if (mDisposable != null && !mDisposable.isDisposed()) {
                    mDisposable.dispose();
                }
                tv_second.setText("");
                mDisposable = Observable.interval(3, 2, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                Log.e(TAG, "interval :accept: " + aLong + "\n");
                                String str = tv_second.getText() + " " + aLong;
                                tv_second.setText(str);
                            }
                        });
                break;
            case R.id.btn_do://让订阅者在接收到数据之前干点有意思的事情
                if (mDisposable != null && !mDisposable.isDisposed()) {
                    mDisposable.dispose();
                }
                tv_second.setText("");
                Observable.just(1, 3, 5, 7, 9)
                        .doOnNext(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                Log.e(TAG, "doOnNext :accept: 保存" + integer + "\n");
                                String str = tv_second.getText() + "保存：" + integer;
                                tv_second.setText(str);
                            }
                        }).subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "doOnNext :accept: " + integer + "\n");
                        String str = tv_second.getText() + "," + integer;
                        tv_second.setText(str);
                    }
                });
                break;
            case R.id.btn_skip://其实作用就和字面意思一样，接受一个 long 型参数 count ，代表跳过 count 个数目开始接收。
                tv_second.setText("");
                Observable.just(1, 2 , 3, 4, 5)
                        .skip(2)
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                Log.e(TAG, "doOnNext :accept: " + integer + "\n");
                                String str = tv_second.getText() + " " + integer;
                                tv_second.setText(str);
                            }
                        });
                break;
            case R.id.btn_take://接受一个 long 型参数 count ，代表至多接收 count 个数据。
                tv_second.setText("");
                Flowable.fromArray(1, 2, 3, 4, 5)
                        .take(3)
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                Log.e(TAG, "doOnNext :accept: " + integer + "\n");
                                String str = tv_second.getText() + " " + integer;
                                tv_second.setText(str);
                            }
                        });
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}
