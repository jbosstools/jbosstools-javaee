<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>SIP Route Manager</title>
        <link rel="stylesheet" type="text/css" href="styles.css"/>
        <!--  Import the required JS libraries -->
        <script type="text/javascript" src="<%= request.getContextPath()%>/common.js"></script>
        <script type="text/javascript" src="<%= request.getContextPath()%>/ajax.js"></script>
        <script type="text/javascript" src="<%= request.getContextPath()%>/helptips.js"></script>       
        <script type="text/javascript" src="<%= request.getContextPath()%>/NumberScript.js"></script>       
        <script type="text/javascript" src="<%= request.getContextPath()%>/UserScript.js"></script>
        <script type="text/javascript">
            // ---------------
            // Local instances
            // ---------------
            var currentAnchor = null;
            var number   = new NumberScript();
            var user     = new UserScript();
            // ------
            // Logoff
            // ------
            function logoff()
            {
                window.onbeforeunload = null;
                location.replace("<%= request.getContextPath()%>/logoff");
            }
            // -----------
            // manageUsers
            // -----------
            function manageUsers()
            {
                // Prime Users panel
                sendRequest("<%= request.getContextPath()%>/controller", 
                            "dynamicPanel", 
                            null,
                            "task=usertoroutesetManagement&action=entry", 
                            function()
                            {
                                user.showRSChoice();
                                user.showUsers();
                            });
            }            
            // -------------
            // manageNumbers
            // -------------
            function manageNumbers()
            {
                sendRequest("<%= request.getContextPath()%>/controller", 
                            "dynamicPanel", 
                            null,
                            "task=nntouserManagement&action=entry",
                            function()
                            {
                                number.showNumberChoice();
                                number.showForwarding();
                            });
            }
            // ------------------------------------------------------------
            // Highlight the clicked anchor and clear any current selection
            // ------------------------------------------------------------
            function highlightAnchor(a)
            {
                if (currentAnchor !== null)
                {
                    currentAnchor.parentNode.className = "unselected";
                }
                currentAnchor = a;
                currentAnchor.parentNode.className = "selected";
            }      
        </script> 
    </head>
    <body>
        <!-- Load the help tips -->
        <div id="helptip"></div>
        <!-- Like the Apple site, centred on the page -->
        <div id="wrapper">
           <!-- The full page content -->
            <div id="content">
                <!-- Standard header to site pages -->
                <div id="header">   
                    <div style="float:left; margin-top:15px;"><a href="http://timepoorprogrammer.blogspot.com"><img alt="" src="<%= request.getContextPath()%>/images/logos/author.png" style="border-style: none"/></a></div>
                    <div style="float:left; margin-left:30px;"><img alt="" src="<%= request.getContextPath()%>/images/logos/banner.png"/></div>
                    <div style="float:right; margin-top:21px; margin-left:5px; margin-right:5px">
                        <span onclick="logoff()" class="haettenContainer">
                            <img class="icon" src="<%= request.getContextPath()%>/images/icons/off.gif" alt=""/>Log Off
                        </span>    
                    </div>
                </div>
                <!-- The available list of tab action -->
                <div class="tabs">
                    <ul>
                        <!-- The starting "selected" content of the overall page is manageUsers -->
                        <li class="unselected"><a href="javascript:manageUsers()"
                                              onmouseover="showhelptip('Add/Edit/Delete Users (system managed numbers) and allocate/re-allocate them to RouteSets', 250)"
                                              onmouseout="hidehelptip()"
                                              onclick="highlightAnchor(this);">Users</a></li>  
                        <li class="unselected"><a href="javascript:manageNumbers()"
                                                onmouseover="showhelptip('Add/Edit/Delete forwarding records and associate/de-associate them to managed or un-managed destinations in weight order', 250)"
                                                onmouseout="hidehelptip()"
                                                onclick="highlightAnchor(this);">Forwarding</a></li> 
                    </ul>    
                </div>
                 <!-- This gets populated by clicking on a particular link above -->
                <div id="dynamicPanel">
                <p class="welcomeBlock">Welcome to the <b>admin</b> provisioning portal for the Avaya SIP Route Manager (SRM).</p> 
                <p class="welcomeBlock">The SRM lives in the core of a communications solution providing call switching 
                for external networks and communities, binding these separate elements together.</p>
                <p class="welcomeBlock">An <b>admin</b> role administrator can add <b>Users</b> and <b>Forwarding</b> information to the 
                system.</p>
                <p class="welcomeBlock">When routing a call, the system first checks to see if the normalized dialled number is found in its 
                <b>Forwarding</b> records.  Forwarding relates a normalised dialled number to a set of destinations.  If records are found, 
                the system routes via the highest weighted record first.  For a record, a destination may be either a known User or some 
                unmanaged destination.</p>
                <p class="welcomeBlock">A User maps a known callee to a particular Outbound Route Set.  Alternatively, if the callee is not 
                known to us, calls to an unmanaged destination are achieved by matching the source and destination 
                of the call against the patterns defined in the configured <b>Best Fits</b>.</p>
                <p class="welcomeBlock">To amend the <b>Outbound Route Sets</b>, <b>Routes</b>, and <b>Best Fits</b> data that define the 
                system routing you will need full <b>sysadmin</b> privileges.  As an <b>admin</b> role user you are restricted to using 
                the Outbound Route Sets already provisioned.</p>  
                </div>
                <!-- Standard footer to site pages -->
                <div class="padding"></div>
                <div id="footer">Copyright &copy; 2008 Avaya</div>
            </div>
        </div>
    </body>
</html>

