import java.util.ArrayList;

public class OBQuery {
    String bugId;
    int queryId;
    int obInTitle;
    String queryText;
    String bugType;
    String obCategory;
    int obRating;
    ArrayList<String> screenGroundTruth;
    ArrayList<String> componentGroundTruth;

    OBQuery(String bId, int qId, int obT, String text, String bType, String onCat, int rating, ArrayList<String> sGt, ArrayList<String> cGt) {
        bugId = bId;
        queryId = qId;
        obInTitle = obT;
        queryText = text;
        bugType = bType;
        obCategory = onCat;
        obRating = rating;
        screenGroundTruth = sGt;
        componentGroundTruth = cGt;
    }
}
