:: 4/26/2004
:: changeKeys.bat
::
:: save a copy for resetKeys.bat to use
copy keys-jsf.properties key-jsf.properties.save
:: XMLize the properties file 
perl PropsToXML.pl keys-jsf.properties keys.xml
:: create a new temporary properties file with non-assigned
:: help file names changed to ones base on the key names
java -jar c:/saxon/saxon.jar keys.xml changeKeys.xslt > keys-jsf.properties 