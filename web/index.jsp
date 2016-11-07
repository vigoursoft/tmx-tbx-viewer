
<%@page import="java.net.URI"%>
<%@page import="java.io.File"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <title>Start Page</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>

        <%
            String UPLOAD_DIR = "uploads";
            String applicationPath = System.getProperty("user.home");
            String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;

            File l_Directory = new File(uploadFilePath);
            File[] l_files = l_Directory.listFiles();

            for (int c = 0; c < l_files.length; c++) {
                if (l_files[c].isDirectory()) {
                    //
                } else if (l_files[c].getName().endsWith(".tmx")) {
                    String uri = request.getRequestURL()
                            + "TmxServlet?vtmx=" + l_files[c].getName();
                    out.println("<a href='" + uri + "'>" + l_files[c].getName() + "</a></br>");

                }
                else if (l_files[c].getName().endsWith(".tbx")) {
                String uri = request.getRequestURL()
                            + "TmxServlet?vtbx=" + l_files[c].getName();
                    out.println("<a href='" + uri + "'>" + l_files[c].getName() + "</a></br>");

            }
            }


        %>  

        <p> </p>
        <form action="TmxServlet" method="post" enctype="multipart/form-data">
            <fieldset style="width:20%"> <legend>Upload a .tmx or a .tbx file for display</legend>  
                <p>   <input type="text" name="description" /></p>
                <p><input type="file" name="file"  multiple /></p>

                <p> <input type="submit"  value="Upload"   /></p>

            </fieldset>
        </form>

    </body>
</html>
