package com.qdcz;

import com.qdcz.sdn.entity.User;
import com.qdcz.service.bottom.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;



@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringbootSdnEmbeddedApplication.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    /**
     * 因为是通过http连接到Neo4j数据库的，所以要预先启动Neo4j
     */
    @Test
    public void testInitData(){
        userService.initData();
    }

    @Test
    public void testGetUserByName(){
        User user = userService.getUserByName("John Johnson");
        System.out.println(user);


    }
    @Test
    public void testGetUserByNames(){
        List<User> users = userService.getUserByNames("John Johnson");
        for(User user:users)
        System.out.println(user);
    }
    @Test
    public void testGetUserById(){
        Node node = userService.getUserById(89l);
        System.out.println(node);
    }
}
