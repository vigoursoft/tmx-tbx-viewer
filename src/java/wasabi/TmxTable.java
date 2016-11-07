
/*
 * tmx segment reader
 */
package wasabi;

/**
 *
 * @author dummy
 */
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletOutputStream;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaders;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class TmxTable {

    public String errorMsg = new String();
    private Document document;
    private List<Element> tuList;
    private int columnCount = 0;

    /**
     * @param out
     * @param inputFile
     * @param args the command line arguments
     */
    public void read(PrintWriter out, File inputFile, boolean edit, String uri) {
        try {

            SAXBuilder saxBuilder = new SAXBuilder((XMLReaderJDOMFactory) null);

            document = saxBuilder.build(inputFile);
            System.out.println("Root element :"
                    + document.getRootElement().getName());

            Element classElement = document.getRootElement();

            //tus are children of the body element
            tuList = classElement.getChild("body").getChildren("tu");
            // out.println(inputFile.getName() + "----------------------------");
            out.println("<xhtml><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>"
                    + "<link rel=\"stylesheet\" type=\"text/css\" href=\"tmxcss.css\">"
                    + "<script src=\"tmxscript.js\" type=\"text/javascript\" charset=\"utf-8\"></script>"
                    + "<script>"
                    + "function findMyText(needle){\n"
                    + " "
                    + "if (needle.length ===0) return;"
                    + "var ref=window.location.href;\n"
                    + " ref=ref+\"&find=\"+needle; \n"
                    + " //alert(ref);"
                    + " loadFragmentInToElement(ref, 'content');\n"
                    + "}\n"
                    + "function loadFragmentInToElement(fragment_url, element_id) {\n"
                    + "   "
                    + "    var element = document.getElementById(element_id);\n"
                    + "    element.innerHTML = '<p><em>Loading ...</em></p>';\n"
                    + "    var xhttp = new XMLHttpRequest(); xhttp.open(\"GET\", fragment_url,false);\n"
                    + "    xhttp.send();\n"
                    + "document.getElementById(\"content\").innerHTML = xhttp.responseText;"
                    + "}</script>"
                    + "</head> "
                    + ""
                    + "<body><div id='header'><input width='400' id=\"needle\" name=\"needle\" type=\"text\">"
                    + "<input style=\"height:40px;width:50px\" type=\"button\" value=\"Find\" onclick=\"findMyText(document.getElementById('needle').value)\">"
                    + " <b>Current tmx file: </b> " + uri
                    + "</div> <div id='bod'>");

            // get the first set of tuvs so we get the headers and put them into table
            // headers.
            // Define needed variables first.
            Element tuElement;
            List<Element> tuvs;
            Element tuvElement;
            Attribute attribute;
            Element segment;
            tuvs = tuList.get(0).getChildren("tuv");
            // start a table
            out.println("<table id='content'><tr><th class='ct'>" + "#" + "</th>");
            for (int tuvcount = 0; tuvcount < tuvs.size(); tuvcount++) {
                tuvElement = tuvs.get(tuvcount);
                attribute = tuvElement.getAttribute("lang", Namespace.XML_NAMESPACE);
                out.println("<th>" + attribute.getValue() + "</th>");
            }
            //end table header row
            out.println("</tr>");
            for (int temp = 0; temp < tuList.size(); temp++) {
                tuElement = tuList.get(temp);
                //  out.println("current name"
                //    + tuElement.getName());
                // attribute =  tuElement.getAttribute("creationdate");
                //  out.println("create date : "
                //    + attribute.getValue() );

                //get the liist of tuvs
                tuvs = tuElement.getChildren("tuv");
                out.println("<tr><td class='ct'>" + (temp + 1) + "</td>");
                for (int tuvcount = 0; tuvcount < tuvs.size(); tuvcount++) {
                    tuvElement = tuvs.get(tuvcount);
                    //  attribute=tuvElement.getAttribute("lang", Namespace.XML_NAMESPACE);
                    //    out.println( //attribute.getValue()+
                    //  out.println("<td>" + tuvElement.getChildText("seg") + "</td>"  );
                    XMLOutputter outp = new XMLOutputter();
                    segment = tuvElement.getChild("seg");

                    outp.setFormat(Format.getCompactFormat());
                    //outp.setFormat(Format.getRawFormat());
                    //outp.setFormat(Format.getPrettyFormat());
                    //outp.getFormat().setTextMode(Format.TextMode.PRESERVE);

                    StringWriter sw = new StringWriter();
                    outp.output(segment.getContent(), sw);
                    StringBuffer sb = sw.getBuffer();
                    if (edit) {
                        out.println("<td onclick='tdclick(this)'>" + sb + "</td>");
                    } else {
                        out.println("</td>" + "<td>" + sb + "</td>");
                    }

                }
                out.println("</tr>");

            }
            out.println("</table></div></body></xhtml>");

        } catch (JDOMException ex) {
            Logger.getLogger(TmxTable.class.getName()).log(Level.SEVERE, null, ex);
            errorMsg = errorMsg + ex.getMessage();
        } catch (IOException ex) {
            Logger.getLogger(TmxTable.class.getName()).log(Level.SEVERE, null, ex);
            errorMsg = errorMsg + ex.getMessage();
        }

    }

    public void changeElement(String str, int row, int col) {
        // subtract 
        --row;
        --col;
        Element tuElement;
        List<Element> tuvs;
        Element tuvElement;
        Attribute attribute;
        Element segment;
        try {
            for (int temp = 0; temp < tuList.size(); temp++) {
                tuElement = tuList.get(temp);

                //get the list of tuvs
                tuvs = tuElement.getChildren("tuv");
                System.out.println("<tr>");
                if (temp < row) {
                    continue;
                }
                if (temp > row) {
                    break;
                }
                for (int tuvcount = 0; tuvcount < tuvs.size(); tuvcount++) {
                    tuvElement = tuvs.get(tuvcount);

                    if (tuvcount < col) {
                        continue;
                    }
                    if (tuvcount > col) {
                        break;
                    }

                    segment = tuvElement.getChild("seg");

                    Text tx = new Text(str);
                    segment.setContent(tx);

                    String applicationPath = System.getProperty("user.home");
                    String uploadFilePath = applicationPath + File.separator + "uploads"
                            + File.separator + "output.tmx";

                    try (FileOutputStream fos = new FileOutputStream(uploadFilePath)) {
                        Format format = Format.getPrettyFormat();
                        format.setEncoding("UTF-8");
                        XMLOutputter outputter = new XMLOutputter(format);
                        outputter.output(document, fos);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                System.out.println("</tr>");

            }
        } catch (Exception ex) {
            Logger.getLogger(TmxTable.class.getName()).log(Level.SEVERE, null, ex);
            errorMsg = errorMsg + ex.getMessage();

        }

    }

    public void search(PrintWriter out, String search, boolean edit) {
        try {
            // get the first set of tuvs so we get the headers and put them into table
            // headers.
            // Define needed variables first.
            Element tuElement;
            List<Element> tuvs;
            Element tuvElement;
            Attribute attribute;
            Element segment;
            boolean searchHit = false;
            tuvs = tuList.get(0).getChildren("tuv");
            // start a table
            out.println("<table id='content'><tr><th class='ct'>" + "#" + "<th>");
            for (int tuvcount = 0; tuvcount < tuvs.size(); tuvcount++) {
                tuvElement = tuvs.get(tuvcount);
                attribute = tuvElement.getAttribute("lang", Namespace.XML_NAMESPACE);
                out.println("<th>" + attribute.getValue() + "</th>");
            }
            //end table header row
            out.println("</tr>");
            for (int temp = 0; temp < tuList.size(); temp++) {
                tuElement = tuList.get(temp);
                //  out.println("current name"
                //    + tuElement.getName());
                // attribute =  tuElement.getAttribute("creationdate");
                //  out.println("create date : "
                //    + attribute.getValue() );

                //get the liist of tuvs
                tuvs = tuElement.getChildren("tuv");
                // we need to get the whole tr and decide later if we can ptint it
                StringBuilder oneRow = new StringBuilder();
                searchHit = false;

                // out.println("<tr>");
                oneRow.append("<tr><td class='ct'>").append(temp + 1).append("</td>");
                for (int tuvcount = 0; tuvcount < tuvs.size(); tuvcount++) {
                    tuvElement = tuvs.get(tuvcount);
                    //  attribute=tuvElement.getAttribute("lang", Namespace.XML_NAMESPACE);
                    //    out.println( //attribute.getValue()+
                    //  out.println("<td>" + tuvElement.getChildText("seg") + "</td>"  );
                    XMLOutputter outp = new XMLOutputter();
                    segment = tuvElement.getChild("seg");

                    outp.setFormat(Format.getCompactFormat());

                    StringWriter sw = new StringWriter();
                    outp.output(segment.getContent(), sw);
                    StringBuffer sb = sw.getBuffer();
                    if (sb.indexOf(search) > -1) {
                        searchHit = true;
                    }

                    if (edit) {
                        oneRow.append("<td>").append("<td onclick='tdclick(this)'>").
                                append(sb).append("</td>");
                    } else {
                        oneRow.append("<td>").append("<td onclick='tdclick(this)'>").
                                append("</td>");
                    }

                }
                oneRow.append("</tr>");

                if (searchHit) {
                    out.println(oneRow);
                }

            }
            out.println("</table></body></xhtml>");

        } catch (IOException ex) {
            Logger.getLogger(TmxTable.class.getName()).log(Level.SEVERE, null, ex);
            errorMsg = errorMsg + ex.getMessage();
        }

    }

}
