package personal.hanchuang.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import personal.hanchuang.dao.UserDao;
import personal.hanchuang.entity.User;

import javax.annotation.Resource;
import java.sql.*;
import java.util.*;

@Controller
public class OracleProcedurController {


    static{
        // 加载Oracle驱动
        try {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        // 获得Oracle数据库连接
        return DriverManager.getConnection("jdbc:oracle:thin:@192.168.16.13:1521:orcl", "NZ_DATAI_SOURCE", "NZ_DATAI_SOURCE");
    }


    /**
     * 创建有参数无返回值存储过程
     * @return
     * @throws SQLException
     */
    @RequestMapping("/addProcedureWithParmes")
    public @ResponseBody int addProcedureWithParmes() throws SQLException {


        // 加载Oracle驱动
        Connection conn = getConnection();

        // sql语句
        String procedureSQL = " create OR REPLACE procedure USER_EXIST(in loginName varchar(50),out amount int) " +
                " begin" +
                " declare middleVariable int;" +    //声明中间变量,用到的话就声明
                " select count(*) into amount from DWUSER where username = loginName;" +
                " end;" ;

        PreparedStatement pstmt = null;
        int i= 0;

        try {
            pstmt = conn.prepareStatement(procedureSQL);
            i = pstmt.executeUpdate();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally{
            try {
                pstmt.close();
                pstmt = null;
                conn.close();
                conn = null;
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return i ;
    }

    /**
     * 创建无参数无返回值存储过程
     * @return
     * @throws SQLException
     */
    @RequestMapping("/addProcedure")
    public @ResponseBody int addProcedure() throws SQLException {


        // 加载Oracle驱动
        Connection conn = getConnection();

        // sql语句
        String procedureSQL = " CREATE \n" +
                "\tOR REPLACE PROCEDURE test222 AS BEGIN\n" +
                "\tINSERT INTO a VALUES ( SYSDATE );\n" +
                "END; ";

        PreparedStatement pstmt = null;
        int i= 0;

        try {
            pstmt = conn.prepareStatement(procedureSQL);
            i = pstmt.executeUpdate();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally{
            try {
                pstmt.close();
                pstmt = null;
                conn.close();
                conn = null;
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return i ;
    }


    /**
     * 获取当前用户存储过程列表信息
     * @return
     * @throws SQLException
     */
    @RequestMapping("/procedureList")
    public @ResponseBody HashMap<String, String> getProcedureList() throws SQLException {


        // 加载Oracle驱动
        Connection conn = getConnection();

        // sql语句
        String sql = " SELECT * FROM user_source ORDER BY NAME,line ";

        PreparedStatement pst = null;
        // 定义一个list用于接受数据库查询到的内容
        List<Map<String,String>> list =Collections.EMPTY_LIST ;
        try {
            pst = (PreparedStatement) conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            list = resultSetToList(rs);
        } catch (Exception e) {
        }

        HashMap<String, String> pro = new HashMap<>();

        for (Map<String, String> map : list) {
            String name = map.get("NAME");
            String text = map.get("TEXT");
            //String type = map.get("TYPE");
            //String line = map.get("LINE");

            if (pro.containsKey(name)){
                pro.put(name,pro.get(name)+text);
            }else{
                pro.put(name,text);
            }

        }

        conn.close();
        return pro;
    }


    /**
     * 执行无参数有返回值的存储过程
     * @return
     * @throws SQLException
     */
    @RequestMapping("/execProcedureNoParameWithResult")
    public @ResponseBody String execProcedureNoParameWithResult() throws SQLException {

        // 加载Oracle驱动
        Connection conn = getConnection();


        Statement stmt = null;
        ResultSet rs = null;
        String testPrint = "";
        try {
            CallableStatement proc = null;
            proc = conn.prepareCall("{ call COUNT_NUM(?) }");
            proc.registerOutParameter(1, Types.INTEGER);
            boolean execute = proc.execute();
            testPrint = proc.getString(1);
            System.out.println("存储过程返回信息是："+execute);
            System.out.println("存储过程返回的值是："+testPrint);

            return testPrint;
        }
        catch (SQLException ex2) {
            ex2.printStackTrace();
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
        }
        finally{
            try {
                if(rs != null){
                    rs.close();
                    if(stmt!=null){
                        stmt.close();
                    }
                    if(conn!=null){
                        conn.close();
                    }
                }
            }
            catch (SQLException ex1) {
            }
        }

        return testPrint;
    }


    /**
     * 将ResultSet中的信息放入List<Map<String,String>>中
     * @param rs
     * @return
     * @throws SQLException
     */
    private static List<Map<String,String>> resultSetToList(ResultSet rs) throws SQLException {
        if (rs == null)
            return Collections.EMPTY_LIST;
        ResultSetMetaData md = rs.getMetaData(); //得到结果集(rs)的结构信息，比如字段数、字段名等
        int columnCount = md.getColumnCount(); //返回此 ResultSet 对象中的列数
        List list = new ArrayList();
        Map rowData = new HashMap();
        while (rs.next()) {
            rowData = new HashMap(columnCount);
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(md.getColumnName(i), rs.getObject(i));
            }
            list.add(rowData);
            System.out.println("list:" + list.toString());
        }
        return list;
    }


    /**
     * 无返回值,多参数,自定义存储过程名的存储过程调用方法
     * @param proName
     * @param parmes
     * @return
     * @throws SQLException
     */
    @RequestMapping("/execPro/{proName}/{parmes}")
    public @ResponseBody String execPro(@PathVariable("proName") String proName, @PathVariable("parmes") String[] parmes) throws SQLException {


        // 加载Oracle驱动
        Connection conn = getConnection();

        HashMap<Integer, String> map = new HashMap<>();
        String parmesStr = "";
        if(parmes !=null && parmes.length != 0){
            for (int i = 0; i < parmes.length; i++) {
                if (i==0){
                    parmesStr = " ? ";
                }else{
                    parmesStr += " , ? ";
                }
                map.put( i+1 , parmes[i] );
            }
        }

        // 创建存储过程的对象
        String s = "{call " + proName + "(" + parmesStr + ")}";
        CallableStatement c = conn.prepareCall(" " + s + " ");

        if (map.size()>0){
            for (Map.Entry<Integer, String> entry : map.entrySet()) {
                Integer key = entry.getKey();
                String value = entry.getValue();
                c.setString(key, value);
            }
        }

        // 执行Oracle存储过程
        boolean execute = c.execute();
        System.out.println(execute);
        conn.close();

        return "";
    }


    /**
     * 无返回值,无参数,自定义存储过程名的存储过程调用方法
     * @param proName
     * @return
     * @throws SQLException
     */
    @RequestMapping("/execPro/{proName}")
    public @ResponseBody String execPro(@PathVariable("proName") String proName) throws SQLException {
        Connection conn = getConnection();


        // 创建存储过程的对象
        String s = "{call " + proName + "()}";
        CallableStatement c = conn.prepareCall(" " + s + " ");

        // 执行Oracle存储过程
        boolean execute = c.execute();
        System.out.println(execute);
        conn.close();

        return "";
    }


}

/*

--以下为navicat中执行的存储过程相关语句留作笔记
-- 查询当前用户所有的存储过程信息
SELECT * FROM user_source   ;


-- 部分情况下会有一些权限问题,虽然是dba权限,但要显示重新授权一下
grant  CREATE ANY TABLE   to NZ_DATAI_SOURCE;


-- 会用到的表

CREATE TABLE "ZIDIAN"
 (	"CHARGE_KIND_CODE" VARCHAR2(50) NOT NULL ENABLE,
"CHARGE_KIND_NAME" VARCHAR2(50),
"COMP_CODE" VARCHAR2(50)
 )

 CREATE TABLE "ZIDIAN_1"
 (	"CHARGE_KIND_CODE" VARCHAR2(50) NOT NULL ENABLE,
"CHARGE_KIND_NAME" VARCHAR2(50),
"COMP_CODE" VARCHAR2(50)
 )


CREATE TABLE "A"
 (	"A" DATE
 )

CREATE TABLE "DWUSER"
 (	"USERNAME" VARCHAR2(50)
 )
INSERT INTO "NZ_DATAI_SOURCE"."DWUSER"("USERNAME") VALUES ('韩闯4');
INSERT INTO "NZ_DATAI_SOURCE"."DWUSER"("USERNAME") VALUES ('韩闯5');
INSERT INTO "NZ_DATAI_SOURCE"."DWUSER"("USERNAME") VALUES ('韩闯6');
INSERT INTO "NZ_DATAI_SOURCE"."DWUSER"("USERNAME") VALUES ('韩闯7');
INSERT INTO "NZ_DATAI_SOURCE"."DWUSER"("USERNAME") VALUES ('韩闯8');
INSERT INTO "NZ_DATAI_SOURCE"."DWUSER"("USERNAME") VALUES ('韩闯9');
INSERT INTO "NZ_DATAI_SOURCE"."DWUSER"("USERNAME") VALUES ('韩闯11');
INSERT INTO "NZ_DATAI_SOURCE"."DWUSER"("USERNAME") VALUES ('韩闯12');
INSERT INTO "NZ_DATAI_SOURCE"."DWUSER"("USERNAME") VALUES ('韩闯13');
INSERT INTO "NZ_DATAI_SOURCE"."DWUSER"("USERNAME") VALUES ('韩闯14');
INSERT INTO "NZ_DATAI_SOURCE"."DWUSER"("USERNAME") VALUES ('韩闯15');
INSERT INTO "NZ_DATAI_SOURCE"."DWUSER"("USERNAME") VALUES ('韩闯16');
INSERT INTO "NZ_DATAI_SOURCE"."DWUSER"("USERNAME") VALUES ('韩闯17');
INSERT INTO "NZ_DATAI_SOURCE"."DWUSER"("USERNAME") VALUES ('韩闯18');
INSERT INTO "NZ_DATAI_SOURCE"."DWUSER"("USERNAME") VALUES ('韩闯19');
INSERT INTO "NZ_DATAI_SOURCE"."DWUSER"("USERNAME") VALUES ('韩闯20');
INSERT INTO "NZ_DATAI_SOURCE"."DWUSER"("USERNAME") VALUES ('韩闯21');
INSERT INTO "NZ_DATAI_SOURCE"."DWUSER"("USERNAME") VALUES ('韩闯22');
INSERT INTO "NZ_DATAI_SOURCE"."DWUSER"("USERNAME") VALUES ('韩闯23');


-- 无参数无返回值的存储过程 --删除
create OR REPLACE procedure USER_EXIST
as
begin
delete from DWUSER where USERNAME = '韩闯';
end;

call USER_EXIST();


--无参数无返回值的存储过程--插入
CREATE OR REPLACE PROCEDURE test
AS
BEGIN
INSERT INTO a VALUES ( SYSDATE );
END;

call TEST ();

--无参数无返回值的存储过程--插入--两表操作,查询+插入
CREATE OR REPLACE PROCEDURE pro_1
AS
BEGIN
INSERT INTO ZIDIAN_1 ( CHARGE_KIND_CODE, CHARGE_KIND_NAME, COMP_CODE ) SELECT
CHARGE_KIND_CODE,
CHARGE_KIND_NAME,
COMP_CODE
FROM
ZIDIAN;
END;

call pro_1 ();



--有参数无返回值的存储过程--删除--参数类型借用字段类型
create OR REPLACE procedure DEL_EXIST( loginName IN DWUSER.USERNAME%type)
as
begin
delete from DWUSER where USERNAME = loginName;
end;

call DEL_EXIST('韩闯');

--有参数无返回值的存储过程--删除--指定正确参数类型
create OR REPLACE procedure DEL_EXIST2( loginName IN VARCHAR)
as
begin
delete from DWUSER where USERNAME = loginName;
end;

call DEL_EXIST2('韩闯2');
--有参数无返回值的存储过程--删除--指定类似参数类型
create OR REPLACE procedure DEL_EXIST3( loginName IN NVARCHAR2)
as
begin
delete from DWUSER where USERNAME = loginName;
end;

call DEL_EXIST3('韩闯3');

--有参数无返回值的存储过程--删除--指定错误参数类型
create OR REPLACE procedure DEL_EXIST4( loginName IN NUMBER)
as
begin
delete from DWUSER where USERNAME = loginName;
end;
call DEL_EXIST4('1');报错非法数字
call DEL_EXIST4(1);报错非法数字

--有参数无返回值的存储过程--查询--输出数字
create OR REPLACE procedure COUNT_NUM( AMOUNT out NUMBER )
as
begin
SELECT COUNT(1) INTO AMOUNT  from DWUSER ;
dbms_output.put_line('哈哈共有行数:'||AMOUNT);
end;
--此种存储过程不能直接用call来调用，这种情况的调用将在下面oracle函数调用中说明
--调用方法
DECLARE
user_num NUMBER;
BEGIN
	user_num := 0;
	COUNT_NUM ( user_num );
	DBMS_OUTPUT.PUT_LINE ( '获取的人员数量'||user_num );
END;

--多参数有返回值的存储过程--查询--输出数字
create OR REPLACE procedure COUNT_NUM_WITH_BASE(base_num in int  ,AMOUNT out NUMBER )
as
begin
SELECT COUNT(1) INTO AMOUNT  from DWUSER ;
dbms_output.put_line('哈哈共有行数:'||AMOUNT);
dbms_output.put_line('哈哈共有行数加上base:'||(base_num+AMOUNT));
end;
--此种存储过程不能直接用call来调用，这种情况的调用将在下面oracle函数调用中说明
--调用方法

--多参数顺序传参
DECLARE
user_number NUMBER;
base_number NUMBER;
BEGIN
	user_number := 0;
	base_number := 1000;
	COUNT_NUM_WITH_BASE ( base_number ,user_number );
	DBMS_OUTPUT.PUT_LINE ( '获取的人员数量'||user_number );
	DBMS_OUTPUT.PUT_LINE ( '获取的人员数量加上base'||(user_number+ base_number));
END;

--多参数非顺序传参
DECLARE
user_number NUMBER;
base_number NUMBER;
BEGIN
	user_number := 0;
	base_number := 1000;
	COUNT_NUM_WITH_BASE (AMOUNT=>user_number,base_num => base_number  );
	DBMS_OUTPUT.PUT_LINE ( '获取的人员数量'||user_number );
	DBMS_OUTPUT.PUT_LINE ( '获取的人员数量加上base'||(user_number+ base_number));
END;







--不调用存储过程的执行
declare
      sal int;
      job varchar(40);
begin
      sal:=1100;
      job:='编程';
      DBMS_OUTPUT.PUT_LINE(sal||JOB);
END;







 */