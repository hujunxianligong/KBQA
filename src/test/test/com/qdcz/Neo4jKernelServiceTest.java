package com.qdcz;

import com.qdcz.neo4jkernel.ExpanderService;
import com.qdcz.neo4jkernel.LoopDataService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringbootSdnEmbeddedApplication.class)
public class Neo4jKernelServiceTest {

    @Autowired
    private LoopDataService loopDataService;

    @Autowired
    private ExpanderService expanderService;

    @Test
    public void testFindJohnSeenMovie(){
        loopDataService.findJohnSeenMovie(143l);
    }

    @Test
    public void testRecommendMovieToJohn(){
        loopDataService.recommendMovieToJohn(143l);
    }

    @Test
    public void testLoopDataByApi(){
        loopDataService.loopDataByLoopApi(143l,2);
    }

    @Test
    public void testOrderedExpander(){
        expanderService.orderedExpander(44l);
    }
}
