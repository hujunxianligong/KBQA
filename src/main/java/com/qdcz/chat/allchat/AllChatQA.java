package com.qdcz.chat.allchat;

import com.qdcz.chat.entity.RequestParameter;
import com.qdcz.chat.interfaces.ChatQA;
import org.neo4j.driver.v1.types.Path;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by star on 17-8-11.
 */
@Service
public class AllChatQA extends ChatQA {
    @Override
    public Set<Path> MatchPath(List<Map<String, Object>> maps, RequestParameter requestParameter) {
        return null;
    }
}
