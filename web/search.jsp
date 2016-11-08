<%--
  Created by IntelliJ IDEA.
  User: Curtis
  Date: 11/6/16
  Time: 1:22 AM
  To change this template use File | Settings | File Templates.
--%>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cs390.Crawler.searchResult" %>
<%@ page import="cs390.Crawler.searchPeople" %>
<%@ page import="java.util.*" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>${keyword} - CS390Search</title>

    <!-- Bootstrap Core CSS -->
    <link href="HTML-Resource/startbootstrap-1-col-portfolio-gh-pages/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="HTML-Resource/startbootstrap-1-col-portfolio-gh-pages/css/1-col-portfolio.css" rel="stylesheet">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

</head>

<body>

<!-- Navigation -->
<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/index.jsp">CS390Java Project</a>
        </div>
        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
                <!--<li>
                    <a href="#">About</a>
                </li>
                <li>
                    <a href="#">Services</a>
                </li>
                <li>
                    <a href="#">Contact</a>
                </li>  -->
            </ul>
            <form class="navbar-form navbar-right" method="post" action="search">
                <input type="text" placeholder="" name="searchWord" class="form-control">
                <button type="submit" class="btn btn-success">Search</button>
            </form>
        </div>
        <!-- /.navbar-collapse -->
    </div>
    <!-- /.container -->
</nav>

<!-- Page Content -->
<div class="container">

    <!-- Page Heading -->
    <div class="row">
        <div class="col-lg-12">
            <h1 class="page-header">Search Result
                <small>for ${keyword}</small>
            </h1>
        </div>
    </div>
    <!-- /.row -->
    <%
        // retrieve your list from the request, with casting
        List<searchResult> rs = (List<searchResult>)request.getAttribute("result_list");
        List<searchPeople> sp = (List<searchPeople>)request.getAttribute("sp_list");
        if(sp != null && sp.size() != 0) {
            for (searchPeople s : sp) {
                out.print("    <div class=\"row\">\n" +
                        "        <div class=\"col-md-7\">\n" +
                        "            <a href=\" ");
                out.print(s.getHomepage_url());
                out.print("\">");
                out.println("<img class=\"img-responsive\" src=\"");
                if(s.getImg_url()!=null){
                    out.println(s.getImg_url());
                }
                out.println("\" alt=\"\" height=\"350\" width=\"150\">");
                out.println("            </a>\n" +
                        "        </div>\n" +
                        "        <div class=\"col-md-5\">");
                out.println("<h3>" + s.getFirstName() + " " + s.getLastName() + "</h3>");
                out.println("<h4>" + s.getPosition() + "</h4>");
                if(s.getPhone_number()!=null){
                    out.println("<h5>" + s.getPhone_number() + "</h5>");

                }
                if(s.getOffice()!=null){
                    out.println("<h5>" + s.getOffice() + "</h5>");
                }
                out.println("<a href=\"" + s.getEmail_url() + "\">" + "Mail" + "</a>");
                out.println("<br/>");
                out.println("<a href=\"" + s.getHomepage_url() + "\">" + "Bio" + "</a>");
                out.println("        </div>\n" +
                        "    </div>\n" +
                        "    <!-- /.row -->\n" +
                        "\n" +
                        "    <hr>");
            }
        }

        if(rs != null && rs.size() !=0 ){
            for(searchResult r : rs){

                out.print("    <div class=\"row\">\n" +
                        "        <div class=\"col-md-7\">\n" +
                        "            <a href=\" " );
                out.print(r.getUrl());
                out.print("\">");

                out.println("<img class=\"img-responsive\" src=\"" + r.getImage_url() + "\" alt=\"\" height=\"150\" width=\"350\">");

                out.println("            </a>\n" +
                        "        </div>\n" +
                        "        <div class=\"col-md-5\">");

                out.println("<h3>" + r.getTitle().replace(":"," ") +"</h3>");
                out.println("<h4>" + r.getUrl() +"</h4>");
                out.println("<p>" + r.getDescription().replace(":"," ") +"</p>");


                out.println("<a class=\"btn btn-primary\" href=\"" + r.getUrl() + "\">Go to This Page <span class=\"glyphicon glyphicon-chevron-right\"></span></a>");
                out.println("        </div>\n" +
                        "    </div>\n" +
                        "    <!-- /.row -->\n" +
                        "\n" +
                        "    <hr>");
            }
        }
    %>
    <!-- Project One -->
    <!--<div class="row">
        <div class="col-md-7">
            <a href="#">
                <img class="img-responsive" src="http://placehold.it/700x300" alt="">
            </a>
        </div>
        <div class="col-md-5">
            <h3>Project One</h3>
            <h4>Subheading</h4>
            <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Laudantium veniam exercitationem expedita laborum at voluptate. Labore, voluptates totam at aut nemo deserunt rem magni pariatur quos perspiciatis atque eveniet unde.</p>
            <a class="btn btn-primary" href="#">View Project <span class="glyphicon glyphicon-chevron-right"></span></a>
        </div>
    </div>
    <hr>      -->
    <!-- /.row -->



    <!-- Pagination -->
    <!--<div class="row text-center">
        <div class="col-lg-12">
            <ul class="pagination">
                <li>
                    <a href="#">&laquo;</a>
                </li>
                <li class="active">
                    <a href="#">1</a>
                </li>
                <li>
                    <a href="#">2</a>
                </li>
                <li>
                    <a href="#">3</a>
                </li>
                <li>
                    <a href="#">4</a>
                </li>
                <li>
                    <a href="#">5</a>
                </li>
                <li>
                    <a href="#">&raquo;</a>
                </li>
            </ul>
        </div>
    </div>-->
    <!-- /.row -->

    <hr>

    <!-- Footer -->
    <footer>
        <div class="row">
            <div class="col-lg-12">
                <p>Copyright &copy; Your Website 2014</p>
            </div>
        </div>
        <!-- /.row -->
    </footer>

</div>
<!-- /.container -->

<!-- jQuery -->
<script src="HTML-Resource/startbootstrap-1-col-portfolio-gh-pages/js/jquery.js"></script>

<!-- Bootstrap Core JavaScript -->
<script src="HTML-Resource/startbootstrap-1-col-portfolio-gh-pages/js/bootstrap.min.js"></script>

</body>

</html>




