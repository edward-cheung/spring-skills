package cn.edcheung.springskills.middleware.esapp;

import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class EsAppApplicationTests {

    @Autowired
    @Qualifier("esRestClient")
    private RestHighLevelClient client;

    @Test
    void contextLoads() {
        System.out.println(client);
    }

    /**
     * 测试创建索引
     */
    @Test
    public void testCreateIndex() throws IOException {
        // 设置索引  类型
        String index = "users";
        String type = "man";
        // 设置索引的信息:分片 备份
        Settings.Builder settings = Settings.builder()
                .put("number_of_shards", 3)
                .put("number_of_replicas", 1);
        // 构建mapping
        XContentBuilder mapping = JsonXContent.contentBuilder()
                .startObject().startObject("properties")
                //.startObject("_all").field("enabled", false).endObject()
                //.startObject("_source").field("enabled", false).endObject()
                .startObject("create_time")
                .field("format", "yyyy/MM/dd||yyyy/MM/dd HH:mm:ss")
                .field("index", "not_analyzed")
                .endObject()
                //.startObject("userId").field("type", "long").endObject()
                //.startObject("phone").field("type", "keyword").endObject()
                //.startObject("money").field("type", "double").endObject()
                .startObject("userName").field("type", "text").endObject()
                .startObject("age").field("type", "integer").endObject()
                .endObject().endObject();
        // 创建索引请求
        CreateIndexRequest request = new CreateIndexRequest(index)
                .settings(settings)
                .mapping(type, mapping.contentType());
        // 执行客户端请求
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        boolean acknowledged = response.isAcknowledged(); // 指示是否所有节点都已确认请求
        boolean shardsAcknowledged = response.isShardsAcknowledged(); // 指示是否在超时之前为索引中的每个分片启动了必需的分片副本数
        // 获取响应数据
        System.out.println(response);
    }

    /**
     * 测试存储
     */
    @Test
    public void testIndexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
        // indexRequest.source("userName", "zhangsan", "age", "18");
        User user = new User();
        user.setAge(18);
        user.setUserName("lisi");
        user.setGender("男");
        String jsonString = JSONObject.toJSONString(user);
        // 必须指定XContentType
        indexRequest.source(jsonString, XContentType.JSON);
        // 指定路由
        // 所有的文档API请求(get,index,delete,bulk,update)都接受routing路由参数,通过这个参数可以自定义文档到数据分片的映射规则
        // indexRequest.routing("id");
        // 设置数据更改后的刷新策略
        // NONE：这是默认的一种方式，调用request修改以后，并不进行强制刷新，刷新的时间间隔为refresh_interval设置的参数。
        // IMMEDIATE：强制刷新相关的主分片和副分片（而不是整个索引），使更新的分片状态变为可搜索。这种方式并不适用于索引和查询高吞吐量的场景，但是作为流量小时提供一致性的视图的确是很使用的。
        // WAIT_UNTIL：在返回请求结果之前，会等待刷新请求所做的更改。
        indexRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.NONE);
        // 执行
        IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
        // 获取响应数据
        System.out.println(response);
    }

    /**
     * 测试检索
     */
    @Test
    public void testSearch1() throws IOException {
        // 创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        // 指定索引
        searchRequest.indices("bank");
        // 指定DSL
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        // 按照年龄的值分布进行聚合
        searchSourceBuilder.aggregation(AggregationBuilders.terms("aggAge").field("age").size(10));
        // 计算平均薪资
        searchSourceBuilder.aggregation(AggregationBuilders.avg("balanceAvg").field("balance"));
        System.out.println("检索条件" + searchSourceBuilder.toString());
        searchRequest.source(searchSourceBuilder);

        // 同步执行（也可以使用异步）
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        // 分析结果
        System.out.println(searchResponse);
    }

    /**
     * 测试检索
     */
    @Test
    public void testSearch2() throws IOException {
        // 创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        // 指定索引
        searchRequest.indices("bank");
        // 指定DSL
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        // 按照年龄的值分布进行聚合
        searchSourceBuilder.aggregation(AggregationBuilders.terms("aggAge").field("age").size(10));
        // 计算平均薪资
        searchSourceBuilder.aggregation(AggregationBuilders.avg("balanceAvg").field("balance"));

        System.out.println("检索条件" + searchSourceBuilder.toString());
        searchRequest.source(searchSourceBuilder);

        // 同步执行（也可以使用异步）
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        // 分析结果
        System.out.println(searchResponse);
        // 获取所有命中的结果
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hitsHits = hits.getHits();
        for (SearchHit hit : hitsHits) {
            String string = hit.getSourceAsString();
            Account account = JSONObject.parseObject(string, Account.class);
            System.out.println("account:" + account);
        }
        // 获取分析信息
        Aggregations aggregations = searchResponse.getAggregations();
        Terms aggAge = aggregations.get("aggAge");
        aggAge.getBuckets().forEach((bucket) -> {
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄为" + keyAsString + "有" + bucket.getDocCount() + "人");
        });
        Avg avg = aggregations.get("balanceAvg");
        System.out.println("平均薪资为" + avg.getValue());
    }

    static class User {
        private String userName;
        private String gender;
        private Integer age;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }

    static class Account {
        private int account_number;

        private int balance;

        private String firstname;

        private String lastname;

        private int age;

        private String gender;

        private String address;

        private String employer;

        private String email;

        private String city;

        private String state;

        public int getAccount_number() {
            return account_number;
        }

        public void setAccount_number(int account_number) {
            this.account_number = account_number;
        }

        public int getBalance() {
            return balance;
        }

        public void setBalance(int balance) {
            this.balance = balance;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getEmployer() {
            return employer;
        }

        public void setEmployer(String employer) {
            this.employer = employer;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

}
