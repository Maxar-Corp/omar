#!/bin/sh

# function takes a arguments in the folowing order
# (URL, ID, FORMAT, FORMAT_TYPE)
#
# 
func()
{
  URL=$1
  ID=$2
  FORMAT=$3
  FORMAT_TYPE=$4
  wget --quiet -O out1 "$URL/omar/ogc/getTile?id=$ID&startSample=0&startLine=0&endSample=255&endLine=255&FORMAT=$FORMAT" 
  if [[ "$?" != "0" ]] ; then
      echo "Unable to get location location for $URL/omar/ogc/getTile?id=$ID&startSample=0&startLine=0&endSample=255&endLine=255&FORMAT=$FORMAT" ;
      exit 1;
  fi
  wget --quiet -O out2 "$URL/omar/icp/getTile?id=$ID&x=0&y=0&width=256&height=256&FORMAT=$FORMAT"
  if [[ "$?" != "0" ]] ; then
      echo "Unable to get location location for $URL/omar/icp/getTile?id=$ID&x=0&y=0&width=256&height=256&FORMAT=$FORMAT" ;
      exit 1;
  fi

  if [ -f out1 -a -f out2 ] 
    then
    echo "Created files? passed"
  else
    echo "Created files? failed"
  fi
  DIFF=`diff out1 out2`
  if [ "$DIFF" == "" ] 
  then
    echo "Same files? passed"
  else
    echo "Same files? failed"
  fi

  FILE_TYPE=`file --separator - out1`
  if [ "$FILE_TYPE" == "" ] 
   then
    echo "Proper format? failed"
  else
    echo "Proper format? passed"
  fi


  rm -f out1 out2
}

URL="http://localhost:8080"

echo "testing jpeg"
func $URL 1 image/jpeg JPEG
echo "testing png"
func $URL 1 image/png PNG
echo "testing gif"
func $URL 1 image/gif GIF

