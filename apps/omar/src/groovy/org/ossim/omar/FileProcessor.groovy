package org.ossim.omar
interface FileProcessor
{
  // if the input is a directory and a false is return,  this
  // test the scanner not to recurse into that directory.

  public boolean process(File fileOrDirectory)
}

