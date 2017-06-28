招行法律知识库与智能交互研发
===================================
项目描述
-----------------------------------
   - **介绍**:本项目用于奇点创智数据科技有限公司操作neo4j数据库以及与前端交互的服务器app。
   - **用途**：对3.1.1版本的neo4j数据库进行增删改查外加索引的操作
   - **特性**：
   	1.neo4j自带的服务端对中文建立的内置索引分词粒度以字为单位，不太符合实际需求，而且cypher语法没有对分词器的选择操作语句。
    2.本代码选用IK分词器做索引分词底层，可扩展词典修改分词粒度，基于spring 框架安全对库直接进行操作。

编译、运行环境说明：
-----------------------------------
    使用JDK1.8.0版本，除了pom中spring，neo4j常用公共第三方开源以外，在公司的nexus上加载了自己修改的IK的分析器IKAnalyzer6x。

总体目录
-----------------------------------

+ main
    + java
        + com
            + qdcz
                * config
                * controller
                * neo4jkernel
                * sdn
                * service
                * Tools
            + APP
    + resources
+ test

-------------------
**config**中加载了spring框架配置以及读取`resources`所需配置加载。<br>
**controller**中控制APP接口实现。<br>
**neo4jkernel**为spring neo4j data调用的各类搜索实现(各种Service)，扩展(evaluator,expander)以及图节点边实例定义(generic)。<br>
**sdn**为图边节点的实例化(entity)以及对应所需要的知识库cypher(epository)。<br>
**service**为结合实际所需的服务逻辑编写。<br>
**Tools**为定义的工具类。<br>
**APP为**  `SpringbootSdnEmbeddedApplication`，启动服务主入口。<br>
**resources**为配置文件<br>
	其中`IKAnalyzer.cfg.xml`为IK分词的扩展配置，加载了2个扩展词典`sougou`、 `stopword`<br>
	`neo4j.properties`设置了app启动的驱动，访问的数据库的位置，对外端口<br>
**test**下为各类单元测试时使用的测试。<br>



