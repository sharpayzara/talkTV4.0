package com.sumavision;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by sharpay on 16-10-18.
 */
public class RxJavaTest {
    List<Integer> testList;
    List<String> testCharList;
    public RxJavaTest(){
        testList = new ArrayList<>();
        testCharList = new ArrayList<>();
        for(int i = 0; i < 9; i++){
            testList.add(i);
            testCharList.add(String.valueOf((char)(65 + i)));
        }
    }
//****************************以下是创建操作***************************
    /**
     *  desc  创建一个Observable
     */
    @Test
    public void testCreate(){
        String str = "Sharpayzara";
        Observable.create((new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(str);
            }
        })).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(s);
            }
        });
        //lambda简易表达式
        Observable.create(subscriber -> subscriber.onNext(str))
                .subscribe(result-> System.out.println(result));
    }

    /**
     *  desc  创建一个发射指定值的Observable
     */
    @Test
    public void testJust(){
        Observable.just("222","333")
                .subscribe(str -> System.out.println(str));
    }


    /**
     *  desc  直到有观察者订阅时才创建Observable，并且为每个观察者创建一个新的Observable
     */
    @Test
    public void testDefer(){
        Observable.defer(() -> Observable.just("testDefer"))
                .subscribe(str -> System.out.println(str));
    }

    /**
     *  desc  迭代Iterable
     */
    @Test
    public void testFrom(){
        List<String> list = new ArrayList<>();
        list.add("http://");
        list.add("ftp://");
        Observable.from(list)
                .filter(a -> false)
                .subscribe(url -> System.out.println(url));
    }

    /**
     *  desc 延时+间隔循环 创建一个按固定时间间隔发射整数序列的Observable 单位毫秒
     */
    @Test
    public void testInterval(){
        Observable.interval(1, 3000, TimeUnit.MILLISECONDS) //junit测试不出来
                //延时3000 ，每间隔3000，时间单位
                .subscribe(i -> System.out.println(i));
    }

    /**
     *  desc  定时器 它在一个给定的延迟后发射一个特殊的值 单位毫秒
     */
    @Test
    public void testTimer(){
        Observable.timer(1 , TimeUnit.MILLISECONDS)    //junit测试不出来
                .subscribe(i -> System.out.println(i));
    }



    //*************************************以下是变换操作***************************
    /**
     *  desc  定期收集Observable的数据放进一个数据包裹，然后发射这些数据包裹，而不是一次发射一个值
     */
    @Test
    public void testBuffer(){
        Observable.from(testList)
                .buffer(3)
                .subscribe(input -> System.out.println(input));//默认用[]包裹数据
    }

    /**
     *  desc  Map操作符 Observable发射的每一项数据应用一个函数，执行变换操作
     */
    @Test
    public void testMap(){
        Observable.from(testList)
                .map(i -> i + "map")  //返回的是一个发射的值 而不是Observable
                .subscribe(str -> System.out.println(str));
    }


    /**
     *  desc  flatMap操作符 FlatMap将一个发射数据的Observable变换为多个Observables，然后将它们发射的数据合并后放进一个单独的Observable
     */
    @Test
    public void testFlatMap(){
        Observable.just(testList)
                .flatMap(i -> Observable.from(i))  //返回的是一个Observable 而不是发射的值
                .flatMap(j -> handData(j))
                .subscribe(str -> System.out.println(str));

       /* //实战演练
        SumaClient.getRetrofitInstance(PlayerRetrofit.class,"http://124.193.180.78:8100/mepg-api").getProgramListTopic(SumaClient.getCacheControl(),"ca1z3e") // 返回Observable<ProgramListTopic>
                .map(programListTopic -> programListTopic.getItems()) //返回一个栏目列表的list
                .flatMap(itemsBeen -> SumaClient.getRetrofitInstance(PlayerRetrofit.class).getProgramListData(itemsBeen.get(0).getId(),itemsBeen.get(0).getName(),1,30)) //根据第一个栏目的id和name获取一页节目列表数据
                .map(programListData -> programListData.getItems()) //从返回的json对象中取出节目列表数据
                .flatMap(itemsBeen1 -> Observable.from(itemsBeen1)) //遍历节目列表数据 发射带有单个节目数据的Observable
                .subscribe(itemsBean -> System.out.println(itemsBean.getName() + itemsBean.getPrompt())); //取出打印出节目名称和评分信息*/
    }

    /**
     *  desc  处理数据
     */
    public Observable<String> handData(Integer num){
        return Observable.create(subscriber -> subscriber.onNext(num+"flatmap"));
    }

    /**
     *  desc 过滤操作符 只发射通过了谓词测试的数据项
     */
    @Test
    public void testFilter(){
        Observable.from(testList)
                .filter(x -> x > 5)   //即 x -> x > 5?true:false
                .subscribe( x -> System.out.println(x));
    }

    /**
     *  desc  只发射第一项（或者满足某个条件的第一项）数据
     */
    @Test
    public void testFirst(){
        /*Observable.from(testList)
                .first()
                .subscribe(x -> System.out.println(x));*/
        Observable.from(testList)
                .first(x -> x > 5)
                .subscribe(x -> System.out.println(x));
    }

    /**
     *  desc  只发射最后项（或者满足某个条件的最后项）数据
     */
    @Test
    public void testLast(){
        Observable.from(testList)
                .last(x -> x < 5)
                .subscribe(x -> System.out.println(x));
    }

    /**
     *  desc  抑制Observable发射的前N项数据
     */
    @Test
    public void testSkip(){
        Observable.from(testList)
                .skip(2) //丢弃前两项发射的数据
                .subscribe(x -> System.out.println(x));
    }

    /**
     *  desc  抑制Observable发射的后N项数据
     */
    @Test
    public void testSkipLast(){
        Observable.from(testList)
                .skipLast(2) //丢弃后两项发射的数据
                .subscribe(x -> System.out.println(x));
    }

    /**
     *  desc  只发生Observable的前N项数据
     */
    @Test
    public void testTake(){ //takeLast同理
        Observable.from(testList)
                .take(2) //只接受前两项发射的数据
                .subscribe(x -> System.out.println(x));
    }

    //****************************以下是合并（多输入 单输出）操作***************************
    /**
     *  desc  通过一个函数将多个Observables的发射物结合到一起，基于这个函数的结果为每个结合体发射单个数据项。
     */
    @Test
    public void testZip(){
        testCharList.add("J");
        Observable.zip(Observable.from(testList),Observable.from(testCharList),(x,y) -> x+y)
                .subscribe(z -> System.out.println(z));

    }
    /**
     *  desc  使用Merge操作符你可以将多个Observables的输出合并
     */
    @Test
    public void testMerge(){
        Observable.merge(Observable.from(testList),Observable.from(testCharList))
                .subscribe(z -> System.out.println(z));
    }


    //****************************以下是辅助操作***************************
    /**
     *  desc  在序列的开头插入指定的数据序列
     */
    @Test
    public void testStartWith() {
        Observable.from(testCharList)
                .startWith("test")
                .subscribe(z -> System.out.println(z));

    }

    /**
     *  desc  延迟一段指定的时间再发射来自Observable的发射物
     */
    @Test
    public void testDelay(){
        new Thread(() -> {  Observable.from(testCharList) //看来junit在线程里也测不出来
                .delay(1000,TimeUnit.MILLISECONDS)
                .subscribe(z -> System.out.println(z));
        }).start();
    }

    /**
     *  desc  仅在过了一段指定的时间还没发射数据时才发射一个数据
     */
    @Test
    public void testDeBounce(){
        Observable.from(testCharList)
                .delay(2,TimeUnit.MILLISECONDS)//每2毫秒发一次
                .debounce(10,TimeUnit.MILLISECONDS)//每10毫秒接受一次 a，b，c，d会被丢弃 e通过，
                .subscribe(x -> System.out.println(x));
    }
    /**
     *  desc  与debounce类似 作用都是减少函数被调用的次数 它会定期发射这个时间段里源Observable发射的第一个数据
     */
    @Test
    public void testThrottleFirst(){
        Observable.from(testCharList)
                .delay(10,TimeUnit.MILLISECONDS)
                .throttleFirst(30,TimeUnit.MILLISECONDS)  //例如用于防止按钮重复点击
                .subscribe(z -> System.out.println(z));
    }
    /**
     *  desc  对原始Observable的一个镜像，如果过了一个指定的时长仍没有发射数据，它会发一个错误通知
     */
    @Test
    public void testTimeout(){
        Observable.from(testCharList)
                .delay(10, TimeUnit.MILLISECONDS)
                .timeout(5, TimeUnit.MILLISECONDS)
                .subscribe(z -> System.out.println(z),error -> System.out.println("error"));
    }

    /**
     *  desc  处理数据 不改变数据流 （日志保存等操作）
     */
    @Test
    public void testDoOnNext(){
        Observable.from(testCharList)
                .doOnNext(x -> saveData(x))
                .subscribe(y -> System.out.println(y));
    }
    private void saveData(String str){
        //保存数据的操作
    }

}
