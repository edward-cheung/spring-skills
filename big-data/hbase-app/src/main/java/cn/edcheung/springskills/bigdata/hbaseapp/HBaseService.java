package cn.edcheung.springskills.bigdata.hbaseapp;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.NamespaceExistException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Description HbaseService
 *
 * @author Edward Cheung
 * @date 2022/9/23
 * @since JDK 1.8
 */
@Service
public class HBaseService implements InitializingBean, DisposableBean {

    private Admin admin = null;

    private Connection connection = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 设置临时的hadoop环境变量，之后程序会去这个目录下的\bin目录下找winutils.exe工具，windows连接hadoop时会用到
        //System.setProperty("hadoop.home.dir", "D:\\Program Files\\Hadoop");
        // 使用 HBaseConfiguration 的单例方法实例化，执行此步时，会去resources目录下找相应的配置文件，例如hbase-site.xml
        Configuration conf = HBaseConfiguration.create();
        //conf.set("hbase.zookeeper.quorum", "127.0.0.1");
        //conf.set("hbase.zookeeper.property.clientPort", "2181");
        connection = ConnectionFactory.createConnection(conf);
        admin = connection.getAdmin();
    }

    @Override
    public void destroy() throws Exception {
        IOUtils.closeQuietly(admin);
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException ignored) {
            }
        }
    }

    public boolean isTableExist(String tableName) throws Exception {
        return admin.tableExists(TableName.valueOf(tableName));
    }

    /**
     * 创建表
     *
     * @param tableName    表名
     * @param columnFamily 列簇名
     * @throws Exception
     */
    public void createTable(String tableName, String... columnFamily) throws Exception {
        if (columnFamily.length <= 0) {
            throw new Exception("请传入列簇信息");
        }
        // 判断表是否存在
        if (isTableExist(tableName)) {
            throw new Exception("表" + tableName + "已存在");
        }
        // 创建表属性对象,表名需要转字节
        HTableDescriptor descriptor = new HTableDescriptor(TableName.valueOf(tableName));
        // 创建多个列族
        for (String cf : columnFamily) {
            descriptor.addFamily(new HColumnDescriptor(cf));
        }
        // 根据对表的配置，创建表
        admin.createTable(descriptor);
    }

    /**
     * 删除表
     *
     * @param tableName
     */
    public void dropTable(String tableName) throws Exception {
        if (!isTableExist(tableName)) {
            throw new Exception(tableName + ": 不存在 !");
        }
        // 1、下线表
        admin.disableTable(TableName.valueOf(tableName));
        // 2、删除表
        admin.deleteTable(TableName.valueOf(tableName));
    }

    /**
     * 创建命名空间
     *
     * @param nameSpace
     */
    public void createNameSpace(String nameSpace) throws Exception {
        if (StringUtils.isEmpty(nameSpace)) {
            throw new Exception(nameSpace + ": 不存在 !");
        }
        NamespaceDescriptor namespaceDescriptor = NamespaceDescriptor.create(nameSpace).build();
        try {
            admin.createNamespace(namespaceDescriptor);
        } catch (NamespaceExistException e) {
            throw new Exception("命名空间已存在");
        }
    }

    /**
     * @param tableName 表名
     * @param rowKey    rowKey
     * @param cf        columnFamily
     * @param cn        columnName
     * @param value     columnValue
     */
    public void putData(String tableName, String rowKey, String cf, String cn, String value) throws Exception {
        // 1、获取表对象
        Table table = connection.getTable(TableName.valueOf(tableName));
        // 2、拼接 put对象
        Put put = new Put(Bytes.toBytes(rowKey));
        // 3、添加 字段信息 column
        put.addColumn(Bytes.toBytes(cf), Bytes.toBytes(cn), Bytes.toBytes(value));
        // 4、执行数据插入
        table.put(put);
    }

    /**
     * 获取数据
     *
     * @param tableName
     * @param rowKey
     * @param cf
     * @param cn
     */
    public Map<String, String> getData(String tableName, String rowKey, String cf, String cn) throws Exception {
        Map<String, String> resultMap = new HashMap<>();
        Table table = connection.getTable(TableName.valueOf(tableName));
        Get get = new Get(Bytes.toBytes(rowKey));
        // 添加 cf ，也可以不添加
        //get.addFamily(Bytes.toBytes(cf));
        // 同时传入 cf 和 cn
        if (StringUtils.isNotEmpty(cf) && StringUtils.isNotEmpty(cn)) {
            get.addColumn(Bytes.toBytes(cf), Bytes.toBytes(cn));
        }
        Result result = table.get(get);
        // 解析结果
        Cell[] cells = result.rawCells();
        for (Cell cell : cells) {
            String columnFamilyName = Bytes.toString(CellUtil.cloneFamily(cell));
            String colName = Bytes.toString(CellUtil.cloneQualifier(cell));
            String colValue = Bytes.toString(CellUtil.cloneValue(cell));
            resultMap.put(colName, colValue);
        }
        return resultMap;
    }

    /**
     * 通过扫描的方式获取数据
     *
     * @param tableName
     */
    public Map<String, String> getDataFromScan(String tableName) throws Exception {
        Map<String, String> resultMap = new HashMap<>();
        Table table = connection.getTable(TableName.valueOf(tableName));
        // 拿到扫描器对象
        //Scan scan = new Scan();
        // 可以根据 rowkey 继续获取【非必须】
        Scan scan = new Scan();
        // 指定需要的family或column，如果没有调用任何addFamily或Column，会返回所有的columns
        //scan.addFamily();
        //scan.addColumn();
        // 指定开始的行，如果不调用，则从表头开始
        //scan.setStartRow();
        // 指定结束的行(不含此行)
        //scan.setStopRow();
        // 指定最多返回的Cell数目，用于防止一行中有过多的数据，导致OutofMemory错误
        //scan.setBatch();
        // 指定Filter来过滤掉不需要的信息
        //scan.setFilter();

        // HBase 内置以下7种比较运算符，在比较过滤器中需要用到比较运算符
        //public enum CompareOperator {
        //	LESS,
        //	LESS_OR_EQUAL,
        //	EQUAL,
        //	NOT_EQUAL,
        //	GREATER_OR_EQUAL,
        //	GREATER,
        //	NO_OP;
        //	private CompareOperator() {}
        //}

        // 通过比较器可以实现多样化目标匹配效果，比较器有以下子类可以使用
        // BinaryComparator // 匹配完整字节数组
        // BinaryPrefixComparator // 匹配字节数组前缀
        // BitComparator // 按位执行与、或、异或比较
        // NullComparator // 判断当前值是不是 NULL
        // RegexStringComparator // 正则表达式匹配字符串
        // SubstringComparator // 子串匹配，相当于 contains()，大小写不敏感

        // FilterList.Operator.MUST_PASS_ALL --> and
        // FilterList.Operator.MUST_PASS_ONE --> or
        // Scan scan = new Scan();
        // Filter filter1 = new ...;
        // Filter filter2 = new ...;
        // Filter filter3 = new ...;
        // FilterList list = new FilterList(FilterList.Operator.MUST_PASS_ONE);
        // scan.setFilter(list);

        ResultScanner resultScanner = table.getScanner(scan);
        //结果解析
        for (Result result : resultScanner) {
            Cell[] cells = result.rawCells();
            for (Cell cell : cells) {
                String columnFamilyName = Bytes.toString(CellUtil.cloneFamily(cell));
                String colName = Bytes.toString(CellUtil.cloneQualifier(cell));
                String colValue = Bytes.toString(CellUtil.cloneValue(cell));
                resultMap.put(colName, colValue);
            }
        }
        return resultMap;
    }

    /**
     * 删除数据
     *
     * @param tableName
     * @param rowKey
     * @param cf
     * @param cn
     * @throws Exception
     */
    public void deleteData(String tableName, String rowKey, String cf, String cn) throws Exception {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Delete delete = new Delete(Bytes.toBytes(rowKey));
        // 还可以传入列簇，以及字段名【非必须】
        if (StringUtils.isNotEmpty(cf) && StringUtils.isNotEmpty(cn)) {
            delete.addColumn(Bytes.toBytes(cf), Bytes.toBytes(cn));
        }
        table.delete(delete);
    }
}
