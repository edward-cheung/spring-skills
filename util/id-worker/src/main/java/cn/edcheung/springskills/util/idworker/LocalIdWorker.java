package cn.edcheung.springskills.util.idworker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * Description SnowFlake 算法，是 Twitter 开源的分布式 id 生成算法
 * <p>
 * SnowFlake算法的优点：
 * （1）高性能高可用：生成时不依赖于数据库，完全在内存中生成。
 * （2）容量大：每秒中能生成数百万的自增ID。
 * （3）ID自增：存入数据库中，索引效率高。
 * <p>
 * SnowFlake算法的缺点：
 * （1）依赖与系统时间的一致性，如果系统时间被回调，或者改变，可能会造成id冲突或者重复。
 * <p>
 * 如何解决时钟回拨问题?
 * <p>
 * 在运行中的时钟回拨
 * 每个worker中记录上一次生成ID用的时间戳lastTimestamp，每次生成成功后更新该值
 * 生成新guid时，只有当前时间戳大于lastTimestamp时方法才能继续
 * 可以采取一些补偿措施，比如当前时间戳小于lastTimestamp时，若偏差在5ms以内，则等待2倍的时间差后开始生成
 * 若两者偏差大于5ms，则立即抛出异常，避免阻塞
 * </p>
 * 服务重启时的时钟回拨
 * 通过异步定时的方式，将worker中lastTimestamp更新到redis中
 * 在类加载时，校验本地时间与redis中记录的worker时间戳，只有本地时间大于redis时间，才能继续加载
 * 否则服务启动失败，需要等待一会再重启
 *
 * @author zhangyi
 * @date 2020/10/29
 * @since JDK 1.8
 */
public class LocalIdWorker {

    private static final Logger logger = LoggerFactory.getLogger(LocalIdWorker.class);

    //下面两个每个5位，加起来就是10位的工作机器id
    private long workerId;    //机器id
    private long datacenterId;   //数据中心id

    private long sequence;   //12位的序列号

    //初始时间戳(取一个离当前时间最近的,从此时间戳开始够用69年)
    private long twepoch = 1600000000000L;

    //长度为5位
    private long workerIdBits = 5L;
    private long datacenterIdBits = 5L;
    //最大值
    private long maxWorkerId = ~(-1L << workerIdBits);
    private long maxDatacenterId = ~(-1L << datacenterIdBits);
    //序列号id长度
    private long sequenceBits = 12L;
    //序列号最大值
    private long sequenceMask = ~(-1L << sequenceBits);

    //工作id需要左移的位数，12位
    private long workerIdShift = sequenceBits;
    //数据id需要左移位数 12+5=17位
    private long datacenterIdShift = sequenceBits + workerIdBits;
    //时间戳需要左移位数 12+5+5=22位
    private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    //上次时间戳，初始值为负数
    private long lastTimestamp = -1L;

    public LocalIdWorker() {
        this.datacenterId = getDatacenterId(maxDatacenterId);
        this.workerId = getMaxWorkerId(datacenterId, maxWorkerId);
        this.sequence = 0L;
        if (logger.isInfoEnabled()) {
            logger.info("worker starting. timestamp left shift {}, datacenter id bits {}, worker id bits {}, sequence bits {}, datacenterid {}, workerid {}\r\n",
                    timestampLeftShift, datacenterIdBits, workerIdBits, sequenceBits, datacenterId, workerId);
        }
    }

    public LocalIdWorker(long workerId, long datacenterId, long sequence) {
        // sanity check for workerId
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0\r\n", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0\r\n", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
        this.sequence = sequence;
        if (logger.isInfoEnabled()) {
            logger.info("worker starting. timestamp left shift {}, datacenter id bits {}, worker id bits {}, sequence bits {}, datacenterid  {}, workerid {}\r\n",
                    timestampLeftShift, datacenterIdBits, workerIdBits, sequenceBits, datacenterId, workerId);
        }
    }

    protected static long getMaxWorkerId(long datacenterId, long maxWorkerId) {
        StringBuilder mPid = new StringBuilder();
        mPid.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (!name.isEmpty()) {
            /*
             * GET jvmPid
             */
            mPid.append(name.split("@")[0]);
        }
        /*
         * MAC + PID 的 hashcode 获取16个低位
         */
        long workId = (mPid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
        if (logger.isDebugEnabled()) {
            logger.debug("WorkerId:" + workId);
        }
        return workId;
    }

    protected static long getDatacenterId(long maxDatacenterId) {
        long datacenterId = 0L;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                datacenterId = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                datacenterId = ((0x000000FF & (long) mac[mac.length - 1])
                        | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                datacenterId = datacenterId % (maxDatacenterId + 1);
            }
        } catch (Exception e) {
            logger.error("getDatacenterId error", e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("DatacenterId:" + datacenterId);
        }
        return datacenterId;
    }

    /**
     * 下一个ID生成算法
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        //获取当前时间戳如果小于上次时间戳，则表示时间戳获取出现异常
        if (timestamp < lastTimestamp) {
            logger.error("clock is moving backwards.  Rejecting requests until {}.", lastTimestamp);
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp));
        }

        //获取当前时间戳如果等于上次时间戳（同一毫秒内），则在序列号加一；否则序列号赋值为0，从0开始。
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tillNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        //将上次时间戳值刷新
        lastTimestamp = timestamp;

        /**
         * 返回结果：
         * (timestamp - twepoch) << timestampLeftShift) 表示将时间戳减去初始时间戳，再左移相应位数
         * (datacenterId << datacenterIdShift) 表示将数据id左移相应位数
         * (workerId << workerIdShift) 表示将工作id左移相应位数
         * | 是按位或运算符，例如：x | y，只有当x，y都为0的时候结果才为0，其它情况结果都为1。
         * 因为个部分只有相应位上的值有意义，其它位上都是0，所以将各部分的值进行 | 运算就能得到最终拼接好的id
         */
        return ((timestamp - twepoch) << timestampLeftShift) |
                (datacenterId << datacenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    /**
     * 获取时间戳，并与上次时间戳比较
     */
    private long tillNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 获取系统时间戳
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }
}



