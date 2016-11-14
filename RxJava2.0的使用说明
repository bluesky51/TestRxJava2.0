 RxJava2.0(函数式编程):
   与1.x版本不同的是Publisher的实现叫做Flowable,但是旧的Observerable也保留了；
   原因：
   由于1.x有很多事件不能正确的背压，从而抛出MissingBackpressureException，例如：
   在 RxJava1.x 中的 observeOn,因为是切换了消费者的线程，因此内部实现用队列存储事件。
   在 Android 中默认的 buffersize 大小是16，因此当消费比生产慢时，
   队列中的数目积累到超过16个，就会抛出 MissingBackpressureException


   在Rx2.0中Observable 不再支持背压，而Flowable 支持非阻塞式的背压。并且规范要求，所有的操作符强制支持背压。
   1> Flowable 中的操作符用法大多与旧有的 Observable 类似。
      备注：
       Obsesrver用于订阅Observable，而Subscriber用于订阅Flowable

   2> Subscriber比以前多了一个 onSubscribe 的方法
   3> Subscription综合了旧的 Producer 与 Subscription 的综合体。他既可以向上游请求数据，又可以打断并释放资源
   4> 旧的 Subscription被重新命名为Disposable,

   重点：
   旧的阻塞式的背压，就是根据下游的消费速度，中游可以选择阻塞住等待下游的消费，随后向上游请求数据。
   新的非阻塞就不在有中间阻塞的过程，由下游自己决定取多少，还有背压策略，如抛弃最新、抛弃最旧、缓存、抛异常等。

 使用介绍说明如下：
 1.入门用法请看baseUseRxjava1(),baseUseRxjava2()和baseUseRxjava3()三个方法
 2. RxJava的常用操作符的介绍：
  1》map()：把一个事件转换为另一个事件(用于变换Observable对象的，实现链式调用，最终将最简洁的数据传递给Subscriber对象),
   示例：useMapOperation()
  2》scan():遍历扫描操作，示例看方法：useScanOperation()
  备注：额外操作符的扩充：
   1》timer：使用timer做定时操作。表示“指定秒数后执行指定操作”
   2》interval：使用interval做周期性操作,表示每隔指定数执行指定的操作
   3》concat(memory, disk, network).first(DefaultImg):依次检查memory、disk和network中是否存在数据，任何一步一旦发现数据后面的操作都不执行。


3.RxJava线程调度：
   在不指定线程的情况下， RxJava 遵循的是线程不变的原则，
   即：在哪个线程调用 subscribe()，就在哪个线程生产事件；在哪个线程生产事件，就在哪个线程消费事件。
   如果需要切换线程，就需要用到 Scheduler（调度器:线程控制器：RxJava 通过它来指定每一段代码应该运行在什么样的线程）。
RxJava 已经内置了几个 Scheduler ，它们已经适合大多数的使用场景：
     1.Schedulers.immediate(): 直接在当前线程运行，相当于不指定线程。这是默认的 Scheduler。
     2.Schedulers.newThread(): 总是启用新线程，并在新线程执行操作。
     3.Schedulers.io(): I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。行为模式和 newThread() 差不多，
                        区别在于 io() 的内部实现是是用一个无数量上限的线程池，可以重用空闲的线程，因此多数情况下 io()
                        比 newThread() 更有效率。不要把计算工作放在 io() 中，可以避免创建不必要的线程。
     4.Schedulers.computation(): 计算所使用的 Scheduler。这个计算指的是 CPU 密集型计算，
                                 即不会被 I/O 等操作限制性能的操作，例如图形的计算。这个 Scheduler 使用的固定的线程池，
                                 大小为 CPU 核数。不要把 I/O 操作放在 computation() 中，否则 I/O 操作的等待时间会浪费 CPU。
                                 另外， Android 还有一个专用的 AndroidSchedulers.mainThread()，它指定的操作将在 Android 主线程运行。

  <<>> 使用 subscribeOn() 和 observeOn() 两个方法来对线程进行控制
   1.subscribeOn(): 指定 subscribe() 所发生的线程，即 Observable.OnSubscribe 被激活时所处的线程。
   或者叫做事件产生的线程。subscribeOn() 的位置放在哪里都可以，但它是只能调用一次的
   2.observeOn(): 指定 Subscriber 所运行在的线程。或者叫做事件消费的线程。
   observeOn() 指定的是它之后的操作所在的线程。
   因此如果有多次切换线程的需求，只要在每个想要切换线程的位置调用一次 observeOn() 即可

   备注:不同与RxJava1.x版本的是，不需要执行 createWorker,再用 Worker对象去 shedule,
    RxJava2.0可以直接在 Scheduler 使用以下这些方法：
    public Disposable scheduleDirect(Runnable task) { ... }
    public Disposable scheduleDirect(Runnable task, long delay, TimeUnit unit) { ... }
    public Disposable scheduleDirectPeriodically(Runnable task, long initialDelay,
            long period, TimeUnit unit) { ... }
    public long now(TimeUnit unit) { ... }

   案例:请看项目中方法threadSchedulers()和downloadImg(View view)方法



备注:在界面结束或者不需要观察事件的时候可以使用RxJava中的事件中Disposable的对象调用dispose()方法可以进行移除：
