package cn.edcheung.springskills.util.guavaapp;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.google.common.util.concurrent.RateLimiter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GuavaAppApplication {

    public static void main(String[] args) {
        testStringOperation();
        testCollectionOperation();
        testCacheOperation();
        testRateLimiterOperation();
        testBloomFilterOperation();
    }

    public static void testStringOperation() {
        Map<String, String> testMap = Maps.newLinkedHashMap();
        testMap.put("Cookies", "12332");
        testMap.put("Content-Length", "30000");
        testMap.put("Date", "2018.07.04");
        testMap.put("Mime", "text/html");
        // 用:分割键值对，并用#分割每个元素，返回字符串
        // 字符串拼接，跳过空字符串
        String returnedString = Joiner.on("#").skipNulls().withKeyValueSeparator(":").join(testMap);
        System.out.println(returnedString);

        // 接上一个，内部类的引用，得到分割器，将字符串解析为map
        Splitter.MapSplitter ms = Splitter.on("#").withKeyValueSeparator(':');
        Map<String, String> returnedMap = ms.split(returnedString);
        for (Map.Entry<String, String> entry : returnedMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        // 接上一个，将字符串解析为list
        // trimResults():去除空格，omitEmptyStrings()：删除空数组
        List<String> returnedList = Splitter.on("#").trimResults().omitEmptyStrings().splitToList(returnedString);
        returnedList.forEach(System.out::println);
    }

    public static void testCollectionOperation() {
        List<String> list = Lists.newArrayList();
        Set<String> set = Sets.newHashSet();
        Map<String, String> map = Maps.newHashMap();

        HashSet<Integer> setA = Sets.newHashSet(1, 2, 3, 4, 5);
        HashSet<Integer> setB = Sets.newHashSet(4, 5, 6, 7, 8);
        Sets.SetView<Integer> union = Sets.union(setA, setB);
        System.out.println("union:" + union);
        Sets.SetView<Integer> difference = Sets.difference(setA, setB);
        System.out.println("difference:" + difference);
        Sets.SetView<Integer> intersection = Sets.intersection(setA, setB);
        System.out.println("intersection:" + intersection);

        HashMap<String, Integer> mapA = Maps.newHashMap();
        mapA.put("a", 1);
        mapA.put("b", 2);
        mapA.put("c", 3);
        HashMap<String, Integer> mapB = Maps.newHashMap();
        mapB.put("b", 20);
        mapB.put("c", 3);
        mapB.put("d", 4);
        MapDifference<String, Integer> differenceMap = Maps.difference(mapA, mapB);
        Map<String, MapDifference.ValueDifference<Integer>> entriesDiffering = differenceMap.entriesDiffering();
        // 左边差集
        Map<String, Integer> entriesOnlyLeft = differenceMap.entriesOnlyOnLeft();
        // 右边差集
        Map<String, Integer> entriesOnlyRight = differenceMap.entriesOnlyOnRight();
        // 交集
        Map<String, Integer> entriesInCommon = differenceMap.entriesInCommon();
        System.out.println(entriesDiffering);   // {b=(2, 20)}
        System.out.println(entriesOnlyLeft);    // {a=1}
        System.out.println(entriesOnlyRight);   // {d=4}
        System.out.println(entriesInCommon);    // {c=3}
    }

    public static void testCacheOperation() {
        // CacheLoader: 当检索不存在的时候，会自动的加载信息的
        CacheLoader<String, String> cacheLoader = new CacheLoader<String, String>() {
            // 如果找不到元素，会调用这里
            @Override
            public String load(String s) {
                return null;
            }
        };

        // Guava Cache提供了三种基本的缓存回收方式：基于容量回收、定时回收和基于引用回收。
        //
        // 3.基于引用的回收（Reference-based Eviction）【强（strong）、软（soft）、弱（weak）、虚（phantom】
        // 通过使用弱引用的键、或弱引用的值、或软引用的值，Guava Cache可以把缓存设置为允许垃圾回收：
        //
        // CacheBuilder.weakKeys()：使用弱引用存储键。当键没有其它（强或软）引用时，缓存项可以被垃圾回收。因为垃圾回收仅依赖恒等式（），使用弱引用键的缓存用而不是equals比较键。
        // CacheBuilder.weakValues()：使用弱引用存储值。当值没有其它（强或软）引用时，缓存项可以被垃圾回收。因为垃圾回收仅依赖恒等式（），使用弱引用值的缓存用而不是equals比较值。
        // CacheBuilder.softValues()：使用软引用存储值。软引用只有在响应内存需要时，才按照全局最近最少使用的顺序回收。考虑到使用软引用的性能影响，我们通常建议使用更有性能预测性的缓存大小限定（见上文，基于容量回收）。使用软引用值的缓存同样用==而不是equals比较值
        Cache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(1000) // 1.基于容量的回收
                .expireAfterAccess(2, TimeUnit.SECONDS) // 2.定时回收，缓存项在给定时间内没有被读/写访问，则回收
                .expireAfterWrite(3, TimeUnit.SECONDS) // 2.定时回收，缓存项在给定时间内没有被写访问（创建或覆盖），则回收
                .refreshAfterWrite(2, TimeUnit.MINUTES) // 设置缓存里的值两分钟刷新一次
                .recordStats() // 开启缓存的统计功能
                .removalListener(new MyRemovalListener()) // 失效监听器
                .build(cacheLoader);

        cache.put("Tom", "24");
        cache.put("Jack", "25");
        cache.put("Lisa", "18");

        // 显式清除
        // 个别清除：Cache.invalidate(key)
        // 批量清除：Cache.invalidateAll(keys)
        // 清除所有缓存项：Cache.invalidateAll()
        cache.invalidate("Jack");

        try {
            cache.get("Lisa", new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return null;
                }
            });
        } catch (ExecutionException e) {
            System.out.println(Throwables.getStackTraceAsString(Throwables.getRootCause(e)));
        }

        String val = cache.getIfPresent("Tom");
        System.out.println(val);
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 过期后获取为 null 值报错
        System.out.println(cache.getIfPresent("Tom"));
    }

    public static void testRateLimiterOperation() {
        // 常用的限流算法有 漏桶算法、令牌桶算法。这两种算法各有侧重点：
        // 漏桶算法：漏桶的意思就像一个漏斗一样，水一滴一滴的滴下去，流出是匀速的。当访问量过大的时候这个漏斗就会积水。
        // 漏桶算法的实现依赖队列，一个处理器从队头依照固定频率取出数据进行处理。如果请求量过大导致队列堆满那么新来的请求就会被抛弃。
        // 漏桶一般按照固定的速率流出。
        //
        // 令牌桶算法则是存放固定容量的令牌，按照固定速率从桶中取出令牌。初始给桶中添加固定容量令牌，当桶中令牌不够取出的时候则拒绝新的请求。
        // 令牌桶不限制取出令牌的速度，只要有令牌就能处理。所以令牌桶允许一定程度的突发，而漏桶主要目的是平滑流出。

        // RateLimiter 使用了令牌桶算法，提供两种限流的实现方案：
        // 1. 平滑突发限流(SmoothBurst)
        // 设置每秒放置的令牌数为 5 个，基本 0.2s 一次符合每秒 5 个的设置。保证每秒不超过 5 个达到了平滑输出的效果。
        // 在没有请求使用令牌桶的时候，令牌会先创建好放在桶中，所以此时如果突然有突发流量进来，由于桶中有足够的令牌可以快速响应。
        // RateLimiter 在没有足够令牌发放时采用滞后处理的方式，前一个请求获取令牌所需等待的时间由下一次请求来承受。
        RateLimiter rateLimiter1 = RateLimiter.create(5);
        rateLimiter1.setRate(5d);

        // 2. 平滑预热限流(SmoothWarmingUp)
        // 平滑预热限流并不会像平滑突发限流一样先将所有的令牌创建好，它启动后会有一段预热期，逐步将分发频率提升到配置的速率。
        // 比如下面例子创建一个平均分发令牌速率为 2，预热期为 3 秒钟。由于设置了预热时间是 3 秒，令牌桶一开始并不会 0.5 秒发一个令牌，
        // 而是形成一个平滑线性下降的坡度，频率越来越高，在 3 秒钟之内达到原本设置的频率，以后就以固定的频率输出。
        RateLimiter rateLimiter2 = RateLimiter.create(2, 3, TimeUnit.SECONDS);

        rateLimiter1.acquire();
        rateLimiter1.acquire(3);
        rateLimiter1.tryAcquire(1, 3, TimeUnit.SECONDS);
    }

    public static void testBloomFilterOperation() {
        BloomFilter<Long> bloomFilter = BloomFilter.create(Funnels.longFunnel(), 1000000L, 0.01D);
        bloomFilter.put(1L);
        bloomFilter.put(2L);
        bloomFilter.put(3L);

        Long val = 1L;
        if (bloomFilter.mightContain(val)) {
            System.out.println(val + " exists");
        }
    }
}

/**
 * 缓存移除监听器
 */
class MyRemovalListener implements RemovalListener<String, String> {

    @Override
    public void onRemoval(RemovalNotification<String, String> notification) {
        String reason = String.format("key=%s, value=%s, reason=%s", notification.getKey(), notification.getValue(),
                notification.getCause());
        System.out.println(reason);
    }
}