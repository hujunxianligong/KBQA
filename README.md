# 知识图谱管理与智能问答引擎
- ##  **介绍**
基于Neo4j图数据库构建知识图谱，提供知识图谱的增删改查；基于知识图谱和策略构建智能问答引擎
- ##**特性**
1. 采用Neo4j构建知识图谱
2. 基于Spring-data-neo4j框架实现OGM
3. 基于RESTful API方式提供外部调用接口
4. 自定义分词插件

## 版本更新

###2017-08-02

1. 支持银行、金融、票据、证券、法律等行业的名词概念问答

###2017-07-20

1. 初始版本
2. 支持银团贷业务指引条款相关内容问答
3. 针对无法回答的问题，引入图灵机器人

------------
# 编译、运行环境说明

使用JDK1.8版本，除了pom中spring，neo4j常用公共第三方开源以外，在公司的nexus上加载了自己修改的IK的分析器IKAnalyzer6x。

# 目录结构
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

- **config** spring框架配置、mongo配置以及读取`resources`所需配置。
- **controller** API业务控制层。
- **neo4jkernel** spring-data-neo4j调用的各类搜索实现(各种Service)，扩展(evaluator,expander)以及图节点边实例定义(generic)。
- **sdn** 图边节点的实例化(entity)以及对应所需要的知识库cypher(repository)。
- **service** 结合实际所需的服务逻辑编写,分为低中高三层，上层依赖下层。
- **Tools** 定义的工具类。
- **APP**  `SpringbootSdnEmbeddedApplication`，服务主入口。
- **resources** 配置文件目录，其中：
 + **IKAnalyzer.cfg.xml** IK分词的扩展配置，加载了2个扩展词典sougou、 stopword、银团指引词典
  + **neo4j.properties** 配置访问neo4j数据库的驱动类、访问的数据库的位置和对外端口
  + **mongo.properties** 配置mongo-driver所需的信息
  + **hanlp.properties** 配置hanlp分词器加载词典位置
- **test**下为各类单元测试时使用的测试。



