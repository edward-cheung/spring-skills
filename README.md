### Spring Skills

- db 公共模块
    - persistence 持久层，同时接入Flyway数据库版本控制
    - code-generator-01 MyBatis代码生成器
    - code-generator-02 FreeMarker代码生成器

- web 接入层
    - validation 单参数以及对象参数校验、分组校验、嵌套校验、自定义校验和全局异常处理
    - custom-req-log 自定义请求日志，另外包括异步执行和计划任务

- gateway 网关
    - rest-template http请求转发网关
    - netty-forward Netty转发网关

- util 工具
    - mail 邮件发送
    - id-worker 唯一id生成器
    - http-conn-pool http连接池
    - custom-thread-pool 自定义线程池
    - guava-app Guava应用

- middleware 中间件
    - l2-cache 整合Spring Cache并实现二级缓存
    - redisson-app Redisson应用
    - zk-app Zookeeper应用
    - kafka-app Kafka应用
    - es-app Elasticsearch应用
    - hbase-app HBase应用
    - netty-app Netty应用

- cloud SpringCloud
    - eureka-server 注册中心服务端
    - config-server 配置中心服务端
    - spring-gateway 网关，整合Spring Security认证并鉴权
    - service-consumer 服务消费者
    - service-provider 服务提供者

- cloud-ali SpringCloudAlibaba 包括Nacos、Dubbo、RocketMQ、Seata等应用
    - app-consumer 服务应用消费者
    - app-provider 服务应用提供者