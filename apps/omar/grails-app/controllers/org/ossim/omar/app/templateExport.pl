#! /opt/local/bin/perl

$paramsFile = @ARGV[0];





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
$northAngle = @params[6];
$logoFilesLocation = @params[7];
$tempFilesLocation = @params[8];
$date = @params[9];





################################################################################################
######################################## Image Download ########################################
################################################################################################
########## Image filename once it is downloaded
$imageFile = $tempFilesLocation.$date."omarImage.png";

########## Download the image file
$x = "curl -L '$imageURL' -o $imageFile";
`$x`;





##################################################################################################
######################################## Image Dimensions ########################################
##################################################################################################
########## Determine the width of the image 
$x = $pathToImageMagick."identify -format %w $imageFile";
$imageWidth = `$x`;
chomp($imageWidth);

########## Determine the height of the image 
$x = $pathToImageMagick."identify -format %h $imageFile";
$imageHeight = `$x`;
chomp($imageHeight);





###################################################################################################
######################################## Header Adjustment ########################################
###################################################################################################
########## Generate header
$headerWidth = int(0.9633 * $imageWidth);
$headerHeight = int(0.1286 * $imageHeight);
$x = $pathToImageMagick."convert -size $headerWidth"."x$headerHeight xc:#00000000 -transparent black -fill white -draw \"roundrectangle 0,0 $headerWidth,$headerHeight 10,10\" $tempFilesLocation".$date."header.png";
`$x`;





###########################################################################################
######################################## Logo Icon ########################################
###########################################################################################
########## Scale the logo
$logoWidth = int(0.75 * $headerHeight);#
$logoHeight = $logoWidth;
$x = $pathToImageMagick."convert $logoFilesLocation".$logoFile.".png -resize $logoWidth"."x$logoHeight $tempFilesLocation".$date.$logoFile."Scaled.png";
`$x`;

########## Add the logo to the header 
$logoOffset = ($headerHeight - $logoHeight) / 2;																		       
$x = $pathToImageMagick."composite $tempFilesLocation".$date.$logoFile."Scaled.png -gravity West -geometry +$logoOffset+0 $tempFilesLocation".$date."header.png $tempFilesLocation".$date."header.png";
`$x`;

########## Delete the scaled logo file#
$x = "rm $tempFilesLocation".$date.$logoFile."Scaled.png";
`$x`;





#############################################################################################
######################################## Header Text ########################################
#############################################################################################
$textWidth = int(0.6654 * $headerWidth);

########## Generate 1st line of text
$line1Height = int(0.41 * $logoHeight);
$x = $pathToImageMagick."convert -background white -fill black -size $textWidth"."x$line1Height -gravity West caption:'".$line1."' $tempFilesLocation".$date."line1.png";
`$x`;

########## Generate 2nd line of text 
$line2Height = int(0.33 * $logoHeight);
$x = $pathToImageMagick."convert -background white -fill black -size $textWidth"."x$line2Height -gravity West caption:'".$line2."' $tempFilesLocation".$date."line2.png";
`$x`;

########## Generate 3rd line of text
$line3Height = int(0.28 * $logoHeight);
$x = $pathToImageMagick."convert -background white -fill black -size $textWidth"."x$line3Height -gravity West caption:'".$line3."' $tempFilesLocation".$date."line3.png";
`$x`;

########## Combine all three lines of text 
$x = $pathToImageMagick."convert $tempFilesLocation".$date."line1.png $tempFilesLocation".$date."line2.png $tempFilesLocation".$date."line3.png -append $tempFilesLocation".$date."text.png";
`$x`;

########## Delete the 1st line of text file 
$x = "rm $tempFilesLocation".$date."line1.png";
`$x`;

########## Delete the 2nd line of text file 
$x = "rm $tempFilesLocation".$date."line2.png";
`$x`;

########## Delete the 3rd line of text file
$x = "rm $tempFilesLocation".$date."line3.png";
`$x`;





###################################################################################################
######################################## Header Adjustment ########################################
###################################################################################################
########## Add the header text to the header 
$textOffset = 2 * $logoOffset + $logoWidth;
$x = $pathToImageMagick."composite $tempFilesLocation".$date."text.png -gravity West -geometry +$textOffset+0 $tempFilesLocation".$date."header.png $tempFilesLocation".$date."header.png";
`$x`;

########## Delete the header text file
$x = "rm $tempFilesLocation".$date."text.png";
`$x`;





#############################################################################################
######################################## North Arrow ########################################
#############################################################################################
########## Scale the north arrow
$x = $pathToImageMagick."convert $logoFilesLocation"."northArrow.png -resize $logoWidth"."x$logoHeight $tempFilesLocation".$date."northArrowScaled.png";
`$x`;

########## Rotate the north arrow
$x = $pathToImageMagick."convert $tempFilesLocation".$date."northArrowScaled.png -rotate $northAngle $tempFilesLocation".$date."northArrowRotated.png";
`$x`;

########## Determine the width of the scaled north arrow
$x = $pathToImageMagick."identify -format %w $tempFilesLocation".$date."northArrowScaled.png";
$northArrowWidth = `$x`;
chomp($northArrowWidth);

########## Determine the height of the scaled north arrow
$x = $pathToImageMagick."identify -format %h $tempFilesLocation".$date."northArrowScaled.png";
$northArrowHeight = `$x`;
chomp($northArrowHeight);

########## Delete the scaled north arrow file
$x = "rm $tempFilesLocation".$date."northArrowScaled.png";
`$x`;

########## Crop the north rotated north arrow
$x = $pathToImageMagick."convert $tempFilesLocation".$date."northArrowRotated.png -crop $northArrowWidth"."x$northArrowHeight+0+0 +repage $tempFilesLocation".$date."northArrowRotated.png";
`$x`;





#######################################################################################################################
################################################## Header Adjustment ##################################################
#######################################################################################################################
########## Add the north arrow to the header
$northArrowOffset = $logoOffset;
$x = $pathToImageMagick."composite $tempFilesLocation".$date."northArrowRotated.png -gravity East -geometry +$northArrowOffset+0 $tempFilesLocation".$date."header.png $tempFilesLocation".$date."header.png";
`$x`;

##########  Delete the north arrow rotated file
$x = "rm $tempFilesLocation".$date."northArrowRotated.png";
`$x`;





###################################################################################################
######################################## Header Adjustment ########################################
###################################################################################################
########## Add a shadow to the header
$x = $pathToImageMagick."convert -page +4+4 $tempFilesLocation".$date."header.png -matte \\( +clone -background black -shadow 60x4+4+4 \\) +swap -background none -mosaic $tempFilesLocation".$date."header.png";
`$x`;

########## Add the header to the image 
$headerOffset = ($imageWidth - $headerWidth) / 4;
$headerOffset = int($headerOffset);
$x = $pathToImageMagick."composite $tempFilesLocation".$date."header.png -gravity North -geometry +0+$headerOffset $imageFile $tempFilesLocation".$date."finishedProduct.png";
`$x`;

########## Delete the header file
$x = "rm $tempFilesLocation".$date."header.png";
`$x`;





#################################################################################################
######################################## Security Banner ########################################
#################################################################################################
########## Determine security classification
# stub for external script
$securityClassification = "UNCLASSIFIED // FOUO";
########## Generate security banner text
$securityTextHeight = int(0.25 * $headerHeight);
$x = $pathToImageMagick."convert -background white -fill black -size x$securityTextHeight -gravity West label:'".$securityClassification."' $tempFilesLocation".$date."securityBanner.png";
`$x`;

########## Add security banner to finished product
$x = $pathToImageMagick."composite $tempFilesLocation".$date."securityBanner.png -gravity SouthWest -geometry +0+0 $tempFilesLocation".$date."finishedProduct.png $tempFilesLocation".$date."finishedProduct.png";
`$x`;

########## Delete the security banner file
$x = "rm $tempFilesLocation".$date."securityBanner.png";
`$x`;





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




print "$tempFilesLocation".$date."finishedProduct.png";
