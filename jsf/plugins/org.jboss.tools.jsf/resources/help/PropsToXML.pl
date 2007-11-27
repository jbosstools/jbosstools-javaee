# 	4/26/2004
#		PropsToXML.pl
# 
# 	This is a program that reads in a valid properties file and creates an XML file
#		from it. If the output is being stored in a file, it will include a stylesheet
#		reference at the top of the file. This allows viewing the styled contents by 
#		double-clicking the XML file.
# 	

# 	0 arguments:	standard input is input -- standard output is output
# 	1 argument:		argument is filename of input -- standard output is output
# 	2 arguments:	argument 1 is filename of input -- argument 2 is filename of output
# 	2+ arguments:	arguments after 2 ignored
if (scalar(@ARGV) >= 1) {
	open(INFILE, "$ARGV[0]") or die("Couldn't open file $ARGV[0] for reading");
} else {
	open(INFILE, "-") or die("Couldn't open standard input for reading");
}
if (scalar(@ARGV) >= 2) {
	open(OUTFILE, ">$ARGV[1]") or die("Couldn't open file $ARGV[1] for writing");
} else {
	open(OUTFILE, ">-") or die("Couldn't open standard output for writing");
}

#		If the output is going to a file, print a stylesheet reference at the top
if (scalar(@ARGV) >= 2) {
	print OUTFILE "\<?xml-stylesheet type=\"text/xsl\" href=\"keys.xsl\" ?\>\n";
} 
print OUTFILE "<properties filename=\"$ARGV[0]\">\n";
while (<INFILE>) {
# if it's not a comment or blank it must be a name/value pair...of course!
	if (not ( /^#/ or /^$/ )) {
  	chomp();
    split (/=/);
 		print OUTFILE "<property name=\"@_[0]\" value=\"@_[1]\"/>\n";
  }
}
print OUTFILE "</properties>\n";