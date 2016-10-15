package model;

import java.io.IOException;
import java.util.List;

/**
 * Created by Yuzer on 31.07.2016.
 */
public interface FileIOHandler {

    List<String> getIDList() throws IOException;

    List<CourtCase> readCurrentListOfCases();

    void writeAllTheInfo(List<CourtCase> listOfRows) throws IOException;

}