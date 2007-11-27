:: 4/26/2004
:: keys.bat
:: one-liner to turn the key.properties file into an XML 
:: file with a stylesheet reference in it that will 
:: distinguish between help file refs and dialog text
:: 
:: the path variable at the top of keys.xslt will have to
:: be adjusted for your setup -- don't commit the file 
:: after this adjustment
perl PropsToXML.pl keys-jsf.properties keys.xml