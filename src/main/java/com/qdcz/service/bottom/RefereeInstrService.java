package com.qdcz.service.bottom;

import com.qdcz.sdn.entity.instruments.CaseReason;
import com.qdcz.sdn.entity.instruments.*;
import com.qdcz.sdn.repository.instruments.CaseReasRepos;
import com.qdcz.sdn.repository.instruments.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by hadoop on 17-6-29.
 */
@Service
public class RefereeInstrService {
    @Autowired
    private CaseExampRepos caseExampRepos;

    @Autowired
    private CaseReasRepos caseReasRepos;

    @Autowired
    private ExtractRepos extractRepos;

    @Autowired
    private ForExamRepos forExamRepos;

    @Autowired
    private InvolvedRepos involvedRepos;

    @Autowired
    private LawQuestRepos lawQuestRepos;

    @Autowired
    private LawScenesRepos lawScenesRepos;

    @Autowired
    private MainlyInvoRepos mainlyInvoRepos;

    @Autowired
    private RegulaRepos regulaRepos;

    @Autowired
    private ShowRepos showRepos;

    @Autowired
    private GraphDatabaseService graphDatabaseService;

    //点插入
    @Transactional
    public long addCaseExample (CaseExample caseExample){
        CaseExample c = caseExampRepos.save(caseExample);
        return c.getId();
    }
    @Transactional
    public long addCaseReason (CaseReason caseReason){
        CaseReason c = caseReasRepos.save(caseReason);
        return c.getId();
    }
    @Transactional
    public long addLawQuestion (LawQuestion lawQuestion){
        LawQuestion q = lawQuestRepos.save(lawQuestion);
        return q.getId();
    }
    @Transactional
    public long addLawScenes (LawScenes lawScenes){
        LawScenes s = lawScenesRepos.save(lawScenes);
        return s.getId();
    }
    @Transactional
    public long addRegulations (Regulations regulations){
        Regulations r = regulaRepos.save(regulations);
        return r.getId();
    }


    //边插入
    @Transactional
    public long addExtract (Extract extract){
        Extract e = extractRepos.save(extract);
        return e.getId();
    }
    @Transactional
    public long addInvloved (Involved invloved){
        Involved i = involvedRepos.save(invloved);
        return i.getId();
    }
    @Transactional
    public long addForExample (ForExample forExample){
        ForExample f = forExamRepos.save(forExample);
        return f.getId();
    }
    @Transactional
    public long addMainlyInvolved (MainlyInvolved mainlyInvolved){
        MainlyInvolved m = mainlyInvoRepos.save(mainlyInvolved);
        return m.getId();
    }
    @Transactional
    public long addShow (Show show){
        Show s = showRepos.save(show);
        return s.getId();
    }

    //点删除
    @Transactional
    public void deleteCaseExample (CaseExample caseExample){
        caseExampRepos.delete(caseExample);
    }
    @Transactional
    public void deleteCaseReason (CaseReason caseReason){
       caseReasRepos.delete(caseReason);
    }
    @Transactional
    public void deleteLawQuestion (LawQuestion lawQuestion){
       lawQuestRepos.delete(lawQuestion);

    }
    @Transactional
    public void deleteLawScenes (LawScenes lawScenes){
        lawScenesRepos.delete(lawScenes);

    }
    @Transactional
    public void deleteRegulations (Regulations regulations){
        regulaRepos.delete(regulations);
    }


    //边删除
    @Transactional
    public void deleteExtract (Extract extract){
        extractRepos.delete(extract);

    }
    @Transactional
    public void deleteInvloved (Involved invloved){
        involvedRepos.delete(invloved);

    }
    @Transactional
    public void deleteForExample (ForExample forExample){
        forExamRepos.delete(forExample);

    }
    @Transactional
    public void deleteMainlyInvolved (MainlyInvolved mainlyInvolved){
        mainlyInvoRepos.delete(mainlyInvolved);
    }
    @Transactional
    public void deleteShow (Show show){
        showRepos.delete(show);
    }

    //点查询--name查
    @Transactional
    public CaseExample getExpByName(String mongoId){
        return caseExampRepos.getExpByName(mongoId);
    }
    @Transactional
    public CaseReason getReasByName(String name){
        CaseReason reasByName = caseReasRepos.getReas(name);
        System.out.println();
        return reasByName;
    }
    @Transactional
    public LawQuestion getQuesByName(String name){
        return lawQuestRepos.getQuesByName(name);
    }
    @Transactional
    public LawScenes getScenByName(String name){
        return lawScenesRepos.getScenByName(name);
    }
    @Transactional
    public Regulations getRegByName(String name){
        return regulaRepos.getRegByName(name);
    }

}
