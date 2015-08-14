#!/bin/sh
git archive --format zip --output "output.zip" grails-2.5.x  -0
zip -d output.zip "*.tgz" "*.zip"
