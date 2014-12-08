mybatis-enhanced-cache
======================

MyBatis Enhanced Cache, Control your Caches precisely! 
当前的MyBaits对于缓存的比较粗糙，一般为一个Mapper配置一个Cache缓存，或者多个Mapper共用一个缓存。对于不同的StatementId，它们之间的缓存可能会有某种依赖关系，比如

AMapper.xml 中定义了对ATable的CRUD操作，BMapper定义了对BTable的CRUD操作。假设MyBatis的二级缓存开启，并且AMapper中使用了二级缓存。
除此之外，AMapper中还定义了一个跟BTable有关的查询语句，类似如下所述：
<select id="selectATableWidhJoin" resultMap="BaseResultMap" useCache="true">
      select * from ATable left join BTable on ....
</select>
如果某个时候，BMapper中执行了对BTable的update操作(update 、delete、insert),BTable 的数据已经更新，
但是由于对于AMapper而言，使用了二级缓存，"selectATableWidhJoin"语句的查询会从缓存中直接取值，其结果极有可能跟真实数据不一致的问题。

上述的问题即：缓存数据不同步或者




Usage：
======================
使用此插件非常简单：
1. 在mybatisConfig.xml 文件中定义plugin节点如下：
  <plugins>
       <plugin interceptor="org.luanlouis.mybatis.plugin.cache.EnhancedCachingExecutor">
          <property name="dependency" value="dependencys.xml"/>
          <property name="cacheEnabled" value="true"/>
       </plugin>
  </plugins>
2. dependencys.xml配置文件，配置StatemntId依赖关系
