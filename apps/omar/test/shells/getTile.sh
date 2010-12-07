#!/bin/sh


func()
{
  ID=$1
  FORMAT=$2
  FORMAT_TYPE=$3
  wget --quiet -O out1 "http://localhost:8080/omar/ogc/getTile?id=$ID&startSample=0&startLine=0&endSample=255&endLine=255&FORMAT=$FORMAT" 
  wget --quiet -O out2 "http://localhost:8080/omar/icp/getTile?id=$ID&x=0&y=0&width=256&height=256&FORMAT=$FORMAT"

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


echo "testing jpeg"
func 1 image/jpeg JPEG
echo "testing png"
func 1 image/png PNG
echo "testing gif"
func 1 image/gif GIF
