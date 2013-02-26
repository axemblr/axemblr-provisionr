#!/bin/bash

# utility script to automatically unzip and setup AWS credentails from Mavem for snapshot builds
if [ $# -ne 2 ]; then
	echo "The script must take two parameters - the destination folder (must exist) and the snapshot version you are trying to deploy."
	exit 0
fi
rm -rf $1/provisionr
archive=$(basename "../karaf/assembly/target/com.axemblr.provisionr-$2-SNAPSHOT.tar.gz") 
cp karaf/assembly/target/$archive $1 && cd $1
tar xvzf $archive && rm $archive
mv "${archive%.*.*}" provisionr
cd provisionr/
for w in "secretKey" "accessKey"
do
        key=$(grep -E -m 1 -o "<.*amazon.*$w>(.*)</.*amazon.*$w>" ~/.m2/settings.xml | sed -e 's,.*<*.>\([^<]*\)</.*>.*,\1,g')
        sed -i -e "s/$w = .*$/$w = $key/g" ~/provisionr/system/com/axemblr/provisionr/provisionr-amazon/0.4.0-SNAPSHOT/provisionr-amazon-0.4.0-SNAPSHOT-defaults.cfg
done