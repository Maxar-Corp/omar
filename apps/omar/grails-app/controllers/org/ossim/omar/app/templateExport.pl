#! /opt/local/bin/perl

$paramsFile = @ARGV[0];

$numberOfParams = -1;
open(FILE, $paramsFile);
while($line = <FILE>)
{
	$numberOfParams++;
	@params[$numberOfParams] = $line;
}
close (FILE);
chomp(@params);

$pathToImageMagick = @params[0];
$imageURL = @params[1];
$logoFile = @params[2];;
$line1 = @params[3];
$line2 = @params[4];
$line3 = @params[5];
$northAngle = @params[6];
$logoFilesLocation = @params[7];
$tempFilesLocation = @params[8];
$date = @params[9];

$imageFile = $tempFilesLocation.$date."omarImage.png";

$x = "curl -L '$imageURL' -o $imageFile";
`$x`;

$x = $pathToImageMagick."identify -format %w $imageFile";
$imageWidth = `$x`;
chomp($imageWidth);

$x = $pathToImageMagick."identify -format %h $imageFile";
$imageHeight = `$x`;
chomp($imageHeight);

$headerWidth = int(0.9633 * $imageWidth);
$headerHeight = int(0.1286 * $imageHeight);
$x = $pathToImageMagick."convert -size $headerWidth"."x$headerHeight xc:#00000000 -transparent black -fill white -draw \"roundrectangle 0,0 $headerWidth,$headerHeight 10,10\" $tempFilesLocation".$date."header.png";
`$x`;

$logoWidth = int(0.75 * $headerHeight);
$logoHeight = $logoWidth;
$x = $pathToImageMagick."convert $logoFilesLocation".$logoFile.".png -resize $logoWidth"."x$logoHeight $tempFilesLocation".$date.$logoFile."Scaled.png";
`$x`;

$logoOffset = ($headerHeight - $logoHeight) / 2;
$x = $pathToImageMagick."composite $tempFilesLocation".$date.$logoFile."Scaled.png -gravity West -geometry +$logoOffset+0 $tempFilesLocation".$date."header.png $tempFilesLocation".$date."header.png";
`$x`;

$x = "rm $tempFilesLocation".$date.$logoFile."Scaled.png";
`$x`;

$textWidth = int(0.6654 * $headerWidth);
$line1Height = int(0.41 * $logoHeight);
$x = $pathToImageMagick."convert -background white -fill black -size $textWidth"."x$line1Height -gravity West caption:'".$line1."' $tempFilesLocation".$date."line1.png";
`$x`;

$line2Height = int(0.33 * $logoHeight);
$x = $pathToImageMagick."convert -background white -fill black -size $textWidth"."x$line2Height -gravity West caption:'".$line2."' $tempFilesLocation".$date."line2.png";
`$x`;

$line3Height = int(0.28 * $logoHeight);
$x = $pathToImageMagick."convert -background white -fill black -size $textWidth"."x$line3Height -gravity West caption:'".$line3."' $tempFilesLocation".$date."line3.png";
`$x`;

$x = $pathToImageMagick."convert $tempFilesLocation".$date."line1.png $tempFilesLocation".$date."line2.png $tempFilesLocation".$date."line3.png -append $tempFilesLocation".$date."text.png";
`$x`;

$x = "rm $tempFilesLocation".$date."line1.png";
`$x`;

$x = "rm $tempFilesLocation".$date."line2.png";
`$x`;

$x = "rm $tempFilesLocation".$date."line3.png";
`$x`;

$textOffset = 2 * $logoOffset + $logoWidth;
$x = $pathToImageMagick."composite $tempFilesLocation".$date."text.png -gravity West -geometry +$textOffset+0 $tempFilesLocation".$date."header.png $tempFilesLocation".$date."header.png";
`$x`;

$x = "rm $tempFilesLocation".$date."text.png";
`$x`;

$x = $pathToImageMagick."convert $logoFilesLocation"."northArrow.png -resize $logoWidth"."x$logoHeight $tempFilesLocation".$date."northArrowScaled.png";
`$x`;

$x = $pathToImageMagick."convert $tempFilesLocation".$date."northArrowScaled.png -rotate $northAngle $tempFilesLocation".$date."northArrowRotated.png";
`$x`;

$x = $pathToImageMagick."identify -format %w $tempFilesLocation".$date."northArrowScaled.png";
$northArrowWidth = `$x`;
chomp($northArrowWidth);

$x = $pathToImageMagick."identify -format %h $tempFilesLocation".$date."northArrowScaled.png";
$northArrowHeight = `$x`;
chomp($northArrowHeight);

$x = "rm $tempFilesLocation".$date."northArrowScaled.png";
`$x`;

$x = $pathToImageMagick."convert $tempFilesLocation".$date."northArrowRotated.png -crop $northArrowWidth"."x$northArrowHeight+0+0 +repage $tempFilesLocation".$date."northArrowRotated.png";
`$x`;

$northArrowOffset = $logoOffset;
$x = $pathToImageMagick."composite $tempFilesLocation".$date."northArrowRotated.png -gravity East -geometry +$northArrowOffset+0 $tempFilesLocation".$date."header.png $tempFilesLocation".$date."header.png";
`$x`;

$x = "rm $tempFilesLocation".$date."northArrowRotated.png";
`$x`;

$x = $pathToImageMagick."convert -page +4+4 $tempFilesLocation".$date."header.png -matte \\( +clone -background black -shadow 60x4+4+4 \\) +swap -background none -mosaic $tempFilesLocation".$date."header.png";
`$x`;

$headerOffset = ($imageWidth - $headerWidth) / 4;
$headerOffset = int($headerOffset);
$x = $pathToImageMagick."composite $tempFilesLocation".$date."header.png -gravity North -geometry +0+$headerOffset $imageFile $tempFilesLocation".$date."finishedProduct.png";
`$x`;

$disclaimerTextWidth = $imageWidth;
$disclaimerTextHeight = int(0.035 * $imageHeight); 
$x = $pathToImageMagick."convert $tempFilesLocation".$date."finishedProduct.png -background yellow -fill black -size $disclaimerTextWidth"."x$disclaimerTextHeight -gravity center label:'Not an intelligence product  //  For informational use only  //  Not certified for targeting' -append $tempFilesLocation".$date."finishedProduct.png";
`$x`;

$x = "rm $tempFilesLocation".$date."header.png";
`$x`;

$x = "rm $tempFilesLocation".$date."omarImage.png";
`$x`;

print $tempFilesLocation.$date."finishedProduct.png";
