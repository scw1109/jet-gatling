#!/usr/bin/env sh

mvn build-helper:parse-version versions:set \
    -DnewVersion="\${parsedVersion.majorVersion}.\${parsedVersion.nextMinorVersion}" \
    versions:commit
