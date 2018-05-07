package com.autotest.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 读取 SQL 脚本并执行
 *
 * @author Unmi
 */
@Component
public class SqlFileExecutor {
    private final static Logger LOGGER = LoggerFactory.getLogger(SqlFileExecutor.class);

    @Autowired
    private Environment env;

    /**
     * 读取 SQL 文件，获取 SQL 语句
     *
     * @param sqlFile SQL 脚本文件
     * @return List<sql> 返回所有 SQL 语句的 List
     * @throws Exception
     */
    private static List<String> loadSql(File sqlFile) throws Exception {
        List<String> sqlList = new ArrayList<String>();

        try {
            InputStream sqlFileIn = new FileInputStream(sqlFile);
            StringBuffer sqlSb = new StringBuffer();
            byte[] buff = new byte[1024];
            int byteRead = 0;
            while ((byteRead = sqlFileIn.read(buff)) != -1) {
                sqlSb.append(new String(buff, 0, byteRead));
            }
            String[] sqlArr = sqlSb.toString().split("\n");
            for (int i = 0; i < sqlArr.length; i++) {
                String sql = sqlArr[i].replaceAll("--.*", "").trim();
                if (!"".equals(sql) && !sql.contains("#")) {
                    sqlList.add(sql);
                }
            }
            return sqlList;
//        } catch (FileNotFoundException fileNotFound) {
//            LOGGER.error("\n❤❤❤❤❤❤❤❤❤❤找不到SQL文件❤❤❤❤❤❤❤❤❤❤: {}", sqlFile);
//            return null;
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    /**
     * 传入连接来执行 SQL 脚本文件，这样可与其外的数据库操作同处一个事物中
     *
     * @param conn    传入数据库连接
     * @param sqlFile SQL 脚本文件
     * @throws Exception
     */
    public static void execute(Connection conn, File sqlFile) throws Exception {
        Statement stmt = null;
        List<String> sqlList = loadSql(sqlFile);
        stmt = conn.createStatement();
        for (String sql : sqlList) {
            if (!sql.trim().startsWith("#")) {
                stmt.execute(sql);
            }

        }
    }

    /**
     * 自建连接，独立事物中执行 SQL 文件，批处理的方式
     *
     * @param sqlFile SQL 脚本文件
     * @throws Exception
     */
    public static void Bashexecute(File sqlFile, Connection conn) throws Exception {
        Statement stmt = null;
        List<String> sqlList = loadSql(sqlFile);
        LOGGER.info("\n❤❤❤❤❤❤❤❤❤❤开始加载SQL文件❤❤❤❤❤❤❤❤❤❤ {}", sqlFile.getPath());
        LOGGER.info("\n❤❤❤❤❤❤❤❤❤❤加载文件完毕❤❤❤❤❤❤❤❤❤❤将要执行的SQL语句数为: {}", sqlList.size());
        try {
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            for (String sql : sqlList) {
                //if(sql.trim().startsWith("#")){
                //    //LOGGER.info(sql);
                //    System.out.println(sql); //注释只输出不执行
                //}
                stmt.addBatch(sql);
            }
            int[] rows = stmt.executeBatch();
            LOGGER.info("\n❤❤❤❤❤❤❤❤❤❤SQL语句执行完毕❤❤❤❤❤❤❤❤❤❤受影响的行统计: {}", Arrays.toString(rows));
            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            if (stmt != null) {
                try {
                    stmt.clearBatch();
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 执行sql文件
     *
     * @param filename 文件全路径
     * @author hxs
     */
    public void executeSqlFile(String filename) throws Exception {
        //读取配置文件中的数据库信息
        String jdbcUrl = env.getProperty("spring.datasource.url");
        String username = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");
        Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
        File sqlFile = new File(filename);
        //调用陈斌的方法
        Bashexecute(sqlFile, conn);
    }

    /**
     *
     * @Description:测试
     * @Date:16:18 2017-10-13
     * @Params:
     */
    public static void main(String[] args) throws Exception {
        File sqlFile = new File("apptest/src/test/resources/AppTest/fp.sql");
        //SqlFileExecutor se=new SqlFileExecutor();
        //Connection con= DriverManager.getConnection("jdbc:mysql://10.10.127.215:3306/qianjin","qianjin20160304","qianjin20160304");
        List<String> sqlList = new SqlFileExecutor().loadSql(new File("src/test/resources/AppTest/testData.sql"));
        System.out.println("size:" + sqlList.size());
        for (String sql : sqlList) {
            System.out.println(sql);
        }
        //se.Bashexecute(sqlFile,con);
    }
}