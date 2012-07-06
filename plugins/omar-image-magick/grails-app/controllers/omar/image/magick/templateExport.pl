#! /opt/local/bin/perl

$paramsFile = @ARGV[0];
$DEBUG = 0;




###################################################################################################
######################################## Script Parameters ########################################
###################################################################################################
########## Read in script parameters
$numberOfParams = -1;
open(FILE, $paramsFile);
while($line = <FILE>)
{
	$numberOfParams++;
	@params[$numberOfParams] = $line;
}
close (FILE);
chomp(@params);

########## Assign Parameters 
$pathToImageMagick = @params[0];
$imageURL = @params[1];	
$logoFile = @params[2];	
$line1 = @params[3];
$line2 = @params[4];
$line3 = @params[5];
$includeOutlineMap = @params[6];
$includeOverviewMap = @params[7];
$country = @params[8];
$northAngle = @params[9];
$securityClassification = @params[10];
$logoFilesLocation = @params[11];
$mapFilesLocation = @params[11]."overviewMaps/";
$tempFilesLocation = @params[12];
$date = @params[13];





################################################################################################
######################################## Image Download ########################################
################################################################################################
if ($DEBUG) { print "##### Image Download #####\n"; }
########## Image filename once it is downloaded
$imageFile = $tempFilesLocation.$date."omarImage.png";
if ($DEBUG) { print "Image filename once it is downloaded: $imageFile\n"; }

########## Download the image file
$x = "curl -s -L '$imageURL' -o $imageFile";
`$x`;
if ($DEBUG) { print "Download the image file: $x\n\n"; }





##################################################################################################
######################################## Image Dimensions ########################################
##################################################################################################
if ($DEBUG) { print "##### Image Dimensions #####\n";}
########## Determine the width of the image
$x = $pathToImageMagick."identify -format %w $imageFile";
$imageWidth = `$x`;
chomp($imageWidth);
if ($DEBUG) { print "Determine the width of the image: $imageWidth\n"; }

########## Determine the height of the image
$x = $pathToImageMagick."identify -format %h $imageFile";
$imageHeight = `$x`;
chomp($imageHeight);
if ($DEBUG) { print "Determine the height of the image: $imageHeight\n\n"; }





###################################################################################################
######################################## Header Adjustment ########################################
###################################################################################################
if ($DEBUG) { print "##### Header Adjustment #####\n"; }
########## Generate blank header
$headerWidth = int(0.96 * $imageWidth);
$headerHeight = int(0.14 * $imageHeight);
$x = $pathToImageMagick."convert -size $headerWidth"."x$headerHeight xc:#00000000 -transparent black -fill white -draw \"roundrectangle 0,0 $headerWidth,$headerHeight 10,10\" $tempFilesLocation".$date."header.png";
`$x`;
if ($DEBUG) { print "Generate the header: $x\n\n"; }





###########################################################################################
######################################## Logo Icon ########################################
###########################################################################################
if ($DEBUG) { print "##### Logo Icon #####\n"; }
########## Scale the logo
$logoWidth = int(0.75 * $headerHeight);
$logoHeight = $logoWidth;
$x = $pathToImageMagick."convert $logoFilesLocation".$logoFile.".png -resize $logoWidth"."x$logoHeight $tempFilesLocation".$date.$logoFile."Scaled.png";
`$x`;
if ($DEBUG) { print "Scale the logo: $x\n"; }

########## Add the logo to the header 
$logoOffset = ($headerHeight - $logoHeight) / 2;
$x = $pathToImageMagick."composite $tempFilesLocation".$date.$logoFile."Scaled.png -gravity West -geometry +$logoOffset+0 $tempFilesLocation".$date."header.png $tempFilesLocation".$date."header.png";
`$x`;
if ($DEBUG) { print "Add the logo to the header: $x\n"; }

########## Delete the scaled logo file
$x = "rm $tempFilesLocation".$date.$logoFile."Scaled.png";
`$x`;
if ($DEBUG) { print "Delete the scaled logo file: $x\n\n"; }





#############################################################################################
######################################## Outline Map ########################################
#############################################################################################
if ($DEBUG) { print "##### Outline Map #####\n"; }
########## Determine the height of the outline map
$outlineMapHeight = int(0.2 * $imageHeight);
if ($DEBUG) { print "Determine the height of the outline map: $outlineMapHeight\n"; }

##########
if ($includeOutlineMap eq "on")
{
	########## Scale the outline map
	$x = $pathToImageMagick."convert $mapFilesLocation".$country.".gif -resize x$outlineMapHeight $tempFilesLocation".$date."outlineMapScaled.png";
	`$x`;
	if ($DEBUG) { print "Scale the outline map: $x\n"; }

	########## Determine the height of the scaled outline map
	$x = $pathToImageMagick."identify -format %h $tempFilesLocation".$date."outlineMapScaled.png";
	$outlineMapHeight = `$x`;
	chomp($outlineMapHeight);
	if ($DEBUG) { print "Determine the height of the scaled outline map: $outlineMapHeight\n"; }

	########## Add a shadow to the outline map
	$x = $pathToImageMagick."convert -page +4+4 $tempFilesLocation".$date."outlineMapScaled.png -matte \\( +clone -background black -shadow 60x4+4+4 \\) +swap -background none -mosaic $tempFilesLocation".$date."outlineMapScaled.png";
	`$x`;
	if ($DEBUG) { print "Add a shadow to the outline map: $x\n"; }

	########## Determine the width of the outline map with a shadow
	$x = $pathToImageMagick."identify -format %w $tempFilesLocation".$date."outlineMapScaled.png";
	$outlineMapWidth = `$x`;
	chomp($outlineMapWidth);
	if ($DEBUG) { print "Determine the width of the outline map with a shadow: $outlineMapWidth\n\\n"; }
}
else 
{
	if ($DEBUG) { print "\n"; }
}





##################################################################################################################
################################################## Overview Map ##################################################
##################################################################################################################
if ($DEBUG) { print "##### Overview Map #####\n"; }
if ($includeOverviewMap eq "on")
{
	########## Scale the overview map
	$overviewMapHeight = $outlineMapHeight;
	$x = $pathToImageMagick."convert $mapFilesLocation".$country.".gif -resize x$overviewMapHeight $tempFilesLocation".$date."overviewMapScaled.png";
	`$x`;
	if ($DEBUG) { print "Scale the overview map: $x\n"; }

	########## Add a shadow to the overview map
	$x = $pathToImageMagick."convert -page +4+4 $tempFilesLocation".$date."overviewMapScaled.png -matte \\( +clone -background black -shadow 60x4+4+4 \\) +swap -background none -mosaic $tempFilesLocation".$date."overviewMapScaled.png";
	`$x`;
	if ($DEBUG) { print "Add a shadow to the overview map: $x\n"; }

	########## Determine the width of the overview map
	$x = $pathToImageMagick."identify -format %w $tempFilesLocation".$date."overviewMapScaled.png";
	$overviewMapWidth = `$x`;
	chomp($overviewMapWidth);
	if ($DEBUG) { print "Determine the width of the overview map: $overviewMapWidth\n\n"; }
}
else 
{
	if ($DEBUG) { print "\n"; }
}





#############################################################################################
######################################## Header Text ########################################
#############################################################################################
if ($DEBUG) { print "##### Header Text #####\n"; }
########## Determine the maximum width for each line of text
$textWidth = int($headerWidth - (2 * $logoWidth) - (5 * $logoOffset) - $outlineMapWidth - $overviewMapWidth);
if ($DEBUG) { print "Determine the maximum width for each line of text: $textWidth\n"; }

########## Generate 1st line of text
$line1Height = int(0.41 * $logoHeight);
$x = $pathToImageMagick."convert -background white -fill black -size $textWidth"."x$line1Height -gravity West caption:'".$line1."' $tempFilesLocation".$date."line1.png";
`$x`;
if ($DEBUG) { print "Generate 1st line of text: $x\n"; }

########## Generate 2nd line of text 
$line2Height = int(0.33 * $logoHeight);
$x = $pathToImageMagick."convert -background white -fill black -size $textWidth"."x$line2Height -gravity West caption:'".$line2."' $tempFilesLocation".$date."line2.png";
`$x`;
if ($DEBUG) { print "Generate 2nd line of text: $x\n"; }

########## Generate 3rd line of text
$line3Height = int(0.28 * $logoHeight);
$x = $pathToImageMagick."convert -background white -fill black -size $textWidth"."x$line3Height -gravity West caption:'".$line3."' $tempFilesLocation".$date."line3.png";
`$x`;
if ($DEBUG) { print "Generate 3rd line of text: $x\n"; }

########## Combine all three lines of text 
$x = $pathToImageMagick."convert $tempFilesLocation".$date."line1.png $tempFilesLocation".$date."line2.png $tempFilesLocation".$date."line3.png -append $tempFilesLocation".$date."text.png";
`$x`;
if ($DEBUG) { print "Combine all three lines of text: $x\n"; }

########## Delete the 1st line of text file 
$x = "rm $tempFilesLocation".$date."line1.png";
`$x`;
if ($DEBUG) { print "Delete the 1st line of text file: $x\n"; }

########## Delete the 2nd line of text file 
$x = "rm $tempFilesLocation".$date."line2.png";
`$x`;
if ($DEBUG) { print "Delete the 2nd line of text file: $x\n"; }

########## Delete the 3rd line of text file
$x = "rm $tempFilesLocation".$date."line3.png";
`$x`;
if ($DEBUG) { print "Delete the 3rd line of text file: $x\n\n"; }





#######################################################################################################################
################################################## Header Adjustment ##################################################
#######################################################################################################################
if ($DEBUG) { print "##### Header Adjustment #####\n"; }
########## Add the header text to the header 
$textOffset = 2 * $logoOffset + $logoWidth;
$x = $pathToImageMagick."composite $tempFilesLocation".$date."text.png -gravity West -geometry +$textOffset+0 $tempFilesLocation".$date."header.png $tempFilesLocation".$date."header.png";
`$x`;
if ($DEBUG) { print "Add the header text to the header: $x\n"; }

########## Delete the header text file
$x = "rm $tempFilesLocation".$date."text.png";
`$x`;
if ($DEBUG) { print "Delete the header text file: $x\n\n"; }





#################################################################################################################
################################################## North Arrow ##################################################
#################################################################################################################
if ($DEBUG) { print "##### North Arrow #####\n"; }
########## Scale the north arrow
$x = $pathToImageMagick."convert $logoFilesLocation"."northArrow.png -resize $logoWidth"."x$logoHeight $tempFilesLocation".$date."northArrowScaled.png";
`$x`;
if ($DEBUG) { print "Scale the north arrow: $x\n"; }

########## Rotate the north arrow
$x = $pathToImageMagick."convert $tempFilesLocation".$date."northArrowScaled.png -rotate $northAngle $tempFilesLocation".$date."northArrowRotated.png";
`$x`;
if ($DEBUG) { print "Rotate the north arrow: $x\n"; }

########## Determine the width of the scaled north arrow
$x = $pathToImageMagick."identify -format %w $tempFilesLocation".$date."northArrowScaled.png";
$northArrowWidth = `$x`;
chomp($northArrowWidth);
if ($DEBUG) { print "Determine the width of the scaled north arrow: $northArrowWidth\n"; }

########## Determine the height of the scaled north arrow
$x = $pathToImageMagick."identify -format %h $tempFilesLocation".$date."northArrowScaled.png";
$northArrowHeight = `$x`;
chomp($northArrowHeight);
if ($DEBUG) { print "Determine the height of the scaled north arrow: $northArrowHeight\n"; }

########## Delete the scaled north arrow file
$x = "rm $tempFilesLocation".$date."northArrowScaled.png";
`$x`;
if ($DEBUG) { print "Delete the scaled north arrow file: $x\n"; }

########## Crop the north rotated north arrow
$x = $pathToImageMagick."convert $tempFilesLocation".$date."northArrowRotated.png -crop $northArrowWidth"."x$northArrowHeight+0+0 +repage $tempFilesLocation".$date."northArrowRotated.png";
`$x`;
if ($DEBUG) { print "Crop the north rotated north arrow: $x\n\n"; }





#######################################################################################################################
################################################## Header Adjustment ##################################################
#######################################################################################################################
if ($DEBUG) { print "##### Header Adjustment #####\n"; }
########## Add the north arrow to the header
$northArrowOffset = $logoOffset;
$x = $pathToImageMagick."composite $tempFilesLocation".$date."northArrowRotated.png -gravity East -geometry +$northArrowOffset+0 $tempFilesLocation".$date."header.png $tempFilesLocation".$date."header.png";
`$x`;
if ($DEBUG) { print "Add the north arrow to the header: $x\n";  }

##########  Delete the north arrow rotated file
$x = "rm $tempFilesLocation".$date."northArrowRotated.png";
`$x`;
if ($DEBUG) { print "Delete the north arrow rotated file: $x\n\n"; }





#######################################################################################################################
################################################## Header Adjustment ##################################################
#######################################################################################################################
if ($DEBUG) { print "##### Header Adjustment #####\n"; }
########## Add a shadow to the header
$x = $pathToImageMagick."convert -page +4+4 $tempFilesLocation".$date."header.png -matte \\( +clone -background black -shadow 60x4+4+4 \\) +swap -background none -mosaic $tempFilesLocation".$date."header.png";
`$x`;
if ($DEBUG) { print "Add a shadow to the header: $x\n"; }

########## Add the header to the image 
$headerOffset = int(($imageWidth - 0.96 * $imageWidth) / 4);
$x = $pathToImageMagick."composite $tempFilesLocation".$date."header.png -gravity North -geometry +0+$headerOffset $imageFile $tempFilesLocation".$date."finishedProduct.png";
`$x`;
if ($DEBUG) { print "Add the header to the image: $x\n\n"; }





#######################################################################################################################
################################################## Report Adjustment ##################################################
#######################################################################################################################
if ($DEBUG) { print "##### Report Adjustment #####\n"; }
########## Add the outline map to the finished product

$outlineMapOffset = ($imageWidth - $headerWidth) / 2 +  $northArrowWidth + 2 * $northArrowOffset;
$x = $pathToImageMagick."composite $tempFilesLocation".$date."outlineMapScaled.png -gravity NorthEast -geometry +$outlineMapOffset+0 $tempFilesLocation".$date."finishedProduct.png $tempFilesLocation".$date."finishedProduct.png";
`$x`;
if ($DEBUG) { print "Add the outline map to the finished product: $x\n\n"; }





#######################################################################################################################
################################################## Report Adjustment ##################################################
#######################################################################################################################
if ($DEBUG) { print "##### Report Adjustment #####\n"; }
########## Add the overview map to the finished product
if ($includeOverviewMap eq "on")
{
	$overviewMapOffset = $outlineMapOffset + $outlineMapWidth;;
	$x = $pathToImageMagick."composite $tempFilesLocation".$date."overviewMapScaled.png -gravity NorthEast -geometry +$overviewMapOffset+0 $tempFilesLocation".$date."finishedProduct.png $tempFilesLocation".$date."finishedProduct.png"; 
	`$x`;
	if ($DEBUG) { print "Add the overview map to the finished product: $x\n\n"; }
}





#####################################################################################################################
################################################## Security Banner ##################################################
#####################################################################################################################
if ($DEBUG) { print "##### Security Banner #####\n"; }
########## Generate security banner text
$securityTextHeight = int(0.25 * $headerHeight);
$x = $pathToImageMagick."convert -background white -fill black -size x$securityTextHeight -gravity West label:'".$securityClassification."' $tempFilesLocation".$date."securityText.png";
`$x`;

########## Determine the width of the security banner
$x = $pathToImageMagick."identify -format %w $tempFilesLocation".$date."securityText.png";
$securityBannerWidth = `$x`;
chomp($securityBannerWidth);
$securityBannerWidth = 1.1 * $securityBannerWidth;

########## Determine the height of the security banner
$x = $pathToImageMagick."identify -format %h $tempFilesLocation".$date."securityText.png";
$securityBannerHeight = `$x`;
chomp($securityBannerHeight);
$securityBannerHeight = 1.1 * $securityBannerHeight;

########## Generate security banner
$x = $pathToImageMagick."convert -size $securityBannerWidth"."x$securityBannerHeight xc:#00000000 -transparent black -fill white -draw \"roundrectangle 0,0 $securityBannerWidth,$securityBannerHeight 10,10\" $tempFilesLocation".$date."securityBanner.png";
`$x`;

########## Add text to security banner
$x = $pathToImageMagick."composite $tempFilesLocation".$date."securityText.png -gravity Center -geometry +0+0 $tempFilesLocation".$date."securityBanner.png $tempFilesLocation".$date."securityBanner.png";
`$x`;

########## Delete the security text file
$x = "rm $tempFilesLocation".$date."securityText.png";
`$x`;

########## Add a shadow to the security banner
$x = $pathToImageMagick."convert -page +4+4 $tempFilesLocation".$date."securityBanner.png -matte \\( +clone -background black -shadow 60x4+4+4 \\) +swap -background none -mosaic $tempFilesLocation".$date."securityBanner.png";
`$x`;

########## Add security banner to finished product
$securityBannerOffsetX = ($imageWidth - 0.96 * $imageWidth) / 4;
$securityBannerOffsetX = int($securityBannerOffsetX);
$securityBannerOffsetY = int($securityBannerOffsetX / 2);
$x = $pathToImageMagick."composite $tempFilesLocation".$date."securityBanner.png -gravity SouthWest -geometry +$securityBannerOffsetX+$securityBannerOffsetY $tempFilesLocation".$date."finishedProduct.png $tempFilesLocation".$date."finishedProduct.png";
`$x`;

##########
if ($includeOutlineMap eq "on")
{
	$x = $pathToImageMagick."identify -format %h $tempFilesLocation".$date."outlineMapScaled.png";
	$outlineMapHeight = `$x`;
	chomp($outlineMapHeight);
	$securityBannerOffsetY = $outlineMapHeight;
}
elsif ($includeOverviewMap eq "on")
{
	$x = $pathToImageMagick."identify -format %h $tempFilesLocation".$date."overviewMapScaled.png";
	$overviewMapHeight = `$x`;
	chomp($overviewMapHeight);
	$securityBannerOffsetY = $overviewMapHeight;
}
else
{
	$x = $pathToImageMagick."identify -format %h $tempFilesLocation".$date."header.png";
	$headerHeight = `$x`;
	chomp($headerHeight);
	$securityBannerOffsetY = $headerHeight + $headerOffset;
}
$x = $pathToImageMagick."composite $tempFilesLocation".$date."securityBanner.png -gravity NorthEast -geometry +$securityBannerOffsetX+$securityBannerOffsetY $tempFilesLocation".$date."finishedProduct.png $tempFilesLocation".$date."finishedProduct.png";
`$x`;

########## Delete the header file
$x = "rm $tempFilesLocation".$date."header.png";
`$x`;
if ($DEBUG) { print "Delete the header file: $x\n"; }

########## Delete the outline map file
$x = "rm $tempFilesLocation".$date."outlineMapScaled.png";
`$x`;
if ($DEBUG) { print "Delete the outline map file: $x\n"; }

########## Delete the overview map file
$x = "rm $tempFilesLocation".$date."overviewMapScaled.png";
`$x`;
if ($DEBUG) { print "Delete the overview map file: $x\n"; }

########## Delete the security banner file
$x = "rm $tempFilesLocation".$date."securityBanner.png";
`$x`;
if ($DEBUG) { print "Delete the security banner file\n\n"; }




################################################################################################
######################################## Diclaimer Text ########################################
################################################################################################
########## Generate disclaimer text
$disclaimerTextWidth = $imageWidth;
$disclaimerTextHeight = int(0.035 * $imageHeight); 
$x = $pathToImageMagick."convert $tempFilesLocation".$date."finishedProduct.png -background yellow -fill black -size $disclaimerTextWidth"."x$disclaimerTextHeight -gravity center label:'Not an intelligence product  //  For informational use only  //  Not certified for targeting' -append $tempFilesLocation".$date."finishedProduct.png";
`$x`;

########## Delete the image file
$x = "rm $tempFilesLocation".$date."omarImage.png";
`$x`;




########## Report Location ##########
######### Print the location of the finished product#
print "$tempFilesLocation".$date."finishedProduct.png";
