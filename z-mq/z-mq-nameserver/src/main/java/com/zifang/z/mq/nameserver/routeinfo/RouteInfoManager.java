package com.zifang.z.mq.nameserver.routeinfo;

import com.zifang.z.mq.remoting.common.RemotingHelper;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 路由信息管理器
 * 负责管理Broker路由信息、Topic路由数据、Broker存活状态等
 *
 * 核心数据结构：
 * 1. topicQueueTable - Topic队列信息（Topic -> List<QueueData>）
 * 2. brokerAddrTable - Broker地址信息（BrokerName -> BrokerData）
 * 3. clusterAddrTable - 集群信息（ClusterName -> Set<BrokerName>）
 * 4. brokerLiveTable - Broker存活信息（BrokerAddr -> BrokerLiveInfo）
 */
public class RouteInfoManager {

    private static final Logger log = LoggerFactory.getLogger(RouteInfoManager.class);

    // Broker超时时间（毫秒）- 2分钟
    private static final long BROKER_CHANNEL_EXPIRED_TIME = 1000 * 60 * 2;

    // 读写锁，用于并发控制
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    // Topic队列信息：Topic -> List<QueueData>
    private final HashMap<String, List<QueueData>> topicQueueTable = new HashMap<>();

    // Broker地址信息：BrokerName -> BrokerData
    private final HashMap<String, BrokerData> brokerAddrTable = new HashMap<>();

    // 集群信息：ClusterName -> Set<BrokerName>
    private final HashMap<String, Set<String>> clusterAddrTable = new HashMap<>();

    // Broker存活信息：BrokerAddr -> BrokerLiveInfo
    private final ConcurrentHashMap<String, BrokerLiveInfo> brokerLiveTable = new ConcurrentHashMap<>();

    /**
     * 扫描并移除不活跃的Broker
     */
    public void scanNotActiveBroker() {
        Iterator<Map.Entry<String, BrokerLiveInfo>> it = this.brokerLiveTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, BrokerLiveInfo> entry = it.next();
            long lastUpdateTimestamp = entry.getValue().getLastUpdateTimestamp();
            long channelExpiredTime = entry.getValue().getChannelExpiredTime();

            if ((lastUpdateTimestamp + channelExpiredTime) < System.currentTimeMillis()) {
                // 关闭通道
                RemotingHelper.closeChannel(entry.getValue().getChannel());
                it.remove();
                log.warn("The broker channel expired, {} ,{}ms", entry.getKey(), channelExpiredTime);
                // 从路由表中删除Broker
                this.onChannelDestroy(entry.getKey(), entry.getValue().getChannel());
            }
        }
    }

    /**
     * Broker下线处理
     */
    public void onChannelDestroy(String remoteAddr, Channel channel) {
        String brokerNameFound = null;
        String brokerAddrFound = null;

        this.lock.writeLock().lock();
        try {
            // 查找对应的Broker
            Iterator<Map.Entry<String, BrokerLiveInfo>> brokerLiveTableIt = this.brokerLiveTable.entrySet().iterator();
            while (brokerLiveTableIt.hasNext()) {
                Map.Entry<String, BrokerLiveInfo> entry = brokerLiveTableIt.next();
                if (entry.getValue().getChannel() == channel) {
                    brokerAddrFound = entry.getKey();
                    break;
                }
            }

            if (brokerAddrFound != null) {
                // 从BrokerData中移除
                Iterator<Map.Entry<String, BrokerData>> brokerAddrTableIt = this.brokerAddrTable.entrySet().iterator();
                while (brokerAddrTableIt.hasNext()) {
                    BrokerData brokerData = brokerAddrTableIt.next().getValue();
                    brokerNameFound = brokerData.getBrokerName();
                    brokerData.getBrokerAddrs().values().remove(brokerAddrFound);

                    if (brokerData.getBrokerAddrs().isEmpty()) {
                        brokerAddrTableIt.remove();
                        // 从clusterAddrTable中移除
                        // 使用迭代器避免lambda中的非final变量问题
                        Iterator<Map.Entry<String, Set<String>>> clusterIt = this.clusterAddrTable.entrySet().iterator();
                        while (clusterIt.hasNext()) {
                            Set<String> brokerNames = clusterIt.next().getValue();
                            brokerNames.remove(brokerNameFound);
                            if (brokerNames.isEmpty()) {
                                clusterIt.remove();
                            }
                        }
                    }
                    break;
                }

                // 从brokerLiveTable中移除
                this.brokerLiveTable.remove(brokerAddrFound);

                // 从topicQueueTable中移除
                // 从topicQueueTable中移除
                Iterator<Map.Entry<String, List<QueueData>>> topicQueueTableIt = this.topicQueueTable.entrySet().iterator();
                while (topicQueueTableIt.hasNext()) {
                    List<QueueData> queueDataList = topicQueueTableIt.next().getValue();
                    // 使用显式循环避免lambda中的非final变量问题
                    Iterator<QueueData> queueDataIt = queueDataList.iterator();
                    while (queueDataIt.hasNext()) {
                        QueueData queueData = queueDataIt.next();
                        if (queueData.getBrokerName().equals(brokerNameFound)) {
                            queueDataIt.remove();
                        }
                    }
                    if (queueDataList.isEmpty()) {
                        topicQueueTableIt.remove();
                    }
                }
            }
        } finally {
            this.lock.writeLock().unlock();
        }

        if (brokerNameFound != null) {
            log.info("onChannelDestroy: remove broker[{}][{}] from NameServer", brokerNameFound, brokerAddrFound);
        }
    }

    /**
     * 注册Broker
     */
    public RegisterBrokerResult registerBroker(
            final String clusterName,
            final String brokerAddr,
            final String brokerName,
            final long brokerId,
            final String haServerAddr,
            final Channel channel) {

        RegisterBrokerResult result = new RegisterBrokerResult();

        this.lock.writeLock().lock();
        try {
            // 更新集群信息
            Set<String> brokerNames = this.clusterAddrTable.computeIfAbsent(clusterName, k -> new HashSet<>());
            brokerNames.add(brokerName);

            // 更新Broker地址信息
            BrokerData brokerData = this.brokerAddrTable.get(brokerName);
            if (brokerData == null) {
                brokerData = new BrokerData(brokerName, new HashMap<>());
                this.brokerAddrTable.put(brokerName, brokerData);
            }

            // 如果是主Broker，记录HA地址
            if (brokerId == 0) {
                result.setHaServerAddr(haServerAddr);
            }

            // 更新Broker地址
            String oldAddr = brokerData.getBrokerAddrs().put(brokerId, brokerAddr);
            result.setOldAddr(oldAddr);

            // 更新Broker存活信息
            BrokerLiveInfo prevBrokerLiveInfo = this.brokerLiveTable.put(brokerAddr,
                    new BrokerLiveInfo(System.currentTimeMillis(), BROKER_CHANNEL_EXPIRED_TIME, channel));

            if (prevBrokerLiveInfo != null) {
                log.warn("Broker[{}] Channel has been changed! OldChannel:{}, NewChannel:{}",
                        brokerAddr, prevBrokerLiveInfo.getChannel(), channel);
            }

        } finally {
            this.lock.writeLock().unlock();
        }

        log.info("registerBroker: clusterName={}, brokerAddr={}, brokerName={}, brokerId={}",
                clusterName, brokerAddr, brokerName, brokerId);

        return result;
    }

    /**
     * 注销Broker
     */
    public void unregisterBroker(
            final String clusterName,
            final String brokerAddr,
            final String brokerName,
            final long brokerId) {

        this.lock.writeLock().lock();
        try {
            // 从Broker存活表中移除
            BrokerLiveInfo removed = this.brokerLiveTable.remove(brokerAddr);
            if (removed == null) {
                log.warn("unregisterBroker: brokerAddr[{}] not found in brokerLiveTable", brokerAddr);
                return;
            }

            // 从Broker地址表中移除
            BrokerData brokerData = this.brokerAddrTable.get(brokerName);
            if (brokerData != null) {
                String removedAddr = brokerData.getBrokerAddrs().remove(brokerId);
                if (brokerData.getBrokerAddrs().isEmpty()) {
                    this.brokerAddrTable.remove(brokerName);
                    // 从集群表中移除
                    Set<String> brokerNames = this.clusterAddrTable.get(clusterName);
                    if (brokerNames != null) {
                        brokerNames.remove(brokerName);
                        if (brokerNames.isEmpty()) {
                            this.clusterAddrTable.remove(clusterName);
                        }
                    }
                }
                log.info("unregisterBroker: remove broker[{}][{}] from brokerAddrTable", brokerName, removedAddr);
            }

        } finally {
            this.lock.writeLock().unlock();
        }

        log.info("unregisterBroker: clusterName={}, brokerAddr={}, brokerName={}, brokerId={}",
                clusterName, brokerAddr, brokerName, brokerId);
    }

    /**
     * 获取Topic路由信息
     */
    public TopicRouteData pickupTopicRouteData(final String topic) {
        TopicRouteData topicRouteData = new TopicRouteData();

        this.lock.readLock().lock();
        try {
            // 获取队列信息
            List<QueueData> queueDataList = this.topicQueueTable.get(topic);
            if (queueDataList == null || queueDataList.isEmpty()) {
                return null;
            }
            topicRouteData.setQueueDatas(queueDataList);

            // 获取Broker信息
            Set<String> brokerNameSet = new HashSet<>();
            for (QueueData queueData : queueDataList) {
                brokerNameSet.add(queueData.getBrokerName());
            }

            List<BrokerData> brokerDataList = new ArrayList<>();
            for (String brokerName : brokerNameSet) {
                BrokerData brokerData = this.brokerAddrTable.get(brokerName);
                if (brokerData != null) {
                    brokerDataList.add(brokerData);
                }
            }
            topicRouteData.setBrokerDatas(brokerDataList);

        } finally {
            this.lock.readLock().unlock();
        }

        return topicRouteData;
    }

    /**
     * 获取所有集群信息
     */
    public Map<String, Set<String>> getClusterInfo() {
        this.lock.readLock().lock();
        try {
            return new HashMap<>(this.clusterAddrTable);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * 根据集群名称获取Broker名称列表
     */
    public Set<String> getBrokerNamesByCluster(String clusterName) {
        this.lock.readLock().lock();
        try {
            Set<String> brokerNames = this.clusterAddrTable.get(clusterName);
            if (brokerNames != null) {
                return new HashSet<>(brokerNames);
            }
            return null;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    /**
     * 获取系统Topic列表
     */
    public Set<String> getSystemTopicList() {
        this.lock.readLock().lock();
        try {
            Set<String> topicList = new HashSet<>();
            // 添加所有Topic
            topicList.addAll(this.topicQueueTable.keySet());
            // 添加集群名称
            topicList.addAll(this.clusterAddrTable.keySet());
            return topicList;
        } finally {
            this.lock.readLock().unlock();
        }
    }
}

/**
 * Topic路由数据
 */
class TopicRouteData {
    private List<QueueData> queueDatas = new ArrayList<>();
    private List<BrokerData> brokerDatas = new ArrayList<>();

    public List<QueueData> getQueueDatas() {
        return queueDatas;
    }

    public void setQueueDatas(List<QueueData> queueDatas) {
        this.queueDatas = queueDatas;
    }

    public List<BrokerData> getBrokerDatas() {
        return brokerDatas;
    }

    public void setBrokerDatas(List<BrokerData> brokerDatas) {
        this.brokerDatas = brokerDatas;
    }
}

/**
 * 队列数据
 */
class QueueData {
    private String brokerName;
    int readQueueNums = 0;
    int writeQueueNums = 0;
    int perm = 6; // 权限

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public int getReadQueueNums() {
        return readQueueNums;
    }

    public void setReadQueueNums(int readQueueNums) {
        this.readQueueNums = readQueueNums;
    }

    public int getWriteQueueNums() {
        return writeQueueNums;
    }

    public void setWriteQueueNums(int writeQueueNums) {
        this.writeQueueNums = writeQueueNums;
    }

    public int getPerm() {
        return perm;
    }

    public void setPerm(int perm) {
        this.perm = perm;
    }
}

/**
 * Broker数据
 */
class BrokerData {
    private String brokerName;
    private HashMap<Long, String> brokerAddrs = new HashMap<>();

    public BrokerData(String brokerName, HashMap<Long, String> brokerAddrs) {
        this.brokerName = brokerName;
        this.brokerAddrs = brokerAddrs;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public HashMap<Long, String> getBrokerAddrs() {
        return brokerAddrs;
    }

    public void setBrokerAddrs(HashMap<Long, String> brokerAddrs) {
        this.brokerAddrs = brokerAddrs;
    }
}

/**
 * Broker注册结果
 */
class RegisterBrokerResult {
    private String haServerAddr;
    private String oldAddr;

    public String getHaServerAddr() {
        return haServerAddr;
    }

    public void setHaServerAddr(String haServerAddr) {
        this.haServerAddr = haServerAddr;
    }

    public String getOldAddr() {
        return oldAddr;
    }

    public void setOldAddr(String oldAddr) {
        this.oldAddr = oldAddr;
    }
}

/**
 * Broker存活信息
 */
class BrokerLiveInfo {
    private long lastUpdateTimestamp;
    private long channelExpiredTime;
    private Channel channel;

    public BrokerLiveInfo(long lastUpdateTimestamp, long channelExpiredTime, Channel channel) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
        this.channelExpiredTime = channelExpiredTime;
        this.channel = channel;
    }

    public long getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(long lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    public long getChannelExpiredTime() {
        return channelExpiredTime;
    }

    public void setChannelExpiredTime(long channelExpiredTime) {
        this.channelExpiredTime = channelExpiredTime;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
