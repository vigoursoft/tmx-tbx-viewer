package wasabi;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.websocket.Session;

@WebServlet(value = "/TmxServlet", loadOnStartup = 0)
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, // 10 MB
        maxFileSize = 1024 * 1024 * 50, // 50 MB
        maxRequestSize = 1024 * 1024 * 100)   	// 100 MB

public class TmxServlet
        extends HttpServlet {

    private static final long serialVersionUID = 2L;

    /**
     * Directory where uploaded files will be saved, its relative to the web
     * application directory.
     */
    private static final String UPLOAD_DIR = "uploads";
    private Object Collectors;
    private boolean Ex = false;
    private TmxTable reader;
    private TbxTable tbreader;
    private boolean edit = false;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = getServletContext();

        edit = Boolean.parseBoolean(context.getInitParameter("edit"));
        System.out.println("????????" + context.getInitParameter("edit") + "????" + edit);

    }

    protected void doGet(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String vtmxPara = request.getParameter("vtmx");
        String vtbxPara = request.getParameter("vtbx");
        String save = request.getParameter("save");
        String find = request.getParameter("find");
        
     

        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        //plain loading of table 
        if (vtmxPara != null && (save==null && find==null) ) {
            PrintWriter out = response.getWriter();

            String applicationPath = System.getProperty("user.home");
            String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR
                    + File.separator + vtmxPara;

            // out.println("your requested " + vtmxPara);
            String uri = request.getRequestURL()
                    + "?vtmx=" + vtmxPara;
            uri = "<a href='" + uri + "'>" + vtmxPara + "</a></br>";
            this.reader = new TmxTable();
            reader.read(out, new File(uploadFilePath), edit, uri);

            out.write(reader.errorMsg);
            out.flush();
            out.close();
        } else  if (vtbxPara != null && (save==null && find==null) ) {
            PrintWriter out = response.getWriter();

            String applicationPath = System.getProperty("user.home");
            String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR
                    + File.separator + vtbxPara;

            // out.println("your requested " + vtmxPara);
            String uri = request.getRequestURL()
                    + "?vtbx=" + vtbxPara;
            uri = "<a href='" + uri + "'>" + vtbxPara + "</a></br>";
            this.tbreader = new TbxTable();
            tbreader.read(out, new File(uploadFilePath), edit, uri);

            out.write(reader.errorMsg);
            out.flush();
            out.close();
        } 
      //if null // next is saving a change
        else if (request.getParameter("save") != null) {
            save = request.getParameter("save");
            int row = Integer.valueOf(request.getParameter("row"));
            int cell = Integer.valueOf(request.getParameter("cell"));

            reader.changeElement(save, row, cell);
            // do we have a search request?
        } else if (request.getParameter("find") != null) {
            PrintWriter out = response.getWriter();
            
            if (vtmxPara != null)
               reader.search(out, request.getParameter("find"), edit);
            else if (vtbxPara != null)
               tbreader.search(out, request.getParameter("find"), edit);
            out.flush();
            out.close();
        }

    }

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // right off then bat, set the encoding to utf 8 so we get interlantional filenmes
        // you may want to do this in the server.xml of tomcat URIEncoding="UTF-8"
        request.setCharacterEncoding("UTF-8");

        // get or make a sssion for the filenames
        HttpSession session = request.getSession(true);
        session.setAttribute("test", "am I a session");
        ArrayList filesList = new ArrayList();

        // constructs path of the directory to save uploaded file
        String applicationPath = System.getProperty("user.home");
        String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;
        // String vtmxPara = request.getParameter("vtmx");

        // creates the save directory if it does not exists
        File fileSaveDir = new File(uploadFilePath);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdirs();
        }
        System.out.println("Upload File Directory=" + fileSaveDir.getAbsolutePath());

        //Get all the parts from request and write it to the file on server
        try {
            Collection<Part> fileParts = request.getParts();
            int s = fileParts.size();

            for (Part filePart : fileParts) {
                String fileName = filePart.getSubmittedFileName();
                if (fileName != null) {
                    filesList.add(fileName); // we could also just add the Collection
                    filePart.write(uploadFilePath + File.separator + fileName);
                }
               
            }

        } catch (Exception e) {
            Ex = true;
        }

        String msgtext = new String();
        File l_Directory = new File(uploadFilePath);
        File[] l_files = l_Directory.listFiles();

        for (int c = 0; c < l_files.length; c++) {
            if (l_files[c].isDirectory()) {
                //
            } else if (l_files[c].getName().endsWith(".tmx")) {
                String uri = request.getRequestURL()
                        + "?vtmx=" + l_files[c].getName();
                msgtext = msgtext + "<a href='" + uri + "'>" + l_files[c].getName() + "</a></br>";

            }
            else if (l_files[c].getName().endsWith(".tbx")) {
                String uri = request.getRequestURL()
                        + "?vtbx=" + l_files[c].getName();
                msgtext = msgtext + "<a href='" + uri + "'>" + l_files[c].getName() + "</a></br>";

            }
        }

        if (!Ex) {
            request.setAttribute("message", msgtext);
            //  request.setAttribute("errors", errorMsg);
        } else {
            request.setAttribute("message", "An upload error occurred");
        }

        Ex = false;
        getServletContext().getRequestDispatcher("/response.jsp").forward(
                request, response);
    }

    /**
     * Utility method to get file name from HTTP header content-disposition
     */
    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        System.out.println("content-disposition header= " + contentDisp);
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "";
    }
}
