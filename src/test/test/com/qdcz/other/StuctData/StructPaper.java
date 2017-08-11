package com.qdcz.other.StuctData;

import com.qdcz.common.CommonTool;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.*;

/**
 * Created by star on 17-7-27.
 */
public class StructPaper {
    public static void main(String[] args) throws Exception{
        Scanner sc = new Scanner(new File("/media/star/Doc/工作文档/微信-论文项目/person.csv"));
        List<String> error_lines =  new ArrayList<>();

        JSONArray nodes_arr = new JSONArray();
        JSONArray edges_arr = new JSONArray();
        Map<String,String> identity_map =  new HashMap();
        Map<String,String> all_com_map = new HashMap();

        Set<String> per_num = new HashSet<>();


        while(sc.hasNext()){

            String line=   sc.nextLine().replace("\u201c","").replace("\u2014","").replace("\u201d","");


//            if(line.contains("Center for Chinese Rural Studies of Central China Normal University"))
//                System.out.println(line);

            String title_id = UUID.randomUUID().toString();


            String[] splits =  line.split(",");
            if(splits.length==9) {
//                for (String tmp:splits){
//                    System.out.println(tmp);
//                }


                String title = splits[0];
                String _abstract = splits[1];
                String author = splits[2];
                String all_company = splits[3];
                String journal = splits[4];
                String keywords = splits[5];
                String fund  = splits[6];
                String _class = splits[7];
                String url = splits[8];


//                if(!all_company.contains("中国科学院")){
//                    continue;
//                }



                if(all_company.isEmpty() || author.isEmpty() || title.isEmpty()  || all_company.contains("不详")){
                    error_lines.add(line);
                    continue;
                }

                Map<Integer,String> comp_map = getAllComMap(all_company);
                








                int au_index = 0;
                String tmppppp[] = new String[]{};
                tmppppp = author.split("(\t| )");
                if(author.replace(" ","").matches("\\w+")){
                    tmppppp =  author.split("\t");
                }

                for(String one_au: tmppppp){
                    au_index++;
                    one_au = one_au.replace("\\s+","");

                    //获取所有的人和对应单位
                    List<String> one_all = getOneToAll(one_au,comp_map);


                    for (String one_tmp_au_com: one_all){

                        one_au = one_tmp_au_com.split("_")[0];
                        String tmp_com = one_tmp_au_com.split("_")[1];
                        String mk = one_tmp_au_com.split("_")[2];

                        String company = "";
                        String area = "";
                        String zip_code = "";

                        if(tmp_com.endsWith("，")){
                            tmp_com = tmp_com.substring(0,tmp_com.length()-1);
                        }


                        if(tmp_com.contains("，") && !tmp_com.contains("Professor") && !tmp_com.contains("University")
                                && !tmp_com.contains("Reassessment")  && !tmp_com.contains("Metaphysics")
                                && !tmp_com.contains("Philosophic") && !tmp_com.contains("Hospitality")){
                            company= tmp_com.split("，")[0];


                            area = tmp_com.split("，")[1].replace(")","").replace(":","").replace("〕","").replace("#","").replace(".","").replace(" ","").replaceAll("[\\d\\w]{4,10}","");

                            zip_code =  tmp_com.split("，")[1].replace(")","").replace(":","").replace("〕","").replace("#","").replace(".","").replace(" ","").replace(area,"");


//                            if(tmp_com.contains("Virginia"))
//                                System.out.println(tmp_com);
//
//
//                            if(!zip_code.isEmpty() && !"WC2A2AE".equals(zip_code) && !"i311121".equals(zip_code)
//                                    && !"ACT2604".equals(zip_code) && !"CA94720".equals(zip_code)
//                                    && !"S0171BJ".equals(zip_code) && !"41600G".equals(zip_code)
//                                    && !"PR12HE".equals(zip_code))
//                                Integer.parseInt(zip_code);

                        }else if(tmp_com.contains("，")){
                            company= tmp_com.split("，")[0];

                            area = tmp_com.split("，")[1];

                        }else{
                            company = tmp_com;
                        }

                        String per_num_test = one_au+"\t"+company+"\t"+title;
                        if(!per_num.contains(per_num_test)){
                            per_num.add(per_num_test);
                        }


                        String person_id =UUID.randomUUID().toString();

                        if(identity_map.containsKey(one_au.trim()+"_"+title.trim())){
                            person_id = identity_map.get(one_au.trim()+"_"+title.trim());
                            identity_map.put(one_au.trim()+"_"+company.trim(),person_id);
                        }else if(identity_map.containsKey(one_au.trim()+"_"+company.trim())){
                            person_id = identity_map.get(one_au.trim()+"_"+company.trim());
                            identity_map.put(one_au.trim()+"_"+title.trim(),person_id);
                        }else{
                            identity_map.put(one_au.trim()+"_"+title.trim(),person_id);
                            identity_map.put(one_au.trim()+"_"+company.trim(),person_id);
                        }


                        String com_id = UUID.randomUUID().toString();
                        if(all_com_map.containsKey(company.trim())){
                            com_id =all_com_map.get(company.trim());
                        }else{
                            all_com_map.put(company.trim(),com_id);
                        }

//                        System.out.println(one_au+"\t"+company+ "\t"+area+"\t"+zip_code);


                        JSONObject content_com = new JSONObject();
                        content_com.put("area",area.trim());
                        content_com.put("zipcode",zip_code.trim());

                        JSONObject node_com = new JSONObject();
                        node_com.put("identity",com_id);
                        node_com.put("root","社会科学知识库");
                        node_com.put("name",company.trim());
                        node_com.put("type","com");
                        node_com.put("content",content_com.toString());

//                System.out.println(author+"\t"+company+"\t"+url);



                        nodes_arr.put(node_com);






                        JSONObject node_person = new JSONObject();
                        node_person.put("identity",person_id);
                        node_person.put("root","社会科学知识库");
                        node_person.put("name",one_au.trim());
                        node_person.put("type","author");
                        node_person.put("content",new JSONObject().toString());



                        JSONObject content_one_edge =  new JSONObject();
                        content_one_edge.put("order",mk);

                        JSONObject one_edges = new JSONObject();
                        one_edges.put("root","社会科学知识库");
                        one_edges.put("from",person_id);
                        one_edges.put("to",com_id);
                        one_edges.put("name","属于");
                        one_edges.put("content",content_one_edge.toString());




                        JSONObject content_two_edge =  new JSONObject();
                        content_two_edge.put("order",au_index+"");

                        JSONObject two_edges = new JSONObject();
                        two_edges.put("root","社会科学知识库");
                        two_edges.put("from",person_id);
                        two_edges.put("to",title_id);
                        two_edges.put("name","发表");
                        two_edges.put("content",content_two_edge);



                        nodes_arr.put(node_person);
                        edges_arr.put(one_edges);
                        edges_arr.put(two_edges);

                    }
                }

                JSONObject content_paper = new JSONObject();
                content_paper.put("abstract",_abstract.trim());
                content_paper.put("journal",journal.trim());
                content_paper.put("keywords",keywords.trim());
                content_paper.put("fund",fund.trim());
                content_paper.put("class",_class.trim());
                content_paper.put("url",url.trim());

                JSONObject node_paper = new JSONObject();
                node_paper.put("identity",title_id);
                node_paper.put("root","社会科学知识库");
                node_paper.put("name",title.replace("\\s+","").replace(" ",""));
                node_paper.put("type","paper");
                node_paper.put("content",content_paper.toString());
                nodes_arr.put(node_paper);
            }else{
                error_lines.add(line);
            }
        }
        sc.close();



        StringBuffer sb = new StringBuffer();
        for (int i = 0;i< nodes_arr.length();i++){
            sb.append(nodes_arr.getJSONObject(i).toString()+"\n");
        }
        CommonTool.printFile(sb.toString().getBytes(),"/media/star/Doc/工作文档/微信-论文项目/vertex_all.txt");
        sb.delete(0,sb.length());

        for (int i = 0;i< edges_arr.length();i++){
            sb.append(edges_arr.getJSONObject(i).toString()+"\n");
        }
        CommonTool.printFile(sb.toString().getBytes(),"/media/star/Doc/工作文档/微信-论文项目/edges_all.txt");
        sb.delete(0,sb.length());



        for (String error_one_line:error_lines){
            sb.append(error_one_line+"\n");
//            System.out.println(error_one_line);
        }
        CommonTool.printFile(sb.toString().getBytes(),"/media/star/Doc/工作文档/微信-论文项目/error.txt");
        sb.delete(0,sb.length());

        for (String one_per:per_num){
            sb.append(one_per+"\n");
//            System.out.println(error_one_line);
        }
        CommonTool.printFile(sb.toString().getBytes(),"/media/star/Doc/工作文档/微信-论文项目/per_num_test.txt");
        sb.delete(0,sb.length());



    }

    private static Map<Integer,String> getAllComMap(String all_company) {
        Map<Integer,String> comp_map = new HashMap();


        if(all_company.matches(".*\\[\\d+\\]+.*")){
            String[] tmps_com = all_company.split("\\[\\d+\\]");

            int tmp_com_index = 1;
            for(String tmp_one_com:tmps_com)
                if(!tmp_one_com.isEmpty()){
                    comp_map.put(tmp_com_index,tmp_one_com);
                    tmp_com_index++;
                }
        }else{
            comp_map.put(1,all_company);
        }
        return comp_map;
    }

    private static List<String> getOneToAll(String one_au, Map<Integer, String> comp_map) {
        List<String> list = new ArrayList<>();
        if(one_au.matches(".*\\[.*\\]")){
            String au = one_au.replaceAll("\\[.*\\]","");

            String[] dd = one_au.replace(au,"").replace("[","").replace("]","").split("，");

            for(int i = 1;i<15;i++){
                String mmm = "1";
                if(dd.length != 1){
                    for (int mk = 0;mk<dd.length ;mk++){
                        String one_index = dd[mk];

                        if(Integer.parseInt(one_index) == i){
                            mmm = (mk+1)+"";
                        }
                    }
                }




                if(one_au.contains(i+"")){
                    list.add(au+"_"+comp_map.get(i)+"_"+mmm);
                }
            }


            if(one_au.contains("15")){
                throw new RuntimeException(one_au);
            }
        }else{
            list.add(one_au+"_"+comp_map.get(1)+"_1");
        }

        return list;

    }
}
