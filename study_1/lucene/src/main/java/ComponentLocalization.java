import edu.wayne.cs.severe.ir4se.processor.controllers.impl.RAMRetrievalIndexer;
import edu.wayne.cs.severe.ir4se.processor.controllers.impl.lucene.LuceneRetrievalSearcher;
import edu.wayne.cs.severe.ir4se.processor.entity.RelJudgment;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.store.Directory;
import seers.textanalyzer.PreprocessingOptionsParser;
import seers.textanalyzer.TextProcessor;
import seers.textanalyzer.entity.Sentence;
import seers.textanalyzer.entity.Token;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.opencsv.CSVWriter;
import edu.wayne.cs.severe.ir4se.processor.exception.EvaluationException;
import org.apache.commons.io.comparator.NameFileComparator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;

import org.apache.commons.io.FileUtils;


public class ComponentLocalization {
    public static int qId = 0;
    public static int resultForEachQueryCounterScreenRetrieval = 0;
    public static int resultForEachQueryCounterComponentRetrieval = 0;
    public static int noOfFailedCases = 0;


    // Function to sort hashmap by values
    public static HashMap<String, Float> sortByValue(HashMap<String, Float> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Float>> list =
                new LinkedList<>(hm.entrySet());

        // Sort the list
        Collections.sort(list, (o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));

        // put data from sorted list to hashmap
        HashMap<String, Float> temp = new LinkedHashMap<>();
        for (Map.Entry<String, Float> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }


    public static JSONObject readJson(String path) {
        JSONParser jsonParser = new JSONParser();
        JSONObject object = null;

        try (FileReader reader = new FileReader(path)) {
            String obj = jsonParser.parse(reader).toString();
            object = new JSONObject(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }


    public static Iterator<String> sortJSONObjectKeys(JSONObject object) {
        SortedSet<String> set = new TreeSet<>();

        String[] keyArray = object.keySet().toArray(new String[0]);
        Collections.addAll(set, keyArray);
        return set.iterator();
    }

    public static List<Document> createComponentDocuments(JSONObject object, String screenId) {
        List<Document> componentDocuments = new ArrayList<>();

        Iterator<String> keys = sortJSONObjectKeys(object);

        // Iterate through the keys
        while (keys.hasNext()) {
            String key = keys.next();
            // If the key is a particular screenId
            if (key.equals(screenId)) {
                JSONArray jsonArray = object.getJSONArray(key);
                // Iterate through the components of the screen
                for (int i = 0; i < jsonArray.length(); i++) {
                    String document;
                    String resourceId = "";
                    String text_content = "";
                    String componentType = "";
                    JSONObject componentObject = (JSONObject) jsonArray.get(i);

                    if (componentObject.has("resource_id") && componentObject.get("resource_id") != null) {
                        resourceId = (String) componentObject.get("resource_id");
                    }
                    if (componentObject.has("text_content")) {
                        text_content = (String) componentObject.get("text_content");
                    }
                    if (componentObject.has("type")) {
                        componentType = (String) componentObject.get("type");
                    }
                    document = resourceId + " " + text_content + " " + componentType;

                    componentDocuments.add(new Document((Integer) componentObject.get("component_id"), document, componentObject.get("component_id").toString()));
                }
            }
        }
        return componentDocuments;
    }

    private static String preprocessText(String text, List<String> stopWords) {

        String[] preprocessingOptions = {PreprocessingOptionsParser.CAMEL_CASE_SPLITTING,
                PreprocessingOptionsParser.NUMBERS_REMOVAL, PreprocessingOptionsParser.PUNCTUATION_REMOVAL,
                PreprocessingOptionsParser.SHORT_TOKENS_REMOVAL + " 3",
                PreprocessingOptionsParser.SPECIAL_CHARS_REMOVAL};

        if (StringUtils.isBlank(text)) return null;
        List<Sentence> prepSentences = TextProcessor.preprocessText(text, stopWords, preprocessingOptions);
        List<Token> allTokens = TextProcessor.getAllTokens(prepSentences);
        return allTokens.stream().map(Token::getLemma).collect(Collectors.joining(" "));
    }


    public static Map<Query, List<Double>> searchEngine(List<Document> documentList, List<OBQuery> obQueries, boolean isScreenCorpus, String resultsPath) throws EvaluationException, IOException {
        // Read stop words
        String stopWordsPath = "src/main/resources/stop-words-bugs.txt";
        List<String> stopWords = TextProcessor.readStopWords(stopWordsPath);

        // Preprocess files
        List<String> preprocessedDocuments = new ArrayList<>();
        for (Document document : documentList) {
            preprocessedDocuments.add(preprocessText(document.docText, stopWords));
            //preprocessedDocuments.add(document.docText);
        }

        // Build corpus
        List<RetrievalDoc> corpus = IntStream.range(0, preprocessedDocuments.size()).mapToObj(i -> {
                    String docText = preprocessedDocuments.get(i);
                    if (StringUtils.isBlank(docText)) return null;
                    int docId = documentList.get(i).docId;
                    //System.out.println(docText);
                    String docName = documentList.get(i).docName;
                    return new RetrievalDoc(docId, docText, docName);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Create evaluator for computing effectiveness metrics
        MyRetrievalEvaluator myEvaluator = new MyRetrievalEvaluator();

        // This map will store the evaluation metric values for all the queries
        Map<Query, List<Double>> queriesOfOneScreenResults = new HashMap<>();

        // This list will store the results for each query
        List<String[]> resultsOfEachQuery = new ArrayList<>();

        //-------------------------------------
        // Build query and search for each query
        Query query;
        for (OBQuery obQuery : obQueries) {
            String preprocessedOB = preprocessText(obQuery.queryText, stopWords);
            //String preprocessedOB = obQuery.queryText;
            //int queryId = Integer.parseInt(String.valueOf(obQuery.screenId).concat(String.valueOf(obQuery.queryId)));
            int queryId = qId;
            qId++;
            int canRetrieveAnyDocument = 0;
            //System.out.println("Query Id: "+queryId);

            query = new Query(queryId, preprocessedOB);

            // Expected results for the query
            List<String> keyOfExpectedSearchResult;

            // Set the expected results for the query
            if (isScreenCorpus) {
                keyOfExpectedSearchResult = obQuery.screenGroundTruth;
            } else {
                keyOfExpectedSearchResult = obQuery.componentGroundTruth;
            }

            List<Double> evalResults = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));

            // Index the corpus and store it in RAM
            // Use the class DefaultRetrievalIndexer to store the index in disk
            try (Directory index = new RAMRetrievalIndexer().buildIndex(corpus, null)) {
                // The searcher
                LuceneRetrievalSearcher searcher = new LuceneRetrievalSearcher(index, null);
                List<RetrievalDoc> searchResults = null;
                if (query.getTxt().length() == 0) {
                    System.out.println("Query is empty");
                    noOfFailedCases++;
                    resultsOfEachQuery.add(new String[]{String.valueOf(obQuery.bugId), String.valueOf(obQuery.queryId), obQuery.queryText, String.valueOf(obQuery.obInTitle), obQuery.bugType, obQuery.obCategory, String.valueOf(obQuery.obRating), obQuery.queryText, keyOfExpectedSearchResult.toString(), "{}", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"});
                    queriesOfOneScreenResults.put(query, evalResults);
                    continue;
                } else {
                    // Search
                    searchResults = searcher.searchQuery(query);
                }

                // Search
                //List<RetrievalDoc> searchResults = searcher.searchQuery(query);

                // Get results from the corpus so that we can print their content
                //List<RetrievalDoc> resultsFromCorpus =
                //        corpus.stream().filter(searchResults::contains).collect(Collectors.toList());

//                resultsFromCorpus.forEach(d -> {
//                    System.out.print("Search result = ");
//                    System.out.print(d.getDocId());
//                    System.out.print(": ");
//                    System.out.println(d.getDocText());
//                });
                //System.out.println("Query ID:"+ query.getQueryId() + "Result:" + resultsFromCorpus.get(0).getDocId());


                RelJudgment expectedSearchResults = new RelJudgment();


                // Get the expected results from the corpus
                List<RetrievalDoc> docs =
                        corpus.stream().filter(doc -> keyOfExpectedSearchResult.contains(doc.getDocName()))
                                .collect(Collectors.toList());

                //System.out.println("Expected Results: " + docs);
                // Set the expected results for the query
                expectedSearchResults.setRelevantDocs(docs);


                HashMap<String, Float> scores = new HashMap<>();
                HashMap<String, Float> sortedScores = new HashMap<>();

                for (RetrievalDoc doc : searchResults) {
                    scores.put(doc.getDocName(), doc.getDocScore());
                }
                //System.out.println("Scores: " + scores.toString());
                sortedScores = sortByValue(scores);

                // Evaluate the query
                //List<Double> evalResults = evaluator.evaluateRelJudgment(expectedSearchResults, searchResults);

//                List<String> rankedDocuments = new ArrayList<>();
//                for (RetrievalDoc doc : searchResults) {
//                    rankedDocuments.add(doc.getDocName());
//                }
                //System.out.println("Ranked Documents: " + rankedDocuments);

                if (sortedScores.size() > 0) {
                    canRetrieveAnyDocument = 1;
                } else {
                    noOfFailedCases++;
                }

                //System.out.println(searchResults);
                evalResults = myEvaluator.evaluateRelJudgment(expectedSearchResults, searchResults);

                //System.out.println("Query ID: " + query.getQueryId() + " " + evalResults);

                // Add the results for the query to the list
                resultsOfEachQuery.add(new String[]{String.valueOf(obQuery.bugId), String.valueOf(obQuery.queryId), obQuery.queryText, String.valueOf(obQuery.obInTitle), obQuery.bugType, obQuery.obCategory, String.valueOf(obQuery.obRating), obQuery.queryText, keyOfExpectedSearchResult.toString(), sortedScores.toString(), String.valueOf(evalResults.get(0)), String.valueOf(evalResults.get(1)), String.valueOf(evalResults.get(7)), String.valueOf(evalResults.get(8)), String.valueOf(evalResults.get(9)), String.valueOf(evalResults.get(10)), String.valueOf(evalResults.get(11)), String.valueOf(evalResults.get(12)), String.valueOf(evalResults.get(13)), String.valueOf(evalResults.get(14)), String.valueOf(evalResults.get(15)), String.valueOf(evalResults.get(16)), String.valueOf(evalResults.get(17)), String.valueOf(canRetrieveAnyDocument)});

                //System.out.println(resultsOfEachQuery.get(0)[0]);
                // INDEX 0: rank of the first relevant and retrieved doc
                // INDEX 1: reciprocal rank
                // INDEX 2: # of true positives
                // INDEX 3: precision
                // INDEX 4: # of false negatives
                // INDEX 5: recall
                // INDEX 6: f1 score
                // INDEX 7: average precision
                // INDEX 8: any relevant doc in top 1?
                // INDEX 9: any relevant doc in top 2?
                // INDEX 10:any relevant doc in top 3?
                // INDEX 11:any relevant doc in top 4?
                // INDEX 12:any relevant doc in top 5?
                // INDEX 13:any relevant doc in top 6?
                // INDEX 14:any relevant doc in top 7?
                // INDEX 15:any relevant doc in top 8?
                // INDEX 16:any relevant doc in top 9?
                // INDEX 17:any relevant doc in top 10?

                // --------------------------------------------------
                // Add the metrics to the map
                queriesOfOneScreenResults.put(query, evalResults);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Sort the results by the first element of the array
        Comparator<String[]> byFirstElement = Comparator.comparingInt((String[] array) -> Integer.parseInt(array[1]));
        // Sort the results by the first element of the array
        List<String[]> resultForEachQueryList = resultsOfEachQuery.stream().sorted(byFirstElement).collect(Collectors.toList());
        // Write the results of each query to a file
        writeResultsOfEachQuery(resultsPath, resultForEachQueryList);
        if (isScreenCorpus)
            resultForEachQueryCounterScreenRetrieval += resultForEachQueryList.size();
        else
            resultForEachQueryCounterComponentRetrieval += resultForEachQueryList.size();
        return queriesOfOneScreenResults;
    }


    public static void writeResultsOfEachQuery(String filePath, List<String[]> results) {
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                FileWriter outputFile = new FileWriter(file);

                CSVWriter writer = new CSVWriter(outputFile, ';',
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);

                writer.writeNext(new String[]{"Bug-ID", "OB-ID", "OB-Text", "OB-in-Title?", "Bug-Type", "OB-Category", "OB-Rating", "OB-Text", "Ground-Truth", "Ranked-Document", "First-Rank", "Reciprocal-Rank", "Average-Precision", "h@1", "h@2", "h@3", "h@4", "h@5", "h@6", "h@7", "h@8", "h@9", "h@10", "Can-Retrieve-Any-Document?"});
                writer.close();
            }

            FileWriter outputFile = new FileWriter(filePath, true);

            CSVWriter writer = new CSVWriter(outputFile, ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            writer.writeAll(results);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeOverallResults(String filePath, String[] overallResults) {
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                FileWriter outputFile = new FileWriter(filePath);

                CSVWriter writer = new CSVWriter(outputFile, ';',
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);

                writer.writeNext(new String[]{"# of Queries", "MRR", "MAP", "h@1", "h@2", "h@3", "h@4", "h@5", "h@6", "h@7", "h@8", "h@9", "h@10", "# of Failed Cases"});
                writer.close();
            }
            FileWriter outputFile = new FileWriter(filePath, true);

            CSVWriter writer = new CSVWriter(outputFile, ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            writer.writeNext(overallResults);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
    }


    public static void main(String[] args) throws EvaluationException, IOException {
        // Screen components folder path for creating the corpus
        String screenComponentsFolderPath = "../real_data_construction/real_data/screen_components/";
        // OB file path for creating the query
        String obFilePath = "../real_data_construction/real_data/ob/obs.json";

        // Result file paths for saving the results
        String componentRetrievalResultsOfAllObPath = "../results/CL/LUCENE_results.csv";
        String componentRetrievalResultsOfAllObWithDetailsPath = "../results/CL/LUCENE_results_with_details.csv";

        // Delete the existing files
        deleteFile(componentRetrievalResultsOfAllObPath);
        deleteFile(componentRetrievalResultsOfAllObWithDetailsPath);

        // Read the queries
        JSONObject obFile = readJson(obFilePath);
        //System.out.println(obFile);

        // Sort the keys of the JSON object
        Iterator<String> bugKeys = sortJSONObjectKeys(obFile);
        //System.out.println(bugKeys.next());

        MyRetrievalEvaluator myEvaluator = new MyRetrievalEvaluator();

        // These map will contain the results of all queries for component retrieval
        Map<Query, List<Double>> componentRetrievalResultsAllQueries = new HashMap<>();

        String[] componentRetrievalResultsForAllQueries;

        while (bugKeys.hasNext()) {
            String bugID = bugKeys.next();
            System.out.println(bugID);
            if (obFile.get(bugID) instanceof JSONObject) {
                String bugScreenComponentFilePath = screenComponentsFolderPath + bugID + ".json";
                // Get the screen components of the app
                JSONObject screenComponents = readJson(bugScreenComponentFilePath);

                // These map will contain the results of each query for component retrieval
                Map<Query, List<Double>> componentRetrievalResultOfOneQuery = new HashMap<>();

                //System.out.println(obFile.get(bugID));
                JSONObject allOBsInBug = (JSONObject) obFile.get(bugID);

                //System.out.println(allOBsInBug);
                //System.out.println(allOBsInBug.keys());
                for (Iterator<String> obKeys = allOBsInBug.keys(); obKeys.hasNext(); ) {


                    String obID = obKeys.next();
                    JSONObject obDetails = (JSONObject) allOBsInBug.get(obID);

                    JSONArray screens = (JSONArray) obDetails.get("screens");
                    if (screens.length() == 0) {
                        continue;
                    }
                    ArrayList<String> screenGroundTruth = new ArrayList<>();

                    for (int i = 0; i < screens.length(); i++) {
                        ArrayList<String> componentGroundTruth = new ArrayList<>();
                        List<OBQuery> obs = new ArrayList<>();
                        String jsonArray = screens.get(i).toString();
                        JSONObject innerDict = new JSONObject(jsonArray);
                        screenGroundTruth.add((String) innerDict.get("screen_id"));
                        // If the screen does not have any components, then continue
                        if (innerDict.getJSONArray("components").length() == 0) {
                            continue;
                        }
                        for (int j = 0; j < innerDict.getJSONArray("components").length(); j++) {
                            componentGroundTruth.add(innerDict.getJSONArray("components").get(j).toString());
                        }
                        //System.out.println(componentGroundTruth);

                        obs.add(new OBQuery(bugID, Integer.parseInt(obID), (Integer) obDetails.get("ob_in_title"), obDetails.get("ob_text").toString(), obDetails.get("bug_type").toString(), obDetails.get("ob_category").toString(), (Integer) obDetails.get("ob_rating"), screenGroundTruth, componentGroundTruth));

                        String screenId = (String) innerDict.get("screen_id");
                        List<Document> componentDocuments = createComponentDocuments(screenComponents, screenId);

                        // Perform the component retrieval
                        if (componentDocuments.size() >= 1) {
                            componentRetrievalResultOfOneQuery = searchEngine(componentDocuments, obs, false, componentRetrievalResultsOfAllObWithDetailsPath);
                        }
                        componentRetrievalResultsAllQueries.putAll(componentRetrievalResultOfOneQuery);
                    }
                }
                System.out.println("-------------------------------------------------");
            }
        }

        RetrievalStats screenRetrivalEvalResults = myEvaluator.evaluateModel(componentRetrievalResultsAllQueries);
        //System.out.println("Overall evaluation results of Screen Retrival= " + screenRetrivalEvalResults.getAmountOfQueries() + "\t" + screenRetrivalEvalResults.getMeanRecipRank());
        componentRetrievalResultsForAllQueries = new String[]{String.valueOf(screenRetrivalEvalResults.getAmountOfQueries()), String.valueOf(screenRetrivalEvalResults.getMeanRecipRank()), String.valueOf(screenRetrivalEvalResults.getMeanAvgPrecision()), String.valueOf(screenRetrivalEvalResults.getPercentageTop1s()), String.valueOf(screenRetrivalEvalResults.getPercentageTop2s()), String.valueOf(screenRetrivalEvalResults.getPercentageTop3s()), String.valueOf(screenRetrivalEvalResults.getPercentageTop4s()), String.valueOf(screenRetrivalEvalResults.getPercentageTop5s()), String.valueOf(screenRetrivalEvalResults.getPercentageTop6s()), String.valueOf(screenRetrivalEvalResults.getPercentageTop7s()), String.valueOf(screenRetrivalEvalResults.getPercentageTop8s()), String.valueOf(screenRetrivalEvalResults.getPercentageTop9s()), String.valueOf(screenRetrivalEvalResults.getPercentageTop10s()), String.valueOf(noOfFailedCases)};
        writeOverallResults(componentRetrievalResultsOfAllObPath, componentRetrievalResultsForAllQueries);
    }
}