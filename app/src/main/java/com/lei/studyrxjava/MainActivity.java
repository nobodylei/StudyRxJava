package com.lei.studyrxjava;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_hello, btn_map, btn_zip, btn_concat, btn_flatmap, btn_second,
                    btn_concatMap, btn_distinct, btn_filter, btn_Buffer, btn_timer;
    private TextView tv_hello;
    private Observable mObservable;
    private static final String TAG = "tag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btn_hello = findViewById(R.id.btn_hello);
        btn_map = findViewById(R.id.btn_map);
        btn_zip = findViewById(R.id.btn_zip);
        btn_concat = findViewById(R.id.btn_concat);
        btn_flatmap = findViewById(R.id.btn_flatmap);
        btn_concatMap = findViewById(R.id.btn_concatmap);
        btn_distinct = findViewById(R.id.btn_distinct);
        btn_filter = findViewById(R.id.btn_filter);
        btn_Buffer = findViewById(R.id.btn_buffer);
        btn_timer = findViewById(R.id.btn_timer);
        btn_second = findViewById(R.id.btn_second);
        tv_hello = findViewById(R.id.tv_hello);
        tv_hello.setMovementMethod(new ScrollingMovementMethod());
        btn_hello.setOnClickListener(this);
        btn_map.setOnClickListener(this);
        btn_zip.setOnClickListener(this);
        btn_concat.setOnClickListener(this);
        btn_flatmap.setOnClickListener(this);
        btn_concatMap.setOnClickListener(this);
        btn_distinct.setOnClickListener(this);
        btn_filter.setOnClickListener(this);
        btn_Buffer.setOnClickListener(this);
        btn_timer.setOnClickListener(this);
        btn_second.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_hello:
                tv_hello.setText("");
                mObservable = Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        Log.e(TAG, "Observable emit 1" + "\n");
                        emitter.onNext(1);
                        Log.e(TAG, "Observable emit 2" + "\n");
                        emitter.onNext(2);
                        Log.e(TAG, "Observable emit 3" + "\n");
                        emitter.onNext(3);
                        Log.e(TAG, "Observable emit 4" + "\n");
                        emitter.onNext(4);
                        emitter.onComplete();
                    }
                });
                mObservable.subscribe(new Observer<Integer>() {
                    private int i;
                    private Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe: " + d.isDisposed() + "\n");
                        disposable = d;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext: " + integer + "\n");
                        String str = tv_hello.getText() + " " + integer;
                        tv_hello.setText(str);
                        i++;
                        if (i == 2) {
                            //在Rxjava2中，新增Disposable可以做到切断操作，
                            //让Observer观察者不在接收上游事件
                            disposable.dispose();
                            Log.e(TAG, "onNext：isDisposable:" + disposable.isDisposed() + "\n");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError : value : " + e.getMessage() + "\n");
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete" + "\n");
                    }
                });
                break;
            case R.id.btn_map:
                tv_hello.setText("");//Map
                mObservable = Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        emitter.onNext(1);
                        emitter.onNext(2);
                        emitter.onNext(3);
                    }
                });
                mObservable.map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return "This is result " + integer;
                    }
                }).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG, "map : accept: " + s + "\n");
                        String str = tv_hello.getText() + s + " ";
                        tv_hello.setText(str);
                    }
                });
                break;
            case R.id.btn_zip://zip
                tv_hello.setText("");
                mObservable = Observable.zip(getStringObservable(), getIntegerObservable(),
                        new BiFunction<String, Integer, String>() {
                            @Override
                            public String apply(String str, Integer integer) throws Exception {
                                return str + integer;
                            }
                        });
                mObservable.subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG, "zip :accept: " + s + "\n");
                        String str = tv_hello.getText() + s + " ";
                        tv_hello.setText(str);
                    }
                });
                break;
            case R.id.btn_concat://Concat
                tv_hello.setText("");
                mObservable = Observable.concat(Observable.just(1, 2, 3),
                        Observable.just(4, 5, 6));
                mObservable.subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "concat :accept: " + integer + "\n");
                        String str = tv_hello.getText() + " " + integer;
                        tv_hello.setText(str);
                    }
                });
                break;
            case R.id.btn_flatmap:
                tv_hello.setText("");
                mObservable = Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        emitter.onNext(1);
                        emitter.onNext(2);
                        emitter.onNext(3);
                    }
                }).flatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer integer) throws Exception {
                        List<String> list = new ArrayList<>();
                        for (int i = 0; i < 3; i++) {
                            list.add("I am value " + integer);
                        }
                        int delayTime = (int)(1 + Math.random() * 10);
                        return Observable.fromIterable(list).delay(delayTime, TimeUnit.MILLISECONDS);
                    }
                });
                mObservable.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                Log.e(TAG, "flatMap :accept: " + s + "\n");
                                String str = tv_hello.getText() + " " + s;
                                tv_hello.setText(str);
                            }
                        });
                break;
            case R.id.btn_concatmap:
                tv_hello.setText("");
                mObservable = Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        emitter.onNext(1);
                        emitter.onNext(2);
                        emitter.onNext(3);
                    }
                }).concatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer integer) throws Exception {
                        List<String> list = new ArrayList<>();
                        for (int i = 0; i < 3; i++) {
                            list.add("I am value " + integer);
                        }
                        int delayTime = (int)(1 + Math.random() * 10);
                        return Observable.fromIterable(list).delay(delayTime, TimeUnit.MILLISECONDS);
                    }
                });
                mObservable.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                Log.e(TAG, "flatMap :accept: " + s + "\n");
                                String str = tv_hello.getText() + " " + s;
                                tv_hello.setText(str);
                            }
                        });
                break;
            case R.id.btn_distinct:
                tv_hello.setText("");
                mObservable = Observable.just(1,2,2,3,3,4,4,5,5,4);
                mObservable.distinct().subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "distinct :accept: " + integer + "\n");
                        String str = tv_hello.getText() + " " + integer;
                        tv_hello.setText(str);
                    }
                });
                break;
            case R.id.btn_filter:
                tv_hello.setText("");
                mObservable = Observable.just(1, 20, 3, 5, 6,11, 22, 33, 5, 4);
                mObservable.filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer > 10;
                    }
                }).subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "filter :accept: " + integer + "\n");
                        String str = tv_hello.getText() + " " + integer;
                        tv_hello.setText(str);
                    }
                });
                break;
            case R.id.btn_buffer:
                tv_hello.setText("");
                mObservable = Observable.just(1, 2, 3, 4, 4, 5, 6, 7 )
                .buffer(3, 2);
                mObservable.subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> list) throws Exception {
                        Log.e(TAG, "buffer:accept: " + list.size() + "\n");
                        String str = tv_hello.getText() + " list size: " + list.size() + "value ";
                        for(Integer i : list) {
                            Log.e(TAG, "buffer : list : " + i);
                            str += " " + i;
                        }
                        tv_hello.setText(str + "\n");
                    }
                });
                break;
            case R.id.btn_timer:
                tv_hello.setText("");
                mObservable = Observable.timer(2, TimeUnit.SECONDS).subscribeOn(Schedulers.io());
                mObservable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                Log.e(TAG, "Time :accept: " + aLong + "\n");
                                String str = tv_hello.getText() + " " + aLong;
                                tv_hello.setText(str);
                            }
                        });
                break;
            case R.id.btn_second:
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private Observable<String> getStringObservable() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    emitter.onNext("A");
                    Log.e(TAG, "String emit : A \n");
                    emitter.onNext("B");
                    Log.e(TAG, "String emit : B \n");
                    emitter.onNext("C");
                    Log.e(TAG, "String emit : C \n");
                }
            }
        });
    }

    private Observable<Integer> getIntegerObservable() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    emitter.onNext(1);
                    Log.e(TAG, "Integer emit : 1 \n");
                    emitter.onNext(2);
                    Log.e(TAG, "Integer emit : 2 \n");
                    emitter.onNext(3);
                    Log.e(TAG, "Integer emit : 3 \n");
                    emitter.onNext(4);
                    Log.e(TAG, "Integer emit : 4 \n");
                    emitter.onNext(5);
                    Log.e(TAG, "Integer emit : 5 \n");
                }
            }
        });
    }
}
