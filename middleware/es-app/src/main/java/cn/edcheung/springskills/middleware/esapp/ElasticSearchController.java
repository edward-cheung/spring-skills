package cn.edcheung.springskills.middleware.esapp;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.Strings;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Description ElasticSearchController
 *
 * @author Edward Cheung
 * @date 2022/3/19
 * @since JDK 1.8
 */
@Api(tags = "ElasticSearch控制器")
@RestController
@RequestMapping("es")
public class ElasticSearchController {

    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    /**
     * 新增索引
     *
     * @param index 索引
     * @return ResponseVo
     */
    @ApiOperation("新增索引")
    @PostMapping("index")
    public boolean createIndex(String index) throws IOException {
        return elasticSearchUtil.createIndex(index);
    }

    /**
     * 索引是否存在
     *
     * @param index index
     * @return ResponseVo
     */
    @ApiOperation("索引是否存在")
    @GetMapping("index/{index}")
    public boolean existIndex(@PathVariable String index) throws IOException {
        return elasticSearchUtil.isIndexExist(index);
    }

    /**
     * 删除索引
     *
     * @param index index
     * @return ResponseVo
     */
    @ApiOperation("删除索引")
    @DeleteMapping("index/{index}")
    public boolean deleteIndex(@PathVariable String index) throws IOException {
        return elasticSearchUtil.deleteIndex(index);
    }


    /**
     * 新增/更新数据
     *
     * @param entity 数据
     * @param index  索引
     * @param esId   esId
     * @return ResponseVo
     */
    @ApiOperation("新增/更新数据")
    @PostMapping("data")
    public String submitData(Object entity, String index, String esId) throws IOException {
        return elasticSearchUtil.submitData(entity, index, esId);
    }

    /**
     * 通过id删除数据
     *
     * @param index index
     * @param id    id
     * @return ResponseVo
     */
    @ApiOperation("通过id删除数据")
    @DeleteMapping("data/{index}/{id}")
    public String deleteDataById(@PathVariable String index, @PathVariable String id) throws IOException {
        return elasticSearchUtil.deleteDataById(index, id);
    }

    /**
     * 通过id查询数据
     *
     * @param index  index
     * @param id     id
     * @param fields 需要显示的字段，逗号分隔（缺省为全部字段）
     * @return ResponseVo
     */
    @ApiOperation("通过id查询数据")
    @GetMapping("data")
    public Map<String, Object> searchDataById(String index, String id, String fields) throws IOException {
        return elasticSearchUtil.searchDataById(index, id, fields);
    }

    /**
     * 分页查询（这只是一个demo）
     *
     * @param index index
     * @return ResponseVo
     */
    @ApiOperation("分页查询")
    @GetMapping("data/page")
    public List<Map<String, Object>> selectPage(String index) throws IOException {
        //构建查询条件
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //精确查询
        //boolQueryBuilder.must(QueryBuilders.wildcardQuery("name", "张三"));
        // 模糊查询
        boolQueryBuilder.filter(QueryBuilders.wildcardQuery("name", "张"));
        // 范围查询 from:相当于闭区间; gt:相当于开区间(>) gte:相当于闭区间 (>=) lt:开区间(<) lte:闭区间 (<=)
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("age").from(18).to(32));
        SearchSourceBuilder query = new SearchSourceBuilder();
        query.query(boolQueryBuilder);
        //需要查询的字段，缺省则查询全部
        String fields = "";
        //需要高亮显示的字段
        String highlightField = "name";
        if (StringUtils.isNotBlank(fields)) {
            //只查询特定字段。如果需要查询所有字段则不设置该项。
            query.fetchSource(new FetchSourceContext(true, fields.split(","), Strings.EMPTY_ARRAY));
        }
        //分页参数，相当于pageNum
        int from = 0;
        //分页参数，相当于pageSize
        int size = 2;
        //设置分页参数
        query.from(from);
        query.size(size);

        //设置排序字段和排序方式，注意：字段是text类型需要拼接.keyword
        //query.sort("age", SortOrder.DESC);
        query.sort("name" + ".keyword", SortOrder.ASC);

        // 取消1w条限制
        //query.trackTotalHits(true);
        // 设置超时时间为20s
        //query.timeout(new TimeValue(20000));
        return elasticSearchUtil.searchListData(index, query, highlightField);
    }
}

