package com.qdcz.sdn.entity;

import lombok.Data;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

/**
 * Created by zhangjing on 16-7-15.
 */

@Data
@NodeEntity(label="USERS")
public class User {

    public User(){

    }

    public User(String name){
        this.name = name;
    }

    @GraphId
    private Long nodeId;

    @Property(name="name")
    private String name;

    //关系直接定义在节点中
    @Relationship(type = "IS_FRIEND_OF", direction=Relationship.OUTGOING)
    private List<User> friends;

    //使用外部定义的关系
    @Relationship(type = "HAS_SEEN")
    private List<Seen> hasSeenMovies;


    public  void setFriends(List<User> users){
        for(User user:users){
            if(this.friends==null){
                this.friends=users;
                return;
            }else
            if(!this.friends.contains(user)){
                this.friends.add(user);
            }
        }
    }
}
