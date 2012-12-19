#!/bin/bash
set -e -x
export DEBIAN_FRONTEND=noninteractive

apt-get update

# TODO implement a special action for global package upgrades 
# apt-get upgrade -y

apt-get install -y ruby ruby-dev rubygems libopenssl-ruby rdoc ri irb build-essential wget ssl-cert

echo "export PATH=$PATH:"`gem environment | grep "EXECUTABLE DIRECTORY" | sed 's|- EXECUTABLE DIRECTORY: ||' | sed -e 's/^[ \t]*//'`"" >> /etc/profile
echo "export PATH=$PATH:"`gem environment | grep "EXECUTABLE DIRECTORY" | sed 's|- EXECUTABLE DIRECTORY: ||' | sed -e 's/^[ \t]*//'`"" >> /etc/bash.bashrc

gem install facter --no-rdoc --no-ri --bindir /usr/bin/
gem install puppet --no-rdoc --no-ri --bindir /usr/bin/

useradd puppet