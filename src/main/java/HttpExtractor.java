
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpExtractor implements Extractor {

    public CourtCase getCase(String caseNumber) {
        //определить, какой суд по номеру дела - получить url, referer, court_id.
        //используя вышеуканные данные получить список дел по указанному суду
        //выбрать дело по соответствию номера, вернуть ссылку в вызывающий меод
        Court court = getCourtForRequest(caseNumber);
        List<CourtCase> caseList = null;
        try {
            caseList = getCourtCases(court.getUrl(), court.getReferer(), court.getCourtId());
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (CourtCase c : caseList) {
            if (c.getNumber().equals(caseNumber)) {
                return c;
            }
        }

        return null;
    }


    /*
    This method makes a http POST-Request to the URL with headers and request body
    url, referer-header and courtId are parameters used in order to make a correct request
    returns a list of court cases fetched from server
    */
    private List<CourtCase> getCourtCases(String url, String referer, String courtId) throws IOException {
        HttpResponse<JsonNode> jsonResponse = null;
        try {
            jsonResponse = Unirest.post("http://pm.od.court.gov.ua/new.php")
                    .header("Host", "pm.od.court.gov.ua")
                    .header("Accept", "application/json, text/javascript, */*; q=0.01")
                    .header("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("Referer", "http://pm.od.court.gov.ua/sud1522/csz/")
                    .body("q_court_id=1522")
                    .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        JSONArray jsonArray = jsonResponse.getBody().getArray();

        List<CourtCase> caseList = new ArrayList<CourtCase>();

        for (int i = 0; i < jsonArray.length(); i++) {
            caseList.add(parseCourtCaseFromJson(new JsonNode(jsonArray.get(i).toString()).getObject()));
        }

        System.out.println(caseList.get(7).toString());

        return caseList;
    }

    // convert json-object to court case
    private static CourtCase parseCourtCaseFromJson(JSONObject jsonCase) {
        return new CourtCase(
                jsonCase.getString("date"),
                jsonCase.getString("number"),
                jsonCase.getString("involved"),
                jsonCase.getString("description"),
                jsonCase.getString("judge"),
                jsonCase.getString("forma"),
                jsonCase.getString("add_address")
        );
    }

    // find out the information about court which is needed for making correct http-request.
    // returns Court that consist url, referer, court_id and others
    private Court getCourtForRequest(String caseNumber) {
        return null;
    }
}