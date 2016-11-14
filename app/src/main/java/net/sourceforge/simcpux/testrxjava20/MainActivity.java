package net.sourceforge.simcpux.testrxjava20;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import net.sourceforge.simcpux.testrxjava20.HttpUtils.HttpUtils;
import net.sourceforge.simcpux.testrxjava20.cache.MyDiskCache;
import net.sourceforge.simcpux.testrxjava20.cache.MyMemoryCache;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static io.reactivex.schedulers.Schedulers.newThread;
import static net.sourceforge.simcpux.testrxjava20.R.id.imageView;

/**

 * 在以前是必须要先 createWorker ，用 Worker 对象去 shedule， 现在可以直接在 Scheduler 用这些方法：
 * <p>
 * Schedulers.immediate(): 直接在当前线程运行，相当于不指定线程。这是默认的 Scheduler。
 * Schedulers.newThread(): 总是启用新线程，并在新线程执行操作。
 * -Schedulers.io(): I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。
 * 行为模式和 new Thread() 差不多，区别在于 io() 的内部实现是是用一个无数量上限的线程池，可以重用空闲的线程，
 * 因此多数情况下 io() 比new Thread()更有效率。不要把计算工作放在 io() 中，可以避免创建不必要的线程。
 * Schedulers.computation(): 计算所使用的 Scheduler。这个计算指的是 CPU 密集型计算，即不会被 I/O 等操作限制性能的操作，
 * 例如图形的计算。这个 Scheduler 使用的固定的线程池，大小为 CPU 核数。不要把 I/O 操作放在computation()中，
 * 否则 I/O 操作的等待时间会浪费 CPU。 另外， Android 还有一个专用的 AndroidSchedulers.mainThread()，它指定的操作将在 Android 主线程运行。
 * 有了这几个 Scheduler ，就可以使用 subscribeOn()和 observeOn()两个方法来对线程进行控制了。
 * subscribeOn()和 observeOn()两个方法来对线程进行控制
 * subscribeOn(): 指定 subscribe()所发生的线程，即 Observable.OnSubscribe被激活时所处的线程。或者叫做事件产生的线程。
 * observeOn(): 指定 Subscriber 所运行在的线程。或者叫做事件消费的线程。
 */
public class MainActivity extends AppCompatActivity {
    ArrayList list = new ArrayList<String>();
    String result = "";
    ImageView img;
    MyMemoryCache myMemoryCache;
    MyDiskCache myDiskCache;
    String url = "https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1478835647&di=9db59811a521b2bfb775c813f9d37555&src=http://cdn.duitang.com/uploads/item/201512/12/20151212094343_vGC8y.jpeg";
    Disposable d1;
    Disposable d2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView) findViewById(imageView);
        myMemoryCache = MyMemoryCache.getMyMemeryCache();
        myDiskCache = MyDiskCache.getMyDiskCache(this);
    }

    //入门使用1
    public void baseUseRxjava1() {
        Flowable f = Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) throws Exception {
                e.onNext("哈哈哈");
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER);

        f.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e("====", "==FlowableOnSubscribe==accept=" + s);
            }
        });
    }

    //入门使用2
    public void baseUseRxjava2() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("哈哈哈");
                e.onComplete();
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e("====", "==ObservableOnSubscribe==accept=" + s);
            }
        });
    }

    //入门使用3
    public void baseUseRxjava3() {

        Flowable f = Flowable.just("嘿嘿", "呵呵").flatMapIterable(new Function<String, Iterable<?>>() {
            @Override
            public Iterable<?> apply(String s) throws Exception {
                for (int i = 0; i < 10; i++) {
                    list.add("=嘿嘿===" + i + "==呵呵呵==");

                }
                Log.e("====", "flatMapIterable==apply===" + s);
                return list;
            }
        });
        f.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e("====", "flatMapIterable==accept===" + s);
            }
        });
    }

    //map操作符的使用
    public void useMapOperation() {
        Observable.fromArray(1, 2, 3, 4).map(new Function<Integer, Integer>() {

            @Override
            public Integer apply(Integer integer) throws Exception {
                return integer * 10;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e("====", "==Consumer==accept=" + integer);
            }
        });

    }

    //scan操作符的使用
    public void useScanOperation() {
//        Flowable.just("嘿嘿","呵呵","哈哈哈","嘻嘻").scan(new BiFunction<String, String, String>() {
//            @Override
//            public String apply(String s, String s2) throws Exception {
//                String result=s+s2;
//                return s2;
//            }
//        }).subscribe(new Subscriber<String>() {
//            @Override
//            public void onSubscribe(Subscription s) {
//                s.request(10);
//            }
//
//            @Override
//            public void onNext(String s) {
//                Log.e("onNext", "onNext: " + s);
//            }
//
//            @Override
//            public void onError(Throwable t) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });


        Flowable.fromArray(2, 3, 4).scan(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                return integer2;
            }
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1000);
                //Log.e("RXJAVA", "onSubscribe: " );
            }

            @Override
            public void onNext(Integer integer) {
                Log.e("RXJAVA", "item is: " + integer);
            }

            @Override
            public void onError(Throwable t) {
                //Log.e("RXJAVA", "onError: " + t.getMessage());
            }

            @Override
            public void onComplete() {
                // Log.e("RXJAVA", "onComplete: " );
            }
        });
    }

    //线程调度相关案例
    public void threadSchedulers() {
//        Flowable.just(1, 2, 3, 4)
//                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
//                .doOnComplete(new Action() {
//                    @Override
//                    public void run() throws Exception {
//                        Log.e("===doOnComplete===", "threadName:"+Thread.currentThread().getName());
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
//                .doOnComplete(new Action() {
//                    @Override
//                    public void run() throws Exception {
//                        Log.e("===Action===", "threadName:"+Thread.currentThread().getName());
//                    }
//                })
//                .subscribe(new Consumer<Integer>() {
//                    @Override
//                    public void accept(Integer integer) throws Exception {
//                        Log.e("===Schedulers线程调度===", "threadName:"+Thread.currentThread().getName());
//                        Log.e("===Schedulers线程调度===", "integer:" + integer);
//                    }
//                });


        Observable.just(1, 2, 3, 4) // IO 线程，由 subscribeOn() 指定
                .subscribeOn(Schedulers.io())
                .observeOn(newThread())
                .map(new Function<Integer, Object>() {

                    @Override
                    public Object apply(Integer integer) throws Exception {
                        return integer + 4;
                    }
                }) // 新线程，由 observeOn() 指定
                .observeOn(Schedulers.io())
                .map(new Function<Object, Object>() {
                    @Override
                    public Object apply(Object o) {
                        return (Integer) o + 2;
                    }
                }) // IO 线程，由 observeOn() 指定
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        Log.e("=====", "Consumer===== :" + o);
                    }
                });  // Android 主线程，由 observeOn() 指定
    }

    public void downloadImg(View view) {
        Flowable.just("https://www.baidu.com/img/bd_logo1.png")
                .subscribeOn(newThread())
                .map(new Function<String, Bitmap>() {

                    @Override
                    public Bitmap apply(String s) throws Exception {
                        InputStream is = HttpUtils.getInputStream(s);
                        Bitmap bmp = BitmapFactory.decodeStream(is);
                        return bmp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        img.setImageBitmap(bitmap);
                    }
                });
    }


    //使用timer做定时操作。表示“指定秒数后执行指定操作”
    public void timer() {
        //表示5s后输出内容
        Flowable.timer(5, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e("======", "==延迟5s执行====" + aLong);
                    }
                });
    }

    //
//    //使用interval做周期性操作,表示每隔指定数执行指定的操作
    public void intervalDeliver() {
        d1 = Flowable.interval(2, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                //aLong为时间
                Log.e("====", "===accept=心跳==" + aLong);
            }
        });

    }

    //
    //使用schedulePeriodically做轮询请求
    public void scheduleTask() {
        //直接进行网络请求，本身就是异步的，不需要开启线程
        d1 = Schedulers.newThread().schedulePeriodicallyDirect(new Runnable() {
            @Override
            public void run() {
                //网络请求得到结果让观察者观察到进行下一步处理
                //doNetworkCallAndGetStringResult()
                InputStream is = HttpUtils.getInputStream("http://www.baidu.com/");
                String str = HttpUtils.getDataByInputStream(is);
                Log.e("=====", "====str===" + str);
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS);
//        // 使用Rxjava模式进行轮询请求
//        Observable.create(new ObservableOnSubscribe<String>() {
//
//            @Override
//            public void subscribe(ObservableEmitter<String> e) throws Exception {
//                e.onNext("http://www.baidu.com/");
//                e.onComplete();
//            }
//        }).subscribe(new Consumer<String>() {
//            @Override
//            public void accept(final String s) throws Exception {
//                d2 = Schedulers.newThread().schedulePeriodicallyDirect(new Runnable() {
//                    @Override
//                    public void run() {
//                        //网络请求得到结果让观察者观察到进行下一步处理
//                        //doNetworkCallAndGetStringResult()
//                        InputStream is = HttpUtils.getInputStream(s);
//                        String str = HttpUtils.getDataByInputStream(is);
//                        Log.e("=====", "====str===" + str);
//                    }
//                    //参数1:初始事件，参数2:间隔或者轮询时间
//                }, 1000, 1000, TimeUnit.MILLISECONDS);
//
//            }
//        });
    }

    //使用concat和first做三级缓存
    public void concatAndFirstOperation() {
        //依次检查memory、disk和network中是否存在数据，任何一步一旦发现数据后面的操作都不执行。
        // 检查memory
        Observable<Bitmap> memory = Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> e) throws Exception {
                Bitmap bmp = myMemoryCache.get(url);
                if (bmp != null) {
                    e.onNext(bmp);
                    Log.e("=====", "==memory==onNext=");
                } else {
                    e.onComplete();
                    Log.e("=====", "==memory==内存没有找到该图片的缓存=");
                }
            }
        });
        //检查disk
        Observable<Bitmap> disk = Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> e) throws Exception {
                Bitmap bmp = myDiskCache.getBitmap(url);
                if (bmp != null) {
                    myMemoryCache.putBitmap(url, bmp);
                    e.onNext(bmp);
                    Log.e("=====", "==disk==onNext=");
                } else {
                    e.onComplete();
                    Log.e("=====", "==disk==磁盘中没有找到该图片的缓存=");
                }
            }
        });
        //网络执行
        Observable<Bitmap> network = Observable.create(
                new ObservableOnSubscribe<Bitmap>() {
                    @Override
                    public void subscribe(ObservableEmitter<Bitmap> e) throws Exception {
                        InputStream is = HttpUtils.getInputStream(url);
                        Bitmap bmp = BitmapFactory.decodeStream(is);
                        if (bmp != null) {
                            Log.e("=====", "==network==onNext=");
                            myMemoryCache.put(url, bmp);
                            myDiskCache.putBitmap(url, bmp);
                            e.onNext(bmp);

                        } else {
                            e.onComplete();
                            Log.e("=====", "==network==图片下载失败=");
                        }
                    }
                }).doOnNext(new Consumer<Bitmap>() {
            @Override
            public void accept(Bitmap bitmap) throws Exception {

            }
        });

        //依次检查memory、disk、network
        Observable.concat(memory, disk, network)
                .first(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                //事件发生在子线程
                .subscribeOn(newThread())
                //事件消费发生在主线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        Log.e("=====", "=======" + bitmap);
                        img.setImageBitmap(bitmap);
                    }

                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (d1!= null) {
            d1.dispose();
        }
        if (d2 != null) {
            d2.dispose();
        }
    }
}
