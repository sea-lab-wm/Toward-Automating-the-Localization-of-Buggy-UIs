import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalEvaluator;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.DefaultRetrievalEvaluator;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.RAMRetrievalIndexer;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.lucene.LuceneRetrievalSearcher;
import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RelJudgment;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalStats;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.store.Directory;
import seers.textanalyzer.PreprocessingOptionsParser;
import seers.textanalyzer.TextProcessor;
import seers.textanalyzer.entity.Sentence;
import seers.textanalyzer.entity.Token;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.commons.io.FileUtils;
import java.nio.charset.StandardCharsets;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import com.opencsv.CSVWriter;
import java.lang.String;
import com.opencsv.*;
import org.javatuples.Triplet;
import org.javatuples.Quartet;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class MainClass {  
    //https://stackoverflow.com/questions/3571223/how-do-i-get-the-file-extension-of-a-file-in-java
    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }

    //https://stackoverflow.com/questions/14676407/list-all-files-in-the-folder-and-also-sub-folders
    public static void listf(String directoryName, List<File> files) {
        File directory = new File(directoryName);
    
        // Get all files from a directory.
        File[] fList = directory.listFiles();
        if(fList != null) {
            for (File file : fList) {      
                if (file.isFile()) {
                    
                    String extension = getFileExtension(file);
                    if(extension.equals(".java")) {
                        files.add(file);
                    }
                } else if (file.isDirectory()) {
                    listf(file.getAbsolutePath(), files);
                }
            }
        }
    }

    private static List<String> get_buggy_java_files(String bug_id, String buggy_project_dir) {
        List<String> buggy_file_list = new ArrayList<String>();
        String json_file = "../../data/JSON-Files-All/" + bug_id + ".json";
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(json_file));
            JSONObject jsonObj = (JSONObject) obj;
            JSONArray bug_locations = (JSONArray)jsonObj.get("bug_location");

            for (int i = 0; i < bug_locations.size(); i++) {
                Object object = bug_locations.get(i);
                JSONObject jsonObject = (JSONObject) object;
                String name = jsonObject.get("file_name").toString();
                String buggy_file_full_path = buggy_project_dir + "/" + name;
                String file_extension = getFileExtension(new File(buggy_file_full_path));
                if(file_extension.equals(".java")) {
                    buggy_file_list.add(buggy_file_full_path);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return buggy_file_list;
    }
    
    private static boolean isBuggy(List<String> buggy_file_list, String filename) {
        if(buggy_file_list.contains(filename)) {
            return true;
        }
        return false;
    }

    private static int get_number_of_found_files(String bug_id, String query_reformulation_type, String result_file) throws Exception {
        File file = new File(result_file);
        if(!file.exists()) {
            return 0;
        }

        try (CSVReader csvReader = new CSVReader(new FileReader(result_file));) {
            String[] row = null;
            while((row=csvReader.readNext())!=null) {
                List<String> items = Arrays.asList(row);
                if (items.get(0).equals(bug_id) && items.get(1).equals(query_reformulation_type)) {
                    return Integer.parseInt(items.get(2));
                }
            }
        }
        return 0;
    }

    private static String get_bug_report_contents_preprocessed(String queries_dir, String bug_id, String obID, String query) throws Exception{
        String bug_report_file = queries_dir + "/" + query + "/bug_report_" + bug_id + "/ob_" + obID +".txt";
        String bug_report_contents = FileUtils.readFileToString(new File(bug_report_file), StandardCharsets.UTF_8);
        if (bug_report_contents == null || bug_report_contents.length()<1) {
            bug_report_contents = "";
        }

        return bug_report_contents;
    }

    private static String get_code_preprocessed(String preprocessed_code_path, String bug_id, String code_file_name) throws Exception{
        String code_store_file = preprocessed_code_path + "/bug-" + bug_id + ".csv";
        try (CSVReader csvReader = new CSVReader(new FileReader(code_store_file));) {
            String[] row = null;
            while((row=csvReader.readNext())!=null) {
                List<String> items = Arrays.asList(row);
                if (items.get(0).equals(code_file_name)) {
                    if(items.get(1)==null || items.get(1).length()<1) {
                        return "";
                    }
                    return items.get(1);
                }
            }
        }
        return "";
    }

     // Function to sort hashmap by values
     private static HashMap<String, Double> sortByValue(HashMap<String, Double> hm)
     {
         // Create a list from elements of HashMap
         List<Map.Entry<String, Double> > list =
                 new LinkedList<>(hm.entrySet());
 
         // Sort the list
         Collections.sort(list, (o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));
 
         // put data from sorted list to hashmap
         HashMap<String, Double> temp = new LinkedHashMap<>();
         for (Map.Entry<String, Double> aa : list) {
             temp.put(aa.getKey(), aa.getValue());
         }
         return temp;
     }

    private static ArrayList<AbstractMap.SimpleEntry<String, String>> getRanklist(String bugID, String obID, String obText, Query query, 
    List<RetrievalDoc> corpus, List<String> buggy_file_list, CSVWriter file_count_writer, CSVWriter file_ranks_writer, String query_reformulation_type) throws Exception {
        ArrayList<AbstractMap.SimpleEntry<String, String>> ranklist = new ArrayList<>();

        //evaluator creation, for computing effectiveness metrics
        RetrievalEvaluator evaluator = new DefaultRetrievalEvaluator();

        //index the corpus and store it in RAM
        //Use the class DefaultRetrievalIndexer to store the index in disk
        try (Directory index = new RAMRetrievalIndexer().buildIndex(corpus, null)) {

            //the searcher
            LuceneRetrievalSearcher searcher = new LuceneRetrievalSearcher(index, null);

            //search
            List<RetrievalDoc> searchResults = searcher.searchQuery(query);


            //get results from the corpus so that we can print their content
            List<RetrievalDoc> resultsFromCorpus =
                    corpus.stream().filter(searchResults::contains).collect(Collectors.toList());

            file_count_writer.writeNext(new String[]{bugID, obID, obText, query_reformulation_type, String.valueOf(resultsFromCorpus.size())});
            file_count_writer.flush();

            HashMap<String, Double> scores= new HashMap<>();
            HashMap<String, Double> sortedScores= new HashMap<>();

            for (RetrievalDoc doc : searchResults) {
                scores.put(doc.getDocName(), (double)doc.getDocScore());
            }

            sortedScores = sortByValue(scores);
            int rank = 1;

            for(Map.Entry<String, Double> entry: sortedScores.entrySet()) {
                file_ranks_writer.writeNext(new String[]{bugID, obID, obText, entry.getKey(), String.valueOf(rank), String.valueOf(entry.getValue())});
                rank++;
            }
            file_ranks_writer.flush();

            for (int fileIndex=0; fileIndex<corpus.size(); fileIndex++) {
                //expected results for the query
                RelJudgment expectedSearchResults = new RelJudgment();
                String bug_file_name = corpus.get(fileIndex).getDocName();
                
                if (isBuggy(buggy_file_list, bug_file_name)) {
                    // System.out.println("corpus index: " + List.of(corpus.get(fileIndex)));
                    expectedSearchResults.setRelevantDocs(List.of(corpus.get(fileIndex)));
                    
                    //compute metrics (see what each position means in the resulting list)
                    List<Double> evalResults = evaluator.evaluateRelJudgment(expectedSearchResults, searchResults);
                    // System.out.print("Query evaluation results = ");
                    // System.out.println(evalResults.get(0));
                    ranklist.add(new AbstractMap.SimpleEntry<>(bug_file_name, evalResults.get(0).toString()));
                }   
            }
        }

        return ranklist;
    }

    private static void processResults(String queries_dir, String bug_id, String obID, String obText,
        List<String>preprocessedCodeDocuments, List<String>codeFileNameList, List<String> buggy_file_list,
        CSVWriter writer, CSVWriter query_replacement_writer, CSVWriter query_expansion1_writer,
        CSVWriter file_count_writer, CSVWriter bug_report_files_writer, CSVWriter query_replacement_files_writer, CSVWriter query_expansion1_files_writer) throws Exception{
        //build corpus
        List<RetrievalDoc> corpus = IntStream.range(0, preprocessedCodeDocuments.size())
                .mapToObj(i -> {
                    String docText = preprocessedCodeDocuments.get(i);
                    if (StringUtils.isBlank(docText)) return null;
                    int docId = i;
                    String docName = codeFileNameList.get(i);
                    return new RetrievalDoc(docId, docText, docName);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        String original_bug_report = get_bug_report_contents_preprocessed(queries_dir, bug_id, obID, "bug_report_original");
        writeResult(corpus, bug_id, obID, obText, buggy_file_list, original_bug_report, writer, file_count_writer, bug_report_files_writer, "original-bug-report");
        String replaced_query = get_bug_report_contents_preprocessed(queries_dir, bug_id, obID, "replaced_query");
        writeResult(corpus, bug_id, obID, obText, buggy_file_list, replaced_query, query_replacement_writer, file_count_writer, query_replacement_files_writer, "replaced-query");
        String query_expansion_1 = get_bug_report_contents_preprocessed(queries_dir, bug_id, obID, "query_expansion_1");
        writeResult(corpus, bug_id, obID, obText, buggy_file_list, query_expansion_1, query_expansion1_writer, file_count_writer, query_expansion1_files_writer, "query-expansion-1");
    }

    private static void writeResult(List<RetrievalDoc> corpus, String bugID, String obID, String obText, 
        List<String> buggy_file_list, String preprocessedQuery,
        CSVWriter writer, CSVWriter file_count_writer, CSVWriter file_ranks_writer, String query_reformulation_type) throws Exception{
        
        if (preprocessedQuery == null || preprocessedQuery.isEmpty()) {
            writer.writeNext(new String[]{bugID, "[]", "[]", "[]", "[]", "[]"});
            writer.flush();
            return;
        }

        // String preprocessedBugReport = preprocessText(bugReportContent, stopWords);
        Query query = new Query(1, preprocessedQuery);
       
        List<String> row = new ArrayList<String>();
        ArrayList<AbstractMap.SimpleEntry<String, String>> ranklist = getRanklist(bugID, obID, obText, query, corpus, buggy_file_list, file_count_writer, file_ranks_writer, query_reformulation_type);

        row.add(bugID);
        row.add(obID);
        row.add(obText);
        
        StringBuilder ranks_str=new StringBuilder();
        StringBuilder sorted_ranks_str = new StringBuilder();
        StringBuilder bug_file_str_list=new StringBuilder();

        ranks_str.append("[");
        bug_file_str_list.append("[");
        sorted_ranks_str.append("[");

        int ind = 0;
        List<Integer> sorted_ranklist = new ArrayList<Integer>();
        for(AbstractMap.SimpleEntry<String, String> item: ranklist) {
            ranks_str.append(item.getValue());
            sorted_ranklist.add((int)Double.parseDouble(item.getValue()));
            bug_file_str_list.append(item.getKey());
            if (ind!=ranklist.size()-1) {
                ranks_str.append(",");
                bug_file_str_list.append(",");
            }
            ind++;
        }
        bug_file_str_list.append("]");
        ranks_str.append("]");
        Collections.sort(sorted_ranklist);

        ind = 0;
        for(Integer item: sorted_ranklist) {
            sorted_ranks_str.append(item.toString());
            if (ind!=sorted_ranklist.size()-1) {
                sorted_ranks_str.append(",");
            }
            ind++;
        }
        sorted_ranks_str.append("]");
        
        row.add(ranks_str.toString());
        row.add(sorted_ranks_str.toString());
        row.add(bug_file_str_list.toString());

        // System.out.println(ranks_str.toString());
        String[] rowArray = new String[row.size()];

        for (int i = 0; i < row.size(); i++) {
            rowArray[i] = row.get(i);
        }

        writer.writeNext(rowArray);
        writer.flush();
    }

    private static void createDirectories(String filepath) {
        File file = new File(filepath);
        File parent = new File(file.getParent());
        if (!parent.exists()) {
            parent.mkdirs();
        }
    }

    private static List<CSVWriter> create_result_files_header(String result_sub_dir) throws Exception{
        String header[] = {"Bug Id", "OB Id", "OB Text", "Ranks(unsorted)", "Ranks", "Ranked Buggy Files"};

        List<CSVWriter> writerList = new ArrayList<CSVWriter>();
        String bug_report_file = result_sub_dir + "/original-bug-report.csv";
        createDirectories(bug_report_file);
        CSVWriter bug_report_writer = new CSVWriter(new FileWriter(bug_report_file));
        bug_report_writer.writeNext(header);
        bug_report_writer.flush();
        writerList.add(bug_report_writer);

        String query_replacement_file = result_sub_dir + "/replaced-query.csv";
        createDirectories(query_replacement_file);
        CSVWriter query_replacement_writer = new CSVWriter(new FileWriter(query_replacement_file));
        query_replacement_writer.writeNext(header);
        query_replacement_writer.flush();
        writerList.add(query_replacement_writer);

        String query_expansion1_file = result_sub_dir + "/query-expansion-1.csv";
        createDirectories(query_expansion1_file);
        CSVWriter query_expansion1_writer = new CSVWriter(new FileWriter(query_expansion1_file));
        query_expansion1_writer.writeNext(header);
        query_expansion1_writer.flush();
        writerList.add(query_expansion1_writer);

        String retrieved_files_count_store_file = result_sub_dir + "/retrieved-files.csv";
        createDirectories(retrieved_files_count_store_file);
        CSVWriter retrieved_files_count_writer = new CSVWriter(new FileWriter(retrieved_files_count_store_file));
        String file_count_header[] = {"Bug_Id", "OB Id", "OB Text","Query_Reformulation_Type", "Number_Of_Retrieved_Files"};
        retrieved_files_count_writer.writeNext(file_count_header);
        retrieved_files_count_writer.flush();
        writerList.add(retrieved_files_count_writer);

        return writerList;
    }

    private static List<CSVWriter> create_file_similarity_header(String result_sub_dir) throws Exception{
        String header[] = {"Bug Id", "OB Id", "OB Text", "Filepath", "Rank", "Score"};

        List<CSVWriter> writerList = new ArrayList<CSVWriter>();
        String bug_report_file = result_sub_dir + "/original-bug-report-file-ranks.csv";
        createDirectories(bug_report_file);
        CSVWriter bug_report_writer = new CSVWriter(new FileWriter(bug_report_file));
        bug_report_writer.writeNext(header);
        bug_report_writer.flush();
        writerList.add(bug_report_writer);

        String query_replacement_file = result_sub_dir + "/replaced-query-file-ranks.csv";
        createDirectories(query_replacement_file);
        CSVWriter query_replacement_writer = new CSVWriter(new FileWriter(query_replacement_file));
        query_replacement_writer.writeNext(header);
        query_replacement_writer.flush();
        writerList.add(query_replacement_writer);

        String query_expansion1_file = result_sub_dir + "/query-expansion-1-file-ranks.csv";
        createDirectories(query_expansion1_file);
        CSVWriter query_expansion1_writer = new CSVWriter(new FileWriter(query_expansion1_file));
        query_expansion1_writer.writeNext(header);
        query_expansion1_writer.flush();
        writerList.add(query_expansion1_writer);

        return writerList;
    }

    private static CSVWriter create_final_result_header(String result_file) throws Exception {
        createDirectories(result_file);
        String[] final_header = {"Bug Report ID", "OB Id", "OB Text", "Ranks-unsorted (Query-Bug Report)", "Ranks (Query-Bug Report)", "Files (Query-Bug Report)",
            "Ranks-unsorted (Query Replacement)", "Ranks (Query Replacement)", "Files (Query Replacement)",
            "Ranks-unsorted (Query Expansion 1)", "Ranks (Query Expansion 1)", "Files (Query Expansion 1)"};

        CSVWriter final_result_writer = new CSVWriter(new FileWriter(result_file));
        final_result_writer.writeNext(final_header);
        final_result_writer.flush();
        return final_result_writer;
    }

     private static CSVWriter create_re_rank_result_header(String result_file) throws Exception {
        createDirectories(result_file);
        String[] final_header = {"Bug Report ID", "Ranks-unsorted (Query-Bug Report)", "Ranks (Query-Bug Report)", "Files (Query-Bug Report)",
            "Ranks-unsorted (Query Replacement)", "Ranks (Query Replacement)", "Files (Query Replacement)",
            "Ranks-unsorted (Query Expansion 1)", "Ranks (Query Expansion 1)", "Files (Query Expansion 1)"};

        CSVWriter final_result_writer = new CSVWriter(new FileWriter(result_file));
        final_result_writer.writeNext(final_header);
        final_result_writer.flush();
        return final_result_writer;
    }

    private static CSVWriter create_final_file_ranks_header(String result_file) throws Exception {
        createDirectories(result_file);
        String[] final_header = {"Bug Report ID", "OB Id", "OB Text", "Filepath", "Rank", "Score"};

        CSVWriter final_result_writer = new CSVWriter(new FileWriter(result_file));
        final_result_writer.writeNext(final_header);
        final_result_writer.flush();
        return final_result_writer;
    }

    private static CSVWriter create_re_rank_file_ranks_header(String result_file) throws Exception {
        createDirectories(result_file);
        String[] final_header = {"Bug Report ID", "Filepath", "Rank", "Score"};

        CSVWriter final_result_writer = new CSVWriter(new FileWriter(result_file));
        final_result_writer.writeNext(final_header);
        final_result_writer.flush();
        return final_result_writer;
    }

    private static List<Integer> listStringToInt(String rankStr) {
        List<Integer>ranks = new ArrayList<Integer>();
        String[] rankVals = rankStr.split("[,\\[\\] ]+");
        for(String rnk: rankVals) {
            if(rnk == null || rnk.length()<1) {
                continue;
            }

            int val = (int)Double.parseDouble(rnk);
            ranks.add(val);
        }
        return ranks;
    }

    private static List<String> listStringToString(String rankStr) {
        List<String>ranks = new ArrayList<String>();
        String[] rankVals = rankStr.split("[,\\[\\]\\s]+");
        for(String rnk: rankVals) {
            if(rnk == null || rnk.length()<1) {
                continue;
            }
            ranks.add(rnk);
        }
        return ranks;
    }

    //https://www.baeldung.com/java-csv-file-array
    private static List<Integer> get_ranks(String bug_id, String obID, String obText, String result_file) throws Exception {
        File file = new File(result_file);
        if(!file.exists()) {
            return Collections.emptyList();
        }
        try (CSVReader csvReader = new CSVReader(new FileReader(result_file));) {
            String[] row = null;
            while((row=csvReader.readNext())!=null) {
                List<String> items = Arrays.asList(row);
                if (items.get(0).equals(bug_id) && items.get(1).equals(obID)) {
                    if(items.get(3).equals("[]") || items.get(3)==null || items.get(3).length()<1) {
                        return Collections.emptyList();
                    }
                    return listStringToInt(items.get(3));
                }
            }
        }
        return Collections.emptyList();
    }

    private static List<String> get_buggy_found_files(String bug_id, String obID, String obText, String result_file) throws Exception {
        File file = new File(result_file);
        if(!file.exists()) {
            return Collections.emptyList();
        }

        try (CSVReader csvReader = new CSVReader(new FileReader(result_file));) {
            String[] row = null;
            while((row=csvReader.readNext())!=null) {
                List<String> items = Arrays.asList(row);
                if (items.get(0).equals(bug_id) && items.get(1).equals(obID)) {
                    if(items.get(3).equals("[]") || items.get(3)==null || items.get(3).length()<1) {
                        return Collections.emptyList();
                    }
                    return listStringToString(items.get(5));
                }
            }
        }
        return Collections.emptyList();
    }

    //Input: bug ID and filename, Output: Lucene score
     private static double get_score_for_individual_file(String bug_id, String obID, String cur_file, HashMap<String, String> fileInfo) {
        String searchKey = bug_id + "-" + obID + "-" + cur_file;

        if(fileInfo.containsKey(searchKey)) {
            return Double.parseDouble(fileInfo.get(searchKey));
        }
        return 0.0;
    }

    //Calculate average score for each file in terms of individual ob ids
    private static double get_average_Lucene_score(String bug_id, List<String> obIDList, String cur_file, HashMap<String, String> fileInfo) {
        double scoreSum = 0.0;
        for(String obID: obIDList) {
            double score = get_score_for_individual_file(bug_id, obID, cur_file, fileInfo);
            scoreSum += score;
        }
        if (obIDList.size()==0) {
            return 0.0;
        }
        return scoreSum/(double)obIDList.size();
    }

    private static List<String> get_filtered_files(String bug_id, String obID, String obText, String filename) throws Exception {
        File file = new File(filename);
        if(!file.exists()) {
            return Collections.emptyList();
        }
        List<String> filtered_files = new ArrayList<String>();
        try (CSVReader csvReader = new CSVReader(new FileReader(filename));) {
            String[] row = null;
            while((row=csvReader.readNext())!=null) {
                List<String> items = Arrays.asList(row);
                if (items.get(0).equals(bug_id) && items.get(1).equals(obID)) {
                    String cur_file = items.get(4);
                    filtered_files.add(cur_file);
                }
            }
        }
        return filtered_files;
    }

    private static List<String[]> get_individual_file_info(String bug_id, String obID, String obText, String filename) throws Exception {
        File file = new File(filename);
        if(!file.exists()) {
            return Collections.emptyList();
        }
        List<String[]> file_info_list = new ArrayList<String[]>();
        try (CSVReader csvReader = new CSVReader(new FileReader(filename));) {
            String[] row = null;
            while((row=csvReader.readNext())!=null) {
                List<String> items = Arrays.asList(row);
                if (items.get(0).equals(bug_id) && items.get(1).equals(obID)) {
                    String cur_file = items.get(3);
                    String rank = items.get(4);
                    String score = items.get(5);
                    file_info_list.add(new String[]{bug_id, obID, obText, cur_file, rank, score});
                }
            }
        }
        return file_info_list;
    }

    // https://stackoverflow.com/questions/189559/how-do-i-join-two-lists-in-java
    private static List<Integer> concatIntegersLists(List<Integer> list1, List<Integer> list2) {
        List<Integer> concatenatedList = Stream.concat(list1.stream(), list2.stream())
                .collect(Collectors.toList());
        return concatenatedList;
    }

    private static List<String> concatStringsLists(List<String> list1, List<String> list2) {
        List<String> concatenatedList = Stream.concat(list1.stream(), list2.stream())
                .collect(Collectors.toList());
        return concatenatedList;
    }

    private static void get_final_ranks(String bug_id, String obID, String obText, String query_type, String matched_files_stored_file, 
        String result_folder1, String result_folder2, String operations, List<Integer> unsorted_ranks,
        List<Integer> cur_ranks, List<String> ranks_files) throws Exception {

        List<Integer> unsorted_ranks1 = get_ranks(bug_id, obID, obText, result_folder1 + "/" + query_type + ".csv");
        List<String> ranks1_files = get_buggy_found_files(bug_id, obID, obText, result_folder1 + "/" + query_type + ".csv");
        
        if(operations.equals("Filtering") || operations.equals("QueryReformulation")) {
            List<Integer> sorted_ranks1 = new ArrayList<Integer>(unsorted_ranks1);
            Collections.sort(sorted_ranks1);
            unsorted_ranks.addAll(unsorted_ranks1);
            cur_ranks.addAll(sorted_ranks1);
            ranks_files.addAll(ranks1_files);
            return;
        }

        List<Integer> ranks2_temp = get_ranks(bug_id, obID, obText, result_folder2 + "/" + query_type + ".csv");
        List<String>ranks2_files_temp = get_buggy_found_files(bug_id, obID, obText, result_folder2 + "/" + query_type + ".csv");
        List<Integer>ranks2 = new ArrayList<Integer>();
        List<String>ranks2_files = new ArrayList<String>();

        for(int i=0;i<ranks2_temp.size();i++) {
            if(ranks2_temp.get(i)>0) {
                ranks2.add(ranks2_temp.get(i));
                ranks2_files.add(ranks2_files_temp.get(i));
            }
        }

        int number_of_filtered_files = get_filtered_files(bug_id, obID, obText, matched_files_stored_file).size();

        List<Integer> second_ranks = new ArrayList<Integer>();
        if (ranks2.size()>0) {
            second_ranks = ranks2.stream().map(r -> r + number_of_filtered_files).collect(Collectors.toList());
        }

        List<Integer> concatenatedIntegers = concatIntegersLists(unsorted_ranks1, second_ranks);

        List<Integer> sorted_ranks1 = new ArrayList<Integer>(concatenatedIntegers);
        Collections.sort(sorted_ranks1);
        List<String> concatenatedStrings = concatStringsLists(ranks1_files, ranks2_files);

        unsorted_ranks.addAll(concatenatedIntegers);
        cur_ranks.addAll(sorted_ranks1);
        ranks_files.addAll(concatenatedStrings);
    }

    private static List<String[]> get_individual_file_ranks(String bug_id, String obID, String obText, String query_type, String matched_files_stored_file, 
        String result_folder1, String result_folder2, String operations, List<Integer> unsorted_ranks,
        List<Integer> cur_ranks, List<String> ranks_files) throws Exception {

        List<String[]> file_info1 = get_individual_file_info(bug_id, obID, obText, result_folder1 + "/" + query_type + ".csv");
        
        if(operations.equals("Filtering") || operations.equals("QueryReformulation")) {
            return file_info1;
        }
        int number_of_filtered_files = get_filtered_files(bug_id, obID, obText, matched_files_stored_file).size();

        List<String[]> file_info2 = get_individual_file_info(bug_id, obID, obText, result_folder2 + "/" + query_type + ".csv");

        for(String[] info: file_info2) {
            int rank = Integer.parseInt(info[4]);
            info[4] = String.valueOf(rank+number_of_filtered_files);
        }

        double max_score_in_info2 = 0;
        if(file_info2.size()>0) {
            max_score_in_info2 = Double.parseDouble(file_info2.get(0)[5]);
        }

        for(String[] info: file_info1) {
            double score = Double.parseDouble(info[5]);
            info[5] = String.valueOf(score+max_score_in_info2);
        }

        file_info1.addAll(file_info2);
        return file_info1;
    }

    private static void clear_lists(List<Integer> unsorted_ranks1,
        List<Integer> cur_ranks, List<String> ranks1_files) {
        unsorted_ranks1.clear();
        cur_ranks.clear();
        ranks1_files.clear();
    }

    private static void add_values_to_row(List<Integer> unsorted_ranks1,
        List<Integer> cur_ranks, List<String> ranks1_files, List<String> data_row) {
        data_row.add(unsorted_ranks1.toString());
        data_row.add(cur_ranks.toString());
        data_row.add(ranks1_files.toString());
    }

    private static HashMap<String, String> getFilesAndScores(String rank_file) {
        HashMap<String, String> fileInfo = new HashMap<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(rank_file));) {
            String[] row = null;
            while((row=csvReader.readNext())!=null) {
                List<String> items = Arrays.asList(row);
                fileInfo.put(items.get(0) + "-" + items.get(1) + "-" + items.get(3), items.get(5));
            }
        } catch (Exception e) {
            System.out.println (e.getMessage());
        }
        return fileInfo;
    }

    private static void re_rank_files_on_average_similarity_for_each_query_type(List<Triplet<String, 
        String, String>> obInfoList, String buggy_project_dir, String final_ranks_file, CSVWriter re_rank_file_ranks_writer) throws Exception{
        String prevId = "-1";
        List<String> obIDList = new ArrayList<String>();
        TreeSet<Integer> bugIDSet = new TreeSet<Integer>();
        Map<String, List<String>> bugOBMap = new HashMap<>(); 
        for(Triplet<String, String, String> obInfo: obInfoList) {
            String bugID = obInfo.getValue0();
            String obID = obInfo.getValue1();
            bugIDSet.add(Integer.parseInt(bugID));

            if(!bugID.equals(prevId)) {
                if(!prevId.equals("-1")) {
                    bugOBMap.put(prevId, obIDList);
                }
                obIDList = new ArrayList<String>();
                obIDList.add(obID);
            } else {
                obIDList.add(obID);
            }
            prevId = bugID;
        }

        if(!prevId.equals("-1")) {
            bugOBMap.put(prevId, obIDList);
        }

        HashMap<String, String> fileInfo = getFilesAndScores(final_ranks_file);

        for(int bugID: bugIDSet) {
            String path = buggy_project_dir + "/bug-" + String.valueOf(bugID);
            obIDList = bugOBMap.get(String.valueOf(bugID));
            List<File> files = new ArrayList<File>();
            listf(path,files);

            HashMap<String, Double> scores= new HashMap<>();
            HashMap<String, Double> sortedScores= new HashMap<>();
            
            for(File file: files) {
                String cur_file = file.toString();
                double avg_score = get_average_Lucene_score(String.valueOf(bugID), obIDList, cur_file, fileInfo);
                scores.put(cur_file, avg_score);
            }
            sortedScores = sortByValue(scores);

            int rank = 1;
            for(Map.Entry<String, Double> entry: sortedScores.entrySet()) {
                re_rank_file_ranks_writer.writeNext(new String[]{String.valueOf(bugID), entry.getKey(), String.valueOf(rank), String.valueOf(entry.getValue())});
                rank++;
            }
            re_rank_file_ranks_writer.flush();
        }
    }

    private static void re_rank_files_on_average_similarity(List<Triplet<String, String, String>> obInfoList, String buggy_project_dir,
        String final_bug_report_file_ranks_file, String final_query_replacement_file_ranks_file, String final_query_expansion_file_ranks_file,
        CSVWriter re_rank_bug_report_file_ranks_writer, CSVWriter re_rank_query_replacement_file_ranks_writer, CSVWriter re_rank_query_expansion_file_ranks_writer) throws Exception {
            re_rank_files_on_average_similarity_for_each_query_type(obInfoList, buggy_project_dir, final_bug_report_file_ranks_file, re_rank_bug_report_file_ranks_writer);
            re_rank_files_on_average_similarity_for_each_query_type(obInfoList, buggy_project_dir, final_query_replacement_file_ranks_file, re_rank_query_replacement_file_ranks_writer);
            re_rank_files_on_average_similarity_for_each_query_type(obInfoList, buggy_project_dir, final_query_expansion_file_ranks_file, re_rank_query_expansion_file_ranks_writer);
    }

    private static TreeSet<Integer> get_bug_ids(List<Triplet<String, String, String>> obInfoList) {
        TreeSet<Integer> bugIDSet = new TreeSet<Integer>();

        for(Triplet<String, String, String> obInfo: obInfoList) {
            String bugID = obInfo.getValue0();
            bugIDSet.add(Integer.parseInt(bugID));
        }
        return bugIDSet;
    }

    private static HashMap<String, String> getReRankFileRanking(String re_rank_file) {
        HashMap<String, String> fileInfo = new HashMap<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(re_rank_file));) {
            String[] row = null;
            while((row=csvReader.readNext())!=null) {
                List<String> items = Arrays.asList(row);
                if(items.get(3).equals("0.0")) {
                    continue;
                }
                fileInfo.put(items.get(0) + "-" + items.get(1), items.get(2));
            }
        } catch (Exception e) {
            System.out.println (e.getMessage());
        }
        return fileInfo;
    }

    private static int getRankFromHashMap(String bugID, String buggy_file, HashMap<String, String> re_rank_fileInfo) {
        String searchKey = bugID + "-" + buggy_file;
        if(re_rank_fileInfo.containsKey(searchKey)) {
            return Integer.parseInt(re_rank_fileInfo.get(searchKey));
        }
        return -1;
    }

    //https://stackoverflow.com/questions/203984/how-do-i-remove-repeated-elements-from-arraylist
    private static List<String> removeDuplicates(List<String> curList) {
        Set<String> set = new HashSet<>(curList);
        curList.clear();
        curList.addAll(set);
        return curList;
    }

    private static void re_rank_files_ranking(List<Triplet<String, String, String>> obInfoList, String buggy_project_dir,
        String re_rank_bug_report_all_file, String re_rank_query_replacement_all_file, String re_rank_query_expansion_all_file,
        CSVWriter writer) throws Exception{
        TreeSet<Integer> bugIDSet = get_bug_ids(obInfoList);
        
        HashMap<String, String> re_rank_bug_report_all_file_info = getReRankFileRanking(re_rank_bug_report_all_file);
        HashMap<String, String> re_rank_query_replacement_all_file_info = getReRankFileRanking(re_rank_query_replacement_all_file);
        HashMap<String, String> re_rank_query_expansion_all_file_info = getReRankFileRanking(re_rank_query_expansion_all_file);

        for(int bugID: bugIDSet) {
            List<String> row = new ArrayList<String>();
            List<Integer> ranklist = new ArrayList<Integer>();
            List<String> buggy_found_files = new ArrayList<String>();

             List<String> buggy_file_list = get_buggy_java_files(String.valueOf(bugID), buggy_project_dir);
             buggy_file_list = removeDuplicates(buggy_file_list);

            row.add(String.valueOf(bugID));
             //For bug report
             for(String bug_file: buggy_file_list) {
                int rank = getRankFromHashMap(String.valueOf(bugID), bug_file, re_rank_bug_report_all_file_info);
                if(rank!=-1) {
                    ranklist.add(rank);
                    buggy_found_files.add(bug_file);
                }
             }
             row.add(ranklist.toString());
             Collections.sort(ranklist);
             row.add(ranklist.toString());
             row.add(buggy_found_files.toString());

              //For query replacement
              ranklist = new ArrayList<Integer>();
              buggy_found_files = new ArrayList<String>();
             for(String bug_file: buggy_file_list) {
                int rank = getRankFromHashMap(String.valueOf(bugID), bug_file, re_rank_query_replacement_all_file_info);
                if(rank!=-1) {
                    ranklist.add(rank);
                    buggy_found_files.add(bug_file);
                }
             }
             row.add(ranklist.toString());
             Collections.sort(ranklist);
             row.add(ranklist.toString());
             row.add(buggy_found_files.toString());

             //For query expansion
              ranklist = new ArrayList<Integer>();
              buggy_found_files = new ArrayList<String>();
             for(String bug_file: buggy_file_list) {
                int rank = getRankFromHashMap(String.valueOf(bugID), bug_file, re_rank_query_expansion_all_file_info);
                if(rank!=-1) {
                    ranklist.add(rank);
                    buggy_found_files.add(bug_file);
                }
             }
             row.add(ranklist.toString());
             Collections.sort(ranklist);
             row.add(ranklist.toString());
             row.add(buggy_found_files.toString());

             String[] rowArray = new String[row.size()];
             for(int i=0;i<row.size();i++) {
                rowArray[i] = row.get(i);
             }
            writer.writeNext(rowArray);
            writer.flush();
        }
    }

    private static void merge_query_matching_ranks(List<Triplet<String, String, String>> obInfoList, CSVWriter final_result_writer, String matched_file, 
    String matched_ranks_folder, String not_matched_ranks_folder, String operation_type) throws Exception {
        for(Triplet<String, String, String> obInfo: obInfoList) {
            String bug_id = obInfo.getValue0();
            String obID = obInfo.getValue1();
            String obText = obInfo.getValue2();
            
            List<String> data_row = new ArrayList<String>();
            data_row.add(bug_id);
            data_row.add(obID);
            data_row.add(obText);

            List<Integer> unsorted_ranks1 = new ArrayList<Integer>();
            List<Integer> cur_ranks = new ArrayList<Integer>();
            List<String> ranks1_files = new ArrayList<String>();

            String query_type = "original-bug-report";
            get_final_ranks(bug_id, obID, obText, query_type, matched_file, 
                matched_ranks_folder, not_matched_ranks_folder, operation_type, unsorted_ranks1, cur_ranks, ranks1_files);
            add_values_to_row(unsorted_ranks1, cur_ranks, ranks1_files, data_row);
            clear_lists(unsorted_ranks1, cur_ranks, ranks1_files);

            query_type = "replaced-query";
            get_final_ranks(bug_id, obID, obText, query_type, matched_file, 
                matched_ranks_folder, not_matched_ranks_folder, operation_type, unsorted_ranks1, cur_ranks, ranks1_files);
            add_values_to_row(unsorted_ranks1, cur_ranks, ranks1_files, data_row);
            clear_lists(unsorted_ranks1, cur_ranks, ranks1_files);

            query_type = "query-expansion-1";
            get_final_ranks(bug_id, obID, obText, query_type, matched_file, 
                matched_ranks_folder, not_matched_ranks_folder, operation_type, unsorted_ranks1, cur_ranks, ranks1_files);
            add_values_to_row(unsorted_ranks1, cur_ranks, ranks1_files, data_row);
            clear_lists(unsorted_ranks1, cur_ranks, ranks1_files);

            String[] rowArray = new String[data_row.size()];

            for (int l = 0; l < data_row.size(); l++) {
                rowArray[l] = data_row.get(l);

            }
            final_result_writer.writeNext(rowArray);
            final_result_writer.flush();
        }
    }

    private static void merge_and_update_file_ranks(List<Triplet<String, String, String>> obInfoList, CSVWriter final_bug_report_file_ranks_writer, 
        CSVWriter final_query_replacement_file_ranks_writer, 
        CSVWriter final_query_expansion_file_ranks_writer, String matched_file, 
        String matched_ranks_folder, String not_matched_ranks_folder, String operation_type) throws Exception {
        for(Triplet<String, String, String> obInfo: obInfoList) {
            String bug_id = obInfo.getValue0();
            String obID = obInfo.getValue1();
            String obText = obInfo.getValue2();

            List<String> data_row = new ArrayList<String>();
            data_row.add(bug_id);
            data_row.add(obID);
            data_row.add(obText);

            List<Integer> unsorted_ranks1 = new ArrayList<Integer>();
            List<Integer> cur_ranks = new ArrayList<Integer>();
            List<String> ranks1_files = new ArrayList<String>();

            String query_type = "original-bug-report-file-ranks";
            //get_individual_file_ranks
            List<String[]> file_ranks_bug_report = get_individual_file_ranks(bug_id, obID, obText, query_type, matched_file, 
                matched_ranks_folder, not_matched_ranks_folder, operation_type, unsorted_ranks1, cur_ranks, ranks1_files);
            file_ranks_bug_report.forEach(writer->final_bug_report_file_ranks_writer.writeNext(writer));
            final_bug_report_file_ranks_writer.flush();

            query_type = "replaced-query-file-ranks";
            List<String[]> file_ranks_query_replacement = get_individual_file_ranks(bug_id, obID, obText, query_type, matched_file, 
                matched_ranks_folder, not_matched_ranks_folder, operation_type, unsorted_ranks1, cur_ranks, ranks1_files);
            file_ranks_query_replacement.forEach(writer->final_query_replacement_file_ranks_writer.writeNext(writer));
            final_query_replacement_file_ranks_writer.flush();

            query_type = "query-expansion-1-file-ranks";
            List<String[]> file_ranks_query_expansion = get_individual_file_ranks(bug_id, obID, obText, query_type, matched_file, 
                matched_ranks_folder, not_matched_ranks_folder, operation_type, unsorted_ranks1, cur_ranks, ranks1_files);
            file_ranks_query_expansion.forEach(writer->final_query_expansion_file_ranks_writer.writeNext(writer));
            final_query_expansion_file_ranks_writer.flush();
        }
    }

    private static void file_search_and_rankings(List<Triplet<String, String, String>> obInfoList, List<String> stopWords, String result_folder, 
        String subpath, String filetype, 
        String preprocessed_code_dir, String filtered_boosted_dir, String buggy_project_dir, 
        String prep_code_path, String prep_query_path, String preprocessed_data_path, String jsonFilePath, 
        String query_reformulation_gui) throws Exception{

        String result_sub_dir = result_folder + "/" + subpath + "/QueryReformulation-" + query_reformulation_gui + "/" + filetype;
        List<CSVWriter> writerList = create_result_files_header(result_sub_dir);
        List<CSVWriter> all_files_writerList = create_file_similarity_header(result_sub_dir);

        String query_match_files_dir = filtered_boosted_dir + "/" + subpath +"/" + filetype;
        String queries_dir = preprocessed_data_path + "/" + prep_query_path;

        for(Triplet<String, String, String> obInfo: obInfoList) {
            String bugID = obInfo.getValue0();
            String obID = obInfo.getValue1();
            String obText = obInfo.getValue2();

            List<String>prepCodeFileContent = new ArrayList<String>();
            List<String>codeFileNameList = new ArrayList<String>();
            String path = query_match_files_dir + "/bug-" + bugID + "/ob-" + obID;
            List<File> files = new ArrayList<File>();
            listf(path,files);

            for(int i=0;i<files.size();i++) {
                String filename = files.get(i).toString();
                filename = filename.replace(path, preprocessed_code_dir + "/bug-" + bugID);
                codeFileNameList.add(filename);
                String prepContent = get_code_preprocessed(prep_code_path, bugID, filename);
                prepCodeFileContent.add(prepContent);
            }

            List<String> buggy_file_list = get_buggy_java_files(bugID, buggy_project_dir);
        
            processResults(queries_dir, bugID, obID, obText, prepCodeFileContent, codeFileNameList, buggy_file_list,
                writerList.get(0), writerList.get(1), writerList.get(2), writerList.get(3),
                all_files_writerList.get(0), all_files_writerList.get(1), all_files_writerList.get(2));
        }

        for(int i=0;i<writerList.size();i++) {
            writerList.get(i).close();
        }
    }

    private static List<Triplet<String, String, String>> getOBInfos(String csvFile) throws Exception {
    	List<Triplet<String, String, String>> obInfos = new ArrayList<>();
		try {
			//csv file containing data
			String strFile = csvFile;

			CSVReader reader = new CSVReader(new FileReader(strFile));
			String [] nextLine;
            int flag = 0;
			while ((nextLine = reader.readNext()) != null) {
                if(flag==0) {
                    flag=1;
                    continue;
                }
                obInfos.add(new Triplet<>(nextLine[0], nextLine[1], nextLine[2]));
			}
		} catch(Exception e) {
            e.printStackTrace();
        }

        return obInfos;
    }

    //run this class/method using project directory as the working directory for the program
    public static void main(String[] args) throws Exception {
        //https://argparse4j.github.io
        ArgumentParser parser = ArgumentParsers.newFor("MainClass").build().defaultHelp(true).description("MainClass Arguments");
        parser.addArgument("-rf","--result");
        parser.addArgument("-f", "--filtering");
        parser.addArgument("-b", "--boosting");
        parser.addArgument("-q", "--query_reformulation");
        parser.addArgument("-s", "--screen");
        parser.addArgument("-bpd", "--buggy_project_dir");
        parser.addArgument("-pcd", "--preprocessed_code_dir");
        parser.addArgument("-fbr","--filtered_boosted_repo");
        parser.addArgument("-preq","--prep_data_path");
        parser.addArgument("-prec","--prep_code_path");
        parser.addArgument("-jpath","--json_file_path");
        parser.addArgument("-ops","--operations");
        parser.addArgument("-franks","--final_ranks_folder");
        parser.addArgument("-fbfilenames","--filtered_boosted_filenames");

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        //https://stackoverflow.com/questions/5204051/how-to-calculate-the-running-time-of-my-program
        long start_time = System.currentTimeMillis();

        // //read stop words
        String stopWordsPath = "src/main/resources/java-keywords-bugs.txt";
        List<String> stopwords = TextProcessor.readStopWords(stopWordsPath);

        // BL and Mapping Bug IDs - 79 bugs
        // ArrayList<String> bug_issue_ids = new ArrayList<String>(Arrays.asList("2", "1028", "8", "10", "11", "18", "19", "1563", "1568", "44", "45", "1073", "53", "54", "55", "56", "1089", "71", "1096", "76", "84", "87", "92", "1640", "1641", "106", "1130", "1645", "110", "117", "1146", "1147", "1150", "1151", "128", "129", "130", "135", "158", "159", "160", "162", "168", "1198", "1202", "1205", "1207", "1213", "1214", "191", "192", "193", "1215", "1222", "199", "200", "201", "1223", "1224", "1228", "206", "209", "248", "256", "271", "275", "1299", "1389", "1399", "1402", "1403", "1406", "1425", "1428", "1430", "1441", "1445", "1446", "1481"));

        String screen_path_for_queries = "Screen-" + ns.getString("screen");
        String corpus_path_for_queries = screen_path_for_queries  + "/Corpus-All_Java_Files";
        String queryInfoFile = ns.getString("filtered_boosted_filenames") + "/" + corpus_path_for_queries + "/Queries.csv";

        List<Triplet<String, String, String>> obInfoList =  getOBInfos(queryInfoFile);
        TreeSet<Integer> bugIDSet = get_bug_ids(obInfoList);
        
        String prep_query_path = "Screen-" + ns.getString("screen") + "/Preprocessed_with_" + ns.getString("query_reformulation");

        if (ns.getString("operations").equals("Filtering")) {
            String corpus_path = "Screen-" + ns.getString("screen") + "/" + "Corpus-" + ns.getString("filtering");
            file_search_and_rankings(obInfoList, stopwords, ns.getString("result"), corpus_path, "FilteredFiles", 
                ns.getString("preprocessed_code_dir"), ns.getString("filtered_boosted_repo"), 
                ns.getString("buggy_project_dir"), ns.getString("prep_code_path"), 
                prep_query_path, ns.getString("prep_data_path"), ns.getString("json_file_path"), ns.getString("query_reformulation"));

            String final_ranks_file = ns.getString("final_ranks_folder") + "/Rankings/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Filtering-" + ns.getString("filtering") + "#Query_Reformulation-" + ns.getString("query_reformulation") + ".csv";
            String final_bug_report_file_ranks_file = ns.getString("final_ranks_folder") + "/Files/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Filtering-" + ns.getString("filtering") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-bug-report.csv";
            String final_query_replacement_file_ranks_file = ns.getString("final_ranks_folder") + "/Files/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Filtering-" + ns.getString("filtering") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-query-replacement.csv";
            String final_query_expansion_file_ranks_file = ns.getString("final_ranks_folder") + "/Files/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Filtering-" + ns.getString("filtering") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-query-expansion.csv";

            String matched_file = "";
            String matched_ranks_folder = ns.getString("result") + "/" + corpus_path + "/QueryReformulation-" + ns.getString("query_reformulation") + "/" + "FilteredFiles";
            String not_matched_ranks_folder = "";

            CSVWriter final_result_writer = create_final_result_header(final_ranks_file);
            merge_query_matching_ranks(obInfoList, final_result_writer, matched_file, 
                matched_ranks_folder, not_matched_ranks_folder, ns.getString("operations"));
            
            CSVWriter final_bug_report_file_ranks_writer = create_final_file_ranks_header(final_bug_report_file_ranks_file);
            CSVWriter final_query_replacement_file_ranks_writer = create_final_file_ranks_header(final_query_replacement_file_ranks_file);
            CSVWriter final_query_expansion_file_ranks_writer = create_final_file_ranks_header(final_query_expansion_file_ranks_file);
            merge_and_update_file_ranks(obInfoList, final_bug_report_file_ranks_writer, final_query_replacement_file_ranks_writer, 
                final_query_expansion_file_ranks_writer, matched_file, 
                matched_ranks_folder, not_matched_ranks_folder, ns.getString("operations"));

            String re_rank_bug_report_file_ranks_file = ns.getString("final_ranks_folder") + "/FilesOnAverageRankings/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Filtering-" + ns.getString("filtering") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-re-ranked-bug-report.csv";
            String re_rank_query_replacement_file_ranks_file = ns.getString("final_ranks_folder") + "/FilesOnAverageRankings/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Filtering-" + ns.getString("filtering") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-re-ranked-query-replacement.csv";
            String re_rank_query_expansion_file_ranks_file = ns.getString("final_ranks_folder") + "/FilesOnAverageRankings/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Filtering-" + ns.getString("filtering") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-re-ranked-query-expansion.csv";
            CSVWriter re_rank_bug_report_file_ranks_writer = create_re_rank_file_ranks_header(re_rank_bug_report_file_ranks_file);
            CSVWriter re_rank_query_replacement_file_ranks_writer = create_re_rank_file_ranks_header(re_rank_query_replacement_file_ranks_file);
            CSVWriter re_rank_query_expansion_file_ranks_writer = create_re_rank_file_ranks_header(re_rank_query_expansion_file_ranks_file);

            re_rank_files_on_average_similarity(obInfoList, ns.getString("buggy_project_dir"), 
                final_bug_report_file_ranks_file, final_query_replacement_file_ranks_file, final_query_expansion_file_ranks_file,
                re_rank_bug_report_file_ranks_writer, re_rank_query_replacement_file_ranks_writer, re_rank_query_expansion_file_ranks_writer);

            String re_rank_buggy_file_rankes_file = ns.getString("final_ranks_folder") + "/AverageSimilarityRankings/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Filtering-" + ns.getString("filtering") + "#Query_Reformulation-" + ns.getString("query_reformulation") + ".csv";
            CSVWriter re_rank_result_writer = create_re_rank_result_header(re_rank_buggy_file_rankes_file);
            re_rank_files_ranking(obInfoList, ns.getString("buggy_project_dir"),
                re_rank_bug_report_file_ranks_file, re_rank_query_replacement_file_ranks_file, re_rank_query_expansion_file_ranks_file,
                re_rank_result_writer);

            final_result_writer.close();
            final_bug_report_file_ranks_writer.close();
            final_query_replacement_file_ranks_writer.close();
            final_query_expansion_file_ranks_writer.close();
            re_rank_bug_report_file_ranks_writer.close();
            re_rank_query_replacement_file_ranks_writer.close();
            re_rank_query_expansion_file_ranks_writer.close();
            re_rank_result_writer.close();

        } else if (ns.getString("operations").equals("Boosting")) {
            String corpus_path = "Screen-" + ns.getString("screen") + "/" + "Corpus-" + "All_Java_Files";
            String boosting_path = corpus_path + "/Boosting-" + ns.getString("boosting");

            file_search_and_rankings(obInfoList, stopwords, ns.getString("result"), boosting_path, "MatchedQueryFiles", 
                ns.getString("preprocessed_code_dir"), ns.getString("filtered_boosted_repo"), 
                ns.getString("buggy_project_dir"), ns.getString("prep_code_path"), 
                prep_query_path, ns.getString("prep_data_path"), ns.getString("json_file_path"), ns.getString("query_reformulation"));

            file_search_and_rankings(obInfoList, stopwords, ns.getString("result"), boosting_path, "NotMatchedQueryFiles", 
                ns.getString("preprocessed_code_dir"), ns.getString("filtered_boosted_repo"), 
                ns.getString("buggy_project_dir"), ns.getString("prep_code_path"), 
                prep_query_path, ns.getString("prep_data_path"), ns.getString("json_file_path"), ns.getString("query_reformulation"));

            String final_ranks_file = ns.getString("final_ranks_folder") + "/Rankings/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Boosting-" + ns.getString("boosting") + "#Query_Reformulation-" + ns.getString("query_reformulation") + ".csv";
            String final_bug_report_file_ranks_file = ns.getString("final_ranks_folder") + "/Files/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Boosting-" + ns.getString("boosting") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-bug-report.csv";
            String final_query_replacement_file_ranks_file = ns.getString("final_ranks_folder") + "/Files/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Boosting-" + ns.getString("boosting") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-query-replacement.csv";
            String final_query_expansion_file_ranks_file = ns.getString("final_ranks_folder") + "/Files/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Boosting-" + ns.getString("boosting") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-query-expansion.csv";
            
            String matched_file = ns.getString("filtered_boosted_filenames") + "/" + boosting_path + "/Match_Query_File_List.csv";
            String matched_ranks_folder = ns.getString("result") + "/" + boosting_path + "/QueryReformulation-" + ns.getString("query_reformulation") + "/" + "MatchedQueryFiles";
            String not_matched_ranks_folder = ns.getString("result") + "/" + boosting_path + "/QueryReformulation-" + ns.getString("query_reformulation") + "/" + "NotMatchedQueryFiles";

            CSVWriter final_result_writer = create_final_result_header(final_ranks_file);
            merge_query_matching_ranks(obInfoList, final_result_writer, matched_file, 
                matched_ranks_folder, not_matched_ranks_folder, ns.getString("operations"));

            CSVWriter final_bug_report_file_ranks_writer = create_final_file_ranks_header(final_bug_report_file_ranks_file);
            CSVWriter final_query_replacement_file_ranks_writer = create_final_file_ranks_header(final_query_replacement_file_ranks_file);
            CSVWriter final_query_expansion_file_ranks_writer = create_final_file_ranks_header(final_query_expansion_file_ranks_file);
            merge_and_update_file_ranks(obInfoList, final_bug_report_file_ranks_writer, final_query_replacement_file_ranks_writer, 
                final_query_expansion_file_ranks_writer, matched_file, 
                matched_ranks_folder, not_matched_ranks_folder, ns.getString("operations"));
            
            String re_rank_bug_report_file_ranks_file = ns.getString("final_ranks_folder") + "/FilesOnAverageRankings/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Boosting-" + ns.getString("boosting") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-re-ranked-bug-report.csv";
            String re_rank_query_replacement_file_ranks_file = ns.getString("final_ranks_folder") + "/FilesOnAverageRankings/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Boosting-" + ns.getString("boosting") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-re-ranked-query-replacement.csv";
            String re_rank_query_expansion_file_ranks_file = ns.getString("final_ranks_folder") + "/FilesOnAverageRankings/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Boosting-" + ns.getString("boosting") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-re-ranked-query-expansion.csv";
            CSVWriter re_rank_bug_report_file_ranks_writer = create_re_rank_file_ranks_header(re_rank_bug_report_file_ranks_file);
            CSVWriter re_rank_query_replacement_file_ranks_writer = create_re_rank_file_ranks_header(re_rank_query_replacement_file_ranks_file);
            CSVWriter re_rank_query_expansion_file_ranks_writer = create_re_rank_file_ranks_header(re_rank_query_expansion_file_ranks_file);

            re_rank_files_on_average_similarity(obInfoList, ns.getString("buggy_project_dir"), 
                final_bug_report_file_ranks_file, final_query_replacement_file_ranks_file, final_query_expansion_file_ranks_file,
                re_rank_bug_report_file_ranks_writer, re_rank_query_replacement_file_ranks_writer, re_rank_query_expansion_file_ranks_writer);

            String re_rank_buggy_file_rankes_file = ns.getString("final_ranks_folder") + "/AverageSimilarityRankings/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Boosting-" + ns.getString("boosting") + "#Query_Reformulation-" + ns.getString("query_reformulation") + ".csv";
            CSVWriter re_rank_result_writer = create_re_rank_result_header(re_rank_buggy_file_rankes_file);
            re_rank_files_ranking(obInfoList, ns.getString("buggy_project_dir"),
                re_rank_bug_report_file_ranks_file, re_rank_query_replacement_file_ranks_file, re_rank_query_expansion_file_ranks_file,
                re_rank_result_writer);
            
            final_result_writer.close();
            final_bug_report_file_ranks_writer.close();
            final_query_replacement_file_ranks_writer.close();
            final_query_expansion_file_ranks_writer.close();
            re_rank_bug_report_file_ranks_writer.close();
            re_rank_query_replacement_file_ranks_writer.close();
            re_rank_query_expansion_file_ranks_writer.close();
            re_rank_result_writer.close();

        } else if (ns.getString("operations").equals("Filtering+Boosting")) {
            String corpus_path = "Screen-" + ns.getString("screen") + "/" + "Corpus-" + ns.getString("filtering");
            String boosting_path = corpus_path + "/Boosting-" + ns.getString("boosting");

            file_search_and_rankings(obInfoList, stopwords, ns.getString("result"), boosting_path, "MatchedQueryFiles", 
                ns.getString("preprocessed_code_dir"), ns.getString("filtered_boosted_repo"), 
                ns.getString("buggy_project_dir"), ns.getString("prep_code_path"), 
                prep_query_path, ns.getString("prep_data_path"), ns.getString("json_file_path"), ns.getString("query_reformulation"));

            file_search_and_rankings(obInfoList, stopwords, ns.getString("result"), boosting_path, "NotMatchedQueryFiles", 
                ns.getString("preprocessed_code_dir"), ns.getString("filtered_boosted_repo"), 
                ns.getString("buggy_project_dir"), ns.getString("prep_code_path"), 
                prep_query_path, ns.getString("prep_data_path"), ns.getString("json_file_path"), ns.getString("query_reformulation"));

            String final_ranks_file = ns.getString("final_ranks_folder") + "/Rankings/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Filtering-" + ns.getString("filtering") + "#Boosting-" + ns.getString("boosting") + "#Query_Reformulation-" + ns.getString("query_reformulation") + ".csv";
            String final_bug_report_file_ranks_file = ns.getString("final_ranks_folder") + "/Files/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Filtering-" + ns.getString("filtering") + "#Boosting-" + ns.getString("boosting") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-bug-report.csv";
            String final_query_replacement_file_ranks_file = ns.getString("final_ranks_folder") + "/Files/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Filtering-" + ns.getString("filtering") + "#Boosting-" + ns.getString("boosting") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-query-replacement.csv";
            String final_query_expansion_file_ranks_file = ns.getString("final_ranks_folder") + "/Files/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Filtering-" + ns.getString("filtering") + "#Boosting-" + ns.getString("boosting") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-query-expansion.csv";
            
            String matched_file = ns.getString("filtered_boosted_filenames") + "/" + boosting_path + "/Match_Query_File_List.csv";
            String matched_ranks_folder = ns.getString("result") + "/" + boosting_path + "/QueryReformulation-" + ns.getString("query_reformulation") + "/" + "MatchedQueryFiles";
            String not_matched_ranks_folder = ns.getString("result") + "/" + boosting_path + "/QueryReformulation-" + ns.getString("query_reformulation") + "/" + "NotMatchedQueryFiles";

            CSVWriter final_result_writer = create_final_result_header(final_ranks_file);
            merge_query_matching_ranks(obInfoList, final_result_writer, matched_file, 
                matched_ranks_folder, not_matched_ranks_folder, ns.getString("operations"));

            CSVWriter final_bug_report_file_ranks_writer = create_final_file_ranks_header(final_bug_report_file_ranks_file);
            CSVWriter final_query_replacement_file_ranks_writer = create_final_file_ranks_header(final_query_replacement_file_ranks_file);
            CSVWriter final_query_expansion_file_ranks_writer = create_final_file_ranks_header(final_query_expansion_file_ranks_file);
            merge_and_update_file_ranks(obInfoList, final_bug_report_file_ranks_writer, final_query_replacement_file_ranks_writer, 
                final_query_expansion_file_ranks_writer, matched_file, 
                matched_ranks_folder, not_matched_ranks_folder, ns.getString("operations"));

            String re_rank_bug_report_file_ranks_file = ns.getString("final_ranks_folder") + "/FilesOnAverageRankings/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Filtering-" + ns.getString("filtering") + "#Boosting-" + ns.getString("boosting") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-re-ranked-bug-report.csv";
            String re_rank_query_replacement_file_ranks_file = ns.getString("final_ranks_folder") + "/FilesOnAverageRankings/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Filtering-" + ns.getString("filtering") + "#Boosting-" + ns.getString("boosting") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-re-ranked-query-replacement.csv";
            String re_rank_query_expansion_file_ranks_file = ns.getString("final_ranks_folder") + "/FilesOnAverageRankings/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Filtering-" + ns.getString("filtering") + "#Boosting-" + ns.getString("boosting") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-re-ranked-query-expansion.csv";
            CSVWriter re_rank_bug_report_file_ranks_writer = create_re_rank_file_ranks_header(re_rank_bug_report_file_ranks_file);
            CSVWriter re_rank_query_replacement_file_ranks_writer = create_re_rank_file_ranks_header(re_rank_query_replacement_file_ranks_file);
            CSVWriter re_rank_query_expansion_file_ranks_writer = create_re_rank_file_ranks_header(re_rank_query_expansion_file_ranks_file);
            
            re_rank_files_on_average_similarity(obInfoList, ns.getString("buggy_project_dir"), 
                final_bug_report_file_ranks_file, final_query_replacement_file_ranks_file, final_query_expansion_file_ranks_file,
                re_rank_bug_report_file_ranks_writer, re_rank_query_replacement_file_ranks_writer, re_rank_query_expansion_file_ranks_writer);

             String re_rank_buggy_file_rankes_file = ns.getString("final_ranks_folder") + "/AverageSimilarityRankings/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Filtering-" + ns.getString("filtering") + "#Boosting-" + ns.getString("boosting") + "#Query_Reformulation-" + ns.getString("query_reformulation") + ".csv";
            CSVWriter re_rank_result_writer = create_re_rank_result_header(re_rank_buggy_file_rankes_file);
            re_rank_files_ranking(obInfoList, ns.getString("buggy_project_dir"),
                re_rank_bug_report_file_ranks_file, re_rank_query_replacement_file_ranks_file, re_rank_query_expansion_file_ranks_file,
                re_rank_result_writer);

            final_result_writer.close();
            final_bug_report_file_ranks_writer.close();
            final_query_replacement_file_ranks_writer.close();
            final_query_expansion_file_ranks_writer.close();
            re_rank_bug_report_file_ranks_writer.close();
            re_rank_query_replacement_file_ranks_writer.close();
            re_rank_query_expansion_file_ranks_writer.close();
            re_rank_result_writer.close();
        } else if (ns.getString("operations").equals("QueryReformulation")) {
            String corpus_path = "Screen-" + ns.getString("screen") + "/" + "Corpus-" + "All_Java_Files";
            file_search_and_rankings(obInfoList, stopwords, ns.getString("result"), corpus_path, "FilteredFiles", 
                ns.getString("preprocessed_code_dir"), ns.getString("filtered_boosted_repo"), 
                ns.getString("buggy_project_dir"), ns.getString("prep_code_path"), 
                prep_query_path, ns.getString("prep_data_path"), ns.getString("json_file_path"), ns.getString("query_reformulation"));

            String final_ranks_file = ns.getString("final_ranks_folder") + "/Rankings/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Query_Reformulation-" + ns.getString("query_reformulation") + ".csv";
            String final_bug_report_file_ranks_file = ns.getString("final_ranks_folder") + "/Files/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-bug-report.csv";
            String final_query_replacement_file_ranks_file = ns.getString("final_ranks_folder") + "/Files/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-query-replacement.csv";
            String final_query_expansion_file_ranks_file = ns.getString("final_ranks_folder") + "/Files/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-query-expansion.csv";
            
            String matched_file = "";
            String matched_ranks_folder = ns.getString("result") + "/" + corpus_path + "/QueryReformulation-" + ns.getString("query_reformulation") + "/" + "FilteredFiles";
            String not_matched_ranks_folder = "";

            CSVWriter final_result_writer = create_final_result_header(final_ranks_file);
            merge_query_matching_ranks(obInfoList, final_result_writer, matched_file, 
                matched_ranks_folder, not_matched_ranks_folder, ns.getString("operations"));

            CSVWriter final_bug_report_file_ranks_writer = create_final_file_ranks_header(final_bug_report_file_ranks_file);
            CSVWriter final_query_replacement_file_ranks_writer = create_final_file_ranks_header(final_query_replacement_file_ranks_file);
            CSVWriter final_query_expansion_file_ranks_writer = create_final_file_ranks_header(final_query_expansion_file_ranks_file);
            merge_and_update_file_ranks(obInfoList, final_bug_report_file_ranks_writer, final_query_replacement_file_ranks_writer, 
                final_query_expansion_file_ranks_writer, matched_file, 
                matched_ranks_folder, not_matched_ranks_folder, ns.getString("operations"));

            String re_rank_bug_report_file_ranks_file = ns.getString("final_ranks_folder") + "/FilesOnAverageRankings/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-re-ranked-bug-report.csv";
            String re_rank_query_replacement_file_ranks_file = ns.getString("final_ranks_folder") + "/FilesOnAverageRankings/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-re-ranked-query-replacement.csv";
            String re_rank_query_expansion_file_ranks_file = ns.getString("final_ranks_folder") + "/FilesOnAverageRankings/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Query_Reformulation-" + ns.getString("query_reformulation") + "-files-re-ranked-query-expansion.csv";
            CSVWriter re_rank_bug_report_file_ranks_writer = create_re_rank_file_ranks_header(re_rank_bug_report_file_ranks_file);
            CSVWriter re_rank_query_replacement_file_ranks_writer = create_re_rank_file_ranks_header(re_rank_query_replacement_file_ranks_file);
            CSVWriter re_rank_query_expansion_file_ranks_writer = create_re_rank_file_ranks_header(re_rank_query_expansion_file_ranks_file);

            re_rank_files_on_average_similarity(obInfoList, ns.getString("buggy_project_dir"), 
            final_bug_report_file_ranks_file, final_query_replacement_file_ranks_file, final_query_expansion_file_ranks_file,
            re_rank_bug_report_file_ranks_writer, re_rank_query_replacement_file_ranks_writer, re_rank_query_expansion_file_ranks_writer);

             String re_rank_buggy_file_rankes_file = ns.getString("final_ranks_folder") + "/AverageSimilarityRankings/" + ns.getString("operations") + "#Screen-" + ns.getString("screen") + "#Query_Reformulation-" + ns.getString("query_reformulation") + ".csv";
            CSVWriter re_rank_result_writer = create_re_rank_result_header(re_rank_buggy_file_rankes_file);
            re_rank_files_ranking(obInfoList, ns.getString("buggy_project_dir"),
                re_rank_bug_report_file_ranks_file, re_rank_query_replacement_file_ranks_file, re_rank_query_expansion_file_ranks_file,
                re_rank_result_writer);

            final_result_writer.close();
            final_bug_report_file_ranks_writer.close();
            final_query_replacement_file_ranks_writer.close();
            final_query_expansion_file_ranks_writer.close();
            re_rank_bug_report_file_ranks_writer.close();
            re_rank_query_replacement_file_ranks_writer.close();
            re_rank_query_expansion_file_ranks_writer.close();
            re_rank_result_writer.close();
        }
        long end_time = System.currentTimeMillis();

        NumberFormat formatter = new DecimalFormat("#0.00000");
        System.out.println("Execution time is " + formatter.format((end_time - start_time) / 1000d) + " seconds");

    }
}
